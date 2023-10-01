package com.root.businessservice.controllers;

import com.root.businessservice.service.ProductService;
import com.root.commondependencies.displayvo.ChildPartQuantityVO;
import com.root.commondependencies.exception.ValidationException;
import com.root.commondependencies.vo.ChildPartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ProductController {
    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

//    @GetMapping("/product-list")
//    public List<ProductVO> getProductList() throws ValidationException {
//        return productService.getProductList();
//    }

    @GetMapping("/childPart-list")
    public List<ChildPartVO> getchildPartList() throws ValidationException {
        return productService.getchildPartList();
    }

    @GetMapping("/productChildPart-list")
    public Map<Long,List<ChildPartQuantityVO>> getProductChildPartList() {
        return productService.getProductChildPartList();

    }

}
