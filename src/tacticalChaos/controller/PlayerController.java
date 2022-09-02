package tacticalChaos.controller;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.util.Pair;

import tacticalChaos.exceptions.*;
import tacticalChaos.Main;
import tacticalChaos.model.*;
import tacticalChaos.view.PlayerIO;

public class PlayerController {

    int increasingGolds = 2;
    static int maxSwapsChampion = 2;

    Player player;
    public PlayerIO playerIO;
    BattleFieldController fieldController;

    PlayerController(Player player,BattleFieldController fieldController,PlayerIO playerIO){
        this.player = player;
        this.playerIO=playerIO;

        this.fieldController=fieldController;
    }

    int getNumberOfChampion(Champion champion){
        int idx = Main.championID.get(champion.attributes.baseName);
        return player.cnt_champs[idx][champion.lv-1];
    }

    boolean haveChampionKind(String name){
        for(int i=0;i<3;i++)
            if(player.cnt_champs[Main.championID.get(name)][i]>0) return true;

        return false;
    }

    // reset all champions' extras
    void reset(){
        for (Champion champ : player.champs) {
            if(champ==null) continue;
            ChampionController.resetExtra(champ);
        }
    }

    int getNumberOfAGroup(String group){
        return player.cnt_groups[Main.groupID.get(group)];
    }

    boolean GroupDisActivated(String group){
        return getNumberOfAGroup(group) == Main.groupRequirement.get(group) - 1;
    }

    boolean groupAbilityActivated(String groupName){
        if(groupName==null) return false;
        return player.cnt_groups[Main.groupID.get(groupName)] >= Main.groupRequirement.get(groupName);
    }

    // update appearance array (cells visibility) according to player's champions vision ranges
    void updateAppearance(){
        for(int i=0;i<Main.n;i++){
            for(int j=0;j<Main.m;j++){
                player.appear[i][j]=false;
            }
        }

        for (Champion ch : player.champs) {
            Pair<Integer, Integer> pa = Main.championPosition.get(ch.name);
            if(pa==null) return;
            int vr = (int) ch.attributes.visionRange;

            ArrayList<Pair<Integer, Integer>> range = GameController.getRange(pa, vr);

            for (Pair<Integer, Integer> idx : range) {
                if(fieldController.getType(idx)==2){
                    if(Math.max(Math.abs(pa.getKey()-idx.getKey()),Math.abs(pa.getValue()-idx.getValue()))<=vr/2)
                        player.appear[idx.getKey()][idx.getValue()] = true;
                }else{
                    player.appear[idx.getKey()][idx.getValue()] = true;
                }
            }
        }
    }

    // at the first round, all cells are invisible expect player's champions positions
    void updateAppearanceForFirstPhase(){
        for(int i=0;i<Main.n;i++){
            for(int j=0;j<Main.m;j++){
                player.appear[i][j]=false;
            }
        }

        for (Champion ch : player.champs) {
            Pair<Integer, Integer> pa = Main.championPosition.get(ch.name);
            if(pa==null) return;
            player.appear[pa.getKey()][pa.getValue()]=true;
        }
    }

    void increaseGolds(){
        player.golds+=increasingGolds;
        playerIO.updateGoldsValueLabel();
    }

    void decreaseGolds(double dec){
        player.golds-=dec;
        playerIO.updateGoldsValueLabel();
    }

    // swap between a champion on the field and another on the bench
    void swap(Champion chField,Champion chBench){

        Pair<Integer,Integer>pos=Main.championPosition.get(chField.name);
        if(pos==null) return;

        removeFromBench(chBench);
        removeFromField(chField);

        addToField(chBench);
        addToBench(chField);

        fieldController.addChampion(chBench,pos);
        fieldController.removeChampion(chField);
        Main.championPosition.put(chField.name,new Pair<>(-1,-1));
    }

    void addToField(Champion ch){
        player.champs.add(ch);
        ch.inField=true;
        int idx = Main.championID.get(ch.attributes.baseName);
        player.cnt_champs[idx][ch.lv-1]++;
        for(String group:ch.attributes.groups){
            player.cnt_groups[Main.groupID.get(group)]++;
        }
    }

    void addToBench(Champion ch){
        player.champsBench.add(ch);
        ch.inField=false;
        int idx = Main.championID.get(ch.attributes.baseName);
        player.cnt_champs[idx][ch.lv-1]++;
        for(String group:ch.attributes.groups){
            player.cnt_groups[Main.groupID.get(group)]++;
        }
    }

