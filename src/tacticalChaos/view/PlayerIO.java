package tacticalChaos.view;

import javafx.util.Pair;
import java.util.ArrayList;

import tacticalChaos.model.BattleField;
import tacticalChaos.model.Champion;

public interface PlayerIO {
    Champion buy(ArrayList<Champion> list)                       throws Exception;
    Pair<Integer,Integer> selectPosition(Champion champion);
    Pair<Integer,Integer> selectPositionInField()                throws Exception;
    int selectCommand();
    int selectMove(ArrayList<String>moves);
    String selectEnemy();
    int selectDirection(ArrayList<String>directions);
    int selectChampFromField();
    int selectChampFromBench();
    void notification(String message);
    void setState(String state);
    void showArena(BattleField field);
    void showBench();
    void showTempStore(ArrayList<Champion> list, boolean numbered);
    void updateButtons();
    void equippingItems();
    void closeWindow();
    void updateGoldsValueLabel();
}



