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

    //save game and return game id
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
            //already have save, updata the database
            gameId = rs.getInt("id");
            PreparedStatement updatePs = conn.prepareStatement(
                "UPDATE saved_game SET current_piece=?, board=?, step_count=? WHERE id=?");
            updatePs.setString(1, String.valueOf(currentPiece.getSymbol()));
            updatePs.setString(2, boardData);
            updatePs.setInt(3, stepCount);
            updatePs.setInt(4, gameId);
            updatePs.executeUpdate();
            updatePs.close();
            System.out.println("updata save: " + player1 + " vs " + player2 + ", steps: " + stepCount);
        } else {
            //dont have save, insert into the database
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
                throw new SQLException("fail to create game save, cant get the game id");
            }
            insertPs.close();
            System.out.println("create a new save: " + player1 + " vs " + player2 + ", gameId: " + gameId);
        }
        
        rs.close();
        checkPs.close();
        return gameId;
    }
    
    //check whether has saved_game between two players
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
    
    //Load the saved game of the specified two players
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
            System.out.println("load game: " + player1 + " vs " + player2 + 
                             ", steps: " + stepCount + ", gameId: " + gameId);
        }
        
        rs.close();
        ps.close();
        return savedGame;
    }
    
    //according to players to get game id
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
    
    //delete the specific players game
    public void deleteSavedGame(String player1, String player2) throws SQLException, StandardException {
    Connection conn = DatabaseManager.getConnection();
    
    //get the game id
    Integer gameId = getGameId(player1, player2);
    if (gameId != null) {
        //delete game histroy
        MoveHistoryDAO historyDAO = new MoveHistoryDAO();
        historyDAO.deleteGameHistory(gameId);
        System.out.println("delete game " + gameId + " success");
    }
    
    //delete save
    PreparedStatement ps = conn.prepareStatement(
        "DELETE FROM saved_game WHERE player1=? AND player2=?");
    ps.setString(1, player1);
    ps.setString(2, player2);
    int count = ps.executeUpdate();
    ps.close();
    
    if (count > 0) {
        System.out.println("delete the saved_game " + player1 + " vs " + player2);
    }
}
    
    //clear the all saved game
    public void clearAllSaves() throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        
        //clear infomation from table move_history
        st.executeUpdate("DELETE FROM move_history");
        //clear information from table saved_game
        st.executeUpdate("DELETE FROM saved_game");
        
        st.close();
        System.out.println("clear all saved_game success");
    }
    
    //get all saved_game
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
