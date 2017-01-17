package com.moebuff.discord.listener;

import com.moebuff.discord.io.FF;
import com.moebuff.discord.io.FileHandle;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackQueueEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * OSU Support
 *
 * @author muto
 */
public class OSUListener {
    // Stores the last channel that the join command was sent from
    private static final Map<IGuild, IChannel> LAST_CHANNEL = new HashMap<>();

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

        if (split.length == 1) {
            help(channel);
            return;
        }

        String[] args = split.length > 2 ?
                Arrays.copyOfRange(split, 2, split.length) :
                new String[0];
        switch (split[1]) {
            case "join":
                LAST_CHANNEL.put(guild, channel);
                join(channel, user);
                break;
            case "queueUrl":
                queueUrl(channel, String.join(" ", args));
                break;
            case "queueFile":
                queueFile(channel, String.join(" ", args));
                break;
            case "play":
                player(channel).setPaused(false);
                break;
            case "pause":
                player(channel).setPaused(true);
                break;
            case "skip":
                player(channel).skip();
                break;
            case "vol":
                float volume = Integer.parseInt(args[0]) / 100.0f;
                if (volume < 0) volume = 0;
                if (volume > 1.5) volume = 1.5f;
                player(channel).setVolume(volume);
                break;
        }
    }

    private static void help(IChannel channel)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        channel.sendMessage("help test.");
    }

    // Track events
    //---------------------------------------------------------------------------------------------

    @EventSubscriber
    public static void onTrackQueue(TrackQueueEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IGuild guild = event.getPlayer().getGuild();
        String msg = String.format("Added **%s** to the playlist.", getTrackTitle(event));
        LAST_CHANNEL.get(guild).sendMessage(msg);
    }

    @EventSubscriber
    public void onTrackStart(TrackStartEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IGuild guild = event.getPlayer().getGuild();
        String msg = String.format("Now playing **%s**.", getTrackTitle(event));
        LAST_CHANNEL.get(guild).sendMessage(msg);
    }

    @EventSubscriber
    public static void onTrackFinish(TrackFinishEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IGuild guild = event.getPlayer().getGuild();
        IChannel channel = LAST_CHANNEL.get(guild);
        String msg = String.format("Finished playing **%s**.", getTrackTitle(event));
        channel.sendMessage(msg);

        if (event.getNewTrack() == null) {
            channel.sendMessage("The playlist is now empty.");
        }
    }

    // Audio player methods
    //---------------------------------------------------------------------------------------------

    private static void join(IChannel channel, IUser user)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        if (user.getConnectedVoiceChannels().size() < 1) {
            channel.sendMessage("You aren't in a voice channel!");
        } else {
            IVoiceChannel voice = user.getConnectedVoiceChannels().get(0);
            IUser our = channel.getClient().getOurUser();
            int limit = voice.getUserLimit();
            if (!voice.getModifiedPermissions(our).contains(Permissions.VOICE_CONNECT)) {
                channel.sendMessage("I can't join that voice channel!");
            } else if (limit > 0 && voice.getConnectedUsers().size() >= limit) {
                channel.sendMessage("That room is full!");
            } else {
                voice.join();
                String msg = String.format("Connected to **%s**.", voice.getName());
                channel.sendMessage(msg);
            }
        }
    }

    private static void queueUrl(IChannel channel, String spec)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        try {
            URL url = new URL(spec);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)"); //模拟ie浏览器
            AudioInputStream input = AudioSystem.getAudioInputStream(conn.getInputStream());
            setTrackTitle(player(channel).queue(input), url.getFile());
        } catch (MalformedURLException e) {
            channel.sendMessage("That URL is invalid!");
        } catch (IOException e) {
            channel.sendMessage("An IO exception occured: " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            channel.sendMessage("That type of file is not supported!");
        }
    }

    private static void queueFile(IChannel channel, String path)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        FileHandle audio = FF.SONGS.child(path);
        if (!audio.exists()) {
            channel.sendMessage("That file doesn't exist!");
        } else if (!audio.canRead()) {
            channel.sendMessage("I don't have access to that file!");
        } else {
            try {
                setTrackTitle(player(channel).queue(audio), path);
            } catch (IOException e) {
                channel.sendMessage("An IO exception occured: " + e.getMessage());
            } catch (UnsupportedAudioFileException e) {
                channel.sendMessage("That type of file is not supported!");
            }
        }
    }

    private static AudioPlayer player(IChannel channel) {
        return AudioPlayer.getAudioPlayerForGuild(channel.getGuild());
    }

    // Utility methods
    //---------------------------------------------------------------------------------------------

    private static String getTrackTitle(TrackQueueEvent event) {
        return getTrackTitle(event.getTrack());
    }

    private static String getTrackTitle(TrackStartEvent event) {
        return getTrackTitle(event.getTrack());
    }

    private static String getTrackTitle(TrackFinishEvent event) {
        return getTrackTitle(event.getOldTrack());
    }

    private static String getTrackTitle(AudioPlayer.Track track) {
        Map<String, Object> metadata = track.getMetadata();
        return metadata.containsKey("title") ? metadata.get("title")
                + "" : "Unknown Track";
    }

    private static void setTrackTitle(AudioPlayer.Track track, String title) {
        track.getMetadata().put("title", title);
    }
}
