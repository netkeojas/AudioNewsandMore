package com.example.newsandmore;

public class bookModel {

    String name,link;

    public bookModel()
    {
        //nothing
    }

    public bookModel(String name,String link)
    {
        this.name=name;
        this.link=link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
