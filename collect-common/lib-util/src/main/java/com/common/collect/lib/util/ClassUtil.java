package com.common.collect.lib.util;

import com.common.collect.lib.api.excps.UnifiedException;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by nijianfeng on 2019/5/19.
 */
public class ClassUtil {

    public static void changeAnnotationField(Annotation annotation, String fieldName, Object value) {
        if (annotation == null || EmptyUtil.isEmpty(fieldName) || value == null) {
            return;
        }
        try {
            // 获取InvocationHandler
            InvocationHandler h = Proxy.getInvocationHandler(annotation);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field hField = h.getClass().getDeclaredField("memberValues");
            //  打开权限
            hField.setAccessible(true);
            @SuppressWarnings("rawtypes")
            Map memberValues = (Map) hField.get(h);
            // 修改属性值
            memberValues.put(fieldName, value);
        } catch (Exception ex) {
            throw UnifiedException.gen("修改 annotation 失败", ex);
        }
    }

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

    public static Map<Integer, Class> getSuperClassGenericTypeMap(@NonNull Class clazz) {
        Map<Integer, Class> classMap = new HashMap<>();
        Type type = clazz.getGenericSuperclass();
        if (type instanceof Class) {
            return classMap;
        }
        ParameterizedType parameterizedType = ((ParameterizedType) type);
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < actualTypeArguments.length; i++) {
            if (actualTypeArguments[i] instanceof Class) {
                classMap.put(i, (Class) actualTypeArguments[i]);
            } else if (actualTypeArguments[i] instanceof ParameterizedType) {
                classMap.put(i, (Class) ((ParameterizedType) actualTypeArguments[i]).getRawType());
            }
        }
        return classMap;
    }

    public static Map<String, List<Class>> getSuperInterfaceGenericTypeMap(@NonNull Class clazz) {
        Map<String, List<Class>> interfaceClassMap = new HashMap<>();
        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof Class) {
                continue;
            }
            ParameterizedType parameterizedType = ((ParameterizedType) type);
            Class in = (Class) parameterizedType.getRawType();
            List<Class> classes = new ArrayList<>();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                if (actualTypeArguments[i] instanceof Class) {
                    classes.add((Class) actualTypeArguments[i]);
                } else if (actualTypeArguments[i] instanceof ParameterizedType) {
                    classes.add((Class) ((ParameterizedType) actualTypeArguments[i]).getRawType());
                }
            }
            if (EmptyUtil.isNotEmpty(classes)) {
                interfaceClassMap.put(in.getName(), classes);
            }
        }
        return interfaceClassMap;
    }

    // K -> String V -> Long
    public static Map<String, Type> getParameterizedTypeMap(@NonNull ParameterizedType parameterizedType) {
        Map<String, Type> parameterTypeMap = new HashMap<>();
        Type[] actualTypes = parameterizedType.getActualTypeArguments();
        TypeVariable[] typeVariables = ((Class) parameterizedType.getRawType()).getTypeParameters();
        for (int j = 0; j < actualTypes.length; j++) {
            parameterTypeMap.put(typeVariables[j].getTypeName(), actualTypes[j]);
        }
        return parameterTypeMap;
    }

    public static Map<String, Type> getMethodReturnGenericTypeMap(@NonNull Method method) {
        Type returnType = method.getGenericReturnType();
        Map<String, Type> returnTypeMap = new HashMap<>();
        if (returnType instanceof ParameterizedType) {
            returnTypeMap.putAll(getParameterizedTypeMap((ParameterizedType) returnType));
        }
        return returnTypeMap;
    }

    public static Map<String, Type> getMethodParameterGenericTypeMap(@NonNull Method method, int index) {
        Type[] parameterTypes = method.getGenericParameterTypes();
        Map<String, Type> parameterTypeMap = new HashMap<>();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (index != i) {
                continue;
            }
            Type parameterType = parameterTypes[i];
            if (parameterType instanceof ParameterizedType) {
                parameterTypeMap.putAll(getParameterizedTypeMap((ParameterizedType) parameterType));
            }
        }
        return parameterTypeMap;
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
        return CollectionUtil.distinct(clazzList);
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
            boolean acceptClass = file.getName().endsWith(".class");// 接受class文件
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
                classes.add(getClass(packageName + "." + className));
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
            classes.add(getClass(name.replace("/", ".").substring(0, name.length() - 6)));
        }
    }

}
