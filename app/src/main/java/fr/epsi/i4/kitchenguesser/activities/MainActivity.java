package fr.epsi.i4.kitchenguesser.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.epsi.i4.kitchenguesser.R;
import fr.epsi.i4.kitchenguesser.classes.Game;
import fr.epsi.i4.kitchenguesser.entities.Question;
import fr.epsi.i4.kitchenguesser.entities.Thing;


public class MainActivity extends ActionBarActivity {
    private Question currentQuestion;
    private TextView questionTextView;

    public MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Game.getInstance().init(this.getApplicationContext());

        questionTextView = (TextView) findViewById(R.id.questionTextView);
        setCurrentQuestion(Game.getInstance().getRandomQuestion());

        mp = MediaPlayer.create(MainActivity.this,R.raw.ingame_music);

        if (mp != null){
            mp.setLooping(true);
            mp.setVolume(100, 100);
            mp.start();
        }

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
    public void onBackPressed() {
        if (!rollBack()){
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void addAnswer(int answer) {
        Thing thingFound = Game.getInstance().addAnswer(currentQuestion, answer);

        if (thingFound == null) {
            setCurrentQuestion(Game.getInstance().getBestQuestion());
        } else {
            thingFound(thingFound);
        }
    }

    private void thingFound(Thing thing){
        Intent intent = new Intent(this, ThingFoundActivity.class);
        intent.putExtra("thingId",thing.getId());
        if(mp != null)
            mp.stop();
        startActivity(intent);
    }

    private boolean rollBack() {
        Log.d("RollBack", "On roll back (enfin on essaye)");

        Question lastQuestion = Game.getInstance().rollBack();

        if (lastQuestion != null){
            setCurrentQuestion(lastQuestion);
            return true;
        }

        return false;
    }

    private void setCurrentQuestion(Question question) {
        this.currentQuestion = question;
        questionTextView.setText(this.currentQuestion.getQuestion());
    }
}
