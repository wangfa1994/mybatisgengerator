package com.wf.basicservice;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class ClazzUtils {

    private static Logger logger = LoggerFactory.getLogger(ClazzUtils.class);

    /**
     * Just test ClassUtils
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        String packageName = "com.souche.appstorecommoditycomm.core.model";
        Map<String,String> classNames = getClassName(packageName, true);
        if (classNames != null) {
            for (Map.Entry<String, String> entry : classNames.entrySet()) {
                System.out.println(entry.getKey()+" : "+entry.getValue());
            }
        }
    }


    /**
     *
     * @Author: wangfa
     * @Date: 2019/5/30 下午7:04
     * @Description: 得到类名称
     * @param packageName  要查找的包名称
     * @param isRecursion  是否展示子包名称
     * @return 全路径的包名 简类名/全类名
     * @throws IOException
     */
    public static Map<String,String> getClassName(String packageName, boolean isRecursion){
        Map<String,String> classNames = new HashMap();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace('.', '/');

        URL url = loader.getResource(packagePath);
        if (url != null) {
            try {
                classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if (StringUtils.isEmpty(packageName) && !classNames.isEmpty()) {
            Map<String,String> allClassNames = new HashMap<>();

            for (Map.Entry<String, String> entry: classNames.entrySet()) {
                String simpleModel = entry.getKey();
                String model = entry.getValue();
                if (model.indexOf('.') == 0) {
                    allClassNames.put(simpleModel,model.substring(1));
                }
            }
            return allClassNames;
        } else {
            return classNames;
        }
    }

    /**
     *
     * @Author: wangfa
     * @Date: 2019/5/30 下午7:08
     * @Description: 递归获取所有class文件的名字
     *
     * @param filePath
     * @param packageName
     * @param isRecursion
     * @return
     */
    private static Map<String,String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
        Map<String,String> className = new HashMap<>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File childFile : files) {
                if (childFile.isDirectory()) {
                    if (isRecursion) {
                        className.putAll(getClassNameFromDir(childFile.getPath(), packageName + "." + childFile.getName(), isRecursion));
                    }
                } else {
                    String fileName = childFile.getName();
                    if (fileName.endsWith(".class") && !fileName.contains("$")) {
                            className.put(fileName.replace(".class", ""),packageName + "." + fileName.replace(".class", ""));
                    }
                }
            }
        }
        return className;
    }
}