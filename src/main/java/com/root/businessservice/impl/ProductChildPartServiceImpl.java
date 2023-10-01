//package com.root.businessservice.impl;
//
//import com.root.businessservice.context.SupplierContext;
//import com.root.businessservice.proxy.PlanAndProductDataProxy;
//import com.root.businessservice.service.ProductChildPartService;
//import com.root.businessservice.utils.SessionUtil;
//import com.root.businessservice.vo.DataRetrieveBusinessVO;
//import com.root.businessservice.vo.MonthlySheetResponse;
//import com.root.commondependencies.displayvo.*;
//import com.root.commondependencies.exception.ValidationException;
//import com.root.commondependencies.vo.*;
//import com.root.redis.services.RedisContextWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class ProductChildPartServiceImpl implements ProductChildPartService {
//
//    private final RedisContextWrapper redisContextWrapper;
//    private final SessionUtil sessionUtil;
//    PlanAndProductDataProxy dataRetrieveProxy;
//
//    @Autowired
//    public ProductChildPartServiceImpl(PlanAndProductDataProxy dataRetrieveProxy,
//                                       SessionUtil sessionUtil,
//                                       RedisContextWrapper redisContextWrapper) {
//        this.dataRetrieveProxy = dataRetrieveProxy;
//        this.sessionUtil = sessionUtil;
//        this.redisContextWrapper = redisContextWrapper;
//    }
//
//
//
//    @Override
//    public MonthlySheetResponse getMonthlyPlan(CreationDateVO creationDateVO) throws ValidationException {
//        MonthlySheetResponse monthlySheetResponse = new MonthlySheetResponse();
//        List<MonthlyPlanEntityVO> monthlyPlanEntityVOList = dataRetrieveProxy.getMonthlyPlanVOList(creationDateVO);
//
//        MonthlyPlanResponseVO monthlyPlanResponseVO = new MonthlyPlanResponseVO();
//        monthlyPlanResponseVO.setStartDate(creationDateVO.getStartDate());
//        monthlyPlanResponseVO.setEndDate(creationDateVO.getEndDate());
//        monthlyPlanResponseVO.setMonthlyDisplayVOList(getMonthlyPlanVODisplayList(
//                monthlyPlanEntityVOList));
//
//        monthlySheetResponse.setStatusCode("200");
//        monthlySheetResponse.setMessage("SUCCESS");
//        monthlySheetResponse.setMonthlyPlanResponseVO(monthlyPlanResponseVO);
//        return monthlySheetResponse;
//    }
//
//    public List<MonthlyDisplayVO> getMonthlyPlanVODisplayList(
//             List<MonthlyPlanEntityVO> monthlyPlanEntityVOList) throws ValidationException {
//
//        String sessionId = sessionUtil.getSessionId();
//        SupplierContext supplierContext = redisContextWrapper.getContext(sessionId, SupplierContext.class);
//        List<MonthlyDisplayVO> monthlyDisplayVOList = new ArrayList<>();
//
//        List<ProductDisplayVO> productVOList = supplierContext.getProductList();
//        Map<Long, ProductDisplayVO> productMap = new HashMap<>();
//        for (ProductDisplayVO productEntityVO : productVOList) {
//            productMap.put(productEntityVO.getProductID(), productEntityVO);
//        }
//
//        for (MonthlyPlanEntityVO monthlyPlanEntity : monthlyPlanEntityVOList) {
//            MonthlyDisplayVO monthlyDisplayVO = new MonthlyDisplayVO();
//            ProductDisplayVO product = productMap.get(monthlyPlanEntity.getProductId());
//
//            monthlyDisplayVO.setProductName(product.getProductName());
//            monthlyDisplayVO.setProductSeries(product.getProductSeries());
//            monthlyDisplayVO.setProductName(product.getProductName());
//            monthlyDisplayVO.setWeek1(monthlyPlanEntity.getWeek1());
//            monthlyDisplayVO.setWeek2(monthlyPlanEntity.getWeek2());
//            monthlyDisplayVO.setWeek3(monthlyPlanEntity.getWeek3());
//            monthlyDisplayVO.setWeek4(monthlyPlanEntity.getWeek4());
//            monthlyDisplayVO.setTotal(monthlyDisplayVO.getWeek1() + monthlyDisplayVO.getWeek2() + monthlyDisplayVO.getWeek3() + monthlyDisplayVO.getWeek4());
//            monthlyDisplayVO.setOpeningStock(product.getProductOpeningStock());
//            monthlyDisplayVO.setDispatch(500);
//            monthlyDisplayVO.setProductionRFD(100);
//            CoverageVO coverageVO = new CoverageVO();
//            coverageVO.setCoverageWeek1(monthlyDisplayVO.getOpeningStock() + monthlyDisplayVO.getProductionRFD() - monthlyPlanEntity.getWeek1());
//            coverageVO.setCoverageWeek2(coverageVO.getCoverageWeek1() - monthlyPlanEntity.getWeek2());
//            coverageVO.setCoverageWeek3(coverageVO.getCoverageWeek2() - monthlyPlanEntity.getWeek3());
//            coverageVO.setCoverageWeek4(coverageVO.getCoverageWeek3() - monthlyPlanEntity.getWeek4());
//            coverageVO.setCoverageTotal(coverageVO.getCoverageWeek4());
//            monthlyDisplayVO.setCoverageVO(coverageVO);
//
//            List<ChildPartWeeklyPlan> weeklyPlan = getChildPartWeeklyPlan(monthlyPlanEntityVOList, supplierContext);
//            List<ChildPartWithCoverage> childPartsCoverageVOList = new ArrayList<>();
//            List<ProductChildPartRelationShipVO> productChildPartVOList = findProductRelatedProductChildpartList(product.getProductID(), supplierContext);
//            for (ProductChildPartRelationShipVO getChildPartId : productChildPartVOList) {
//                ChildPartDisplayVO childPart=findChildPartByChildPartId(getChildPartId.getChildPartId(),supplierContext);
//                ChildPartWithCoverage childPartsWithCoverageVO = new ChildPartWithCoverage();
//                childPartsWithCoverageVO.setChildPartId(childPart.getChildPartID());
//                childPartsWithCoverageVO.setChildPartName(childPart.getChildPartName());
//                childPartsWithCoverageVO.setChildPartSeries(childPart.getChildPartSeries());
//                childPartsWithCoverageVO.setChildPartQuantity(getChildPartId.getChildPartQuantity());
//                ChildPartWeeklyPlan childPartWeeklyPlan = new ChildPartWeeklyPlan();
//                for (ChildPartWeeklyPlan chP : weeklyPlan) {
//
//                    if (Objects.equals(chP.getChildPartId(), childPart.getChildPartID())) {
//                        childPartWeeklyPlan.setChildPartId(childPartWeeklyPlan.getChildPartId());
//                        childPartWeeklyPlan.setChildPWeeklyPlanI(chP.getChildPWeeklyPlanI());
//                        childPartWeeklyPlan.setChildPWeeklyPlanII(chP.getChildPWeeklyPlanII());
//                        childPartWeeklyPlan.setChildPWeeklyPlanIII(chP.getChildPWeeklyPlanIII());
//                        childPartWeeklyPlan.setChildPWeeklyPlanIV(chP.getChildPWeeklyPlanIV());
//                        childPartWeeklyPlan.setChildPMonthlyRequired(chP.getChildPMonthlyRequired());
//                    }
//                }
//
//                ChildPartsCoverageVO childPartsCoverageVO = new ChildPartsCoverageVO();
//                childPartsCoverageVO.setChildPCoverageWeek1(childPart.getChildPartOpeningStock()+ 100 - childPartWeeklyPlan.getChildPWeeklyPlanI());
//                childPartsCoverageVO.setChildPCoverageWeek2(childPartsCoverageVO.getChildPCoverageWeek1()- childPartWeeklyPlan.getChildPWeeklyPlanII());
//                childPartsCoverageVO.setChildPCoverageWeek3(childPartsCoverageVO.getChildPCoverageWeek2()- childPartWeeklyPlan.getChildPWeeklyPlanIII());
//                childPartsCoverageVO.setChildPCoverageWeek4(childPartsCoverageVO.getChildPCoverageWeek3()- childPartWeeklyPlan.getChildPWeeklyPlanIV());
//                childPartsCoverageVO.setChildPCoverageTotal(childPartsCoverageVO.getChildPCoverageWeek4());
//                childPartsWithCoverageVO.setChildPartCoverage(childPartsCoverageVO);
//
//                childPartsCoverageVOList.add(childPartsWithCoverageVO);
//
//            }
//            monthlyDisplayVO.setChildPartWithCoverageList(childPartsCoverageVOList);
//            monthlyDisplayVOList.add(monthlyDisplayVO);
//        }
//
//
//        return monthlyDisplayVOList;
//    }
//
//    public List<ChildPartWeeklyPlan> getChildPartWeeklyPlan(List<MonthlyPlanEntityVO> monthlyPlanEntityVOList, SupplierContext supplierContext) {
//        List<ChildPartWeeklyPlan> childPartWeeklyPlanList = new ArrayList<>();
//        List<ChildPartDisplayVO> childPartEntityList = supplierContext.getChildPartList();
//        for (ChildPartDisplayVO childPartEntity : childPartEntityList) {
//            ChildPartWeeklyPlan childPartWeeklyPlan = new ChildPartWeeklyPlan();
//            List<ProductChildPartRelationShipVO> productChildPartVOList = findChildPRelatedProductChildpartList(childPartEntity.getChildPartID(), supplierContext);
//            childPartWeeklyPlan.setChildPartId(childPartEntity.getChildPartID());
//            childPartWeeklyPlan.setChildPartSeries(childPartEntity.getChildPartSeries());
//
//            int sumValuePlanI = 0, sumValuePlanII = 0, sumValuePlanIII = 0, sumValuePlanIV = 0;
//            for (ProductChildPartRelationShipVO productChildPartEntity : productChildPartVOList) {
//                MonthlyPlanVO monthlyPlanVO = findByProductSeriesInMonthlyPlanList(productChildPartEntity.getProductId(), monthlyPlanEntityVOList);
//                int valuePlanI = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek1();
//                int valuePlanII = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek2();
//                int valuePlanIII = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek3();
//                int valuePlanIV = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek4();
//                sumValuePlanI = valuePlanI + sumValuePlanI;
//                sumValuePlanII = valuePlanII + sumValuePlanII;
//                sumValuePlanIII = valuePlanIII + sumValuePlanIII;
//                sumValuePlanIV = valuePlanIV + sumValuePlanIV;
//
//                childPartWeeklyPlan.setChildPWeeklyPlanI(sumValuePlanI);
//                childPartWeeklyPlan.setChildPWeeklyPlanII(sumValuePlanII);
//                childPartWeeklyPlan.setChildPWeeklyPlanIII(sumValuePlanIII);
//                childPartWeeklyPlan.setChildPWeeklyPlanIV(sumValuePlanIV);
//                childPartWeeklyPlan.setChildPMonthlyRequired(childPartWeeklyPlan.getChildPWeeklyPlanI() +
//                        childPartWeeklyPlan.getChildPWeeklyPlanII() + childPartWeeklyPlan.getChildPWeeklyPlanIII() +
//                        childPartWeeklyPlan.getChildPWeeklyPlanIV());
//            }
//            childPartWeeklyPlanList.add(childPartWeeklyPlan);
//        }
//        return childPartWeeklyPlanList;
//    }
//
//    public List<ProductChildPartRelationShipVO> findChildPRelatedProductChildpartList(Long childPartEntity, SupplierContext supplierContext) {
//        List<ProductChildPartRelationShipVO> childPartRelatedProductChildpartList = new ArrayList<>();
//
//        for (ProductChildPartRelationShipVO productChildPartVO : supplierContext.getProductChildPartRelationshipVOList()) {
//            if (productChildPartVO.getChildPartId().equals(childPartEntity)) {
//                childPartRelatedProductChildpartList.add(productChildPartVO);
//            }
//        }
//        return childPartRelatedProductChildpartList;
//    }
//
//    public List<ProductChildPartRelationShipVO> findProductRelatedProductChildpartList(Long productID, SupplierContext supplierContext) {
//        List<ProductChildPartRelationShipVO> findProductRelatedProductChildpartList = new ArrayList<>();
//
//        for (ProductChildPartRelationShipVO productChildPartVO : supplierContext.getProductChildPartRelationshipVOList()) {
//            if (productChildPartVO.getProductId().equals(productID)) {
//                findProductRelatedProductChildpartList.add(productChildPartVO);
//            }
//        }
//        return findProductRelatedProductChildpartList;
//    }
//
//    public MonthlyPlanVO findByProductSeriesInMonthlyPlanList(Long productID, List<MonthlyPlanEntityVO> monthlyPlanEntityVOList) {
//        MonthlyPlanVO monthlyPlanEntityVO = new MonthlyPlanVO();
//
//        for (MonthlyPlanEntityVO monthlyPlanVO1 : monthlyPlanEntityVOList) {
//            if (monthlyPlanVO1.getProductId().equals(productID)) {
//                //monthlyPlanEntityVO.setProductId(monthlyPlanVO1.getProductId());
//
//                monthlyPlanEntityVO.setWeek1(monthlyPlanVO1.getWeek1());
//                monthlyPlanEntityVO.setWeek2(monthlyPlanVO1.getWeek2());
//                monthlyPlanEntityVO.setWeek3(monthlyPlanVO1.getWeek3());
//                monthlyPlanEntityVO.setWeek4(monthlyPlanVO1.getWeek4());
//            }
//        }
//        return monthlyPlanEntityVO;
//    }
//
//    public ChildPartDisplayVO findChildPartByChildPartId(Long childPartID,SupplierContext supplierContext){
//        List<ChildPartDisplayVO> childPartVOList=supplierContext.getChildPartList();
//        ChildPartDisplayVO childPartDisplayVO=new ChildPartDisplayVO();
//        for(ChildPartDisplayVO childPartVO:childPartVOList){
//            if(childPartVO.getChildPartID().equals(childPartID)){
//              childPartDisplayVO.setChildPartID(childPartVO.getChildPartID());
//              childPartDisplayVO.setChildPartName(childPartVO.getChildPartName());
//              childPartDisplayVO.setChildPartSeries(childPartVO.getChildPartSeries());
//              childPartDisplayVO.setChildPartOpeningStock(childPartVO.getChildPartOpeningStock());
//            }
//        }
//        return childPartDisplayVO;
//    }
//}
