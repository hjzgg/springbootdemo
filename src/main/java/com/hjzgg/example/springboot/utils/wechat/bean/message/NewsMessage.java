package com.hjzgg.example.springboot.utils.wechat.bean.message;

import java.util.List;

public class NewsMessage
        extends Message {
    private News news;

    public NewsMessage(String touser, List<Article> articles) {
        super(touser, "news");
        this.news = new News();
        this.news.setArticles(articles);
    }

    public News getNews() {
        return this.news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public static class News {
        private List<Article> articles;

        public List<Article> getArticles() {
            return this.articles;
        }

        public void setArticles(List<Article> articles) {
            this.articles = articles;
        }
    }

    public static class Article {
        private String title;
        private String description;
        private String url;
        private String picurl;

        public Article(String title, String description, String url, String picurl) {
            this.title = title;
            this.description = description;
            this.url = url;
            this.picurl = picurl;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPicurl() {
            return this.picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