    void removeFromField(Champion ch){
        synchronized (ch){
            player.cnt_champs[Main.championID.get(ch.attributes.baseName)][ch.lv-1]--;
            for(String group:ch.attributes.groups){
                player.cnt_groups[Main.groupID.get(group)]--;
            }

            for(int i=0;i<player.champs.size();i++){
                if(player.champs.get(i)==null)continue;
                if(player.champs.get(i).name.equals(ch.name)){
                    player.champs.remove(i);
                    //player.champs.add(i,null);
                    break;
                }
            }
        }
    }

    void removeFromBench(Champion ch){
        player.champsBench.remove(ch);
        player.cnt_champs[Main.championID.get(ch.attributes.baseName)][ch.lv-1]--;
        for(String group:ch.attributes.groups){
            player.cnt_groups[Main.groupID.get(group)]--;
        }
    }

    ArrayList<Champion> getChampionsInField(){
        return player.champs;
    }

    ArrayList<Champion> getChampionsInBench(){
        return player.champsBench;
    }

    void deleteChampion(Champion champion){
        GameController.receiveItems(champion);
        if(champion.inField) {
            removeFromField(champion);
            fieldController.removeChampion(champion);
        }else{
            removeFromBench(champion);
        }
    }

    void displayEnvironment(){
        playerIO.updateGoldsValueLabel();
        //playerIO.updateButtons();
        playerIO.showArena(fieldController.field);
        playerIO.showBench();
    }

    void firstPhase(){

        for(int i=0;i<9;i++){

            playerIO.setState("First Phase "+(i+1));
            increaseGolds();

            ArrayList<Champion> tempStore = GameController.storeController.generateTempStore();

            while (buyAndPos(tempStore)) {
                updateAppearanceForFirstPhase();
                displayEnvironment();
            }

            GameController.storeController.receiveTempStore(tempStore);
        }

        playerIO.equippingItems();

        for (Item item:player.items){
            fieldController.addRandomly(item);
        }

        player.items.clear();
    }

