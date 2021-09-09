package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {
    private DragGridLayout gridLayout1;
    private DragGridLayout gridLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        gridLayout1 = findViewById(R.id.drag_layout_1);
        gridLayout2 = findViewById(R.id.drag_layout_2);

        Toolbar toolbar = findViewById(R.id.detail_tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("选择标签");

        gridLayout1.setCanDrag(true);
        gridLayout1.setItems(MyApplication.chosen);
        gridLayout2.setItems(MyApplication.unchosen);

        gridLayout1.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                gridLayout1.removeView(tv);
                MyApplication.chosen.remove(tv.getText().toString());
                gridLayout2.addGridItem(tv.getText().toString());
                MyApplication.unchosen.add(tv.getText().toString());
            }
        });
        gridLayout2.setOnDragItemClickListener(new DragGridLayout.OnDragItemClickListener() {
            @Override
            public void onDragItemClick(TextView tv) {
                gridLayout2.removeView(tv);
                MyApplication.unchosen.remove(tv.getText().toString());
                gridLayout1.addGridItem(tv.getText().toString());
                MyApplication.chosen.add(tv.getText().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
