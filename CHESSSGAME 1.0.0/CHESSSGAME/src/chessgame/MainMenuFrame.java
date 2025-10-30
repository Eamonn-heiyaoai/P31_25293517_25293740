package chessgame;

import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    private String lastPlayer1 = "";
    private String lastPlayer2 = "";

    public MainMenuFrame() {
        setTitle("五子棋 - 主菜单");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("五子棋游戏", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton startButton = new JButton("🎮 开始游戏");
        JButton rankButton = new JButton("🏆 查看玩家排名");
        JButton manageSavesButton = new JButton("管理存档");
        JButton exitButton = new JButton("❌ 退出游戏");

        startButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        rankButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        manageSavesButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        exitButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));

        buttonPanel.add(startButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(manageSavesButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.CENTER);

        // ===== 事件绑定 =====
        startButton.addActionListener(e -> showPlayerNameDialog());
        rankButton.addActionListener(e -> new PlayerRankingFrame().setVisible(true));
        manageSavesButton.addActionListener(e -> showSaveManagement());
        exitButton.addActionListener(e -> {
            try {
                DatabaseManager.closeConnection();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });
    }

    /**
     * 显示玩家输入对话框，并检查是否有存档
     */
    public void showPlayerNameDialog() {
        JTextField player1Field = new JTextField(lastPlayer1, 15);
        JTextField player2Field = new JTextField(lastPlayer2, 15);

        Object[] message = {
                "黑棋玩家名:", player1Field,
                "白棋玩家名:", player2Field
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "输入玩家姓名",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String p1 = player1Field.getText().trim();
            String p2 = player2Field.getText().trim();

            if (p1.isEmpty() || p2.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "请填写双方的玩家姓名！",
                        "⚠️ 输入不完整",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            lastPlayer1 = p1;
            lastPlayer2 = p2;

            // ✅ 检查是否有存档
            checkAndStartGame(p1, p2);
        }
    }

    /**
     * 检查两个玩家是否有存档，如果有则询问是否加载
     */
    private void checkAndStartGame(String player1, String player2) {
        try {
            GameDAO gameDAO = new GameDAO();
            
            if (gameDAO.hasSavedGame(player1, player2)) {
                // 有存档，询问是否加载
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "检测到 " + player1 + " 与 " + player2 + " 之间有一场未完成的对局。\n\n是否加载存档继续游戏？",
                        "发现存档",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    // 加载存档
                    loadAndStartGame(player1, player2);
                } else {
                    startNewGame(player1, player2);
                }
            } else {
                // 没有存档，直接开始新游戏
                startNewGame(player1, player2);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "检查存档时出错：" + ex.getMessage() + "\n\n将直接开始新游戏。",
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
            startNewGame(player1, player2);
        }
    }

    /**
     * 加载存档并开始游戏
     */
    private void loadAndStartGame(String player1, String player2) {
        try {
            GameDAO gameDAO = new GameDAO();
            SavedGame saved = gameDAO.loadGame(player1, player2);
            
            if (saved != null) {
                GameFrame gameFrame = new GameFrame(player1, player2);
                gameFrame.loadSavedGame(saved);
                gameFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "加载存档失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "加载存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * 开始新游戏
     */
    private void startNewGame(String player1, String player2) {
        GameFrame gameFrame = new GameFrame(player1, player2);
        gameFrame.setVisible(true);
        dispose();
    }

    /**
     * 显示存档管理界面
     */
    private void showSaveManagement() {
        try {
            GameDAO gameDAO = new GameDAO();
            java.util.List<SavedGame> saves = gameDAO.getAllSavedGames();
            
            if (saves.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "当前没有任何存档。",
                        "存档管理",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
            
            // 构建存档列表
            StringBuilder sb = new StringBuilder("当前存档列表：\n\n");
            for (int i = 0; i < saves.size(); i++) {
                SavedGame s = saves.get(i);
                sb.append((i + 1)).append(". ")
                  .append(s.player1).append(" vs ").append(s.player2)
                  .append(" (当前回合: ")
                  .append(s.currentPiece == ChessPiece.BLACK ? "黑棋" : "白棋")
                  .append(")\n");
            }
            
            sb.append("\n是否清空所有存档？");
            
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    sb.toString(),
                    "存档管理",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                gameDAO.clearAllSaves();
                JOptionPane.showMessageDialog(
                        this,
                        "所有存档已清空！",
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "读取存档失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
