package fr.epsi.i4.kitchenguesser;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.epsi.i4.kitchenguesser.entities.Thing;


public class ThingFoundActivity extends ActionBarActivity {


    private TextView thing;
    private Button yesAnswer;
    private Button noAnswer;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thing_found);

        thing = (TextView) findViewById(R.id.thing_found);
        yesAnswer = (Button) findViewById(R.id.YesAnswer);
        noAnswer = (Button) findViewById(R.id.NoAnswer);

        Intent intent = getIntent();
        int thingId = intent.getIntExtra("thingId",-1);

        System.out.println("Id de la thing : "+thingId);

        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        final Thing thingFound = Thing.findById(thingId,db);
        thing.setText(thingFound.getName());

        yesAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThingFoundActivity.this, SuccessfulGameActivity.class);
                startActivity(intent);
            }
        });

        noAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ThingFoundActivity.this,SelectionThingActivity.class);
                intent.putExtra("thingFoundName",thingFound.getName());
                startActivity(intent);
            }
        });


        Log.d("Coucou", thingFound.toString());

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
}
