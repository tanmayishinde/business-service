package com.root.businessservice.controllers;

import com.root.businessservice.service.ProductChildPartService;
import com.root.businessservice.vo.DataRetrieveBusinessVO;
import com.root.businessservice.vo.MonthlySheetResponse;
import com.root.commondependencies.exception.ValidationException;
import com.root.commondependencies.vo.CreationDateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductChildPartController {
    private ProductChildPartService monthlyService;

    @Autowired
    public ProductChildPartController(ProductChildPartService monthlyService) {
        this.monthlyService = monthlyService;
    }

    @GetMapping("/dataRetrieve-list")
    public DataRetrieveBusinessVO getDataFromDb() {
        return monthlyService.getDataFromDb();
    }

    //    quickIndustry/monthly-plan/?startDate=06-01-2022&endDate=06-30-2022
    @PostMapping("/monthly-plan")
    public MonthlySheetResponse getMonthlyPlanBetweenDates(@RequestBody CreationDateVO requestVO) throws ValidationException {
        return monthlyService.getMonthlyPlan(requestVO);
    }


}
