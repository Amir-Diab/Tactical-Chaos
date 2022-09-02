package tacticalChaos.controller;

import java.util.ArrayList;
import java.util.Random;

import tacticalChaos.Main;
import tacticalChaos.model.Champion;
import tacticalChaos.model.Store;

public class StoreController {

    Store store;

    StoreController(Store store){
        this.store = store;
    }

    void generateRandomStore(){
        ArrayList<String> championsNames = Main.championsNames;

        store.champs.clear();
        Random rand = new Random();
        // generate 100 champions
        for(int i=0;i<100;i++){
            store.champs.add(ChampionController.newChampion(championsNames.get(rand.nextInt(championsNames.size())),1));
        }
    }

    void add(Champion ch){
        store.champs.add(ch);
    }

    void remove(Champion ch){
        store.champs.remove(ch);
    }

    synchronized ArrayList<Champion> generateTempStore(){
        ArrayList<Champion> tempStore = new ArrayList<>();
        Random rand = new Random();

        int mn = Math.min(store.champs.size(),store.tempStoreSize);
        for(int i=0;i<mn;i++){
            Champion ch = store.champs.get(rand.nextInt(store.champs.size()));
            remove(ch);
            tempStore.add(ch);
        }
        return tempStore;
    }

    // get back temp store champions that players didn't buy
    synchronized void receiveTempStore(ArrayList<Champion> tempStore){
        for(int i=0;i<tempStore.size();i++){
            add(tempStore.get(i));
        }
    }
}
