package com.moebuff.discord.listener;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageDeleteEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * 默认情况下，删除消息是没有任何提示的，这里借鉴了腾讯的做法
 *
 * @author muto
 */
public class Withdraw {

    @EventSubscriber
    public static void onMessageDelete(MessageDeleteEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;

        message.reply("You withdrew a message.");
        IGuild guild = message.getGuild();
        String msg = String.format("You withdrew ***%s*** in ***%s#%s***.",
                message.getContent(),
                guild == null ? "" : guild.getName(),
                message.getChannel().getName());
        user.getOrCreatePMChannel().sendMessage(msg);
    }

}
