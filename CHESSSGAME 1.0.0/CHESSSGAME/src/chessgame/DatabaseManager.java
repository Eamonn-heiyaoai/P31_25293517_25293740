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
                throw new SQLException("Derby driver is missing: " + e.getMessage());
            }
            conn = DriverManager.getConnection(DB_URL);
            initTables();
        }
        return conn;
    }

    private static void initTables() throws SQLException, StandardException {
        Statement stmt = conn.createStatement();
        
        //create players table
        try {
            stmt.executeUpdate(
                "CREATE TABLE players (" +
                "name VARCHAR(50) PRIMARY KEY, " +
                "score REAL DEFAULT 1000" +
                ")"
            );
            System.out.println("create players success");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                throw e;
            }
            System.out.println("players already exit");
        }
        
        //create saved_game table 
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
            System.out.println("create saved_game success");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                throw e;
            }
            System.out.println("saved_game already exit");
        }
        
        //create move_histroy table
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
            System.out.println("craete move_history success");
        } catch (SQLException e) {
            if (!e.getSQLState().equals("X0Y32")) {
                throw e;
            }
            System.out.println("move_history already exit");
        }
        
        stmt.close();
    }
    
    //insert test players infor
    public static void insertSampleData() throws SQLException, StandardException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        String[] samplePlayers = {
            "INSERT INTO players (name, symbol, score) VALUES ('lll', 'X', 150)",
            "INSERT INTO players (name, symbol, score) VALUES ('eamonn', 'O', 120)",
            "INSERT INTO players (name, symbol, score) VALUES ('321', 'X', 100)",
            "INSERT INTO players (name, symbol, score) VALUES ('123', 'O', 80)",
            "INSERT INTO players (name, symbol, score) VALUES ('harry', 'X', 50)"
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
        System.out.println("insert test infor success");
    }
    
    //close the database link
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("link close");
            }
            try {
                DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException e) {
                if (e.getSQLState().equals("XJ015")) {
                    System.out.println("Derby database close");
                } else {
                    throw e;
                }
            }
        } catch (SQLException e) {
            System.err.println("error when close the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
