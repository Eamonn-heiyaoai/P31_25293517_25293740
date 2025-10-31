package chessgame;

public class SavedGame {
    public String player1;
    public String player2;
    public ChessPiece currentPiece;
    public Board board;
    public int stepCount;
    public int gameId;  //game ID

    public SavedGame(String p1, String p2, ChessPiece piece, Board board) {
        this(p1, p2, piece, board, 0);
    }
    
    public SavedGame(String p1, String p2, ChessPiece piece, Board board, int stepCount) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentPiece = piece;
        this.board = board;
        this.stepCount = stepCount;
        this.gameId = -1; 
    }
    
    public SavedGame(String p1, String p2, ChessPiece piece, Board board, int stepCount, int gameId) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentPiece = piece;
        this.board = board;
        this.stepCount = stepCount;
        this.gameId = gameId;
    }
}