package collect.container;

import com.common.collect.container.elastic.AbstractElasticMapper;
import com.common.collect.container.elastic.ElasticClient;
import com.common.collect.container.elastic.JoinField;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/2/22.
 */

public class ElasticTest {

    public static void main(String[] args) {
        AbstractElasticMapper goodsElasticMapper = new AbstractElasticMapper() {
            @Override
            public RestHighLevelClient getElasticClient() {
                ElasticClient client = new ElasticClient();
                client.setHost("127.0.0.1");
                client.setPort("2800");
                client.setSchema("http");
                return client.getRestHighLevelClient();
            }

            @Override
            public String getIndex() {
                return "shop_goods_index";
            }

            @Override
            public String getType() {
                return "_doc";
            }

            @Override
            public Class getIndexClass() {
                return GoodsElastic.class;
            }

            @Override
            public JoinField getJoinField() {
                return new JoinField("shop_goods");
            }

        };

        System.out.println(goodsElasticMapper.get("59142660"));
        System.exit(-1);
    }

    @ToString
    @Data
    public static class GoodsElastic {
        private Long goodsId;
        private String goodsName;
        private List<Long> goodsCategoryIds;
        private Integer goodsSaleCount;
        private Integer goodsIsHidden;
        private Integer goodsIsOnline;
        private Integer goodsIsDelete;
        private Integer goodsHasStore;
        private JoinField myJoinField;
    }

}
