package com.moebuff.discord.entity;

public class Gift {
    private int id;
    private int price;
    private String name;
    private String charm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCharm() {
        return charm;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }

    @Override
    public String toString() {
        return name + ": $" + price + ", will give " + charm + " charm points to receiver.";
    }
}
