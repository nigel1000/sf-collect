package lib.util;

import com.common.collect.lib.api.Response;
import com.common.collect.lib.util.ClassUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/7/21.
 */
public class ClassUtilTest {

    public static void main(String[] args) {


        for (Method method : ClassUtil.getDeclaredMethods(ClassUtilTest.class)) {
            if (method.getName().equals("test")) {
                System.out.println(ClassUtil.getMethodReturnGenericTypeMap(method));
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    System.out.println(ClassUtil.getMethodParameterGenericTypeMap(method, i));
                }
            }
        }
        System.out.println(ClassUtil.getSuperInterfaceGenericTypeMap(CallableTest.class));
        System.out.println(ClassUtil.getSuperClassGenericTypeMap(CallableTest.class));
        System.out.println(ClassUtil.getClazzFromPackage("com.common.collect.lib.api.docs"));
    }

    public Response<Integer> test(List<Integer[]> list, Map<String, Long> map) {
        return null;
    }

    private static class CallableTest
            extends HashMap<List<Double>, BigDecimal>
            implements Runnable, Callable<Object>, Supplier<List<Integer>>, Function<Double, Float> {
        @Override
        public Object call() {
            return null;
        }

        @Override
        public Float apply(Double aDouble) {
            return null;
        }

        @Override
        public List<Integer> get() {
            return null;
        }

        @Override
        public void run() {

        }
    }

}
