package fr.epsi.i4.kitchenguesser;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import fr.epsi.i4.kitchenguesser.entities.Question;
import fr.epsi.i4.kitchenguesser.entities.Thing;
import fr.epsi.i4.kitchenguesser.entities.UserAnswer;


public class MainActivity extends ActionBarActivity {
    private static final int MIN_SCORE_TO_KEEP = -3;
    private static final int FIRST_QUESTION_TO_CLEAN = 3;
    private static final int THRESHOLD_TO_CLEAN = 10;
    private static final int PRECISION_THRESHOLD = 10;
    private static final int MAX_QUESTIONS = 15;

    private SQLiteDatabase db;

    private HashMap<Integer, Question> questions;
    private ArrayList<Thing> things;

    private Question currentQuestion;
    private TextView questionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openDB();
        initializeGame();

        currentQuestion = getRandomQuestion();

        questionTextView = (TextView) findViewById(R.id.questionTextView);
        questionTextView.setText(currentQuestion.getQuestion());

        Button yesButton = (Button) findViewById(R.id.yesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAnswer(1);
            }
        });

        Button probyesButton = (Button) findViewById(R.id.probyesButton);
        probyesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAnswer(2);
            }
        });

        Button unknownButton = (Button) findViewById(R.id.unknownButton);
        unknownButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAnswer(3);
            }
        });

        Button probnoButton = (Button) findViewById(R.id.probnoButton);
        probnoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAnswer(4);
            }
        });

        Button noButton = (Button) findViewById(R.id.noButton);
        noButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addAnswer(5);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_debug) {
            Thing thing = new Thing(3, "Couteau de cuisine");
            thingFound(thing);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }

    private void openDB() {
        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();
    }

    private void initializeGame() {
        Log.d("init", "Init game");

        Game.getInstance().resetGame();
        things      = new ArrayList<>();
        questions   = new HashMap<>();

        things = (ArrayList) Thing.findAll(db);

        for (Question question : Question.findAll(db)){
            questions.put(question.getId(), question);
        }
    }

    private Question getRandomQuestion(){
        Random generator = new Random();
        Object[] values = questions.values().toArray();
        return (Question) values[generator.nextInt(values.length)];
    }

    private Question getBestQuestion() {
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

    private void cleanThingsList() {
        int highScore = things.get(0).getScore();
        for (int i = things.size()-1; i >= 0; i--) {
            if (things.get(i).getScore() < highScore-THRESHOLD_TO_CLEAN){
                things.remove(i);
            }
        }

        /*for (int i = things.size()-1; i >= 0; i--) {
            if (things.get(i).getScore() <= MIN_SCORE_TO_KEEP){
                things.remove(i);
            }
        }*/
    }

    private void addAnswer(int answer) {
        Game.getInstance().addAnswer(new UserAnswer(currentQuestion.getId(), answer));
        updateThingsScore(currentQuestion.getId(), answer);

        if (Game.getInstance().getSize() >= FIRST_QUESTION_TO_CLEAN) {
            cleanThingsList();
        }

        questions.remove(currentQuestion.getId());

        if (questions.size() > 0 && things.size() > 1 && Game.getInstance().getSize() < MAX_QUESTIONS){
            float bestPrecision     = ((float) things.get(0).getScore()/(Game.getInstance().getSize()*3))*100;
            float secondPrecision   = ((float) things.get(1).getScore()/(Game.getInstance().getSize()*3))*100;

            // Dans le cas où un objet se démarque
            if ((bestPrecision-PRECISION_THRESHOLD > secondPrecision && Game.getInstance().getSize() > 5)){
                List<Thing> bestThings = getThingsWithScore(things.get(0).getScore());

                if (bestThings.size() > 1){
                    things = (ArrayList) bestThings;
                    currentQuestion = getBestQuestion();
                    questionTextView.setText(currentQuestion.getQuestion());
                } else {
                    thingFound(things.get(0));
                }
            } else {
                currentQuestion = getBestQuestion();
                questionTextView.setText(currentQuestion.getQuestion());
            }
        } else {
            thingFound(things.get(0));
        }
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

    private void thingFound(Thing thing){
        Intent intent = new Intent(this, ThingFoundActivity.class);
        intent.putExtra("thingId",thing.getId());
        startActivity(intent);
        finish();
    }

    private boolean rollBack() {
        //Log.d("");
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!rollBack()){
            super.onBackPressed();
        }
    }
}
