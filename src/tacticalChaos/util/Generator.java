package tacticalChaos.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Generator {

    static JSONObject jo = new JSONObject();

    static ArrayList<String> championsNames =new ArrayList<>(Arrays.asList(
            "Aatrox", "Akali", "Anivia", "Brand", "Chogath", "Darius", "Draven", "Fiora",
            "Gankplank", "Garen", "Graves", "Karthus", "Kassadin", "Katarina", "Kennen", "Kindred", "Leona",
            "Lissandra", "Lucian", "Lulu", "MissFortune", "Mordekaiser", "Morgana", "Nidale", "Poppy", "Shen", "Shyvana",
            "Vayne", "Veiger", "Volibear", "Warwick"));

    static ArrayList<String> itemsNames = new ArrayList<>(Arrays.asList("Magic Hat","Warrior Gloves","Knight Armor","Angry Cloak","Night Shift","Void Hit","Universe Core"));
    static HashMap<String, ArrayList<String>> groupChampions=new HashMap<>();
    static HashMap<String,Integer> groupRequirement = new HashMap<>();

    static {

        groupChampions.put("BladeMaster", new ArrayList<>(Arrays.asList("Aatrox","Shen","Draven","Fiora","Gankplank","Leona")));
        groupChampions.put("Demon",new ArrayList<>(Arrays.asList("Aatrox","Brand","Morgana")));
        groupChampions.put("Ninja",new ArrayList<>(Arrays.asList("Shen","Akali","Kennen")));
        groupChampions.put("Assassin",new ArrayList<>(Arrays.asList("Akali","Katarina")));
        groupChampions.put("Imperial",new ArrayList<>(Arrays.asList("Akali","Darius","Draven","Graves","Katarina")));
        groupChampions.put("Glacial",new ArrayList<>(Arrays.asList("Anivia","Lissandra","Volibear")));
        groupChampions.put("Elementalist",new ArrayList<>(Arrays.asList("Anivia","Kennen","Lissandra","Lulu")));
        groupChampions.put("Sorcerer",new ArrayList<>(Arrays.asList("Anivia","Brand","Karthus","Kassadin","Lissandra","Morgana","Veiger")));
        groupChampions.put("Void",new ArrayList<>(Arrays.asList("Chogath","Karthus","Kassadin","Kindred","Mordekaiser")));
        groupChampions.put("Knight",new ArrayList<>(Arrays.asList("Darius","Garen","Mordekaiser","Poppy","Shyvana")));
        groupChampions.put("Ranger",new ArrayList<>(Arrays.asList("Draven","Kindred","Vayne")));
        groupChampions.put("Noble",new ArrayList<>(Arrays.asList("Fiora","Garen","Leona","Lucian","Vayne")));
        groupChampions.put("Pirate",new ArrayList<>(Arrays.asList("Gankplank","Graves","MissFortune")));
        groupChampions.put("Gunslinger",new ArrayList<>(Arrays.asList("Gankplank","Graves","Lucian","MissFortune")));
        groupChampions.put("Yordle",new ArrayList<>(Arrays.asList("Kennen","Lulu","Poppy","Veiger")));
        groupChampions.put("Wild",new ArrayList<>(Arrays.asList("Nidale","Warwick")));
        groupChampions.put("Shapeshifter",new ArrayList<>(Arrays.asList("Nidale")));
        groupChampions.put("Dragon",new ArrayList<>(Arrays.asList("Shyvana")));
        groupChampions.put("Brawler",new ArrayList<>(Arrays.asList("Chogath","Darius","Garen","Leona","Poppy","Shyvana","Volibear","Warwick")));

        groupRequirement.put("Imperial"    , groupChampions.get("Imperial").size());
        groupRequirement.put("Ninja"       , groupChampions.get("Ninja").size());

        groupRequirement.put("Assassin"    ,2);
        groupRequirement.put("Brawler"     ,2);
        groupRequirement.put("Demon"       ,2);
        groupRequirement.put("Dragon"      ,2);
        groupRequirement.put("Elementalist",2);
        groupRequirement.put("Glacial"     ,2);
        groupRequirement.put("Gunslinger"  ,2);
        groupRequirement.put("Knight"      ,2);
        groupRequirement.put("Void"        ,2);
        groupRequirement.put("Wild"        ,2);
        groupRequirement.put("Yordle"      ,2);

        groupRequirement.put("BladeMaster" ,3);
        groupRequirement.put("Noble"       ,3);
        groupRequirement.put("Pirate"      ,3);
        groupRequirement.put("Ranger"      ,3);
        groupRequirement.put("Shapeshifter",3);
        groupRequirement.put("Sorcerer"    ,3);


        JSONArray ja = new JSONArray();

// goldCost health armor magicResist visionRange attackRange basicAttack movementSpeed criticalStrikeChance criticalStrikeDamage mana manaCost

        ja.add(new ArrayList<>(Arrays.asList("Demon","BladeMaster")));
        ja.add(new ArrayList<>(Arrays.asList(3,750,0.4,0.4,20,10,50,10,0.25,1.5,5,10))); jo.put("Aatrox",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Ninja","BladeMaster")));
        ja.add(new ArrayList<>(Arrays.asList(4,650,0.4,0.4,20,10,50,10,0.75,3.5,5,10))); jo.put("Shen",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Ninja","Assassin","Imperial")));
        ja.add(new ArrayList<>(Arrays.asList(6,350,0.25,0.25,20,10,30,10,0.75,3.5,5,4))); jo.put("Akali",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Glacial","Elementalist","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(6,500,0.5,0.5,20,10,20,10,0.25,1.5,5,10))); jo.put("Anivia",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Demon","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(4,400,0.3,0.3,20,10,25,10,0.25,1.5,5,10))); jo.put("Brand",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Void","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(1,600,0.3,0.3,25,15,35,10,0.25,1.5,5,10))); jo.put("Chogath",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Imperial","Knight","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(5,600,0.5,0.5,20,10,50,10,0.25,1.5,5,10))); jo.put("Darius",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Imperial","BladeMaster","Ranger")));
        ja.add(new ArrayList<>(Arrays.asList(6,400,0.4,0.4,20,10,35,10,0.25,1.5,5,10))); jo.put("Draven",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Noble","BladeMaster")));
        ja.add(new ArrayList<>(Arrays.asList(1,500,0.4,0.4,20,10,45,10,0.25,1.5,5,10))); jo.put("Fiora",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Pirate","BladeMaster","Gunslinger")));
        ja.add(new ArrayList<>(Arrays.asList(4,200,0.4,0.4,20,10,50,10,0.25,1.5,5,10))); jo.put("Gankplank",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Noble","Knight","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(5,600,0.5,0.5,20,10,50,10,0.25,1.5,5,10))); jo.put("Garen",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Imperial","Gunslinger","Pirate")));
        ja.add(new ArrayList<>(Arrays.asList(4,200,0.2,0.2,60,30,30,5,0.25,2.0,5,10))); jo.put("Graves",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Void","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(4,450,0.3,0.3,20,10,50,10,0.25,1.5,5,10))); jo.put("Karthus",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Void","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(2,550,0.3,0.3,20,10,50,10,0.25,1.5,0,0))); jo.put("Kassadin",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Imperial","Assassin")));
        ja.add(new ArrayList<>(Arrays.asList(2,450,0.25,0.25,20,10,50,10,0.25,1.5,2,8))); jo.put("Katarina",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Ninja","Yordle","Elementalist")));
        ja.add(new ArrayList<>(Arrays.asList(5,350,0.5,0.5,20,10,60,10,0.75,3.5,5,10))); jo.put("Kennen",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Void","Ranger")));
        ja.add(new ArrayList<>(Arrays.asList(2,350,0.1,0.1,60,30,50,5,0.25,2.0,5,10))); jo.put("Kindred",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Noble","BladeMaster","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(2,500,0.4,0.4,20,10,50,10,0.25,1.5,5,10))); jo.put("Leona",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Glacial","Elementalist","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(6,800,0.5,0.5,20,10,50,10,0.25,1.5,5,10))); jo.put("Lissandra",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Noble","Gunslinger")));
        ja.add(new ArrayList<>(Arrays.asList(1,450,0.2,0.2,60,30,50,5,0.25,2.0,5,10))); jo.put("Lucian",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Yordle","Elementalist")));
        ja.add(new ArrayList<>(Arrays.asList(2,350,0.5,0.5,20,10,50,10,0.25,1.5,2,8))); jo.put("Lulu",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Pirate","Gunslinger")));
        ja.add(new ArrayList<>(Arrays.asList(1,200,0.2,0.2,60,30,50,5,0.25,2.0,5,10))); jo.put("MissFortune",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Void","Knight")));
        ja.add(new ArrayList<>(Arrays.asList(1,550,0.5,0.5,20,10,50,10,0.25,1.5,5,10))); jo.put("Mordekaiser",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Demon","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(3,500,0.3,0.3,20,10,50,10,0.25,1.5,5,10))); jo.put("Morgana",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Wild","Shapeshifter")));
        ja.add(new ArrayList<>(Arrays.asList(1,500,0.45,0.45,20,10,50,10,0.25,1.5,5,10))); jo.put("Nidale",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Yordle","Knight","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(6,650,0.5,0.5,20,10,50,10,0.25,1.5,5,10))); jo.put("Poppy",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Dragon","Knight","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(6,650,0.5,0.5,20,10,50,10,0.25,1.5,5,10))); jo.put("Shyvana",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Noble","Ranger")));
        ja.add(new ArrayList<>(Arrays.asList(1,300,0.1,0.1,60,30,50,5,0.25,2.0,0,3))); jo.put("Vayne",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Yordle","Sorcerer")));
        ja.add(new ArrayList<>(Arrays.asList(3,300,0.3,0.3,20,10,50,10,0.25,1.5,0,7))); jo.put("Veiger",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Glacial","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(1,650,0.3,0.3,25,15,50,10,0.25,1.5,5,10))); jo.put("Volibear",ja); ja = new JSONArray();

        ja.add(new ArrayList<>(Arrays.asList("Wild","Brawler")));
        ja.add(new ArrayList<>(Arrays.asList(1,500,0.3,0.3,25,15,50,10,0.25,1.5,5,10))); jo.put("Warwick",ja); ja = new JSONArray();
// extraMagicDamage extraBasicAttack extraArmor extraCriticalStrikeChance extraMaxHealth extraMagicResist
        ja.add(new ArrayList<>(Arrays.asList(0.2,0.0,0.0,0.0,0.0,0.0,"Sorcerer"))); jo.put("Magic Hat",ja); ja=new JSONArray();
        ja.add(new ArrayList<>(Arrays.asList(0.0,0.1,0.0,0.0,0.0,0.0,"BladeMaster"))); jo.put("Warrior Gloves",ja); ja=new JSONArray();
        ja.add(new ArrayList<>(Arrays.asList(0.0,0.0,0.15,0.0,0.0,0.0,"Knight"))); jo.put("Knight Armor",ja); ja=new JSONArray();
        ja.add(new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.1,0.0,0.0,"Yordle"))); jo.put("Angry Cloak",ja); ja=new JSONArray();
        ja.add(new ArrayList<>(Arrays.asList(0.0,0.2,0.0,0.0,0.0,0.0,"Assassin"))); jo.put("Night Shift",ja); ja=new JSONArray();
        ja.add(new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0,0.05,0.0,"Void"))); jo.put("Void Hit",ja); ja=new JSONArray();
        ja.add(new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0,0.0,0.2,"Elementalist"))); jo.put("Universe Core",ja);

    }

    public static void generateDataFile() {
        jo.put("championsNames",championsNames);
        jo.put("itemsNames",itemsNames);
        jo.put("groupChampions",groupChampions);
        jo.put("groupRequirement",groupRequirement);

        PrintWriter pw = null;

        try {
            pw = new PrintWriter("all data.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pw.write(jo.toJSONString());

        pw.flush();
        pw.close();
    }
}