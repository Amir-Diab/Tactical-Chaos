package tacticalChaos.model;

import java.io.Serializable;
import java.util.ArrayList;

import tacticalChaos.Main;

public class Player implements Serializable {

    public boolean [][]appear;                          // which cells are apparent to the player and which are not
    public ArrayList<Item> items = new ArrayList<>();   // items that the player has

    public int teamNumber;          // the team's number that the player belongs to
    public boolean lose = false;    // true if the player lose the game

    public String name;
    public int number;              // the player's id
    public double golds;

    public int [][]cnt_champs;      // the count of champions the player has for all champions in the game grouped by the three levels
    public int []cnt_groups;        // the count of champions' groups the player has

    public boolean isHuman;         // false if the player is a bot

    public ArrayList<Champion> champs = new ArrayList <>();     // player's field champions
    public ArrayList<Champion> champsBench = new ArrayList<>(); // player's bench champions

    public int maxChampsInField, maxChampsInBench;          // the maximum number of champions allowed for a player
                                                            // to has on the field and the bench respectively

    public Player(String name,boolean isHuman,int number,int teamNumber,int maxChampsInField,int maxChampsInBench){

        cnt_champs=new int[Main.championID.size()][3];
        cnt_groups=new int[Main.groupID.size()];
        appear = new boolean[Main.n][Main.m];

        this.isHuman = isHuman;
        this.name = name;
        this.number = number;
        this.teamNumber=teamNumber;
        this.maxChampsInField=maxChampsInField;
        this.maxChampsInBench=maxChampsInBench;
    }
}
