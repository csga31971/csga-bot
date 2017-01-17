package com.moebuff.discord;

import com.moebuff.discord.reflect.ClassKit;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.UnhandledException;
import org.slf4j.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;

public class BotLauncher implements IListener<ReadyEvent> {
    private static IDiscordClient client;
    private static EventDispatcher dispatcher;

    private static final String LISTENER_PKG = "com.moebuff.discord.listener";
    private static final Logger LOGGER = Log.getLogger();

    public static void main(String[] args) throws DiscordException {
        LOGGER.info("Logging bot in...");
        client = new ClientBuilder().withToken(Settings.TOKEN).build();
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
        LOGGER.info(Settings.USERNAME + " is ready.");
    }
}
