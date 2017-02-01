package com.moebuff.discord.listener;

import com.moebuff.discord.tuling123.ITuring;
import com.moebuff.discord.tuling123.Issue;
import com.moebuff.discord.tuling123.TuringException;
import com.moebuff.discord.tuling123.TuringFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 * 对话机器人
 *
 * @author muto
 */
public class Dialogue {
    @EventSubscriber
    public static void onMention(MentionEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;

        IChannel channel = message.getChannel();
        Issue issue = TuringFactory.getIssue(user);
        issue.ask(message.getContent()
                .replace(user.mention(), "")//去除艾特
                .trim());

        ITuring turing = TuringFactory.getApi();
        try {
            turing.talk(issue);
            channel.sendMessage(issue.getAnswer());
        } catch (TuringException e) {
            message.getClient().changeStatus(Status.game("with your ❤"));
            channel.sendMessage(e.getMessage());
        }
    }
}
