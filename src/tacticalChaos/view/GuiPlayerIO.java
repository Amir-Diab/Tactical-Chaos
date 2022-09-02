package tacticalChaos.view;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.input.MouseEvent;

import tacticalChaos.util.Lock;
import tacticalChaos.Main;
import tacticalChaos.controller.ChampionController;
import tacticalChaos.controller.GameController;
import tacticalChaos.model.*;

public class GuiPlayerIO extends Application implements PlayerIO, EventHandler<ActionEvent> {

    Player player;
    int[][] type;

    public GuiPlayerIO(Player player, int[][] type) {
        this.player = player;
        this.type=type;
        start(new Stage());
    }

    private Stage window;

    // region __FIELD SECTION__
    // Scroll Panes
    private final ScrollPane fieldScrollPane = new ScrollPane();

    // Grids
    private final GridPane fieldGrid = new GridPane();
    private final GridPane fieldPositionGrid = new GridPane();

    // Combo Boxes
    private final ComboBox<Champion>[][] arena = new ComboBox[Main.n][Main.m];

    // Buttons
    private final Button[][] setPositionButton = new Button[Main.n][Main.m];

    // Images
    // types
    private Image grassImage;
    private Image terrainImage;
    private Image waterImage;
    private Image invisibleImage;

    // items
    private Image magicHatImage;
    private Image warriorGlovesImage;
    private Image knightArmorImage;
    private Image angryCloakImage;
    private Image nightShiftImage;
    private Image voidHitImage;
    private Image universeCoreImage;

    private final ImageView[][] images = new ImageView[Main.n][Main.m];
    private final ImageView[][] images2 = new ImageView[Main.n][Main.m];

    // Locks
    private final Lock pressingChampionField = new Lock();
    private final Lock positionLock = new Lock();
    //endregion

    // region __RIGHT SECTION__
    // Grids
    private final GridPane rightGrid = new GridPane();

    // VBoxes
    private final VBox attr = new VBox();

    // Labels
    private final Label championName = new Label("Name : ");
    private final Label championClasses = new Label("Classes : ");
    private final Label healthLabel = new Label("Health : ");
    private final Label manaLabel = new Label("Mana : ");
    private final Label manaCostLabel = new Label("Mana Cost : ");
    private final Label gameNotification = new Label("Notification :");
    private final Label messageLabel = new Label();
    private final Label stateLabel = new Label("State : ");
    private final Label itemsLabel = new Label("Items : ");
    private final Label enemyLabel = new Label("Enemy :");

    // Text Fields
    private final TextField attackTextField = new TextField();

    // Combo Boxes
    private final ComboBox<String> movementComboBox = new ComboBox<>();
    private final ComboBox<String> swapComboBox = new ComboBox<>();

    // Buttons
    private final Button attackButton = new Button("Attack!");
    private final Button useAbilityButton = new Button("Use Ability");
    private final Button sellButton = new Button("Sell");
    private final Button movementButton = new Button("Movement");
    private final Button swapButton = new Button("Swap");

    // Locks
    private final Lock commandLock = new Lock();
    private final Lock moveLock = new Lock();
    private final Lock directionLock = new Lock();
    private final Lock swapLock = new Lock();
    // endregion

    // region __DOWN SECTION__
    // Grids
    private final GridPane downGrid = new GridPane();

    // HBoxes
    private final HBox benchHBox = new HBox(10);
    private final HBox storeHBox = new HBox(10);

    // Labels
    private final Label storeLabel = new Label("Store :");
    private final Label benchLabel = new Label("Bench :");
    private final Label goldsLabel = new Label("golds :");
    private final Label goldsValueLabel = new Label("0");

    // Buttons
    private final Button addToBenchButton = new Button("Add to bench");
    private final Button nextRoundButton = new Button("Next Round");
    private final Button saveGameButton = new Button("Save Game");

    // Locks
    private final Lock pressingStoreLock = new Lock();
    // endregion

    // region __Variables__
    private Champion selectedStoreChampion;
    private Champion selectedBenchChampion;
    private Champion selectedFieldChampion;
    private int selectedCommand;
    private boolean canSetPos = false;
    private Pair<Integer, Integer> posToSetChampion;
    private String enemyToAttack;
    private String selectedMove;
    private String selectedDirection;
    private String selectedChampionToSwapWith;
    private Pair<Integer, Integer> selectedPos;
    // endregion

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle(player.name);

