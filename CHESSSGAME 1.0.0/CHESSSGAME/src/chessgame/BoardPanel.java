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
        setBackground(new Color(255, 204, 102));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller != null) {
                    controller.handleClick(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
        repaint();
    }

    public void setController(GameController controller) {
        this.controller = controller;

        // ✅ 移除旧监听器，重新绑定新的 controller
        for (MouseListener ml : getMouseListeners()) {
            removeMouseListener(ml);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller != null) {
                    controller.handleClick(e.getX(), e.getY());
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        ChessPiece[][] grid = board.getBoard();
        int size = grid.length;

        for (int i = 0; i < size; i++) {
            g.drawLine(40, 40 + i * CELL_SIZE, 40 + (size - 1) * CELL_SIZE, 40 + i * CELL_SIZE);
            g.drawLine(40 + i * CELL_SIZE, 40, 40 + i * CELL_SIZE, 40 + (size - 1) * CELL_SIZE);
        }

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
}
