/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.ResultSet;

/**
 *
 * @author 34395
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:derby:gomokuDB;create=true";
    private static Connection conn;

    public static Connection getConnection() throws SQLException, StandardException {
        if (conn == null || conn.isClosed()) {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Derby 驱动未找到: " + e.getMessage());
            }
            conn = DriverManager.getConnection(DB_URL);
            initTables();
        }
        return conn;
    }

    private static void initTables() throws SQLException, StandardException {
        Statement stmt = conn.createStatement();
        
        //创建玩家表
        try {
            stmt.executeUpdate(
                "CREATE TABLE players (" +
                "name VARCHAR(50) PRIMARY KEY, " +
                "score REAL DEFAULT 1000" +
                ")"
            );
            System.out.println("创建 players 表成功");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) { //表已存在
                throw e;
            }
            System.out.println("players 表已存在");
        }
        
        try {
            stmt.executeUpdate(
                "CREATE TABLE saved_game (" +
                "id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                "player1 VARCHAR(50), " +
                "player2 VARCHAR(50), " +
                "current_piece CHAR(1), " +
                "board CLOB, " +
                "step_count INT DEFAULT 0" +
                ")"
            );
            System.out.println("创建 saved_game 表成功");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                throw e;
            }
            System.out.println("saved_game 表已存在");
        }
        
        try {
            stmt.executeUpdate(
                "CREATE TABLE move_history (" +
                "id INT NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                "game_id INT NOT NULL, " +
                "step_number INT NOT NULL, " +
                "player_name VARCHAR(50) NOT NULL, " +
                "piece CHAR(1) NOT NULL, " +
                "row_pos INT NOT NULL, " +
                "col_pos INT NOT NULL, " +
                "move_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            System.out.println("创建 move_history 表成功");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                throw e;
            }
            System.out.println("move_history 表已存在");
        }
        
        stmt.close();
    }
    
    //插入测试数据（以过时）
    public static void insertSampleData() throws SQLException, StandardException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        String[] samplePlayers = {
            "INSERT INTO players (name, symbol, score) VALUES ('Alice', 'X', 15)",
            "INSERT INTO players (name, symbol, score) VALUES ('Bob', 'O', 12)",
            "INSERT INTO players (name, symbol, score) VALUES ('Cathy', 'X', 10)",
            "INSERT INTO players (name, symbol, score) VALUES ('David', 'O', 8)",
            "INSERT INTO players (name, symbol, score) VALUES ('Eve', 'X', 5)"
        };
        
        for (String sql : samplePlayers) {
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                if (!e.getSQLState().equals("23505")) {
                    throw e;
                }
            }
        }
        
        stmt.close();
        System.out.println("测试数据插入完成");
    }
    
    //清空所有数据（谨用！谨用！谨用！）
    public static void clearAllData() throws SQLException, StandardException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        try {
            stmt.executeUpdate("DELETE FROM games");
            stmt.executeUpdate("DELETE FROM players");
            System.out.println("所有数据已清空");
        } finally {
            stmt.close();
        }
    }
    
    //关闭数据库连接
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("数据库连接已关闭");
            }
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                if (e.getSQLState().equals("XJ015")) {
                    System.out.println("Derby 数据库已关闭");
                } else {
                    throw e;
                }
            }
        } catch (SQLException e) {
            System.err.println("关闭数据库时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
