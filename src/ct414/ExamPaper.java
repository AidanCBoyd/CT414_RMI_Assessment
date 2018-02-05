package ct414;

import a.i.Q;

import java.util.Date;
import java.util.List;

public class ExamPaper implements Assessment {
    private Date closeDate;
    private List<Question> questions;
    private String info;
    @Override
    public String getInformation() {
        return this.info;
    }

    @Override
    public Date getClosingDate() {
        return this.closeDate;
    }

    @Override
    public List<Question> getQuestions() {
        return this.questions;
    }

    @Override
    public Question getQuestion(int questionNumber) throws InvalidQuestionNumber {
        return this.questions.get(questionNumber-1);
    }

    @Override
    public void selectAnswer(int questionNumber, int optionNumber) throws InvalidQuestionNumber, InvalidOptionNumber {

    }

    @Override
    public int getSelectedAnswer(int questionNumber) {
        return 0;
    }

    @Override
    public int getAssociatedID() {
        return 0;
    }
}
