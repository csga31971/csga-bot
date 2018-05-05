package com.moebuff.discord.listener;

import com.moebuff.discord.AccessControl;
import com.moebuff.discord.PermissionException;
import com.moebuff.discord.Settings;
import com.moebuff.discord.maps.Maps;
import com.moebuff.discord.service.UserManager;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.URLUtils;
import org.apache.commons.lang3.RandomUtils;
import org.pircbotx.exception.IrcException;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        String[] split = message.getContent().toLowerCase().replaceAll("\\s{2,}"," ").split(" ");
        String cmd = split[0];
        if (!cmd.startsWith(PREFIX)) return;
        args = split.length > 1 ?
                Arrays.copyOfRange(split, 1, split.length) :
                new String[0];
        String content = message.getContent();
        int errCmdCount = 0;
        switch (cmd.substring(1).toLowerCase()) {
            case "roll":
                roll();
                errCmdCount = 0;
                break;
            case "vol":
            case "music":
            case "au":
            case "audio":
                Audio.handle(guild, channel, user, args);
                errCmdCount = 0;
                break;
            case "163":
            case "netease":
                netEase();
                errCmdCount = 0;
                break;
            case "off":
            case "exit":
                errCmdCount = 0;
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
                errCmdCount = 0;
                OSUHandler.handle(guild,channel,user,message,args);
                break;
            case "repeat":
                errCmdCount = 0;
                repeat();
                break;
            //禁言相关
            //效率太低，而且没什么实用性，瞎写着玩
            case "sleep":
            case "silence":
            case "shuiba":
            case "睡吧":
                errCmdCount = 0;
                Silence.silence(guild, channel, user, mentionedUsers);
                break;
            case "wake":
                errCmdCount = 0;
                Silence.wake(guild, channel, user, mentionedUsers);
                break;
            case "setdogrole":
                errCmdCount = 0;
                Silence.setDogRole(guild, channel, user, mentionedRoles);
                break;
            case "updog":
                errCmdCount = 0;
                Silence.updog(guild, channel, user, mentionedUsers);
                break;
            case "downdog":
                errCmdCount = 0;
                Silence.downdog(guild, channel, user, mentionedUsers);
                break;
            case "setsilencerole":
                errCmdCount = 0;
                Silence.setSilenceRole(guild, channel, user, mentionedRoles);
                break;
            case "setfreechannel":
                errCmdCount = 0;
                Silence.setFreeChannel(guild, channel, user);
                break;
            case "reset":
                errCmdCount = 0;
                Silence.reset(guild, channel);
                break;
            case "gift":
                errCmdCount = 0;
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
                errCmdCount = 0;
                gust();
                break;
            /*
            弄不好，先去掉
            case "irc":
                IRC.handle(guild,channel,args);
                break;
            */
            case "five":
                errCmdCount = 0;
                FiveChess.handle(guild,channel,user,message,args);
                break;
            case "panda":
                errCmdCount = 0;
                PandaHandler.handle(guild,channel,user,message,args);
                break;
            case "rabbit":
                errCmdCount = 0;
                RabbitHandler.handle(guild,channel,user,message,args);
                break;
            case "say":
                errCmdCount = 0;
                if(!content.substring(4).matches("\\s*")){
                    message.delete();
                    channel.sendMessage(content.substring(4));
                }
                break;
            /*case "setqq":
                setQQ();
                break;*/
            /*case "enableqq":
                if(user.getStringID().equals("267505999764520961")){
                    MsgFromQQ.getInstance().setEnabled();
                    MsgFromQQ.getQQThread().start();
                    channel.sendMessage("qq enabled");
                }else{
                    channel.sendMessage("you are not allowed to do this");
                }
                break;
            case "disableqq":
                if(user.getStringID().equals("267505999764520961")){
                    MsgFromQQ.getInstance().setDisnabled();
                    channel.sendMessage("qq disabled");
                    MsgFromQQ.getQQThread().start();
                }else{
                    channel.sendMessage("you are not allowed to do this");
                }
                break;*/
            /*case "toqq":
                if(args.length == 0){
                    channel.sendMessage("please add option: [1/2] (1=osu hebei, 2=home)");
                    return;
                }
                if(args[0].equals("1")){
                    RandomListeners.toGroup = 135294979;
                }else if(args[0].equals("2")){
                    RandomListeners.toGroup = 139841354;
                }else{
                    channel.sendMessage("invalid args.");
                }
                break;*/
            case "init":
                errCmdCount = 0;
                try{
                    UserManager.initUser(guild);
                } catch (Exception e){
                    channel.sendMessage("something is wrong: " + e.getMessage());
                }
                channel.sendMessage("successfully initialized users");
                break;
            case "help":
                errCmdCount = 0;
                help();
                break;
            case "superat":
                errCmdCount = 0;
                superat();
                break;
            default:
                errCmdCount++;
                if(errCmdCount>2){
                    channel.sendMessage("你是弱智吗连个命令都打不对？看老娘play你们是不是很爽？");
                }
                message.getClient().changePlayingText(cmd.substring(1));
                break;
        }
        // TODO: 命令行模式，摆脱对if和switch的依赖
    }

    private static void roll()
            throws RateLimitException, DiscordException, MissingPermissionsException {
        int max = 100;
        try{
            if (args.length > 0) {
                max = Integer.parseInt(args[0]);
                if(max <= 0 ) {
                    max = 100;
                }
            }
        } catch (NumberFormatException e){
            Log.getLogger().info(e.getMessage());
        } finally {
            int num = RandomUtils.nextInt(0, max);
            message.reply(String.format("rolls %s point(s).", num));
        }
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
        List<IMessage.Attachment> attachments = repeated.getAttachments();
        String repeatContent = repeated.getContent();
        if (Settings.BOT_ID_STRING.equals(repeated.getAuthor().getStringID())) {
            channel.sendMessage("I won't repeat my words! I'm not a repeater like you! >A<");
            if ("I won't repeat my words! I'm not a repeater like you! >A<".equals(repeatContent) && repeated.getAuthor()==repeated.getClient().getOurUser()) {
                channel.sendMessage("Wait, I am not repeating myself!");
            }
            if (repeatContent.matches("Wait, I am not repeating myself[!]+")) {
                channel.sendMessage(repeatContent + "!!!");
            }
            return;
        }
        message.delete();
        if(attachments!=null && attachments.size()>0){
            try {
                URL url = new URL(attachments.get(0).getUrl());
                HttpsURLConnection connection  = (HttpsURLConnection) url.openConnection();
                connection.addRequestProperty("user-agent",Settings.URL_AGENT);
                channel.sendFile(repeatContent, connection.getInputStream(), attachments.get(0).getFilename());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }else{
            channel.sendMessage(repeatContent);
        }

    }

    //咼敳罘的api
    private static void gust() {
        message.getClient().changePlayingText("with Gust's waifu");
        if(args.length<2) {
            channel.sendMessage("please play with gust like `" + Settings.PREFIX + "gust [nick] [message]`");
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

    private static void setQQ() {
        /*channel.sendMessage("currently disabled");
        return;*/

        if(!guild.getStringID().equals("267506592977649675")){
            channel.sendMessage("sorry, this function is for private use");
            return;
        }
        if(args.length==0){
            channel.sendMessage("set qq channel: " + channel.getName() + ", all messages in this channel will be sent to qq group automatically.");
            Maps.QQChannelForGuild.put(guild, channel);
        }else{
            if(args[0].toLowerCase().equals("r")){
                channel.sendMessage("set qq channel: null");
                Maps.QQChannelForGuild.put(guild, channel);
            }
        }
    }

    private static void superat(){
        if(args.length==0){
            channel.sendMessage("please mention the one you want to supermetion");
            return;
        }
        message.delete();
        int times = 20;
        List<IUser> mentions = message.getMentions();
        IUser target = mentions.get(0);
        String content = target.mention();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if(args.length==2)
                times = Integer.valueOf(args[1]);
        } catch (NumberFormatException e){
            channel.sendMessage(e.getMessage());
        }finally {
            for(int i =0;i<times;i++){
                stringBuilder.append(content);
            }
            channel.sendMessage(stringBuilder.toString());
        }
    }

    private static void help(){
        if(args.length==0){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("current prefix is `");
            stringBuilder.append(Settings.PREFIX);
            stringBuilder.append("`\n");
            stringBuilder.append("```roll [Integer]: return a random number between [0,Integer)\n");
            stringBuilder.append("repeat: repeat last message in this channel\n");
            stringBuilder.append("init: initialize users in this guild\n");
            stringBuilder.append("say [message]: let me say [message]\n");
            stringBuilder.append("gust [nick] [message]: return fake qq screenshot\n");
            stringBuilder.append("panda {index} [message]: return ruozhi panda image\n");
            stringBuilder.append("rabbit [top-message] [bottom-message]: return ruozhi rabbit image```\n");
            stringBuilder.append("for more commands, use " + Settings.PREFIX + "help [osu/five/audio/163/gift]");
            channel.sendMessage(stringBuilder.toString());
        }else{
            StringBuilder stringBuilder = new StringBuilder();
            switch (args[0].toLowerCase()){
                case "osu":
                    stringBuilder.append("osu ralated commands. you should use them like %osu {command} [params]\n");
                    stringBuilder.append("```setid: set your osuid\n");
                    stringBuilder.append("profile/p: show your profile\n");
                    stringBuilder.append("recent/r: show your recent submitted play (including fails)\n");
                    stringBuilder.append("pp {beatmaplink}: show pp for ss on given map\n");
                    stringBuilder.append("with [mod-combinations] show pp for ss with given mods\n");
                    stringBuilder.append("search/s [keyword]: search beatmaps with keyword and return top 5 of the result```");
                    break;
                case "five":
                    stringBuilder.append("five-in-a-row ralated commands. you should use them like %five {command} [params]\n");
                    stringBuilder.append("start/s: create a room\n");
                    stringBuilder.append("join/j [roomid]: join an existing room\n");
                    stringBuilder.append("place/p {x} {y}: place chess\n");
                    stringBuilder.append("quit/q: quit current room");
                    break;
                case "audio":
                    stringBuilder.append("debugging...");
                    break;
                case "163":
                    stringBuilder.append("search/s {keyword}: search music from NetEaseCloudMusic");
                    break;
                case "gift":
                    stringBuilder.append("@Deprecated");
                    break;
                    default:
                    stringBuilder.append("no such command");
            }
            channel.sendMessage(stringBuilder.toString());
        }
    }
}