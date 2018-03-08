package com.moebuff.discord.entity;

public class ChessRoom {
    private int id;
    private User player1;
    private User player2;
    private String chessboard;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public String getChessboard() {
        return chessboard;
    }

    public void setChessboard(String chessboard) {
        this.chessboard = chessboard;
    }

    @Override
    public boolean equals(Object that){
        return this.id == ((ChessRoom)that).id;
    }

    @Override
    public String toString(){

        if(new Integer(id) == null){
            return "null";
        }
        String result = id + ": " + "[";
        if(player1 == null){
            result += "player1:null ], [";
        }else {
            result += player1.getUsername() + ": " + player1.getUserid() + "], [";
        }

        if(player2 == null){
            result += "player2:null ]";
        }else {
            result += player2.getUsername() + ": " + player2.getUserid() + "]";
        }
        return result;
    }
}
