package fr.epsi.i4.kitchenguesser;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Jonathan on 19/06/2015.
 */
public class CustomAdapter extends ArrayAdapter<String>{

    private List<String> listString;

    public CustomAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.listString = objects;
    }

    @Override
    public boolean isEnabled(int position) {
        if(listString.get(0).equals("Pas d'objet trouv\u00e9...")){
            return false;
        }
        else {
            return super.isEnabled(position);
        }
    }


}
