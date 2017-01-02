package net.damroo.androidprototype.database.model;

import com.google.gson.annotations.Expose;

public class Image {

    @Expose
    private String url;

    @Expose
    private String classifier;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }
}
