package com.moebuff.discord.service;

import com.google.gson.Gson;
import com.moebuff.discord.dao.ChessRoomDAO;
import com.moebuff.discord.entity.ChessRoom;
import com.moebuff.discord.entity.User;
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
        room.setNow(1);
        dao.insertRoom(room);
        room = getRoomByUser(player1);
        session.close();
        return room;
    }

    public static int joinRoom(IUser user, int roomid){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);

        //-2: 没roomid对应的房间
        //-1: 已经在其他房间里
        //0: 已经在要加入的房间里
        //1: 成功

        ChessRoom joinedRoom = getRoomByUser(user);
        if(joinedRoom.getId() == -1){
            dao.addPlayer2ToRoom(UserManager.getUser(user), roomid);
            session.close();
            return 1;
        }
        ChessRoom toJoinRoom = getRoomById(roomid);
        if(toJoinRoom.getId() == -1){
            return -2;
        }
        if(joinedRoom.getId() != -1 && !joinedRoom.equals(toJoinRoom)){
            return -1;
        }
        if(joinedRoom.getId() != -1 && joinedRoom.equals(toJoinRoom)){
            return 0;
        }
        Log.getLogger().debug("wtf");
        return -3;
    }

    public static int quitRoom(IUser user) {
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        ChessRoom room = getRoomByUser(user);
        User user_ = UserManager.getUser(user);

        //没在房间里
        if(room.getId() == -1){
            return 0;
        }
        //房主美丽，房间boom
        if(room.getPlayer1().equals(user_)){
            Log.getLogger().info("deleted " + room.toString());
            dao.deleteRoom(room);
            return 1;
        }
        //溜了
        if(room.getPlayer2().equals(user_)){
            Log.getLogger().info("player2 in " + room.toString() + " left");
            dao.setPlayer2ToNull(room);
            return -1;
        }
        //wtf
        Log.getLogger().debug("wtf");
        return -2;
    }



    public static int getColor(IUser user){
        ChessRoom room = getRoomByUser(user);
        User user_ = UserManager.getUser(user);
        Log.getLogger().info(room.toString());
        Log.getLogger().info(user_.toString());
        if(room.getId() == -1){
            return 0;
        }
        if(room.getPlayer1().equals(user_)){
            Log.getLogger().info("red");
            return 1;
        }
        if(room.getPlayer2().equals(user_)){
            Log.getLogger().info("black");
            return 2;
        }
        Log.getLogger().info("wtf");
        return 0;
    }

    public static void toggleTurn(ChessRoom room){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        dao.updateNow(room);
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
            room.setNow((int)roomMap.get("now"));
            Log.getLogger().info(user.getName());
            Log.getLogger().info(room.toString());
            return room;
        }
    }

    public static ChessRoom getRoomById(int roomid){
        session = MybatisUtil.getSession();
        ChessRoomDAO dao = session.getMapper(ChessRoomDAO.class);
        Map roomMap = dao.findRoomById(roomid);

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
            room.setNow((int)roomMap.get("now"));
            Log.getLogger().info(room.toString());
            return room;
        }
    }
}
