package com.common.collect.container;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by nijianfeng on 2019/3/16.
 */
public class SerializeUtil {

    public static byte[] hessianSerialize(Object obj) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        Hessian2Output hessian2Output = new Hessian2Output(outputStream);
        hessian2Output.writeObject(obj);
        hessian2Output.close();
        return outputStream.toByteArray();
    }


    @SuppressWarnings("unchecked")
    public static <T> T hessianDeserialize(byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Hessian2Input in = new Hessian2Input(inputStream);
        return (T) in.readObject();
    }

    public static byte[] javaSerialize(Object object) throws IOException {
        // 序列化
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(object);
        return outputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static <T> T javaDeserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(inputStream);
        return (T) ois.readObject();
    }

}
