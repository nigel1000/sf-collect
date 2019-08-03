package collect.container;

import com.common.collect.container.SPELUtil;
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
public class SPELUtilTest {

    public static void main(String[] args) {
        log.info("{}", SPELUtil.getMethodParamName(
                ClassUtil.getMethod(ClassUtil.class, "getMethod", Class.class, String.class, Class[].class)));

        Map<String, Object> variables = new HashMap<>();
        variables.put("test", "test");
        EvaluationContext evaluationContext = SPELUtil.standardEvaluationContext(variables);

        log.info("{}", SPELUtil.spelExpressionParser(String.class, evaluationContext, "Hello World"));
        log.info("{}", SPELUtil.spelExpressionParser(Double.class, evaluationContext, "6.0221415E+23"));
        log.info("{}", SPELUtil.spelExpressionParser(BigDecimal.class, evaluationContext, "6.0221415E+23"));
        log.info("{}", SPELUtil.spelExpressionParser(Integer.class, evaluationContext, "0x7FFFFFFF"));
        log.info("{}", SPELUtil.spelExpressionParser(Boolean.class, evaluationContext, "true"));
        log.info("{}", SPELUtil.spelExpressionParser(Object.class, evaluationContext, "null"));

        variables.put("true", Boolean.FALSE);
        log.info("{}", SPELUtil.spelExpressionParser(Boolean.class, variables, "#{#true}"));
        log.info("{}", SPELUtil.spelExpressionParser(String.class, variables, "#{#test}"));
        variables.put("person", new Person("ni", "jianfeng"));
        log.info("{}", SPELUtil.spelExpressionParser(String.class, variables,
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
