package fr.epsi.i4.kitchenguesser.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 13/06/2015.
 */
public class Question implements BaseColumns {
    private int id;
    private String keyword;
    private String question;

    public static final String TABLE_NAME = "questions";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_KEYWORD = "keyword";
    public static final String COLUMN_NAME_QUESTION = "question";

    public Question(int id, String keyword, String question){
        this.id = id;
        this.keyword = keyword;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getKeyword(){
        return this.keyword;
    }

    public String toString() {
        return "Question "+id+": "+question+" ("+keyword+")";
    }

    public static List<Question> findAll(SQLiteDatabase db) {
        List<Question> questions = new ArrayList<Question>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                Question question = new Question(
                        cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_KEYWORD)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION)));

                questions.add(question);
            }
            while (cursor.moveToNext());
        }
        return questions;
    }

    public static Question findByTitle(String title, SQLiteDatabase db) {
        Question question = null;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE "+COLUMN_NAME_QUESTION+" = \""+title+"\"";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            question = new Question(
                    cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_KEYWORD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION)));
        }
        return question;
    }

    public static void addQuestion(Question question, SQLiteDatabase db){
        if (question.getId() == 0){
            int id = 0;
            String query = "SELECT MAX(id) as max FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }
            question.setId(id+1);
        }

        ContentValues newValues = new ContentValues();
        newValues.put(COLUMN_NAME_ID, question.getId());
        newValues.put(COLUMN_NAME_KEYWORD,"user_defined");
        newValues.put(COLUMN_NAME_QUESTION,question.getQuestion());
        db.insert(TABLE_NAME, null, newValues);
    }
}
