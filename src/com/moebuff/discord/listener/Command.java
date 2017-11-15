package com.moebuff.discord.listener;

import com.google.gson.*;
import com.moebuff.discord.AccessControl;
import com.moebuff.discord.PermissionException;
import com.moebuff.discord.Settings;
import com.moebuff.discord.utils.OSUIDManager;
import com.moebuff.discord.utils.URLUtils;
import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.handle.obj.IMessage.Attachment;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

/**
 * 命令行
 *
 * @author muto
 */
public class Command {
    private static final String PREFIX = "%";

    private static IMessage message;
    private static IUser user;
    private static IChannel channel;//频道
    private static IGuild guild;//工会

    private static String[] args;




    @EventSubscriber
    public static void onMessageReceived(MessageReceivedEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        message = event.getMessage();
        user = message.getAuthor();
        if (user.isBot()) return;

        channel = message.getChannel();
        guild = message.getGuild();
        List<IUser> mentionedUsers = message.getMentions();
        List<IRole> mentionedRoles = message.getRoleMentions();
        String[] split = message.getContent().split(" ");

        String cmd = split[0];
        if (!cmd.startsWith(PREFIX)) return;
        args = split.length > 1 ?
                Arrays.copyOfRange(split, 1, split.length) :
                new String[0];
        switch (cmd.substring(1).toLowerCase()) {
            case "roll":
                roll();
                break;
            case "vol":
            case "music":
            case "au":
            case "audio":
                Audio.handle(guild, channel, user, args);
                break;
            case "163":
                netEase();
                break;
            case "off":
            case "exit":
                AccessControl auth = new AccessControl(user);
                try {
                    auth.contains(guild, Permissions.ADMINISTRATOR);
                    channel.sendMessage("Shutdown in progress.");
                    System.exit(0);
                } catch (PermissionException e) {
                    channel.sendMessage(e.getMessage());
                }
                break;
            case "setid":
                try {
                    String osu_user_id = "";
                    if(message.getContent().contains(" "))
                        osu_user_id = message.getContent().substring(message.getContent().indexOf(" ")+1);
                    setOSUId(osu_user_id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "recent":
                try {
                    OSURecent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "profile":
                try {
                    OSUProfile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "repeat":
                repeat();
                break;
            /**
             *
             * waifu2x is protected by google reCAPTCHA :(
             * api: http://waifu2x.udp.jp/api
             * params: file: [your uploaded file] / url: [url link to an image]
             *         style: (art | photo)
             *         noise: (-1 | 0 | 1 | 2 | 3) ==> no noise reduction / low ~ ~ / medium / high / highest
             *         scale: (-1 | 1 | 2) ==> no scale | 1.6x | 2x
             *
            case "waifu":

                break;
                */

            //禁言相关
            //效率太低，而且没什么实用性，瞎写着玩
            case "sleep":
            case "silence":
            case "shuiba":
            case "睡吧":
                Silence.silence(guild, channel, user, mentionedUsers);
                break;
            case "wake":
                Silence.wake(guild, channel, user, mentionedUsers);
                break;
            case "setdogrole":
                Silence.setDogRole(guild, channel, user, mentionedRoles);
                break;
            case "updog":
                Silence.updog(guild, channel, user, mentionedUsers);
                break;
            case "downdog":
                Silence.downdog(guild, channel, user, mentionedUsers);
                break;
            case "setsilencerole":
                Silence.setSilenceRole(guild, channel, user, mentionedRoles);
                break;
            case "setfreechannel":
                Silence.setFreeChannel(guild, channel, user);
                break;
            case "reset":
                Silence.reset(guild, channel);
                break;
            default:
                message.getClient().changePlayingText(cmd);
                break;
        }

        // TODO: 命令行模式，摆脱对if和switch的依赖

    }

    private static void roll()
            throws RateLimitException, DiscordException, MissingPermissionsException {
        int max = 100;
        if (args.length > 0) {
            max = Integer.parseInt(args[0]);
        }

        int num = RandomUtils.nextInt(0, max);
        message.reply(String.format("rolls %s point(s).", num));
    }

    /**
     * 网易云音乐
     *
     * @throws RateLimitException          过于频繁
     * @throws DiscordException            其它原因
     * @throws MissingPermissionsException 没有权限
     */
    private static void netEase()
            throws RateLimitException, DiscordException, MissingPermissionsException {
        Audio.join(guild, channel, user);
    }

    private static void setOSUId(String id) throws IOException, RateLimitException, DiscordException, MissingPermissionsException {

        if(checkOSUId(id)){
            OSUIDManager.put(user.getName(),id);
            channel.sendMessage("osu!id set:    " + user.getName() + " : " + id);
        }

    }
    private static boolean checkOSUId(String id) throws IOException, RateLimitException, DiscordException, MissingPermissionsException {
        String username = OSUIDManager.get(user.getName());
        //command param is null or 0 length
        if("".equals(id) || id == null){
            message.reply("please add your id after %setid command. (e.g. %setid cookiezi)");
            return false;
        }

        //already set id
        if(username != null && !"".equals(username)){
            message.reply("you have already set your id: " + username);
            return false;
        }

        //check if username exists (from osu!api)
        URLConnection conn = URLUtils.openConnection(new URL("https://osu.ppy.sh/api/get_user?"
                + "k=" + Settings.OSU_APIKEY
                + "&u="+ id.replaceAll(" ", "+"))
                );
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("contentType", "application/json");
        conn.setRequestProperty("user-agent", Settings.URL_AGENT);

        BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String result = "";
        String line;
        while((line = bf.readLine()) != null){
            result += line;
        }

        if(result.length()<4){
            message.reply("I can't find this ID in osu");
            return false;
        }

        bf.close();

        return true;
    }

    private static void OSURecent() throws RateLimitException, DiscordException, MissingPermissionsException, IOException {
        String username = OSUIDManager.get(user.getName());
        //System.out.println("username:" + username);
        if("".equals(username) || username == null){
            message.reply("set your osu!id first please!(use " + PREFIX + "setid [id] command");
        }else{
            URLConnection conn = URLUtils.openConnection(new URL("https://osu.ppy.sh/api/get_user_recent?"
                    + "k=" + Settings.OSU_APIKEY
                    + "&u="+ username.replaceAll(" ", "+")
                    + "&limit=" + "1"
                    )
            );
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("user-agent", Settings.URL_AGENT);

            BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";
            while((line = bf.readLine()) != null){
                result += line;
            }

            bf.close();

            if(result.length()<4){
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
            String date =  obj.get("date").getAsString();
            String rank =  obj.get("rank").getAsString();

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

    private static void OSUProfile() throws RateLimitException, DiscordException, MissingPermissionsException, IOException {
        String username = OSUIDManager.get(user.getName());
        //System.out.println("username:" + username);
        if ("".equals(username) || username == null) {
            message.reply("set your osu!id first please!(use " + PREFIX + "setid [id] command");
        } else {
            URLConnection conn = URLUtils.openConnection(new URL("https://osu.ppy.sh/api/get_user?"
                            + "k=" + Settings.OSU_APIKEY
                            + "&u=" + username.replaceAll(" ", "+")
                    )
            );
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("contentType", "application/json");
            conn.setRequestProperty("user-agent", Settings.URL_AGENT);

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
            String _username = obj.get("username").getAsString();
            String playcount = obj.get("playcount").getAsString();
            String ranked_score = obj.get("ranked_score").getAsString();
            String total_score = obj.get("total_score").getAsString();
            String pp_rank = obj.get("pp_rank").getAsString();
            String level = obj.get("level").getAsString();
            String pp_raw = obj.get("pp_raw").getAsString();
            String accuracy = obj.get("accuracy").getAsString();
            /*String count_rank_ss = obj.get("count_rank_ss").getAsString();
            String count_rank_s = obj.get("count_rank_s").getAsString();
            String count_rank_as = obj.get("count_rank_a").getAsString();
            String country = obj.get("country").getAsString();*/
            String pp_country_rank = obj.get("pp_country_rank").getAsString();

            String userpage = "https://osu.ppy.sh/u/" + userid;
            String resultMsg = "```\n" +
                    "Username: " + _username + "\n" +
                    "Userpage: " + userpage + "\n" +
                    "Playcount: " + playcount + "\n" +
                    "PP: " + pp_raw + "\n" +
                    "Accuracy: " + accuracy + "\n" +
                    "Global Rank: " + pp_rank + "\n" +
                    "Country Rank: " + pp_country_rank + "\n" +
                    "Ranked Score: " + ranked_score + "\n" +
                    "Total Score: " + total_score + "\n" +
                    "Level: " + level + "\n" +
                    "```";

            channel.sendMessage(resultMsg);
        }
    }

    public static void repeat(){

        IMessage repeated = channel.getMessageHistory().get(1);
        String repeatContent = repeated.getContent();

        if(Settings.BOT_ID_STRING.equals(repeated.getAuthor().getStringID())){
            channel.sendMessage("I won't repeat my words ! I'm not a repeater like you! >A<");
            if("I won't repeat my words ! I'm not a repeater like you! >A<".equals(repeatContent)){
                channel.sendMessage("Wait, I am not repeating myself!");
            }
            if(repeatContent.matches("Wait, I am not repeating myself[!]+")){
                channel.sendMessage("Wait, I am not repeating myself !" + "!!!");
            }

            return;
        }

        //message.edit(repeatContent);  bots can't edit users' message currently ._.
        channel.sendMessage(repeatContent);

    }

}
