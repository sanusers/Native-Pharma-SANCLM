package saneforce.sanzen.activity.Quiz.model;

public class QuizQuesNoModel {
    private final int questionNumber;
    private boolean isSelected = false;
    private boolean isFinished = false;

    public QuizQuesNoModel(int questionNumber, boolean isSelected, boolean isFinished) {
        this.questionNumber = questionNumber;
        this.isSelected = isSelected;
        this.isFinished = isFinished;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }
}
