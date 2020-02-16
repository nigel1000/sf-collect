package collect.debug.arrange.demo;

import lombok.Data;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/7/8.
 */

@Data
public class ProductModel {

    private Long goodsId;

    private String name;

    private List<ProductSkuModel> skuModelList;

}
