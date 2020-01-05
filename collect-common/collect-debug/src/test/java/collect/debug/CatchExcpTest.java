package collect.debug;

import com.common.collect.api.Response;
import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.aops.CatchExcp;
import com.common.collect.container.trace.TraceIdUtil;
import com.common.collect.model.flowlog.FlowLog;
import com.common.collect.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by nijianfeng on 2019/3/17.
 */

@Slf4j
public class CatchExcpTest {

    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("context-spring.xml");
        DemoService demoService = applicationContext.getBean(DemoService.class);
        FlowLog flowLog = FlowLog.builder().bizId(IdUtil.uuidHex()).bizType("1").build();

        TraceIdUtil.initTraceId(null);
        Response<Integer> bizExcp = demoService.responseBizExcp(1, flowLog);
        TraceIdUtil.clearTraceId();

        TraceIdUtil.initTraceId(null);
        Response<Integer> sysExcp = demoService.responseSysExcp(2, flowLog);
        TraceIdUtil.clearTraceId();

        log.info("bizExcp:{}", bizExcp);
        log.info("sysExcp:{}", sysExcp);
    }

}

@CatchExcp(module = "test")
@Component
class DemoService {

    public Response<Integer> responseBizExcp(Integer count, FlowLog flowLog) {
        throw UnifiedException.gen("业务异常抛出", new RuntimeException("runtime exception"));
    }

    public Response<Integer> responseSysExcp(Integer count, FlowLog flowLog) {
        Integer i = null;
        i.toString();
        return Response.ok(i);
    }

}