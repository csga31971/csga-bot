package com.moebuff.discord.utils;

import com.google.gson.*;

import java.io.*;
import java.net.URL;

/**
 * Created by Administrator on 2017/10/23.
 */
public class OSUIDManager {

    private static URL url = OSUIDManager.class.getResource("../../../../../../resources/main/user_osu_id.json");

    private static File user_osu_id = new File(url.getPath().replaceAll("%20"," "));
    //private static BufferedReader bf = null;
    //private static FileInputStream reader = null;
    //private static FileOutputStream writer = null;
    private static JsonParser parser = new JsonParser();
    private static String content = "";

    /*static {
        System.out.println(url.getPath());
        try{
            //bf = new BufferedReader(new InputStreamReader(new FileInputStream(user_osu_id)));
            reader = new FileInputStream(user_osu_id);
            writer = new FileOutputStream(user_osu_id);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }*/
    public static String get(String name) throws IOException {

        String line;
        content = "";
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(user_osu_id)));

        while((line = bf.readLine()) != null){
            content += line;
        }
        bf.close();
        System.out.println("get content:" + content);
        if("".equals(content) || content == null){
            return "";
        }

        JsonElement json = parser.parse(content);
        JsonObject obj = json.getAsJsonObject();

        return obj.get(name)!=null?obj.get(name).getAsString():"";
    }

    public static void put(String key, String value) throws IOException {

        String line;
        //byte[] b;
        JsonObject json;
        String added;
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(user_osu_id)));
        FileWriter writer = new FileWriter(user_osu_id);

        System.out.println("before put content:" + content);
        while((line = bf.readLine()) != null){
            content += line;
        }
        bf.close();
        if(content == null || "".equals(content)){
            json = new JsonObject();
            json.addProperty(key, value);
            added = json.toString();
            //b = added.getBytes();
            writer.write(added);
            writer.flush();
            return;
        }

        JsonElement jsonElement = parser.parse(content);
        JsonObject obj = jsonElement.getAsJsonObject();
        obj.addProperty(key,value);
        added = obj.toString();
        System.out.println("put added:" + added);
        //b = added.getBytes();
        writer.write(added);
        writer.flush();
        writer.close();
    }
}
