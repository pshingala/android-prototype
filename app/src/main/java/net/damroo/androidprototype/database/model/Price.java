package net.damroo.androidprototype.database.model;

import com.google.gson.annotations.Expose;

public class Price {

    @Expose
    private String formatted;

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }
}
