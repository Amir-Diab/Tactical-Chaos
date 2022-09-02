package tacticalChaos.model;

import java.io.Serializable;

public class Item implements Cloneable , Serializable {

    public String name;
    public double extraMagicDamage , extraBasicAttack , extraArmor , extraCriticalStrikeChance , extraMaxHealth , extraMagicResist ;
    public String extraGroup;

    public Item clone(){
        try {
            return (Item) super.clone();
        }catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }
}
