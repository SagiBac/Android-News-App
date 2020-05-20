package com.androidserverside.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.ArticlesViewHolder> {

        private List<Sports> articles;
        ArticleEventListener listener;



        public void setListener(ArticleEventListener listener){
            this.listener = listener;
        }

        public SportsAdapter(List<Sports> articles){
            this.articles = articles;
        }

        public class ArticlesViewHolder extends RecyclerView.ViewHolder{
            TextView ArticleTitleTv;
            TextView ArticleDescriptionTv;
            TextView ArticleSeeMore;
            ImageView ArticleImage;

            public ArticlesViewHolder(@NonNull final View itemView) {
                super(itemView);
                ArticleTitleTv = itemView.findViewById(R.id.Article_title);
                ArticleDescriptionTv = itemView.findViewById(R.id.Article_description);
                ArticleImage = itemView.findViewById(R.id.Article_image);
                ArticleSeeMore = itemView.findViewById(R.id.Article_SeeMore);
                ArticleSeeMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.SeeFullArticle(getAdapterPosition());

                    }
                });
            }
        }

        @NonNull
        @Override
        public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sports_article_view,parent,false);
            ArticlesViewHolder ArticleViewHolder = new ArticlesViewHolder(view);
            return ArticleViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position) {
            Sports article = articles.get(position);
            holder.ArticleTitleTv.setText(article.getTitle());
            holder.ArticleDescriptionTv.setText(article.getDescription());
            holder.ArticleImage.setImageBitmap(article.getImageBitMap());
        }

        @Override
        public int getItemCount() {
            return articles.size();
        }

        interface ArticleEventListener{
            void SeeFullArticle(int position);
        }
}
