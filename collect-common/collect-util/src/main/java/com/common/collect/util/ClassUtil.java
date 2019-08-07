package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by nijianfeng on 2019/5/19.
 */
public class ClassUtil {

    public static Class<?> getClass(String clazz) {
        try {
            return Class.forName(clazz);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format(" {} 无法找到类定义", clazz), e);
        }
    }

    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T)clazz.newInstance();
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format(" {} 无法初始化", clazz.getName()), e);
        }
    }

    public static <T> T newInstance(String clazz) {
        return (T) newInstance(getClass(clazz));
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        try {
            return clazz.getDeclaredMethod(methodName, args);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format("class:{},method:{},找不到", clazz.getName(), methodName), e);
        }
    }

    public static <T> T invoke(Object target, Method method, Object... args) {
        try {
            return (T) method.invoke(target, args);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format(" class:{},method:{} 调用方法失败", target.getClass().getName(), method.getName()), e);
        }
    }

    public static Field getField(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format(" class:{},field:{} 获取属性值失败", clazz.getName(), name), e);
        }
    }

    public static <T> T getFieldValue(Object target, String name) {
        try {
            return (T) getField(target.getClass(), name).get(target);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format(" class:{},field:{} 获取属性值失败", target.getClass().getName(), name), e);
        }
    }

    public static void setFieldValue(Object target, String name, Object value) {
        try {
            Field field = getField(target.getClass(), name);
            field.set(target, value);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format(" class:{},field:{} 设置属性值失败", target.getClass().getName(), name), e);
        }
    }

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

    // 获取泛型类型
    public static Class getGenericType(Class clazz, Type genType, int index) {
        if (!(genType instanceof ParameterizedType)) {
            throw UnifiedException.gen(StringUtil.format(" class:{},index:{} 的父类没有泛型", clazz.getName(), index));
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    throw UnifiedException.gen(StringUtil.format(" class:{},index:{} 的父类没有设置具体的类型", clazz.getName(), index));
                } else {
                    return (Class) params[index];
                }
            } else {
                throw UnifiedException.gen(StringUtil.format(" class:{},index:{} 的父类与 index 不匹配", clazz.getName(), index));
            }
        }
    }

    public static Class getSuperClassGenericType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();
        return getGenericType(clazz, genType, index);
    }

    public static List<Class> getSuperInterfaceGenericType(Class clazz, int index) {
        Type[] genTypes = clazz.getGenericInterfaces();
        List<Class> classes = new ArrayList<>();
        for (Type genType : genTypes) {
            if (genType instanceof ParameterizedType) {
                classes.add(getGenericType(clazz, genType, index));
            }
        }
        return classes;
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
            if (!dirs.hasMoreElements()) {
                // 可能是全路径不是目录
                dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName + ".class");
            }
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {// 如果是以文件的形式保存在服务器上
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");// 获取包的物理路径
                    File file = new File(filePath);
                    if (!file.isDirectory()) {
                        clazzs.add(Thread.currentThread().getContextClassLoader().loadClass(packageName));
                    } else {
                        findClassesByFile(packageName, filePath, true, clazzs);
                    }
                } else if ("jar".equals(protocol)) {// 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    findClassesByJar(packageName, jar, clazzs);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw UnifiedException.gen("getClazzFromPackage failed", e);
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
                } catch (ClassNotFoundException e) {
                    throw UnifiedException.gen("findClassesByFile failed", e);
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
            } catch (ClassNotFoundException e) {
                throw UnifiedException.gen("findClassesByJar failed", e);
            }
        }
    }

}
