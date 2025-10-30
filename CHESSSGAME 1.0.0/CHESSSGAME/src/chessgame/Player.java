package chessgame;

/**
 * 玩家类：包含名字、分数、棋子类型（黑或白）
 */
public class Player {
    private String name;
    private double score;      // ✅ 改为 double 支持积分
    private ChessPiece piece;  // ✅ 棋子类型

    // ✅ 构造函数：名字 + 棋子类型
    public Player(String name, ChessPiece piece) {
        this.name = name;
        this.piece = piece;
        this.score = 1000; // 默认初始分
    }

    // ✅ 构造函数：名字 + 初始分
    public Player(String name, double score) {
        this.name = name;
        this.score = score;
        this.piece = ChessPiece.EMPTY;
    }

    // ✅ getter & setter
    public String getName() {
        return name;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    // ✅ 旧兼容方法（int 版本）
    public void addScore(int s) {
        this.score += s;
    }

    // ✅ 新增 double 版本（防止类型不匹配）
    public void addScore(double s) {
        this.score += s;
    }

    // ✅ 胜利加分
    public void winAgainst(Player opponent) {
        double delta = Math.max(10, opponent.score * 0.05);
        this.score += delta;
    }

    // ✅ 失败扣分
    public void loseAgainst(Player opponent) {
        double delta = Math.max(10, this.score * 0.05);
        this.score = Math.max(100, this.score - delta);
    }
}
