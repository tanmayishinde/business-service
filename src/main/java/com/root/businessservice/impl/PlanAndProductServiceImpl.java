package com.root.businessservice.impl;

import com.root.businessservice.context.MasterDataCache;
import com.root.businessservice.proxy.PlanAndProductDataProxy;
import com.root.businessservice.service.AsyncService;
import com.root.businessservice.service.PlanAndProductService;
import com.root.businessservice.utils.Constants;
import com.root.businessservice.utils.PlanProductUtil;
import com.root.businessservice.vo.MonthlyPlanResponseVO;
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
        monthlyPlanResponseVO.setMonthlyDisplayList(PlanProductUtil.getMonthlyPlanDisplayList(
                monthlyPlanEntityVOList,redisContextWrapper));

        monthlySheetResponse.setStatusCode("200");
        monthlySheetResponse.setMessage("SUCCESS");
        monthlySheetResponse.setMonthlyPlanResponseVO(monthlyPlanResponseVO);
        return monthlySheetResponse;
    }



}
