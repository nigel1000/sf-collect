package lib.util;

import com.common.collect.lib.util.ClassUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/7/21.
 */
public class ClassUtilTest {

    public static void main(String[] args) {

        System.out.println(ClassUtil.getSuperClassGenericType(CallableTest.class, 0));
        System.out.println(ClassUtil.getSuperClassGenericType(CallableTest.class, 1));

        System.out.println(ClassUtil.getSuperInterfaceGenericType(CallableTest.class, Callable.class,0));

        System.out.println(ClassUtil.getSuperInterfaceGenericType(CallableTest.class, Supplier.class,0));

        System.out.println(ClassUtil.getSuperInterfaceGenericType(CallableTest.class, Function.class, 0));
        System.out.println(ClassUtil.getSuperInterfaceGenericType(CallableTest.class, Function.class,1));

        System.out.println(ClassUtil.getClazzFromPackage("com.common.collect.slf4j"));

    }

    private static class CallableTest
            extends HashMap<List<Double>, BigDecimal>
            implements Runnable, Callable<Object>, Supplier<Integer>, Function<Double, Float> {
        @Override
        public Object call() {
            return null;
        }

        @Override
        public Float apply(Double aDouble) {
            return null;
        }

        @Override
        public Integer get() {
            return null;
        }

        @Override
        public void run() {

        }
    }

}
