package ca.xiaowei.xiaowei;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

public class QuestionModel implements Serializable,Comparable<QuestionModel>{
    private String question;
    private double answer;
    private boolean isRounded;
    private int score;
    private int answeredQuestionCount;
    private  boolean isAnswered;
    private double userAnswer;

    public QuestionModel() {
        score = 0;
        answeredQuestionCount = 0 ;
        isAnswered = false;
        isRounded = false;
    }

    public String getQuestion() {
        return question;
    }

    public boolean validateAnswer(double userAnswer) {
        boolean isCorrect;
        if(isRounded)
            isCorrect = (userAnswer >= answer - 0.001 && userAnswer < answer + 0.001);
        else
            isCorrect = (userAnswer == answer);

        if (isCorrect) {
            score++;
        }
        if (!isAnswered) {
            answeredQuestionCount++;
            isAnswered = true;
        }
        return isCorrect;
    }

    public int totalQuestionCount(){
       return answeredQuestionCount;
    }
    public boolean isQuestionAnswered() {
        return isAnswered;
    }
    public double getAnswer() {
        return answer;
    }

    public int getScore() {
        return score;
    }

    public double getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(double userAnswer) {
        this.userAnswer = userAnswer;
    }

    public void generateNewQuestion() {
        int number1 = getRandomNumber();
        int number2 = getRandomNumber();
         String operator = getRandomOperator();

        // Check if it's a division operation with the second number as zero
        if (operator.equals("/") && number2 == 0) {
            question = "Invalid operation";
            answer = 0;
            return;
        }else{
            question = number1 + " " + operator + " " + number2;
            answer = calculateAnswer(number1, number2, operator);
        }
       isAnswered = false;
    }

    private int getRandomNumber() {
        Random random = new Random();
        return random.nextInt(11);
    }

    private String getRandomOperator() {
        String[] operators = {"+", "-", "*", "/"};
        Random random = new Random();
        int index = random.nextInt(operators.length);
        return operators[index];
    }

    private double calculateAnswer(int number1, int number2, String operator) {
        switch (operator) {
            case "+":
                return number1 + number2;
            case "-":
                return number1 - number2;
            case "*":
                return number1 * number2;
            case "/":
                if (number2 != 0 ) {
                    double divisionResult = (double) number1 / number2;
                    if ( number1 % number2 != 0) {
                        isRounded = true;
                    }
                    return divisionResult;
                }
            default:
                return 0;
        }
    }

    @Override
    public int compareTo(QuestionModel o) {
        boolean thisCorrect = validateAnswer(this.getUserAnswer());
        boolean otherCorrect = o.validateAnswer(o.getUserAnswer());

        if (thisCorrect == otherCorrect) {
            // If both have the same correctness, compare based on user answer
            if (this.getUserAnswer() == o.getUserAnswer()) {
                return 0;
            } else if (this.getUserAnswer() < o.getUserAnswer()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            // If correctness is different, sort incorrect answers first
            return thisCorrect ? -1 : 1;
        }
    }

}
