package com.moebuff.discord.dao;

import com.moebuff.discord.entity.ChessRoom;
import com.moebuff.discord.entity.User;
import org.apache.ibatis.annotations.Param;

public interface ChessRoomDAO {
    ChessRoom insertRoom(ChessRoom room);
    void deleteRoom(int id);
    void deleteRoom(ChessRoom room);
    void setPlayer1ToNull(ChessRoom room);
    void setPlayer2ToNull(ChessRoom room);
    void addPlayer2ToRoom(@Param("player2") User player2, @Param("roomid") int roomid);
    void updateRoom(ChessRoom room);
    ChessRoom getRoom(int id);
    ChessRoom getRoom(User player);
}
