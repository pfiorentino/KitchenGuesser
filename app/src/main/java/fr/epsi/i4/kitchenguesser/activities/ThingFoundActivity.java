package fr.epsi.i4.kitchenguesser.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.epsi.i4.kitchenguesser.classes.KitchenGuesserOpenHelper;
import fr.epsi.i4.kitchenguesser.R;
import fr.epsi.i4.kitchenguesser.classes.Game;
import fr.epsi.i4.kitchenguesser.classes.GameStep;
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

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                public void run() {
                    mp.start();
                }
            }, 1000);
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

        final Thing thingFound = Thing.findById(thingId,db);
        thing.setText(Utils.ucfirst(thingFound.getName()));

        yesAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMissingAnswers(thingFound);

                Intent intent = new Intent(ThingFoundActivity.this, PlayAgainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_thing_found, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        db.close();
    }

    private void addMissingAnswers(Thing thing) {
        for (GameStep answer : Game.getInstance().getCurrentGame()){
            ThingQuestion tq = new ThingQuestion(0, thing.getId(), answer.getQuestionId(), answer.getValue());
            ThingQuestion.addThingQuestion(tq, db);
            //select * from things_questions where thing_id = 3;
        }
    }
}