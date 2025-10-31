package chessgame;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

/**
 * 游戏控制器：处理落子、胜负判断、积分更新
 */
public class GameController {
    private Board board;
    private ChessPiece currentPiece;
    private JLabel statusLabel;
    private Player player1;
    private Player player2;
    private PlayerDAO playerDAO = new PlayerDAO();
    private MoveHistoryDAO moveHistoryDAO = new MoveHistoryDAO();
    
    private int currentGameId = -1;  //当前游戏ID
    private int stepCount = 0;       //步数计数器
    private boolean autoSaveEnabled = true;

    public GameController(Board board, JLabel statusLabel, String player1Name, String player2Name) throws StandardException {
        this.board = board;
        this.statusLabel = statusLabel;
        this.currentPiece = ChessPiece.BLACK;

        try {
            player1 = playerDAO.getOrCreatePlayer(player1Name, ChessPiece.BLACK);
            player2 = playerDAO.getOrCreatePlayer(player2Name, ChessPiece.WHITE);
        } catch (SQLException ex) {
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

    public GameController(Board board, JLabel statusLabel) throws StandardException {
        this(board, statusLabel, "Player 1", "Player 2");
    }

    public void handleClick(int x, int y) throws StandardException {
        int cellSize = 40;
        int row = (y - 40 + cellSize / 2) / cellSize + 1;
        int col = (x - 40 + cellSize / 2) / cellSize + 1;
        
        if (board.placePiece(row, col, currentPiece)) {
            stepCount++;
            
            if (autoSaveEnabled) {
                try {
                    if (currentGameId == -1) {
                        GameDAO gameDAO = new GameDAO();
                        currentGameId = gameDAO.saveGameAndGetId(
                            player1.getName(), player2.getName(), currentPiece, board, stepCount);
                    } else {
                        GameDAO gameDAO = new GameDAO();
                        gameDAO.saveGameAndGetId(
                            player1.getName(), player2.getName(), currentPiece, board, stepCount);
                    }
                    String currentPlayerName = (currentPiece == ChessPiece.BLACK) ? 
                        player1.getName() : player2.getName();
                    moveHistoryDAO.saveMove(currentGameId, stepCount, currentPlayerName, 
                                           currentPiece, row, col);
                    
//                    System.out.println("✓ 保存第 " + stepCount + " 步: " + currentPlayerName + 
//                                     " 在 (" + row + "," + col + ")");
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.err.println("保存落子历史失败: " + ex.getMessage());
                }
            }
            
            if (board.checkWin(row, col, currentPiece)) {
                Player winner = (currentPiece == ChessPiece.BLACK ? player1 : player2);
                Player loser  = (currentPiece == ChessPiece.BLACK ? player2 : player1);

                winner.winAgainst(loser);
                loser.loseAgainst(winner);

                try {
                    playerDAO.updateScore(winner);
                    playerDAO.updateScore(loser);
                    
                    //游戏结束后删除存档和历史记录
                    if (currentGameId != -1) {
                        GameDAO gameDAO = new GameDAO();
                        gameDAO.deleteSavedGame(player1.getName(), player2.getName());
//                        System.out.println("✓ 游戏结束，已清除存档");
                    }
                    
                } catch (SQLException ex) {
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
            statusLabel.setText("当前玩家：" + currentPlayer + " - 第 " + (stepCount + 1) + " 手");
        }
    }

    public void restartGame(BoardPanel panel) {
        board = new Board();
        currentPiece = ChessPiece.BLACK;
        currentGameId = -1;
        stepCount = 0;

        if (panel != null) {
            panel.setBoard(board);
            panel.repaint();
        }
        if (statusLabel != null) {
            statusLabel.setText("新的游戏开始！" + player1.getName() + "（黑棋）先行");
        }
    }

    public Board getBoard() { return this.board; }
    public ChessPiece getCurrentPiece() { return this.currentPiece; }
    public String getPlayer1Name() { return this.player1.getName(); }
    public String getPlayer2Name() { return this.player2.getName(); }
    public int getStepCount() { return stepCount; }
    public int getCurrentGameId() { return currentGameId; }
    
    public void setCurrentPiece(ChessPiece piece) { 
        this.currentPiece = piece;
        updateStatus();
    }
    
    public void setCurrentGameId(int gameId) { 
        this.currentGameId = gameId; 
    }
    
    public void setStepCount(int stepCount) { 
        this.stepCount = stepCount;
        updateStatus();
    }
    
    //控制自动保存
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
    }
    
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }
    
    /**
     * 获取当前游戏的历史记录
     */
    public List<MoveRecord> getMoveHistory() throws StandardException {
        try {
            if (currentGameId != -1) {
                return moveHistoryDAO.getMoveHistory(currentGameId);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }
}