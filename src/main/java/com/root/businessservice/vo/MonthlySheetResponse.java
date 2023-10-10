package com.root.businessservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlySheetResponse {
    private String statusCode;
    private String message;
    MonthlyPlanResponseVO monthlyPlanResponseVO;


}
