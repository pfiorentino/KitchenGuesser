package fr.epsi.i4.kitchenguesser.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    public void onBackPressed() {
        if (!rollBack()){
            AlertDialog dlg = null;

            if (!this.isFinishing()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle(R.string.dialog_exit_game_title)
                        .setMessage(R.string.dialog_exit_game)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.super.onBackPressed();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                dlg = builder.create();
                dlg.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null){
            mp.stop();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(mp != null){
            mp.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mp != null){
            mp.start();
        }
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
        intent.putExtra("thingId", thing.getId());
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
