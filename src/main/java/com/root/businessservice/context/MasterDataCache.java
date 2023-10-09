package com.root.businessservice.context;

import com.root.commondependencies.displayvo.ChildPartDisplayVO;
import com.root.commondependencies.displayvo.ProductDisplayVO;
import com.root.commondependencies.vo.ProductChildPartRelationShipVO;
import com.root.redis.context.RedisSessionContext;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MasterDataCache extends RedisSessionContext {

    List<ProductDisplayVO> productList;
    List<ChildPartDisplayVO> childPartList;
    List<ProductChildPartRelationShipVO> relationShipList;

    @Override
    public String getContextIdentifier() {
        return "SUPPLIER_MASTER_DATA";
    }


    @Override
    public Integer sessionExpiryTime() {
        return null;
    }
}
