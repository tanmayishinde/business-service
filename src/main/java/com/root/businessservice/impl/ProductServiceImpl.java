package com.root.businessservice.impl;

import com.root.businessservice.proxy.DataRetrieveProxy;
import com.root.businessservice.service.ProductService;
import com.root.commondependencies.displayvo.ChildPartDisplayVO;
import com.root.commondependencies.displayvo.ChildPartQuantityVO;
import com.root.commondependencies.vo.ChildPartVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductServiceImpl implements ProductService {
    DataRetrieveProxy dataRetrieveProxy;

    public ProductServiceImpl(DataRetrieveProxy dataRetrieveProxy){
        this.dataRetrieveProxy=dataRetrieveProxy;
    }

    @Override
    public List<ChildPartVO> getchildPartList() {
        List<ChildPartDisplayVO> childPartDisplayVOList=dataRetrieveProxy.getChildPartList();

        List<ChildPartVO> childPartVOList=new ArrayList<>();
        for (ChildPartDisplayVO childPartDisplayVO:childPartDisplayVOList){
            ChildPartVO childPartVO=new ChildPartVO();
            childPartVO.setChildPartSeries(childPartDisplayVO.getChildPartSeries());
            childPartVO.setChildPartName(childPartDisplayVO.getChildPartName());
            childPartVO.setChildPartOpeningStock(childPartDisplayVO.getChildPartOpeningStock());
            childPartVOList.add(childPartVO);
        }
        return childPartVOList;

    }

    @Override
    public Map<Long,List<ChildPartQuantityVO>> getProductChildPartList() {
        return dataRetrieveProxy.getProductChildPartList();
    }
}
