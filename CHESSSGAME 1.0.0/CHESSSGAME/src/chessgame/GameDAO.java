/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import chessgame.Board;
import chessgame.ChessPiece;
import java.sql.*;
import org.apache.derby.iapi.error.StandardException;

/**
 *
 * @author 34395
 */
public class GameDAO {

    /**
     * 保存游戏并返回游戏ID
     */
    public int saveGameAndGetId(String player1, String player2, ChessPiece currentPiece, 
                                Board board, int stepCount) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        String boardData = board.toString();
        
        PreparedStatement checkPs = conn.prepareStatement(
            "SELECT id FROM saved_game WHERE player1=? AND player2=?");
        checkPs.setString(1, player1);
        checkPs.setString(2, player2);
        ResultSet rs = checkPs.executeQuery();
        
        int gameId;
        if (rs.next()) {
            // 已有存档，执行更新
            gameId = rs.getInt("id");
            PreparedStatement updatePs = conn.prepareStatement(
                "UPDATE saved_game SET current_piece=?, board=?, step_count=? WHERE id=?");
            updatePs.setString(1, String.valueOf(currentPiece.getSymbol()));
            updatePs.setString(2, boardData);
            updatePs.setInt(3, stepCount);
            updatePs.setInt(4, gameId);
            updatePs.executeUpdate();
            updatePs.close();
            System.out.println("✓ 更新存档: " + player1 + " vs " + player2 + ", 步数: " + stepCount);
        } else {
            // 没有存档，执行插入
            PreparedStatement insertPs = conn.prepareStatement(
                "INSERT INTO saved_game (player1, player2, current_piece, board, step_count) " +
                "VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            insertPs.setString(1, player1);
            insertPs.setString(2, player2);
            insertPs.setString(3, String.valueOf(currentPiece.getSymbol()));
            insertPs.setString(4, boardData);
            insertPs.setInt(5, stepCount);
            insertPs.executeUpdate();
            
            ResultSet generatedKeys = insertPs.getGeneratedKeys();
            if (generatedKeys.next()) {
                gameId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("创建游戏记录失败，无法获取ID");
            }
            insertPs.close();
            System.out.println("✓ 新建存档: " + player1 + " vs " + player2 + ", gameId: " + gameId);
        }
        
        rs.close();
        checkPs.close();
        return gameId;
    }
    
    /**
     * 检查两个玩家之间是否有存档
     */
    public boolean hasSavedGame(String player1, String player2) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT COUNT(*) FROM saved_game WHERE player1=? AND player2=?");
        ps.setString(1, player1);
        ps.setString(2, player2);
        ResultSet rs = ps.executeQuery();
        
        boolean exists = false;
        if (rs.next()) {
            exists = rs.getInt(1) > 0;
        }
        
        rs.close();
        ps.close();
        return exists;
    }
    
    /**
     * 加载指定两个玩家的存档（包含游戏ID和步数）
     */
    public SavedGame loadGame(String player1, String player2) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM saved_game WHERE player1=? AND player2=?");
        ps.setString(1, player1);
        ps.setString(2, player2);
        ResultSet rs = ps.executeQuery();
        
        SavedGame savedGame = null;
        if (rs.next()) {
            int gameId = rs.getInt("id");
            char pieceChar = rs.getString("current_piece").charAt(0);
            String boardData = rs.getString("board");
            int stepCount = rs.getInt("step_count");
            
            ChessPiece piece = (pieceChar == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            Board board = Board.fromString(boardData);
            
            savedGame = new SavedGame(player1, player2, piece, board, stepCount, gameId);
            System.out.println("✓ 加载存档: " + player1 + " vs " + player2 + 
                             ", 步数: " + stepCount + ", gameId: " + gameId);
        }
        
        rs.close();
        ps.close();
        return savedGame;
    }
    
    /**
     * 根据玩家名获取游戏ID
     */
    public Integer getGameId(String player1, String player2) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT id FROM saved_game WHERE player1=? AND player2=?");
        ps.setString(1, player1);
        ps.setString(2, player2);
        ResultSet rs = ps.executeQuery();
        
        Integer gameId = null;
        if (rs.next()) {
            gameId = rs.getInt("id");
        }
        
        rs.close();
        ps.close();
        return gameId;
    }
    
    /**
     * 删除指定两个玩家的存档
     */
    public void deleteSavedGame(String player1, String player2) throws SQLException, StandardException {
    Connection conn = DatabaseManager.getConnection();
    
    // ✅ 先获取游戏ID，用于删除历史记录
    Integer gameId = getGameId(player1, player2);
    if (gameId != null) {
        // 删除历史记录
        MoveHistoryDAO historyDAO = new MoveHistoryDAO();
        historyDAO.deleteGameHistory(gameId);
        System.out.println("✓ 已删除游戏 " + gameId + " 的历史记录");
    }
    
    // 删除存档
    PreparedStatement ps = conn.prepareStatement(
        "DELETE FROM saved_game WHERE player1=? AND player2=?");
    ps.setString(1, player1);
    ps.setString(2, player2);
    int count = ps.executeUpdate();
    ps.close();
    
    if (count > 0) {
        System.out.println("✓ 删除存档成功: " + player1 + " vs " + player2);
    }
}
    
    /**
     * 清空所有存档
     */
    public void clearAllSaves() throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        
        // 先清空历史记录
        st.executeUpdate("DELETE FROM move_history");
        // 再清空存档
        st.executeUpdate("DELETE FROM saved_game");
        
        st.close();
        System.out.println("✓ 已清空所有存档和历史记录");
    }
    
    /**
     * 获取所有存档列表
     */
    public java.util.List<SavedGame> getAllSavedGames() throws SQLException, StandardException {
        java.util.List<SavedGame> list = new java.util.ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM saved_game");
        
        while (rs.next()) {
            int gameId = rs.getInt("id");
            String player1 = rs.getString("player1");
            String player2 = rs.getString("player2");
            char pieceChar = rs.getString("current_piece").charAt(0);
            String boardData = rs.getString("board");
            int stepCount = rs.getInt("step_count");
            
            ChessPiece piece = (pieceChar == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            Board board = Board.fromString(boardData);
            
            list.add(new SavedGame(player1, player2, piece, board, stepCount, gameId));
        }
        
        rs.close();
        st.close();
        return list;
    }
}
