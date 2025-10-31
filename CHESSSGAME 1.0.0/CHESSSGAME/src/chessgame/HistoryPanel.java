/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.iapi.error.StandardException;

/**
 *
 * @author 34395
 */
public class HistoryPanel extends JPanel {
    private JList<String> historyList;
    private DefaultListModel<String> historyListModel;
    private GameController controller;

    public HistoryPanel(GameController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 600));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("落子历史"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        setBackground(Color.WHITE);

        //历史记录列表
        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        historyList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyList.setFixedCellHeight(25);

        JScrollPane scrollPane = new JScrollPane(historyList);
        add(scrollPane, BorderLayout.CENTER);
        
        //刷新按钮
        JButton refreshButton = new JButton("🔄 刷新");
        refreshButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> updateHistory());
        add(refreshButton, BorderLayout.SOUTH);
        
        updateHistory();
    }

    //更新历史记录显示
    public void updateHistory() {
        SwingUtilities.invokeLater(() -> {
            historyListModel.clear();
            List<MoveRecord> history = null;
            try {
                history = controller.getMoveHistory();
            } catch (StandardException ex) {
                Logger.getLogger(HistoryPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (history.isEmpty()) {
                historyListModel.addElement("暂无落子记录");
                return;
            }
            
            //添加表头
            historyListModel.addElement("步数  玩家       位置");
            historyListModel.addElement("------------------------");
            
            for (MoveRecord record : history) {
                String pieceSymbol = record.getPiece() == ChessPiece.BLACK ? "⚫" : "⚪";
                String stepInfo = String.format("%2d    %-8s %s (%2d,%2d)",
                    record.getStepNumber(),
                    record.getPlayerName(),
                    pieceSymbol,
                    record.getRow(),
                    record.getCol()
                );
                historyListModel.addElement(stepInfo);
            }
            
            //滚动到最后一条
            if (historyListModel.size() > 0) {
                int lastIndex = historyListModel.size() - 1;
                historyList.ensureIndexIsVisible(lastIndex);
                historyList.setSelectedIndex(lastIndex);
            }
        });
    }
    
    //更新控制器引用
    public void setController(GameController controller) {
        this.controller = controller;
        updateHistory();
    }
}
