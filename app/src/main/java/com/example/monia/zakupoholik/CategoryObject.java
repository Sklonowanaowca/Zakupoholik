package com.example.monia.zakupoholik;

import android.widget.ImageView;

/**
 * Created by Monia on 2018-01-11.
 */

public class CategoryObject {
    private String content;
    private String categoryName;

    public CategoryObject(String content, String categoryName){
        this.content = content;
        this.categoryName = categoryName;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getContent(){return content;}

    public void setCategoryName(String categoryName){this.categoryName = categoryName;}

    public String getCategoryName(){return categoryName;}
}
