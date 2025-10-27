package chessgame;

import static chessgame.ChessPiece.BLACK;
import static chessgame.ChessPiece.WHITE;

/**
 * 工具类
 * 输入验证等工具方法
 */
public class Utils {
    public static boolean isValidInput(String input) {
        // 检查输入是否合法
        return input != null && !input.trim().isEmpty();
    }
    
    public static char returntype(String record){
        if (record.equals(WHITE)){
            return 'O';
        }
        else if(record.equals(BLACK)){
            return 'X';
        }
            return '+';
    }
}
