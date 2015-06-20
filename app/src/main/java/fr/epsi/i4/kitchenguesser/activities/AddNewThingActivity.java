package fr.epsi.i4.kitchenguesser.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

import fr.epsi.i4.kitchenguesser.classes.KitchenGuesserOpenHelper;
import fr.epsi.i4.kitchenguesser.R;
import fr.epsi.i4.kitchenguesser.entities.Question;
import fr.epsi.i4.kitchenguesser.entities.Thing;
import fr.epsi.i4.kitchenguesser.entities.ThingQuestion;


public class AddNewThingActivity extends ActionBarActivity {

    private TextView question;
    private EditText objectName;
    private TextView titleFieldName;
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
        final String searchString = intent.getStringExtra("searchString");
        final String thingFoundName = intent.getStringExtra("thingFoundName");

        objectName.setText(searchString);

        if(nameThingGrab.equals("")){
            findViewById(R.id.thing_name_text_view).setVisibility(View.GONE);
            question.setText("Qu'est-ce qui différencie votre objet d'un(e) \"" + thingFoundName + "\" ?");
        } else {
            titleFieldName.setText("Ajouter une question");
            objectName.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.thing_name_text_view)).setText(nameThingGrab);
            String questionString = "Qu'est-ce qui différencie un(e) \""+nameThingGrab+"\" d'un(e) \""+thingFoundName+"\" ?";
            question.setText(questionString);
        }

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValid = false;

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
                    if (!objectName.getText().toString().equals("") && !customerQuestion.getText().toString().equals("")) {
                        addThingAndQuestion(thingFoundName, objectName.getText().toString(), customerQuestion.getText().toString(), answer, answerThingFound);
                        isValid = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Veuillez saisir un nom d'objet et une question.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (!customerQuestion.getText().toString().equals("")) {
                        addQuestion(thingFoundName, nameThingGrab, customerQuestion.getText().toString(), answer, answerThingFound);
                        isValid = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "Veuillez saisir une question.", Toast.LENGTH_LONG).show();
                    }
                }

                if (isValid) {
                    Toast.makeText(getApplicationContext(), "Base de donnée mise à jour :) ", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), PlayAgainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Log.d("error", "Hhhmmmm flag isn't set to true...");
                }
            }
        });
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
