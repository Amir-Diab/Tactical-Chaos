package tacticalChaos.controller;

import java.util.ArrayList;

import tacticalChaos.Main;
import tacticalChaos.model.Attributes;
import tacticalChaos.model.Champion;
import tacticalChaos.model.Item;

public abstract class ChampionController {

    public static Champion newChampion(String name,int lv) {
        Attributes attributes = Main.championAttributes.get(name);
        return new Champion(name,attributes,lv);
    }

    static void resetExtra(Champion champion) {
        champion.canMove = true;
        champion.canAttack = true;
        champion.canUseAbility = true;
        champion.extraBasicAttack = 0;
        champion.extraVisionRange = 0;
        champion.extraAttackRange = 0;
        champion.extraMagicResist = 0;
        champion.extraArmor = 0;
        champion.extraMovementSpeed = 0;
        champion.extraCriticalStrikeChance = 0;
        champion.extraCriticalStrikeDamage = 0;
        champion.magicAttack = 0;
        champion.willFreeze = false;
        champion.freeze = Math.max(champion.freeze - 1, 0);

        for (Item item:champion.items){
            champion.magicAttack+=item.extraMagicDamage*(champion.magicAttack);
            champion.extraBasicAttack+=item.extraBasicAttack*(champion.extraBasicAttack+champion.attributes.basicAttack);
            champion.extraArmor+=item.extraArmor*(champion.attributes.armor+champion.extraArmor);
            champion.extraCriticalStrikeChance+=item.extraCriticalStrikeChance*(champion.attributes.criticalStrikeChance+champion.extraCriticalStrikeChance);
            champion.attributes.maxHealth+=item.extraMaxHealth*(champion.attributes.maxHealth);
        }

        champion.moves.clear();
    }

    static void increaseCriticalStrikeChance(Champion champion,double inc) {
        champion.extraCriticalStrikeChance += inc;
    }

    static void increaseCriticalStrikeDamage(Champion champion,double inc) {
        champion.extraCriticalStrikeDamage += inc;
    }

    static int indexOfGroup(Champion champion,String group) {
        for (int i = 0; i < champion.attributes.groups.size(); i++) {
            if (champion.attributes.groups.get(i).equals(group)) return i;
        }
        return -1;
    }

    static void increaseMovementSpeed(Champion champion,int inc) {
        champion.extraMovementSpeed += inc;
    }

    static void increaseTrueAttack(Champion champion,double inc) {
        champion.trueAttack += inc;
    }

    static void increaseArmor(Champion champion,double inc) {
        champion.extraArmor += inc;
    }

    static void increaseVisionRange(Champion champion, int inc) {
        champion.extraVisionRange += inc;
    }

    static void increaseAttackRange(Champion champion,int inc) {
        champion.extraAttackRange += inc;
    }

    static void increaseMagicResist(Champion champion,double inc) {
        champion.extraMagicResist += inc;
    }

    static void increaseMagicAttack(Champion champion,double inc) {
        champion.magicAttack += inc;
    }

    static void increaseBasicAttack(Champion champion,double inc) {
        champion.extraBasicAttack += inc;
    }

    static void increaseMana(Champion champion,double inc) {
        champion.mana += inc;
        champion.mana = Math.max(champion.mana, 0);
    }

    static boolean increaseHealth(Champion champion,double inc) {
        champion.health += inc;
        return champion.health <= 0;
    }

    public static void addItem(Champion champion,Item item){

        boolean ok=true;
        for(String group:champion.attributes.groups){
            if(group.equals(item.extraGroup)){
                ok=false;
                break;
            }
        }
        if(ok)champion.attributes.groups.add(item.extraGroup);
        champion.items.add(item);
    }

    static void removeItem(Champion champion,Item item){
        champion.items.remove(item);
    }

    static boolean receiveDamage(Champion champion,double basicDamage, double magicDamage, double trueDamage, boolean freeze, int reduceMana) {

        if (champion == null) return false;

        increaseMana(champion, -reduceMana);

        basicDamage = basicDamage * Math.max(1 - (champion.attributes.armor + champion.extraArmor), 0);
        magicDamage = magicDamage * Math.max(1 - (champion.attributes.magicResist + champion.extraMagicResist), 0);

        if (champion.extraArmor >= 1 && champion.extraMagicResist >= 1) trueDamage = 0;

        if (freeze) champion.freeze += 1;
        return increaseHealth(champion, -basicDamage - magicDamage - trueDamage);

    }

    static void addToMove(Champion champion,GameController.Move mo) {
        champion.moves.add(mo);
    }

    static ArrayList<GameController.Move> getMoves(Champion champion) {
        return champion.moves;
    }

    public static void setPlayer(Champion champion,int playerNum, int chNumber) {
        champion.name += "" + playerNum + chNumber + champion.lv;
        champion.playerNumber = playerNum;
    }

}
