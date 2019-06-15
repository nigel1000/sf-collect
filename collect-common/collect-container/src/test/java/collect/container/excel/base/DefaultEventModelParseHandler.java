package collect.container.excel.base;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.excel.context.EventModelContext;
import com.common.collect.container.excel.define.IEventModelParseHandler;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

public class DefaultEventModelParseHandler implements IEventModelParseHandler {

    @Override
    public void handle(EventModelContext eventModelContext) {
        System.out.println("#################################");
        System.out.println(JsonUtil.bean2jsonPretty(eventModelContext.getRows()));
        for (List<String> obj : eventModelContext.getRows()) {
            System.out.println(JsonUtil.bean2jsonPretty(toDomain(obj, Domain.class)));
        }
    }

}
