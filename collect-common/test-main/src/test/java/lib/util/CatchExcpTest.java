package lib.util;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.api.excps.UnifiedException;
import com.common.collect.lib.util.TraceIdUtil;
import com.common.collect.lib.util.spring.aop.CatchExcp;
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

        TraceIdUtil.initTraceId(null);
        Response<Integer> bizExcp = demoService.responseBizExcp(1);
        TraceIdUtil.clearTraceId();

        TraceIdUtil.initTraceId(null);
        Response<Integer> sysExcp = demoService.responseSysExcp(2);
        TraceIdUtil.clearTraceId();

        log.info("bizExcp:{}", bizExcp);
        log.info("sysExcp:{}", sysExcp);
    }

    @CatchExcp(module = "test")
    @Component
    public static class DemoService {

        public Response<Integer> responseBizExcp(Integer count) {
            throw UnifiedException.gen("业务异常抛出", new RuntimeException("runtime exception"));
        }

        public Response<Integer> responseSysExcp(Integer count) {
            Integer i = null;
            i.toString();
            return Response.ok(i);
        }

    }

}

