package framework.elastic;

import com.common.collect.framework.elastic.AbstractElasticMapper;
import com.common.collect.framework.elastic.ElasticClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Created by hznijianfeng on 2019/2/22.
 */

@Slf4j
public class ElasticTest {

    public static void main(String[] args) {
        AbstractElasticMapper<ShopGoods> shopGoodsMapper = new AbstractElasticMapper<ShopGoods>() {
            @Override
            public RestHighLevelClient getElasticClient() {
                ElasticClient client = new ElasticClient();
                client.setHost("127.0.0.1");
                client.setPort("9200");
                client.setSchema("http");
                return client.getRestHighLevelClient();
            }

            @Override
            public String getIndex() {
                return "shop_goods_index";
            }

            @Override
            public String getType() {
                return "shop_goods";
            }
        };

        ShopGoods shopGoods = new ShopGoods(59142660L, 59142660L);
        shopGoods.setGoodsName("name");

        shopGoodsMapper.index(shopGoods.docId(), null, shopGoods);
        log.info("shopGoodsMapper.get:{}", shopGoodsMapper.get(shopGoods.docId(), null));

        System.exit(-1);
    }

    @Data
    public static class ShopGoods {
        private Long shopId;
        private Long goodsId;
        private String goodsName;

        public ShopGoods() {
        }

        public ShopGoods(Long goodsId, Long shopId) {
            this.goodsId = goodsId;
            this.shopId = shopId;
        }

        public String docId() {
            return shopId + "-" + goodsId;
        }
    }

}
