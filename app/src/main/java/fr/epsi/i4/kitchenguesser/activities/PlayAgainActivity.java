package fr.epsi.i4.kitchenguesser.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import fr.epsi.i4.kitchenguesser.R;


public class PlayAgainActivity extends ActionBarActivity {

    private Button yesButton;
    private Button noButton;
    private TextView finalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_again);
    }

    @Override
    public void onBackPressed() {
        AlertDialog dlg = null;

        if (!this.isFinishing()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.dialog_exit_game_title)
                    .setMessage(R.string.dialog_exit_game)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            noClick(null);
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

    public void yesClick(View v) {
        Intent intent = new Intent(PlayAgainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void noClick(View v) {
        finish();
        System.exit(0);
    }
}
