package com.example.lab1_2;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    private EditText inputText;
    private Button submitButton;
    private TextView resultText;
    private ImageView emojiView;
    private ConstraintLayout mainLayout; // ConstraintLayout từ activity_main.xml

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.etInput);
        submitButton = findViewById(R.id.btnSubmit);
        resultText = findViewById(R.id.resultTextView);
        emojiView = findViewById(R.id.imgEmoji);  // Đảm bảo ID của ImageView đúng
        mainLayout = findViewById(R.id.main_layout); // ID của ConstraintLayout

        submitButton.setOnClickListener(v -> {
            String text = inputText.getText().toString();
            if (!text.isEmpty()) {
                SentimentService.analyzeSentiment(text, new SentimentService.SentimentCallback() {
                    @Override
                    public void onSuccess(String sentiment) {
                        runOnUiThread(() -> updateUI(sentiment));
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> resultText.setText(errorMessage));
                    }
                });
            }
        });
    }

    private void updateUI(String sentiment) {
        if (sentiment.equalsIgnoreCase("positive")) {
            mainLayout.setBackgroundColor(Color.GREEN);
            emojiView.setImageResource(R.drawable.smile); // Đổi thành emoji mặt cười
            resultText.setText("Sentiment: positive");
        } else if (sentiment.equalsIgnoreCase("negative")) {
            mainLayout.setBackgroundColor(Color.RED);
            emojiView.setImageResource(R.drawable.sad); // Đổi thành emoji mặt buồn
            resultText.setText("Sentiment: negative");
        } else {
            resultText.setText("Sentiment: unknown");
        }
    }
}
