package com.moebuff.discord.listener;

import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Arrays;

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
    public static void onMessage(MessageReceivedEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        message = event.getMessage();
        user = message.getAuthor();
        if (user.isBot()) return;

        channel = message.getChannel();
        guild = message.getGuild();
        String[] split = message.getContent().split(" ");

        String cmd = split[0];
        if (!cmd.startsWith(PREFIX)) return;
        args = split.length > 1 ?
                Arrays.copyOfRange(split, 1, split.length) :
                new String[0];
        switch (cmd.substring(1)) {
            case "roll":
                roll();
                break;
            case "vol":
            case "music":
            case "au":
            case "audio":
                Audio.handle(guild, channel, user, args);
                break;
            case "163":
                netEase();
                break;
        }

        // Display the last command
        message.getClient().changeStatus(Status.game(cmd));
    }

    private static void roll()
            throws RateLimitException, DiscordException, MissingPermissionsException {
        int max = 100;
        if (args.length > 0) {
            max = Integer.parseInt(args[0]);
        }

        int num = RandomUtils.nextInt(0, max);
        message.reply(String.format("rolls %s point(s).", num));
    }

    /**
     * 网易云音乐
     *
     * @throws RateLimitException          请求过于频繁
     * @throws DiscordException            消息未发送
     * @throws MissingPermissionsException 没有权限
     */
    private static void netEase()
            throws RateLimitException, DiscordException, MissingPermissionsException {
        Audio.join(guild, channel, user);
    }
}
