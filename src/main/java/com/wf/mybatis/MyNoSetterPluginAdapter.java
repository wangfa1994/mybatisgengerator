package com.wf.mybatis;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * @Author: wangfa
 * @Date: 2020/6/20 10:45
 * @Description: 用于移除model生成的getter和setter方法，使用于Lombok
 *  如果想要使用需要在配置文件generatorConfig中添加配置
 *          <plugin type="com.wf.mybatis.MyNoSetterPluginAdapter"/>
 *
 */
public class MyNoSetterPluginAdapter extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    /**
     * 表示生成Model的时候的处理
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        /**
         * 添加注解和导包
         * */
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        return super.modelBaseRecordClassGenerated(topLevelClass,
                introspectedTable);
    }


    /**
     * 在生成getter方法的时候，进行处理，不想生成getter，直接返回false即可；
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    /**
     * 在生成setter方法的时候，进行处理，不想生成getter，直接返回false即可；
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

}
