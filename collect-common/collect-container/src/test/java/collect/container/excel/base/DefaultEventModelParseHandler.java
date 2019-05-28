package collect.container.excel.base;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.excel.define.IEventModelParseHandler;
import com.common.collect.container.excel.pojo.EventModelParam;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

public class DefaultEventModelParseHandler implements IEventModelParseHandler {

    @Override
    public void handle(EventModelParam eventModelParam) {
        System.out.println(JsonUtil.bean2jsonPretty(eventModelParam));
    }

}
