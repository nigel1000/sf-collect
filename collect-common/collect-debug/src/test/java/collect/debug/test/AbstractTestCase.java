package collect.debug.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

@TestExecutionListeners({MockitoListener.class})
//@SpringBootTest(classes = {App.class})
@SpringBootTest
// 会扫描到 App.class，由于 App 上有 ImportSource，导致配置文件加载两次。dubbo 标签无法兼容加载两次直接抛错
// @ContextConfiguration(locations={"classpath:context-spring.xml"})
@Slf4j
public abstract class AbstractTestCase extends AbstractTestNGSpringContextTests {

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

    protected void mock() {
    }

}
