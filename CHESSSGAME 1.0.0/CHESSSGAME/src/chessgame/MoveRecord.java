/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import java.sql.Timestamp;

/**
 *
 * @author 34395
 */
public class MoveRecord {
    private int stepNumber;      // 步数
    private String playerName;   // 玩家名
    private ChessPiece piece;    // 棋子类型
    private int row;             // 行坐标
    private int col;             // 列坐标
    private Timestamp moveTime;  // 落子时间
    
    public MoveRecord(int stepNumber, String playerName, ChessPiece piece, 
                     int row, int col, Timestamp moveTime) {
        this.stepNumber = stepNumber;
        this.playerName = playerName;
        this.piece = piece;
        this.row = row;
        this.col = col;
        this.moveTime = moveTime;
    }
    
    public int getStepNumber() { return stepNumber; }
    public String getPlayerName() { return playerName; }
    public ChessPiece getPiece() { return piece; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public Timestamp getMoveTime() { return moveTime; }
    
    @Override
    public String toString() {
        String pieceSymbol = piece == ChessPiece.BLACK ? "⚫" : "⚪";
        return String.format("第%d手 %s %s (%d,%d)", 
                           stepNumber, playerName, pieceSymbol, row, col);
    }
}
