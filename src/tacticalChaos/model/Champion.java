package tacticalChaos.model;

import java.io.Serializable;
import java.util.ArrayList;

import tacticalChaos.controller.GameController.Move;

public class Champion implements Serializable {

    public Attributes attributes;   // fixed attributes

    // variable attributes that depend on the state of the game
    public String name;
    public boolean[] groupsAbilityActive = new boolean[10];
    public double mana,health, trueAttack;
    public double extraArmor, extraMagicResist, extraBasicAttack, extraCriticalStrikeChance, extraCriticalStrikeDamage,magicAttack;
    public double increasingMana = 1;

    public int  attackTimes = 1;
    public int extraVisionRange, extraAttackRange, extraMovementSpeed;
    public int willReduceMana;
    public boolean canMove = true, canAttack = true, canUseAbility = true, sold, willFreeze;
    public int freeze;
    public int playerNumber;

    public boolean inField;     // true if the champion is on the field, false if it is on the bench

    public int lv;    //champion level
    public ArrayList<Move> moves = new ArrayList<>();
    public ArrayList<Item> items=new ArrayList<>();

    public Champion(String name,Attributes attributes, int lv) {

        this.name = name;

        if(attributes==null) return;

        attributes.baseName=name;

        this.lv = lv;
        if (lv >= 2) {
            attributes.basicAttack += 0.1 * attributes.basicAttack;
            attributes.armor += 0.2 * attributes.armor;
            attributes.magicResist += 0.2 * attributes.magicResist;
            if (lv == 3) {
                attributes.basicAttack += 0.15 * attributes.basicAttack;
                attributes.armor += 0.25 * attributes.armor;
                attributes.magicResist += 0.25 * attributes.magicResist;
            }
        }

        this.attributes=attributes;

        mana = attributes.initialMana;
        health = attributes.maxHealth;
    }

    @Override
    public String toString() {
        return name;
    }
}
