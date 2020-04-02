package com.common.collect.lib.util;

import com.common.collect.lib.api.ApiConstants;
import com.common.collect.lib.api.excps.UnifiedException;
import lombok.Getter;
import lombok.NonNull;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nijianfeng on 2019/7/21.
 */
public class AlgorithmUtil {

    public enum HashAlgorithm {
        MD2("MD2"),
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512"),
        ;

        @Getter
        private String name;

        HashAlgorithm(String name) {
            this.name = name;
        }

    }

    // 散列算法
    public static String signature(@NonNull byte[] content, @NonNull HashAlgorithm algorithm) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm.getName());
        } catch (NoSuchAlgorithmException e) {
            throw UnifiedException.gen(StringUtil.format("hash:{} 加密失败", algorithm), e);
        }
        // content 散列处理
        byte[] bytes = messageDigest.digest(content);
        return byteToHexStr(bytes);
    }

    // 将字节数组转换为16进制字符串的形式.
    public static String byteToHexStr(byte[] bytes) {
        StringBuffer s = new StringBuffer(bytes.length * 2);

        for (byte b : bytes) {
            s.append(ApiConstants.HEX_LOOKUP_STRING[(b >>> 4) & 0x0f]);
            s.append(ApiConstants.HEX_LOOKUP_STRING[b & 0x0f]);
        }

        return s.toString();
    }

    // 将16进制字符串还原为字节数组.
    public static byte[] byteFromHexStr(String content) {
        byte[] bytes;

        bytes = new byte[content.length() / 2];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(content.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

    public static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encodeBuffer(bytes);
    }

    public static byte[] base64Decode(String content) {
        try {
            return new BASE64Decoder().decodeBuffer(content);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("base64Decode 失败"));
        }
    }

    // 对称加密 des
    public static byte[] desEncrypt(byte[] data, byte[] key) {
        String algorithm = "DES";
        try {
            // 生成一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance(algorithm);

            // 用密钥初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("des 加密失败"), ex);
        }

    }

    // 对称解密 des
    // key需要满足 AESConstants 16 24 32 不能有中文字符
    public static byte[] desDecrypt(@NonNull byte[] data, byte[] key) {
        String algorithm = "DES";
        try {
            // 生成一个可信任的随机数源
            SecureRandom sr = new SecureRandom();
            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            SecretKey secretKey = keyFactory.generateSecret(dks);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(algorithm);
            // 用密钥初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("des 解密失败"), ex);
        }
    }

    // 对称加密 aes
    public static byte[] aesEncrypt(byte[] data, byte[] key) {
        try {
            String algorithm = "AES";
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("aes 加密失败"), ex);
        }
    }

    // 对称解密 aes
    public static byte[] aesDecrypt(byte[] data, byte[] key) {
        try {
            String algorithm = "AES";
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("aes 解密失败"), ex);
        }
    }

    public static List<byte[]> rsaKeyGenerate() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            List<byte[]> keys = new ArrayList<>();
            keys.add(publicKey.getEncoded());
            keys.add(privateKey.getEncoded());
            return keys;
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("rsa key 生成失败"), ex);
        }
    }

    // 非对称加密 rsa
    public static byte[] rsaEncrypt(byte[] data, byte[] pubKey) {
        try {
            String algorithm = "RSA";

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            PublicKey key = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(1, key);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("rsa 加密失败"), ex);
        }
    }

    // 非对称解密 rsa
    public static byte[] rsaDecrypt(byte[] data, byte[] priKey) {
        try {
            String algorithm = "RSA";

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priKey);
            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
            PrivateKey key = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(2, key);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw UnifiedException.gen(StringUtil.format("rsa 解密失败"), ex);
        }
    }

    public static String uRLDecoderUtf8(String content) {
        return uRLDecoder(content, StandardCharsets.UTF_8.name());
    }

    public static String uRLDecoder(String content, @NonNull String encode) {
        if (EmptyUtil.isEmpty(content)) {
            return "";
        }
        try {
            return URLDecoder.decode(content, encode);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format("url 加密失败"), e);
        }
    }

    public static String uRLEncoderUtf8(String content) {
        return uRLEncoder(content, StandardCharsets.UTF_8.name());
    }

    public static String uRLEncoder(String content, @NonNull String encode) {
        if (EmptyUtil.isEmpty(content)) {
            return "";
        }
        try {
            return URLEncoder.encode(content, encode);
        } catch (Exception e) {
            throw UnifiedException.gen(StringUtil.format("url 解密失败"), e);
        }
    }

}
