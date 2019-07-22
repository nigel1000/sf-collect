package collect.util;

import com.common.collect.util.AlgorithmUtil;
import com.common.collect.util.StringUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.common.collect.util.AlgorithmUtil.signature;

/**
 * Created by nijianfeng on 2019/7/21.
 */
public class AlgorithmUtilTest {

    public static void main(String[] args) {
        String content = "22dafsd中国232";
        String key = "afsefsrgrwg11111";

        // 散列算法
        System.out.println("##########hash###########");
        for (AlgorithmUtil.HashAlgorithm algorithm : AlgorithmUtil.HashAlgorithm.values()) {
            System.out.println(StringUtil.format("algorithm:{},hash:{}", algorithm.getName(), signature(content.getBytes(StandardCharsets.UTF_8), algorithm)));
        }

        // 对称加密算法

        // des
        System.out.println("##########des###########");
        System.out.println(StringUtil.format("des 加密数据:{}", content));
        byte[] desEncrypt = AlgorithmUtil.desEncrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        // 入库
        String db = AlgorithmUtil.base64Encode(desEncrypt);
        System.out.println(StringUtil.format("des 进行入库:{}", db));

        byte[] desDecrypt = AlgorithmUtil.desDecrypt(AlgorithmUtil.base64Decode(db), key.getBytes(StandardCharsets.UTF_8));
        System.out.println(StringUtil.format("des 解密数据:{}", new String(desDecrypt, StandardCharsets.UTF_8)));

        // aes
        System.out.println("##########aes###########");
        System.out.println(StringUtil.format("aes 加密数据:{}", content));
        byte[] aesEncrypt = AlgorithmUtil.aesEncrypt(content.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        // 入库
        db = AlgorithmUtil.byteToHexStr(aesEncrypt);
        System.out.println(StringUtil.format("aes 进行入库:{}", db));

        byte[] aesDecrypt = AlgorithmUtil.aesDecrypt(AlgorithmUtil.byteFromHexStr(db), key.getBytes(StandardCharsets.UTF_8));
        System.out.println(StringUtil.format("aes 解密数据:{}", new String(aesDecrypt, StandardCharsets.UTF_8)));

        System.out.println("##########url###########");
        String url = "name=sdff{王小帅}\"";
        System.out.println(StringUtil.format("url 加密数据:{}", url));
        System.out.println(StringUtil.format("url 加密结果:{}", AlgorithmUtil.uRLEncoderUtf8(url)));
        System.out.println(StringUtil.format("url 解密数据:{}", AlgorithmUtil.uRLDecoderUtf8(url)));

        // rsa
        System.out.println("##########rsa###########");
        List<byte[]> keyPair = AlgorithmUtil.rsaKeyGenerate();
        String pubKey = AlgorithmUtil.base64Encode(keyPair.get(0));
        String priKey = AlgorithmUtil.base64Encode(keyPair.get(1));
        System.out.println(StringUtil.format("rsa public key:{}", pubKey));
        System.out.println(StringUtil.format(""));
        System.out.println(StringUtil.format("rsa private key:{}", priKey));

        System.out.println("##########des###########");
        System.out.println(StringUtil.format("rsa 加密数据:{}", content));
        byte[] rsaEncrypt = AlgorithmUtil.rsaEncrypt(content.getBytes(StandardCharsets.UTF_8), AlgorithmUtil.base64Decode(pubKey));
        // 入库
        db = AlgorithmUtil.base64Encode(rsaEncrypt);
        System.out.println(StringUtil.format("rsa 进行入库:{}", db));

        byte[] rsaDecrypt = AlgorithmUtil.rsaDecrypt(AlgorithmUtil.base64Decode(db), AlgorithmUtil.base64Decode(priKey));
        System.out.println(StringUtil.format("rsa 解密数据:{}", new String(rsaDecrypt, StandardCharsets.UTF_8)));

    }


}
