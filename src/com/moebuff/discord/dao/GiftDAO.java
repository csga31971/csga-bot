package com.moebuff.discord.dao;

import com.moebuff.discord.entity.Gift;

import java.util.List;
import java.util.Map;

public interface GiftDAO {
    List<Gift> getGiftList();
    Gift getGift(int id);
    void addCharmPoint(Map map);// receiver, GiftHandler
    void subMoney(Map map);// sender, GiftHandler
    void addRichPoint(Map map);// sender, GiftHandler
    void addLog(Map map);// sender, receiver, GiftHandler
}
