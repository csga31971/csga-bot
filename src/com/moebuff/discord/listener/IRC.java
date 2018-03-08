package com.moebuff.discord.listener;

import com.moebuff.discord.utils.Log;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;
import java.util.Arrays;

public class IRC extends MyListenerAdapter{

    public static IChannel iChannel;
    public static String respond = "";

    public static void handle(IGuild guild, IChannel channel, String[] args) throws IOException, IrcException {
        Configuration configuration = new Configuration.Builder()
                .setName("CSGA-DarkArchon")
                .setServerPassword("c0318ae5")
                .addServer("irc.ppy.sh")
                .addListener(new IRCListener())
                .buildConfiguration();

        //Create our bot with the configuration
        PircBotX bot = new PircBotX(configuration);
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
            case "connect":
            case "c":
                connect(bot, channel);
                break;
            case "send":
                send(channel, param_with_spacebar);
                break;
            case "stop":
                bot.stopBotReconnect();
                bot.close();
                break;
            default:
                channel.sendMessage("unknown command.");
                break;
        }
    }

    public static void connect(PircBotX bot, IChannel channel) throws IOException, IrcException {
        bot.startBot();
        channel.sendMessage("irc connected");
    }

    public static void send(IChannel channel, String msg){
        respond = msg;

    }
}