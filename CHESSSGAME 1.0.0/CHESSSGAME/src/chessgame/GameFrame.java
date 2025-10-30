package chessgame;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private BoardPanel boardPanel;
    private GameController controller;
    private JLabel statusLabel;
    private String player1Name;
    private String player2Name;

    // ✅ 新构造方法：支持从主菜单传入玩家名
    public GameFrame(String player1, String player2) {
        this.player1Name = player1;
        this.player2Name = player2;
        initGame(); // 把界面初始化逻辑提取出去
        setupWindowListener(); // ✅ 新增：添加关闭窗口时询问保存
    }

    // ✅ 兼容旧版本（如果没传名字）
    public GameFrame() {
        this("Player 1", "Player 2");
    }

    private void initGame() {
        setTitle("五子棋 - " + player1Name + " vs " + player2Name);
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // ✅ 改成手动控制关闭
        setLocationRelativeTo(null);

        Board board = new Board();
        statusLabel = new JLabel("当前玩家：" + player1Name + "（黑棋）", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        // ✅ 修改：把玩家名传入控制器
        controller = new GameController(board, statusLabel, player1Name, player2Name);
        boardPanel = new BoardPanel(board, controller);

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        // ===== 菜单栏 =====
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("游戏");

        JMenuItem restartItem = new JMenuItem("重新开始");
        restartItem.addActionListener(e -> controller.restartGame(boardPanel));
        gameMenu.add(restartItem);

        JMenuItem backToMenuItem = new JMenuItem("🏠 返回主菜单");
        backToMenuItem.addActionListener(e -> backToMainMenu());
        gameMenu.add(backToMenuItem);

        JMenuItem backToNameInputItem = new JMenuItem("返回输入姓名界面");
        backToNameInputItem.addActionListener(e -> {
            dispose(); // 关闭当前游戏窗口
            MainMenuFrame menu = new MainMenuFrame();
            menu.setVisible(true);

            // 可选：自动弹出输入框，且保留之前的内容
            SwingUtilities.invokeLater(menu::showPlayerNameDialog);
        });
        gameMenu.add(backToNameInputItem);

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> askSaveBeforeExit()); // ✅ 改成询问保存
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }

    // ✅ 新增：退出游戏前询问是否保存
    private void setupWindowListener() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                askSaveBeforeExit();
            }
        });
    }

    // ✅ 新增：封装询问保存逻辑
    private void askSaveBeforeExit() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "是否保存当前棋局进度？",
                "退出游戏",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.CANCEL_OPTION) {
            return; // 不关闭窗口
        }

        if (option == JOptionPane.YES_OPTION) {
            try {
                GameSaveDAO dao = new GameSaveDAO();
                dao.saveGame(controller.getBoard(),
                        player1Name,
                        player2Name,
                        controller.getCurrentPiece());
                JOptionPane.showMessageDialog(this, "✅ 棋局已成功保存！");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "保存失败：" + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 不管是否保存，都回主菜单
        backToMainMenu();
    }

    // ✅ 新增：统一回到主菜单
    private void backToMainMenu() {
        dispose();
        new MainMenuFrame().setVisible(true);
    }

    // ✅ 新增：用于继续游戏加载存档
    public void loadSavedGame(SavedGame saved) {
        this.controller = new GameController(saved.board, statusLabel, saved.player1, saved.player2);
        this.boardPanel.setBoard(saved.board);
        this.controller.setCurrentPiece(saved.currentPiece);
        statusLabel.setText("继续游戏：" + saved.player1 + " vs " + saved.player2);
        repaint();
    }
}
