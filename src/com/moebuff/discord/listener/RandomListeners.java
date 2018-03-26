package com.moebuff.discord.listener;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.moebuff.discord.Settings;
import com.moebuff.discord.maps.Maps;
import com.moebuff.discord.oppai.Koohii;
import com.moebuff.discord.utils.Log;
import com.vdurmont.emoji.EmojiManager;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.GuildEmojisUpdateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.*;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RandomListeners {

    private static String MsgFromQQ = "";

    @EventSubscriber
    public static void onWhatReceive(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        IChannel channel = message.getChannel();
        String content = message.getContent();
        String whatPattern = "[.?。？]*(你说)?(what|waht|啥|什么)[.?。？]*";
        if(content.toLowerCase().matches(whatPattern)){
            channel.setTypingStatus(true);//正在输入，回复后自动取消
            MessageHistory messageHistory = channel.getMessageHistory();
            IMessage repeatMessage = messageHistory.get(1);
            if(Settings.BOT_ID_STRING.equals(repeatMessage.getAuthor().getStringID())){
                channel.sendMessage("You should read my words more carefully! ( ╬◣ 益◢)y");
            }else{
                /*test
                channel.sendMessage(
                        "0:" + messageHistory.get(0) + "\n" +
                        "1:" + messageHistory.get(1) + "\n" +
                        "2:" + messageHistory.get(2) + "\n" +
                        "3:" + messageHistory.get(3) + "\n"

                );*/
                channel.sendMessage("**" + repeatMessage.getContent() + "**");
            }
        }
    }

    @EventSubscriber
    public static void onMessageDelete(MessageDeleteEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;
        message.reply("You withdrew a message.");
        IGuild guild = message.getGuild();
        String msg = String.format("You withdrew ***%s*** in ***%s#%s***.",
                message.getContent(),
                guild == null ? "" : guild.getName(),
                message.getChannel().getName());
        user.getOrCreatePMChannel().sendMessage(msg);
    }

    @EventSubscriber
    public static void onThinkingReceive(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        String content = message.getContent();
        if(content.toLowerCase().contains("thinking") || content.toLowerCase().contains("thonking")){
            message.addReaction(EmojiManager.getForAlias("thinking"));
        }
    }

    @EventSubscriber
    public static void onReactionAdd(ReactionAddEvent event){
        if(event.getUser().getStringID() != Settings.BOT_ID_STRING){
            IMessage message = event.getMessage();
            IReaction reaction = event.getReaction();
            message.addReaction(reaction.getEmoji());
        }
    }

    @EventSubscriber
    public static void onReactionRemove(ReactionRemoveEvent event){
        if(event.getUser().getStringID() != Settings.BOT_ID_STRING){
            IMessage message = event.getMessage();
            IReaction reaction = event.getReaction();
            message.removeReaction(event.getClient().getOurUser(),reaction.getEmoji());
        }
    }

    @EventSubscriber
    public static void onMessageEdit(MessageUpdateEvent event){
        IMessage message = event.getNewMessage();
        message.addReaction(EmojiManager.getForAlias("thinking"));
    }

    @EventSubscriber
    public static void onGuildEmojisUpdate(GuildEmojisUpdateEvent event){
        //API有bug，先不管
        /*IChannel channel = event.getGuild().getChannelsByName("general").get(0);
        List<IEmoji> emojis = event.getNewEmojis();
        if(channel!=null){
            for(IEmoji emoji : emojis){
                channel.sendMessage(emoji.getName());
            }
        }*/
    }

    @EventSubscriber
    public static void onBot(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        String content = message.getContent().toLowerCase();
        if(content.contains("bot") || content.contains("机器人")){
            message.addReaction(EmojiManager.getForAlias("thinking"));
        }
    }

    @EventSubscriber
    public static void onRepeating(MessageReceivedEvent event){
        IChannel channel = event.getChannel();
        IMessage his1 = channel.getMessageHistory().get(0);
        IMessage his2 = channel.getMessageHistory().get(1);
        IMessage his3 = channel.getMessageHistory().get(2);

        if(his1.getAuthor()!=his1.getClient().getOurUser() && his1.getContent().equals(his2.getContent()) && his2.getContent().equals(his3.getContent())) {
            channel.sendMessage(his1.getContent());
            Log.getLogger().info("auto repeat: " + his1.getContent());
        }
    }

    @EventSubscriber
    public static void onDiscordToQQ(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        IChannel channel = event.getChannel();
        IGuild guild = event.getGuild();
        IUser user = event.getAuthor();
        IUser our = event.getClient().getOurUser();
        String content = message.getContent();
        //仅供测试
        if(guild.getStringID().equals("267506592977649675") && Maps.QQChannelForGuild.get(guild) == channel){
            try {
                URL url = new URL("http://127.0.0.1:5700/send_group_msg");//coolQ http api
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                Map params = new HashMap();
                params.put("group_id", 135294979);//osu河北群
                String msg = "from discord: <" + user.getName() + ">: " + content;

                //TODO
                /*
                好乱，先不管了
                //自定义表情的文本是 <:267587760297213963:422931971707240459> 分别是guiidID和emojiID
                String pattern = "<:267587760297213963:\\d{18}>";
                int index = 0;
                if(content.matches("\\S{0}(<:267587760297213963:\\d{18}>)+\\S{0}")) {
                    index = content.indexOf("<:267587760297213963:", index);
                    String emojiID = content.substring(index + "<:267587760297213963:".length(), "<:267587760297213963:".length() + 18);
                    Log.getLogger().debug(emojiID);
                    IEmoji emoji = guild.getEmojiByID(Long.valueOf(emojiID));
                    String imageUrl = emoji.getImageUrl();
                    URL imageURL = new URL(imageUrl);
                    HttpURLConnection conn1 = (HttpURLConnection) imageURL.openConnection();
                    conn1.setRequestMethod("GET");
                    conn1.setDoOutput(true);

                    InputStream inputStream = conn.getInputStream();
                    byte[] bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                }
                */

                params.put("message", msg);
                params.put("auto_escape", false);
                String s = new Gson().toJson(params);
                conn.getOutputStream().write(s.getBytes("UTF-8"));

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                line = reader.readLine();
                String status = new JsonParser().parse(line).getAsJsonObject().get("status").getAsString();
                if(status.equals("failed")){
                    channel.sendMessage("failed to send message to qq group");
                    channel.sendMessage(line);
                }
                message.getClient().changePlayingText("tencent");
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

    @EventSubscriber
    public static void onBotSentToQQ(MessageSendEvent event){
        IMessage message = event.getMessage();
        IChannel channel = event.getChannel();
        IGuild guild = event.getGuild();
        IUser our = event.getClient().getOurUser();
        //仅供测试
        if(guild.getStringID().equals("267506592977649675") && Maps.QQChannelForGuild.get(guild) == channel){
            try {
                URL url = new URL("http://127.0.0.1:5700/send_group_msg");//coolQ http api
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                Map params = new HashMap();
                params.put("group_id", 135294979);//osu河北群
                String msg = "from discord:[" + our.getName() + "]: " + message.getContent();
                msg += " (没错，这条消息来自本bot，看看这群人都让我说了什么，哼)";
                params.put("message", msg);
                params.put("auto_escape", false);
                String s = new Gson().toJson(params);
                conn.getOutputStream().write(s.getBytes("UTF-8"));

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                line = reader.readLine();
                message.getClient().changePlayingText("tencent");
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


}
