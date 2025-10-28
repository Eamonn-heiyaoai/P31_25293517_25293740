/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import chessgame.Player;
import chessgame.ChessPiece;

import java.sql.*;
import java.util.HashMap;

/**
 *
 * @author 34395
 */
public class PlayerDAO {

    public void savePlayer(Player player) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "MERGE INTO players (name, symbol, score) VALUES (?, ?, ?)");
        ps.setString(1, player.getName());
        String symbol = "" + player.getPiece().getSymbol();
        ps.setString(2, symbol);
        ps.setInt(3, player.getScore());
        ps.executeUpdate();
        ps.close();
    }

    public HashMap<String, Player> loadAllPlayers() throws SQLException {
        HashMap<String, Player> playerMap = new HashMap<>();
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM players");

        while (rs.next()) {
            String name = rs.getString("name");
            char symbol = rs.getString("symbol").charAt(0);
            int score = rs.getInt("score");
            ChessPiece piece = (symbol == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            Player p = new Player(name, piece);
            p.addScore(score);
            playerMap.put(name, p);
        }
        rs.close();
        st.close();
        return playerMap;
    }

    public void updateScore(String name, int newScore) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement("UPDATE players SET score=? WHERE name=?");
        ps.setInt(1, newScore);
        ps.setString(2, name);
        ps.executeUpdate();
        ps.close();
    }
}
