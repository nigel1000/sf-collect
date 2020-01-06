package com.common.collect.util;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hznijianfeng on 2017/2/14.
 */
public class SqlUtil {

    private StringBuilder sb = new StringBuilder();
    private List<Object> args = new ArrayList<>();
    private boolean hasWhere = false;
    private boolean hasSet = false;

    private SqlUtil() {
    }

    public static SqlUtil of(@NonNull String... sqls) {
        SqlUtil sqlUtil = new SqlUtil();
        for (String sql : sqls) {
            sqlUtil.sql(sql);
        }
        return sqlUtil;
    }

    public boolean hasWhere() {
        return hasWhere;
    }

    public boolean hasSet() {
        return hasSet;
    }

    public String getSql() {
        return sb.toString();
    }

    public List<Object> getSqlArgs() {
        return args;
    }

    public Object[] getSqlArgsArray() {
        return args.toArray();
    }

    public SqlUtil addSqlArgs(@NonNull List<Object> args) {
        this.args.addAll(args);
        return this;
    }

    public SqlUtil addSqlArg(@NonNull Object arg) {
        this.args.add(arg);
        return this;
    }

    public SqlUtil sql(String sql) {
        if (sql == null) {
            return this;
        }
        sb.append(sql).append(" ");
        return this;
    }

    // and
    public SqlUtil whereEqAnd(@NonNull String field, Object value) {
        return where("=", "and", field, value);
    }

    public SqlUtil whereGtAnd(@NonNull String field, Object value) {
        return where(">", "and", field, value);
    }

    public SqlUtil whereGtEqAnd(@NonNull String field, Object value) {
        return where(">=", "and", field, value);
    }

    public SqlUtil whereLtAnd(@NonNull String field, Object value) {
        return where("<", "and", field, value);
    }

    public SqlUtil whereLtEqAnd(@NonNull String field, Object value) {
        return where("<=", "and", field, value);
    }

    public SqlUtil whereLikeTailAnd(@NonNull String field, String value) {
        if (EmptyUtil.isBlank(value)) {
            return this;
        }
        return where("like", "and", field, value + "%");
    }

    public SqlUtil whereLikeHeadAnd(@NonNull String field, String value) {
        if (EmptyUtil.isBlank(value)) {
            return this;
        }
        return where("like", "and", field, "%" + value);
    }

    public SqlUtil whereLikeAroundAnd(@NonNull String field, String value) {
        if (EmptyUtil.isBlank(value)) {
            return this;
        }
        return where("like", "and", field, "%" + value + "%");
    }

    public SqlUtil whereInAnd(@NonNull String field, List<?> values) {
        return whereIn("in", "and", field, values);
    }

    public SqlUtil whereNotInAnd(@NonNull String field, List<?> values) {
        return whereIn("not in", "and", field, values);
    }

    // or
    public SqlUtil whereEqOr(@NonNull String field, Object value) {
        return where("=", "or", field, value);
    }

    public SqlUtil whereGtOr(@NonNull String field, Object value) {
        return where(">", "or", field, value);
    }

    public SqlUtil whereGtEqOr(@NonNull String field, Object value) {
        return where(">=", "or", field, value);
    }

    public SqlUtil whereLtOr(@NonNull String field, Object value) {
        return where("<", "or", field, value);
    }

    public SqlUtil whereLtEqOr(@NonNull String field, Object value) {
        return where("<=", "or", field, value);
    }

    public SqlUtil whereLikeTailOr(@NonNull String field, String value) {
        if (EmptyUtil.isBlank(value)) {
            return this;
        }
        return where("like", "or", field, value + "%");
    }

    public SqlUtil whereLikeHeadOr(@NonNull String field, String value) {
        if (EmptyUtil.isBlank(value)) {
            return this;
        }
        return where("like", "or", field, "%" + value);
    }

    public SqlUtil whereLikeAroundOr(@NonNull String field, String value) {
        if (EmptyUtil.isBlank(value)) {
            return this;
        }
        return where("like", "or", field, "%" + value + "%");
    }

    public SqlUtil whereInOr(@NonNull String field, List<?> values) {
        return whereIn("in", "or", field, values);
    }

    public SqlUtil whereNotInOr(@NonNull String field, List<?> values) {
        return whereIn("not in", "or", field, values);
    }

    // order by
    public SqlUtil orderBy(String orderBy) {
        if (EmptyUtil.isBlank(orderBy)) {
            return this;
        }
        sb.append("order by ");
        sb.append(orderBy);
        sb.append(" ");
        return this;
    }

    // limit
    public SqlUtil limit(int limit) {
        sb.append("limit ");
        sb.append(limit);
        sb.append(" ");
        return this;
    }

    // offset
    public SqlUtil offset(int offset) {
        sb.append("offset ");
        sb.append(offset);
        sb.append(" ");
        return this;
    }

    // set
    public SqlUtil set(@NonNull String field,
                       Object value) {
        if (value == null) {
            return this;
        }
        startSet();
        sb.append(field);
        sb.append(" = ");
        addSqlArg(value);
        sb.append("? ");
        return this;
    }

    public SqlUtil set(String sql) {
        if (sql == null) {
            return this;
        }
        startSet();
        sb.append(sql);
        sb.append(" ");
        return this;
    }

    private void startSet() {
        if (!hasSet) {
            hasSet = true;
            sb.append("set");
        } else {
            sb.append(",");
        }
        sb.append(" ");
    }

    private void startWhere(@NonNull String relate) {
        if (!hasWhere) {
            hasWhere = true;
            sb.append("where");
        } else {
            sb.append(relate);
        }
        sb.append(" ");
    }

    // relate and or
    // symbol != = > <
    private SqlUtil where(@NonNull String compare,
                          @NonNull String relate,
                          @NonNull String field,
                          Object value) {
        if (value == null) {
            return this;
        }
        startWhere(relate);
        sb.append(field);
        sb.append(" ");
        sb.append(compare);
        addSqlArg(value);
        sb.append(" ? ");
        return this;
    }

    private SqlUtil whereIn(@NonNull String compare, @NonNull String relate, @NonNull String field, List<?> values) {
        if (EmptyUtil.isEmpty(values)) {
            return this;
        }
        int size = values.size() - 1;
        for (int i = 0; i < size + 1; i++) {
            if (i == 0) {
                startWhere(relate);
                sb.append(field);
                sb.append(" ");
                sb.append(compare);
                sb.append(" (");
            }
            addSqlArg(values.get(i));
            sb.append("?");
            if (size == i) {
                sb.append(") ");
            } else {
                sb.append(",");
            }
        }
        return this;
    }

}