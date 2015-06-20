package fr.epsi.i4.kitchenguesser.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import fr.epsi.i4.kitchenguesser.classes.KitchenGuesserOpenHelper;
import fr.epsi.i4.kitchenguesser.R;
import fr.epsi.i4.kitchenguesser.classes.Utils;

public class SplashScreenActivity extends Activity {
    private RelativeLayout accueil = null;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        accueil = (RelativeLayout) this.findViewById(R.id.splashScreen);
        accueil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                finish();
                startActivity(intent);

                if(mp != null){
                    mp.stop();
                }
            }
        });

        initializeDB();

        startBlink();

        mp = MediaPlayer.create(this,R.raw.splashscreen_music);
        if(mp != null) {
            mp.setLooping(true); // Set looping
            mp.setVolume(100, 100);
            mp.start();
        }
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

    private void initializeDB() {
        String appDataPath = Environment.getDataDirectory()+File.separator+"data"+File.separator+getPackageName();
        File dbExistsFile = new File(appDataPath+File.separator+"database_imported");
        if (!dbExistsFile.exists()){
            Log.d("import", "On importe la base !");

            // Pour créer la base si elle n'existe pas encore
            KitchenGuesserOpenHelper mDbHelper = new KitchenGuesserOpenHelper(this.getApplicationContext());
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            db.close();

            String dbPath = appDataPath+File.separator+"databases"+File.separator+"KitchenGuesser.db";
            File appDB = new File(dbPath);

            try {
                InputStream src = this.getResources().openRawResource(R.raw.kitchenguesser);
                ReadableByteChannel rbc = Channels.newChannel(src);
                FileChannel dst = new FileOutputStream(appDB).getChannel();

                Utils.fastChannelCopy(rbc, dst);

                src.close();
                dst.close();

                dbExistsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("import", "On n'importe PAS la base !");
        }
    }

    private void startBlink(){
        TextView myText = (TextView) findViewById(R.id.touch_to_begin_text_view);

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        myText.startAnimation(anim);
    }


}
