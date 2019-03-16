package com.common.collect.container;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;

import java.io.*;
import java.util.Objects;

/**
 * Created by nijianfeng on 2019/3/16.
 */
public class SerializeUtil {

    public static void hessianSerialize(Object obj, OutputStream outputStream) throws IOException {
        Objects.requireNonNull(obj);
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.writeObject(obj);
        hessian2Output.close();
    }


    @SuppressWarnings("unchecked")
    public static <T> T hessianDeserialize(InputStream inputStream) throws IOException {
        Hessian2Input in = new Hessian2Input(inputStream);
        return (T) in.readObject();
    }

    public static void javaSerialize(Object object, OutputStream outputStream) throws IOException {
        // 序列化
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T javaDeserialize(InputStream inputStream) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        return (T) ois.readObject();
    }

}
