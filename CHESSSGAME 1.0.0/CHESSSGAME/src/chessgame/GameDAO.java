/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import chessgame.Board;
import chessgame.ChessPiece;
import java.sql.*;

/**
 *
 * @author 34395
 */
public class GameDAO {

    //保存游戏进度（两个玩家之间只保留一份存档，后保存的覆盖前面的）
    public void saveGame(String player1, String player2, ChessPiece currentPiece, Board board) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        
        //将棋盘转为字符串
        String boardData = board.toString();
        
        //检查是否已有这两个玩家的存档
        PreparedStatement checkPs = conn.prepareStatement(
            "SELECT id FROM saved_game WHERE player1=? AND player2=?");
        checkPs.setString(1, player1);
        checkPs.setString(2, player2);
        ResultSet rs = checkPs.executeQuery();
        
        if (rs.next()) {
            //若已有存档则更新
            int id = rs.getInt("id");
            PreparedStatement updatePs = conn.prepareStatement(
                "UPDATE saved_game SET current_piece=?, board=? WHERE id=?");
            updatePs.setString(1, String.valueOf(currentPiece.getSymbol()));
            updatePs.setString(2, boardData);
            updatePs.setInt(3, id);
            updatePs.executeUpdate();
            updatePs.close();
            System.out.println("✓ 更新存档成功: " + player1 + " vs " + player2);
        } else {
            //若没有存档则添加
            PreparedStatement insertPs = conn.prepareStatement(
                "INSERT INTO saved_game (player1, player2, current_piece, board) VALUES (?, ?, ?, ?)");
            insertPs.setString(1, player1);
            insertPs.setString(2, player2);
            insertPs.setString(3, String.valueOf(currentPiece.getSymbol()));
            insertPs.setString(4, boardData);
            insertPs.executeUpdate();
            insertPs.close();
            System.out.println("✓ 新建存档成功: " + player1 + " vs " + player2);
        }
        
        rs.close();
        checkPs.close();
    }
    
    //检查两个玩家之间是否有存档
    public boolean hasSavedGame(String player1, String player2) throws SQLException {
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
    
    //加载指定两个玩家的存档
    public SavedGame loadGame(String player1, String player2) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM saved_game WHERE player1=? AND player2=?");
        ps.setString(1, player1);
        ps.setString(2, player2);
        ResultSet rs = ps.executeQuery();
        
        SavedGame savedGame = null;
        if (rs.next()) {
            char pieceChar = rs.getString("current_piece").charAt(0);
            String boardData = rs.getString("board");
            
            ChessPiece piece = (pieceChar == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            Board board = Board.fromString(boardData);
            
            savedGame = new SavedGame(player1, player2, piece, board);
            System.out.println("✓ 加载存档成功: " + player1 + " vs " + player2);
        }
        
        rs.close();
        ps.close();
        return savedGame;
    }
    
    //删除指定两个玩家的存档
    public void deleteSavedGame(String player1, String player2) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
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
    
    //清空所有存档
    public void clearAllSaves() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM saved_game");
        st.close();
        System.out.println("✓ 已清空所有存档");
    }
    
    //获取所有存档列表
    public java.util.List<SavedGame> getAllSavedGames() throws SQLException {
        java.util.List<SavedGame> list = new java.util.ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM saved_game");
        
        while (rs.next()) {
            String player1 = rs.getString("player1");
            String player2 = rs.getString("player2");
            char pieceChar = rs.getString("current_piece").charAt(0);
            String boardData = rs.getString("board");
            
            ChessPiece piece = (pieceChar == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            Board board = Board.fromString(boardData);
            
            list.add(new SavedGame(player1, player2, piece, board));
        }
        
        rs.close();
        st.close();
        return list;
    }
}
