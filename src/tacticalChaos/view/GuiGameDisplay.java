package tacticalChaos.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import tacticalChaos.Main;
import tacticalChaos.model.*;

public class GuiGameDisplay implements GameDisplay , EventHandler<ActionEvent> {

    private final Stage window = new Stage();
    private final Label fieldLabel = new Label("field :");
    private final Label rowsLabel = new Label("rows :");
    private final Label columnsLabel = new Label("column :");
    private final Label numberOfPlayerLabel = new Label("number of players");
    private final Label teamNumberLabel = new Label(" Team");
    private final Label []playersLabels = new Label[8];
    private final TextField[] playersTextFields = new TextField[8];
    private final ChoiceBox<String>[] playersChoiceBoxes = new ChoiceBox[8];
    private final ChoiceBox<Integer>[] teamsNumbers = new ChoiceBox[8];
    private final TextField rowsTextField = new TextField();
    private final TextField columnsTextField = new TextField();
    private final ChoiceBox<Integer> playersNumberChoiceBox = new ChoiceBox<>();
    private final Button startButton = new Button("Start Game");
    private final GridPane grid = new GridPane();

    Game game;

    public GuiGameDisplay(Game game){
        this.game=game;
    }

    @Override
    public void gameSettings() {

        window.setTitle("Tactical Chaos");

        game.players_num=2;

        rowsTextField.setPromptText("1~100");
        columnsTextField.setPromptText("1~100");

        rowsTextField.setText("30");
        columnsTextField.setText("30");

        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(10);
        grid.setHgap(10);

        window.setMinWidth(600);
        window.setMinHeight(600);

        playersNumberChoiceBox.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        playersNumberChoiceBox.setValue(2);
        setPlayers(0,2);
        playersNumberChoiceBox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> setPlayers(oldValue,newValue));

        GridPane.setConstraints(fieldLabel, 0, 0);
        GridPane.setConstraints(rowsLabel, 0, 1);
        GridPane.setConstraints(rowsTextField, 1, 1);
        GridPane.setConstraints(columnsLabel, 0, 2);
        GridPane.setConstraints(columnsTextField, 1, 2);
        GridPane.setConstraints(numberOfPlayerLabel, 0, 3);
        GridPane.setConstraints(playersNumberChoiceBox, 1, 3);
        GridPane.setConstraints(teamNumberLabel, 3, 3);
        GridPane.setConstraints(startButton, 2, 12);

        grid.getChildren().addAll(fieldLabel, teamNumberLabel,rowsLabel, rowsTextField, columnsLabel, numberOfPlayerLabel, columnsTextField, playersNumberChoiceBox,startButton);

        startButton.setOnAction(this);

        Scene scene;
        scene = new Scene(grid, 600, 600);
        window.setScene(scene);
        window.showAndWait();
    }

    private void setPlayers(Integer oldPlayerNumber,int playersNumber){

        game.players_num=playersNumber;
        if(oldPlayerNumber!=null)
            for (int i=0;i<oldPlayerNumber;i++) grid.getChildren().removeAll(playersLabels[i], playersTextFields[i], playersChoiceBoxes[i],teamsNumbers[i]);

        for (int i=0;i<playersNumber;i++){
            playersLabels[i] = new Label("player "+(i+1)+" :");
            playersTextFields[i]=new TextField();
            playersTextFields[i].setPromptText("name");
            playersTextFields[i].setText("player "+(i+1));
            playersChoiceBoxes[i]=new ChoiceBox<>();
            teamsNumbers[i]=new ChoiceBox<>();
            playersChoiceBoxes[i].getItems().addAll("human","bot");
            teamsNumbers[i].getItems().addAll(1,2,3,4,5,6,7,8);
            if(i==0)
                playersChoiceBoxes[i].setValue("human");
            else
                playersChoiceBoxes[i].setValue("bot");
            teamsNumbers[i].setValue(i+1);
            GridPane.setConstraints(playersLabels[i],0,i+4);
            GridPane.setConstraints(playersTextFields[i], 1, i+4);
            GridPane.setConstraints(playersChoiceBoxes[i], 2, i+4);
            GridPane.setConstraints(teamsNumbers[i], 3, i+4);
            grid.getChildren().addAll(playersLabels[i], playersTextFields[i], playersChoiceBoxes[i],teamsNumbers[i]);
        }

    }

    @Override
    public void handle(ActionEvent event) {
        int n,m;
        if (event.getSource() == startButton) {
            n=Integer.parseInt(rowsTextField.getText());
            m=Integer.parseInt(columnsTextField.getText());

            Main.n=n; Main.m=m;
            game.field = new BattleField(n,m);
            game.store = new Store(5);

            for(int i=1;i<=game.players_num;i++){

                if(playersChoiceBoxes[i-1].getValue().equals("human")){
                    game.players[i] = new Player(playersTextFields[i-1].getText(),true,i,teamsNumbers[i-1].getValue(),9,8);
                }
                else
                    game.players[i] = new Player(playersTextFields[i-1].getText(),false,i,teamsNumbers[i-1].getValue(),9,8);
            }
            window.close();
        }
    }

}
