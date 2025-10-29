package chessgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame {
    private BoardPanel boardPanel;
    private GameController controller;

    public GameFrame() {
        setTitle("五子棋 - Chess Game");
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Board board = new Board();
        
        JLabel statusLabel = new JLabel("黑棋先行", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
    
        controller = new GameController(board, statusLabel);
        boardPanel = new BoardPanel(board, controller);

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // 添加菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("游戏");
        JMenuItem restartItem = new JMenuItem("重新开始");
        restartItem.addActionListener(e -> controller.restartGame(boardPanel));
        gameMenu.add(restartItem);
        
        JMenuItem backToMenuItem = new JMenuItem("🏠 返回主菜单");
        backToMenuItem.addActionListener(e -> {
            dispose();
            new MainMenuFrame().setVisible(true);
        });
        gameMenu.add(backToMenuItem);

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }
}

