package fr.epsi.i4.kitchenguesser;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class SplashScreenActivity extends Activity {
    private RelativeLayout accueil = null;

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
            }
        });

        initializeDB();
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
}
