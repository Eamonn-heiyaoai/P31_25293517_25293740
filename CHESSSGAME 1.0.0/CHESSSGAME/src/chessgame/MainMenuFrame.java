package chessgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuFrame extends JFrame {

    public MainMenuFrame() {
        setTitle("五子棋 - 主菜单");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("五子棋游戏", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JButton startButton = new JButton("🎮 开始游戏");
        JButton rankButton = new JButton("🏆 查看玩家排名");
        JButton exitButton = new JButton("❌ 退出游戏");

        buttonPanel.add(startButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.CENTER);

        // ===== 事件绑定 =====
        startButton.addActionListener(e -> {
            dispose();  // 关闭主菜单
            new GameFrame().setVisible(true);  // 打开棋盘窗口
        });

        rankButton.addActionListener(e -> {
            new PlayerRankingFrame().setVisible(true);
        });

        exitButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
