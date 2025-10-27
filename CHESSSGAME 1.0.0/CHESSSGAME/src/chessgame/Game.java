package chessgame;

/**
 * 游戏主控类
 * 负责整体流程控制
 */

import java.util.Scanner;

public class Game {
    private Board board;
    private Player[] players;
    private int turn;
    private PlayerRecord pr;
    private GameRecord gr;

    public Game() {
        board = new Board();
        pr = new PlayerRecord();
        gr = new GameRecord();
        pr.LoadRecord();
        gr.LoadGameList();
        turn = 0;
    }

    public void setTurn(int t){
        this.turn = t;
    }
    
    public void menu(){
        Scanner in = new Scanner(System.in);
        
        String cmd = "e";
        
        System.out.println("Welcome to 16x16 Gomoku game!");
        System.out.println("Enter S to start game");
        System.out.println("Enter P to show PlayerList");
        System.out.println("Enter Q to qiut");
        
        while(cmd.equals("e")){
            cmd = in.nextLine();
            switch(cmd){
                case "s":
                case "S":
                    this.start();
                    break;
                case "p":
                case "P":
                    this.List();
                    break;
                case "q":
                case "Q":
                    break;
                default:
                    System.out.println("Illegal enter!");
                    cmd = "e";
                    break;
            }
        }
        
        
    }
    
    public void start() {
        Scanner sc = new Scanner(System.in);
        
        String tempname1,tempname2;
        System.out.println("Please enter the player1 name:");
        tempname1 = sc.nextLine();
        System.out.println("Please enter the player2 name:");
        tempname2 = sc.nextLine();
        
        players = new Player[]{
                new Player(tempname1, ChessPiece.BLACK),
                new Player(tempname2, ChessPiece.WHITE)
        };
        
        pr.UpdatePlayerlist(players[0],players[1]);
        pr.SaveRecord();
        
        String gamename = GameRecord.rtgamename(players[0],players[1]);
        if (gr.gamesave.containsKey(gamename)){
            System.out.println("Wheteher to read the last saved game?(Y/N)");
            System.out.println("Note:Only one game can be saved between every"
                    + " twop players. The game saved later will overwrite the "
                    + "orginal record!");
            String cmd = "e";
            while(cmd.equals("e")){
                cmd = sc.nextLine();
                switch(cmd){
                    case "y":
                    case "Y":
                        board = gr.LoadBoard(gamename);
                        turn = gr.LoadTrun(gamename);
                        break;
                    case "n":
                    case "N":
                        break;
                    default:
                        System.out.println("Illegal enter!");
                        cmd = "e";
                        break;
                }
            }
        }
        
        System.out.println("16x16 Gomoku game. Enter row and column (1-16), or enter 'q' to quit.");
        System.out.println("Enter S can save the match.");
        board.printBoard();

        while (true) {
            Player current = players[turn % 2];
            System.out.print(current.getName() + " (" + current.getPiece().getSymbol() + ") enter coordinates: ");

            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("quit")) {
                System.out.println("Game over. Final board:");
                board.printBoard();
                break;
            }
            
            if (input.equalsIgnoreCase("s") || input.equalsIgnoreCase("S") 
                    || input.equalsIgnoreCase("save")) {
                
                gr.SaveGame(gamename, turn, board.getBoard());
                System.out.println("Game save. Final board:");
                board.printBoard();
                break;
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Invalid input. Please try again!");
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0]);
                int col = Integer.parseInt(parts[1]);
                if (!board.placePiece(row, col, current.getPiece())) {
                    System.out.println("Invalid move. Please choose again.");
                    continue;
                }
                board.printBoard();

                // Check if the player wins
                if (board.checkWin(row, col, current.getPiece())) {
                    System.out.println("Congratulations! " + current.getName() + " wins! 🎉");
                    pr.UpdateScore(current.getName(),current);
                    pr.SaveRecord();
                    break;
                }
                

                turn++;
            } catch (NumberFormatException e) {
                System.out.println("Please enter two integers.");
            }
        }

        sc.close();
    }
    
    public void List(){
        pr.ShowList();
    }
}