package chessgame;


/**
 * 玩家类
 */
public class Player {
    private String name;
    private int score;
    private ChessPiece piece;

    public Player(String name, ChessPiece piece) {
        this.name = name;
        this.piece = piece;
        this.score = 0;
    }

    public String getName() {
        return this.name;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int s) {
        score += s;
    }
    
}
