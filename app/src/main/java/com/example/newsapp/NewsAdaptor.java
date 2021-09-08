package com.example.newsapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.litepal.LitePal;

import java.util.List;

public class NewsAdaptor extends RecyclerView.Adapter<NewsAdaptor.BaseViewHolder> {

    final static int ONE_IMAGE_VIEW_TYPE = 1;
    final static int THREE_IMAGES_VIEW_TYPE = 3;
    final static int FOOTER_VIEW_TYPE = -1;
    final static int HAS_MORE = 996;
    final static int FINISHED = 997;
    final static int FAILED = 998;
    List<News> newsList;
    NewsFragment newsFragment;

    int footerViewStatus;

    public NewsAdaptor(List<News> newsList, NewsFragment newsFragment) {
        super();
        this.newsList = newsList;
        this.newsFragment = newsFragment;
        footerViewStatus = HAS_MORE;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == THREE_IMAGES_VIEW_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_three_images, parent, false);
            return new ThreeImagesViewHolder(itemView);
        } else if (viewType == ONE_IMAGE_VIEW_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_one_image, parent, false);
            return new OneImageViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view, parent, false);
            return new FooterViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof NewsViewHolder) {
            News news = newsList.get(position);
            ((NewsViewHolder) holder).title.setText(news.title);
            if (!LitePal.where("historyID=?", String.valueOf(news.id)).find(HistoryLog.class).isEmpty()) {
                ((NewsViewHolder) holder).title.setTextColor(Color.GRAY);
            } else {
                ((NewsViewHolder) holder).title.setTextColor(Color.BLACK);
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(news.publisher).append("    ").append(news.publishTime);
            ((NewsViewHolder) holder).description.setText(stringBuilder.toString());
            if (holder instanceof OneImageViewHolder) {
                Glide.with(MyApplication.context).load(news.images.get(0)).into(((OneImageViewHolder) holder).image);
            } else if (holder instanceof ThreeImagesViewHolder) {
                Glide.with(MyApplication.context).load(news.images.get(0)).into(((ThreeImagesViewHolder) holder).image1);
                Glide.with(MyApplication.context).load(news.images.get(1)).into(((ThreeImagesViewHolder) holder).image2);
                Glide.with(MyApplication.context).load(news.images.get(2)).into(((ThreeImagesViewHolder) holder).image3);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyApplication.context, DetailActivity.class);
                    News currentNews = newsList.get(holder.getAdapterPosition());
                    intent.putExtra("content=", currentNews.content);
                    intent.putExtra("title=", currentNews.title);
                    intent.putExtra("publishTime=", currentNews.publishTime);
                    intent.putExtra("publisher=", currentNews.publisher);
                    intent.putExtra("image=", currentNews.image);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (LitePal.where("historyID=?", String.valueOf(news.id)).find(HistoryLog.class).isEmpty()) {
                        HistoryLog log = new HistoryLog(news.id);
                        log.save();
                        ((NewsViewHolder) holder).title.setTextColor(Color.GRAY);
                    }
                    startActivity(MyApplication.context, intent, null);
                }
            });
        }
        else {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            if (footerViewStatus == HAS_MORE) {
                footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                footerViewHolder.message.setText("正在加载中...");
            } else if (footerViewStatus == FAILED) {
                footerViewHolder.progressBar.setVisibility(View.GONE);
                footerViewHolder.message.setText("加载失败，点击重新加载");
            } else if (footerViewStatus == FINISHED) {
                footerViewHolder.progressBar.setVisibility(View.GONE);
                footerViewHolder.message.setText("已经没有更多内容了");
            }
            footerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                    footerViewHolder.message.setText("正在加载中...");
                    footerViewStatus = HAS_MORE;
                    newsFragment.loadCacheData();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_VIEW_TYPE;
        } else {
            News news = newsList.get(position);
            if (news.images.size() >= 3) {
                return THREE_IMAGES_VIEW_TYPE;
            } else {
                return ONE_IMAGE_VIEW_TYPE;
            }
        }
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class NewsViewHolder extends BaseViewHolder {
        TextView title;
        TextView description;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_title);
            description = itemView.findViewById(R.id.news_description);
        }
    }

    class FooterViewHolder extends BaseViewHolder {
        ProgressBar progressBar;
        TextView message;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            message = itemView.findViewById(R.id.message);
        }
    }

    class OneImageViewHolder extends NewsViewHolder {
        ImageView image;

        public OneImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.news_image);
        }
    }

    class ThreeImagesViewHolder extends NewsViewHolder {
        ImageView image1;
        ImageView image2;
        ImageView image3;

        public ThreeImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            image1 = itemView.findViewById(R.id.news_image_1);
            image2 = itemView.findViewById(R.id.news_image_2);
            image3 = itemView.findViewById(R.id.news_image_3);
        }
    }
}
