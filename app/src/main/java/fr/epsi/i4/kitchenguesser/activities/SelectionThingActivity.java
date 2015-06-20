package fr.epsi.i4.kitchenguesser.activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.epsi.i4.kitchenguesser.adapters.CustomAdapter;
import fr.epsi.i4.kitchenguesser.classes.KitchenGuesserOpenHelper;
import fr.epsi.i4.kitchenguesser.R;
import fr.epsi.i4.kitchenguesser.entities.Thing;


public class SelectionThingActivity extends ActionBarActivity {

    private EditText searchField;
    private ListView listThings;
    private Button buttonAddThing;
    private SQLiteDatabase db;
    private Context context;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_thing);

        searchField = (EditText) findViewById(R.id.searchField);
        listThings = (ListView) findViewById(R.id.listThings);
        buttonAddThing = (Button) findViewById(R.id.buttonAddThing);

        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        mp = MediaPlayer.create(SelectionThingActivity.this,R.raw.oh_non);

        if(mp != null) {
            mp.setVolume(100, 100);
            mp.start();
        }

        this.context = this;
        final Intent thingFoundIntent = getIntent();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Thing> list;
                final ArrayList<String> listNames = new ArrayList<String>();
                //System.out.println("Liste de things vide ? : "+ list.isEmpty());
                if (s.length() != 0) {
                    list = Thing.findByString(db, s.toString());
                    if (!list.isEmpty()) {
                        for (Thing t : list) {
                            listNames.add(t.getName());
                        }
                    } else {
                        listNames.add("Pas d'objet trouv\u00e9...");
                    }
                }

                int resource = android.R.layout.simple_list_item_1;


                CustomAdapter listAdapter = new CustomAdapter(context, resource, listNames);

                listThings.setAdapter(listAdapter);

                listThings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Intent intent = new Intent(SelectionThingActivity.this,AddNewThingActivity.class);
                        String thingName = listNames.get(position);

                        intent.putExtra("name",thingName);
                        intent.putExtra("thingFoundName",thingFoundIntent.getStringExtra("thingFoundName"));

                        startActivity(intent);
                    }
                });

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonAddThing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionThingActivity.this, AddNewThingActivity.class);
                intent.putExtra("name","");
                intent.putExtra("searchString", searchField.getText().toString());
                intent.putExtra("thingFoundName", thingFoundIntent.getStringExtra("thingFoundName"));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selection_thing, menu);
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
}
