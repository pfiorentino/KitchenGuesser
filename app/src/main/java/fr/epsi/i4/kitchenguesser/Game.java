package fr.epsi.i4.kitchenguesser;

import java.util.ArrayList;

import fr.epsi.i4.kitchenguesser.entities.UserAnswer;

/**
 * Created by paul on 19/06/2015.
 */
public class Game {
    private static Game _instance;

    private ArrayList<UserAnswer> currentGame;

    public static Game getInstance() {
        if (_instance == null){
            _instance = new Game();
        }

        return _instance;
    }

    private Game() {
        this.currentGame = new ArrayList<>();
    }

    public void addAnswer(UserAnswer answer) {
        this.currentGame.add(answer);
    }

    public ArrayList<UserAnswer> getCurrentGame() {
        return this.currentGame;
    }

    public int getSize() {
        return this.currentGame.size();
    }

    public void resetGame() {
        this.currentGame = new ArrayList<>();
    }
}
