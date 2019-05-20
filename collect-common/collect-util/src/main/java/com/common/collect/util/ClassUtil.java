package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by nijianfeng on 2019/5/19.
 */
public class ClassUtil {

    public static Object returnBaseDataType(Class<?> returnType) {
        if (returnType != null && returnType.isPrimitive()) {
            if (Boolean.TYPE == returnType) {
                return false;
            }
            if (Byte.TYPE == returnType) {
                return (byte) 0;
            }
            if (Short.TYPE == returnType) {
                return (short) 0;
            }
            if (Integer.TYPE == returnType) {
                return 0;
            }
            if (Float.TYPE == returnType) {
                return 0f;
            }
            if (Long.TYPE == returnType) {
                return 0L;
            }
            if (Double.TYPE == returnType) {
                return 0d;
            }
            if (Character.TYPE == returnType) {
                return (char) 0;
            }
        }
        return null;
    }

    public static List<Class> getSuperclasses(Class clazz) {
        List<Class> result = new ArrayList<>();
        result.add(clazz);
        result.addAll(Arrays.asList(clazz.getInterfaces()));
        if (clazz.equals(Object.class)) {
            return result;
        }
        result.addAll(getSuperclasses(clazz.getSuperclass()));
        return result;
    }

    /**
     * 获得包下面的所有的class
     * List包含所有class的实例
     */
    public static List<Class<?>> getClazzFromPackage(String packageName) {
        List<Class<?>> clazzs = new ArrayList<>();
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    findClassesByFile(packageName, filePath, true, clazzs);
                } else if ("jar".equals(protocol)) {// 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    findClassesByJar(packageName, jar, clazzs);
                }
            }

        } catch (Exception e) {
            throw UnifiedException.gen("getClazzFromPackage failed");
        }
        return CollectionUtil.removeDuplicate(clazzs);
    }

    /**
     * 在package对应的路径下找到所有的class
     * recursive 是否循环搜索子包
     */
    private static void findClassesByFile(String packageName, String filePath, final boolean recursive,
                                          List<Class<?>> classes) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles((file) -> {
            boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
            boolean acceptClass = file.getName().endsWith("class");// 接受class文件
            return acceptDir || acceptClass;
        });
        if (dirFiles == null) {
            return;
        }
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassesByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (Exception e) {
                    throw UnifiedException.gen("findClassesByFile failed");
                }
            }
        }
    }

    /**
     * 扫描包路径下的所有class文件
     */
    private static void findClassesByJar(String pkgName, JarFile jar, List<Class<?>> classes) {
        String pkgDir = pkgName.replace(".", "/");

        Enumeration<JarEntry> entry = jar.entries();
        JarEntry jarEntry;
        while (entry.hasMoreElements()) {
            jarEntry = entry.nextElement();

            String name = jarEntry.getName();
            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
                // 非指定包路径， 非class文件
                continue;
            }

            // 去掉后面的".class", 将路径转为package格式
            String className = name.substring(0, name.length() - 6);
            try {
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(pkgName + "." + className));
            } catch (Exception e) {
                throw UnifiedException.gen("findClassesByJar failed");
            }
        }
    }

}
