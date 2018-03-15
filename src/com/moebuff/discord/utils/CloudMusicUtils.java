package com.moebuff.discord.utils;

import com.google.gson.*;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CloudMusicUtils {
    private static String Encrypt(String sSrc, String sKey) throws Exception {
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return new BASE64Encoder().encode(encrypted);
    }

    public static String get_params(String text) throws Exception {
        String first_key = "0CoJUm6Qyw8W8jud";
        String second_key = "FFFFFFFFFFFFFFFF";
        String h_encText = Encrypt(text, first_key);
        h_encText = Encrypt(h_encText, second_key);
        h_encText = URLEncoder.encode(h_encText,"utf-8");
        return h_encText;
    }

    public static String get_encSecKey() {
        String encSecKey = "257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffca5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";
        return encSecKey;
    }

    private static String mapToJsonStr(Map map){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        return gson.toJson(map);
    }

    public static JsonArray searchSong(String name){

        try {
            Map map = new HashMap();
            map.put("s",name);
            map.put("type",1);
            String p = mapToJsonStr(map);
            String params = "params=" + get_params(p) + "&encSecKey=" + get_encSecKey();
            URL url = new URL("http://music.163.com/weapi/cloudsearch/get/web?csrf_token=");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            int i;
            String result = "{";
            while((i=reader.read())!=-1){
                result+=reader.readLine();
            }

            JsonElement jsonElement = new JsonParser().parse(result);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray songs = jsonObject.get("result").getAsJsonObject().get("songs").getAsJsonArray();

            return songs;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMp3Link(int id) {
        Map map = new HashMap();
        String[] s = {String.valueOf(id)};
        map.put("ids",s);
        map.put("br",128000);
        map.put("csrf_token","");
        String p = mapToJsonStr(map);
        String params = null;
        try {
            params = "params=" + get_params(p) + "&encSecKey=" + get_encSecKey();
            URL url = new URL("http://music.163.com/weapi/song/enhance/player/url");
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            int i;
            String result = "{";
            while((i=reader.read())!=-1){
                result+=reader.readLine();
            }
            Log.getLogger().info(result);
            return new JsonParser().parse(result).getAsJsonObject().
                    get("data").getAsJsonArray().
                    get(0).getAsJsonObject().
                    get("url").getAsString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
