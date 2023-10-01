package com.root.businessservice.service;


import com.root.businessservice.vo.DataRetrieveBusinessVO;
import com.root.businessservice.vo.MonthlySheetResponse;
import com.root.commondependencies.exception.ValidationException;
import com.root.commondependencies.vo.CreationDateVO;

public interface ProductChildPartService {
    DataRetrieveBusinessVO getDataFromDb();
    MonthlySheetResponse getMonthlyPlan(CreationDateVO requestVO) throws ValidationException;

//    List<ProductDisplayVO> getProductList();
//    List<ProductChildPartDisplayVO> getProductChildPartList();
}
