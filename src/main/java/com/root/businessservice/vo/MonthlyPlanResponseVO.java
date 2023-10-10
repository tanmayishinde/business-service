package com.root.businessservice.vo;

import com.root.commondependencies.displayvo.MonthlyDisplayVO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class MonthlyPlanResponseVO {
    private Date startDate;
    private Date endDate;
    private List<MonthlyDisplayVO> monthlyDisplayList;

}
