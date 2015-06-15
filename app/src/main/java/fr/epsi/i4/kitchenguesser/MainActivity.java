package fr.epsi.i4.kitchenguesser;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.epsi.i4.kitchenguesser.entities.Question;
import fr.epsi.i4.kitchenguesser.entities.Thing;
import fr.epsi.i4.kitchenguesser.entities.UserAnswer;


public class MainActivity extends ActionBarActivity {
    private static final int MIN_SCORE_TO_KEEP = -6;
    private static final int FIRST_QUESTION_TO_CLEAN = 3;
    private static final int MAX_QUESTIONS = 20;

    private SQLiteDatabase db;

    private HashMap<Integer, Question> questions;
    private ArrayList<Thing> things;
    private ArrayList<UserAnswer> currentGame;

    private Question currentQuestion;
    private TextView questionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDB();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeDB() {
        String dbPath = File.separator+"data"+File.separator+getPackageName()+File.separator+"databases"+File.separator+"KitchenGuesser.db";
        File appDB = new File(Environment.getDataDirectory(), dbPath);
        if (!appDB.exists()){
            try {
                InputStream src = this.getResources().openRawResource(R.raw.kitchenguesser);
                ReadableByteChannel rbc = Channels.newChannel(src);
                FileChannel dst = new FileOutputStream(appDB).getChannel();

                Utils.fastChannelCopy(rbc, dst);

                src.close();
                dst.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();
    }

    private void initializeGame() {
        currentGame = new ArrayList<>();
        things      = new ArrayList<>();
        questions   = new HashMap<>();

        things = (ArrayList) Thing.findAll(db);

        for (Question question : Question.findAll(db)){
            questions.put(question.getId(), question);
        }
    }

    private Question getRandomQuestion(){
        int randIndex = (int) (1+Math.random()*(questions.size()-2));
        return questions.get(randIndex);
    }

    private Question getBestQuestion() {
        int maxScore = -1;
        Question bestQuestion = null;

        for (Map.Entry<Integer, Question> entry : questions.entrySet()) {
            int questionScore = getScore(entry.getKey());

            if (maxScore < questionScore || maxScore == questionScore && Math.random() > 0.5f){
                maxScore = questionScore;
                bestQuestion = entry.getValue();
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
        ArrayList<Integer> keysToDelete = new ArrayList<>();

        for (int i = things.size()-1; i >= 0; i--) {
            if (things.get(i).getScore() <= MIN_SCORE_TO_KEEP){
                things.remove(i);
            }
        }
    }

    private void addAnswer(int answer) {
        currentGame.add(new UserAnswer(currentQuestion.getId(), answer));
        updateThingsScore(currentQuestion.getId(), answer);

        if (currentGame.size() >= FIRST_QUESTION_TO_CLEAN) {
            cleanThingsList();
        }

        questions.remove(currentQuestion.getId());

        float bestPrecision     = ((float) things.get(0).getScore()/(currentGame.size()*3))*100;
        float secondPrecision   = ((float) things.get(1).getScore()/(currentGame.size()*3))*100;

        if ((bestPrecision-20 > secondPrecision && currentGame.size() > 5) || currentGame.size() > MAX_QUESTIONS){
            List<Thing> bestThings = getThingsWithScore(things.get(0).getScore());

            if (bestThings.size() > 1){
                things = (ArrayList) bestThings;
                currentQuestion = getBestQuestion();
            } else {
                /*if (purposeAnswer(things.get(0), bestPrecision)){
                    addMissingAnswers(things.get(0).getDBObject(em).getId());
                    input = "q";
                } else {
                    learn(things.get(0));
                    input = "q";
                }*/
            }
        } else {
            currentQuestion = getBestQuestion();
            questionTextView.setText(currentQuestion.getQuestion());
        }

        Log.d("Things: ", things.toString());
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
}
