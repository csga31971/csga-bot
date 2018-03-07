package com.moebuff.discord.dao;

import com.moebuff.discord.entity.ChessLog;
import com.moebuff.discord.entity.ChessRoom;

import java.util.List;

public interface ChessLogDAO {
    //void insert(ChessLog chessLog);
    //void insert(int color, int x, int y, ChessRoom room);
    void insert(int color, int x, int y, int chessroomid);
    //List<ChessLog> list();
}
