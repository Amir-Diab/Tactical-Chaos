package tacticalChaos.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * pre-known champion's attributes:
 *      contains all fixed attributes that depend only on champion's kind
 */

public class Attributes implements Cloneable , Serializable {

    public String baseName;
    public ArrayList<String> groups;
    public long goldCost,maxHealth;
    public double armor, magicResist, criticalStrikeChance, criticalStrikeDamage;
    public long visionRange, attackRange, basicAttack, movementSpeed, manaCost, initialMana;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Attributes attributes = (Attributes) super.clone();
        attributes.groups = (ArrayList<String>) attributes.groups.clone();
        return attributes;
    }
}
