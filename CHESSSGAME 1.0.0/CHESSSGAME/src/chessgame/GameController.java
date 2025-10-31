package chessgame;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

/**
 * Game Controller: Handles moves, win/loss detection, and score updates
 */
public class GameController {
    private Board board;
    private ChessPiece currentPiece;
    private JLabel statusLabel;
    private Player player1;
    private Player player2;
    private PlayerDAO playerDAO = new PlayerDAO();
    private MoveHistoryDAO moveHistoryDAO = new MoveHistoryDAO();

    private int currentGameId = -1;
    private int stepCount = 0;
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
                    "⚠️ Database access failed. Using default player information!\n" + ex.getMessage(),
                    "Database Error",
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

//                    System.out.println("✓ Saved move " + stepCount + ": " + currentPlayerName +
//                                     " at (" + row + "," + col + ")");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.err.println("Failed to save move history: " + ex.getMessage());
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

                    if (currentGameId != -1) {
                        GameDAO gameDAO = new GameDAO();
                        gameDAO.deleteSavedGame(player1.getName(), player2.getName());
//                        System.out.println("✓ Game over. Save data cleared.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Error updating database scores!\n" + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                JOptionPane.showMessageDialog(null,
                        "🎉 Congratulations " + winner.getName() + " wins!\n\n" +
                                "🏆 Current Scores:\n" +
                                winner.getName() + ": " + (int) winner.getScore() + "\n" +
                                loser.getName() + ": " + (int) loser.getScore());

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
                            ? player1.getName() + " (Black)"
                            : player2.getName() + " (White)";
            statusLabel.setText("Current Player: " + currentPlayer + " - Move " + (stepCount + 1));
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
            statusLabel.setText("New game started! " + player1.getName() + " (Black) goes first.");
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

    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
    }

    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

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
