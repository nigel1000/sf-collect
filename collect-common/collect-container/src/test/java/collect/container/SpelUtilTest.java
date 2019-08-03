package collect.container;

import com.common.collect.container.SpelUtil;
import com.common.collect.util.ClassUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/8/3.
 */

@Slf4j
public class SpelUtilTest {

    public static void main(String[] args) {
        log.info("{}", SpelUtil.getMethodParamName(
                ClassUtil.getMethod(ClassUtil.class, "getMethod", Class.class, String.class, Class[].class)));

        Map<String, Object> variables = new HashMap<>();
        variables.put("test", "test");
        EvaluationContext evaluationContext = SpelUtil.standardEvaluationContext(variables);

        log.info("{}", SpelUtil.spelExpressionParser(String.class, evaluationContext, "Hello World"));
        log.info("{}", SpelUtil.spelExpressionParser(Double.class, evaluationContext, "6.0221415E+23"));
        log.info("{}", SpelUtil.spelExpressionParser(BigDecimal.class, evaluationContext, "6.0221415E+23"));
        log.info("{}", SpelUtil.spelExpressionParser(Integer.class, evaluationContext, "0x7FFFFFFF"));
        log.info("{}", SpelUtil.spelExpressionParser(Boolean.class, evaluationContext, "true"));
        log.info("{}", SpelUtil.spelExpressionParser(Object.class, evaluationContext, "null"));

        variables.put("true", Boolean.FALSE);
        log.info("{}", SpelUtil.spelExpressionParser(Boolean.class, variables, "#{#true}"));
        log.info("{}", SpelUtil.spelExpressionParser(String.class, variables, "#{#test}"));
        variables.put("person", new Person("ni", "jianfeng"));
        log.info("{}", SpelUtil.spelExpressionParser(String.class, variables,
                "姓:#{#person.firstName},名:#{#person.lastName},list:#{#person.list[1]}"));

    }

    @Data
    private static class Person {

        private String firstName;
        private String lastName;
        private List<String> list;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            list = new ArrayList<>();
            list.add("1");
            list.add("2");
            list.add("3");
        }
    }

}
