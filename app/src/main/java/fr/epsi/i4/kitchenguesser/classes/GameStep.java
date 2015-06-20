package fr.epsi.i4.kitchenguesser.classes;

import java.util.ArrayList;
import java.util.List;

import fr.epsi.i4.kitchenguesser.entities.Thing;

/**
 * Created by paul on 13/06/2015.
 */
public class GameStep {
    private int questionId;
    private int answer;
    private ArrayList<Thing> deletedThings;

    public GameStep(int questionId, int answer){
        this.questionId = questionId;
        this.answer     = answer;
        deletedThings   = new ArrayList<>();
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public int getAnswer() {
        return this.answer;
    }

    public void addDeletedThing(Thing thing){
        deletedThings.add(thing);
    }

    @Override
    public String toString() {
        return "Question "+questionId+": "+answer;
    }
}