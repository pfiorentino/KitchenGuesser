package fr.epsi.i4.kitchenguesser;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Space;
import android.widget.TextView;


public class AddNewThingActivity extends ActionBarActivity {

    private TextView question;
    private EditText objectName;
    private TextView titleFieldName;
    private android.support.v4.widget.Space spaceNameThing_Question;
    private EditText customerQuestion;
    private Button validation;

    private RadioGroup radioGroup;
    private RadioButton radioYes;
    private RadioButton radioNo;

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_thing);

        question = (TextView) findViewById(R.id.question);
        objectName = (EditText) findViewById(R.id.objectName);
        titleFieldName = (TextView) findViewById(R.id.intituléChampNom);
        spaceNameThing_Question = (android.support.v4.widget.Space) findViewById(R.id.spaceNameThing_Question);
        validation = (Button) findViewById(R.id.validation);
        customerQuestion = (EditText) findViewById(R.id.customerQuestion);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioYes = (RadioButton) findViewById(R.id.radioYes);
        radioNo = (RadioButton) findViewById(R.id.radioNo);
        radioYes.setChecked(true);

        KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        Intent intent = getIntent();
        final String nameThingGrab = intent.getStringExtra("name");
        final String thingFoundName = intent.getStringExtra("thingFoundName");


        if(nameThingGrab.equals("")){
            question.setText("Quelle question doit-on associer à \"\" pour le/la différencier de \""+thingFoundName+"\" ?");
            objectName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    question.setText("Quelle question doit-on associer à \""+s+"\" pour le/la différencier de \""+thingFoundName+"\" ?");

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        else{
            objectName.setVisibility(View.GONE);
            titleFieldName.setVisibility(View.GONE);
            spaceNameThing_Question.setVisibility(View.GONE);
            String questionString = "Quelle question doit-on associer à \""+nameThingGrab+"\" pour le/la différencier de \""+thingFoundName+"\" ?";
            question.setText(questionString);
        }

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer;
                int checkedId = radioGroup.getCheckedRadioButtonId();
                if(radioYes.getId() == checkedId){
                    answer = "Oui";
                }
                else{
                    answer = "Non";
                }

                if(nameThingGrab.equals("")){
                    addThingAndQuestion(objectName.getText().toString(),customerQuestion.getText().toString(),answer);
                }
                else{
                    addQuestion(objectName.getText().toString(),customerQuestion.getText().toString(),answer);
                }

                Intent intent = new Intent(AddNewThingActivity.this, SuccessfulGameActivity.class);
                intent.putExtra("ajoutInDB", true);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_thing, menu);
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

    public void addQuestion(String thingName, String question, String answer){
        Log.d("feedback reponse",answer);
    }

    public void addThingAndQuestion(String thingName, String question, String answer){

        addQuestion(thingName,question,answer);
    }
}
