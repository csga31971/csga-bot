package com.moebuff.discord.service;

import com.moebuff.discord.dao.ChessRoomDAO;
import com.moebuff.discord.entity.ChessRoom;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import sx.blah.discord.handle.obj.IUser;

public class ChessRoomManager {
    private static SqlSession session;

    public static ChessRoom createRoom(IUser player1){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        ChessRoom room = new ChessRoom();
        room.setPlayer1(UserManager.getUser(player1));
        room.setChessboard(player1.getStringID());
        dao.insertRoom(room);
        room = dao.getRoom(UserManager.getUser(player1));
        return room;
    }

    public static void quitRoom(IUser user){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        int color = getColor(user);
        ChessRoom room = getRoomByUser(user);
        if(color == 1)
            dao.setPlayer1ToNull(room);
        else if(color == -1)
            dao.setPlayer2ToNull(room);
        else
            return;
    }

    public static void joinRoom(IUser user, int roomid){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        dao.addPlayer2ToRoom(UserManager.getUser(user), roomid);
    }

    public static ChessRoom getRoomByUser(IUser user){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        return dao.getRoom(UserManager.getUser(user));
    }

    public static int getColor(IUser user){
        ChessRoom room = getRoomByUser(user);
        if(room == null){
            return 0;
        }
        if(room.getPlayer1() == UserManager.getUser(user)){
            return 1;
        }
        if(room.getPlayer2() == UserManager.getUser(user)){
            return -1;
        }
        return 0;
    }
}
