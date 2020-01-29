package collect.util;

import com.common.collect.util.AlgorithmUtil;
import com.common.collect.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by nijianfeng on 2019/7/21.
 */

@Slf4j
public class AlgorithmUtilTest {

    public static void hash(String content) {
        log.info("########## 散列算法 ###########");
        for (AlgorithmUtil.HashAlgorithm algorithm : AlgorithmUtil.HashAlgorithm.values()) {
            log.info(StringUtil.format("algorithm:{},hash:{}", algorithm.getName(), AlgorithmUtil.signature(content.getBytes(StandardCharsets.UTF_8), algorithm)));
        }
    }

    public static void des(String key, String content) {
        log.info("########## des 对称加密算法 ###########");
        log.info(StringUtil.format("des 加密数据:{}", content));
        // 加密
        byte[] desEncrypt = AlgorithmUtil.desEncrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        // 转成 string
        String db = AlgorithmUtil.base64Encode(desEncrypt);
        log.info(StringUtil.format("des 加密后 byte2string:{}", db));
        // 解密
        byte[] desDecrypt = AlgorithmUtil.desDecrypt(AlgorithmUtil.base64Decode(db), key.getBytes(StandardCharsets.UTF_8));
        log.info(StringUtil.format("des 解密数据:{}", new String(desDecrypt, StandardCharsets.UTF_8)));
    }

    public static void aes(String key, String content) {
        log.info("########## aes 对称加密算法 ###########");
        log.info(StringUtil.format("aes 加密数据:{}", content));
        // 加密
        byte[] aesEncrypt = AlgorithmUtil.aesEncrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        // 转成 string
        String db = AlgorithmUtil.byteToHexStr(aesEncrypt);
        log.info(StringUtil.format("aes 加密后 byte2string:{}", db));
        // 解密
        byte[] aesDecrypt = AlgorithmUtil.aesDecrypt(AlgorithmUtil.byteFromHexStr(db), key.getBytes(StandardCharsets.UTF_8));
        log.info(StringUtil.format("aes 解密数据:{}", new String(aesDecrypt, StandardCharsets.UTF_8)));
    }

    public static void rsa(byte[] pub, byte[] pri, String content) {
        // rsa
        log.info("########## rsa 非对称加密算法 ###########");
        log.info(StringUtil.format("rsa 加密数据:{}", content));
        String pubKey = AlgorithmUtil.base64Encode(pub);
        String priKey = AlgorithmUtil.base64Encode(pri);
        log.info(StringUtil.format("rsa public key:{}", pubKey));
        log.info(StringUtil.format("rsa private key:{}", priKey));
        // 加密
        byte[] rsaEncrypt = AlgorithmUtil.rsaEncrypt(content.getBytes(StandardCharsets.UTF_8), AlgorithmUtil.base64Decode(pubKey));
        // 转成 string
        String db = AlgorithmUtil.base64Encode(rsaEncrypt);
        log.info(StringUtil.format("rsa 加密后 byte2string:{}", db));
        // 解密
        byte[] rsaDecrypt = AlgorithmUtil.rsaDecrypt(AlgorithmUtil.base64Decode(db), AlgorithmUtil.base64Decode(priKey));
        log.info(StringUtil.format("rsa 解密数据:{}", new String(rsaDecrypt, StandardCharsets.UTF_8)));
    }

    public static void urlEncode(String content) {
        log.info("");
        log.info("########## url encode ###########");
        log.info(StringUtil.format("url 加密数据:{}", content));
        String afterEncode = AlgorithmUtil.uRLEncoderUtf8(content);
        log.info(StringUtil.format("url 加密结果:{}", afterEncode));
        log.info(StringUtil.format("url 解密数据:{}", AlgorithmUtil.uRLDecoderUtf8(afterEncode)));
    }

    public static void hexStr(String content) {
        log.info("");
        log.info("########## url hexStr ###########");
        String hexStr = AlgorithmUtil.byteToHexStr(content.getBytes());
        log.info(StringUtil.format("url 原始数据:{}", content));
        log.info(StringUtil.format("url 原始数据2HexStr:{}", hexStr));
        log.info(StringUtil.format("url HexStr2原始数据:{}", new String(AlgorithmUtil.byteFromHexStr(hexStr))));
    }


    public static void main(String[] args) {
        String content = "22dafsd中国232";
        String key = "afsefsrgrwg11111";
        String url = "http://aa.com?name=sdff{王小帅}\"&age=20";

        hash(content);

        des(key, content);

        aes(key, content);

        List<byte[]> keyPair = AlgorithmUtil.rsaKeyGenerate();
        rsa(keyPair.get(0), keyPair.get(1), content);

        log.info("");
        urlEncode(url);

        hexStr(content);

        importantAesEn();
        importantAesDe();
    }

    public static void importantAesEn() {
        String content = "afsefsrgrwg11111";
        String key = "afsefsrgrwg11111";
        System.out.println(AlgorithmUtil.byteToHexStr(AlgorithmUtil.aesEncrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8))));
    }

    public static void importantAesDe() {
        String content = "6a0d1038bdebdc3e5c209fb1bce6bc89f6c8095153b3ddcf26c538ea1ec1e2b0";
        String key = "afsefsrgrwg11111";
        // 解密
        byte[] aesDecrypt = AlgorithmUtil.aesDecrypt(AlgorithmUtil.byteFromHexStr(content), key.getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(aesDecrypt, StandardCharsets.UTF_8));
    }

}
