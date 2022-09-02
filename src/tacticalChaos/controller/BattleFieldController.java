package tacticalChaos.controller;

import java.util.ArrayList;
import java.util.Random;

import javafx.util.Pair;
import tacticalChaos.Main;
import tacticalChaos.model.BattleField;
import tacticalChaos.model.Champion;
import tacticalChaos.model.Item;

public class BattleFieldController {

    BattleField field;

    BattleFieldController(BattleField field){
        this.field = field;
    }

    public void generateRandomField(){
        Random rand = new Random();

        int n=field.n;
        int m=field.m;

        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(GameController.chance(10)){
                    field.type[i][j] = rand.nextInt(3) + 2 ;
                }else{
                    field.type[i][j]=1;
                }
                if(GameController.chance(5) && field.type[i][j]!=3){
                    addItem(Main.itemAttributes.get(Main.itemsNames.get(rand.nextInt(Main.itemsNames.size()))).clone(),new Pair<>(i,j));
                }
            }
        }
    }

    void addRandomly(Item item){
        Random rand = new Random();
        boolean ok=false;
        int cnt=0;
        while (!ok){
            cnt++;
            if(cnt==200)break;
            int x = rand.nextInt(Main.n);
            int y = rand.nextInt(Main.m);
            if(field.type[x][y]!=3) {
                addItem(item,new Pair<>(x,y));
                ok=true;
            }
        }
        if(!ok){
            for(int i=0;i<Main.n;i++){
                for (int j=0;j<Main.m;j++){
                    if(field.type[i][j]!=3){
                        addItem(item,new Pair<>(i,j));
                        return;
                    }
                }
            }
        }
    }

    void addItem(Item item,Pair<Integer,Integer>pos){
        field.item[pos.getKey()][pos.getValue()].add(item);
    }

    void removeItem(Item item, Pair<Integer,Integer>pos){
        field.item[pos.getKey()][pos.getValue()].remove(item);
    }

    void addChampion(Champion champion,Pair<Integer,Integer> pa){
        field.battleField[pa.getKey()][pa.getValue()].add(champion);
        Main.championPosition.put(champion.name,pa);
    }

    void removeChampion(Champion champion){
        Pair<Integer,Integer> pos = Main.championPosition.get(champion.name);
        if(pos==null) return;
        field.battleField[pos.getKey()][pos.getValue()].remove(champion);
        //Main.championPosition.put(champion.name,null);
    }

    public int getType(Pair<Integer,Integer>pos){
        return field.type[pos.getKey()][pos.getValue()];
    }

    public ArrayList<Champion> getAList(Pair<Integer,Integer>pa){
        return field.battleField[pa.getKey()][pa.getValue()];
    }

    Champion getChampionFromAList(String name){
        Pair<Integer,Integer>pa=Main.championPosition.get(name);
        if(pa==null) return null;
        ArrayList<Champion> list = field.battleField[pa.getKey()][pa.getValue()];
        for (Champion champion : list) {
            if (champion.name.equals(name)) {
                return champion;
            }
        }
        return null;
    }
}
