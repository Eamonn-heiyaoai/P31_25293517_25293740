package chessgame;

/**
 * 棋盘类
 * 用二维数组存储棋子
 */
public class Board {
    private static final int SIZE = 16;
    private static final int WIN_COUNT = 5; // 五子连珠
    private ChessPiece[][] grid;

    public Board() {
        grid = new ChessPiece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = ChessPiece.EMPTY;
            }
        }
    }

    public boolean placePiece(int row, int col, ChessPiece piece) {
        if (row < 1 || row > SIZE || col < 1 || col > SIZE) {
            return false;
        }
        if (grid[row - 1][col - 1] != ChessPiece.EMPTY) {
            return false;
        }
        grid[row - 1][col - 1] = piece;
        return true;
    }

    // ✅ 检查是否获胜
    public boolean checkWin(int row, int col, ChessPiece piece) {
        int r = row - 1, c = col - 1;

        int[][] directions = {
                {0, 1},   // 水平
                {1, 0},   // 垂直
                {1, 1},   // 左上到右下
                {1, -1}   // 右上到左下
        };

        for (int[] d : directions) {
            int count = 1;
            count += countPieces(r, c, d[0], d[1], piece);
            count += countPieces(r, c, -d[0], -d[1], piece);
            if (count >= WIN_COUNT) return true;
        }
        return false;
    }

    private int countPieces(int r, int c, int dr, int dc, ChessPiece piece) {
        int count = 0;
        int nr = r + dr;
        int nc = c + dc;
        while (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && grid[nr][nc] == piece) {
            count++;
            nr += dr;
            nc += dc;
        }
        return count;
    }

    public void printBoard() {
        System.out.print("   ");
        for (int j = 1; j <= SIZE; j++) {
            System.out.printf("%2d ", j);
        }
        System.out.println();

        for (int i = 0; i < SIZE; i++) {
            System.out.printf("%2d ", i + 1);
            for (int j = 0; j < SIZE; j++) {
                System.out.print(grid[i][j].getSymbol() + "  ");
            }
            System.out.println();
        }
    }

    public ChessPiece[][] getBoard() {
        return grid;
    }

    public void setBoard(ChessPiece[][] newGrid) {
        this.grid = newGrid;
    }

    public int getSize() {
        return SIZE;
    }

    // ✅ 存档功能：序列化棋盘
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                sb.append(grid[i][j].getSymbol());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // ✅ 读档功能：反序列化棋盘
    public static Board fromString(String data) {
        Board board = new Board();
        String[] lines = data.split("\n");
        for (int i = 0; i < lines.length && i < board.getSize(); i++) {
            for (int j = 0; j < lines[i].length() && j < board.getSize(); j++) {
                char c = lines[i].charAt(j);
                if (c == ChessPiece.BLACK.getSymbol()) {
                    board.grid[i][j] = ChessPiece.BLACK;
                } else if (c == ChessPiece.WHITE.getSymbol()) {
                    board.grid[i][j] = ChessPiece.WHITE;
                } else {
                    board.grid[i][j] = ChessPiece.EMPTY;
                }
            }
        }
        return board;
    }
}
