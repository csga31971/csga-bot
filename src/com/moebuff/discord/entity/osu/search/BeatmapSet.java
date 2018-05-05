package com.moebuff.discord.entity.osu.search;

import com.google.gson.Gson;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.Date;

public class BeatmapSet {
    private String artist;
    private Beatmap[] beatmaps;
    private double bpm;
    private boolean can_be_hyped;
    private Cover covers;
    private String creator;
    private boolean discussion_enabled;
    private int favourite_count;
    private boolean has_favourited;
    private boolean has_scores;
    private Hype hype;
    private int id;
    private String last_updated;
    private String legacy_thread_url;
    private Nomination nominations;
    private int play_count;
    private String preview_url;
    private int ranked;
    private String ranked_date;
    private String source;
    private String status;
    private boolean storyboard;
    private String submitted_date;
    private String tags;
    private String title;
    private int user_id;
    private boolean video;

    public String getArtist() {
        return artist;
    }

    public Beatmap[] getBeatmaps() {
        return beatmaps;
    }

    public Beatmap getBeatmap(int index) {
        return beatmaps[index];
    }

    public double getBpm() {
        return bpm;
    }

    public boolean CanBeHyped() {
        return can_be_hyped;
    }

    public Cover getCovers() {
        return covers;
    }

    public String getCreator() {
        return creator;
    }

    public boolean isDiscussionEnabled() {
        return discussion_enabled;
    }

    public int getFavouriteCount() {
        return favourite_count;
    }

    public boolean HasFavourited() {
        return has_favourited;
    }

    public boolean HasScores() {
        return has_scores;
    }

    public Hype getHype() {
        return hype;
    }

    public int getBeatMapSetId() {
        return id;
    }

    public Date getLastUpdatedDate() {
        return DateUtil.strToDate(last_updated);
    }

    public String getLegacyThreadUrl() {
        return legacy_thread_url;
    }

    public Nomination getNominations() {
        return nominations;
    }

    public int getPlayCount() {
        return play_count;
    }

    public String getPreviewUrl() {
        return preview_url;
    }

    public Date getRankedDate() {
        return DateUtil.strToDate(ranked_date);
    }

    public String getSource() {
        return source;
    }

    public String getStatus() {
        return status;
    }

    public boolean hasStoryboard() {
        return storyboard;
    }

    public Date getSubmittedDate() {
        return DateUtil.strToDate(submitted_date);
    }

    public String[] getTags() {
        return tags.split(" ");
    }

    public String getTitle() {
        return title;
    }

    public int getMapperId() {
        return user_id;
    }

    public boolean hasVideo() {
        return video;
    }

    public boolean isRanked(){
        return ranked == 1;
    }

    public String getBackGroundUrl(){
        return "https://b.ppy.sh/thumb/" + id + "l.jpg";
    }

    @Override
    public String toString(){
        return String.format("```%s - %s\n" +
                "Mapped by %s Profile: <%s>\n" +
                "Detail: <https://osu.ppy.sh/beatmapsets/%s>\n" +
                "Discussion Thread: <%s>",artist, title, creator, "https://osu.ppy.sh/user/" + user_id, id, legacy_thread_url);
    }

    public String toJsonString(){
        return new Gson().toJson(this);
    }

    public EmbedObject toEmbed(){
        return new EmbedBuilder().withColor(Color.WHITE)
                .withTitle(artist + " - " + title)
                .withAuthorIcon("https://a.ppy.sh/" + user_id)
                .withAuthorName(creator)
                .withAuthorUrl("https://osu.ppy.sh/user/" + user_id)
                .withThumbnail(getBackGroundUrl())
                .withUrl("https://osu.ppy.sh/beatmapsets/" + id)
                .appendDescription("Download: <https://osu.ppy.sh/beatmapsets/" + id + "/download>\n")
                .withFooterText("Discussion Thread: " + legacy_thread_url + "")
                .build();
    }

}
