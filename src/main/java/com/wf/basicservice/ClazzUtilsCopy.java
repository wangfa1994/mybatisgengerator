package com.wf.basicservice;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ClazzUtilsCopy {

    private static Logger log = LoggerFactory.getLogger(ClazzUtilsCopy.class);

    /**
     * Just test ClassUtils
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        String packageName = "com.souche.appstorecommoditycomm.core.model";
        Set<String> classNames = getClassName(packageName, true);
        if (classNames != null) {
            for (String className : classNames) {
                System.out.println(className);
            }
        }
    }


    /**
     *
     * @Author: wangfa
     * @Date: 2019/5/30 下午7:04
     * @Description: 得到全路径的类名称
     * @param packageName  要查找的包名称
     * @param isRecursion  是否展示子包名称
     * @return 全路径的包名
     * @throws IOException
     */
    public static Set<String> getClassName(String packageName, boolean isRecursion) throws IOException {
        Set<String> classNames = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = packageName.replace('.', '/');

        URL url = loader.getResource(packagePath);
        if (url != null) {
            String protocol = url.getProtocol();
            if ("file".equals(protocol)) {
                classNames = getClassNameFromDir(url.getPath(), packageName, isRecursion);
            } else if ("jar".equals(protocol)) {
                JarFile jarFile = null;
                try {
                    jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                } catch (Exception e) {
                    log.error(e.getCause().getMessage());
                    throw e;
                }

                if (jarFile != null) {
                    classNames = getClassNameFromJar(jarFile.entries(), packageName, isRecursion);
                }
            }
        } else {
            classNames = getClassNameFromJars(((URLClassLoader) loader).getURLs(), packageName, isRecursion);
        }

        if (StringUtils.isEmpty(packageName) && !CollectionUtils.isEmpty(classNames)) {
            Set<String> allClassNames = new TreeSet<>();
            for (String className : classNames) {
                if (className.indexOf('.') == 0) {
                    allClassNames.add(className.substring(1));
                }
            }
            return allClassNames;
        } else {
            return classNames;
        }
    }

    /**
     * Gets a packet of all classes.
     *
     * @return getAllClassNames
     * @throws IOException IOException
     */
    public static Set<String> getAllClassNames() throws IOException {
        return getClassName("", true);
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
    private static Set<String> getClassNameFromDir(String filePath, String packageName, boolean isRecursion) {
        Set<String> className = new HashSet<String>();
        File file = new File(filePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File childFile : files) {
                if (childFile.isDirectory()) {
                    if (isRecursion) {
                        className.addAll(getClassNameFromDir(childFile.getPath(), packageName + "." + childFile.getName(), isRecursion));
                    }
                } else {
                    String fileName = childFile.getName();
                    if (fileName.endsWith(".class") && !fileName.contains("$")) {
                        className.add(packageName + "." + fileName.replace(".class", ""));
                    }
                }
            }
        }
        return className;
    }


    /**
     * Gets a package of all classes from the project file
     *
     * @param jarEntries  jarEntries
     * @param packageName packageName
     * @param isRecursion Whether to traverse the subpackage
     * @return The full name of the class
     */
    private static Set<String> getClassNameFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion) {
        Set<String> classNames = new HashSet<String>();

        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (!jarEntry.isDirectory()) {
                String entryName = jarEntry.getName().replace('/', '.');
                if (entryName.endsWith(".class") && !entryName.contains("$") && entryName.startsWith(packageName)) {
                    entryName = entryName.replace(".class", "");
                    if (isRecursion) {
                        classNames.add(entryName);
                    } else if (!entryName.replace(packageName + ".", "").contains(".")) {
                        classNames.add(entryName);
                    }
                }
            }
        }

        return classNames;
    }

    /**
     * Search for the package from all jars and get all the classes under that package
     *
     * @param urls        urls
     * @param packageName packageName
     * @param isRecursion Whether to traverse the subpackage
     * @return The full name of the class
     */
    private static Set<String> getClassNameFromJars(URL[] urls, String packageName, boolean isRecursion) throws IOException {
        Set<String> classNames = new HashSet<String>();

        for (int i = 0; i < urls.length; i++) {
            String classPath = urls[i].getPath();

            //You do not have to search the classes folder.
            if (classPath.endsWith("classes/")) {
                continue;
            }

            JarFile jarFile = null;
            try {
                jarFile = new JarFile(classPath.substring(classPath.indexOf('/')));
                classNames.addAll(getClassNameFromJar(jarFile.entries(), packageName, isRecursion));
            } catch (IOException e) {
                log.error(e.getCause().getMessage());
                throw e;
            } finally {
                if (jarFile != null) {
                    jarFile.close();
                }
            }
        }

        return classNames;
    }
}