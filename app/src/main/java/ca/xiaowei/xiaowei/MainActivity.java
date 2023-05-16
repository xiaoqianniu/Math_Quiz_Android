package ca.xiaowei.xiaowei;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button dotBtn, minusBtn, generateBtn, validateBtn, clearBtn, scoreBtn, finishBtn, removeBtn;
    int buttonCount = 10;
    Button[] numberButtons = new Button[buttonCount];
    TextView operationsTextView, answerCheckTextView;
    EditText userAnswerText;
    ArrayList<QuestionModel> listOfQuestions = new ArrayList<>();
    QuestionModel questionModel;
    ActivityResultLauncher<Intent> infoFromResultActivity;
    private Chronometer chronometer;
    private long offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    public void initialize() {

        for (int i = 0; i < buttonCount; i++) {
            int buttonId = getResources().getIdentifier("button" + i, "id", getPackageName());
            numberButtons[i] = findViewById(buttonId);
            numberButtons[i].setOnClickListener(this);
        }

        dotBtn = findViewById(R.id.dot_button);
        dotBtn.setOnClickListener(this);
        minusBtn = findViewById(R.id.minus_button);
        minusBtn.setOnClickListener(this);
        generateBtn = findViewById(R.id.generate_button);
        generateBtn.setOnClickListener(this);
        validateBtn = findViewById(R.id.validate_button);
        validateBtn.setOnClickListener(this);
        clearBtn = findViewById(R.id.clear_button);
        clearBtn.setOnClickListener(this);
        scoreBtn = findViewById(R.id.score_button);
        scoreBtn.setOnClickListener(this);
        finishBtn = findViewById(R.id.finish_button);
        finishBtn.setOnClickListener(this);
        removeBtn = findViewById(R.id.remove_button);
        removeBtn.setOnClickListener(this);
        chronometer = findViewById(R.id.chronometer);

        operationsTextView = findViewById(R.id.operationDisplay);
        answerCheckTextView = findViewById(R.id.mathTitle);
        userAnswerText = findViewById(R.id.answerDisplay);


        infoFromResultActivity = getInfoFromResultActivity();
    }

    @Override
    public void onClick(View v) {
        if (isNumberButton(v)) {
            Button numberButton = (Button) v;
            String numberText = numberButton.getTag().toString();
            appendTextToAnswerTextView(numberText);
        } else if (v.getId() == R.id.dot_button) {
            String currentText = userAnswerText.getText().toString();
            if (!currentText.contains(".")) {
                String dot = ".";
                appendTextToAnswerTextView(dot);
            }
        } else if (v.getId() == R.id.minus_button) {
            String currentText = userAnswerText.getText().toString();
            if (currentText.isEmpty()) {
                String minus = "-";
                appendTextToAnswerTextView(minus);
            }
        } else if (v.getId() == R.id.generate_button) {
            goToGenerate();

        } else if (v.getId() == R.id.validate_button) {
            goToValidate();

        } else if (v.getId() == R.id.score_button) {
            goToDisplayScore();

        } else if (v.getId() == R.id.finish_button) {
            goToFinish();

        } else if (v.getId() == R.id.clear_button) {
            goToClear();

        } else if (v.getId() == R.id.remove_button) {
            goToRemove();
        }
    }

    private boolean isNumberButton(View view) {
        return view.getTag() != null && view.getTag() instanceof String;
    }

    private void appendTextToAnswerTextView(String numberText) {
        String currentText = userAnswerText.getText().toString();
        String newText = currentText + numberText;
        userAnswerText.setText(newText);
    }

    private void goToGenerate() {

        answerCheckTextView.setText("");

        questionModel = new QuestionModel();
        questionModel.generateNewQuestion();

        String question = questionModel.getQuestion();
    //    double answer = questionModel.getAnswer();
        try {
            // Check if it's a division operation with the second number as zero
            if (question.equals("Invalid operation")) {
                operationsTextView.setText("Invalid operation");
                answerCheckTextView.setText("");
                userAnswerText.setEnabled(false);
                userAnswerText.setFocusable(false);
                validateBtn.setEnabled(false);
                return;
            }

            operationsTextView.setText(question);
            userAnswerText.setText("");

            userAnswerText.setEnabled(true);
            validateBtn.setEnabled(true);

        } catch (ArithmeticException e) {
            operationsTextView.setText("Invalid operation");
            answerCheckTextView.setText("");
            userAnswerText.setEnabled(false);
            userAnswerText.setFocusable(false);
            validateBtn.setEnabled(false);
            return;
        }
        setBaseTime();
        chronometer.start();
    }

    private void setBaseTime() {
        chronometer.setBase(SystemClock.elapsedRealtime() - offset);
    }

    private void goToValidate() {
        String operations = operationsTextView.getText().toString();
        String answerText = userAnswerText.getText().toString();

        if (!answerText.isEmpty() && !operations.isEmpty()) {

            double userAnswer = Double.parseDouble(answerText);
            if (questionModel.validateAnswer(userAnswer)) {
                answerCheckTextView.setText("Answer is right!");
            } else {
                answerCheckTextView.setText("Answer is wrong!");
            }
            questionModel.setUserAnswer(userAnswer);
            listOfQuestions.add(questionModel);

        } else if (operations.isEmpty()) {
            validateBtn.setEnabled(false);
            answerCheckTextView.setText("Generate operation first");
        } else {
            validateBtn.setEnabled(false);
            answerCheckTextView.setText("Please input answer!");
        }
        validateBtn.setEnabled(true);
    }

    private void goToDisplayScore() {
        int score = 0;
        int totalQuestions = 0;

        for (QuestionModel question : listOfQuestions) {
            score += question.getScore();
            totalQuestions += question.totalQuestionCount();
        }
        if (totalQuestions == 0) {
            answerCheckTextView.setText("No questions answered yet");
        } else {
            double percentage = (score * 100.0) / totalQuestions;

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtra("percentage", percentage);
            bundle.putSerializable("bundleListOfQuestions", listOfQuestions);
            intent.putExtra("intentListOfQuestions", bundle);
            infoFromResultActivity.launch(intent);
        }
        chronometer.stop();
    }

    private void goToClear() {
        operationsTextView.setText("");
        userAnswerText.setText("");
        answerCheckTextView.setText("");
    }

    private void goToFinish() {
        finish();
    }

    private void goToRemove() {
        String currentText = userAnswerText.getText().toString();
        if (!currentText.isEmpty()) {
            String updatedText = currentText.substring(0, currentText.length() - 1);
            userAnswerText.setText(updatedText);
        }
    }

    private ActivityResultLauncher<Intent> getInfoFromResultActivity() {
        return (infoFromResultActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    String receiveRegister = data.getStringExtra("intentRegister");
                    ArrayList<QuestionModel> receiveList = (ArrayList<QuestionModel>) data.getSerializableExtra("listOfQuestions");
                    answerCheckTextView.setText(receiveRegister);
                    operationsTextView.setText("");
                    userAnswerText.setText("");
                } else {
                    answerCheckTextView.setText("Null");
                }
            }
            setBaseTime();
        }));
    }
}