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
        client.changePlayingText(" with " + user.getNicknameForGuild(event.getGuild()));

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

    /*
    事实上每次登陆都会触发，有点扰民，先去掉了
    @EventSubscriber
    public static void onJoinGuild(GuildCreateEvent event)
            throws MissingPermissionsException, RateLimitException, DiscordException{
        //also on bot login
        IGuild guild = event.getGuild();
        List<IChannel> channelList = guild.getChannelsByName("general");
        if(channelList.size()>0){
            IChannel generalChannel = channelList.get(0);
            generalChannel.sendMessage("O-ooooooo-aaaa-e-a-e-i-e-a-Joooooooo");
        }

    }
    */

    @EventSubscriber
    public static void onWhatReceive(MessageReceivedEvent event){
        IMessage message = event.getMessage();
        IChannel channel = message.getChannel();

        String content = message.getContent();
        String whatPattern = "[.?。？]*(你说)?(what|waht|啥|什么)[.?。？]*";

        if(content.toLowerCase().matches(whatPattern)){
            channel.setTypingStatus(true);//正在输入，回复后自动取消
            MessageHistory messageHistory = channel.getMessageHistory();
            IMessage repeatMessage = messageHistory.get(1);
            if(Settings.BOT_ID_STRING.equals(repeatMessage.getAuthor().getStringID())){
                channel.sendMessage("You should read my words more carefully! ( ╬◣ 益◢)y");
            }else{
                /*test
                channel.sendMessage(
                        "0:" + messageHistory.get(0) + "\n" +
                        "1:" + messageHistory.get(1) + "\n" +
                        "2:" + messageHistory.get(2) + "\n" +
                        "3:" + messageHistory.get(3) + "\n"

                );*/
                channel.sendMessage("**" + repeatMessage.getContent() + "**");
            }
        }
    }
}
