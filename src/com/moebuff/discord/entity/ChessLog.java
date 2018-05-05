package com.moebuff.discord.entity;

public class ChessLog {
    private int id;
    private int color;
    private int x;
    private int y;
    private ChessRoom chessRoom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ChessRoom getChessRoom() {
        return chessRoom;
    }

    public void setChessRoom(ChessRoom chessRoom) {
        this.chessRoom = chessRoom;
    }
}
