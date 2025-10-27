package chessgame;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏历史记录类
 */
public class GameHistory {
    private List<Move> moves;

    public GameHistory() {
        moves = new ArrayList<>();
    }

    public void addMove(Move move) {
        moves.add(move);
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void printHistory() {
        // 打印历史记录
    }
}
