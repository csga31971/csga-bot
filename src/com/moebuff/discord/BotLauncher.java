package com.moebuff.discord;

import com.moebuff.discord.reflect.ClassKit;
import com.moebuff.discord.utils.Log;
import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;

import java.util.List;

public class BotLauncher implements IListener<ReadyEvent> {
    private static IDiscordClient client;
    private static EventDispatcher dispatcher;

    private static final String LISTENER_PKG = "com.moebuff.discord.listener";

    public static void main(String[] args) throws DiscordException {
        Log.getLogger().info("Logging bot in...");
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
        List<IUser> users = client.getUsers();
        int index = RandomUtils.nextInt(0, users.size());
        String name = users.get(index).getName();
        client.changeStatus(Status.game(name));
    }
}
