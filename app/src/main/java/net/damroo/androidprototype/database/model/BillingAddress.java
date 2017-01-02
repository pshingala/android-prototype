package net.damroo.androidprototype.database.model;

import com.google.gson.annotations.Expose;

public class BillingAddress {

    @Expose
    private String firstName;


    @Expose
    private String lastName;


    @Expose
    private String city;


    @Expose
    private String country;

    private String displayCountry;

    public String getDisplayCountry() { return displayCountry; }

    public void setDisplayCountry(String displayCountry) {
        this.displayCountry = displayCountry;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
