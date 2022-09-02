package tacticalChaos;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import tacticalChaos.controller.GameController;
import tacticalChaos.model.*;
import tacticalChaos.view.*;
import tacticalChaos.util.Generator;

public class Main extends Application implements EventHandler<ActionEvent> {

    private Stage window;
    private final Button consoleButton = new Button("Start Console Game");
    private final Button guiButton = new Button("Start GUI Game");
    private final Button loadGame = new Button("Load Game");

    private static Game game=new Game();
    private GameController gameController;
    private GameDisplay gameDisplay;

    public static int n,m;

    public static ArrayList<String> championsNames = new ArrayList<>();
    public static ArrayList<String> itemsNames = new ArrayList<>();

    public static HashMap<String,Integer> championID = new HashMap<>();
    public static HashMap<String,Integer> groupID = new HashMap<>();

    public static HashMap<String, Attributes> championAttributes = new HashMap<>();
    public static HashMap<String, Item> itemAttributes = new HashMap<>();
    public static HashMap<String, Pair<Integer,Integer>> championPosition = new HashMap<>();

    public static HashMap<String, ArrayList<String>> groupChampions = new HashMap<>();
    public static HashMap<String,Long> groupRequirement = new HashMap<>();

    public static void main(String[] args) {
        Generator.generateDataFile();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        window = primaryStage;
        window.setTitle("Tactical Chaos");

        window.setMinWidth(300);
        window.setMinHeight(300);

        consoleButton.setOnAction(this);
        guiButton.setOnAction(this);
        loadGame.setOnAction(this);

        VBox vBox = new VBox(30);
        Font font = new Font(32);
        Text welcome = new Text("Welcome To Tactical Chaos");
        welcome.setFont(font);
        HBox box = new HBox(30);
        box.setPadding(new Insets(40,0,0,10));
        box.getChildren().addAll(consoleButton,guiButton,loadGame);

        vBox.setPadding(new Insets(180,100,230,100));

        vBox.getChildren().addAll(welcome,box);

        Scene scene = new Scene(vBox,600,600);
        window.setScene(scene);
        window.show();
    }

    void initializeGame() {

        Object obj=null;

        try {
            obj = new JSONParser().parse(new FileReader("all data.json"));
        }catch (Exception e){
        }

        JSONObject jo = (JSONObject) obj ;

        championsNames = (ArrayList<String>) jo.get("championsNames");
        itemsNames = (ArrayList<String>) jo.get("itemsNames");
        groupChampions = (HashMap<String, ArrayList<String>>) jo.get("groupChampions");
        groupRequirement = (HashMap<String,Long>) jo.get("groupRequirement");

        for (Map.Entry<String, Long> pair : groupRequirement.entrySet()) {
            groupID.put(pair.getKey(), groupID.size());
        }

        for(String championName:championsNames){

            championID.put(championName,championID.size());

            Attributes att = new Attributes();

            JSONArray ja = (JSONArray) jo.get(championName);
            Iterator iterator2 = ja.iterator();

            att.groups = (ArrayList<String>) iterator2.next();

            Iterator it = ((ArrayList) iterator2.next()).iterator();

            att.goldCost =  (long) it.next();
            att.maxHealth =  (long) it.next();
            att.armor = (double) it.next();
            att.magicResist = (double) it.next();
            att.visionRange =  (long) it.next();
            att.attackRange =  (long) it.next();
            att.basicAttack =  (long) it.next();
            att.movementSpeed =  (long) it.next();
            att.criticalStrikeChance = (double) it.next();
            att.criticalStrikeDamage = (double) it.next();
            att.initialMana =  (long) it.next();
            att.manaCost =  (long) it.next();

            championAttributes.put(championName,att);

        }

        for (String itemName:itemsNames){

            Item item = new Item();
            item.name=itemName;

            JSONArray ja = (JSONArray) jo.get(itemName);
            Iterator iterator2 = ja.iterator();

            ArrayList list = (ArrayList) iterator2.next();

            Iterator it = list.iterator();

            item.extraMagicDamage=(double)it.next();
            item.extraBasicAttack=(double)it.next();
            item.extraArmor=(double)it.next();
            item.extraCriticalStrikeChance=(double)it.next();
            item.extraMaxHealth=(double)it.next();
            item.extraMagicResist=(double)it.next();
            item.extraGroup=(String)it.next();

            itemAttributes.put(itemName,item);
        }
    }

    boolean loadGame(){
        try {
            FileInputStream file = new FileInputStream("save");
            ObjectInputStream in = new ObjectInputStream(file);

            game = (Game)in.readObject();

            in.close();
            file.close();
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        GameController.state = game.state;
        Main.n=game.field.n;
        Main.m=game.field.m;

        for(int i=0;i<n;i++){
            for (int j=0;j<m;j++){
                for(Champion champion:game.field.battleField[i][j]){
                    championPosition.put(champion.name,new Pair<>(i,j));
                }
            }
        }

        for (int i=1;i<=game.players_num;i++){
            if(game.players[i].lose)continue;
            for(Champion champion:game.players[i].champsBench){
                championPosition.put(champion.name,new Pair<>(-1,-1));
            }
        }
        return true;
    }

    @Override
    public void handle(ActionEvent event) {

        if(event.getSource()==loadGame){
            if(!loadGame()) return;
            initializeGame();
            window.close();
            gameController=new GameController(game);
            new Thread(()-> gameController.startGame()).start();
            return;
        }

        initializeGame();

        if (event.getSource() == consoleButton) {
            game.isConsole=true;
            gameDisplay = new ConsoleGameDisplay(game);
        }
        else if (event.getSource() == guiButton) {
            game.isConsole=false;
            gameDisplay = new GuiGameDisplay(game);
        }

        window.close();

        gameDisplay.gameSettings();

        gameController=new GameController(game);

        new Thread(()-> gameController.startGame()).start();
    }

}
