package tacticalChaos.controller;

import java.util.ArrayList;
import java.util.Random;

import javafx.util.Pair;
import tacticalChaos.Main;
import tacticalChaos.model.BattleField;
import tacticalChaos.model.Champion;
import tacticalChaos.model.Item;
import tacticalChaos.model.Player;
import tacticalChaos.view.PlayerIO;

public class BotPlayerIO implements PlayerIO {
    Player player;
    int[][] type;
    private Champion selectedChampion;
    private boolean hasBuy = false;
    private int champNumber = 1, champBenchNumber = 1;
    private boolean wantToAttack = true, willAttack = false;
    private String enemy = null;

    BotPlayerIO(Player player, int[][] type) {
        this.player=player;
        this.type=type;
    }

    @Override
    public int selectCommand() {
        if (!hasBuy) {
            hasBuy = true;
            return 0;
        }
        if (champBenchNumber <= player.champsBench.size()) {
            selectedChampion = player.champsBench.get(champBenchNumber - 1);
            return champBenchNumber + player.champs.size();
        }
        if (champNumber <= player.champs.size()){
            selectedChampion = player.champs.get(champNumber - 1);
            if(selectedChampion.moves.size()==3){
                champNumber++;
            }
        }

        if(champNumber<=player.champs.size()) return champNumber;

        champNumber = 1;
        champBenchNumber = 1;
        hasBuy = false;

        return player.champs.size()+player.champsBench.size()+1;
    }

    public int selectMove(ArrayList<String> moves) {

        if (champBenchNumber <= player.champsBench.size()) {
            champBenchNumber++;
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).equals("Set Position")) {
                    return i + 1;
                }
            }
            return moves.size();
        }

        if (wantToAttack) {
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).equals("Basic Attack")) {
                    if (willAttack) return i + 1;
                    enemy = GameController.getEnemyInRange(selectedChampion);
                    if (enemy == null) {
                        wantToAttack = false;
                        return moves.size();
                    }
                    willAttack = true;
                    for (int j = 0; j < moves.size(); j++) {
                        if (moves.get(j).equals("Use Ability")) {
                            return j + 1;
                        }
                    }
                    return i + 1;
                }
            }
        }

        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).equals("Movement")) {
                return i + 1;
            }
        }

        if (selectedChampion.health < 50) {
            for (int i = 0; i < moves.size(); i++) {
                if (moves.get(i).equals("Swap")) {
                    return i + 1;
                }
            }
        }

        wantToAttack = true;
        willAttack = false;
        enemy = null;
        champNumber++;

        return moves.size();
    }

    public int selectDirection(ArrayList<String> directions) {
        return new Random().nextInt(directions.size()) + 1;
    }

    public int selectChampFromField() {
        for (int i = 0; i < player.champs.size(); i++) {
            if (player.champs.get(i).health < 50)
                return i + 1;
        }
        return 1;
    }

    public int selectChampFromBench() {
        for (int i = 0; i < player.champsBench.size(); i++) {
            if (player.champsBench.get(i).health > 50)
                return i + 1;
        }
        return 1;
    }

    public Champion buy(ArrayList<Champion> list) {
        if (player.champs.size() == player.maxChampsInField && player.champsBench.size() == player.maxChampsInBench) {
            return null;
        }

        for (Champion champion : list) {
            if (champion.attributes.goldCost <= player.golds) {
                return champion;
            }
        }
        return null;
    }

    public Pair<Integer, Integer> selectPosition(Champion champion) {
        if (player.champs.size() == player.maxChampsInField) {
            return new Pair<>(-1, -1);
        } else {
            return selectPositionInField();
        }
    }

    public Pair<Integer, Integer> selectPositionInField() {
        int x=0, y=0, n = Main.n, m = Main.m;
        int cnt=0;
        Random rand = new Random();
        boolean ok=false;
        while (!ok){
            cnt++;
            if(cnt==200){
                for(int i=0;i<n&&!ok;i++){
                    for (int j=0;j<m&&!ok;j++){
                        if(type[i][j]!=3){
                            x=i;
                            y=j;
                            ok=true;
                        }
                    }
                }
                break;
            }
            if (player.champs.size() == 0) {
                x = rand.nextInt(n);
                y = rand.nextInt(m);
                if(type[x][y]!=3){
                    ok=true;
                }
            } else {

                Pair<Integer,Integer>pa=Main.championPosition.get(player.champs.get(0).name);
                if(pa==null) return new Pair<>(0,0);

                x = pa.getKey();
                y = pa.getValue();

                if (rand.nextBoolean()) {
                    x += rand.nextInt(Math.min(11, n - x));
                } else {
                    x -= rand.nextInt(Math.min(11, x + 1));
                }

                if (rand.nextBoolean()) {
                    y += rand.nextInt(Math.min(11, m - y));
                } else {
                    y -= rand.nextInt(Math.min(11, y + 1));
                }

                if(type[x][y]!=3){
                    ok = true;
                }
            }
        }

        return new Pair<>(x, y);
    }

    public String selectEnemy() {
        return enemy;
    }

    @Override
    public void equippingItems() {
        int canGive = 3*player.champs.size();

        int choice = 1;

        while (!player.items.isEmpty() && canGive>0){

            Item item = player.items.get(0);

            if (player.champs.get(choice-1).items.size()==3){
                choice++;
            }

            ChampionController.addItem(player.champs.get(choice-1),item);
            player.items.remove(item);
        }
    }

    @Override
    public void notification(String message) { }

    @Override
    public void setState(String state) { }

    @Override
    public void showArena(BattleField field) { }

    @Override
    public void showBench() { }

    @Override
    public void showTempStore(ArrayList<Champion> list, boolean numbered) { }

    @Override
    public void updateButtons() { }

    @Override
    public void closeWindow() { }

    @Override
    public void updateGoldsValueLabel() { }

}
