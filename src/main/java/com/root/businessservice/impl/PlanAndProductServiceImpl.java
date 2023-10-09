package com.root.businessservice.impl;

import com.root.businessservice.context.MasterDataCache;
import com.root.businessservice.proxy.PlanAndProductDataProxy;
import com.root.businessservice.service.AsyncService;
import com.root.businessservice.service.PlanAndProductService;
import com.root.businessservice.utils.Constants;
import com.root.businessservice.utils.PlanProductUtil;
import com.root.businessservice.vo.MonthlySheetResponse;
import com.root.commondependencies.displayvo.*;
import com.root.commondependencies.exception.ValidationException;
import com.root.commondependencies.vo.*;
import com.root.redis.services.RedisContextWrapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class PlanAndProductServiceImpl implements PlanAndProductService {


    private final AsyncService asyncService;

    private final PlanAndProductDataProxy planAndProductDataProxy;
    private final RedisContextWrapper redisContextWrapper;


    @Autowired
    public PlanAndProductServiceImpl(AsyncService asyncService,
                                     PlanAndProductDataProxy planAndProductDataProxy,
                                     RedisContextWrapper redisContextWrapper) {
        this.asyncService = asyncService;
        this.planAndProductDataProxy = planAndProductDataProxy;
        this.redisContextWrapper = redisContextWrapper;
    }

    @Override
    @SneakyThrows
    public List<ProductChildPartDisplayVO> getDetailedProductList() {

        try {
            List<ProductDisplayVO> productList = null;
            List<ChildPartDisplayVO> childPartList = null;
            List<ProductChildPartRelationShipVO> relationShipList = null;
            MasterDataCache masterDataCache
                    = redisContextWrapper.getContext(Constants.MASTER_DATA_CACHE_KEY, MasterDataCache.class);
            if (masterDataCache == null) {

                masterDataCache = new MasterDataCache();

                CompletableFuture<List<ProductDisplayVO>> productListCf = asyncService.getProductListFromDB();
                CompletableFuture<List<ChildPartDisplayVO>> childPartListCf = asyncService.getChildPartListFromDB();
                CompletableFuture<List<ProductChildPartRelationShipVO>> relationshipCf
                        = asyncService.getProductChildRelationListFromDB();

                CompletableFuture.allOf(productListCf, childPartListCf, relationshipCf).join();

                productList = productListCf.get();
                childPartList = childPartListCf.get();
                relationShipList = relationshipCf.get();

                masterDataCache.setProductList(productList);
                masterDataCache.setChildPartList(childPartList);
                masterDataCache.setRelationShipList(relationShipList);

                redisContextWrapper.setContextWithNoTimeout(Constants.MASTER_DATA_CACHE_KEY, masterDataCache);

            } else {
                productList = masterDataCache.getProductList();
                childPartList = masterDataCache.getChildPartList();
                relationShipList = masterDataCache.getRelationShipList();
            }

            List<ProductChildPartDisplayVO> productChildPartList
                    = PlanProductUtil.getDetailedProductList(productList, childPartList, relationShipList);

            return productChildPartList;
        } catch (ValidationException e) {
            throw e;
        } catch (InterruptedException | ExecutionException e) {
            throw new ValidationException.Builder().errorMessage(e.getMessage()).build();
        }
    }

    @Override
    @SneakyThrows
    public MonthlySheetResponse getMonthlyPlan(CreationDateVO creationDateVO) {
        MonthlySheetResponse monthlySheetResponse = new MonthlySheetResponse();
        List<MonthlyPlanEntityVO> monthlyPlanEntityVOList = planAndProductDataProxy.getMonthlyPlanVOList(creationDateVO);

        MonthlyPlanResponseVO monthlyPlanResponseVO = new MonthlyPlanResponseVO();
        monthlyPlanResponseVO.setStartDate(creationDateVO.getStartDate());
        monthlyPlanResponseVO.setEndDate(creationDateVO.getEndDate());
        monthlyPlanResponseVO.setMonthlyDisplayList(getMonthlyPlanDisplayList(
                monthlyPlanEntityVOList));

        monthlySheetResponse.setStatusCode("200");
        monthlySheetResponse.setMessage("SUCCESS");
        monthlySheetResponse.setMonthlyPlanResponseVO(monthlyPlanResponseVO);
        return monthlySheetResponse;
    }

    public List<MonthlyDisplayVO> getMonthlyPlanDisplayList(
            List<MonthlyPlanEntityVO> monthlyPlanEntityVOList) throws ValidationException {
        MasterDataCache masterDataCache
                = redisContextWrapper.getContext(Constants.MASTER_DATA_CACHE_KEY, MasterDataCache.class);

        List<MonthlyDisplayVO> monthlyDisplayVOList = new ArrayList<>();
        Map<Long, ProductDisplayVO> productMap = PlanProductUtil.getProductMap(masterDataCache.getProductList());

        for (MonthlyPlanEntityVO monthlyPlanEntity : monthlyPlanEntityVOList) {
            MonthlyDisplayVO monthlyDisplayVO = new MonthlyDisplayVO();
            ProductDisplayVO product = productMap.get(monthlyPlanEntity.getProductId());

            monthlyDisplayVO.setProductName(product.getProductName());
            monthlyDisplayVO.setProductSeries(product.getProductSeries());
            monthlyDisplayVO.setProductName(product.getProductName());
            monthlyDisplayVO.setWeek1(monthlyPlanEntity.getWeek1());
            monthlyDisplayVO.setWeek2(monthlyPlanEntity.getWeek2());
            monthlyDisplayVO.setWeek3(monthlyPlanEntity.getWeek3());
            monthlyDisplayVO.setWeek4(monthlyPlanEntity.getWeek4());
            Integer total = monthlyDisplayVO.getWeek1() + monthlyDisplayVO.getWeek2() + monthlyDisplayVO.getWeek3()
                    + monthlyDisplayVO.getWeek4();
            monthlyDisplayVO.setTotal(total);
            monthlyDisplayVO.setOpeningStock(product.getProductOpeningStock());
            monthlyDisplayVO.setDispatch(500);
            monthlyDisplayVO.setProductionRFD(100);

            CoverageVO coverageVO =
                    PlanProductUtil.getProductCoverage(product.getProductOpeningStock(),
                            monthlyDisplayVO.getProductionRFD(),
                            monthlyPlanEntity);
            monthlyDisplayVO.setCoverageVO(coverageVO);


            List<ChildPartWeeklyPlan> weeklyPlan = PlanProductUtil.getChildPartWeeklyPlan(monthlyPlanEntityVOList, masterDataCache);

            List<ChildPartWithCoverage> childPartsCoverageVOList = PlanProductUtil.getChildPartsCoverageVOList(product, weeklyPlan, masterDataCache);

            monthlyDisplayVO.setChildPartWithCoverageList(childPartsCoverageVOList);
            monthlyDisplayVOList.add(monthlyDisplayVO);
        }


        return monthlyDisplayVOList;
    }





}
