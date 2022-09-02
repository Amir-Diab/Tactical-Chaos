package tacticalChaos.controller;

import java.io.*;
import java.util.*;

import javafx.util.Pair;
import tacticalChaos.Main;
import tacticalChaos.model.*;
import tacticalChaos.view.*;

public class GameController {

    public static Game game;
    public static String state="First Round";
    public static boolean willSave=false;

    public static StoreController storeController ;
    static BattleFieldController fieldController;
    static PlayerController [] playerControllers;

    FileOutputStream fileOut;
    ObjectOutputStream objectOut;

    public GameController(Game game){

        GameController.game = game;

        storeController = new StoreController(game.store);
        fieldController = new BattleFieldController(game.field);
        playerControllers = new PlayerController[game.players_num+1];

        if(game.isConsole) {
            for (int i = 1; i <= game.players_num; i++) {

                if(game.players[i].isHuman){
                    playerControllers[i]=new PlayerController(game.players[i],fieldController,new ConsolePlayerIO(game.players[i],fieldController.field.type));
                    playerControllers[i].updateAppearance();
                    playerControllers[i].displayEnvironment();
                }
                else
                    playerControllers[i]=new PlayerController(game.players[i],fieldController,new BotPlayerIO(game.players[i],fieldController.field.type));
            }
        }
        else {
            for (int i = 1; i <= game.players_num; i++) {

                if(game.players[i].isHuman){
                    playerControllers[i]=new PlayerController(game.players[i],fieldController,new GuiPlayerIO(game.players[i],fieldController.field.type));
                    playerControllers[i].updateAppearance();
                    playerControllers[i].displayEnvironment();
                }
                else
                    playerControllers[i]=new PlayerController(game.players[i],fieldController,new BotPlayerIO(game.players[i],fieldController.field.type));
            }
        }
    }

    public void startGame() {

        Thread[] thread = new Thread[10];

        if(state.equals("First Round")) {

            storeController.generateRandomStore();
            fieldController.generateRandomField();

            state = "First Round";

            for (int i = 1; i <= game.players_num; i++) {
                if (game.players[i].isHuman && game.isConsole)
                    System.out.println(game.players[i].name + "'s Turn :");
                if (game.isConsole)
                    playerControllers[i].firstPhase();
                else {
                    int finalI = i;
                    thread[i] = new Thread(() -> playerControllers[finalI].firstPhase());
                    thread[i].start();
                }
            }

            if (!game.isConsole) {
                for (int i = 1; i <= game.players_num; i++) {
                    try {
                        thread[i].join();
                    } catch (InterruptedException ignored) {}
                }
            }
            state="Planning Round";
        }

        while (true) {

            if (willSave) {
                saveGame();
                for (int i = 1; i <= game.players_num; i++) {
                    playerControllers[i].closeWindow();
                }
                return;
            }

            if (state.equals("Planning Round")) {
                for (int i = 1; i <= game.players_num; i++) {
                    if (game.players[i].lose) continue;

                    if (game.players[i].isHuman && game.isConsole)
                        System.out.println(game.players[i].name + "'s Turn :");
                    if (game.isConsole)
                        playerControllers[i].planningRound();
                    else {
                        int finalI = i;
                        thread[i] = new Thread(() -> playerControllers[finalI].planningRound());
                        thread[i].start();
                    }
                }

                if (!game.isConsole) {
                    for (int i = 1; i <= game.players_num; i++) {
                        try {
                            thread[i].join();
                        } catch (InterruptedException ignored) {}
                    }
                }
                state = "Executing Round";
            }

            if(state.equals("Executing Round")){
                executingRound();

                int winner = GameOver();
                if ( winner > 0) {
                    game.GameOver = true;
                    for (int i = 1; i <= game.players_num; i++) {
                        playerControllers[i].playerIO.notification("Team "+ winner + " win!!");
                        playerControllers[i].playerIO.setState("Game Over!");
                    }
                    playerControllers[GameOver()].playerIO.notification(game.players[GameOver()].name + " win!!");
                    return;
                }
                state="Planning Round";
            }
        }
    }

    private void saveGame(){
        try {
            game.state=state;

            fileOut = new FileOutputStream("save");
            objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(game);
            objectOut.close();
            fileOut.close();
        }catch (Exception ignored){}
    }

