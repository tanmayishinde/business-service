package com.root.businessservice.proxy;

import com.root.commondependencies.displayvo.ChildPartDisplayVO;
import com.root.commondependencies.displayvo.ChildPartQuantityVO;
import com.root.commondependencies.displayvo.ProductDisplayVO;
import com.root.commondependencies.exception.ValidationException;
import com.root.commondependencies.vo.ChildPartVO;
import com.root.commondependencies.vo.CreationDateVO;
import com.root.commondependencies.vo.MonthlyPlanEntityVO;
import com.root.commondependencies.displayvo.MonthlyPlanResponseVO;
import com.root.commondependencies.vo.ProductChildPartVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "db-service/db")
public interface DataRetrieveProxy {
    @GetMapping("/getCreationDate")
    MonthlyPlanEntityVO getCreationDate();

    @PostMapping("/dataRetrieve/getMonthlyPlanList")
    public List<MonthlyPlanEntityVO> getMonthlyPlanVOList(@RequestBody CreationDateVO creationDateVO);

    @GetMapping("/dataRetrieve/product-list")
    public List<ProductDisplayVO> getProductList();

    @GetMapping("/dataRetrieve/childPart-list")
    public List<ChildPartDisplayVO> getChildPartList();

    @GetMapping("/dataRetrieve/productChildPartRelationship-list")
    public List<ProductChildPartVO> getproductChildPartRelationshipList();

    @GetMapping("/dataRetrieve/productChildPart-list")
    public Map<Long,List<ChildPartQuantityVO>> getProductChildPartList();
//    @GetMapping("/productChildPart-list")
//    public List<ProductChildPartDisplayVO> getProductChildPartList();
}
