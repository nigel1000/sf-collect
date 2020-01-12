package com.common.collect.util;

import com.common.collect.api.excps.UnifiedException;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by nijianfeng on 2019/5/19.
 */
public class ClassUtil {

    public static Class<?> getClass(String clazz) {
        return ExceptionUtil.reThrowException(() -> Class.forName(clazz),
                StringUtil.format(" {} 无法找到类定义", clazz));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        return ExceptionUtil.reThrowException(() -> (T) clazz.newInstance(),
                StringUtil.format(" {} 无法初始化", clazz.getName()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String clazz) {
        return (T) newInstance(getClass(clazz));
    }


    public static Method[] getMethods(Class<?> clazz) {
        return ExceptionUtil.reThrowException(() -> clazz.getDeclaredMethods(),
                StringUtil.format("class:{},获取方法失败", clazz.getName()));
    }


    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
        return ExceptionUtil.reThrowException(() -> clazz.getDeclaredMethod(methodName, args),
                StringUtil.format("class:{},method:{},获取方法失败", clazz.getName(), methodName));
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object target, Method method, Object... args) {
        return ExceptionUtil.reThrowException(() -> (T) method.invoke(target, args),
                StringUtil.format(" class:{},method:{} 调用方法失败", target.getClass().getName(), method.getName()));
    }

    public static Field[] getFields(Class<?> clazz) {
        return ExceptionUtil.reThrowException(() -> {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                    }
                    return fields;
                },
                StringUtil.format(" class:{} 获取属性值失败", clazz.getName()));
    }

    public static Field getField(Class<?> clazz, String name) {
        return ExceptionUtil.reThrowException(() -> {
                    Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    return field;
                },
                StringUtil.format(" class:{},field:{} 获取属性值失败", clazz.getName(), name));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object target, String name) {
        return ExceptionUtil.reThrowException(() -> (T) getField(target.getClass(), name).get(target),
                StringUtil.format(" class:{},field:{} 获取属性值失败", target.getClass().getName(), name));
    }

    public static void setFieldValue(Object target, String name, Object value) {
        ExceptionUtil.reThrowException(() -> {
                    Field field = getField(target.getClass(), name);
                    field.set(target, value);
                },
                StringUtil.format(" class:{},field:{} 设置属性值失败", target.getClass().getName(), name));
    }

    public static boolean isPrimitive(Class cls) {
        boolean isPrimitive = cls.isPrimitive();
        if (isPrimitive) {
            return true;
        }
        if (cls == Long.class ||
                cls == Integer.class ||
                cls == Byte.class ||
                cls == Short.class ||
                cls == Float.class ||
                cls == Double.class ||
                cls == Character.class ||
                cls == Boolean.class) {
            return true;
        }
        return false;
    }

    public static Object returnBaseDataType(Class returnType) {
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
    public static Class getGenericType(@NonNull Type type, int index) {
        if (!(type instanceof ParameterizedType)) {
            throw UnifiedException.gen(StringUtil.format(" type:{} 不是泛型类型", type.getTypeName()));
        } else {
            Type[] params = ((ParameterizedType) type).getActualTypeArguments();
            if (index < params.length && index >= 0) {
                if (!(params[index] instanceof Class)) {
                    throw UnifiedException.gen(StringUtil.format(" type:{},index:{} 没有设置具体的类型", type.getTypeName(), index));
                } else {
                    return (Class) params[index];
                }
            } else {
                throw UnifiedException.gen(StringUtil.format(" type:{},index:{} 超出了下标", type.getTypeName(), index));
            }
        }
    }

    public static Class getSuperClassGenericType(@NonNull Class clazz, int index) {
        Type type = clazz.getGenericSuperclass();
        return getGenericType(type, index);
    }

    public static Class getSuperInterfaceGenericType(@NonNull Class clazz, @NonNull Class superInterface, int index) {
        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == superInterface) {
                return getGenericType(type, index);
            }
        }
        throw UnifiedException.gen(StringUtil.format(" clazz:{} 不存在接口 interface:{} 或者 此接口不是泛型接口 ", clazz.getName(), superInterface.getName()));
    }

    public static Class getFieldGenericType(@NonNull Field field, int index) {
        Type type = field.getGenericType();
        return getGenericType(type, index);
    }


    // K -> String V -> Long
    public static Map<String, Type> getMethodReturnGenericType(@NonNull Method method) {
        Type returnType = method.getGenericReturnType();
        Map<String, Type> returnTypeMap = new HashMap<>();
        if (returnType instanceof ParameterizedType) {
            // 获取返回值泛型参数类型 数组 --- 如： Map<K,V>
            Type[] actualTypes = ((ParameterizedType) returnType).getActualTypeArguments();
            TypeVariable[] typeVariables = method.getReturnType().getTypeParameters();
            for (int i = 0; i < actualTypes.length; i++) {
                returnTypeMap.put(typeVariables[i].getTypeName(), actualTypes[i]);
            }
        }
        return returnTypeMap;
    }


    /**
     * 获得包下面的所有的class
     * List包含所有class的实例
     */
    public static List<Class<?>> getClazzFromPackage(@NonNull String packageName) {
        List<Class<?>> clazzList = new ArrayList<>();
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
                        clazzList.add(Thread.currentThread().getContextClassLoader().loadClass(packageName));
                    } else {
                        findClassesByFile(packageName, filePath, true, clazzList);
                    }
                } else if ("jar".equals(protocol)) {// 如果是jar包文件
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    findClassesByJar(packageName, jar, clazzList);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw UnifiedException.gen("getClazzFromPackage failed", e);
        }
        return CollectionUtil.removeDuplicate(clazzList);
    }

    /**
     * 在package对应的路径下找到所有的class
     * recursive 是否循环搜索子包
     */
    private static void findClassesByFile(@NonNull String packageName, @NonNull String filePath, final boolean recursive,
                                          @NonNull List<Class<?>> classes) {
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
                    throw UnifiedException.gen(StringUtil.format("findClassesByFile failed. packageName:{},className:{}", packageName, className), e);
                }
            }
        }
    }

    /**
     * 扫描包路径下的所有class文件
     */
    private static void findClassesByJar(@NonNull String packageName, @NonNull JarFile jar, @NonNull List<Class<?>> classes) {
        String pkgDir = packageName.replace(".", "/");

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
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
            } catch (ClassNotFoundException e) {
                throw UnifiedException.gen(StringUtil.format("findClassesByJar failed. packageName:{},className:{}", packageName, className), e);
            }
        }
    }

}
