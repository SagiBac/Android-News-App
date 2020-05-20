package com.androidserverside.myapplication;

import android.graphics.Bitmap;

public class Sports {
        public String title;
        public String description;
        public Bitmap ImageBitMap;
        public String UrlToArticle;

        public Sports(){ };
        public Sports(String title, String description, String urlToArticle) {
            this.title = title;
            this.description = description;
            UrlToArticle = urlToArticle;
        }

        public Sports(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public Sports(String title, String description, Bitmap imageBitMap) {
            this.title = title;
            this.description = description;
            ImageBitMap = imageBitMap;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Bitmap getImageBitMap() {
            return ImageBitMap;
        }

        public void setImageBitMap(Bitmap imageBitMap) {
            ImageBitMap = imageBitMap;
        }

        public String getUrlToArticle() {
            return UrlToArticle;
        }

        public void setUrlToArticle(String urlToArticle) {
            UrlToArticle = urlToArticle;
        }
}
