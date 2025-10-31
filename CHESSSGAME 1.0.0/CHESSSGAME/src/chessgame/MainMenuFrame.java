package chessgame;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.iapi.error.StandardException;

public class MainMenuFrame extends JFrame {

    private String lastPlayer1 = "";
    private String lastPlayer2 = "";

    public MainMenuFrame() {
        setTitle("Gomoku - Main Menu");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Gomoku Game", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JButton startButton = new JButton("Start Game");
        JButton rankButton = new JButton("View Player Ranking");
        JButton manageSavesButton = new JButton("Manage Saves");
        JButton exitButton = new JButton("Exit Game");

        startButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        rankButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        manageSavesButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        exitButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));

        buttonPanel.add(startButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(manageSavesButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> {
            try {
                showPlayerNameDialog();
            } catch (StandardException ex) {
                Logger.getLogger(MainMenuFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        rankButton.addActionListener(e -> {
            try {
                new PlayerRankingFrame().setVisible(true);
            } catch (StandardException ex) {
                Logger.getLogger(MainMenuFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
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

    public void showPlayerNameDialog() throws StandardException {
        JTextField player1Field = new JTextField(lastPlayer1, 15);
        JTextField player2Field = new JTextField(lastPlayer2, 15);

        Object[] message = {
                "Black Player Name:", player1Field,
                "White Player Name:", player2Field
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Enter Player Names",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String p1 = player1Field.getText().trim();
            String p2 = player2Field.getText().trim();

            if (p1.isEmpty() || p2.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter both player names!",
                        "⚠️ Incomplete Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            lastPlayer1 = p1;
            lastPlayer2 = p2;

            checkAndStartGame(p1, p2);
        }
    }

    private void checkAndStartGame(String player1, String player2) throws StandardException {
        try {
            GameDAO gameDAO = new GameDAO();

            if (gameDAO.hasSavedGame(player1, player2)) {
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "A previous unfinished game between " + player1 + " and " + player2 + " was found.\n\nWould you like to load and continue it?",
                        "Saved Game Found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (choice == JOptionPane.YES_OPTION) {
                    loadAndStartGame(player1, player2);
                } else {
                    startNewGame(player1, player2);
                }
            } else {
                startNewGame(player1, player2);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error checking saved games: " + ex.getMessage() + "\n\nStarting a new game instead.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            startNewGame(player1, player2);
        }
    }

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
                JOptionPane.showMessageDialog(this, "Failed to load saved game!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load saved game: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void startNewGame(String player1, String player2) throws StandardException {
        GameFrame gameFrame = new GameFrame(player1, player2);
        gameFrame.setVisible(true);
        dispose();
    }

    private void showSaveManagement() {
        try {
            GameDAO gameDAO = new GameDAO();
            java.util.List<SavedGame> saves = gameDAO.getAllSavedGames();

            if (saves.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "There are currently no saved games.",
                        "Save Management",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            StringBuilder sb = new StringBuilder("Current Saved Games:\n\n");
            for (int i = 0; i < saves.size(); i++) {
                SavedGame s = saves.get(i);
                sb.append((i + 1)).append(". ")
                        .append(s.player1).append(" vs ").append(s.player2)
                        .append(" (Current Turn: ")
                        .append(s.currentPiece == ChessPiece.BLACK ? "Black" : "White")
                        .append(")\n");
            }

            sb.append("\nWould you like to clear all saves?");

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    sb.toString(),
                    "Save Management",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                gameDAO.clearAllSaves();
                JOptionPane.showMessageDialog(
                        this,
                        "All saved games have been cleared!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to read saved games: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenuFrame().setVisible(true));
    }
}
