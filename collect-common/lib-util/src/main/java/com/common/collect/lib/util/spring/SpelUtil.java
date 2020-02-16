package com.common.collect.lib.util.spring;

import lombok.NonNull;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/8/3.
 */
public class SpelUtil {

    public static EvaluationContext standardEvaluationContext(@NonNull Map<String, Object> variables) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        variables.forEach((k, v) -> {
            if (v instanceof Method) {
                context.registerFunction(k, (Method) v);
            } else {
                context.setVariable(k, v);
            }
        });
        return context;
    }

    public static <T> T spelExpressionParser(@NonNull Class<T> clazz,
                                             @NonNull EvaluationContext context,
                                             @NonNull String spelExpression) {
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(spelExpression, new TemplateParserContext()).getValue(context, clazz);
    }

    public static <T> T spelExpressionParser(@NonNull Class<T> clazz,
                                             @NonNull Map<String, Object> variables,
                                             @NonNull String spelExpression) {
        ExpressionParser parser = new SpelExpressionParser();
        return parser.parseExpression(spelExpression, new TemplateParserContext()).getValue(standardEvaluationContext(variables), clazz);
    }


    public static List<String> getMethodParamName(@NonNull Method method) {
        LocalVariableTableParameterNameDiscoverer discover = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNames = discover.getParameterNames(method);
        return Arrays.asList(paraNames);
    }

}
