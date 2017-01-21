package com.moebuff.discord.listener;

import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;

/**
 * 命令行
 */
public class Command {
    private static final String PREFIX = "%";

    private static IMessage message;
    private static IUser user;
    private static IChannel channel;//频道
    private static IGuild guild;//工会
    private static IDiscordClient client;

    private static String[] args;

    @EventSubscriber
    public static void onMessage(MessageReceivedEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        message = event.getMessage();
        user = message.getAuthor();
        if (user.isBot()) return;

        channel = message.getChannel();
        guild = message.getGuild();
        client = message.getClient();

        String[] content = message.getContent().split(" ");
        String cmd = content[0];
        if (!cmd.startsWith(PREFIX)) return;
        args = content.length > 1 ?
                Arrays.copyOfRange(content, 1, content.length) :
                new String[0];
        switch (cmd.substring(1)) {
            case "roll":
                int max = -1;
                if (args.length > 0) {
                    max = Integer.valueOf(args[0]);
                }
                roll(max);
                break;
            case "osu":
                OhShitUninstall.osu(channel);
                break;
            case "join":
                Audio.join(guild, channel, user);
                break;
            case "leave":
                Audio.leave(guild, channel);
                break;
            case "queueUrl":
                Audio.queueUrl(channel, String.join(" ", args));
                break;
            case "queueFile":
                Audio.queueFile(channel, String.join(" ", args));
                break;
            case "play":
                Audio.player(channel).setPaused(false);
                break;
            case "pause":
                Audio.player(channel).setPaused(true);
                break;
            case "skip":
                Audio.player(channel).skip();
                break;
            case "list":
                Audio.list(channel);
                break;
        }

        // Display the last command
        client.changeStatus(Status.game(cmd));
    }

    private static void roll(int max)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        int num = RandomUtils.nextInt(0, max > 0 ? max : 100);
        channel.sendMessage(String.format("%s rolls %s point(s).",
                user.mention(), num));
    }
}
