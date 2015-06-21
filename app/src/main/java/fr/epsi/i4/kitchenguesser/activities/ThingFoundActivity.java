package fr.epsi.i4.kitchenguesser.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.epsi.i4.kitchenguesser.R;
import fr.epsi.i4.kitchenguesser.classes.Game;
import fr.epsi.i4.kitchenguesser.classes.GameStep;
import fr.epsi.i4.kitchenguesser.classes.KitchenGuesserOpenHelper;
import fr.epsi.i4.kitchenguesser.classes.Utils;
import fr.epsi.i4.kitchenguesser.entities.Thing;
import fr.epsi.i4.kitchenguesser.entities.ThingQuestion;


public class ThingFoundActivity extends ActionBarActivity {
    private TextView thing;
    private ImageView thingPicture;
    private Button yesAnswer;
    private Button noAnswer;
    private SQLiteDatabase db;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_found);

        mp = MediaPlayer.create(ThingFoundActivity.this,R.raw.success_music);

        if(mp != null){
            mp.setVolume(100, 100);
            mp.start();
        }

        thing = (TextView) findViewById(R.id.thing_found);
        thingPicture = (ImageView) findViewById(R.id.thing_picture);
        yesAnswer = (Button) findViewById(R.id.YesAnswer);
        noAnswer = (Button) findViewById(R.id.NoAnswer);

        Intent intent = getIntent();
        int thingId = intent.getIntExtra("thingId",-1);
        thingPicture.setImageResource(getResources().getIdentifier("thing_"+thingId, "drawable", getPackageName()));

        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        final Thing thingFound = Thing.findById(thingId, db);
        thing.setText(Utils.ucfirst(thingFound.getName()));

        yesAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMissingAnswers(thingFound);

                Intent intent = new Intent(ThingFoundActivity.this, PlayAgainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        noAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThingFoundActivity.this,SelectionThingActivity.class);
                intent.putExtra("thingFoundName", thingFound.getName());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp != null){
            mp.stop();
        }
        db.close();
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

    private void addMissingAnswers(Thing thing) {
        for (GameStep step : Game.getInstance().getCurrentGame()){
            ThingQuestion tq = new ThingQuestion(0, thing.getId(), step.getQuestionId(), step.getAnswer());
            ThingQuestion.addThingQuestion(tq, db);
        }
    }
}
