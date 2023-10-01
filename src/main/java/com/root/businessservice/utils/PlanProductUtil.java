package com.root.businessservice.utils;

import com.root.commondependencies.displayvo.ChildPartDisplayVO;
import com.root.commondependencies.displayvo.ChildPartQuantityVO;
import com.root.commondependencies.displayvo.ProductChildPartDisplayVO;
import com.root.commondependencies.displayvo.ProductDisplayVO;
import com.root.commondependencies.vo.ChildPartVO;
import com.root.commondependencies.vo.ProductChildPartRelationShipVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlanProductUtil {
    public static List<ProductChildPartDisplayVO> getDetailedProductList(List<ProductDisplayVO> productList,
                                                                         List<ChildPartDisplayVO> childPartList,
                                                                         List<ProductChildPartRelationShipVO>
                                                                                 relationShipList) {

        Map<Long, List<ChildPartQuantityVO>> relationshipMap = getRelationshipMap(relationShipList);
        Map<Long, ChildPartDisplayVO> childPartMap = getChildPartMap(childPartList);


        List<ProductChildPartDisplayVO> detailedProductList = new ArrayList<>();
        for(ProductDisplayVO productDisplay : productList){
            ProductChildPartDisplayVO detailedProduct = new ProductChildPartDisplayVO();
            detailedProduct.setProductName(productDisplay.getProductName());
            detailedProduct.setProductSeries(productDisplay.getProductSeries());
            for(Map.Entry<Long, List<ChildPartQuantityVO>> map : relationshipMap.entrySet()){
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
        for(ChildPartQuantityVO childPart : childPartQuantityVOList){
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
        for(ChildPartDisplayVO childPart : childPartList){
            childPartMap.put(childPart.getChildPartID(), childPart);
        }
        return childPartMap;
    }

    public static Map<Long, ProductDisplayVO> getProductMap(List<ProductDisplayVO> productList) {

        Map<Long, ProductDisplayVO> productMap = new HashMap<>();
        for(ProductDisplayVO product : productList){
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
}
