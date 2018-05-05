package com.moebuff.discord.listener;

import com.google.gson.*;
import com.moebuff.discord.Settings;
import com.moebuff.discord.maps.Maps;
import com.moebuff.discord.utils.oppai.Koohii;
import com.moebuff.discord.entity.osu.search.BeatmapSet;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.service.OSUIDManager;
import com.moebuff.discord.utils.URLUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class OSUHandler {

    private static String RANK_A_ICON = "https://cdn.discordapp.com/emojis/365509580593299466.png";
    private static String RANK_S_ICON = "https://cdn.discordapp.com/emojis/365509580731449354.png";
    private static String RANK_SS_ICON = "https://cdn.discordapp.com/emojis/365509580622659585.png";

    static class TrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static void handle(IGuild guild, IChannel channel, IUser user, IMessage message, String[] args){
        if (args.length == 0) {
            channel.sendMessage("The command requires some additional parameters.");
            channel.sendMessage("For details, refer to the help documentation.");
            return;
        }

        boolean prompt = false;//是否需要提示
        String[] params = args.length > 1 ?
                Arrays.copyOfRange(args, 1, args.length) :
                new String[0];
        String param_with_spacebar = "";

        if(args.length>1){
            for(int i = 1;i < args.length;i++){
                param_with_spacebar += args[i];
                param_with_spacebar += " ";
            }
        }
        switch (args[0]){
            case "setid":
                try {
                    setOSUId(channel, user, param_with_spacebar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "recent":
            case "r":
                try {
                    OSURecent(channel, user, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "profile":
            case "p":
                try {
                    OSUProfile(channel, user, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "pp":
                calcPP(channel, message, params);
                break;
            case "with":
                calcPPWithMods(channel, message, params);
                break;
            case "search":
            case "s":
                searchBeatmap(channel, param_with_spacebar.trim().replace(" ","+"));
                break;
            default:
                channel.sendMessage("unknown command.");
                break;
        }
    }

    private static void setOSUId(IChannel channel, IUser user, String id) throws IOException, RateLimitException, DiscordException, MissingPermissionsException {
        if (checkOSUId(channel, user, id)) {
            OSUIDManager.put(user.getStringID(), id);
            channel.sendMessage("osu!id set:    " + user.getName() + " : " + id);
        }
    }

    private static boolean checkOSUId(IChannel channel, IUser user, String id) throws IOException, RateLimitException, DiscordException, MissingPermissionsException {
        String username = OSUIDManager.get(user.getStringID());
        //command param is null or 0 length
        if ("".equals(id) || id == null) {
            channel.sendMessage("please add your id after `%osu setid` command. (e.g. %osu setid cookiezi)");
            return false;
        }

        //already set id
        if (username != null && !"".equals(username)) {
            channel.sendMessage("you have already set your id: " + username);
            return false;
        }

        //check if username exists (from osu!api)
        URLConnection conn = URLUtils.openConnection(new URL("https://osu.ppy.sh/api/get_user?"
                + "k=" + Settings.OSU_APIKEY
                + "&u=" + id.replaceAll(" ", "+"))
        );
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("contentType", "application/json");
        conn.setRequestProperty("User-agent", Settings.URL_AGENT);

        BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String result = "";
        String line;
        while ((line = bf.readLine()) != null) {
            result += line;
        }
        if (result.length() < 4) {
            channel.sendMessage("I can't find this ID in osu");
            return false;
        }
        bf.close();
        return true;
    }

    private static void OSURecent(IChannel channel, IUser user, IMessage message) throws RateLimitException, DiscordException, MissingPermissionsException, IOException {
        String username = OSUIDManager.get(user.getStringID());
        if ("".equals(username) || username == null) {
            message.reply("set your osu!id first please!(use " + Settings.PREFIX + "osu setid [id] command");
        } else {
            URLConnection conn = URLUtils.openConnection(new URL("https://osu.ppy.sh/api/get_user_recent?"
                            + "k=" + Settings.OSU_APIKEY
                            + "&u=" + username.replaceAll(" ", "+")
                            + "&limit=" + "1"
                    )
            );
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("User-agent", Settings.URL_AGENT);

            BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = "";
            String line = "";
            while ((line = bf.readLine()) != null) {
                result += line;
            }
            bf.close();
            if (result.length() < 4) {
                channel.sendMessage("no score found in last 24h.");
                return;
            }

            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(result);
            JsonArray jsonArray = json.getAsJsonArray();
            JsonElement ele = jsonArray.get(0);
            JsonObject obj = ele.getAsJsonObject();

            String userid = obj.get("user_id").getAsString();
            String beatmap_id = obj.get("beatmap_id").getAsString();
            String score = obj.get("score").getAsString();
            /*String maxcombo = obj.get("maxcombo").getAsString();
            String count50 =  obj.get("count50").getAsString();
            String count100 =  obj.get("count100").getAsString();
            String count300 =  obj.get("count300").getAsString();
            String countmiss =  obj.get("countmiss").getAsString();*/
            String date = obj.get("date").getAsString();
            String rank = obj.get("rank").getAsString();
            String userpage = "https://osu.ppy.sh/u/" + userid;
            String beatmapLink = "https://osu.ppy.sh/b/" + beatmap_id;
            String resultMsg = "```\n" +
                    "Username: " + username + "\n" +
                    "Beatmap: " + beatmapLink + "\n" +
                    "Userpage: " + userpage + "\n" +
                    "Score: " + score + "\n" +
                    "Date: " + date + "\n" +
                    "Rank: " + rank + "\n" +
                    "```";
            channel.sendMessage(resultMsg);
        }
    }

    private static void OSUProfile(IChannel channel, IUser user, IMessage message) throws RateLimitException, DiscordException, MissingPermissionsException, IOException {
        String username = OSUIDManager.get(user.getStringID());
        if ("".equals(username) || username == null) {
            message.reply("set your osu!id first please!(use " + Settings.PREFIX + "osu setid [id] command");
        } else {
            URLConnection conn = URLUtils.openConnection(new URL("https://osu.ppy.sh/api/get_user?"
                            + "k=" + Settings.OSU_APIKEY
                            + "&u=" + username.replaceAll(" ", "+")
                    )
            );
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("User-agent", Settings.URL_AGENT);

            BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String result = "";
            String line = "";
            while ((line = bf.readLine()) != null) {
                result += line;
            }
            bf.close();
            if (result.length() < 4) {
                channel.sendMessage("no data, something is wrong?");
                return;
            }

            JsonObject obj = new JsonParser().parse(result).getAsJsonArray().get(0).getAsJsonObject();

            String userid = obj.get("user_id").getAsString();
            String _username = obj.get("username").getAsString();
            String playcount = obj.get("playcount").getAsString();
            String ranked_score = obj.get("ranked_score").getAsString();
            String total_score = obj.get("total_score").getAsString();
            String pp_rank = obj.get("pp_rank").getAsString();
            String level = obj.get("level").getAsString();
            String pp_raw = obj.get("pp_raw").getAsString();
            String accuracy = obj.get("accuracy").getAsString().substring(0,5);
            String country = obj.get("country").getAsString();
            String pp_country_rank = obj.get("pp_country_rank").getAsString();
            String userpage = "https://osu.ppy.sh/u/" + userid;

            //搞不懂这排版，先这么放着吧
            //inline = false 会在field前后都换行，差评
            EmbedObject embedObject = new EmbedBuilder()
                    .withAuthorName(_username + "'s profile " + country)
                    .withAuthorIcon("https://a.ppy.sh/" + userid)
                    .withAuthorUrl(userpage)
                    .withColor(255,255,255)
                    .appendField("PlayCount: ", playcount, true)
                    .appendField("PP: ",pp_raw, true)
                    .appendField("Accuracy: ",accuracy + "%",true)
                    .appendField("Global Rank: ",pp_rank, true)
                    .appendField("Country Rank: ",pp_country_rank, true)
                    .appendField("Ranked Score: ", ranked_score, true)
                    .appendField("Total Score: ",total_score,true)
                    .appendField("Level:", level, true)
                    .build();
            channel.sendMessage(embedObject);
        }
    }

    //暂时不考虑新主页的beatmap链接
    //如果是beatmapset链接(s/xxxxx)默认返回主难度
    //目前oppai的java库只支持std和taiko，而且taiko的转谱好像还不准
    public static void calcPP(IChannel channel, IMessage message, String[] params) {
        if (params.length == 0 || params.length > 1) {
            channel.sendMessage("please use this command like `%osu pp https://osu.ppy.sh/s/648232`");
            return;
        }
        String beatmapid = "";
        String request_params = "";

        //根据setid返回STD主难度id
        if (params[0].matches("(https://)?osu.ppy.sh/s/\\d+")) {
            String setid = params[0].substring(params[0].lastIndexOf("/") + 1);
            request_params = "k=" + Settings.OSU_APIKEY + "&s=" + setid;
            try {
                URL url = new URL("https://osu.ppy.sh/api/get_beatmaps");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-agent", Settings.URL_AGENT);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(conn.getOutputStream())));
                bw.write(request_params);
                bw.flush();
                bw.close();
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = "";
                String line;
                while ((line = bf.readLine()) != null) {
                    result += line;
                }
                bf.close();
                if(result.length()<4){
                    channel.sendMessage("beatmap does not exist.");
                    return;
                }
                JsonArray beatmaps = new JsonParser().parse(result).getAsJsonArray();
                //倒序查找STD主难度，因为其他模式的排在STD后面
                int size = beatmaps.size();
                for(; size > 0; size--){
                    JsonObject beatmap = beatmaps.get(size-1).getAsJsonObject();
                    if(beatmap.get("mode").getAsInt() == 0){
                        beatmapid = beatmap.get("beatmap_id").getAsString();
                        break;
                    }
                }
                if("".equals(beatmapid)){
                    channel.sendMessage("only support STD now.");
                    return;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (params[0].matches("(https://)?osu.ppy.sh/b/\\d+")) {
            beatmapid = params[0].substring(params[0].lastIndexOf("/") + 1);
            request_params = "k=" + Settings.OSU_APIKEY + "&b=" + beatmapid;
            try {
                URL url = new URL("https://osu.ppy.sh/api/get_beatmaps");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-agent", Settings.URL_AGENT);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(conn.getOutputStream())));
                bw.write(request_params);
                bw.flush();
                bw.close();
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String result = "";
                String line;
                while ((line = bf.readLine()) != null) {
                    result += line;
                }
                bf.close();
                if(result.length()<4){
                    channel.sendMessage("beatmap does not exist.");
                    return;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //获取难度文件(.osu)，oppai根据文件计算难度
        try {
            URL url = new URL("https://osu.ppy.sh/osu/" + beatmapid);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-agent", Settings.URL_AGENT);
            conn.setDoInput(true);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            File file = new File("diff.osu");
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.flush();
            fos.close();

            //oppai by Koohii
            BufferedReader stdin = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            Koohii.Map beatmap = new Koohii.Parser().map(stdin);
            Maps.LastBeatMapRequested.put(channel,beatmap);
            Koohii.DiffCalc stars = new Koohii.DiffCalc().calc(beatmap);
            Koohii.PPv2 pp = new Koohii.PPv2(
                    stars.aim, stars.speed, beatmap);
            channel.sendMessage(String.format("%s - %s[%s]\n" +
                            "Beatmap by %s\n" +
                            "Star Rating: %s\n" +
                            "pp for SS: %s",
                    beatmap.artist,beatmap.title,beatmap.version,beatmap.creator,stars.total,pp.total));

            message.getClient().changePlayingText(beatmap.title);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void calcPPWithMods(IChannel channel, IMessage message, String[] params){
        int mods = Koohii.mods_from_str(params[0]);
        if(mods == 0){
            channel.sendMessage("unknown mods");
            return;
        }
        Koohii.Map lastMap = Maps.LastBeatMapRequested.get(channel);
        if(lastMap == null){
            channel.sendMessage("I don't know which map you are referring.");
            return;
        }
        Koohii.DiffCalc stars = new Koohii.DiffCalc().calc(lastMap, mods);
        Koohii.PPv2 pp = new Koohii.PPv2(
                stars.aim, stars.speed, lastMap, params[0]);
        channel.sendMessage(String.format("%s - %s[%s]\n" +
                        "Beatmap by %s\n" +
                        "Star Rating: %s\n" +
                        "pp for SS: %s\n" +
                        "**with %s**",
                lastMap.artist, lastMap.title, lastMap.version, lastMap.creator, stars,pp.total, params[0].toUpperCase()));
        message.getClient().changePlayingText(lastMap.title + " " + params[0]);
    }

    private static void searchBeatmap(IChannel channel, String keyword){
        try {
            //创建SSLContext
            SSLContext sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new TrustManager()};
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf=sslContext.getSocketFactory();

            String requestURL = "https://osu.ppy.sh/beatmapsets/search?q=" + keyword;
            Log.getLogger().info("requested URL:" + requestURL);
            URL url = new URL(requestURL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("user-agent", Settings.URL_AGENT);
            connection.setRequestProperty("referer","https://osu.ppy.sh/beatmapsets");
            connection.setSSLSocketFactory(ssf);
            connection.setRequestProperty("Cookie", "osu_session=eyJpdiI6IjE1TnhkXC9yaE9Tb3JJamJyZHdhSjJ3PT0iLCJ2YWx1ZSI6IjFTOTBpUEZhMmM1XC9uMlZiXC85VG1sRUZQUEN6OEd3bE1yTW9lNFhaN25Kek8xTlVYUVAxdVVcL1ZISDAzSWFzemI0d1J0NGtZYVE4Ynk2VWtISDVSczFRPT0iLCJtYWMiOiIxNTVjMjQxMTg5ZGMxYjk0ZWMzYTVjN2MxZTYyZGRhZmM5NDA5MmQzYTQwOWNmNmE1MjZkY2FlZTIxOGM0ZWExIn0%3D");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String result = "";
            while((line = reader.readLine())!=null){
                result += line;
            }
            reader.close();

            result = result.replace("card@2x","card2x")
                    .replace("cover@2x","cover2x")
                    .replace("list@2x","list2x")
                    .replace("slimcover@2x","slimcover2x");
            JsonArray beatmapJsonArray = new JsonParser().parse(result).getAsJsonArray();
            Gson gson = new Gson();
            BeatmapSet beatmapSet = null;
            for(int i = 0;i < 5;i++){
                beatmapSet = gson.fromJson(beatmapJsonArray.get(i), BeatmapSet.class);
                channel.sendMessage(beatmapSet.toEmbed());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            channel.sendMessage(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            channel.sendMessage(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
