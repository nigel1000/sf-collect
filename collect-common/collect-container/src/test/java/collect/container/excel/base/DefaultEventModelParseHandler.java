package collect.container.excel.base;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.excel.define.IEventModelParseHandler;
import com.common.collect.container.excel.pojo.EventModelParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

public class DefaultEventModelParseHandler implements IEventModelParseHandler {

    @Override
    public void handle(EventModelParam eventModelParam) {
        List<Domain> domains = toDomain(eventModelParam);
        System.out.println(JsonUtil.bean2jsonPretty(domains));
    }

    @Data
    private class Domain {
        private String s1;
        private String s2;
        private String s3;
        private String s4;
        private String s5;
        private String s6;
        private String s7;
    }

    public List<Domain> toDomain(EventModelParam eventModelParam) {
        List<List<String>> rows = eventModelParam.getRows();
        List<Domain> domains = new ArrayList<>();
        for (List<String> row : rows) {
            Domain domain = new Domain();
            domain.setS1(row.get(0));
            domain.setS2(row.get(1));
            domain.setS3(row.get(2));
            domain.setS4(row.get(3));
            domain.setS5(row.get(4));
            domain.setS6(row.get(5));
            domain.setS7(row.get(6));
            domains.add(domain);
        }
        return domains;
    }
}
