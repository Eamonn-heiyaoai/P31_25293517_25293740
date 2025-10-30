/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author 34395
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:derby:gomokuDB;create=true";
    private static Connection conn;

    public static Connection getConnection() throws SQLException {
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

    private static void initTables() throws SQLException {
        Statement stmt = conn.createStatement();
        
        try {
            stmt.executeUpdate("CREATE TABLE players (" +
                    "name VARCHAR(50) PRIMARY KEY, " +
                    "score REAL" +
                    ")");
            System.out.println("创建 players 表成功");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) { // 表已存在
                throw e;
            }
        }

        try {
            stmt.executeUpdate("CREATE TABLE saved_game (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "player1 VARCHAR(50), " +
                    "player2 VARCHAR(50), " +
                    "current_piece CHAR(1), " +
                    "board CLOB" +
                    ")");
            System.out.println("创建 saved_game 表成功");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) { // 表已存在
                throw e;
            }
        }

        try {
            stmt.executeUpdate("CREATE TABLE games (" +
                    "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                    "player1 VARCHAR(50), " +
                    "player2 VARCHAR(50), " +
                    "turn INT, " +
                    "board CLOB" +
                    ")");
            System.out.println("创建 games 表成功");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                throw e;
            }
        }
        
        stmt.close();
    }
    
    public static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            //关闭Derby数据库
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                if (!e.getSQLState().equals("XJ015")) {
                    throw e;
                }
            }
        }
    }
    
    public static void insertSampleData() throws SQLException {
        Connection conn = getConnection();
        
        String[] samplePlayers = {
            "INSERT INTO players VALUES ('lll', 'X', 15)",
            "INSERT INTO players VALUES ('eamonn', 'O', 12)",
            "INSERT INTO players VALUES ('harry', 'X', 10)",
            "INSERT INTO players VALUES ('pdc', 'O', 8)",
            "INSERT INTO players VALUES ('none', 'X', 5)"
        };
        
        Statement stmt = conn.createStatement();
        for (String sql : samplePlayers) {
            try {
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                // 忽略重复键错误
                if (!e.getSQLState().equals("23505")) {
                    throw e;
                }
            }
        }
        stmt.close();
        System.out.println("✓ 示例数据插入完成");
    }
}
