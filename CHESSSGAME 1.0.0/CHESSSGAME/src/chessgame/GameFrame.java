package chessgame;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.iapi.error.StandardException;

public class GameFrame extends JFrame {
    private BoardPanel boardPanel;
    private GameController controller;
    private HistoryPanel historyPanel;
    private JLabel statusLabel;
    private String player1Name;
    private String player2Name;
    private boolean isGameSaved = false;

    public GameFrame(String player1, String player2) throws StandardException {
        this.player1Name = player1;
        this.player2Name = player2;
        initGame();
        setupWindowListener();
    }

    public GameFrame() throws StandardException {
        this("Player 1", "Player 2");
    }

    private void initGame() throws StandardException {
        setTitle("五子棋 - " + player1Name + " vs " + player2Name);
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        Board board = new Board();
        statusLabel = new JLabel("当前玩家：" + player1Name + "（黑棋）- 第1手", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        controller = new GameController(board, statusLabel, player1Name, player2Name);
        boardPanel = new BoardPanel(board, controller);
        
        // 创建历史面板
        historyPanel = new HistoryPanel(controller);

        // 使用BorderLayout布局
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        centerPanel.add(historyPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== 菜单栏 =====
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("游戏");

        //重新开始
        JMenuItem restartItem = new JMenuItem("🔄 重新开始");
        restartItem.addActionListener(e -> {
            if (!isGameSaved && controller.getStepCount() > 0) {
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
            
            try {
                // ✅ 删除旧的存档和历史记录
                deleteCurrentGameData();
            } catch (StandardException ex) {
                Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            controller.restartGame(boardPanel);
            historyPanel.updateHistory();
            isGameSaved = false;
        });
        gameMenu.add(restartItem);

        // 保存游戏
        JMenuItem saveItem = new JMenuItem("💾 保存游戏");
        saveItem.addActionListener(e -> saveCurrentGame());
        gameMenu.add(saveItem);

        gameMenu.addSeparator();

        // 返回主菜单
        JMenuItem backToMenuItem = new JMenuItem("🏠 返回主菜单");
        backToMenuItem.addActionListener(e -> {
            try {
                askSaveBeforeExit();
            } catch (StandardException ex) {
                Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        gameMenu.add(backToMenuItem);

        // 退出
        JMenuItem exitItem = new JMenuItem("❌ 退出游戏");
        exitItem.addActionListener(e -> {
            try {
                askSaveBeforeExit();
            } catch (StandardException ex) {
                Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    /**
     * 保存当前游戏进度
     */
    private void saveCurrentGame() {
        try {
            GameDAO gameDAO = new GameDAO();
            int gameId = gameDAO.saveGameAndGetId(
                    player1Name,
                    player2Name,
                    controller.getCurrentPiece(),
                    controller.getBoard(),
                    controller.getStepCount()
            );
            
            //更新 controller 的 gameId
            controller.setCurrentGameId(gameId);
            isGameSaved = true;

            JOptionPane.showMessageDialog(
                    this,
                    "✅ 游戏进度已成功保存！\n\n下次开始游戏时可以选择继续。",
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

    /**
     * 退出前询问是否保存
     */
    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                try {
                    askSaveBeforeExit();
                } catch (StandardException ex) {
                    Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    /**
     * 询问保存并退出
     */
    private void askSaveBeforeExit() throws StandardException {
        //如果没有落子或已经保存过，直接退出
        if (controller.getStepCount() == 0) {
            backToMainMenu();
            return;
        }
        
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
            // ✅ 选择保存
            saveCurrentGame();
        } else if (option == JOptionPane.NO_OPTION) {
            // ✅ 选择不保存，删除自动保存的数据
            deleteCurrentGameData();
        }

        backToMainMenu();
    }

    /**
     * 删除当前游戏的存档和历史记录
     */
    private void deleteCurrentGameData() throws StandardException {
        try {
            GameDAO gameDAO = new GameDAO();
            
            //如果有游戏ID，说明有自动保存的数据，需要删除
            if (controller.getCurrentGameId() != -1) {
                gameDAO.deleteSavedGame(player1Name, player2Name);
                System.out.println("✓ 已删除未保存的游戏数据");
            }
            
        } catch (SQLException ex) {
            System.err.println("删除游戏数据时出错: " + ex.getMessage());
        }
    }

    /**
     * 返回主菜单
     */
    private void backToMainMenu() {
        dispose();
        new MainMenuFrame().setVisible(true);
    }

    /**
     * 加载存档
     */
    public void loadSavedGame(SavedGame saved) throws StandardException {
        // 重新创建控制器
        this.controller = new GameController(saved.board, statusLabel, saved.player1, saved.player2);
        this.controller.setCurrentPiece(saved.currentPiece);
        this.controller.setStepCount(saved.stepCount);
        this.controller.setCurrentGameId(saved.gameId);

        // 更新界面
        this.boardPanel.setController(this.controller);
        this.boardPanel.setBoard(saved.board);
        
        // 更新历史面板
        this.historyPanel.setController(this.controller);
        this.historyPanel.updateHistory();

        String currentPlayerName = (saved.currentPiece == ChessPiece.BLACK) ? saved.player1 : saved.player2;
        String pieceName = (saved.currentPiece == ChessPiece.BLACK) ? "黑棋" : "白棋";
        statusLabel.setText("继续游戏 - 当前玩家：" + currentPlayerName + "（" + pieceName + "）- 第" + (saved.stepCount + 1) + "手");

        //标记为已保存
        isGameSaved = true;
        
        boardPanel.repaint();

        JOptionPane.showMessageDialog(
                this,
                "存档加载成功！\n\n继续游戏：" + saved.player1 + " vs " + saved.player2 + 
                "\n当前步数：" + saved.stepCount,
                "加载成功",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * 清除保存标志位（当玩家继续落子时调用）
     */
    public void markGameAsUnsaved() {
        isGameSaved = false;
    }
}