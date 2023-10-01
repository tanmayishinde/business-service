package com.root.businessservice.service;

import com.root.commondependencies.displayvo.ChildPartQuantityVO;
import com.root.commondependencies.displayvo.ProductChildPartDisplayVO;
import com.root.commondependencies.vo.ChildPartVO;
import com.root.commondependencies.vo.ProductVO;

import java.util.List;
import java.util.Map;

public interface ProductService {

    public List<ChildPartVO> getchildPartList();

    Map<Long,List<ChildPartQuantityVO>> getProductChildPartList();
}
