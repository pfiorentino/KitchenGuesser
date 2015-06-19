package fr.epsi.i4.kitchenguesser.entities;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by paul on 13/06/2015.
 */
public class Thing implements BaseColumns, Comparable<Thing> {
    private final int[][] heuristic = {{3,3,0,-2,-3},{2,3,0,-1,-2},{0,0,0,0,0},{-2,-1,0,3,2},{-3,-2,0,3,3}};

    private int id;
    private String name;
    private HashMap<Integer, Integer> answers;
    private int score;

    public static final String TABLE_NAME = "things";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_NAME = "name";

    public Thing(int id, String name){
        this.id         = id;
        this.name       = name;
        this.score      = 0;
        this.answers    = new HashMap<>();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void addAnswer(int questionId, int answer) {
        this.answers.put(questionId, answer);
    }

    public int getAnswer(int questionId){
        if (this.answers.containsKey(questionId)){
            return this.answers.get(questionId);
        } else {
            return 0;
        }
    }

    public int getScore() {
        return this.score;
    }

    public void updateScore(int questionId, int givenAnswer){
        int expectedAnswer = getAnswer(questionId);

        if (expectedAnswer > 0)
            score += heuristic[givenAnswer-1][expectedAnswer-1];
    }

    @Override
    public String toString(){
        String message = "";
        message += this.name+"("+this.score+")\n";
        for (Map.Entry<Integer, Integer> entry : this.answers.entrySet()) {
            message += "Key map : "+entry.getKey()+"\t - \t"+"Value map : "+entry.getValue()+"\n";
        }
        return message;
    }

    @Override
    public int compareTo(Thing o) {
        return o.score - this.score;
    }

    public static List<Thing> findAll(SQLiteDatabase db) {
        List<Thing> things = new ArrayList<Thing>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Thing thing = new Thing(
                        cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME)));

                List<ThingQuestion> thingAnswers = ThingQuestion.findByThingId(thing.getId(), db);
                for (ThingQuestion answer : thingAnswers){
                    thing.addAnswer(answer.getQuestionId(), answer.getValue());
                }

                things.add(thing);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return things;
    }

    public static List<Thing> findByString(SQLiteDatabase db, String name) {
        List<Thing> things = new ArrayList<Thing>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+COLUMN_NAME_NAME+" LIKE \"%"+name+"%\"", null);
        if (cursor.moveToFirst()) {
            do {
                Thing thing = new Thing(
                        cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME)));

                List<ThingQuestion> thingAnswers = ThingQuestion.findByThingId(thing.getId(), db);
                for (ThingQuestion answer : thingAnswers){
                    thing.addAnswer(answer.getQuestionId(), answer.getValue());
                }

                things.add(thing);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return things;
    }

    public static Thing findById(int id, SQLiteDatabase db) {
        Thing thing = null;

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME+" WHERE "+COLUMN_NAME_ID+" ="+id, null);
        if (cursor.moveToFirst()) {
            do {
                thing = new Thing(
                        cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME)));

                List<ThingQuestion> thingAnswers = ThingQuestion.findByThingId(thing.getId(), db);
                for (ThingQuestion answer : thingAnswers){
                    thing.addAnswer(answer.getQuestionId(), answer.getValue());
                }
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return thing;
    }

    public static Thing findByName(String name, SQLiteDatabase db) {
        Thing thing = null;
        String query = "SELECT * FROM " + TABLE_NAME+" WHERE "+COLUMN_NAME_NAME+" = \""+name+"\"";
        Cursor cursor = db.rawQuery(query, null);
        Log.d("thing findByName : ",query);
        if (cursor.moveToFirst()) {
            thing = new Thing(
                    cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_NAME)));

            List<ThingQuestion> thingAnswers = ThingQuestion.findByThingId(thing.getId(), db);
            for (ThingQuestion answer : thingAnswers){
                thing.addAnswer(answer.getQuestionId(), answer.getValue());
            }
        }
        cursor.close();
        return thing;
    }

    public static void addThing(Thing thing, SQLiteDatabase db){
        if (thing.getId() == 0){
            int id = 0;
            String query = "SELECT MAX(id) as max FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }
            thing.setId(id+1);
        }

        ContentValues newValues = new ContentValues();
        newValues.put(COLUMN_NAME_ID, thing.getId());
        newValues.put(COLUMN_NAME_NAME, thing.getName());
        db.insert(TABLE_NAME, null, newValues);
    }
}
