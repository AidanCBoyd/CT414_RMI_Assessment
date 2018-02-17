package ct414;

/**
 * Implementation of the assessment interface so that extra methods can be added
 */


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExamPaper implements Assessment {
    private Date closeDate;
    private List<Question> questions = new ArrayList<>();
    private String info;
    private int studentid;

    public ExamPaper(String closeDate,String info,int studentid){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            this.closeDate = df.parse(closeDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.info=info;
        this.studentid=studentid;
    }
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
    public void addQuestion(ExamQuestion q){
        this.questions.add(q);
    }


    @Override
    public Question getQuestion(int questionNumber) throws InvalidQuestionNumber {
        if (questionNumber<0){
            throw new InvalidQuestionNumber("Can't have negative question number");

        }
        else if (questionNumber>questions.size()){
            throw new InvalidQuestionNumber("Question number is larger than the number of questions");
        }
        else{
            for (Question q : questions){
                if(q.getQuestionNumber()==questionNumber){
                    return  q;
                }
            }

        }
        return  null;
    }

    @Override
    public void selectAnswer(int questionNumber, int optionNumber) throws InvalidQuestionNumber, InvalidOptionNumber {
        if (questionNumber<0){
            throw new InvalidQuestionNumber("Can't have negative question number");

        }
        else if (questionNumber>questions.size()){
            throw new InvalidQuestionNumber("Question number is larger than the number of questions");
        }
        ExamQuestion question;
        for (Question q : questions){
            if(q.getQuestionNumber()==questionNumber){
                question = (ExamQuestion) questions.get((questionNumber-1));
                question.setSelect(optionNumber);
            }
        }


    }

    @Override
    public int getSelectedAnswer(int questionNumber) {
        ExamQuestion question;
        for (Question q : questions){
            if(q.getQuestionNumber()==questionNumber){
                question = (ExamQuestion) questions.get(questionNumber);
                return question.getSelected();

            }
        }
        return -1;
    }

    @Override
    public int getAssociatedID() {
        return this.studentid;
    }
}