    void executingRound() {

        for (int i = 1; i <= game.players_num; i++) {
            if (game.players[i].lose) continue;
            playerControllers[i].playerIO.setState("Executing");
        }

        for (int i = 1; i <= game.players_num; i++) {
            for (Champion champion : game.players[i].champsBench) {
                for (Move move : champion.moves) {
                    if (move.toString().equals("Sell")) {
                        move.performMove(game.players[i], champion);
                        playerControllers[i].updateAppearance();
                        playerControllers[i].displayEnvironment();
                        /*try {
                            Thread.sleep(1000);
                        }catch (InterruptedException ex){}*/
                    }
                }
            }
        }

        int[] idxChampion = new int[10];
        ArrayList<Champion>[] champions = new ArrayList[10];

        for(int i=1;i<=game.players_num;i++){
            if(game.players[i].lose)continue;
            idxChampion[i]=0;
            champions[i] = playerControllers[i].getChampionsInField();
        }

        while (true){

            boolean ok = false;

            for (int i=1;i<=game.players_num;i++){
                if(game.players[i].lose)continue;
                if(idxChampion[i]<champions[i].size()) {
                    ok = true;
                    break;
                }
            }

            if(!ok)break;

            for (int i=1;i<=game.players_num;i++) {
                if(game.players[i].lose) continue;
                if(idxChampion[i]>=champions[i].size())continue;

                Champion champion = champions[i].get(idxChampion[i]++);
                if (champion == null) continue;

                synchronized (champion) {
                    for (String group : champion.attributes.groups) {
                        if (playerControllers[i].groupAbilityActivated(group)) {
                            getGroupAbility(group).activate(champion, playerControllers[i].getNumberOfAGroup(group));
                            playerControllers[i].updateAppearance();
                            playerControllers[i].displayEnvironment();
                            /*try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                            }*/
                        }
                    }
                }
            }
        }

        int[] idxMove = new int [10];

        for (int i=0;i<10;i++){
            idxChampion[i]=0;
            idxMove[i]=0;
        }

        while (true){

            boolean ok = false;

            for (int i=1;i<=game.players_num;i++){
                if(game.players[i].lose)continue;
                if(idxChampion[i]<champions[i].size()) {
                    ok = true;
                    break;
                }
            }

            if(!ok)break;

            for (int i=1;i<=game.players_num;i++) {
                if(game.players[i].lose) continue;
                if(idxChampion[i]>=champions[i].size())continue;

                Champion champion = champions[i].get(idxChampion[i]);
                if (champion == null) { idxChampion[i]++; idxMove[i]=0; continue; }

                synchronized (champion) {

                    if(idxMove[i]>=champion.moves.size()) { idxChampion[i]++; idxMove[i]=0; continue; }

                    Move move = champion.moves.get(idxMove[i]++);
                    move.performMove(game.players[i],champion);
                    playerControllers[i].updateAppearance();
                    playerControllers[i].displayEnvironment();
                    /*try {
                        Thread.sleep(1000);
                    }catch (InterruptedException ex){}*/
                }
            }
        }
    }

    int GameOver() {

        // clean players' champions arrays (removing dead champions (which are null)).
        for (int i = 1; i <= game.players_num; i++) {
            if (game.players[i].lose) continue;

            playerControllers[i].getChampionsInField().removeIf(Objects::isNull);
        }

        // figuring out loosing players
        for(int i=1;i<=game.players_num;i++){

            if (game.players[i].lose) continue;

            boolean lose=true;

            for (int j=1;j<=game.players_num;j++){
                if(game.players[j].teamNumber==game.players[i].teamNumber){
                    if(game.players[j].champs.size()>0){
                        lose=false;
                        break;
                    }
                }
            }

            if(lose){
                playerControllers[i].updateAppearance();
                playerControllers[i].displayEnvironment();
                playerControllers[i].playerIO.notification(game.players[i].name+" Lose!");
                game.players[i].lose = true;
            }
        }

        // figuring out winner team
        boolean ok=false;
        int k = 0;  // winner team's number
        for (int j = 1; j <= game.players_num; j++) {
            if (!game.players[j].lose) {
                if(ok){
                    if(game.players[j].teamNumber!=k) return 0; // there is more than one survival team
                    continue;
                }
                ok=true;
                k = game.players[j].teamNumber;
            }
        }

        return k;
    }

    static public abstract class Move implements Serializable {
        abstract void performMove(Player player,Champion ch);

        @Override
        public String toString() {
            return "Move";
        }
    }

    static class Movement extends Move implements Serializable {

        int right ,up;  // left is actually right with a negative value. The same thing for up and down.

        void setValue(int right,int up) {
            this.right = right;
            this.up = up;
        }

