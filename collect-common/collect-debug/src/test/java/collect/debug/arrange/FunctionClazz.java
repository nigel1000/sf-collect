package collect.debug.arrange;

import com.common.collect.container.JsonUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 2019/7/6.
 */

@Component("functionClazz")
@Slf4j
public class FunctionClazz {

    private static int count = 1;

    public FunctionTestContext testFunction(FunctionTestContext context) {
        log.info("FunctionClazz param:{}", JsonUtil.bean2json(context));
        FunctionTestContext ret = new FunctionTestContext();
        ret.setOut2(Lists.newArrayList(count++));
        return ret;
    }

}
