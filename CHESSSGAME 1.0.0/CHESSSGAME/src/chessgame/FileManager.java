package chessgame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 文件读写类
 * 保存/加载游戏
 */
public class FileManager {

}

class GameRecord {
    public HashMap<String,Integer> gamesave = new HashMap<>();
    private Set Game = gamesave.keySet();
    private Iterator it = Game.iterator();
    private final String txt_located ="./data/gamerecord.txt";
    private String board_located ="./data/Board/";
    
    public GameRecord(){}
    
    public void LoadGameList(){
        try{
            FileReader fr = new FileReader(txt_located);
            BufferedReader inputStream = new BufferedReader(fr);
            String line = null;
            while ((line = inputStream.readLine())!=null){
                String[] gamerecord = line.split(" ");
                if (gamerecord.length>1)
                    gamesave.put(gamerecord[0],Integer.valueOf(gamerecord[1]));
            }
            fr.close();
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    public static String rtgamename(Player player1,Player player2){
        String gamename;
        gamename = player1.getName() + "&" + player2.getName();
        return gamename;
    }
    
    public void SavePlayers(String GameName,int turn){
        gamesave.put(GameName, turn);
        PrintWriter pw = null;
        try{
            FileOutputStream fos = new FileOutputStream(txt_located);
            pw = new PrintWriter(fos);
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        it = Game.iterator();
        while (it.hasNext()){
            Object result = it.next();
            pw.println(result+" "+gamesave.get(result));
        }
        pw.close();
    }
    
    public void SaveBoard(String gamename,ChessPiece[][] grid){
        String Board_path = board_located + gamename + ".txt";
        try {
            File dir = new File(board_located);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(Board_path);
            if (!file.exists()) {
                file.createNewFile();
                //System.out.println("create new save: " + Board_path);
            }

            try (PrintWriter pw = new PrintWriter(new FileOutputStream(file))) {
                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[i].length; j++) {
                        pw.print(grid[i][j].toString());
                        pw.print(" ");
                    }
                    pw.println();
                }
            }

        } catch (IOException e) {
            System.out.println("fali to save Board: " + e.getMessage());
        }
    }
    
    public void SaveGame(String gamename,int turn,ChessPiece[][] grid){
        this.SavePlayers(gamename,turn);
        this.SaveBoard(gamename, grid);
    }
    
    public Board LoadBoard(String gamename){
        String Board_path = board_located + gamename + ".txt";
        Board tempb = new Board();
        try{
            FileReader fr = new FileReader(Board_path);
            BufferedReader inputStream = new BufferedReader(fr);
            String line = null;
            int i = 0;
            while ((line = inputStream.readLine())!=null){
                String[] Boarddate = line.split(" ");
                for (int j = 0;j < Boarddate.length;j++){
                    if (Boarddate[j].equals("BLACK")){
                        tempb.placePiece(i+1,j+1,ChessPiece.BLACK);
                    }
                    else if(Boarddate[j].equals("WHITE")){
                        tempb.placePiece(i+1,j+1,ChessPiece.WHITE);
                    }
                }
                i++;
            }
            
            fr.close();
            return tempb;
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return tempb;
    }
    
    public int LoadTrun(String gamename){
        return gamesave.get(gamename);
    }
    
}

class PlayerRecord{
    public HashMap<String,Player> player = new HashMap<>();
    private Set Name = player.keySet();
    private Iterator it = Name.iterator();
    private final String txt_located ="./data/playerlist.txt";
    
    public PlayerRecord(){
    }
    
    public void LoadRecord(){
        try{
            FileReader fr = new FileReader(txt_located);
            BufferedReader inputStream = new BufferedReader(fr);
            String line = null;
            Player tempp;
            while ((line = inputStream.readLine())!=null){
                String[] playerdata = line.split(" ");
                if (playerdata[1].equals("X")){
                    tempp = new Player(playerdata[0],ChessPiece.BLACK);
                    tempp.addScore(Integer.parseInt(playerdata[2]));
                }
                else{
                    tempp = new Player(playerdata[0],ChessPiece.WHITE);
                    tempp.addScore(Integer.parseInt(playerdata[2]));
                }
                if (playerdata.length>1)
                    player.put(playerdata[0],tempp);
            }
            
            fr.close();
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    public void SaveRecord(){
        PrintWriter pw = null;
        try{
            FileOutputStream fos = new FileOutputStream(txt_located);
            pw = new PrintWriter(fos);
        }
        catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        it = Name.iterator();
        while (it.hasNext()){
            Object result = it.next();
            pw.println(result+" "+player.get(result).getPiece().getSymbol()+" "
                    +player.get(result).getScore());
        }
        pw.close();
    }
    
    public void UpdateScore(String name,Player Player){
        Player.addScore(1);
        player.put(name, Player);
    }
    
    public void UpdatePlayerlist(Player Player1,Player Player2){
        
        if(player.containsKey(Player1.getName())){
            Player1.addScore(player.get(Player1.getName()).getScore());
        }
        if(player.containsKey(Player2.getName())){
            Player2.addScore(player.get(Player2.getName()).getScore());
        }
        
        player.put(Player1.getName(), Player1);
        player.put(Player2.getName(), Player2);
    }
    
    public void ShowList(){
        it = Name.iterator();
        while (it.hasNext()){
            Object result = it.next();
            System.out.println("Name:"+result+" Score:"
                    +player.get(result).getScore());
        }
    }
    
}
