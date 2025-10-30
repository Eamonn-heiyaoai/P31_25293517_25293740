package chessgame;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    private String lastPlayer1 = "";
    private String lastPlayer2 = "";

    // ✅ 默认构造函数
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

        // ===== 按钮事件 =====
        startButton.addActionListener(e -> showPlayerNameDialog());
        rankButton.addActionListener(e -> new PlayerRankingFrame().setVisible(true));
        exitButton.addActionListener(e -> System.exit(0));
    }

    // ✅ 新增构造方法：用于返回时保留输入
    public MainMenuFrame(String lastP1, String lastP2) {
        this(); // 调用默认构造方法
        this.lastPlayer1 = lastP1;
        this.lastPlayer2 = lastP2;
    }

    // ✅ 独立的输入框逻辑
    public void showPlayerNameDialog() {
        JTextField player1Field = new JTextField(lastPlayer1);
        JTextField player2Field = new JTextField(lastPlayer2);

        Object[] message = {
                "黑棋玩家名:", player1Field,
                "白棋玩家名:", player2Field
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "输入玩家姓名",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            String p1 = player1Field.getText().trim();
            String p2 = player2Field.getText().trim();

            if (p1.isEmpty() || p2.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "请填写双方的玩家姓名才能开始游戏！",
                        "⚠️ 输入不完整",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            lastPlayer1 = p1;
            lastPlayer2 = p2;

            GameFrame gameFrame = new GameFrame(p1, p2);
            gameFrame.setVisible(true);
            dispose(); // 关闭主菜单
        }
    }

    // ✅ 程序入口
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
