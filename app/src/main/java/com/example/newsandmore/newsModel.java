package com.example.newsandmore;

public class newsModel {

    String dateOfNews,link;

    public newsModel()
    {
        //nothing
    }

    public newsModel(String dateOfNews,String link)
    {
        this.dateOfNews=dateOfNews;
        this.link=link;
    }

    public String getdate() {
        return dateOfNews;
    }

    public String getLink() {
        return link;
    }
}
