package com.moebuff.discord.listener;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * OSU Support
 *
 * @author muto
 */
public class OSUListener {
    @EventSubscriber
    public static void onMessage(MessageReceivedEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;

        IChannel channel = message.getChannel();
        IGuild guild = message.getGuild();
        String[] split = message.getContent().split(" ");
        if (!split[0].equals("!osu")) return;

        if (split.length < 2) {
            channel.sendMessage("eg: !osu s/64631");
        } else {
            queueOsu(channel, split[1]);
        }
    }

    private static void queueOsu(IChannel channel, String code)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        channel.sendMessage("In development.");
    }
}