        // region __FIELD SECTION__
        // load images
        try {
            grassImage = new Image(new FileInputStream("assets\\cell types\\grass.jpg"));
            terrainImage = new Image(new FileInputStream("assets\\cell types\\terrain.jpg"));
            waterImage = new Image(new FileInputStream("assets\\cell types\\water.jpg"));
            invisibleImage = new Image(new FileInputStream("assets\\cell types\\invisible.jpg"));
            magicHatImage = new Image(new FileInputStream("assets\\items\\magic hat.jpg"));
            warriorGlovesImage = new Image(new FileInputStream("assets\\items\\warrior gloves.jpg"));
            knightArmorImage = new Image(new FileInputStream("assets\\items\\knight armor.jpg"));
            angryCloakImage = new Image(new FileInputStream("assets\\items\\angry cloak.jpg"));
            nightShiftImage = new Image(new FileInputStream("assets\\items\\night shift.jpg"));
            voidHitImage = new Image(new FileInputStream("assets\\items\\void hit.jpg"));
            universeCoreImage = new Image(new FileInputStream("assets\\items\\universe core.jpg"));
        } catch (FileNotFoundException e) {e.printStackTrace();}

        // fieldGrid & fieldPositionGrid
        fieldGrid.setPadding(new Insets(0, 0, 0, 0));
        fieldPositionGrid.setPadding(new Insets(0, 0, 0, 0));
        fieldGrid.setVgap(2);
        fieldPositionGrid.setVgap(2);
        fieldGrid.setHgap(2);
        fieldPositionGrid.setHgap(2);

        for (int i = 0; i < Main.n; i++) {
            for (int j = 0; j < Main.m; j++) {
                arena[i][j] = new ComboBox<>();
                arena[i][j].setOnAction(this);
                arena[i][j].setMinWidth(130);
                arena[i][j].setMinHeight(26);
            }
        }
        for (int i = 0; i < Main.n; i++) {
            for (int j = 0; j < Main.m; j++) {
                setPositionButton[i][j] = new Button();
                setPositionButton[i][j].setOnAction(this);
                setPositionButton[i][j].setMinWidth(130);
                setPositionButton[i][j].setMinHeight(26);
            }
        }
        for (int i = 0; i < Main.n; i++) {
            for (int j = 0; j < Main.m; j++) {
                images[i][j] = new ImageView();
                images2[i][j] = new ImageView();
                images[i][j].setFitWidth(130);
                images[i][j].setFitHeight(26);
                images2[i][j].setFitWidth(130);
                images2[i][j].setFitHeight(26);
                int finalI = i;
                int finalJ = j;
                images[i][j].addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    synchronized (positionLock) {
                        selectedPos = new Pair<>(finalI, finalJ);
                        positionLock.unlock();
                        positionLock.notify();
                    }

                    if (canSetPos) {
                        synchronized (moveLock) {
                            selectedMove = "Set Position";
                            posToSetChampion = new Pair<>(finalI, finalJ);
                            moveLock.unlock();
                            moveLock.notify();
                        }
                    }
                });
                images2[i][j].addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    synchronized (positionLock) {
                        selectedPos = new Pair<>(finalI, finalJ);
                        positionLock.unlock();
                        positionLock.notify();
                    }

