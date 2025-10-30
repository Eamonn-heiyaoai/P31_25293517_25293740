package chessgame;

import javax.swing.*;

/**
 * 游戏控制器：处理落子、胜负判断、积分更新
 */
public class GameController {
    private Board board;
    private ChessPiece currentPiece;
    private JLabel statusLabel;
    private Player player1;
    private Player player2;
    private PlayerDAO playerDAO = new PlayerDAO(); // 数据访问对象

    public GameController(Board board, JLabel statusLabel, String player1Name, String player2Name) {
        this.board = board;
        this.statusLabel = statusLabel;
        this.currentPiece = ChessPiece.BLACK;

        try {
            player1 = playerDAO.getOrCreatePlayer(player1Name, ChessPiece.BLACK);
            player2 = playerDAO.getOrCreatePlayer(player2Name, ChessPiece.WHITE);
        } catch (java.sql.SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "⚠️ 数据库访问失败，使用默认玩家信息！\n" + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.WARNING_MESSAGE);
            player1 = new Player(player1Name, ChessPiece.BLACK);
            player2 = new Player(player2Name, ChessPiece.WHITE);
        }
        updateStatus();
    }

    public GameController(Board board, JLabel statusLabel) {
        this(board, statusLabel, "Player 1", "Player 2");
    }

    public void handleClick(int x, int y) {
        int cellSize = 40;
        int row = (y - 40 + cellSize / 2) / cellSize + 1;
        int col = (x - 40 + cellSize / 2) / cellSize + 1;

        if (board.placePiece(row, col, currentPiece)) {
            if (board.checkWin(row, col, currentPiece)) {
                Player winner = (currentPiece == ChessPiece.BLACK ? player1 : player2);
                Player loser  = (currentPiece == ChessPiece.BLACK ? player2 : player1);

                winner.winAgainst(loser);
                loser.loseAgainst(winner);

                try {
                    playerDAO.updateScore(winner);
                    playerDAO.updateScore(loser);
                } catch (java.sql.SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "更新数据库分数时出错！\n" + ex.getMessage(),
                            "数据库错误",
                            JOptionPane.ERROR_MESSAGE);
                }

                JOptionPane.showMessageDialog(null,
                        "🎉 恭喜 " + winner.getName() + " 获胜！\n\n" +
                                "🏆 当前积分：\n" +
                                winner.getName() + "：" + (int) winner.getScore() + "\n" +
                                loser.getName() + "：" + (int) loser.getScore());

                restartGame(null);
                return;
            }
            switchTurn();
        }
    }

    private void switchTurn() {
        currentPiece = (currentPiece == ChessPiece.BLACK) ? ChessPiece.WHITE : ChessPiece.BLACK;
        updateStatus();
    }

    private void updateStatus() {
        if (statusLabel != null) {
            String currentPlayer =
                    (currentPiece == ChessPiece.BLACK)
                            ? player1.getName() + "（黑棋）"
                            : player2.getName() + "（白棋）";
            statusLabel.setText("当前玩家：" + currentPlayer);
        }
    }

    public void restartGame(BoardPanel panel) {
        board = new Board();
        currentPiece = ChessPiece.BLACK;

        if (panel != null) {
            panel.setBoard(board);
            SwingUtilities.invokeLater(panel::repaint);
        }

        if (statusLabel != null) {
            statusLabel.setText("新的游戏开始！" + player1.getName() + "（黑棋）先行");
        }
    }
}
