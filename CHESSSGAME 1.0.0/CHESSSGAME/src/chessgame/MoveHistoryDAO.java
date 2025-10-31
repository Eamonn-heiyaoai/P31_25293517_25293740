/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;

/**
 *
 * @author 34395
 */
public class MoveHistoryDAO {
    
    //Save a move record
    public void saveMove(int gameId, int stepNumber, String playerName, 
                        ChessPiece piece, int row, int col) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO move_history (game_id, step_number, player_name, piece, row_pos, col_pos) " +
            "VALUES (?, ?, ?, ?, ?, ?)");
        ps.setInt(1, gameId);
        ps.setInt(2, stepNumber);
        ps.setString(3, playerName);
        ps.setString(4, String.valueOf(piece.getSymbol()));
        ps.setInt(5, row);
        ps.setInt(6, col);
        ps.executeUpdate();
        ps.close();
    }
    
    //Retrieve the complete move history of the specified game
    public List<MoveRecord> getMoveHistory(int gameId) throws SQLException, StandardException {
        List<MoveRecord> history = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT step_number, player_name, piece, row_pos, col_pos, move_time " +
            "FROM move_history WHERE game_id = ? ORDER BY step_number");
        ps.setInt(1, gameId);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            char pieceChar = rs.getString("piece").charAt(0);
            ChessPiece piece = (pieceChar == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            
            history.add(new MoveRecord(
                rs.getInt("step_number"),
                rs.getString("player_name"),
                piece,
                rs.getInt("row_pos"),
                rs.getInt("col_pos"),
                rs.getTimestamp("move_time")
            ));
        }
        
        rs.close();
        ps.close();
        return history;
    }
    
    //Delete all history of the specified game
    public void deleteGameHistory(int gameId) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "DELETE FROM move_history WHERE game_id = ?");
        ps.setInt(1, gameId);
        ps.executeUpdate();
        ps.close();
    }
    
    //get the last steps
    public int getLastStepNumber(int gameId) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT MAX(step_number) as last_step FROM move_history WHERE game_id = ?");
        ps.setInt(1, gameId);
        ResultSet rs = ps.executeQuery();
        
        int lastStep = 0;
        if (rs.next()) {
            lastStep = rs.getInt("last_step");
        }
        
        rs.close();
        ps.close();
        return lastStep;
    }
}
