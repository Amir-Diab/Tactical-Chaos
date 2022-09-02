package tacticalChaos.view;

import java.util.ArrayList;
import java.util.Scanner;

import javafx.util.Pair;
import tacticalChaos.Main;
import tacticalChaos.controller.ChampionController;
import tacticalChaos.model.*;
import tacticalChaos.exceptions.*;

public class ConsolePlayerIO implements PlayerIO {

    Player player;

    int[][] type;

    public ConsolePlayerIO(Player player, int[][] type){
        this.type=type;
        this.player = player;
    }

    int getInt(int st, int ed) {

        Scanner scr=new Scanner(System.in);

        int choice=0;

        boolean ok = false;

        while (!ok){
            ok=true;
            try {
                choice = scr.nextInt();
                if(choice<st||choice>ed){
                    System.out.println("number out of range");
                    ok=false;
                }
            } catch (Exception ex){
                System.out.println("invalid input");
                ok=false;
            }
        }

        return choice;
    }

    public void showArena(BattleField field){
        int index=0;
        ArrayList<String>[] L = new ArrayList[10];
        ArrayList<Pair<String,Pair<Integer,Integer>>> items=new ArrayList<>();

        for (int i=0;i<10;i++)
            L[i]=new ArrayList<>();

        for(int i = 0; i< Main.n; i++){
            System.out.println();
            for(int j=0;j<Main.m;j++){
                if(!player.appear[i][j])System.out.print("????????\t");
                else {
                    for(Item item:field.item[i][j])
                        items.add(new Pair<>(item.name,new Pair<>(i+1,j+1)));

                    ArrayList<Champion> champions = field.battleField[i][j];
                    int type = field.type[i][j];
                    if (champions.size() == 0) {
                        if(type==1)
                            System.out.print("________\t");
                        else if(type==2)
                            System.out.print("/|/|/|/|\t");
                        else if(type==3)
                            System.out.print("########\t");
                        else
                            System.out.print("@@@@@@@@\t");
                    }
                    else {
                        if(type==2)
                            System.out.print("(G) ");
                        else if(type==4)
                            System.out.print("(W) ");

                        if (champions.size() == 1) {
                            System.out.print(champions.get(0).name+"\t");
                        }
                        else {
                            System.out.print("L"+(index+1)+"      "+"\t");
                            for(int k=0;k<champions.size();k++){
                                L[index].add(champions.get(k).name);
                            }
                            index++;
                        }
                    }
                }
            }
        }
        System.out.println();
        for(int i=0;i<index;i++){
            System.out.print("L"+(i+1)+" : ");
            for (int j=0;j<L[i].size();j++)
                System.out.print(L[i].get(j)+" ");
            System.out.println();
        }
        if(items.size()>0){
            System.out.println("items : ");
            for (int i=0;i<items.size();i++){
                System.out.println(items.get(i).getKey()+" ("+items.get(i).getValue().getKey()+", "+items.get(i).getValue().getValue()+")");
            }
            System.out.println();
        }
    }

    public int selectMove(ArrayList<String> moves){
        displayNumberedList(moves, 1);
        return getInt(1,moves.size());
    }

    @Override
    public void updateButtons() {

    }

    @Override
    public void updateGoldsValueLabel() {

    }

    @Override
    public int selectCommand(){
        System.out.println("0- buy");
        System.out.println("In Field : ");
        displayNumberedListChampion(player.champs, 1);
        System.out.println("\nIn Bench : ");
        displayNumberedListChampion(player.champsBench, player.champs.size() + 1);
        System.out.println(("\n" + player.champs.size()+player.champsBench.size()+1) +"- Exit");
        return getInt(0,player.champs.size()+player.champsBench.size()+1);
    }

    public Pair<Integer,Integer> selectPosition(Champion ch) {

        int choice;

        System.out.println("add to field or Bench ?\n1-field\n2-Bench");
        choice = getInt(1,2);

        if(choice==1){
            while (true){
                try {
                    return selectPositionInField();
                }catch (OutOfFieldException ex){
                    notification("out of field!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return new Pair<>(-1,-1);
    }

    public Champion buy(ArrayList<Champion> list) throws Exception {

        int choice;

        choice = getInt(0,list.size());

        if(choice==0) return null;

        if (list.get(choice-1).attributes.goldCost > player.golds){
            throw new NoEnoughGoldsException();
        }

        return list.get(choice-1);
    }


    public String selectEnemy(){
        System.out.println("champion's enemy name : ");
        return new Scanner(System.in).next();
    }

    public int selectDirection(ArrayList<String>directions) {
        displayNumberedList(directions, 1);
        return getInt(1,directions.size());
    }

    public int selectChampFromField(){
        displayNumberedListChampion(player.champs, 1);
        System.out.println(player.champs.size()+1 +"- Exit");
        return getInt(1,player.champs.size()+1);
    }

    public int selectChampFromBench(){
        displayNumberedListChampion(player.champsBench, 1);
        System.out.println(player.champsBench.size()+1 +"- Exit");
        return getInt(1,player.champsBench.size()+1);
    }

    public Pair<Integer,Integer> selectPositionInField() throws Exception {
        Scanner scr = new Scanner(System.in);
        System.out.println("select position of champion :");
        System.out.println("row = ");
        int row = scr.nextInt();
        System.out.println("column = ");
        int column = scr.nextInt();

        if(row<1||row>Main.n||column<1||column>Main.m){
            System.out.println("please enter a position inside the field");
            throw new OutOfFieldException();
        }
        if(type[row][column]==3) {
            System.out.println("can't add champion on terrain cell!");
            throw new LocatingOnTerrainException();
        }

        return new Pair<>(row-1,column-1);
    }


    private void displayNumberedList(ArrayList list,int st){
        for(int i=0;i<list.size();i++){
            System.out.println(st++ +"- "+list.get(i));
        }
    }

    private void displayNumberedListChampion(ArrayList<Champion> list,int st) {
        for (Champion champion : list) {
            System.out.println(st++ + "- " + champion.name);
        }
    }

    @Override
    public void notification(String message) {
        System.out.println(message);
    }

    @Override
    public void setState(String state) {
        System.out.println(state);
    }

    @Override
    public void showBench() {
        System.out.print("Bench : ");
        for (int i = 0; i < player.champsBench.size(); i++) System.out.print(player.champsBench.get(i).name + " ");
        System.out.println();
    }

    @Override
    public void showTempStore(ArrayList<Champion> list, boolean numbered){
        System.out.println("Golds = "+player.golds);
        System.out.println("store : \nChampion\tCost");
        for(int i=0;i< list.size();i++) {
            Champion champion = list.get(i);
            if(numbered)
                System.out.print((i+1) + "- ");
            System.out.println(champion.name + "\t" + champion.attributes.goldCost);
        }
    }

    @Override
    public void equippingItems() {

        int canGive = 3*player.champs.size();

        if(!player.items.isEmpty() && canGive>0)
            setState("Equipping items");

        while (!player.items.isEmpty() && canGive>0){

            Item item = player.items.get(0);
            notification(item.name);

            int choice = selectChampFromField();

            if(choice==player.champs.size()+1)break;

            while (player.champs.get(choice-1).items.size()==3){
                notification(item.name+" items limit reached!");
                choice = selectChampFromField();
            }

            ChampionController.addItem(player.champs.get(choice-1),item);
            player.items.remove(item);
        }
    }

    @Override
    public void closeWindow() {

    }
}
