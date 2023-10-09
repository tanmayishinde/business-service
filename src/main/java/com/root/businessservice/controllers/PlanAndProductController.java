package com.root.businessservice.controllers;

import com.root.businessservice.service.PlanAndProductService;
import com.root.businessservice.vo.MonthlySheetResponse;
import com.root.commondependencies.displayvo.ProductChildPartDisplayVO;
import com.root.commondependencies.vo.CreationDateVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlanAndProductController {
    private PlanAndProductService planAndProductService;

    public PlanAndProductController(PlanAndProductService planAndProductService){
        this.planAndProductService=planAndProductService;
    }
    @GetMapping("/getDetailedProductList")
    public List<ProductChildPartDisplayVO> getDetailedProductList() {
        return planAndProductService.getDetailedProductList();

    }
    @PostMapping("/getMonthly-plan")
    public MonthlySheetResponse getMonthlyPlan(@RequestBody CreationDateVO requestVO){
        return planAndProductService.getMonthlyPlan(requestVO);
    }


}
