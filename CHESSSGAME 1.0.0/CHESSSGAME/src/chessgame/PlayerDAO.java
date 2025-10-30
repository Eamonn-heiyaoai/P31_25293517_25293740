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

    //保存或更新玩家信息
    public void savePlayer(Player player) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        
        //先检查玩家是否已存在
        PreparedStatement checkPs = conn.prepareStatement(
            "SELECT name FROM players WHERE name=?");
        checkPs.setString(1, player.getName());
        ResultSet rs = checkPs.executeQuery();
        
        if (rs.next()) {
            //若玩家已存在，执行更新
            PreparedStatement updatePs = conn.prepareStatement(
                "UPDATE players SET symbol=?, score=? WHERE name=?");
            updatePs.setString(1, String.valueOf(player.getPiece().getSymbol()));
            updatePs.setInt(2, player.getScore());
            updatePs.setString(3, player.getName());
            updatePs.executeUpdate();
            updatePs.close();
        } else {
            //若玩家不存在，执行插入
            PreparedStatement insertPs = conn.prepareStatement(
                "INSERT INTO players (name, symbol, score) VALUES (?, ?, ?)");
            insertPs.setString(1, player.getName());
            insertPs.setString(2, String.valueOf(player.getPiece().getSymbol()));
            insertPs.setInt(3, player.getScore());
            insertPs.executeUpdate();
            insertPs.close();
        }
        
        rs.close();
        checkPs.close();
    }

    //加载所有玩家数据
    public HashMap<String, Player> loadAllPlayers() throws SQLException {
        HashMap<String, Player> playerMap = new HashMap<>();
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM players ORDER BY score DESC");

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

    
    //更新玩家分数
    public void updateScore(String name, int newScore) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE players SET score=? WHERE name=?");
        ps.setInt(1, newScore);
        ps.setString(2, name);
        ps.executeUpdate();
        ps.close();
    }
    
    //给玩家增加分数
    public void addScore(String name, int scoreToAdd) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE players SET score = score + ? WHERE name=?");
        ps.setInt(1, scoreToAdd);
        ps.setString(2, name);
        ps.executeUpdate();
        ps.close();
    }
}
