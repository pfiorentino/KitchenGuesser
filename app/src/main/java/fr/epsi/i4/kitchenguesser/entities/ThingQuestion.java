package fr.epsi.i4.kitchenguesser.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 13/06/2015.
 */
public class ThingQuestion implements BaseColumns {

    private int id;
    private int thingId;
    private int questionId;
    private int value;

    public static final String TABLE_NAME = "things_questions";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_THING_ID = "thing_id";
    public static final String COLUMN_NAME_QUESTION_ID = "question_id";
    public static final String COLUMN_NAME_VALUE = "value";

    public ThingQuestion(int id, int thingId, int questionId, int value){
        this.id = id;
        this.thingId = thingId;
        this.questionId = questionId;
        this.value = value;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getValue() {
        return value;
    }

    public static List<ThingQuestion> findByThingId(int thingId, SQLiteDatabase db) {
        List<ThingQuestion> tqs = new ArrayList<ThingQuestion>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_THING_ID + " = " + thingId, null);
        if (cursor.moveToFirst()) {
            do {
                ThingQuestion tq = new ThingQuestion(
                        cursor.getInt( cursor.getColumnIndexOrThrow(COLUMN_NAME_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_THING_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_QUESTION_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NAME_VALUE)));

                tqs.add(tq);
            }
            while (cursor.moveToNext());
        }

        return tqs;
    }
}
