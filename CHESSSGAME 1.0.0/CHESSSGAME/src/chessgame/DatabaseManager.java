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
        if (conn == null) {
            conn = DriverManager.getConnection(DB_URL);
            initTables();
        }
        return conn;
    }

    private static void initTables() throws SQLException {
        Statement stmt = conn.createStatement();
        // 玩家表
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                "name VARCHAR(50) PRIMARY KEY, " +
                "symbol CHAR(1), " + 
                "score INT" +
                ")");
        // 对局表
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS games (" +
                "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                "player1 VARCHAR(50), " +
                "player2 VARCHAR(50), " +
                "turn INT, " +
                "board CLOB, " + 
                "FOREIGN KEY (player1) REFERENCES players(name), " +
                "FOREIGN KEY (player2) REFERENCES players(name)" +
                ")");
        stmt.close();
    }
}
