package com.moebuff.discord.service;

import com.moebuff.discord.dao.ChessLogDAO;
import com.moebuff.discord.entity.ChessRoom;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;

public class ChessLogManager {

    private static SqlSession session;

    public static void addLog(int color, int x, int y, ChessRoom room){
        session = MybatisUtil.getSession();
        ChessLogDAO dao = session.getMapper(ChessLogDAO.class);
        dao.insert(color,x,y,room.getId());
        session.commit();
        session.close();
    }

}
