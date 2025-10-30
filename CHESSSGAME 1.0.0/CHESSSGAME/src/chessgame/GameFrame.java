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
    }

    // ✅ 兼容旧版本（如果没传名字）
    public GameFrame() {
        this("Player 1", "Player 2");
    }

    private void initGame() {
        setTitle("五子棋 - " + player1Name + " vs " + player2Name);
        setSize(700, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        backToMenuItem.addActionListener(e -> {
            dispose();
            new MainMenuFrame().setVisible(true);
        });
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
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }
}
