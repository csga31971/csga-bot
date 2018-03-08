package com.moebuff.discord.service;

import com.google.gson.Gson;
import com.moebuff.discord.dao.ChessRoomDAO;
import com.moebuff.discord.entity.ChessRoom;
import com.moebuff.discord.utils.Log;
import com.moebuff.discord.utils.MybatisUtil;
import org.apache.ibatis.session.SqlSession;
import sx.blah.discord.handle.obj.IUser;

import java.util.Map;

public class ChessRoomManager {
    private static SqlSession session;
    //private static ChessRoomDAO dao;
    /*static{
        session = MybatisUtil.getSession();
        dao = session.getMapper(ChessRoomDAO.class);
    }*/

    public static ChessRoom createRoom(IUser player1){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);

        //如果已经在房间里
        ChessRoom joinedRoom = getRoomByUser(player1);
        Log.getLogger().info(joinedRoom.toString());
        if(joinedRoom.getId() != -1){
            return null;
        }

        ChessRoom room = new ChessRoom();
        room.setPlayer1(UserManager.getUser(player1));
        room.setChessboard(player1.getStringID());
        dao.insertRoom(room);
        room = getRoomByUser(player1);
        session.close();
        return room;
    }

    public static ChessRoom getRoomByUser(IUser user){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        Map roomMap = dao.findRoomByUser(UserManager.getUser(user));

        Log.getLogger().info(new Gson().toJson(roomMap));

        ChessRoom room = new ChessRoom();
        if(roomMap == null){
            //没有在房间里，id设置成-1
            room.setId(-1);
            return room;
        }else{
            room.setId((int)roomMap.get("id"));
            room.setPlayer1(UserManager.getUser((String)roomMap.get("player1")));
            room.setPlayer2(UserManager.getUser((String)roomMap.get("player2")));
            room.setChessboard((String)roomMap.get("chessboard"));
            Log.getLogger().info(user.getName());
            Log.getLogger().info(room.toString());
            return room;
        }
    }

    public static void quitRoom(IUser user) {
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        int color = getColor(user);
        ChessRoom room = getRoomByUser(user);
        if (color == 1) {
            dao.setPlayer1ToNull(room);
            //session.commit();
            session.close();
        } else if (color == -1){
            dao.setPlayer2ToNull(room);
            //session.commit();
            session.close();
        }
        else
            return;
    }

    public static int joinRoom(IUser user, int roomid){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);

        //-1: 已经在其他房间里
        //0: 已经在要加入的房间里
        //1: 成功

        ChessRoom joinedRoom = getRoomByUser(user);
        if(joinedRoom != null && !joinedRoom.equals(dao.findRoomById(roomid))){
            return -1;
        }
        if(joinedRoom.equals(dao.findRoomById(roomid))){
            return 0;
        }
        dao.addPlayer2ToRoom(UserManager.getUser(user), roomid);
        //session.commit();
        session.close();
        return 1;
    }



    public static int getColor(IUser user){
        ChessRoom room = getRoomByUser(user);
        if(room == null){
            return 0;
        }
        if(room.getPlayer1().equals(UserManager.getUser(user))){
            return 1;
        }
        if(room.getPlayer2().equals(UserManager.getUser(user))){
            return -1;
        }
        return 0;
    }
}
