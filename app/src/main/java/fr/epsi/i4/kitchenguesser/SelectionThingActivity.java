package fr.epsi.i4.kitchenguesser;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.epsi.i4.kitchenguesser.entities.Thing;


public class SelectionThingActivity extends ActionBarActivity {

    private EditText searchField;
    private ListView listThings;
    private Button buttonYes;
    private Button buttonNo;
    private SQLiteDatabase db;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_thing);

        searchField = (EditText) findViewById(R.id.searchField);
        listThings = (ListView) findViewById(R.id.listThings);
        buttonYes = (Button) findViewById(R.id.button);
        buttonNo = (Button) findViewById(R.id.button2);

        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        this.context = this;

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
                            System.out.println(t.getName());
                            listNames.add(t.getName());
                        }
                    } else {
                        listNames.add("\'Ajouter un nouvel objet...\'");
                    }
                }
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listNames);
                listThings.setAdapter(listAdapter);
                listThings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        Intent intent = new Intent(SelectionThingActivity.this,AddNewThingActivity.class);
                        String thingName = listNames.get(position);
                        System.out.println(thingName);
                      //  String playerChanged = c.getText().toString();

                       // Toast.makeText(Settings.this, playerChanged, Toast.LENGTH_SHORT).show();
                        intent.putExtra("name",thingName);
                        startActivity(intent);
                    }
                });

                listAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
