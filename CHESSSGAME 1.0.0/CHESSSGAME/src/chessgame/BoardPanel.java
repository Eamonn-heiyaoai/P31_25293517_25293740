package chessgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BoardPanel extends JPanel {
    private final int CELL_SIZE = 40;
    private Board board;
    private GameController controller;

    public BoardPanel(Board board, GameController controller) {
        this.board = board;
        this.controller = controller;
        setBackground(new Color(255, 204, 102)); // 棋盘木色
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.handleClick(e.getX(), e.getY());
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ChessPiece[][] grid = board.getBoard();
        int size = grid.length;

        // 绘制棋盘格线
        for (int i = 0; i < size; i++) {
            g.drawLine(40, 40 + i * CELL_SIZE, 40 + (size - 1) * CELL_SIZE, 40 + i * CELL_SIZE);
            g.drawLine(40 + i * CELL_SIZE, 40, 40 + i * CELL_SIZE, 40 + (size - 1) * CELL_SIZE);
        }

        // 绘制棋子
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == ChessPiece.BLACK) {
                    g.setColor(Color.BLACK);
                    g.fillOval(40 + j * CELL_SIZE - 15, 40 + i * CELL_SIZE - 15, 30, 30);
                } else if (grid[i][j] == ChessPiece.WHITE) {
                    g.setColor(Color.WHITE);
                    g.fillOval(40 + j * CELL_SIZE - 15, 40 + i * CELL_SIZE - 15, 30, 30);
                    g.setColor(Color.BLACK);
                    g.drawOval(40 + j * CELL_SIZE - 15, 40 + i * CELL_SIZE - 15, 30, 30);
                }
            }
        }
    }
    public void setBoard(Board newBoard) {
    this.board = newBoard;
    repaint();  // 通知 Swing 立即重绘
    }

    public void refreshBoard() {
        repaint();
    }

    public int getCellSize() {
        return CELL_SIZE;
    }
}
