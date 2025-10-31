/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import chessgame.Player;

import java.sql.*;
import java.util.HashMap;
import org.apache.derby.iapi.error.StandardException;


/**
 *
 * @author 34395
 */
public class PlayerDAO {

    //保存或更新玩家信息
    public void savePlayer(Player player) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        
        //先检查玩家是否已存在
        PreparedStatement checkPs = conn.prepareStatement(
            "SELECT name FROM players WHERE name=?");
        checkPs.setString(1, player.getName());
        ResultSet rs = checkPs.executeQuery();
        
        if (rs.next()) {
            //若玩家已存在则执行更新
            PreparedStatement updatePs = conn.prepareStatement(
                "UPDATE players SET symbol=?, score=? WHERE name=?");
            updatePs.setString(1, String.valueOf(player.getPiece().getSymbol()));
            updatePs.setDouble(2, player.getScore());
            updatePs.setString(3, player.getName());
            updatePs.executeUpdate();
            updatePs.close();
        } else {
            //若玩家不存在则执行插入
            PreparedStatement insertPs = conn.prepareStatement(
                "INSERT INTO players (name, symbol, score) VALUES (?, ?, ?)");
            insertPs.setString(1, player.getName());
            insertPs.setString(2, String.valueOf(player.getPiece().getSymbol()));
            insertPs.setDouble(3, player.getScore());
            insertPs.executeUpdate();
            insertPs.close();
        }
        
        rs.close();
        checkPs.close();
    }

    //加载所有玩家数据
    public HashMap<String, Player> loadAllPlayers() throws SQLException, StandardException {
        HashMap<String, Player> playerMap = new HashMap<>();
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM players ORDER BY score DESC");

        while (rs.next()) {
            String name = rs.getString("name");
            double score = rs.getDouble("score");
            Player p = new Player(name, ChessPiece.EMPTY);
            p.setScore(score);
            playerMap.put(name, p);
        }
        rs.close();
        st.close();
        return playerMap;
    }

    
    //更新玩家分数
    public void updateScore(String name, double newScore) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE players SET score=? WHERE name=?");
        ps.setDouble(1, newScore);
        ps.setString(2, name);
        ps.executeUpdate();
        ps.close();
    }
    
    //给玩家增加分数
    public void addScore(String name, double scoreToAdd) throws SQLException, StandardException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE players SET score = score + ? WHERE name=?");
        ps.setDouble(1, scoreToAdd);
        ps.setString(2, name);
        ps.executeUpdate();
        ps.close();
    }

    public Player getOrCreatePlayer(String name, ChessPiece piece) throws SQLException, StandardException {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT score FROM players WHERE name=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double score = rs.getDouble("score");
                return new Player(name, score);
            } else {
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO players (name, score) VALUES (?, 1000)");
                insert.setString(1, name);
                insert.executeUpdate();
                return new Player(name, 1000);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Player(name, 1000);
        }
    }
    public void updateScore(Player player) throws SQLException, StandardException {
        updateScore(player.getName(), player.getScore());
    }

}
