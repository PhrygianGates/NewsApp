package com.example.newsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    boolean isSaved;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        TextView textViewContent = findViewById(R.id.content);
        TextView textViewTitle = findViewById(R.id.title);
        TextView textViewPublishTime = findViewById(R.id.publishTime);
        TextView textViewPublisher = findViewById(R.id.publisher);
        String content = getIntent().getStringExtra("content=");
        String title = getIntent().getStringExtra("title=");
        String publishTime = getIntent().getStringExtra("publishTime=");
        String publisher = getIntent().getStringExtra("publisher=");
        textViewContent.setText(content);
        textViewTitle.setText(title);
        textViewPublishTime.setText(publishTime);
        textViewPublisher.setText(publisher);

        ImageView saveIcon = findViewById(R.id.save_icon);
        long id = getIntent().getLongExtra("id=", -1);

        if (LitePal.where("markID=?", String.valueOf(id)).find(MarkLog.class).isEmpty()) {
            isSaved = false;
            saveIcon.setImageResource(R.drawable.ic_save);
        } else {
            isSaved = true;
            saveIcon.setImageResource(R.drawable.ic_saved);
        }

        LinearLayout llGroup = (LinearLayout) findViewById(R.id.ll_group);
        String image = getIntent().getStringExtra("image=");
        List<String> images;
        if (image.length() == 0) {
            images = new ArrayList<>();
            images.add("https://th.bing.com/th/id/OIP.F0l-uBZ7P7BSiifS_ZIRRQAAAA?pid=ImgDet&rs=1");
        } else {
            images = Arrays.asList(image.split(", "));
        }
        llGroup.removeAllViews();  //clear linearlayout
        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Glide.with(MyApplication.context).load(images.get(i)).into(imageView);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            llGroup.addView(imageView);
        }
        saveIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSaved) {
                    isSaved = true;
                    saveIcon.setImageResource(R.drawable.ic_saved);
                    if (LitePal.where("markID=?", String.valueOf(id)).find(MarkLog.class).isEmpty()) {
                        MarkLog log = new MarkLog(id);
                        log.save();
                    }
                }
                else {
                    isSaved = false;
                    saveIcon.setImageResource(R.drawable.ic_save);
                    if (!LitePal.where("markID=?", String.valueOf(id)).find(MarkLog.class).isEmpty()) {
                        LitePal.deleteAll(MarkLog.class, "markID=?", String.valueOf(id));
                    }
                }
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
