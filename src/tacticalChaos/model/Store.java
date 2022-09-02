package tacticalChaos.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Store implements Serializable {

    public int tempStoreSize;
    public ArrayList<Champion> champs = new ArrayList<>();


    public Store(int tempStoreSize){
        this.tempStoreSize = tempStoreSize;
    }

}
