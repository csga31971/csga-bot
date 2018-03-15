package com.moebuff.discord.listener;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.moebuff.discord.AccessControl;
import com.moebuff.discord.PermissionException;
import com.moebuff.discord.Settings;
import com.moebuff.discord.dao.GiftDAO;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            throws RateLimitException, DiscordException, MissingPermissionsException, IOException, IrcException {
        message = event.getMessage();
        user = message.getAuthor();
        if (user.isBot()) return;

        channel = message.getChannel();
        guild = message.getGuild();
        List<IUser> mentionedUsers = message.getMentions();
        List<IRole> mentionedRoles = message.getRoleMentions();
        String[] split = message.getContent().replaceAll("\\s{2,}"," ").split(" ");
        String cmd = split[0];
        if (!cmd.startsWith(PREFIX)) return;
        args = split.length > 1 ?
                Arrays.copyOfRange(split, 1, split.length) :
                new String[0];
        String content = message.getContent();
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
            case "netease":
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
            case "osu":
                OSUHandler.handle(guild,channel,user,message,args);
                break;
            case "repeat":
                repeat();
                break;
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
            case "gift":
                GiftHandler.handle(guild,channel,message.getAuthor(),message,args);
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
            case "gust":
                gust();
                break;
            /*
            弄不好，先去掉
            case "irc":
                IRC.handle(guild,channel,args);
                break;
            */
            case "five":
                FiveChess.handle(guild,channel,user,message,args);
                break;
            case "panda":
                PandaHandler.handle(guild,channel,user,message,args);
                break;
            case "rabbit":
                RabbitHandler.handle(guild,channel,user,message,args);
                break;
            case "say":
                if(!content.substring(4).matches("\\s*")){
                    channel.sendMessage(content.substring(4));
                }
                break;
            case "qq":
                qq();
                break;
            default:
                message.getClient().changePlayingText(cmd.substring(1));
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
        CloudMusic.handle(guild,channel,user,args,message);
    }

    private static void repeat() {
        IMessage repeated = channel.getMessageHistory().get(1);
        String repeatContent = repeated.getContent();
        if (Settings.BOT_ID_STRING.equals(repeated.getAuthor().getStringID())) {
            channel.sendMessage("I won't repeat my words ! I'm not a repeater like you! >A<");
            if ("I won't repeat my words ! I'm not a repeater like you! >A<".equals(repeatContent)) {
                channel.sendMessage("Wait, I am not repeating myself!");
            }
            if (repeatContent.matches("Wait, I am not repeating myself[!]+")) {
                channel.sendMessage("Wait, I am not repeating myself !" + "!!!");
            }

            return;
        }
        //message.edit(repeatContent);  bots can't edit users' message currently ._.
        channel.sendMessage(repeatContent);
    }

    //咼敳罘的api
    private static void gust() {
        message.getClient().changePlayingText("with Gust's waifu");
        if(args.length<2) {
            channel.sendMessage("please play with gust like `" + Settings.PREFIX + "gust [nick] [message]");
            return;
        }
        try {
            URL url = new URL("https://milkitic.name/api/getimage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-agent", Settings.URL_AGENT);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.getOutputStream().write(("nick=" + args[0] + "&message=" + args[1]).getBytes());

            InputStream inputStream = conn.getInputStream();
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            File file = new File("gust.png");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(b);
            fos.flush();
            fos.close();
            channel.sendFile(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //酷Q
    private static void qq() {
        message.getClient().changePlayingText("tencent");
        //仅供测试
        if(!guild.getStringID().equals("267506592977649675")){
            channel.sendMessage("just for private use");
            return;
        }
        if(args.length<1) {
            channel.sendMessage("please play with tencent like `" + Settings.PREFIX + "qq [message]");
            return;
        }
        try {
            URL url = new URL("http://127.0.0.1:5700/send_group_msg");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            Map params = new HashMap();
            params.put("group_id",135294979);//osu河北群
            params.put("message", "from discord:[" + user.getName() + "]: " + message.getContent().substring(4));
            params.put("auto_escape", false);
            String s = new Gson().toJson(params);
            conn.getOutputStream().write(s.getBytes("UTF-8"));

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            line = reader.readLine();
            Log.getLogger().info(line);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}