package fr.epsi.i4.kitchenguesser.entities;

/**
 * Created by paul on 13/06/2015.
 */
public class UserAnswer {
    private int questionId;
    private int answer;

    public UserAnswer(int questionId, int answer){
        this.questionId = questionId;
        this.answer     = answer;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public int getValue() {
        return this.answer;
    }

    @Override
    public String toString() {
        return "Question "+questionId+": "+answer;
    }
}