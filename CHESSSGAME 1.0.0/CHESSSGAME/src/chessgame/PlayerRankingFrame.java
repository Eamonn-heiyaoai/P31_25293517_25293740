package chessgame;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class PlayerRankingFrame extends JFrame {

    public PlayerRankingFrame() {
        setTitle("玩家排名");
        setSize(400, 300);
        setLocationRelativeTo(null);

        String[] columns = {"排名", "玩家名", "胜场", "负场"};
        Object[][] data = {
                {"1", "Alice", "10", "3"},
                {"2", "Bob", "8", "5"},
                {"3", "Cathy", "6", "7"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("返回主菜单");
        backButton.addActionListener(e -> dispose());
        add(backButton, BorderLayout.SOUTH);
    }
}
