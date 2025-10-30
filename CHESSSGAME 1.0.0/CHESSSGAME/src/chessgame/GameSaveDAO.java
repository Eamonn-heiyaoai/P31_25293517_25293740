package chessgame;

import java.sql.*;

public class GameSaveDAO {

    // 保存当前棋盘（覆盖旧存档，只保留一份）
    public void saveGame(Board board, String player1, String player2, ChessPiece currentPiece) throws SQLException {
        Connection conn = DatabaseManager.getConnection();

        // 检查是否已有存档
        Statement check = conn.createStatement();
        ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM saved_game");
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        check.close();

        String boardData = board.toString(); // 假设 Board 有 toString() 输出棋盘状态

        if (count > 0) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE saved_game SET player1=?, player2=?, current_piece=?, board=? WHERE id=1");
            ps.setString(1, player1);
            ps.setString(2, player2);
            ps.setString(3, String.valueOf(currentPiece.getSymbol()));
            ps.setString(4, boardData);
            ps.executeUpdate();
            ps.close();
        } else {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO saved_game (player1, player2, current_piece, board) VALUES (?, ?, ?, ?)");
            ps.setString(1, player1);
            ps.setString(2, player2);
            ps.setString(3, String.valueOf(currentPiece.getSymbol()));
            ps.setString(4, boardData);
            ps.executeUpdate();
            ps.close();
        }
    }

    // 加载存档
    public SavedGame loadGame() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM saved_game FETCH FIRST ROW ONLY");

        if (rs.next()) {
            String player1 = rs.getString("player1");
            String player2 = rs.getString("player2");
            char pieceChar = rs.getString("current_piece").charAt(0);
            String boardData = rs.getString("board");

            ChessPiece piece = (pieceChar == 'X') ? ChessPiece.BLACK : ChessPiece.WHITE;
            Board board = Board.fromString(boardData); // 你需要在 Board 里实现 fromString()

            return new SavedGame(player1, player2, piece, board);
        }
        return null;
    }

    // 删除存档（例如开始新游戏后）
    public void clearSave() throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("DELETE FROM saved_game");
        st.close();
    }
}
