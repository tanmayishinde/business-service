package com.root.businessservice.vo;

import com.root.commondependencies.displayvo.MonthlyPlanResponseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class MonthlySheetResponse {
    private String statusCode;
    private String message;
    MonthlyPlanResponseVO monthlyPlanResponseVO;


}
