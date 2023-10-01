package com.root.businessservice.service;

import com.root.businessservice.proxy.PlanAndProductDataProxy;
import com.root.commondependencies.displayvo.ChildPartDisplayVO;
import com.root.commondependencies.displayvo.ProductDisplayVO;
import com.root.commondependencies.vo.ProductChildPartRelationShipVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AsyncService {

    private final PlanAndProductDataProxy planAndProductDataProxy;

    @Autowired
    public AsyncService(PlanAndProductDataProxy planAndProductDataProxy){
        this.planAndProductDataProxy = planAndProductDataProxy;
    }
    @Async
    public CompletableFuture<List<ProductDisplayVO>> getProductListFromDB()
    {
        List<ProductDisplayVO> productList = planAndProductDataProxy.getProductList();
        return CompletableFuture.completedFuture(productList);
    }

    @Async
    public CompletableFuture<List<ChildPartDisplayVO>> getChildPartListFromDB()
    {
        List<ChildPartDisplayVO> productList = planAndProductDataProxy.getChildPartList();
        return CompletableFuture.completedFuture(productList);
    }

    @Async
    public CompletableFuture<List<ProductChildPartRelationShipVO>> getProductChildRelationListFromDB()
    {
        List<ProductChildPartRelationShipVO> productList
                = planAndProductDataProxy.getproductChildPartRelationshipList();
        return CompletableFuture.completedFuture(productList);
    }


}
