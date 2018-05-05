package com.moebuff.discord.service;

import com.moebuff.discord.dao.UserDAO;
import com.moebuff.discord.entity.User;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class UserManager {
    private static SqlSession session;

    public static User getUser(String userid){
        session = MybatisUtil.getSession();
        UserDAO userdao = session.getMapper(UserDAO.class);
        return userdao.getUser(userid);
    }

    public static User getUser(IUser iUser){
        return getUser(iUser.getStringID());
    }

    public static void addUser(IUser iUser){
        session = MybatisUtil.getSession();
        UserDAO userdao = session.getMapper(UserDAO.class);
        User user = new User(iUser.getStringID(), iUser.getName());
        userdao.addUser(user);
        session.commit();
        session.close();
    }

    public static String getInfo(IUser iUser){
        User user = getUser(iUser.getStringID());
        return user.toString();
    }

    public static void initUser(IGuild guild){
        List<IUser> userList = guild.getUsers();
        for(IUser iUser:userList){
            User user = UserManager.getUser(iUser.getStringID());
            if(user == null){
                addUser(iUser);
            }
        }
    }
}