        void performMove(Player player,Champion ch) {
            Pair<Integer,Integer> pa = Main.championPosition.get(ch.name);
            if(pa==null) return;
            int first = pa.getKey();
            int second = pa.getValue();
            if(second+right<Main.m&&second+right>=0) second+=right;
            if(first-up<Main.n&&first-up>=0)     first-=up;

            fieldController.removeChampion(ch);
            fieldController.addChampion(ch,new Pair<>(first,second));

            Iterator <Item> iterator = fieldController.field.item[first][second].iterator();
            while (iterator.hasNext()) {
                if(ch.items.size()==3)break;
                ChampionController.addItem(ch,iterator.next());
                iterator.remove();
            }
        }
    }

    static class BasicAttack extends Move{
        String defender;
        void setValue(String defender){
            this.defender=defender;
        }

        void performMove(Player player,Champion ch){
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(ch.name),(int)ch.attributes.attackRange);
            if (range==null) return;

            Pair<Integer,Integer> idx;
            Pair<Integer,Integer> defPos=Main.championPosition.get(defender);

            if(defPos==null)return;

            for (Pair<Integer, Integer> pa : range) {
                idx = pa;
                if (defPos.equals(idx)) {
                    Champion defCh = fieldController.getChampionFromAList(defender);
                    if (defCh == null) return;
                    if (game.players[defCh.playerNumber].teamNumber == player.teamNumber) return;

                    for (int j = 0; j < ch.attackTimes; j++) {
                        if (defCh == null) break;
                        if (chance(ch.attributes.criticalStrikeChance * 100))
                            ChampionController.increaseBasicAttack(ch, ch.attributes.criticalStrikeDamage);
                        ChampionController.increaseMana(ch, ch.increasingMana);
                        dealDamage(defCh, ch.attributes.basicAttack + ch.extraBasicAttack, ch.magicAttack, ch.trueAttack, ch.willFreeze, ch.willReduceMana);
                    }
                }
            }
        }
    }

    public static void receiveItems(Champion champion){
        if(champion.inField){
            Pair<Integer,Integer>pos=Main.championPosition.get(champion.name);
            if(pos==null) return;
            for (Item item:champion.items){
                fieldController.addItem(item,pos);
            }
        }else{
            for (Item item:champion.items){
                fieldController.addRandomly(item);
            }
        }
    }

    static class Sell extends Move{
        void performMove(Player player,Champion ch){
            player.golds+=ch.attributes.goldCost/2;
            playerControllers[ch.playerNumber].deleteChampion(ch);
            checkForGroupsAbility(ch);
        }

        @Override
        public String toString() {
            return "Sell";
        }
    }

    public static Move getAbility(String name){
        switch (name){
            case "Aatrox" : return new  AatroxAbility();
            case "Shen" : return new  ShenAbility();
            case "Akali" : return new  AkaliAbility();
            case "Anivia" : return new  AniviaAbility();
            case "Brand" : return new  BrandAbility();
            case "Chogath" : return new  ChogathAbility();
            case "Darius" : return new  DariusAbility();
            case "Draven" : return new  DravenAbility();
            case "Fiora" : return new  FioraAbility();
            case "Gankplank" : return new  GankplankAbility();
            case "Garen" : return new  GarenAbility();
            case "Graves" : return new  GravesAbility();
            case "Karthus" : return new  KarthusAbility();
            case "Kassadin" : return new  KassadinAbility();
            case "Katarina" : return new  KatarinaAbility();
            case "Kennen" : return new  KennenAbility();
            case "Kindred" : return new  KindredAbility();
            case "Leona" : return new  LeonaAbility();
            case "Lissandra" : return new  LissandraAbility();
            case "Lucian" : return new  LucianAbility();
            case "Lulu" : return new  LuluAbility();
            case "MissFortune" : return new  MissFortuneAbility();
            case "Mordekaiser" : return new  MordekaiserAbility();
            case "Morgana" : return new  MorganaAbility();
            case "Nidale" : return new  NidaleAbility();
            //case "Poppy" : return new  PoppyAbility();
            case "Shyvana" : return new  ShyvanaAbility();
            case "Vayne" : return new  VayneAbility();
            case "Veiger" : return new  VeigerAbility();
            case "Volibear" : return new  VolibearAbility();
            case "Warwick" : return new  WarwickAbility();

            default: return null;
        }
    }

    static class AatroxAbility extends Move {
        void performMove(Player player,Champion ch){
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(ch.name),25);
            int playerNumber = ch.playerNumber;
            for(int i=0;i<range.size();i++){
                ArrayList<Champion> champs = fieldController.getAList(range.get(i));
                for(int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber!=playerNumber){
                        dealDamage(champs.get(j),0,250,0,false,0);
                    }
                }
            }
        }
    }

    static class ShenAbility extends Move {
        void performMove(Player player,Champion ch){
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(ch.name),20);
            int playerNumber = ch.playerNumber;
            for(int i=0;i<range.size();i++){
                ArrayList<Champion> champs = fieldController.getAList(range.get(i));
                for(int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber==playerNumber){
                        ChampionController.increaseArmor(champs.get(j),10);
                    }
                }
            }
        }
    }

    static class AkaliAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,0.1*defCh.attributes.maxHealth,0,false,0);
        }
    }

    static  class AniviaAbility extends Move {
        void performMove(Player player,Champion ch){
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(ch.name),30);
            int playerNumber = ch.playerNumber;
            for(int i=0;i<range.size();i++){
                ArrayList<Champion> champs = fieldController.getAList(range.get(i));
                for(int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber!=playerNumber){
                        dealDamage(champs.get(j),0,250,0,false,0);
                    }
                }
            }
            ch.willFreeze=true;
        }
    }

    static  class BrandAbility extends Move {

        @Override
        void performMove(Player player,Champion ch) {

            PlayerIO playerIO = playerControllers[player.number].playerIO;

            String enemy;
            Champion defCh;
            if(player.isHuman)
                playerIO.notification("select 3 enemies : ");
            enemy = playerIO.selectEnemy();
            if(enemy==null) return;

            defCh = fieldController.getChampionFromAList(enemy);
            if(defCh!=null) {
                ChampionController.increaseArmor(defCh,-0.1*(defCh.attributes.armor+defCh.extraArmor));
                ChampionController.increaseMagicResist(defCh,-0.1*(defCh.attributes.magicResist+defCh.extraMagicResist));
                dealDamage(defCh,0, 0.1 * defCh.attributes.maxHealth,0, false,0);
            }

            enemy =playerIO.selectEnemy();
            if(enemy==null) return;

            defCh = fieldController.getChampionFromAList(enemy);
            if(defCh!=null) {
                ChampionController.increaseArmor(defCh,-0.1*(defCh.attributes.armor+defCh.extraArmor));
                ChampionController.increaseMagicResist(defCh,-0.1*(defCh.attributes.magicResist+defCh.extraMagicResist));
                dealDamage(defCh,0, 0.1 * defCh.attributes.maxHealth,0, false,0);
            }

            enemy =playerIO.selectEnemy();
            if(enemy==null) return;

            defCh = fieldController.getChampionFromAList(enemy);
            if(defCh!=null) {
                ChampionController.increaseArmor(ch,-0.1*(defCh.attributes.armor+defCh.extraArmor));
                ChampionController.increaseMagicResist(defCh,-0.1*(defCh.attributes.magicResist+defCh.extraMagicResist));
                dealDamage(defCh,0, 0.1 * defCh.attributes.maxHealth,0, false,0);
            }
        }
    }

    static  class ChogathAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ch.trueAttack+=150;
        }
    }

    static  class DariusAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(ch.name),20);
            int playerNumber = ch.playerNumber;
            for(int i=0;i<range.size();i++){
                ArrayList<Champion> champs = fieldController.getAList(range.get(i));
                for(int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber!=playerNumber){
                        dealDamage(champs.get(j),0,0,75,false,0);
                    }
                }
            }
            ChampionController.increaseHealth(ch,75);
        }
    }

    static  class DravenAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;

            Champion defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,0,0.3*defCh.attributes.maxHealth,false,0);
        }
    }

    static  class FioraAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseArmor(ch,10);
            ChampionController.increaseMagicResist(ch,10);
            ch.willFreeze=true;
        }
    }

    static  class GankplankAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseAttackRange(ch, (int) (ch.attributes.attackRange+ch.extraAttackRange));
            ChampionController.increaseVisionRange(ch, (int) (ch.attributes.visionRange+ch.extraVisionRange));
            ChampionController.increaseMagicAttack(ch,ch.magicAttack);
            ChampionController.increaseBasicAttack(ch,ch.attributes.basicAttack+ch.extraBasicAttack);
        }
    }

    static  class GarenAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;

            Champion defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,defCh.attributes.maxHealth-defCh.health,0,false,0);
        }
    }

    static  class GravesAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            ChampionController.increaseCriticalStrikeChance(defCh,-defCh.attributes.criticalStrikeChance-defCh.extraCriticalStrikeChance);
            dealDamage(defCh,0,300,0,false,0);
        }
    }

    static  class KarthusAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            for(int i=1;i<=game.players_num;i++){
                if(game.players[i]==null||game.players[i].lose||i==ch.playerNumber)continue;
                ArrayList<Champion> champs = game.players[i].champs;
                for(int j=0;j<champs.size();j++){
                    Champion defCh = champs.get(j);
                    dealDamage(defCh,0,250,0,false,0);
                }
            }
        }
    }

    static  class KassadinAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ch.magicAttack+=20;
            ch.willReduceMana+=2;
        }
    }

    static  class KatarinaAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ch.willReduceMana+=1000;
        }
    }

    static  class KennenAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseBasicAttack(ch,2.5*ch.attributes.basicAttack);
        }
    }

    static  class KindredAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ArrayList<Champion>champs = game.players[ch.playerNumber].champs;
            for(int i=0;i<champs.size();i++){
                ChampionController.increaseMagicAttack(champs.get(i),0.1*champs.get(i).magicAttack);
            }
        }
    }

    static  class LeonaAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ch.willFreeze=true;
        }
    }

    static  class LissandraAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,550,0,false,0);
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(defCh.name),5);
            for(int i=0;i<range.size();i++){
                ArrayList<Champion> champs = fieldController.getAList(range.get(i));
                for(int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber!=ch.playerNumber){
                        dealDamage(champs.get(j),0,550,0,false,0);
                        return;
                    }
                }
            }
        }
    }

    static  class LucianAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseAttackRange(ch,10);
            ChampionController.increaseVisionRange(ch,10);
            ChampionController.increaseMovementSpeed(ch,10);
        }
    }

    static  class LuluAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseHealth(ch,150);
            ArrayList<Champion>champs = game.players[ch.playerNumber].champs;
            int cnt=0;
            for(int i=0;i<champs.size()&&cnt<2;i++){
                if(champs.get(i)!=ch){
                    ChampionController.increaseHealth(champs.get(i),150);
                    cnt++;
                }
            }
        }
    }

    static  class MissFortuneAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            ChampionController.increaseBasicAttack(ch,0.1*(defCh.attributes.basicAttack+defCh.extraBasicAttack));
            ChampionController.increaseBasicAttack(defCh,-0.1*(defCh.attributes.basicAttack+defCh.extraBasicAttack));
            dealDamage(defCh,0,100,0,false,0);
        }
    }

    static  class MordekaiserAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            ChampionController.increaseMagicResist(defCh,-0.1*(defCh.attributes.magicResist+defCh.extraMagicResist));
            dealDamage(defCh,0,150,0,false,0);
        }
    }

    static  class MorganaAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ArrayList<Pair<Integer,Integer>> range = getRange(Main.championPosition.get(ch.name),25);
            for(int i=0;i<range.size();i++){
                ArrayList<Champion> champs = fieldController.getAList(range.get(i));
                for (int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber!=ch.playerNumber){
                        ChampionController.increaseMana(champs.get(j),-2);
                        ChampionController.increaseMana(ch,2);
                        dealDamage(champs.get(j),0,100,0,false,0);
                    }
                }
            }
        }
    }

    static  class NidaleAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseMagicAttack(ch,0.1*ch.attributes.magicResist);
            ArrayList<Champion>champs = game.players[ch.playerNumber].champs;
            for(int i=0;i<champs.size();i++){
                if(champs.get(i)!=ch){
                    ChampionController.increaseHealth(champs.get(i),100);
                    break;
                }
            }
        }
    }

    /*
    static  class PoppyAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            if(player.isHuman)
                System.out.print("line = ");

            int line = player.getInt(1,n);
            line--;
            for(int i=0;i<m;i++){
                ArrayList<Champion>champs = field.getAList(new Pair<>(line,i));
                for (int j=0;j<champs.size();j++){
                    if(champs.get(j).playerNumber!=ch.playerNumber){
                        champs.get(j).freeze+=2;
                    }
                }
            }
        }
    }
    */

    static  class ShyvanaAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseHealth(ch,0.1*ch.health);
            ChampionController.increaseVisionRange(ch,5);
            ChampionController.increaseAttackRange(ch,5);
        }
    }

    static  class VayneAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,0,0.15*defCh.attributes.maxHealth,false,0);
        }
    }

    static  class VeigerAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            ChampionController.increaseBasicAttack(ch,10000);
        }
    }

    static  class VolibearAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            if(player.isHuman)
                System.out.println("enter 2 enemies : ");
            Champion defCh;

            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,0.5*(ch.attributes.basicAttack+ch.extraBasicAttack),0,false,0);

            enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            defCh = fieldController.getChampionFromAList(enemy);
            dealDamage(defCh,0,0.5*(ch.attributes.basicAttack+ch.extraBasicAttack),0,false,0);
        }
    }

    static  class WarwickAbility extends Move {
        @Override
        void performMove(Player player,Champion ch) {
            String enemy = playerControllers[player.number].playerIO.selectEnemy();
            if(enemy==null) return;
            Champion defCh = fieldController.getChampionFromAList(enemy);
            ChampionController.increaseHealth(ch,0.1*defCh.attributes.maxHealth);
            dealDamage(defCh,0,0.1*defCh.attributes.maxHealth,0,false,0);
        }
    }

    static GroupAbility getGroupAbility(String name){
        switch (name){

            case "BladeMaster":
                return new BladeMaster();

            case "Demon":
                return new Demon();

            case "Ninja":
                return new Ninja();

            case "Assassin":
                return new Assassin();

            case "Imperial":
                return new Imperial();

            case "Glacial":
                return new Glacial();

            case "Elementalist":
                return new Elementalist();

            case "Sorcerer":
                return new Sorcerer();

            case "Void":
                return new Void();

            case "Brawler":
                return new Brawler();

            case "Knight":
                return new Knight();

            case "Ranger":
                return new Ranger();

            case "Noble":
                return new Noble();

            case "Pirate":
                return new Pirate();
            case "Gunslinger":
                return new Gunslinger();

            case "Yordle":
                return new Yordle();

            case "Wild":
                return new Wild();

            case "Shapeshifter":
                return new Shapeshifter();

            case "Dragon":
                return new Dragon();

            default:return null;
        }
    }

    static void checkForGroupsAbility(Champion ch) {
        PlayerController playerController = playerControllers[ch.playerNumber];
        for(String group:ch.attributes.groups){
            if(playerController.GroupDisActivated(group)){
                ArrayList<Champion> champs = playerController.getChampionsInField();
                for(Champion champion:champs){
                    if(champion==null)continue;
                    for(int k=0;k<champion.attributes.groups.size();k++){
                        if(champion.attributes.groups.get(k).equals(group)){
                            if(champion.groupsAbilityActive[k])
                                getGroupAbility(champion.attributes.groups.get(k)).activate(champion,playerController.getNumberOfAGroup(champion.attributes.groups.get(k))+1);
                        }
                    }
                }
            }
        }
    }

    static void deleteChampion(Champion champion){
        playerControllers[champion.playerNumber].deleteChampion(champion);

    }

    static void dealDamage(Champion defCh, double basicDamage, double magicDamage, double trueDamage, boolean freeze, int reduceMana){

        if(ChampionController.receiveDamage(defCh,basicDamage, magicDamage, trueDamage, freeze,reduceMana)){
            playerControllers[defCh.playerNumber].deleteChampion(defCh);
            checkForGroupsAbility(defCh);
        }
    }


    static abstract class GroupAbility {
        abstract void activate(Champion ch,int cnt);     // to perform the ability
        abstract void deactivate(Champion ch,int cnt); // to undo perform the ability when activation condition is no longer met
    }

    static class BladeMaster extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {

            if(!chance(40))return;

            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"BladeMaster")]=true;
            if(cnt>=3&&cnt<6){
                ch.attackTimes++;
            }else if(cnt>=6&&cnt<9){
                ch.attackTimes+=2;
            }else{
                ch.attackTimes+=4;
            }
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt>=3&&cnt<6){
                ch.attackTimes--;
            }else if(cnt>=6&&cnt<9){
                ch.attackTimes-=2;
            }else{
                ch.attackTimes-=4;
            }
        }
    }

    static class Demon extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            if(!chance(40)) return;
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Demon")]=true;
            ch.willReduceMana+=10*cnt;
            ch.magicAttack+=100*cnt;
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            ch.willReduceMana-=10*cnt;
            ch.magicAttack-=100*cnt;
        }
    }

    static class Ninja extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ArrayList<String> champs = Main.groupChampions.get("Ninja");
            PlayerController playerController = playerControllers[ch.playerNumber];

            for(String champion:champs){
                if(!playerController.haveChampionKind(champion)) return;
            }
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Ninja")]=true;

            ChampionController.increaseCriticalStrikeChance(ch,0.25);
            ChampionController.increaseCriticalStrikeDamage(ch,1);

        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            ChampionController.increaseCriticalStrikeChance(ch,-0.25);
            ChampionController.increaseCriticalStrikeDamage(ch,-1);
        }
    }

    static class Assassin extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {

            fieldController.removeChampion(ch);
            Pair<Integer,Integer> pos = Main.championPosition.get(ch.name);
            if(pos==null) return;

            int x=pos.getKey();
            int y=pos.getValue();
            fieldController.addChampion(ch,new Pair<>(game.field.n-x-1,game.field.m-y-1));


            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Assassin")]=true;
            if(cnt == 2){
                ChampionController.increaseBasicAttack(ch,30);
                ChampionController.increaseMagicAttack(ch,0.1*ch.magicAttack);
            }
            else{
                ChampionController.increaseBasicAttack(ch,60);
                ChampionController.increaseMagicAttack(ch,0.2*ch.magicAttack);
            }
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt == 2){
                ChampionController.increaseBasicAttack(ch,-30);
                ChampionController.increaseMagicAttack(ch,-0.1*ch.magicAttack);
            }
            else{
                ChampionController.increaseBasicAttack(ch,-60);
                ChampionController.increaseMagicAttack(ch,-0.2*ch.magicAttack);
            }
        }
    }

    static class Imperial extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ArrayList<String> champs = Main.groupChampions.get("Imperial");
            PlayerController playerController = playerControllers[ch.playerNumber];

            for(String s:champs){
                if(!playerController.haveChampionKind(s)) return;
            }

            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Imperial")]=true;

            ChampionController.increaseBasicAttack(ch,ch.attributes.basicAttack+ch.extraBasicAttack);
            ChampionController.increaseMagicAttack(ch,ch.magicAttack);

        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            ChampionController.increaseBasicAttack(ch,-(ch.attributes.basicAttack+ch.extraBasicAttack)/2);
            ChampionController.increaseMagicAttack(ch,-ch.magicAttack/2);
        }
    }

    static class Glacial extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            if(!chance(15*cnt)) return;
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Glacial")]=true;
            ch.willFreeze=true;
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            ch.willFreeze=false;
        }
    }

    static class Elementalist extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Elementalist")]=true;
            ch.increasingMana+=ch.increasingMana;
            if(cnt>=4){
                Player player = game.players[ch.playerNumber];
                player.maxChampsInField=10;
            }
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            ch.increasingMana-=ch.increasingMana/2;
            if(cnt>=4){
                Player player = game.players[ch.playerNumber];
                player.maxChampsInField=9;
            }
        }
    }

    static class Sorcerer extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Sorcerer")]=true;
            if(cnt>=3&&cnt<6){
                ChampionController.increaseMagicAttack(ch,0.4*ch.magicAttack);
            }else if(cnt>=6&&cnt<9){
                ChampionController.increaseMagicAttack(ch,1*ch.magicAttack);
            }else{
                ChampionController.increaseMagicAttack(ch,1.7*ch.magicAttack);
            }
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt>=3&&cnt<6){
                ChampionController.increaseMagicAttack(ch,-0.4*ch.magicAttack);
            }else if(cnt>=6&&cnt<9){
                ChampionController.increaseMagicAttack(ch,-1*ch.magicAttack);
            }else{
                ChampionController.increaseMagicAttack(ch,-1.7*ch.magicAttack);
            }
        }
    }

    static class Void extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ChampionController.increaseTrueAttack(ch,100);
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Void")]=true;
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            ChampionController.increaseTrueAttack(ch,-100);
        }
    }

    static class Brawler extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Brawler")]=true;
            if(cnt>=2&&cnt<4){
                ChampionController.increaseHealth(ch,250);
            }else if(cnt>=4&&cnt<6){
                ChampionController.increaseHealth(ch,500);
            }else{
                ChampionController.increaseHealth(ch,1000);
            }
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt>=2&&cnt<4){
                ChampionController.increaseHealth(ch,-250);
            }else if(cnt>=4&&cnt<6){
                ChampionController.increaseHealth(ch,-500);
            }else{
                ChampionController.increaseHealth(ch,-1000);
            }
        }
    }

    static class Pirate extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            ch.groupsAbilityActive[ChampionController.indexOfGroup(ch,"Pirate")]=true;
            PlayerController playerController = playerControllers[ch.playerNumber];
            playerController.increasingGolds=4;
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            PlayerController playerController = playerControllers[ch.playerNumber];
            playerController.increasingGolds=2;
        }
    }

    static class Gunslinger extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {

        }

        @Override
        void deactivate(Champion ch ,int cnt) {

        }
    }

    static class Wild extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {

        }

        @Override
        void deactivate(Champion ch ,int cnt) {

        }
    }

    static class Dragon extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {

        }

        @Override
        void deactivate(Champion ch ,int cnt) {

        }
    }

    static class Knight extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            if(cnt>=2&&cnt<4)
                ChampionController.increaseArmor(ch,5);
            else if(cnt>=4&&cnt<6)
                ChampionController.increaseArmor(ch,8);
            else if(cnt>=6)
                ChampionController.increaseArmor(ch,12);
        }

        @Override
        void deactivate(Champion ch ,int cnt) {

            if(cnt>=2&&cnt<4)
                ChampionController.increaseArmor(ch,-5);
            else if(cnt>=4&&cnt<6)
                ChampionController.increaseArmor(ch,-8);
            else if(cnt>=6)
                ChampionController.increaseArmor(ch,-12);
        }
    }

    static class Ranger extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            if(cnt>=3) {
                ChampionController.increaseAttackRange(ch,(int)ch.attributes.attackRange + ch.extraAttackRange);
                ChampionController.increaseVisionRange(ch,(int)ch.attributes.visionRange + ch.extraVisionRange);
            }

        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt>=3) {
                ChampionController.increaseAttackRange(ch,(int)-ch.attributes.attackRange - ch.extraAttackRange);
                ChampionController.increaseVisionRange(ch,(int)-ch.attributes.visionRange - ch.extraVisionRange);
            }
        }
    }

    static class Noble extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {

            if(cnt>=3) {
                ArrayList<Champion> champs = game.players[ch.playerNumber].champs;
                for (int i = 0; i < champs.size(); i++) {
                    ChampionController.increaseArmor(champs.get(i),20);
                    ChampionController.increaseHealth(champs.get(i),40);
                }
            }
        }

        @Override
        void deactivate(Champion ch ,int cnt) {

            if(cnt>=3) {
                ArrayList<Champion> champs = game.players[ch.playerNumber].champs;
                for (int i = 0; i < champs.size(); i++) {
                    ChampionController.increaseArmor(champs.get(i),-20);
                    ChampionController.increaseHealth(champs.get(i),-40);
                }
            }
        }
    }

    static class Yordle extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            if(cnt>=2&&cnt<4)
                ChampionController.increaseArmor(ch,15);
            else if(cnt>=4&&cnt<6)
                ChampionController.increaseArmor(ch,30);
            else if(cnt>=6)
                ChampionController.increaseArmor(ch,50);

        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt>=2&&cnt<4)
                ChampionController.increaseArmor(ch,-15);
            else if(cnt>=4&&cnt<6)
                ChampionController.increaseArmor(ch,-30);
            else if(cnt>=6)
                ChampionController.increaseArmor(ch,-50);
        }
    }

    static class Shapeshifter extends GroupAbility{
        @Override
        void activate(Champion ch ,int cnt) {
            if(cnt>=3&&cnt<6)
                ChampionController.increaseHealth(ch,40);
            else if(cnt>=6)
                ChampionController.increaseHealth(ch,100);
        }

        @Override
        void deactivate(Champion ch ,int cnt) {
            if(cnt>=3&&cnt<6)
                ChampionController.increaseHealth(ch,-40);
            else if(cnt>=6)
                ChampionController.increaseHealth(ch,-100);
        }
    }

    public static boolean chance(double percent){
        Random rand = new Random();
        int r = rand.nextInt(100);
        return r < percent;
    }

    public static ArrayList<Pair<Integer,Integer>> getRange(Pair<Integer,Integer> pa,int radios){
        if(pa==null) return null;
        int x=pa.getKey();
        int y=pa.getValue();
        ArrayList<Pair<Integer,Integer>>res=new ArrayList<>();

        for(int j=y-radios;j<=y+radios;j++){
            if(j<0||j>=Main.m)continue;
            res.add(new Pair<>(x,j));
        }

        for (int j=1;j<=radios;j++){
            for(int k=y-radios+j-1;k<=y+radios-j+1;k++){
                if(x+j<0 || x+j>=Main.n || k<0 || k>=Main.m)continue;
                res.add(new Pair<>(x+j,k));
            }
            for(int k=y-radios+j-1;k<=y+radios-j+1;k++) {
                if (x - j < 0 || x - j >= Main.n|| k<0 || k>=Main.m) continue;
                res.add(new Pair<>(x-j,k));
            }
        }
        return res;
    }

    static String getEnemyInRange(Champion ch){
        Pair<Integer,Integer> pa = Main.championPosition.get(ch.name);
        if(pa==null) return null;
        int radios = (int)ch.attributes.attackRange;
        ArrayList<Pair<Integer,Integer>> range = getRange(pa,radios);
        for (Pair<Integer, Integer> idx : range) {
            ArrayList<Champion> list = fieldController.getAList(idx);
            for (Champion champion : list) {
                if(playerControllers[champion.playerNumber].player.teamNumber!=playerControllers[ch.playerNumber].player.teamNumber)
                    return champion.name;
            }
        }
        return null;
    }

}
