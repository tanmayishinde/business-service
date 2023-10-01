package com.root.businessservice.service;

import com.root.businessservice.vo.MonthlySheetResponse;
import com.root.commondependencies.displayvo.ProductChildPartDisplayVO;
import com.root.commondependencies.exception.ValidationException;
import com.root.commondependencies.vo.CreationDateVO;

import java.util.List;

public interface PlanAndProductService {

    List<ProductChildPartDisplayVO> getDetailedProductList();

    MonthlySheetResponse getMonthlyPlan(CreationDateVO requestVO);

}