    void planningRound() {

        int[] speeds =new int[10];

        int swaps = 0;

        playerIO.setState("Planning");

        reset();
        increaseGolds();

        ArrayList<Champion> tempStore = GameController.storeController.generateTempStore();

        while (true) {
            updateAppearance();
            displayEnvironment();

            playerIO.updateButtons();
            playerIO.showTempStore(tempStore, false);

            ArrayList<Champion> champs = getChampionsInField();
            ArrayList<Champion> champsBench = getChampionsInBench();

            int choice = playerIO.selectCommand();

            // Exit
            if (choice==champs.size()+champsBench.size()+1) break;

            // Buy
            if (choice == 0) { buyAndPos(tempStore); continue; }

            Champion ch;
            boolean inField;

            // Select Champion
            if (choice <= champs.size()) {
                ch = champs.get(choice - 1);
                inField = true;
            } else {
                ch = champsBench.get(choice - 1 - champs.size());
                inField = false;
            }

            // player selects a champion that he sold in the same round
            if (ch.sold) {
                playerIO.notification("Champion was sold!");
                continue;
            }

            if(ch.moves.size()==3) {
                playerIO.notification("moves limit reached for this champion!");
            }

            // 'moves' is an array which contains all possible moves that player can confirm according to the current state of the game.
            // For each move of all game moves, we check if it meets some necessary condition(s) that make it possible to be applied in the current state.
            // Some moves have sub-moves which also have to be checked if the player has selected one of them.
            ArrayList<String> moves = new ArrayList<>();

            ArrayList<String> directions = new ArrayList<>();
            int originalSpeed, speed, timeCost, x2, y2;

            if (inField) {
                Pair<Integer, Integer> pa = Main.championPosition.get(ch.name);
                int y = pa.getKey();
                int x = pa.getValue();
                originalSpeed = (int)ch.attributes.movementSpeed + ch.extraMovementSpeed;

                boolean ok;

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(x2>=Main.m) { speed=100000; break; }
                    timeCost += fieldController.getType(new Pair<>(y2,x2++))==4? 2 : 1 ;
                }
                speeds[0]=speed;
                if (x + speed < Main.m) {
                    ok=true;
                    for (int i=x+1;i<=x+speed;i++){
                         
                        if(fieldController.getType(new Pair<>(y,i))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("right");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(x2<0) { speed=100000; break; }
                      
                    timeCost += fieldController.getType(new Pair<>(y2,x2--))==4? 2 : 1 ;
                }
                speeds[1]=speed;
                if (x - speed >= 0) {
                    ok=true;
                    for (int i=x-1;i>=x-speed;i--){
                        if(fieldController.getType(new Pair<>(y,i))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("left");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(y2<0) { speed=100000; break; }
                      
                    timeCost += fieldController.getType(new Pair<>(y2--,x2))==4? 2 : 1 ;
                }
                speeds[2]=speed;
                if (y - speed >= 0){
                    ok=true;
                    for (int i=y;i>=y-speed;i--){
                        if(fieldController.getType(new Pair<>(i,x))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("up");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(y2>=Main.n) { speed=100000; break; }
                      
                    timeCost += fieldController.getType(new Pair<>(y2++,x2))==4? 2 : 1 ;
                }
                speeds[3]=speed;
                if (y + speed < Main.n){
                    ok=true;
                    for (int i=y;i<=y+speed;i++){
                        if(fieldController.getType(new Pair<>(i,x))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("down");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(x2>=Main.m) { speed=100000; break; }
                    if(y2<0) { speed=100000; break; }

                    timeCost += fieldController.getType(new Pair<>(y2--,x2++))==4? 2 : 1 ;
                }
                speeds[4]=speed;
                if (x + speed < Main.m && y - speed >= 0){
                    ok=true;
                    for (int i=1;i<=speed;i++){
                        if(fieldController.getType(new Pair<>(y-i,x+i))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("right & up");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(x2>=Main.m) { speed=100000; break; }
                    if(y2>=Main.n) { speed=100000; break; }
                      
                    timeCost += fieldController.getType(new Pair<>(y2++,x2++))==4? 2 : 1 ;
                }
                speeds[5]=speed;
                if (x + speed < Main.m && y + speed < Main.n){
                    ok=true;
                    for (int i=1;i<=speed;i++){
                        if(fieldController.getType(new Pair<>(y+i,x+i))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("right & down");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(x2<0) { speed=100000; break; }
                    if(y2<0) { speed=100000; break; }
                      
                    timeCost += fieldController.getType(new Pair<>(y2--,x2--))==4? 2 : 1 ;
                }
                speeds[6]=speed;
                if (x - speed >= 0 && y - speed >= 0){
                    ok=true;
                    for (int i=1;i<=speed;i++){
                        if(fieldController.getType(new Pair<>(y-i,x-i))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("left & up");
                }

                timeCost=0; speed=0; x2=x; y2=y;
                while (timeCost<originalSpeed){
                    speed++;
                    if(x2<0) { speed=100000; break; }
                    if(y2>=Main.n) { speed=100000; break; }
                      
                    timeCost += fieldController.getType(new Pair<>(y2++,x2--))==4? 2 : 1 ;
                }
                speeds[7]=speed;
                if (x - speed >= 0 && y + speed < Main.n) {
                    ok=true;
                    for (int i=1;i<=speed;i++){
                        if(fieldController.getType(new Pair<>(y+i,x-i))==3){
                            ok=false;
                            break;
                        }
                    }
                    if(ok)
                        directions.add("left & down");
                }
                directions.add("Exit");

                if (ch.canMove && directions.size()>1) moves.add("Movement");
                if (ch.canAttack) moves.add("Basic Attack");
                if (ch.canUseAbility && ch.mana >= ch.attributes.manaCost) moves.add("Use Ability");

            } else {
                if (champs.size() < player.maxChampsInField) moves.add("Set Position");
            }

            if (swaps != maxSwapsChampion) {
                if (inField) {
                    if (champsBench.size() > 0) moves.add("Swap");
                } else {
                    moves.add("Swap");
                }
            }
            moves.add("Sell");
            moves.add("Exit");

            int moveChoice = playerIO.selectMove(moves);

            String s = moves.get(moveChoice - 1);

            if (s.equals("Movement")) {

                GameController.Movement move = new GameController.Movement();

                int dirChoice = playerIO.selectDirection(directions);

                String s2 = directions.get(dirChoice - 1);

                if(s2.equals("Exit")) continue;

                ch.canMove = false;
                if (s2.equals("up")) move.setValue(0, speeds[2]);
                else if (s2.equals("down")) move.setValue(0, -speeds[3]);
                else if (s2.equals("right")) move.setValue(speeds[0], 0);
                else if (s2.equals("left")) move.setValue(-speeds[1], 0);
                else if (s2.equals("right & up")) move.setValue(speeds[4], speeds[4]);
                else if (s2.equals("right & down")) move.setValue(speeds[5], -speeds[5]);
                else if (s2.equals("left & up")) move.setValue(-speeds[6], speeds[6]);
                else move.setValue(-speeds[7], -speeds[7]);

                ChampionController.addToMove(ch,move);

            } else if (s.equals("Basic Attack")) {
                ch.canAttack = false;

                String s2 = playerIO.selectEnemy();

                GameController.BasicAttack move = new GameController.BasicAttack();
                move.setValue(s2);

                ChampionController.addToMove(ch,move);
            } else if (s.equals("Sell")) {
                ch.sold = true;
                GameController.Sell sell = new GameController.Sell();
                ChampionController.addToMove(ch,sell);
            } else if (s.equals("Swap")) {
                Champion ch2;
                if (inField) {
                    int champ2Choice = playerIO.selectChampFromBench();
                    if(champ2Choice==champsBench.size()+1) continue;
                    swaps++;
                    ch2 = champsBench.get(champ2Choice - 1);
                    swap(ch, ch2);
                } else {
                    int champ2Choice = playerIO.selectChampFromField();
                    if(champ2Choice==champs.size()+1) continue;
                    swaps++;
                    ch2 = champs.get(champ2Choice - 1);
                    swap(ch2, ch);
                }
            } else if (s.equals("Set Position")) {

                Pair<Integer, Integer> pos;
                while (true){
                    try {
                        pos = playerIO.selectPositionInField();
                        break;
                    }catch (OutOfFieldException ex){
                        playerIO.notification("out of field!");
                    }catch (Exception ex) { ex.printStackTrace(); }
                }

                removeFromBench(ch);
                addToField(ch);

                fieldController.addChampion(ch,pos);
            } else if (s.equals("Use Ability")) {
                ch.canUseAbility = false;
                ch.mana -= ch.attributes.manaCost;
                ChampionController.addToMove(ch,GameController.getAbility(ch.attributes.baseName));
            }
        }

        GameController.storeController.receiveTempStore(tempStore);

    }

    void closeWindow(){
        playerIO.closeWindow();
    }

    // buying champions from the temporal store and positioning them on the field or on the bench
    boolean buyAndPos(ArrayList<Champion> tempStore){

        if(tempStore.isEmpty()){
            playerIO.notification("temporal store is empty!");
            return false;
        }

        playerIO.showTempStore(tempStore, true);

        playerIO.notification("0- Exit");
        playerIO.notification("");

        if(player.champs.size()==player.maxChampsInField && player.champsBench.size()==player.maxChampsInBench){
            playerIO.notification("you can't buy more champions!");
            return false;
        }

        boolean canBuy=false;
        for (Champion champion : tempStore) {
            if (champion.attributes.goldCost <= player.golds) {
                canBuy = true;
                break;
            }
        }

        if(!canBuy){
            //playerIO.notification("no enough golds to buy! -> nextPhase");
            return false;
        }

        Champion ch;

        while (true)
        {
            try {
                ch = playerIO.buy(tempStore);
                break;
            } catch (NoEnoughGoldsException ex){
                playerIO.notification("no enough golds!");
            } catch (Exception e) {e.printStackTrace();}
        }

        if (ch == null) return false;

        decreaseGolds(ch.attributes.goldCost);

        ChampionController.setPlayer(ch,player.number,getNumberOfChampion(ch)+1);

        tempStore.remove(ch);

        String Name = ch.attributes.baseName;
        int Lv = ch.lv;

        int idx = Main.championID.get(Name);

        // level up!
        if(player.cnt_champs[idx][Lv-1]==2 && Lv!=3){

            ch = ChampionController.newChampion(Name,Lv+1);

            ChampionController.setPlayer(ch,player.number,player.cnt_champs[idx][Lv]+1);

            for (int i=0;i<player.champs.size();i++){
                Champion champion = player.champs.get(i);
                if (champion.attributes.baseName.equals(Name) && champion.lv == Lv) deleteChampion(champion);
            }
            for (int i=0;i<player.champsBench.size();i++){
                Champion champion = player.champsBench.get(i);
                if (champion.attributes.baseName.equals(Name) && champion.lv == Lv) deleteChampion(champion);
            }
        }

        Pair<Integer, Integer> pos;

        if(player.champs.size()==player.maxChampsInField) {
            playerIO.notification("can't add to field!  champion was added to bench");
            pos = new Pair<>(-1,-1);
        }else if(player.champsBench.size()==player.maxChampsInBench){
            playerIO.notification("can't add to bench!");
            while (true){
                try {
                    pos = playerIO.selectPositionInField();
                    break;
                }catch (OutOfFieldException ex){
                    playerIO.notification("out of field!");
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
        else{
            pos = playerIO.selectPosition(ch);
        }

        if (pos.getKey() == -1) {
            addToBench(ch);
        } else {
            if(GameController.state.equals("First Round")){
                Iterator<Item>iterator = fieldController.field.item[pos.getKey()][pos.getValue()].iterator();
                while (iterator.hasNext()) {
                    player.items.add(iterator.next());
                    iterator.remove();
                }
            }else{
                Iterator<Item>iterator = fieldController.field.item[pos.getKey()][pos.getValue()].iterator();
                while (iterator.hasNext()) {
                    if(ch.items.size()==3)break;
                    ChampionController.addItem(ch,iterator.next());
                    iterator.remove();
                }
            }
            addToField(ch);
            fieldController.addChampion(ch,pos);
        }
        return true;
    }
}
