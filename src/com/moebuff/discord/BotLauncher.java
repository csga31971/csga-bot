package com.moebuff.discord;

import com.moebuff.discord.utils.Log;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class BotLauncher {
    private static IDiscordClient client;

    public static void main(String[] args) throws DiscordException {
        Log.getLogger().info("Logging bot in...");
        client = new ClientBuilder().withToken(Settings.TOKEN).build();
        client.login();
    }
}
