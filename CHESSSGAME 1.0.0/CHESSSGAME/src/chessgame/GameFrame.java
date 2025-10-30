package chessgame;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private BoardPanel boardPanel;
    private GameController controller;
    private JLabel statusLabel;
    private String player1Name;
    private String player2Name;
    private boolean isGameSaved = false;

    public GameFrame(String player1, String player2) {
        this.player1Name = player1;
        this.player2Name = player2;
        initGame();
        setupWindowListener();
    }

    public GameFrame() {
        this("Player 1", "Player 2");
    }

    private void initGame() {
        setTitle("五子棋 - " + player1Name + " vs " + player2Name);
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        Board board = new Board();
        statusLabel = new JLabel("当前玩家：" + player1Name + "（黑棋）", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        controller = new GameController(board, statusLabel, player1Name, player2Name);
        boardPanel = new BoardPanel(board, controller);

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== 菜单栏 =====
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("游戏");

        // 重新开始
        JMenuItem restartItem = new JMenuItem("🔄 重新开始");
        restartItem.addActionListener(e -> {
            if (!isGameSaved) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "确定要重新开始吗？当前进度将丢失！",
                    "重新开始",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            controller.restartGame(boardPanel);
            isGameSaved = false; // ✅ 重开后游戏未保存
        });
        gameMenu.add(restartItem);

        //保存游戏
        JMenuItem saveItem = new JMenuItem("💾 保存游戏");
        saveItem.addActionListener(e -> saveCurrentGame());
        gameMenu.add(saveItem);

        gameMenu.addSeparator();

        //返回主菜单
        JMenuItem backToMenuItem = new JMenuItem("🏠 返回主菜单");
        backToMenuItem.addActionListener(e -> askSaveBeforeExit());
        gameMenu.add(backToMenuItem);

        //退出
        JMenuItem exitItem = new JMenuItem("❌ 退出游戏");
        exitItem.addActionListener(e -> askSaveBeforeExit());
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    //保存当前游戏进度
    private void saveCurrentGame() {
    try {
        GameDAO gameDAO = new GameDAO();
        gameDAO.saveGame(
                player1Name,
                player2Name,
                controller.getCurrentPiece(),
                controller.getBoard()
        );
        
        isGameSaved = true; //修改标志位

        JOptionPane.showMessageDialog(
                this,
                "游戏进度已成功保存！\n\n下次开始游戏时可以选择继续。",
                "保存成功",
                JOptionPane.INFORMATION_MESSAGE
        );

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                this,
                "保存失败：" + ex.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

    //退出前询问是否保存
    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                askSaveBeforeExit();
            }
        });
    }

    //询问保存并退出
    private void askSaveBeforeExit() {
    //如果已经保存过，则不再提示
    if (isGameSaved) {
        backToMainMenu();
        return;
    }

    int option = JOptionPane.showConfirmDialog(
            this,
            "是否保存当前游戏进度？",
            "退出游戏",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
    );

    if (option == JOptionPane.CANCEL_OPTION) {
        return; // 取消，不退出
    }

    if (option == JOptionPane.YES_OPTION) {
        saveCurrentGame();
    }

    backToMainMenu();
}

    //返回主菜单
    private void backToMainMenu() {
        dispose();
        new MainMenuFrame().setVisible(true);
    }

    //加载存档
    public void loadSavedGame(SavedGame saved) {
        this.boardPanel.setBoard(saved.board);
        this.controller = new GameController(saved.board, statusLabel, saved.player1, saved.player2);
        this.controller.setCurrentPiece(saved.currentPiece);
        
        String currentPlayerName = (saved.currentPiece == ChessPiece.BLACK) ? saved.player1 : saved.player2;
        String pieceName = (saved.currentPiece == ChessPiece.BLACK) ? "黑棋" : "白棋";
        
        statusLabel.setText("继续游戏 - 当前玩家：" + currentPlayerName + "（" + pieceName + "）");
        boardPanel.repaint();
        
        JOptionPane.showMessageDialog(
                this,
                "存档加载成功！\n\n继续游戏：" + saved.player1 + " vs " + saved.player2,
                "加载成功",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    //清除保存标志位
    public void markGameAsUnsaved() {
        isGameSaved = false;
    }
}