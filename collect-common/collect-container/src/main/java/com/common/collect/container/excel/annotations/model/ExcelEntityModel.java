package com.common.collect.container.excel.annotations.model;

import com.common.collect.api.excps.UnifiedException;
import com.common.collect.container.excel.annotations.ExcelEntity;
import com.common.collect.container.excel.base.ExcelConstants;
import com.common.collect.container.excel.define.IBeanFactory;
import com.common.collect.container.excel.define.ICellConfig;
import com.common.collect.container.excel.define.bean.SingletonBeanFactory;
import lombok.Data;

/**
 * Created by hznijianfeng on 2019/3/8.
 */

@Data
public class ExcelEntityModel {

    private Class<? extends ICellConfig> cellConfig;
    private ICellConfig cellConfigImpl;

    private Class<? extends IBeanFactory> beanFactory;
    private IBeanFactory beanFactoryImpl;

    public static ExcelEntityModel gen(ExcelEntity excelEntity) {
        ExcelEntityModel excelEntityModel = new ExcelEntityModel();
        if (excelEntity == null) {
            excelEntityModel.setBeanFactory(SingletonBeanFactory.class);
            excelEntityModel.setBeanFactoryImpl(new SingletonBeanFactory());
            return excelEntityModel;
        }
        excelEntityModel.setBeanFactory(excelEntity.beanFactory());
        try {
            excelEntityModel.setBeanFactoryImpl(excelEntityModel.getBeanFactory().newInstance());
        } catch (Exception ex) {
            throw UnifiedException.gen(ExcelConstants.MODULE, "工厂类初始化失败", ex);
        }

        excelEntityModel.setCellConfig(excelEntity.cellConfig());
        excelEntityModel.setCellConfigImpl(excelEntityModel.getBeanFactoryImpl().getBean(excelEntity.cellConfig()));

        return excelEntityModel;
    }

}
