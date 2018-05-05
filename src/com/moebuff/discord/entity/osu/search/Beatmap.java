package com.moebuff.discord.entity.osu.search;

import java.util.Date;

public class Beatmap {
    private double accuracy;
    private double ar;
    private int beatmapset_id;
    private String convert;
    private int count_circles;
    private int count_sliders;
    private int count_spinners;
    private int count_total;
    private double cs;
    private String deleted_at;
    private double difficulty_rating;
    private double drain;
    private int id;
    private String last_updated;
    private String mode;
    private int mode_int;
    private int passcount;
    private int playcount;
    private int ranked;
    private String status;
    private int total_length;
    private String url;
    private String version;

    public double getOD() {
        return accuracy;
    }

    public double getAR() {
        return ar;
    }

    public int getBeatmapSetId() {
        return beatmapset_id;
    }

    public int getCountCircles() {
        return count_circles;
    }

    public int getCountSliders() {
        return count_sliders;
    }

    public int getCountSpinners() {
        return count_spinners;
    }

    public int getCountTotal() {
        return count_total;
    }

    public double getCS() {
        return cs;
    }

    public Date getDeletedDate() {
        return  DateUtil.strToDate(deleted_at);
    }

    public double getStarRating() {
        return difficulty_rating;
    }

    public double getHP() {
        return drain;
    }

    public int getBeatmapId() {
        return id;
    }

    public Date getLastUpdatedDate() {
        return DateUtil.strToDate(last_updated);
    }

    public String getModeStr() {
        return mode;
    }

    public int getModeInt() {
        return mode_int;
    }

    public int getPassCount() {
        return passcount;
    }

    public int getPlayCount() {
        return playcount;
    }

    public boolean isRanked() {
        return ranked == 1;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalLength() {
        return total_length;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
