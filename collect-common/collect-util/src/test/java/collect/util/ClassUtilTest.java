package collect.util;

import com.common.collect.util.ClassUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by nijianfeng on 2019/7/21.
 */
public class ClassUtilTest {

    public static void main(String[] args) {

        System.out.println(ClassUtil.getSuperClassGenericType(CallableTest.class, 1));
        System.out.println(ClassUtil.getSuperInterfaceGenericType(CallableTest.class, 0));

    }

    private static class CallableTest
            extends HashMap<Double, BigDecimal>
            implements Callable<Object>, Supplier<Integer>, Function<Double, Float> {
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
    }

}
