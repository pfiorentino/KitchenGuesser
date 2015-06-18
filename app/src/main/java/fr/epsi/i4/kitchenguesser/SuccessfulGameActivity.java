package fr.epsi.i4.kitchenguesser;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SuccessfulGameActivity extends ActionBarActivity {

    private Button yesButton;
    private Button noButton;
    private TextView finalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_game);

        Intent intent = getIntent();
        boolean ajoutInDB = intent.getBooleanExtra("ajoutInDB",false);
        yesButton = (Button) findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);
        finalText = (TextView) findViewById(R.id.finalText);

        if(ajoutInDB)
            finalText.setText("Op\u00e9ration correctement effectu\u00e9e !");

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(SuccessfulGameActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_successful_game, menu);
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
}
