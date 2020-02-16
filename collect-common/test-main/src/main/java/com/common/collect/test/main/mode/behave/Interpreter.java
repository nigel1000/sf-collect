package com.common.collect.test.main.mode.behave;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nijianfeng on 2019/11/3.
 * <p>
 * 提供文法的一种表达，并提供一个解释器
 * 抽象表达式 终结符表达式 非终结符表达式
 */

@Slf4j
public class Interpreter {

    public static void main(String[] args) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.assign("f", false);
        expressionContext.assign("t", true);

        VariableExpression f = new VariableExpression("f");
        VariableExpression t = new VariableExpression("t");

        ConstantExpression yes = new ConstantExpression(true);

        Expression expression = new NotExpression(new OrExpression(new AndExpression(yes, f), t));
        log.info(expression.print());
        log.info("{}", expression.interpret(expressionContext));
    }

}

interface Expression {

    boolean interpret(ExpressionContext ctx);

    String print();
}

// 提供解释器之外的一些全局信息
class ExpressionContext {
    private Map<String, Boolean> varMap = new HashMap<>();

    boolean lookup(VariableExpression var) {
        return varMap.get(var.getKey());
    }

    void assign(String var, boolean value) {
        varMap.put(var, value);
    }
}

@Slf4j
// 终结符表达式
class ConstantExpression implements Expression {

    private Boolean value;

    ConstantExpression(Boolean value) {
        this.value = value;
    }

    @Override
    public boolean interpret(ExpressionContext ctx) {
        return value;
    }

    @Override
    public String print() {
        return value.toString();
    }
}

@Slf4j
// 终结符表达式
class VariableExpression implements Expression {

    @Getter
    String key;

    VariableExpression(String key) {
        this.key = key;
    }

    @Override
    public boolean interpret(ExpressionContext ctx) {
        return ctx.lookup(this);
    }

    @Override
    public String print() {
        return key;
    }
}

@Slf4j
// 非终结符表达式
class AndExpression implements Expression {

    @Getter
    private Expression left;
    private Expression right;

    AndExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(ExpressionContext ctx) {
        return left.interpret(ctx) && right.interpret(ctx);
    }

    @Override
    public String print() {
        return "( " + left.print() + " and " + right.print() + " )";
    }
}

@Slf4j
// 非终结符表达式
class OrExpression implements Expression {

    @Getter
    private Expression left;
    private Expression right;

    OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean interpret(ExpressionContext ctx) {
        return left.interpret(ctx) || right.interpret(ctx);
    }

    @Override
    public String print() {
        return "( " + left.print() + " or " + right.print() + " )";
    }
}

@Slf4j
// 非终结符表达式
class NotExpression implements Expression {

    @Getter
    private Expression exp;

    NotExpression(Expression exp) {
        this.exp = exp;
    }

    @Override
    public boolean interpret(ExpressionContext ctx) {
        return !exp.interpret(ctx);
    }

    @Override
    public String print() {
        return "( not " + exp.print() + " )";
    }
}