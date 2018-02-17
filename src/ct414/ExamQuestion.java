package ct414;

/**
 * Implementation of the question interface to allow for extra methods to be added
 *
 * @author Aidan Boyd 14521303
 * @author Daire Canavan 14439308
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

    public void setSelect(int select) {
        this.select = select;
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

    public String getCorrectAnswer() {
        return ans[correct];
    }

    public String getSelectedAnswer() {
        return ans[select];
    }
}
