package collect.debug.test;

import com.common.collect.lib.util.spring.AopUtil;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockitoListener extends DependencyInjectionTestExecutionListener {

    private Set<Field> injectFields = new HashSet<>();
    private Map<String, Object> mockObjectMap = new HashMap<>();

    @Override
    protected void injectDependencies(TestContext testContext) throws Exception {
        super.injectDependencies(testContext);
        init(testContext);
    }

    // mybatis 的 mapper 类是 final 的，不能被 mock
    // Mockito cannot mock/spy following:
    // - final classes
    // - anonymous classes
    // - primitive types

    /**
     * 通过 spring bean 的 name 进行获取 bean
     * <p>
     * 当使用 @Mock 时，用在非 spring bean 托管的类，直接 mock 此类
     * <p>
     * 当使用 @Spy 时，用在 spring bean 托管的类，根据 field 命名获取真实的 bean，用 spy 的类替换掉代理里的目标类
     * <p>
     * 将标记@Mock、@Spy等注解的属性值注入到标记 @Autowired|@Resource 类
     */
    private void init(TestContext testContext) throws Exception {

        AutowireCapableBeanFactory factory = testContext.getApplicationContext().getAutowireCapableBeanFactory();
        Object bean = testContext.getTestInstance();
        Field[] fields = bean.getClass().getDeclaredFields();

        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Mock) {
                    Class<?> clazz = field.getType();
                    Object object = Mockito.mock(clazz);
                    field.setAccessible(true);
                    field.set(bean, object);
                    mockObjectMap.put(field.getName(), object);
                } else if (annotation instanceof Spy) {
                    // 代理类不能被 mock spy 因为 $Proxy is final
                    Object fb = factory.getBean(field.getName());
                    // 对被代理类进行 mock spy
                    Object targetSource = AopUtil.getTarget(fb);
                    Object spyObject = Mockito.spy(targetSource);
                    if (!fb.equals(targetSource)) {
                        // 是代理类， mock spy 替换掉被代理类
                        if (AopUtils.isJdkDynamicProxy(fb)) {
                            AopUtil.setJdkDynamicProxyTargetObject(fb, spyObject);
                        } else { // cglib
                            AopUtil.setCglibProxyTargetObject(fb, spyObject);
                        }
                    } else {
                        mockObjectMap.put(field.getName(), spyObject);
                    }
                    field.setAccessible(true);
                    field.set(bean, spyObject);
                } else if (annotation instanceof Autowired || annotation instanceof Resource) {
                    injectFields.add(field);
                }
            }
        }
        for (Field field : injectFields) {
            field.setAccessible(true);
            Object fo = field.get(bean);
            if (AopUtils.isAopProxy(fo)) {
                Class targetClass = AopUtils.getTargetClass(fo);
                Object targetSource = AopUtil.getTarget(fo);
                Field[] targetFields = targetClass.getDeclaredFields();
                for (Field targetField : targetFields) {
                    targetField.setAccessible(true);
                    if (mockObjectMap.get(targetField.getName()) == null) {
                        continue;
                    }
                    ReflectionTestUtils.setField(targetSource, targetField.getName(),
                            mockObjectMap.get(targetField.getName()));
                }

            } else {
                Object realObject = factory.getBean(field.getType());
                Field[] targetFields = realObject.getClass().getDeclaredFields();
                for (Field targetField : targetFields) {
                    targetField.setAccessible(true);
                    if (mockObjectMap.get(targetField.getName()) == null) {
                        continue;
                    }
                    ReflectionTestUtils.setField(fo, targetField.getName(), mockObjectMap.get(targetField.getName()));
                }
            }
        }
    }

}
