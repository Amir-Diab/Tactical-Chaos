package tacticalChaos.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BattleField implements Serializable {

    // the rectangular field's dimensions: n rows & m columns
    final public int n, m;

    // each cell in the field can contain multiple champions and items,
    // but it belongs only to one type (standard, grass, ..)
    public ArrayList<Champion>[][] battleField;
    public ArrayList<Item>[][] item;
    public int[][] type;

    public BattleField(int n, int m) {
        this.n = n;
        this.m = m;
        battleField = new ArrayList[n][m];
        item = new ArrayList[n][m];
        type = new int[n][m];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                battleField[i][j] = new ArrayList<>();
                item[i][j] = new ArrayList<>();
            }
        }
    }
}

