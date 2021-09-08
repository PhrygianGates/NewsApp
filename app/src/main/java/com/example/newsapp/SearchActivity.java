package com.example.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    String startTime;
    String endTime;
    String category;
    String word;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        TextView CancelButton = findViewById(R.id.cancel_button);
        EditText startTimeEditText = findViewById(R.id.start_time_edit_text);
        EditText endTimeEditText = findViewById(R.id.end_time_edit_text);
        EditText categoryEditText = findViewById(R.id.category_edit_text);
        EditText wordEditText = findViewById(R.id.word_edit_text);

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        startTimeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    startTime = String.valueOf(startTimeEditText.getText());
                    System.out.println(startTimeEditText.getText());
                    return true;
                } else {
                    return false;
                }
            }
        });
        endTimeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    endTime = String.valueOf(endTimeEditText.getText());
                    System.out.println(endTimeEditText.getText());
                    return true;
                } else {
                    return false;
                }
            }
        });
        categoryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    category = String.valueOf(categoryEditText.getText());
                    System.out.println(categoryEditText.getText());
                    return true;
                } else {
                    return false;
                }
            }
        });
        wordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    word = String.valueOf(wordEditText.getText());
                    System.out.println(wordEditText.getText());
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
