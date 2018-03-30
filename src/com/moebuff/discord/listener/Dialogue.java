package com.moebuff.discord.listener;

import com.moebuff.discord.Settings;
import com.moebuff.discord.tuling123.ITuring;
import com.moebuff.discord.tuling123.Issue;
import com.moebuff.discord.tuling123.TuringException;
import com.moebuff.discord.tuling123.TuringFactory;
import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import org.apache.commons.lang3.RandomUtils;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Collection;

/**
 * 对话机器人
 *
 * @author muto
 */
public class Dialogue {
    private static final Emoji[] EMOJIS;//表情符

    static {
        Collection<Emoji> all = EmojiManager.getAll();
        EMOJIS = all.toArray(new Emoji[all.size()]);
    }

    @EventSubscriber
    public static void onMention(MentionEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;

        IDiscordClient client = message.getClient();
        client.changePlayingText(" with " + user.getDisplayName(event.getGuild()));

        Issue issue = TuringFactory.getIssue(user);
        issue.ask(message.getContent());

        IChannel channel = message.getChannel();
        channel.setTypingStatus(true);//正在输入，回复后自动取消
        ITuring turing = TuringFactory.getApi();
        try {
            issue = turing.talk(issue);
            channel.sendMessage(String.format(
                    "%s %s",
                    EMOJIS[RandomUtils.nextInt(0, EMOJIS.length)].getUnicode(),
                    issue.getAnswer()
            ));
        } catch (TuringException e) {
            client.changePlayingText("with your ❤");
            channel.sendMessage(e.getMessage());
        }
    }
}
