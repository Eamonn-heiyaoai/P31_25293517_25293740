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
        setTitle("Gomoku - " + player1Name + " vs " + player2Name);
        setSize(950, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        Board board = new Board();
        statusLabel = new JLabel("Current player: " + player1Name + " (Black) - Move 1", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        controller = new GameController(board, statusLabel, player1Name, player2Name);
        boardPanel = new BoardPanel(board, controller);

        historyPanel = new HistoryPanel(controller);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(boardPanel, BorderLayout.CENTER);
        centerPanel.add(historyPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem restartItem = new JMenuItem("🔄 Restart");
        restartItem.addActionListener(e -> {
            if (!isGameSaved && controller.getStepCount() > 0) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to restart? Current progress will be lost!",
                        "Restart",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                deleteCurrentGameData();
            } catch (StandardException ex) {
                Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

            controller.restartGame(boardPanel);
            historyPanel.updateHistory();
            isGameSaved = false;
        });
        gameMenu.add(restartItem);

        JMenuItem saveItem = new JMenuItem("💾 Save Game");
        saveItem.addActionListener(e -> saveCurrentGame());
        gameMenu.add(saveItem);

        gameMenu.addSeparator();

        JMenuItem backToMenuItem = new JMenuItem("🏠 Back to Main Menu");
        backToMenuItem.addActionListener(e -> {
            try {
                askSaveBeforeExit();
            } catch (StandardException ex) {
                Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        gameMenu.add(backToMenuItem);

        JMenuItem exitItem = new JMenuItem("❌ Exit Game");
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

            controller.setCurrentGameId(gameId);
            isGameSaved = true;

            JOptionPane.showMessageDialog(
                    this,
                    "✅ Game progress saved successfully!\n\nYou can continue next time.",
                    "Save Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Save failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

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

    private void askSaveBeforeExit() throws StandardException {
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
                "Would you like to save the current game progress?",
                "Exit Game",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.CANCEL_OPTION) {
            return;
        }

        if (option == JOptionPane.YES_OPTION) {
            saveCurrentGame();
        } else if (option == JOptionPane.NO_OPTION) {
            deleteCurrentGameData();
        }

        backToMainMenu();
    }

    private void deleteCurrentGameData() throws StandardException {
        try {
            GameDAO gameDAO = new GameDAO();

            if (controller.getCurrentGameId() != -1) {
                gameDAO.deleteSavedGame(player1Name, player2Name);
                System.out.println("✓ Unsaved game data deleted");
            }

        } catch (SQLException ex) {
            System.err.println("Error deleting game data: " + ex.getMessage());
        }
    }

    private void backToMainMenu() {
        dispose();
        new MainMenuFrame().setVisible(true);
    }

    public void loadSavedGame(SavedGame saved) throws StandardException {
        this.controller = new GameController(saved.board, statusLabel, saved.player1, saved.player2);
        this.controller.setCurrentPiece(saved.currentPiece);
        this.controller.setStepCount(saved.stepCount);
        this.controller.setCurrentGameId(saved.gameId);

        this.boardPanel.setController(this.controller);
        this.boardPanel.setBoard(saved.board);

        this.historyPanel.setController(this.controller);
        this.historyPanel.updateHistory();

        String currentPlayerName = (saved.currentPiece == ChessPiece.BLACK) ? saved.player1 : saved.player2;
        String pieceName = (saved.currentPiece == ChessPiece.BLACK) ? "Black" : "White";
        statusLabel.setText("Continue Game - Current player: " + currentPlayerName + " (" + pieceName + ") - Move " + (saved.stepCount + 1));

        isGameSaved = true;

        boardPanel.repaint();

        JOptionPane.showMessageDialog(
                this,
                "Game loaded successfully!\n\nContinue: " + saved.player1 + " vs " + saved.player2 +
                        "\nCurrent move: " + saved.stepCount,
                "Load Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void markGameAsUnsaved() {
        isGameSaved = false;
    }
}
