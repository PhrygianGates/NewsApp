package com.example.newsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {
    String startTime;
    String endTime;
    String category;
    String word;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        TextView cancelButton = findViewById(R.id.cancel_button);
        TextView searchButton = findViewById(R.id.search_button);
        EditText startTimeEditText = findViewById(R.id.start_time_edit_text);
        EditText endTimeEditText = findViewById(R.id.end_time_edit_text);
        EditText categoryEditText = findViewById(R.id.category_edit_text);
        EditText wordEditText = findViewById(R.id.word_edit_text);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        startTimeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
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
                    System.out.println(wordEditText.getText());
                    return true;
                } else {
                    return false;
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = String.valueOf(startTimeEditText.getText());
                endTime = String.valueOf(endTimeEditText.getText());
                category = String.valueOf(categoryEditText.getText());
                word = String.valueOf(wordEditText.getText());

                String pattern1 = "\\d{4}-\\d{2}-\\d{2}";
                Pattern r = Pattern.compile(pattern1);
                Matcher m = r.matcher(startTime);

                if (!m.find() && !startTime.equals("")) {
                    Toast.makeText(MyApplication.context, "您输入了错误的起始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                m = r.matcher(endTime);
                if (!m.find() && !endTime.equals("")) {
                    Toast.makeText(MyApplication.context, "您输入了错误的结束时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> categories = new ArrayList<>(Arrays.asList("娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"));
                if (!categories.contains(category) && !category.equals("")) {
                    Toast.makeText(MyApplication.context, "您输入了错误的新闻类别", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MyApplication.context, DisplayActivity.class);
                intent.putExtra("startTime=", startTime);
                intent.putExtra("endTime=", endTime);
                intent.putExtra("category=", category);
                intent.putExtra("word=", word);
                startActivity(intent);
            }
        });

    }
}
