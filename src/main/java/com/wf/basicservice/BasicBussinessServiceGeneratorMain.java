package com.wf.basicservice;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * @Author: wangfa
 * @Date: 2019/5/29 17:55
 * @Description: 生成对应的BasicService
 */
public class BasicBussinessServiceGeneratorMain {


    public static void main(String[] args) {

        // Dom4j解析获取packageName和basePackageName 用于生成BasicService
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("generatorConfig.xml");
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(is);
            Element rootElement = doc.getRootElement();
            Element context = rootElement.element("context");
            Element element = context.element("javaModelGenerator");
            Attribute targetPackage = element.attribute("targetPackage");
            String packageName = targetPackage.getValue();
            int i = packageName.lastIndexOf(".");
            String basePackageName = packageName.substring(0,i);
            // 调用创建
            Map<String,String> simplelassNames = ClazzUtils.getClassName(packageName, true);
            for(Map.Entry<String, String> entry :simplelassNames.entrySet()){
                String simpleModel = entry.getKey();
                String model = entry.getValue();

                // 初始化接口 VelocityEngine
                initBaseServicetInterface(basePackageName, simpleModel, model);
                // 初始化接口实现
                initBaseServicetInterfaceImpl(basePackageName,simpleModel,model);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





    private static void initBaseServicetInterface(String basePackageName, String simpleModel, String model) {
        VelocityEngine ve=new VelocityEngine();
        //设置模板加载路径，这里设置的是class下
        ve.setProperty(Velocity.RESOURCE_LOADER, "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        try {
            //进行初始化操作
            ve.init();
            //加载模板，设定模板编码
            Template t=ve.getTemplate("templates/BasicService.tmpl","UTF-8");
            //设置初始化数据
            Map<String,Object> tool =new HashMap<>();
            tool.put("simpleModel", simpleModel);
            tool.put("model", model);
            tool.put("basePackageName",basePackageName);

            VelocityContext context = new VelocityContext(tool);

            //TODO 设置输出 如何将文件输出到指定的文件目录 待完成
            PrintWriter writer = new PrintWriter(simpleModel+"BasicService.java");

            //将环境数据转化输出
            t.merge(context, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initBaseServicetInterfaceImpl(String basePackageName, String simpleModel, String model) {
        VelocityEngine ve=new VelocityEngine();
        //设置模板加载路径，这里设置的是class下
        ve.setProperty(Velocity.RESOURCE_LOADER, "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        try {
            //进行初始化操作
            ve.init();
            //加载模板，设定模板编码
            Template t=ve.getTemplate("templates/BasicServiceImpl.tmpl","UTF-8");
            //设置初始化数据
            Map<String,Object> tool =new HashMap<>();
            tool.put("model", model);
            tool.put("simpleModel", simpleModel);
            tool.put("mapper",model.replace("model","dao"));
            tool.put("basePackageName",basePackageName);
            tool.put("simpleModuleVar",new StringBuilder().append(Character.toLowerCase(simpleModel.charAt(0))).append(simpleModel.substring(1)).toString());

            VelocityContext context = new VelocityContext(tool);

            //设置输出

            PrintWriter writer = new PrintWriter(simpleModel+"BasicServiceImpl.java");

            //将环境数据转化输出
            t.merge(context, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
