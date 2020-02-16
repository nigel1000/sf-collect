package collect.debug.arrange.demo;

import com.common.collect.lib.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hznijianfeng on 2019/7/8.
 */

@Component("productProvider")
@Slf4j
public class ProductProvider {


    public ProductContext queryProduct(ProductContext context) {
        ProductModel productModel = new ProductModel();
        productModel.setGoodsId(context.getGoodsId());
        productModel.setName("素肉");
        context.setProductModel(productModel);
        return context;
    }

    public ProductContext fillProductSku(ProductContext context) {
        ProductModel productModel = context.getProductModel();
        List<ProductSkuModel> skuModelList = new ArrayList<>();
        ProductSkuModel skuModel1 = new ProductSkuModel();
        skuModel1.setGoodsId(productModel.getGoodsId());
        skuModel1.setSkuId(IdUtil.uuid());
        skuModel1.setSkuName(IdUtil.uuid());
        ProductSkuModel skuModel2 = new ProductSkuModel();
        skuModel2.setGoodsId(productModel.getGoodsId());
        skuModel2.setSkuId(IdUtil.uuid());
        skuModel2.setSkuName(IdUtil.uuid());
        skuModelList.add(skuModel1);
        skuModelList.add(skuModel2);
        productModel.setSkuModelList(skuModelList);
        return context;
    }

    public ProductContext fillProductSkuSale(ProductContext context) {
        ProductModel productModel = context.getProductModel();
        List<ProductSkuModel> skuModels = productModel.getSkuModelList();
        Random random = new Random();
        for (ProductSkuModel skuModel : skuModels) {
            skuModel.setSaleCount(random.nextInt(20));
        }
        return context;
    }

}
