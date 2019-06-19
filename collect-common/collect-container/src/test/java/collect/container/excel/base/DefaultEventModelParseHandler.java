package collect.container.excel.base;

import com.common.collect.container.JsonUtil;
import com.common.collect.container.excel.context.EventModelContext;
import com.common.collect.container.excel.define.IEventModelParseHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by hznijianfeng on 2019/5/28.
 */

@Slf4j
public class DefaultEventModelParseHandler implements IEventModelParseHandler {

    @Override
    public void handle(EventModelContext eventModelContext) {
        log.info("#################################");
        log.info("row size:" + eventModelContext.getRows().size());
        for (List<String> obj : eventModelContext.getRows()) {
            log.info("col size:" + obj.size());
            log.info(JsonUtil.bean2json(obj));
            log.info(JsonUtil.bean2json(toDomain(obj, Domain.class)));
        }
    }

}
