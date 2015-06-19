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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import fr.epsi.i4.kitchenguesser.entities.Question;
import fr.epsi.i4.kitchenguesser.entities.Thing;
import fr.epsi.i4.kitchenguesser.entities.ThingQuestion;


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
        titleFieldName = (TextView) findViewById(R.id.add_thing_title);
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
            question.setText("Quelle question permet de différencier votre objet d'un(e) \"" + thingFoundName + "\" ?");
        } else {
            objectName.setVisibility(View.GONE);
            titleFieldName.setVisibility(View.GONE);
            spaceNameThing_Question.setVisibility(View.GONE);
            String questionString = "Quelle question permet de différencier un(e) \""+nameThingGrab+"\" d'un(e) \""+thingFoundName+"\" ?";
            question.setText(questionString);
        }

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int answer;
                int answerThingFound;
                int checkedId = radioGroup.getCheckedRadioButtonId();

                if(radioYes.getId() == checkedId){
                    answer = 1;
                    answerThingFound = 5;
                } else {
                    answer = 5;
                    answerThingFound = 1;
                }

                if(nameThingGrab.equals("")){
                    addThingAndQuestion(thingFoundName,objectName.getText().toString(),customerQuestion.getText().toString(),answer,answerThingFound);
                } else {
                    addQuestion(thingFoundName,nameThingGrab,customerQuestion.getText().toString(),answer,answerThingFound);
                }

                Intent intent = new Intent(AddNewThingActivity.this, PlayAgainActivity.class);
                Toast toast = Toast.makeText(getApplicationContext(), "Base de donnée mise à jour :) ", Toast.LENGTH_LONG);
                toast.show();

                startActivity(intent);
                finish();
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

    public void addQuestion(String thingFoundName, String thingName, String question, int answer, int answerThingFound){
        Question questionAdded = new Question(0,"",question);
        Question.addQuestion(questionAdded, db);
        Question questionObject = Question.findByTitle(question, db);

        Thing thingGiven = Thing.findByName(thingName, db);
        Thing thingFound = Thing.findByName(thingFoundName, db);

        ThingQuestion thingQuestionForThingGiven = new ThingQuestion(0,thingGiven.getId(),questionObject.getId(),answer);
        ThingQuestion.addThingQuestion(thingQuestionForThingGiven, db);

        ThingQuestion thingQuestionForThingFound = new ThingQuestion(0,thingFound.getId(),questionObject.getId(),answerThingFound);
        ThingQuestion.addThingQuestion(thingQuestionForThingFound,db);
    }

    public void addThingAndQuestion(String thingFoundName, String thingName, String question, int answer, int answerThingFound){
        Thing thing = new Thing(0,thingName);
        Thing.addThing(thing,db);
        addQuestion(thingFoundName, thingName, question, answer, answerThingFound);
    }
}
