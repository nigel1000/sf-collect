package com.common.collect.container.excel.annotations.model;

import com.common.collect.container.excel.annotations.ExcelCheck;
import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.ICheckImportHandler;
import com.common.collect.container.excel.define.check.MaxCheckImportHandler;
import com.common.collect.container.excel.define.check.RegexCheckImportHandler;
import com.common.collect.container.excel.define.check.RequireCheckImportHandler;
import com.common.collect.util.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

@Data
public class ExcelCheckModel {

    private boolean required;
    private String requiredTips;
    private long max;
    private String maxTips;
    private String regex;
    private String regexTips;
    private Class<? extends ICheckImportHandler>[] checkImportHandlers;
    private List<ICheckImportHandler> checkImportHandlersList = Lists.newArrayList();

    public static ExcelCheckModel gen(ExcelCheck excelCheck, @NonNull IBeanFactory beanFactory) {
        if (excelCheck == null) {
            return null;
        }
        ExcelCheckModel excelCheckModel = new ExcelCheckModel();
        excelCheckModel.setRequired(excelCheck.required());
        excelCheckModel.setRequiredTips(excelCheck.requiredTips());
        excelCheckModel.setMax(excelCheck.max());
        excelCheckModel.setMaxTips(excelCheck.maxTips());
        excelCheckModel.setRegex(excelCheck.regex());
        excelCheckModel.setRegexTips(excelCheck.regexTips());
        excelCheckModel.setCheckImportHandlers(excelCheck.checkImportHandlers());
        List<ICheckImportHandler> checkHandlers = Lists.newArrayList();
        List<Class<? extends ICheckImportHandler>> handlers =
                CollectionUtil.removeDuplicate(Arrays.asList(excelCheck.checkImportHandlers()));
        for (Class<? extends ICheckImportHandler> handler : handlers) {
            checkHandlers.add(beanFactory.getBean(handler));
        }
        if (excelCheck.required() && !handlers.contains(RequireCheckImportHandler.class)) {
            checkHandlers.add(beanFactory.getBean(RequireCheckImportHandler.class));
        }
        if (excelCheck.max() != Long.MIN_VALUE && !handlers.contains(MaxCheckImportHandler.class)) {
            checkHandlers.add(beanFactory.getBean(MaxCheckImportHandler.class));
        }
        if (StringUtils.isNotEmpty(excelCheck.regex()) && !handlers.contains(RegexCheckImportHandler.class)) {
            checkHandlers.add(beanFactory.getBean(RegexCheckImportHandler.class));
        }
        excelCheckModel.setCheckImportHandlersList(CollectionUtil.removeDuplicate(checkHandlers));
        return excelCheckModel;
    }

}
