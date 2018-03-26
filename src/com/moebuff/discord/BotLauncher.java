package com.moebuff.discord;

import com.moebuff.discord.reflect.ClassKit;
import com.moebuff.discord.utils.Log;
import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class BotLauncher implements IListener<ReadyEvent> {
    private static IDiscordClient client;
    private static EventDispatcher dispatcher;

    private static final String LISTENER_PKG = "com.moebuff.discord.listener";

    public static void main(String[] args) throws DiscordException {
        Log.getLogger().info("Logging bot in...");
        client = new ClientBuilder().withToken(Settings.BOT_TOKEN).build();
        client.login();

        dispatcher = client.getDispatcher();
        dispatcher.registerListener(new BotLauncher());
        Class[] classes = ClassKit.getClasses(LISTENER_PKG);
        for (Class c : classes) {
            dispatcher.registerListener(c);
        }
    }

    @Override
    public void handle(ReadyEvent event) {
        client.changePlayingText("Exception");
        //弱智
        /*IChannel spamChannel = event.getClient().getChannelByID(329504105137438722L);
        if(spamChannel != null){
            spamChannel.sendMessage("Let The Bass Kick!");
            spamChannel.sendMessage("O-oooooooooo AAAAE-A-A-I-A-U- JO-oooooooooooo AAE-O-A-A-U-U-A- E-eee-ee-eee AAAAE-A-E-I-E-A- JO-ooo-oo-oo-oo EEEEO-A-AAA-AAAA");
        }*/
    }
}
