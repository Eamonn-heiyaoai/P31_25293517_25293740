package chessgame;

import javax.swing.*;

public class GameController {
    private Board board;
    private ChessPiece currentPiece;
    private JLabel statusLabel; 
    
    public GameController(Board board, JLabel statusLabel) {
        this.board = board;
        this.statusLabel = statusLabel;
        this.currentPiece = ChessPiece.BLACK;
    }

    public void handleClick(int x, int y) {
        int cellSize = 40;
        int row = (y - 40 + cellSize / 2) / cellSize + 1;
        int col = (x - 40 + cellSize / 2) / cellSize + 1;

        if (board.placePiece(row, col, currentPiece)) {
            if (board.checkWin(row, col, currentPiece)) {
                JOptionPane.showMessageDialog(null, currentPiece + " 获胜！");
                restartGame(null);
                return;
            }
            switchTurn();
        }
    }

    private void switchTurn() {
    currentPiece = (currentPiece == ChessPiece.BLACK) ? ChessPiece.WHITE : ChessPiece.BLACK;
    statusLabel.setText((currentPiece == ChessPiece.BLACK ? "黑棋" : "白棋") + "回合");
    }
    public void restartGame(BoardPanel panel) {

    board = new Board();
    currentPiece = ChessPiece.BLACK;

    if (panel != null) {
        panel.setBoard(board);
    }

    if (statusLabel != null) {
        statusLabel.setText("新的游戏开始！黑棋先行");
    }
    }
}
