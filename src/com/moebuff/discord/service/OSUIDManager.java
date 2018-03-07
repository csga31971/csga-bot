package com.moebuff.discord.service;

import com.google.gson.*;
import com.moebuff.discord.dao.User_osu_id_DAO;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/23.
 */
public class OSUIDManager {

    /*
    private static URL url = OSUIDManager.class.getResource("user_osu_id.json");
    private static File user_osu_id = new File(url.getPath().replaceAll("%20"," "));
    private static JsonParser parser = new JsonParser();
    private static String content = "";
    */

    private static User_osu_id_DAO user_osu_id_dao;
    private static SqlSession session;
    public static String get(String userid){
        session = MybatisUtil.getSession();
        user_osu_id_dao = session.getMapper(User_osu_id_DAO.class);
        return user_osu_id_dao.getosuid(userid);
    }

    public static void put(String userid, String osuid){
        session = MybatisUtil.getSession();
        user_osu_id_dao = session.getMapper(User_osu_id_DAO.class);
        Map<String, String> map = new HashMap<String, String>();
        map.put("userid",userid);
        map.put("osuid",osuid);
        user_osu_id_dao.setosuid(map);
        session.commit();
        session.close();
    }


    /*
    原生jdbc，8888888888888888
    public static String get(String userid){
        return JDBCUtil.queryid(userid);
    }

    public static boolean put(String userid, String osuid){
        return JDBCUtil.addid(userid,osuid);
    }
    */

    /*
    json存储，太弱智了
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
    }*/
}
