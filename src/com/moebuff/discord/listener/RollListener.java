package com.moebuff.discord.listener;

import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * 随机数生成器
 *
 * @author muto
 */
public class RollListener {
    @EventSubscriber
    public static void onMessageReceivedEvent(MessageReceivedEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;

        String[] split = message.getContent().split(" ");
        if (!split[0].equals("!roll")) return;

        int num;
        if (split.length > 1) {
            num = RandomUtils.nextInt(0, Integer.parseInt(split[1]));
        } else {
            num = RandomUtils.nextInt(0, 100);
        }
        String result = String.format("%s rolls %s point(s).", user.mention(), num);
        message.getChannel().sendMessage(result);
    }
}
