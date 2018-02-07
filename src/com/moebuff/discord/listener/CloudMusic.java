package com.moebuff.discord.listener;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.CloudMusicUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;

public class CloudMusic {

    private static int[] song_ID = new int[]{0,0,0,0,0,0,0,0,0,0};
    private static IGuild sguild = null;
    private static IChannel schannel = null;
    private static IUser suser = null;
    private static IMessage smessage = null;

    public static void handle(IGuild guild, IChannel channel, IUser user, String[] args, IMessage message){
        sguild = guild;
        schannel = channel;
        suser = user;
        smessage = message;
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
        switch(args[0]){
            case "search":
            case "s":
                getSongsByName(param_with_spacebar);
            case "play":
                playSong(Integer.valueOf(params[0]));
        }
    }

    static void getSongsByName(String name){
        Log.getLogger().info("requested song name:" + name);
        JsonArray songs = CloudMusicUtils.searchSong(name);
        Log.getLogger().info("songs:" + songs.toString());
        int count = 0;
        String msg = "Result: \n";
        for(JsonElement jsonElement:songs){
            JsonObject song = jsonElement.getAsJsonObject();
            song_ID[count] = song.get("privilege").getAsJsonObject().get("id").getAsInt();
            if(count<10){
                JsonArray artists = song.get("ar").getAsJsonArray();
                for(JsonElement jsonElement1:artists){
                    JsonObject artist = jsonElement1.getAsJsonObject();
                    msg += artist.get("name").getAsString();
                    msg += " & ";
                }
                msg = msg.substring(0,msg.lastIndexOf('&'));
                msg += "  --  ";

                count++;
                msg += count + ". ";
                msg += song.get("name").getAsString();
                msg += "\n";
            }
        }
        schannel.sendMessage(msg);
    }

    //先搜索，再play，奇怪的play
    static void playSong(int index){
        if(song_ID[0] == 0){
            schannel.sendMessage("I don't know what song are you mentioning, please use `%163 search [songName]` first!");
            return;
        }
        String mp3Link = CloudMusicUtils.getMp3Link(song_ID[index-1]);//序号比下标大1
        Audio.queueUrl(schannel,mp3Link);
    }
}
