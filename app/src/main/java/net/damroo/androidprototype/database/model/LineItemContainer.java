package net.damroo.androidprototype.database.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class LineItemContainer {

    @Expose
    private Price grandTotal;

    @Expose
    private List<ProductLineItem> productLineItems;

    public List<ProductLineItem> getProductLineItems() {
        return productLineItems;
    }

    public void setProductLineItems(List<ProductLineItem> productLineItems) {
        this.productLineItems = productLineItems;
    }

    public Price getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Price grandTotal) {
        this.grandTotal = grandTotal;
    }
}
