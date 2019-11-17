package com.wuqibo.bppcallbackservice.btoolkit;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtils {

    private static final String TAG = "=========" + RSAUtils.class.getSimpleName();

    private static final String SIGN_TYPE_RSA = "RSA";
    private static final String SIGN_TYPE_RSA2 = "RSA2";
    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 生成签名
     *
     * @param content    "a=1&b=2&c=3"
     * @param privateKey 私钥
     * @param signType   "RSA/RSA2"
     * @param charset    "UTF-8"
     * @return sign
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String sign(String content, String privateKey, String signType, String charset) {
        PrivateKey priKey = null;
        java.security.Signature signature = null;
        try {
            if (SIGN_TYPE_RSA.equals(signType)) {
                priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));
                signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            } else if (SIGN_TYPE_RSA2.equals(signType)) {
                priKey = getPrivateKeyFromPKCS8(SIGN_TYPE_RSA, new ByteArrayInputStream(privateKey.getBytes()));
                signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);
            } else {
                Log.e(TAG, "不支持该签名类型 : : signType=" + signType);
            }
            signature.initSign(priKey);
            if (isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            byte[] signed = signature.sign();
            return new String(encodeBase64(signed));
        } catch (Exception e) {
            Log.e(TAG, e + " Cause:" + e.getCause());
        }
        return null;
    }

    /**
     * 验签方法
     *
     * @param content   "a=1&b=2&c=3"
     * @param sign      签名时生成的签名串
     * @param publicKey 公钥
     * @param signType  "RSA/RSA2"
     * @param charset   "UTF-8"
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean verify(String content, String sign, String publicKey, String signType, String charset) {
        try {
            java.security.Signature signature = null;
            PublicKey pubKey = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
            if (SIGN_TYPE_RSA.equals(signType)) {
                signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
            } else if (SIGN_TYPE_RSA2.equals(signType)) {
                signature = java.security.Signature.getInstance(SIGN_SHA256RSA_ALGORITHMS);
            } else {
                throw new Exception("不是支持的签名类型 : signType=" + signType);
            }
            signature.initVerify(pubKey);
            if (isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            return signature.verify(decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            Log.e(TAG, "公钥私钥不匹配>>>>>" + e + "|Cause:" + e.getCause());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins) throws Exception {
        if (ins == null || isEmpty(algorithm)) {
            return null;
        }
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] encodedKey = readText(ins).getBytes();
        encodedKey = decodeBase64(encodedKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        io(new InputStreamReader(ins), writer, -1);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = decodeBase64(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }

    private static String readText(InputStream ins) throws IOException {
        Reader reader = new InputStreamReader(ins);
        StringWriter writer = new StringWriter();
        io(reader, writer, -1);
        return writer.toString();
    }

    private static void io(Reader in, Writer out, int bufferSize) throws IOException {
        if (bufferSize == -1) {
            bufferSize = DEFAULT_BUFFER_SIZE >> 1;
        }
        char[] buffer = new char[bufferSize];
        int amount;
        while ((amount = in.read(buffer)) >= 0) {
            out.write(buffer, 0, amount);
        }
    }

    private static boolean isEmpty(String content) {
        return content == null || content.length() == 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static byte[] decodeBase64(byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }

}
