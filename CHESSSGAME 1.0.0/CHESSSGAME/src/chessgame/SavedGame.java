package chessgame;

public class SavedGame {
    public String player1;
    public String player2;
    public ChessPiece currentPiece;
    public Board board;

    public SavedGame(String p1, String p2, ChessPiece piece, Board board) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentPiece = piece;
        this.board = board;
    }
}
