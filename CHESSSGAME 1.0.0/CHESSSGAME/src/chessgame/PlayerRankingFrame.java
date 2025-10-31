package chessgame;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.iapi.error.StandardException;

public class PlayerRankingFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public PlayerRankingFrame() throws StandardException {
        setTitle("Player Ranking");
        setSize(550, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Player Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Rank", "Player Name", "Score"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(173, 216, 230));

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        JButton refreshButton = new JButton("Refresh Ranking");
        refreshButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> {
            try {
                loadPlayerRanking();
            } catch (StandardException ex) {
                Logger.getLogger(PlayerRankingFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        backButton.addActionListener(e -> dispose());

        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPlayerRanking();
    }

    private void loadPlayerRanking() throws StandardException {
        try {
            tableModel.setRowCount(0);

            PlayerDAO playerDAO = new PlayerDAO();
            HashMap<String, Player> playerMap = playerDAO.loadAllPlayers();

            if (playerMap.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No players found!",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            List<Player> playerList = new ArrayList<>(playerMap.values());
            playerList.sort((p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));

            int rank = 1;
            for (Player player : playerList) {
                Object[] row = {
                        rank++,
                        player.getName(),
                        (int)player.getScore()
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load player data:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
