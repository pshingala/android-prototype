package net.damroo.androidprototype.database.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ProductLineItem {

    @Expose
    private String lineItemId;


    @Expose
    private List<Image> images;


    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(String lineItemId) {
        this.lineItemId = lineItemId;
    }


    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}