package ct414;

/**
 * Created by Aidan Boyd on 05/02/18.
 */
public class ExamQuestion implements Question{
    private int num;
    private String question;
    private String[] ans;
    private int correct;
    private int select;
    public ExamQuestion(int number, String q, String[] answers,int correct) {
        this.num = number;
        this.question = q;
        this.ans = answers;
        this.correct=correct;
    }

    public void setAns(String[] ans) {
        this.ans = ans;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public void setCorrectAnswerIndex (int i) {this.correct=i;}

    public void setNum(int num) {
        this.num = num;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public int getQuestionNumber() {
        return this.num;
    }

    @Override
    public String getQuestionDetail() {
        return this.question;
    }

    @Override
    public String[] getAnswerOptions() {
        return this.ans;
    }

    public int getSelected() {
        return select;
    }
}
