package fr.epsi.i4.kitchenguesser.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.epsi.i4.kitchenguesser.entities.Question;
import fr.epsi.i4.kitchenguesser.entities.Thing;
import fr.epsi.i4.kitchenguesser.entities.ThingQuestion;

/**
 * Created by paul on 19/06/2015.
 */
public class Game {
    private static final int MIN_SCORE_TO_KEEP = -3;
    private static final int FIRST_QUESTION_TO_CLEAN = 3;
    private static final int THRESHOLD_TO_CLEAN = 10;
    private static final int PRECISION_THRESHOLD = 10;
    private static final int MAX_QUESTIONS = 15;

    private static Game _instance;

    private SQLiteDatabase db;
    private Context ctx;

    private HashMap<Integer, Question> questions;
    private ArrayList<Thing> things;

    private ArrayList<GameStep> currentGame;

    public static Game getInstance() {
        if (_instance == null){
            _instance = new Game();
        }

        return _instance;
    }

    private Game() {

    }

    private int getScore(int questionId){
        int score = 1;

        int[] answers = {1,1,1,1,1,1};

        for (Thing thing : things) {
            if (thing.getScore() >= 0 && thing.getAnswer(questionId) > 0){
                answers[thing.getAnswer(questionId)]++;
            }
        }

        for (int answer : answers) {
            score *= answer;
        }

        return score;
    }

    private void updateThingsScore(int questionId, int answer) {
        for (Thing thing : things) {
            thing.updateScore(questionId, answer);
        }

        Collections.sort(things);
    }

    private void rollBackThingsScore(int questionId, int answer) {
        for (Thing thing : things) {
            thing.rollBackScore(questionId, answer);
        }

        Collections.sort(things);
    }

    private void cleanThingsList() {
        int highScore = things.get(0).getScore();
        for (int i = things.size()-1; i >= 0; i--) {
            if (things.get(i).getScore() < highScore-THRESHOLD_TO_CLEAN){
                things.remove(i);
            }
        }

        // Old method less efficient
        /*for (int i = things.size()-1; i >= 0; i--) {
            if (things.get(i).getScore() <= MIN_SCORE_TO_KEEP){
                things.remove(i);
            }
        }*/
    }

    private List<Thing> getThingsWithScore(int score) {
        List<Thing> output = new ArrayList();

        for (Thing thing : things) {
            if (thing.getScore() == score){
                output.add(thing);
            }
        }

        return output;
    }

    public ArrayList<GameStep> getCurrentGame() {
        return this.currentGame;
    }

    public int getSize() {
        return this.currentGame.size();
    }

    public void init(Context ctx) {
        Log.d("init", "Init game");
        this.ctx = ctx;
        if (db == null || !db.isOpen()) {
            KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(ctx);
            db = mDbHelper.getReadableDatabase();
        }

        this.currentGame = new ArrayList<>();
        this.things      = new ArrayList<>();
        this.questions   = new HashMap<>();

        things = (ArrayList) Thing.findAll(db);

        for (Question question : Question.findAll(db)){
            questions.put(question.getId(), question);
        }
    }

    public Question getRandomQuestion(){
        Random generator = new Random();
        Object[] values = questions.values().toArray();
        return (Question) values[generator.nextInt(values.length)];
    }

    public Question getBestQuestion() {
        int maxScore = -1;
        Question bestQuestion = null;

        // Dans 3% des cas on ressort une question au hasard
        if (Math.random() > 0.97f){
            bestQuestion = getRandomQuestion();
        } else {
            for (Map.Entry<Integer, Question> entry : questions.entrySet()) {
                int questionScore = getScore(entry.getKey());

                if (maxScore < questionScore || maxScore == questionScore && Math.random() > 0.5f){
                    maxScore = questionScore;
                    bestQuestion = entry.getValue();
                }
            }
        }

        return bestQuestion;
    }

    public Question rollBack() {
        Question removedQuestion = null;

        if (currentGame.size() > 0) {
            GameStep stepToCancel = currentGame.get(currentGame.size() - 1);

            removedQuestion = Question.findById(stepToCancel.getQuestionId(), db);
            questions.put(removedQuestion.getId(), removedQuestion);

            for (Thing thing : stepToCancel.getDeletedThings()) {
                things.add(thing);
            }

            rollBackThingsScore(stepToCancel.getQuestionId(), stepToCancel.getAnswer());

            currentGame.remove(stepToCancel);
        }

        return removedQuestion;
    }

    public Thing addAnswer(Question question, int answer) {
        Thing thingFound = null;

        GameStep currentStep = new GameStep(question.getId(), answer);

        updateThingsScore(question.getId(), answer);

        if (this.currentGame.size() >= FIRST_QUESTION_TO_CLEAN) {
            cleanThingsList();
        }

        questions.remove(question.getId());

        if (questions.size() > 0 && things.size() > 1 && Game.getInstance().getSize() < MAX_QUESTIONS){
            float bestPrecision     = ((float) things.get(0).getScore()/(Game.getInstance().getSize()*3))*100;
            float secondPrecision   = ((float) things.get(1).getScore()/(Game.getInstance().getSize()*3))*100;

            // Dans le cas où un objet se démarque
            if ((bestPrecision-PRECISION_THRESHOLD > secondPrecision && Game.getInstance().getSize() > 5)){
                List<Thing> bestThings = getThingsWithScore(things.get(0).getScore());

                if (bestThings.size() > 1){
                    things = (ArrayList) bestThings;
                } else {
                    thingFound = things.get(0);
                }
            }
        } else {
            thingFound = things.get(0);
        }

        this.currentGame.add(currentStep);

        Log.d("things", things.toString());

        return thingFound;
    }

    public void addMissingAnswers(Thing thing) {
        for (GameStep step : currentGame){
            ThingQuestion tq = new ThingQuestion(0, thing.getId(), step.getQuestionId(), step.getAnswer());
            ThingQuestion.addThingQuestion(tq, db);
        }
    }
}
