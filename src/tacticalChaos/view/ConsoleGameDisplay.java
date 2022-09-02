package tacticalChaos.view;

import java.util.Scanner;

import tacticalChaos.Main;
import tacticalChaos.model.*;

public class ConsoleGameDisplay implements GameDisplay {

    Game game;

    public ConsoleGameDisplay(Game game){
        this.game=game;
    }

    @Override
    public void gameSettings(){

        Scanner scr = new Scanner(System.in);

        System.out.print("field : \nrows = ");
        int n, m;
        n = scr.nextInt();
        System.out.print("columns = ");
        m = scr.nextInt();

        Main.n=n;
        Main.m=m;

        game.field = new BattleField(n,m);
        game.store = new Store(5);

        System.out.print("number of players = ");
        game.players_num = scr.nextInt();
        game.players = new Player[game.players_num + 1];
        for (int i = 1; i <= game.players_num; i++) {
            System.out.println("player " + i + " :");
            System.out.print("Name = ");
            String name = scr.next();

            System.out.println("Type :\n1-human \n 2-bot");
            int choice = scr.nextInt();

            System.out.print("team number = ");
            int teamNumber = scr.nextInt();

            if (choice == 1) game.players[i] = new Player(name,true, i,teamNumber, 9, 8);
            else             game.players[i] = new Player(name,false, i,teamNumber, 9, 8);

        }
    }

}
