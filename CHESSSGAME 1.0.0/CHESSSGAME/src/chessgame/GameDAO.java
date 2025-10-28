/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame;

import chessgame.Board;
import chessgame.ChessPiece;
import chessgame.Player;
import java.sql.*;

/**
 *
 * @author 34395
 */
public class GameDAO {

    public void saveGame(String player1, String player2, int turn, ChessPiece[][] grid) throws SQLException {
        Connection conn = DatabaseManager.getConnection();

        // 将棋盘转为字符串
        StringBuilder sb = new StringBuilder();
        for (ChessPiece[] row : grid) {
            for (ChessPiece c : row) {
                sb.append(c.toString()).append(" ");
            }
            sb.append("\n");
        }

        // 判断是否已有记录（相同两人）
        PreparedStatement check = conn.prepareStatement(
            "SELECT id FROM games WHERE player1=? AND player2=?");
        check.setString(1, player1);
        check.setString(2, player2);
        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            int id = rs.getInt("id");
            PreparedStatement ps = conn.prepareStatement(
                "UPDATE games SET turn=?, board=? WHERE id=?");
            ps.setInt(1, turn);
            ps.setString(2, sb.toString());
            ps.setInt(3, id);
            ps.executeUpdate();
            ps.close();
        } else {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO games (player1, player2, turn, board) VALUES (?, ?, ?, ?)");
            ps.setString(1, player1);
            ps.setString(2, player2);
            ps.setInt(3, turn);
            ps.setString(4, sb.toString());
            ps.executeUpdate();
            ps.close();
        }
        rs.close();
        check.close();
    }

    public Board loadGame(String player1, String player2) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT * FROM games WHERE player1=? AND player2=?");
        ps.setString(1, player1);
        ps.setString(2, player2);
        ResultSet rs = ps.executeQuery();
        Board board = new Board();

        if (rs.next()) {
            String[] lines = rs.getString("board").split("\n");
            int i = 0;
            for (String line : lines) {
                String[] cells = line.trim().split(" ");
                for (int j = 0; j < cells.length; j++) {
                    if (cells[j].equals("BLACK"))
                        board.placePiece(i+1, j+1, ChessPiece.BLACK);
                    else if (cells[j].equals("WHITE"))
                        board.placePiece(i+1, j+1, ChessPiece.WHITE);
                }
                i++;
            }
        }
        rs.close();
        ps.close();
        return board;
    }

    public int loadTurn(String player1, String player2) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement ps = conn.prepareStatement(
            "SELECT turn FROM games WHERE player1=? AND player2=?");
        ps.setString(1, player1);
        ps.setString(2, player2);
        ResultSet rs = ps.executeQuery();
        int turn = 1;
        if (rs.next()) turn = rs.getInt("turn");
        rs.close();
        ps.close();
        return turn;
    }
}
