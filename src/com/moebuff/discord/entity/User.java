package com.moebuff.discord.entity;

public class User {
    private String userid;
    private String username;
    private int charmPoint;
    private int money;
    private int richPoint;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCharmPoint() {
        return charmPoint;
    }

    public void setCharmPoint(int charmPoint) {
        this.charmPoint = charmPoint;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getRichPoint() {
        return richPoint;
    }

    public void setRichPoint(int richPoint) {
        this.richPoint = richPoint;
    }

    @Override
    public String toString(){
        return username + ": money = " + money + "; charm points = " + charmPoint + " ; rich points = " + richPoint + ".";
    }

    @Override
    public boolean equals(Object that){
        return this.userid == ((User)that).getUserid();
    }

    public User(){
        money = 100;
        richPoint = 0;
        charmPoint = 0;
    }
    public User(String userid){
        this();
        this.userid = userid;
    }
    public User(String userid, String username){
        this(userid);
        this.username = username;
    }
}
