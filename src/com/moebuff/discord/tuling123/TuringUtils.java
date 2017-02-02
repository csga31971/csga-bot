package com.moebuff.discord.tuling123;


import com.moebuff.discord.io.IOKit;
import com.moebuff.discord.utils.URLUtils;
import com.moebuff.discord.utils.UnhandledException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * 图灵工具
 *
 * @author 图灵机器人
 * @author muto
 */
public class TuringUtils {
    /**
     * 向后台发送 post 请求
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return 请求结果
     */
    public static String post(String url, String param) {
        URL realUrl = URLUtils.create(url);
        HttpURLConnection conn = URLUtils.openConnection(realUrl);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(50000);
        conn.setReadTimeout(50000);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "token");
        conn.setRequestProperty("tag", "htc_new");
        try {
            conn.setRequestMethod("POST");
            conn.connect();

            IOKit.write(param, conn.getOutputStream());
            return IOKit.toString(conn.getInputStream());
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }

    private static class AESAlg {
        private Key key;
        private IvParameterSpec iv;//AES CBC 模式使用的 Initialization Vector
        private Cipher cipher;//Cipher 物件

        AESAlg(String key) {
            this.key = new SecretKeySpec(DigestUtils.md5(key), "AES");
            this.iv = new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0});
            try {
                this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            } catch (GeneralSecurityException e) {
                throw new UnhandledException(e);
            }
        }

        /**
         * 加密方法
         * <p>
         * 说明：采用128位
         *
         * @return 加密结果
         */
        String encrypt(String data) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return Base64.encodeBase64String(data.getBytes(StandardCharsets.UTF_8));
            } catch (GeneralSecurityException e) {
                throw new UnhandledException(e);
            }
        }
    }

    /**
     * AES 加密
     *
     * @param key  密钥
     * @param data 待加密的数据
     * @return 加密结果
     */
    public static String aes(String key, String data) {
        return new AESAlg(key).encrypt(data);
    }
}
