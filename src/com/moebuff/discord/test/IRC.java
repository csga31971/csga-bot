package com.moebuff.discord.test;

import com.moebuff.discord.listener.MyListenerAdapter;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.dcc.SendChat;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.QuitEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.IOException;

public class IRC extends ListenerAdapter {

    @Override
    public void onMessage(MessageEvent event) throws IOException, InterruptedException {
        String msg =event.getUser().getNick() + ": " + event.getMessage();
        System.out.println("**********************" + event.getUser().getNick() + ": " + event.getMessage() + "**************************");
    }

    @Override
    public void onJoin(JoinEvent event) throws IOException, InterruptedException {

    }
    @Override
    public void onQuit(QuitEvent event) throws IOException, InterruptedException {

    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws IOException, InterruptedException {
        String msg =event.getUser().getNick() + ": " + event.getMessage();
        System.out.println("**********************" + event.getUser().getNick() + ": " + event.getMessage() + "**************************");
        String message = event.getMessage();
        event.respondPrivateMessage(event.getMessage());
        if(message.contains("弱智") || message.contains("sb") || message.contains("SB") || message.contains("zz") || message.contains("智障")){
            event.respondPrivateMessage("gust是弱智");
        }
    }

    public static void main(String[] args) throws IOException, IrcException {
        Configuration configuration = new Configuration.Builder()
                .setName("CSGA-DarkArchon")
                .setServerPassword("c0318ae5")
                .addServer("irc.ppy.sh")
                .addAutoJoinChannel("#romanian")
                .addListener(new IRC())
                .buildConfiguration();

        //Create our bot with the configuration
        PircBotX bot = new PircBotX(configuration);
        //Connect to the server
        bot.startBot();

    }
}