package collect.debug.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@TestExecutionListeners({MockitoListener.class})
//@SpringBootTest(classes = {App.class})
// 会扫描到 App.class，由于 App 上有 ImportSource，导致配置文件加载两次。dubbo 标签无法兼容加载两次直接抛错
// @ContextConfiguration(locations={"classpath:context-spring.xml"})
@Slf4j
// 使用单元测试，想回滚事务，需要确认一下两点：
// 1、确认是否继承自 AbstractTransactionalTestNGSpringContextTests，只有该抽象类支持事务，AbstractTestNGSpringContextTests 这个类不支持
// 2、确认单测方法是否有@Rollback注解，注意：@Rollback(false) 不回滚
public abstract class AbstractTestCase extends AbstractTransactionalTestNGSpringContextTests {

    private long startTime;

    @BeforeMethod
    public void setUp() {
        startTime = System.currentTimeMillis();
        mock();
    }

    @AfterMethod
    public void destroy() {
        log.info("spend " + (System.currentTimeMillis() - startTime) / 1000 + " s");
    }

    protected void mock() {}

}