                    if (canSetPos) {
                        synchronized (moveLock) {
                            selectedMove = "Set Position";
                            posToSetChampion = new Pair<>(finalI, finalJ);
                            moveLock.unlock();
                            moveLock.notify();
                        }
                    }
                });
            }
        }

        // fieldScrollPane
        fieldScrollPane.setContent(fieldGrid);
        fieldScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        fieldScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // endregion

        // region __RIGHT SECTION__

        championName.setFont(new Font(14));
        championClasses.setFont(new Font(14));
        healthLabel.setFont(new Font(14));
        manaLabel.setFont(new Font(14));
        manaCostLabel.setFont(new Font(14));
        gameNotification.setFont(new Font(14));
        messageLabel.setFont(new Font(14));
        stateLabel.setFont(new Font(14));
        itemsLabel.setFont(new Font(14));
        enemyLabel.setFont(new Font(14));

        movementButton.setMinWidth(80);
        movementComboBox.setMinWidth(80);
        swapButton.setMinWidth(80);
        swapComboBox.setMinWidth(80);

        messageLabel.setPadding(new Insets(0, 4, 8, 4));
        attackTextField.setMaxWidth(80);

        useAbilityButton.setOnAction(this);
        useAbilityButton.setVisible(false);
        attackButton.setOnAction(this);
        attackButton.setVisible(false);
        attackTextField.setVisible(false);
        enemyLabel.setVisible(false);
        sellButton.setOnAction(this);
        sellButton.setVisible(false);
        swapComboBox.setOnAction(this);
        swapComboBox.setVisible(false);
        movementComboBox.setOnAction(this);
        movementComboBox.setVisible(false);
        movementButton.setOnAction(this);
        movementButton.setVisible(false);
        swapButton.setOnAction(this);
        swapButton.setVisible(false);

        // champion attributes
        attr.setSpacing(8);
        attr.setPadding(new Insets(8, 4, 8, 4));
        attr.getChildren().addAll(championName, championClasses, healthLabel, manaLabel, manaCostLabel,itemsLabel);

        // rightGrid
        rightGrid.setPadding(new Insets(8, 4, 8, 4));
        rightGrid.setVgap(8);
        rightGrid.setHgap(5);

        GridPane.setConstraints(useAbilityButton, 0, 0);
        GridPane.setConstraints(enemyLabel, 0, 1);
        GridPane.setConstraints(attackTextField, 1, 1);
        GridPane.setConstraints(attackButton, 2, 1);
        GridPane.setConstraints(sellButton, 0, 2);
        GridPane.setConstraints(movementComboBox, 0, 3);
        GridPane.setConstraints(movementButton, 0, 3);
        GridPane.setConstraints(swapComboBox, 0, 4);
        GridPane.setConstraints(swapButton, 0, 4);

        rightGrid.getChildren().addAll(useAbilityButton, enemyLabel, attackTextField, attackButton, sellButton, movementComboBox, swapComboBox, movementButton, swapButton);

        // VBox
        VBox vBox = new VBox(4);
        vBox.setPadding(new Insets(0, 4, 10, 4));
        vBox.getChildren().addAll(stateLabel, gameNotification, messageLabel);

        // Border Pane
        BorderPane bp = new BorderPane();
        bp.setTop(rightGrid);
        bp.setCenter(attr);
        bp.setBottom(vBox);

        // endregion

        // region __DOWN SECTION__

        storeLabel.setFont(new Font(14));
        benchLabel.setFont(new Font(14));
        goldsLabel.setFont(new Font(14));
        goldsValueLabel.setFont(new Font(14));

        addToBenchButton.setOnAction(this);
        nextRoundButton.setOnAction(this);
        saveGameButton.setOnAction(this);
        saveGameButton.setVisible(false);

        // downGrid
        //downGrid.setPadding(new Insets(15, 10, 8, 10));
        downGrid.setVgap(8);
        downGrid.setHgap(8);
        downGrid.setPrefHeight(100);
        downGrid.setPrefWidth(640);

        GridPane.setConstraints(benchLabel, 0, 0);
        GridPane.setConstraints(benchHBox, 1, 0);
        GridPane.setConstraints(goldsLabel, 0, 1);
        GridPane.setConstraints(goldsValueLabel, 1, 1);
        GridPane.setConstraints(storeLabel, 0, 2);
        GridPane.setConstraints(storeHBox, 1, 2);
        GridPane.setConstraints(nextRoundButton, 1, 3);

        downGrid.getChildren().addAll(benchLabel, storeLabel, benchHBox, storeHBox, goldsLabel, goldsValueLabel, nextRoundButton,saveGameButton);

        HBox hBox = new HBox(10);
        hBox.setPadding(new Insets(15, 10, 8, 10));
        hBox.setAlignment(Pos.BOTTOM_LEFT);
        hBox.getChildren().addAll(downGrid, saveGameButton);

        //GridPane.setConstraints(saveGameButton, 2, 3);
        // endregion

        // Scene Border Pane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(fieldScrollPane);
        borderPane.setBottom(hBox);
        borderPane.setRight(bp);

        Scene scene = new Scene(borderPane, 1000, 700);

        window.setScene(scene);
        window.show();
    }

    @Override
    public void showArena(BattleField field) {
        Platform.runLater(() -> {

            // clear field grids
            fieldGrid.getChildren().clear();
            fieldPositionGrid.getChildren().clear();

            // initialize arena ChoiceBoxes & setPosition Buttons
            for (int i = 0; i < Main.n; i++) {
                for (int j = 0; j < Main.m; j++) {
                    arena[i][j].getItems().clear();
                    setPositionButton[i][j].setText(null);
                }
            }

            // show arena
            for (int i = 0; i < Main.n; i++) {
                for (int j = 0; j < Main.m; j++) {
                    // invisible cell
                    if (!player.appear[i][j]) {
                        images[i][j].setImage(invisibleImage);
                        GridPane.setConstraints(images[i][j],j,i);
                        fieldGrid.getChildren().add(images[i][j]);

                        images2[i][j].setImage(invisibleImage);
                        GridPane.setConstraints(images2[i][j],j,i);
                        fieldPositionGrid.getChildren().add(images2[i][j]);
                    }
                    // visible cell
                    else {
                        ArrayList<Champion> champions = field.battleField[i][j];
                        // show champions
                        if(champions.size() > 0){
                            for (Champion champion : champions) {
                                arena[i][j].getItems().add(champion);
                                arena[i][j].setPromptText(champion.name);
                                setPositionButton[i][j].setText(champion.name);
                            }
                            GridPane.setConstraints(arena[i][j],j,i);
                            fieldGrid.getChildren().add(arena[i][j]);

                            GridPane.setConstraints(setPositionButton[i][j],j,i);
                            fieldPositionGrid.getChildren().add(setPositionButton[i][j]);
                        }
                        // no champion on the cell
                        else{
                            ArrayList<Item> items = field.item[i][j];
                            // show items
                            if(items.size()>0){

                                HBox hBox = new HBox(1);
                                HBox hBox2 = new HBox(1);

                                hBox.setAlignment(Pos.BASELINE_CENTER);
                                hBox2.setAlignment(Pos.BASELINE_CENTER);

                                int finalI = i;
                                int finalJ = j;
                                hBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                    synchronized (positionLock) {
                                        selectedPos = new Pair<>(finalI, finalJ);
                                        positionLock.unlock();
                                        positionLock.notify();
                                    }

                                    if (canSetPos) {
                                        synchronized (moveLock) {
                                            selectedMove = "Set Position";
                                            posToSetChampion = new Pair<>(finalI, finalJ);
                                            moveLock.unlock();
                                            moveLock.notify();
                                        }
                                    }
                                });
                                hBox2.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                                    synchronized (positionLock) {
                                        selectedPos = new Pair<>(finalI, finalJ);
                                        positionLock.unlock();
                                        positionLock.notify();
                                    }

                                    if (canSetPos) {
                                        synchronized (moveLock) {
                                            selectedMove = "Set Position";
                                            posToSetChampion = new Pair<>(finalI, finalJ);
                                            moveLock.unlock();
                                            moveLock.notify();
                                        }
                                    }
                                });

                                for(Item item:items){

                                    ImageView imageView = new ImageView();
                                    ImageView imageView2 = new ImageView();
                                    imageView.setFitWidth(26);
                                    imageView.setFitHeight(26);
                                    imageView2.setFitWidth(26);
                                    imageView2.setFitHeight(26);

                                    switch (item.name){
                                        case "Magic Hat":
                                            imageView.setImage(magicHatImage);
                                            imageView2.setImage(magicHatImage);
                                            break;
                                        case "Warrior Gloves":
                                            imageView.setImage(warriorGlovesImage);
                                            imageView2.setImage(warriorGlovesImage);
                                            break;
                                        case "Knight Armor":
                                            imageView.setImage(knightArmorImage);
                                            imageView2.setImage(knightArmorImage);
                                            break;
                                        case "Angry Cloak":
                                            imageView.setImage(angryCloakImage);
                                            imageView2.setImage(angryCloakImage);
                                            break;
                                        case "Night Shift":
                                            imageView.setImage(nightShiftImage);
                                            imageView2.setImage(nightShiftImage);
                                            break;
                                        case "Void Hit":
                                            imageView.setImage(voidHitImage);
                                            imageView2.setImage(voidHitImage);
                                            break;
                                        case "Universe Core":
                                            imageView.setImage(universeCoreImage);
                                            imageView2.setImage(universeCoreImage);
                                            break;
                                    }
                                    hBox.getChildren().add(imageView);
                                    hBox2.getChildren().add(imageView2);
                                }

                                GridPane.setConstraints(hBox,j,i);
                                fieldGrid.getChildren().add(hBox);

                                GridPane.setConstraints(hBox2,j,i);
                                fieldPositionGrid.getChildren().add(hBox2);
                            }
                            // no items on the cell
                            else{
                                // show cell type
                                int type = field.type[i][j];
                                // standard
                                if(type==1){
                                    arena[i][j].setPromptText(null);
                                    setPositionButton[i][j].setText(null);

                                    GridPane.setConstraints(arena[i][j],j,i);
                                    fieldGrid.getChildren().add(arena[i][j]);
                                    GridPane.setConstraints(setPositionButton[i][j],j,i);
                                    fieldPositionGrid.getChildren().add(setPositionButton[i][j]);
                                }
                                // grass
                                else if(type==2){
                                    images[i][j].setImage(grassImage);
                                    GridPane.setConstraints(images[i][j],j,i);
                                    fieldGrid.getChildren().add(images[i][j]);

                                    images2[i][j].setImage(grassImage);
                                    GridPane.setConstraints(images2[i][j],j,i);
                                    fieldPositionGrid.getChildren().add(images2[i][j]);
                                }
                                // terrain
                                else if(type==3){
                                    images[i][j].setImage(terrainImage);
                                    GridPane.setConstraints(images[i][j],j,i);
                                    fieldGrid.getChildren().add(images[i][j]);

                                    images2[i][j].setImage(terrainImage);
                                    GridPane.setConstraints(images2[i][j],j,i);
                                    fieldPositionGrid.getChildren().add(images2[i][j]);
                                }
                                // water
                                else{
                                    images[i][j].setImage(waterImage);
                                    GridPane.setConstraints(images[i][j],j,i);
                                    fieldGrid.getChildren().add(images[i][j]);

                                    images2[i][j].setImage(waterImage);
                                    GridPane.setConstraints(images2[i][j],j,i);
                                    fieldPositionGrid.getChildren().add(images2[i][j]);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void closeWindow() {

        Platform.runLater(() -> window.close());
    }

    @Override
    public void notification(String message) {
        Platform.runLater(() -> messageLabel.setText(message));

    }

    @Override
    public void showBench() {
        Platform.runLater(() -> benchHBox.getChildren().clear());


        for (int i = 0; i < player.champsBench.size(); i++) {
            Button button = new Button(player.champsBench.get(i).name);
            button.setUserData(player.champsBench.get(i));
            button.setOnAction(this);

            Platform.runLater(() -> benchHBox.getChildren().add(button));
        }
    }

    @Override
    public int selectMove(ArrayList<String> moves) {
        Platform.runLater(() -> {
            for (String move : moves) {
                if (move.equals("Use Ability")) useAbilityButton.setVisible(true);
                else if (move.equals("Basic Attack")) {
                    attackButton.setVisible(true);
                    enemyLabel.setVisible(true);
                    attackTextField.setVisible(true);
                } else if (move.equals("Sell")) sellButton.setVisible(true);
                else if (move.equals("Set Position")) canSetPos = true;
                else if (move.equals("Swap")) swapButton.setVisible(true);
                else if (move.equals("Movement")) movementButton.setVisible(true);

            }
        });

        synchronized (moveLock) {
            moveLock.lock();
            while (moveLock.locked) {
                try {
                    moveLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).equals(selectedMove)) return i + 1;
        }
        return 0;
    }

    @Override
    public Pair<Integer, Integer> selectPositionInField() {
        return posToSetChampion;
    }

    @Override
    public int selectCommand() {

        synchronized (commandLock) {
            commandLock.lock();
            while (commandLock.locked) {
                try {
                    commandLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return selectedCommand;

    }

    @Override
    public Pair<Integer, Integer> selectPosition(Champion ch) {

        if (player.champs.size() == player.maxChampsInField) {
            notification("can't add to field!  champion was added to bench");
            return new Pair<>(-1, -1);
        }

        Platform.runLater(() -> {
            GridPane.setConstraints(addToBenchButton, 2, 0);
            downGrid.getChildren().add(addToBenchButton);
            fieldScrollPane.setContent(fieldPositionGrid);
        });

        synchronized (positionLock) {
            positionLock.lock();
            while (positionLock.locked) {
                try {
                    positionLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if(selectedPos.getKey()!=-1){
                    if(type[selectedPos.getKey()][selectedPos.getValue()]==3){
                        positionLock.lock();
                        notification("can't position on terrain cell!");
                    }
                }
            }
        }

        Platform.runLater(() -> {
            downGrid.getChildren().remove(addToBenchButton);
            fieldScrollPane.setContent(fieldGrid);
        });


        return selectedPos;

    }

    @Override
    public void updateGoldsValueLabel() {
        Platform.runLater(() -> goldsValueLabel.setText(String.valueOf(player.golds)));
    }

    @Override
    public void equippingItems() {
        int canGive = 3*player.champs.size();

        if(!player.items.isEmpty() && canGive>0)
            setState("Equipping items");

        while (!player.items.isEmpty() && canGive>0){

            Item item = player.items.get(0);
            notification(item.name);

            synchronized (pressingChampionField){
                pressingChampionField.lock();
                while (pressingChampionField.locked){
                    try {
                        pressingChampionField.wait();
                    }catch (InterruptedException ex) { ex.printStackTrace(); }
                    if(selectedFieldChampion==null) return;
                    if(selectedFieldChampion.playerNumber!=player.number){
                        pressingChampionField.lock();
                    }else{
                        if(selectedFieldChampion.items.size()==3){
                            notification(item.name+" items limit reached!");
                            pressingChampionField.lock();
                        }else{
                            ChampionController.addItem(selectedFieldChampion,item);
                            player.items.remove(item);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void showTempStore(ArrayList<Champion> list, boolean numbered) {

        Platform.runLater(() -> storeHBox.getChildren().clear());


        for (Champion champion : list) {
            Button button = new Button(champion.name + " " + champion.attributes.goldCost);
            button.setUserData(champion);
            button.setOnAction(this);

            Platform.runLater(() -> storeHBox.getChildren().add(button));
        }
    }

    @Override
    public Champion buy(ArrayList<Champion> list) {

        synchronized (pressingStoreLock) {
            while (true) {
                pressingStoreLock.lock();
                while (pressingStoreLock.locked) {
                    try {
                        pressingStoreLock.wait();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (selectedStoreChampion == null) return null;
                if (selectedStoreChampion.attributes.goldCost <= player.golds) break;
                else notification("no enough golds!");
            }
        }

        return selectedStoreChampion;
    }

    @Override
    public String selectEnemy() {
        return enemyToAttack;
    }

    @Override
    public int selectDirection(ArrayList<String> directions) {
        Platform.runLater(() -> {
            movementButton.setVisible(false);
            for (int i = 0; i < directions.size() - 1; i++) {
                movementComboBox.getItems().add(directions.get(i));
            }
            movementComboBox.setVisible(true);
            movementComboBox.show();
        });

        synchronized (directionLock) {
            directionLock.lock();
            while (directionLock.locked) {
                try {
                    directionLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (int i = 0; i < directions.size(); i++) {
            if (directions.get(i).equals(selectedDirection)) return i + 1;
        }

        return 0;

    }

    @Override
    public int selectChampFromField() {
        Platform.runLater(() -> {
            swapButton.setVisible(false);
            for (int i = 0; i < player.champs.size(); i++) {
                swapComboBox.getItems().add(player.champs.get(i).name);
            }
            swapComboBox.setVisible(true);
            swapComboBox.show();
        });

        synchronized (swapLock) {
            swapLock.lock();
            while (swapLock.locked) {
                try {
                    swapLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (int i = 0; i < player.champs.size(); i++) {
            if (player.champs.get(i).name.equals(selectedChampionToSwapWith)) return i + 1;
        }

        return 0;

    }

    @Override
    public int selectChampFromBench() {
        Platform.runLater(() -> {
            swapButton.setVisible(false);
            for (int i = 0; i < player.champsBench.size(); i++) {
                swapComboBox.getItems().add(player.champsBench.get(i).name);
            }
            swapComboBox.setVisible(true);
            swapComboBox.show();
        });

        synchronized (swapLock) {
            swapLock.lock();
            while (swapLock.locked) {
                try {
                    swapLock.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        for (int i = 0; i < player.champsBench.size(); i++) {
            if (player.champsBench.get(i).name.equals(selectedChampionToSwapWith)) return i + 1;
        }

        return 0;

    }

    @Override
    public void setState(String state) {
        if(state.equals("Planning"))
            saveGameButton.setVisible(true);
        Platform.runLater(() -> stateLabel.setText("State : " + state));
    }

    @Override
    public void updateButtons() {
        Platform.runLater(() -> {
            canSetPos = false;
            useAbilityButton.setVisible(false);
            enemyLabel.setVisible(false);
            attackTextField.setVisible(false);
            attackButton.setVisible(false);
            sellButton.setVisible(false);

            swapComboBox.getItems().clear();
            swapComboBox.setVisible(false);

            movementComboBox.getItems().clear();
            movementComboBox.setVisible(false);

            movementButton.setVisible(false);
            swapButton.setVisible(false);
        });
    }

    @Override
    public void handle(ActionEvent event) {

        messageLabel.setText("");

        // Save button was pressed
        if(event.getSource()==saveGameButton){
            GameController.willSave = true;
            setState("Waiting for all players\n to confirm 'Save Game'..");
            nextRoundButton.fire();
        }

        // Next Round button was pressed
        if (event.getSource() == nextRoundButton) {

            synchronized (pressingChampionField){
                selectedFieldChampion=null;
                pressingChampionField.unlock();
                pressingChampionField.notify();
            }

            synchronized (pressingStoreLock) {
                selectedStoreChampion = null;
                pressingStoreLock.unlock();
                pressingStoreLock.notify();
            }

            synchronized (commandLock) {
                selectedCommand = player.champs.size() + player.champsBench.size() + 1;
                commandLock.unlock();
                commandLock.notify();
            }

            synchronized (positionLock) {
                boolean ok=false;

                for (int i=0;i<Main.n&&!ok;i++){
                    for (int j=0;j<Main.m&&!ok;j++){
                        if(type[i][j]!=3){
                            selectedPos = new Pair<>(i,j);
                            ok=true;
                        }
                    }
                }

                positionLock.unlock();
                positionLock.notify();
            }

            synchronized (moveLock) {
                selectedMove = "Exit";
                moveLock.unlock();
                moveLock.notify();
            }

            synchronized (directionLock) {
                selectedDirection = "Exit";
                directionLock.unlock();
                directionLock.notify();
            }

            synchronized (swapLock) {
                selectedChampionToSwapWith = "Exit";
                swapLock.unlock();
                swapLock.notify();
            }

            return;
        }

        // Use Ability button pressed
        if (event.getSource() == useAbilityButton) {
            synchronized (moveLock) {
                selectedMove = "Use Ability";
                moveLock.unlock();
                moveLock.notify();
            }
            return;
        }

        // Attack button pressed
        if (event.getSource() == attackButton) {
            synchronized (moveLock) {
                selectedMove = "Basic Attack";
                enemyToAttack = attackTextField.getText();
                moveLock.unlock();
                moveLock.notify();
            }
            return;
        }

        // Sell button pressed
        if (event.getSource() == sellButton) {
            synchronized (moveLock) {
                selectedMove = "Sell";
                moveLock.unlock();
                moveLock.notify();
            }
            return;
        }

        // Movement button pressed
        if (event.getSource() == movementButton) {
            synchronized (moveLock) {
                selectedMove = "Movement";
                moveLock.unlock();
                moveLock.notify();
            }
            return;
        }

        // Movement Direction Choice Box selected
        if (event.getSource() == movementComboBox) {
            synchronized (directionLock) {
                selectedDirection = movementComboBox.getValue();
                directionLock.unlock();
                directionLock.notify();
            }
            return;
        }

        // Swap button pressed
        if (event.getSource() == swapButton) {
            synchronized (moveLock) {
                selectedMove = "Swap";
                moveLock.unlock();
                moveLock.notify();
            }
            return;
        }

        // Champion to Swap with ChoiceBox selected
        if (event.getSource() == swapComboBox) {
            synchronized (swapLock) {
                selectedChampionToSwapWith = swapComboBox.getValue();
                swapLock.unlock();
                swapLock.notify();
            }
            return;
        }

        // Add To Bench button pressed
        if (event.getSource() == addToBenchButton) {
            if (player.champsBench.size() == player.maxChampsInBench) {
                notification("bench is full!");
                return;
            }
            synchronized (positionLock) {
                selectedPos = new Pair<>(-1, -1);
                positionLock.unlock();
                positionLock.notify();
            }
            return;
        }

        // a Champion from the bench was selected
        for (int i = 0; i < benchHBox.getChildren().size(); i++) {
            if (event.getSource() == benchHBox.getChildren().get(i)) {
                selectedBenchChampion = (Champion) benchHBox.getChildren().get(i).getUserData();

                championName.setText("Name : " + selectedBenchChampion.name);
                healthLabel.setText("Health : " + selectedBenchChampion.health + "/" + selectedBenchChampion.attributes.maxHealth);
                manaLabel.setText("Mana : " + selectedBenchChampion.mana);
                manaCostLabel.setText("Mana Cost : " + selectedBenchChampion.attributes.manaCost);

                String s = "Classes : ";

                for (int j=0;j<selectedBenchChampion.attributes.groups.size();j++){
                    if(j==selectedBenchChampion.attributes.groups.size()-1) s+=selectedBenchChampion.attributes.groups.get(j);
                    else s+=selectedBenchChampion.attributes.groups.get(j)+",";
                }

                championClasses.setText(s);

                s="Items : ";

                for (int j=0;j<selectedBenchChampion.items.size();j++){
                    if(j==selectedBenchChampion.items.size()-1) s+=selectedBenchChampion.items.get(j).name;
                    else s+=selectedBenchChampion.items.get(j)+",";
                }

                itemsLabel.setText(s);

                synchronized (pressingStoreLock) {
                    selectedStoreChampion = null;
                    pressingStoreLock.unlock();
                    pressingStoreLock.notify();
                }
                synchronized (moveLock) {
                    selectedMove = "Exit";
                    moveLock.unlock();
                    moveLock.notify();
                }
                synchronized (commandLock) {
                    selectedCommand = player.champs.size() + i + 1;
                    commandLock.unlock();
                    commandLock.notify();
                }


                return;

            }
        }

        // a Champion from the store was selected
        for (int i = 0; i < storeHBox.getChildren().size(); i++) {
            if (event.getSource() == storeHBox.getChildren().get(i)) {
                synchronized (pressingStoreLock) {
                    selectedStoreChampion = (Champion) storeHBox.getChildren().get(i).getUserData();
                    pressingStoreLock.unlock();
                    pressingStoreLock.notify();
                }
                synchronized (moveLock) {
                    selectedMove = "Exit";
                    moveLock.unlock();
                    moveLock.notify();
                }
                synchronized (commandLock) {
                    selectedCommand = 0;
                    commandLock.unlock();
                    commandLock.notify();
                }


                return;
            }
        }

        // a Champion from the arena was selected
        for (int i = 0; i < Main.n; i++) {
            for (int j = 0; j < Main.m; j++) {
                if (event.getSource() == arena[i][j]) {
                    selectedFieldChampion = arena[i][j].getValue();
                    if (selectedFieldChampion != null && selectedFieldChampion.attributes!=null) {

                        championName.setText("Name : " + selectedFieldChampion.name);
                        healthLabel.setText("Health : " + selectedFieldChampion.health + "/" + selectedFieldChampion.attributes.maxHealth);
                        manaLabel.setText("Mana : " + selectedFieldChampion.mana);
                        manaCostLabel.setText("Mana Cost : " + selectedFieldChampion.attributes.manaCost);

                        String s = "Classes : ";

                        for (int k=0;k<selectedFieldChampion.attributes.groups.size();k++){
                            if(k==selectedFieldChampion.attributes.groups.size()-1) s+=selectedFieldChampion.attributes.groups.get(k);
                            else s+=selectedFieldChampion.attributes.groups.get(k)+",";
                        }

                        championClasses.setText(s);

                        s="Items : ";

                        for (int k=0;k<selectedFieldChampion.items.size();k++){
                            if(k==selectedFieldChampion.items.size()-1) s+=selectedFieldChampion.items.get(k).name;
                            else s+=selectedFieldChampion.items.get(k)+",";
                        }

                        itemsLabel.setText(s);

                        if (arena[i][j].getValue().playerNumber == player.number) {
                            synchronized (pressingStoreLock) {
                                selectedStoreChampion = null;
                                pressingStoreLock.unlock();
                                pressingStoreLock.notify();
                            }
                            synchronized (moveLock) {
                                selectedMove = "Exit";
                                moveLock.unlock();
                                moveLock.notify();
                            }
                            synchronized (commandLock) {
                                for (int k = 0; k < player.champs.size(); k++) {
                                    if (player.champs.get(k) == arena[i][j].getValue()) {
                                        selectedCommand = k + 1;
                                        commandLock.unlock();
                                        commandLock.notify();
                                    }
                                }
                            }
                        }
                    }
                    if (canSetPos) {
                        synchronized (moveLock) {
                            selectedMove = "Set Position";
                            posToSetChampion = new Pair<>(i, j);
                            moveLock.unlock();
                            moveLock.notify();
                        }
                    }
                    return;
                }
            }
        }

        // a cell from the arena for positioning was selected
        for (int i = 0; i < Main.n; i++) {
            for (int j = 0; j < Main.m; j++) {
                if (event.getSource() == setPositionButton[i][j]) {
                    synchronized (positionLock) {
                        selectedPos = new Pair<>(i, j);
                        positionLock.unlock();
                        positionLock.notify();
                    }
                    return;
                }
            }
        }
    }
}
