package com.common.collect.framework.excel.extension;

/**
 * Created by hznijianfeng on 2019/3/7.
 */

public enum OptionYesNo implements IOptionValue {

    NULL(null),

    NO("否"),
    YES("是"),

    ;

    private final String exportValue;

    OptionYesNo(String exportValue) {
        this.exportValue = exportValue;
    }

    @Override
    public OptionYesNo getByExportValue(String exportValue) {
        if (exportValue == null) {
            return NULL;
        }
        for (OptionYesNo type : OptionYesNo.values()) {
            if (exportValue.equals(type.exportValue))
                return type;
        }
        return NULL;
    }

    @Override
    public String getExportValue() {
        return exportValue;
    }

}
