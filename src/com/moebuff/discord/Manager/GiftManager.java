package com.moebuff.discord.service;

import com.moebuff.discord.dao.GiftDAO;
import com.moebuff.discord.dao.UserDAO;
import com.moebuff.discord.entity.Gift;
import com.moebuff.discord.entity.User;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import sx.blah.discord.handle.obj.IUser;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GiftManager {

    private static SqlSession session;

    public static Gift getGift(int id){
        session = MybatisUtil.getSession();
        GiftDAO giftdao = session.getMapper(GiftDAO.class);
        return giftdao.getGift(id);
    }

    public static List<Gift> getGiftList(){
        SqlSession session = MybatisUtil.getSession();
        GiftDAO dao = session.getMapper(GiftDAO.class);
        return dao.getGiftList();
    }

    public static String sendGift(IUser sender, IUser receiver, Gift g){
        session = MybatisUtil.getSession();
        GiftDAO giftdao = session.getMapper(GiftDAO.class);
        UserDAO userdao = session.getMapper(UserDAO.class);
        User u_sender = userdao.getUser(sender.getStringID());
        User u_receiver = userdao.getUser(receiver.getStringID());
        if(u_sender.getMoney() < g.getPrice()){
            String msg = "not enough money, you have $" + u_sender.getMoney() + " now.";
            return msg;
        }
        Map map = new HashMap();
        map.put("sender",u_sender);
        map.put("receiver",u_receiver);
        map.put("gift",g);
        map.put("date",new Date());
        giftdao.addCharmPoint(map);
        giftdao.addRichPoint(map);
        giftdao.subMoney(map);
        giftdao.addLog(map);

        session.commit();
        session.close();
        String msg = sender.getName() + " sent 1 " + g.getName() + " to " ;
        return msg;
    }
}
