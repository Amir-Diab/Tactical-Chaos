package tacticalChaos.model;

import java.io.Serializable;

public class Game implements Serializable {
    public boolean isConsole;
    public int players_num;
    public Player[] players =new Player[10];    // maximum players number
    public boolean GameOver = false;
    public BattleField field;
    public Store store;
    public String state="First Round";

    public Game(){ }

}
