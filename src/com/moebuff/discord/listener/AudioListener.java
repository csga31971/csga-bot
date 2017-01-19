package com.moebuff.discord.listener;

import com.moebuff.discord.io.FF;
import com.moebuff.discord.io.FileHandle;
import com.moebuff.discord.utils.UnhandledException;
import org.apache.commons.lang3.time.FastDateFormat;
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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音频指令
 *
 * @author muto
 */
public class AudioListener {
    // Stores the last channel that the join command was sent from
    private static final Map<IGuild, IChannel> LAST_CHANNEL = new HashMap<>();

    private static final String CHROME
            = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/55.0.2883.87 Safari/537.36";
    private static final FastDateFormat TIME = FastDateFormat.getInstance("HH:mm:ss.SSS");

    @EventSubscriber
    public static void onMessage(MessageReceivedEvent event)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        IMessage message = event.getMessage();
        IUser user = message.getAuthor();
        if (user.isBot()) return;

        IChannel channel = message.getChannel();
        IGuild guild = message.getGuild();
        String[] split = message.getContent().split(" ");

        String cmd = split[0];
        if (!cmd.startsWith("!")) return;
        String[] args = split.length > 1 ?
                Arrays.copyOfRange(split, 1, split.length) :
                new String[0];
        switch (cmd.substring(1)) {
            case "join":
                LAST_CHANNEL.put(guild, channel);
                join(channel, user);
                break;
            case "leave":
                player(channel).clean();
                LAST_CHANNEL.remove(guild);
                leave(channel, user);
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
                float volume = 100;
                if (args.length == 0) {
                    channel.sendMessage("volume ∈ [0,150], default is 100.");
                } else {
                    volume = Integer.parseInt(args[0]) / 100.0f;
                    if (volume < 0) volume = 0;
                    if (volume > 1.5) volume = 1.5f;
                }
                player(channel).setVolume(volume);
                break;
            case "list":
                List<AudioPlayer.Track> list = player(channel).getPlaylist();
                if (list.size() == 0) {
                    channel.sendMessage("No currently playing content.");
                } else
                    list.forEach(t -> {
                        int index = list.indexOf(t);
                        String title = (String) t.getMetadata().get("title");
                        String msg = String.format("%s.%s [%s] %s",
                                index + 1,
                                title,
                                TIME.format(t.getTotalTrackTime()),
                                index == 0 ? "Playing" : "Wait");

                        try {
                            channel.sendMessage(msg);
                        } catch (Exception e) {
                            throw new UnhandledException(e);
                        }
                    });
                break;
        }
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
            IUser our = channel.getClient().getOurUser();
            IVoiceChannel voice = user.getConnectedVoiceChannels().get(0);
            int userLimit = voice.getUserLimit();
            if (!voice.getModifiedPermissions(our).contains(Permissions.VOICE_CONNECT)) {
                channel.sendMessage("I can't join that voice channel!");
            } else if (userLimit > 0 && voice.getConnectedUsers().size() >= userLimit) {
                channel.sendMessage("That room is full!");
            } else {
                voice.join();
                String msg = String.format("Connected to **%s**.", voice.getName());
                channel.sendMessage(msg);
            }
        }
    }

    private static void leave(IChannel channel, IUser user)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        List<IVoiceChannel> connections = user.getConnectedVoiceChannels();
        if (connections.size() < 1) {
            channel.sendMessage("I didn't join any channels!");
        } else {
            IVoiceChannel voice = connections.get(0);
            if (voice.isConnected()) {
                voice.leave();
                String msg = String.format("Has left the **%s**.", voice.getName());
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
            conn.setRequestProperty("User-Agent", CHROME);
            queue(channel, conn.getInputStream(), url.getFile(), "url", url);
        } catch (MalformedURLException e) {
            channel.sendMessage("That URL is invalid!");
        } catch (IOException e) {
            channel.sendMessage("Connection failed: " + e.getMessage());
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
            queue(channel, audio.read(), path, "file", audio);
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

    private static void queue(IChannel channel, InputStream stream, String title,
                              String key, Object value)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(stream);
            AudioPlayer.Track track = player(channel).queue(audio);
            Map<String, Object> metadata = track.getMetadata();
            metadata.put("title", title);
            if (key != null) {
                metadata.put(key, value);
            }
        } catch (IOException e) {
            channel.sendMessage("An IO exception occured: " + e.getMessage());
        } catch (UnsupportedAudioFileException e) {
            channel.sendMessage("That type of file is not supported!");
        }
    }

    private static void queue(IChannel channel, InputStream stream, String title)
            throws RateLimitException, DiscordException, MissingPermissionsException {
        queue(channel, stream, title, null, null);
    }
}
