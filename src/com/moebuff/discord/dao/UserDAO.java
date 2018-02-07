package com.moebuff.discord.dao;

import com.moebuff.discord.entity.User;

public interface UserDAO {
    User getUser(String userid);
    int getMoney(String userid);
    void addUser(User user);
}
