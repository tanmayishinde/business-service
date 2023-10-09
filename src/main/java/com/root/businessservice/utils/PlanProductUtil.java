package com.root.businessservice.utils;

import com.root.businessservice.context.MasterDataCache;
import com.root.commondependencies.displayvo.*;
import com.root.commondependencies.vo.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public final class PlanProductUtil {
    public static List<ProductChildPartDisplayVO> getDetailedProductList(List<ProductDisplayVO> productList,
                                                                         List<ChildPartDisplayVO> childPartList,
                                                                         List<ProductChildPartRelationShipVO>
                                                                                 relationShipList) {

        Map<Long, List<ChildPartQuantityVO>> relationshipMap = getRelationshipMap(relationShipList);
        Map<Long, ChildPartDisplayVO> childPartMap = getChildPartMap(childPartList);


        List<ProductChildPartDisplayVO> detailedProductList = new ArrayList<>();
        for (ProductDisplayVO productDisplay : productList) {
            ProductChildPartDisplayVO detailedProduct = new ProductChildPartDisplayVO();
            detailedProduct.setProductName(productDisplay.getProductName());
            detailedProduct.setProductSeries(productDisplay.getProductSeries());
            for (Map.Entry<Long, List<ChildPartQuantityVO>> map : relationshipMap.entrySet()) {
                List<ChildPartVO> detailedChildPartList
                        = getProductWiseChildPartList(relationshipMap.getOrDefault(productDisplay.getProductID(),
                        new ArrayList<>()), childPartMap);
                detailedProduct.setChildPartVOList(detailedChildPartList);
            }
            detailedProductList.add(detailedProduct);
        }
        return detailedProductList;
    }

    private static List<ChildPartVO> getProductWiseChildPartList(List<ChildPartQuantityVO> childPartQuantityVOList,
                                                                 Map<Long, ChildPartDisplayVO> childPartMap) {
        List<ChildPartVO> detailedChildPartList = new ArrayList<>();
        for (ChildPartQuantityVO childPart : childPartQuantityVOList) {
            ChildPartDisplayVO childPartDisplayVO = childPartMap.get(childPart.getChildPartId());

            ChildPartVO childPartVO = new ChildPartVO();
            childPartVO.setChildPartName(childPartDisplayVO.getChildPartName());
            childPartVO.setChildPartSeries(childPartDisplayVO.getChildPartSeries());
            childPartVO.setChildPartQuantity(childPart.getChildPartQuantity());
            childPartVO.setChildPartOpeningStock(childPartDisplayVO.getChildPartOpeningStock());
            detailedChildPartList.add(childPartVO);
        }
        return detailedChildPartList;
    }

    private static Map<Long, ChildPartDisplayVO> getChildPartMap(List<ChildPartDisplayVO> childPartList) {

        Map<Long, ChildPartDisplayVO> childPartMap = new HashMap<>();
        for (ChildPartDisplayVO childPart : childPartList) {
            childPartMap.put(childPart.getChildPartID(), childPart);
        }
        return childPartMap;
    }

    public static Map<Long, ProductDisplayVO> getProductMap(List<ProductDisplayVO> productList) {

        Map<Long, ProductDisplayVO> productMap = new HashMap<>();
        for (ProductDisplayVO product : productList) {
            productMap.put(product.getProductID(), product);
        }
        return productMap;
    }

    private static Map<Long, List<ChildPartQuantityVO>>
    getRelationshipMap(List<ProductChildPartRelationShipVO> relationShipList) {
        Map<Long, List<ChildPartQuantityVO>> partQuantityVOMap = new HashMap<>();
        for (ProductChildPartRelationShipVO productChildPartVO : relationShipList) {
            if (partQuantityVOMap.containsKey(productChildPartVO.getProductId())) {
                ChildPartQuantityVO childPartQuantityVO = new ChildPartQuantityVO();
                childPartQuantityVO.setChildPartId(productChildPartVO.getChildPartId());
                childPartQuantityVO.setChildPartQuantity(productChildPartVO.getChildPartQuantity());
                partQuantityVOMap.get(productChildPartVO.getProductId()).add(childPartQuantityVO);
            } else {
                List<ChildPartQuantityVO> childPartQuantityVOList = new ArrayList<>();
                ChildPartQuantityVO childPartQuantityVO = new ChildPartQuantityVO();
                childPartQuantityVO.setChildPartId(productChildPartVO.getChildPartId());
                childPartQuantityVO.setChildPartQuantity(productChildPartVO.getChildPartQuantity());
                childPartQuantityVOList.add(childPartQuantityVO);
                partQuantityVOMap.put(productChildPartVO.getProductId(), childPartQuantityVOList);
            }
        }
        return partQuantityVOMap;
    }


    public static CoverageVO getProductCoverage(Integer productOpeningStock, int productionRFD, MonthlyPlanEntityVO monthlyPlanEntity) {
        CoverageVO coverageVO = new CoverageVO();
        coverageVO.setCoverageWeek1(productOpeningStock + productionRFD - monthlyPlanEntity.getWeek1());
        coverageVO.setCoverageWeek2(coverageVO.getCoverageWeek1() - monthlyPlanEntity.getWeek2());
        coverageVO.setCoverageWeek3(coverageVO.getCoverageWeek2() - monthlyPlanEntity.getWeek3());
        coverageVO.setCoverageWeek4(coverageVO.getCoverageWeek3() - monthlyPlanEntity.getWeek4());
        coverageVO.setCoverageTotal(coverageVO.getCoverageWeek4());
        return coverageVO;
    }
    public static List<ChildPartWeeklyPlan> getChildPartWeeklyPlan(List<MonthlyPlanEntityVO> monthlyPlanEntityVOList,
                                                            MasterDataCache masterDataCache) {
        List<ChildPartWeeklyPlan> childPartWeeklyPlanList = new ArrayList<>();

        List<ChildPartDisplayVO> childPartEntityList = masterDataCache.getChildPartList();
        for (ChildPartDisplayVO childPartEntity : childPartEntityList) {
            ChildPartWeeklyPlan childPartWeeklyPlan = new ChildPartWeeklyPlan();
            List<ProductChildPartRelationShipVO> productChildPartVOList =
                    findChildPRelatedProductChildpartList(childPartEntity.getChildPartID(), masterDataCache);
            childPartWeeklyPlan.setChildPartId(childPartEntity.getChildPartID());
            childPartWeeklyPlan.setChildPartSeries(childPartEntity.getChildPartSeries());

            int sumValuePlanI = 0, sumValuePlanII = 0, sumValuePlanIII = 0, sumValuePlanIV = 0;
            for (ProductChildPartRelationShipVO productChildPartEntity : productChildPartVOList) {
                MonthlyPlanVO monthlyPlanVO = findByProductSeriesInMonthlyPlanList(productChildPartEntity.getProductId(),
                        monthlyPlanEntityVOList);
                int valuePlanI = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek1();
                int valuePlanII = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek2();
                int valuePlanIII = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek3();
                int valuePlanIV = productChildPartEntity.getChildPartQuantity() * monthlyPlanVO.getWeek4();
                sumValuePlanI = valuePlanI + sumValuePlanI;
                sumValuePlanII = valuePlanII + sumValuePlanII;
                sumValuePlanIII = valuePlanIII + sumValuePlanIII;
                sumValuePlanIV = valuePlanIV + sumValuePlanIV;

                childPartWeeklyPlan.setChildPWeeklyPlanI(sumValuePlanI);
                childPartWeeklyPlan.setChildPWeeklyPlanII(sumValuePlanII);
                childPartWeeklyPlan.setChildPWeeklyPlanIII(sumValuePlanIII);
                childPartWeeklyPlan.setChildPWeeklyPlanIV(sumValuePlanIV);
                childPartWeeklyPlan.setChildPMonthlyRequired(childPartWeeklyPlan.getChildPWeeklyPlanI() +
                        childPartWeeklyPlan.getChildPWeeklyPlanII() + childPartWeeklyPlan.getChildPWeeklyPlanIII() +
                        childPartWeeklyPlan.getChildPWeeklyPlanIV());
            }
            childPartWeeklyPlanList.add(childPartWeeklyPlan);
        }
        return childPartWeeklyPlanList;
    }

    public static List<ProductChildPartRelationShipVO> findChildPRelatedProductChildpartList(Long childPartEntityId,
                                                                                      MasterDataCache masterDataCache){
        List<ProductChildPartRelationShipVO> childPartRelatedProductChildpartList = new ArrayList<>();

        for (ProductChildPartRelationShipVO productChildPartVO : masterDataCache.getRelationShipList()) {
            if (productChildPartVO.getChildPartId().equals(childPartEntityId)) {
                childPartRelatedProductChildpartList.add(productChildPartVO);
            }
        }
        return childPartRelatedProductChildpartList;
    }

    public static List<ProductChildPartRelationShipVO> findProductRelatedProductChildpartList(
            Long productID, List<ProductChildPartRelationShipVO> getRelationShipList) {
        List<ProductChildPartRelationShipVO> findProductRelatedProductChildpartList = new ArrayList<>();


        for (ProductChildPartRelationShipVO productChildPartVO : getRelationShipList) {
            if (productChildPartVO.getProductId().equals(productID)) {
                findProductRelatedProductChildpartList.add(productChildPartVO);
            }
        }
        return findProductRelatedProductChildpartList;
    }

    public static MonthlyPlanVO findByProductSeriesInMonthlyPlanList(
            Long productID, List<MonthlyPlanEntityVO> monthlyPlanEntityVOList) {
        MonthlyPlanVO monthlyPlanEntityVO = new MonthlyPlanVO();

        for (MonthlyPlanEntityVO monthlyPlanVO1 : monthlyPlanEntityVOList) {
            if (monthlyPlanVO1.getProductId().equals(productID)) {
                monthlyPlanEntityVO.setWeek1(monthlyPlanVO1.getWeek1());
                monthlyPlanEntityVO.setWeek2(monthlyPlanVO1.getWeek2());
                monthlyPlanEntityVO.setWeek3(monthlyPlanVO1.getWeek3());
                monthlyPlanEntityVO.setWeek4(monthlyPlanVO1.getWeek4());
            }
        }
        return monthlyPlanEntityVO;
    }

    public static ChildPartDisplayVO findChildPartByChildPartId(Long childPartID, List<ChildPartDisplayVO> childPartVOList) {
        ChildPartDisplayVO childPartDisplayVO = new ChildPartDisplayVO();
        for (ChildPartDisplayVO childPartVO : childPartVOList) {
            if (childPartVO.getChildPartID().equals(childPartID)) {
                childPartDisplayVO.setChildPartID(childPartVO.getChildPartID());
                childPartDisplayVO.setChildPartName(childPartVO.getChildPartName());
                childPartDisplayVO.setChildPartSeries(childPartVO.getChildPartSeries());
                childPartDisplayVO.setChildPartOpeningStock(childPartVO.getChildPartOpeningStock());
            }
        }
        return childPartDisplayVO;
    }

    public static List<ChildPartWithCoverage> getChildPartsCoverageVOList(ProductDisplayVO product,
                                                                          List<ChildPartWeeklyPlan> weeklyPlan,
                                                                          MasterDataCache masterDataCache) {
        List<ChildPartWithCoverage> childPartsCoverageVOList = new ArrayList<>();
        List<ProductChildPartRelationShipVO> productChildPartVOList =
                PlanProductUtil.findProductRelatedProductChildpartList(product.getProductID(),
                        masterDataCache.getRelationShipList());
        for (ProductChildPartRelationShipVO getChildPartId : productChildPartVOList) {
            ChildPartDisplayVO childPart =
                    PlanProductUtil.findChildPartByChildPartId(getChildPartId.getChildPartId(), masterDataCache.getChildPartList());
            ChildPartWithCoverage childPartsWithCoverageVO = new ChildPartWithCoverage();
            childPartsWithCoverageVO.setChildPartId(childPart.getChildPartID());
            childPartsWithCoverageVO.setChildPartName(childPart.getChildPartName());
            childPartsWithCoverageVO.setChildPartSeries(childPart.getChildPartSeries());
            childPartsWithCoverageVO.setChildPartQuantity(getChildPartId.getChildPartQuantity());
            ChildPartWeeklyPlan childPartWeeklyPlan = new ChildPartWeeklyPlan();
            for (ChildPartWeeklyPlan chP : weeklyPlan) {
                if (Objects.equals(chP.getChildPartId(), childPart.getChildPartID())) {
                    childPartWeeklyPlan.setChildPartId(childPartWeeklyPlan.getChildPartId());
                    childPartWeeklyPlan.setChildPWeeklyPlanI(chP.getChildPWeeklyPlanI());
                    childPartWeeklyPlan.setChildPWeeklyPlanII(chP.getChildPWeeklyPlanII());
                    childPartWeeklyPlan.setChildPWeeklyPlanIII(chP.getChildPWeeklyPlanIII());
                    childPartWeeklyPlan.setChildPWeeklyPlanIV(chP.getChildPWeeklyPlanIV());
                    childPartWeeklyPlan.setChildPMonthlyRequired(chP.getChildPMonthlyRequired());
                }
            }

            ChildPartsCoverageVO childPartsCoverageVO = getChildPartsCoverageVO(childPart,childPartWeeklyPlan);
            childPartsWithCoverageVO.setChildPartCoverage(childPartsCoverageVO);

            childPartsCoverageVOList.add(childPartsWithCoverageVO);

        }
        return childPartsCoverageVOList;
    }
    public static ChildPartsCoverageVO getChildPartsCoverageVO(ChildPartDisplayVO childPart,
                                                               ChildPartWeeklyPlan childPartWeeklyPlan ){
        ChildPartsCoverageVO childPartsCoverageVO = new ChildPartsCoverageVO();
        childPartsCoverageVO.setChildPCoverageWeek1(
                childPart.getChildPartOpeningStock() + 100 - childPartWeeklyPlan.getChildPWeeklyPlanI());
        childPartsCoverageVO.setChildPCoverageWeek2(
                childPartsCoverageVO.getChildPCoverageWeek1() - childPartWeeklyPlan.getChildPWeeklyPlanII());
        childPartsCoverageVO.setChildPCoverageWeek3(
                childPartsCoverageVO.getChildPCoverageWeek2() - childPartWeeklyPlan.getChildPWeeklyPlanIII());
        childPartsCoverageVO.setChildPCoverageWeek4(
                childPartsCoverageVO.getChildPCoverageWeek3() - childPartWeeklyPlan.getChildPWeeklyPlanIV());
        childPartsCoverageVO.setChildPCoverageTotal(childPartsCoverageVO.getChildPCoverageWeek4());
        return  childPartsCoverageVO;

    }

}