package chessgame;

/**
 * 棋子子类
 * 实现不同棋子的规则
 */
public enum ChessPiece {
    EMPTY('+'),
    BLACK('X'),
    WHITE('O');

    private final char symbol;

    ChessPiece(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }
    
}
