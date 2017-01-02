package net.damroo.androidprototype.retrofit;

import net.damroo.androidprototype.database.model.OrderModel;

import java.util.List;

/**
 * Created by damroo on 4/25/2016.
 */
public class OrderPage {
    private List<OrderModel> items;
    private int results;
    private int resultsPerPage;
    private int page;

    public List<OrderModel> getItems() {
        return items;
    }

    public void setItems(List<OrderModel> items) {
        this.items = items;
    }

    public int getResults() {
        return results;
    }

    public void setResults(int results) {
        this.results = results;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
