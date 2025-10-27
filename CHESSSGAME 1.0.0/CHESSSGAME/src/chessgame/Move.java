package chessgame;

/**
 * 移动类
 * 记录一次走棋的信息
 */
public class Move {
    private int row;
    private int col;
    private ChessPiece piece;

    public Move(int row, int col, ChessPiece piece) {
        this.row = row;
        this.col = col;
        this.piece = piece;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ChessPiece getPiece() {
        return piece;
    }
}
