package ca.xiaowei.xiaowei;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.text.DecimalFormat;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    RadioGroup sortRadioGroup;
    Button backBtn;
    Spinner spinnerBtn;
    TextView questionText, userAnswerText, actualAnswerText, scoreText;
    EditText registerText;
    ArrayList<QuestionModel> listOfQuestions;
    private String selectedFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initialize();
    }

    private void initialize() {
        sortRadioGroup = findViewById(R.id.sortRadioGroup);
        sortRadioGroup.setOnCheckedChangeListener(this);
        backBtn = findViewById(R.id.result_backBtn);
        backBtn.setOnClickListener(this);
        spinnerBtn = findViewById(R.id.spinner_choose);
        spinnerBtn.setOnItemSelectedListener(this);

        questionText = findViewById(R.id.result_questions);
        userAnswerText = findViewById(R.id.result_yourAnswer);
        actualAnswerText = findViewById(R.id.result_correctAnswer);
        scoreText = findViewById(R.id.result_score);
        registerText = findViewById(R.id.result_inputName);

        Intent intent = getIntent();
        double percentage = intent.getDoubleExtra("percentage", 0);

        String percentageText = String.format("%.2f", percentage);
        scoreText.setText(percentageText + "%");

        Bundle bundle = intent.getBundleExtra("intentListOfQuestions");
        listOfQuestions = (ArrayList<QuestionModel>) bundle.getSerializable("bundleListOfQuestions");

        selectedFilter = "All";
        displayQuestionDetails();
        spinnerBtn.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.result_backBtn) {
            backToMain();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.sortByCorrect) {
            sortByCorrectAnswer();
        } else if (checkedId == R.id.sortByFault) {
            sortByIncorrectAnswer();
        }
    }

    private void sortByCorrectAnswer() {
        Collections.sort(listOfQuestions);
        displayQuestionDetails();
    }

    private void sortByIncorrectAnswer() {
        Collections.sort(listOfQuestions, Collections.reverseOrder());
        displayQuestionDetails();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getSelectedItem().toString();
        selectedFilter = selectedItem;
        displayQuestionDetails();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

private void displayQuestionDetails() {
    StringBuilder questions = new StringBuilder();
    StringBuilder userAnswers = new StringBuilder();
    StringBuilder actualAnswers = new StringBuilder();

    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    if (selectedFilter.equals("All")) {
        for (QuestionModel question : listOfQuestions) {
            questions.append(question.getQuestion()).append("\n");
            userAnswers.append(question.getUserAnswer()).append("\n");
            double answer = question.getAnswer();
            actualAnswers.append(decimalFormat.format(answer)).append("\n");
        }
    } else if (selectedFilter.equals("Right")) {
        for (QuestionModel question : listOfQuestions) {
            boolean isCorrect = question.validateAnswer(question.getUserAnswer());
            if (isCorrect) {
                questions.append(question.getQuestion()).append("\n");
                userAnswers.append(question.getUserAnswer()).append("\n");
                double answer = question.getAnswer();
                actualAnswers.append(decimalFormat.format(answer)).append("\n");
            }
        }
    } else if (selectedFilter.equals("Wrong")) {
        for (QuestionModel question : listOfQuestions) {
            boolean isCorrect = question.validateAnswer(question.getUserAnswer());
            if (!isCorrect) {
                questions.append(question.getQuestion()).append("\n");
                userAnswers.append(question.getUserAnswer()).append("\n");
                double answer = question.getAnswer();
                actualAnswers.append(decimalFormat.format(answer)).append("\n");
            }
        }
    }

    questionText.setText(questions.toString());
    userAnswerText.setText(userAnswers.toString());
    actualAnswerText.setText(actualAnswers.toString());
}

    private void backToMain() {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.putExtra("listOfQuestions", listOfQuestions);
        intent.putExtra("intentRegister",registerText.getText().toString());
        setResult(Activity.RESULT_OK,intent);
        finish();
    }
}