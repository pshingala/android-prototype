package net.damroo.androidprototype.service.order;

import net.damroo.androidprototype.events.DisplayOrderEvent;
import net.damroo.androidprototype.events.DisplayEventType;
import net.damroo.androidprototype.events.OrdersDownloadEvent;
import net.damroo.androidprototype.events.WriteOrderEvent;
import net.damroo.androidprototype.database.model.OrderModel;
import net.damroo.androidprototype.retrofit.EPRestAdapter;
import net.damroo.androidprototype.retrofit.OrderPage;
import net.damroo.androidprototype.service.DaggerComponent;
import net.damroo.androidprototype.service.DaggerDaggerComponent;
import net.damroo.androidprototype.service.util.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderNetworkService {

    DaggerComponent network;

    private EPRestAdapter rest;

    private OrderDBService orderDBService;
    private final String RESULTS_PER_PAGE = "10";


    @Inject
    public OrderNetworkService(EPRestAdapter rest, OrderDBService orderDBService) {
        network = DaggerDaggerComponent.create();
        network.inject(this);
        this.rest = rest;
        this.orderDBService = orderDBService;
    }

    // allows downloading orders in three modes
    // 1. for first use (from last year)
    // 2. check and download for new orders (while swiping down)
    // 3. download old orders (while swiping up if page is over)
    public void downloadOrder(OrdersDownloadEvent event) {
        if (event.name.equals("firstUse")) {
            downloadOrdersForFirstUse();
        } else if (event.name.equals("downloadOldOrders")) {
            prepareDownloadOldOrdersSinglePage();
        } else if (event.name.equals("downloadNewOrders")) {
            try {
                prepareDownloadNewOrders();
            } finally {
                EventBus.getDefault().post(new DisplayOrderEvent(DisplayEventType.STOP_ANIMATION));
            }
        }

    }

    // 1. for first use (from last year)
    private void downloadOrdersForFirstUse() {
        try {
            if (!orderDBService.ordersExist()) {

                downloadOrdersFromLastYear(); // if db is empty

            } else {

                OrderModel latestNetOrderModel = getLatestNetworkOrder();
                OrderModel oldestNetOrderModel = getOneYearOldNetworkOrder();

                if (latestNetOrderModel == null || oldestNetOrderModel == null)
                    return; // No orders exists or Network not available

                OrderModel latestDBOrderModel = orderDBService.getOrderByOrderId(latestNetOrderModel.getOrderId());
                OrderModel oldestDBOrderModel = orderDBService.getOrderByOrderId(oldestNetOrderModel.getOrderId());

                if (oldestDBOrderModel == null)
                    prepareDownloadOneYearOldOrders(); // download old orders not present in db

                if (latestDBOrderModel == null)
                    prepareDownloadNewOrders(); // download latest orders not present in db.

            }
        } catch (Exception e) {
            // do nothing.
        }
    }

    // all orders from last year
    private void downloadOrdersFromLastYear() {
        Map<String, String> params = new HashMap<>();
        params.put("createdAfter", Utility.getOneYearOldTimeStamp());
        iterateOrderPages(params);
    }

    // 2. check and download for new orders (while swiping down)
    private void prepareDownloadNewOrders() {
        OrderModel latestDBOrderModel = orderDBService.getLatestDBOrder();
        Long newTime = latestDBOrderModel.getCreationDate().getTime() + 1000l;
        Date queryDate = new Date(newTime);
        Map<String, String> params = new HashMap<>();
        params.put("createdAfter", Utility.getDateInString(queryDate));
        iterateOrderPages(params);

    }

    // all orders from last year except those which are already in db
    private void prepareDownloadOneYearOldOrders() {
        OrderModel oldestDBOrderModel = orderDBService.getOldestDBOrder();
        Map<String, String> params = new HashMap<>();
        params.put("createdBefore", Utility.getDateInString(oldestDBOrderModel.getCreationDate()));
        params.put("createdAfter", Utility.getOneYearOldTimeStamp());
        iterateOrderPages(params);
    }


    // 3. download old orders (while swiping up if page is over)
    private void prepareDownloadOldOrdersSinglePage() {
        OrderModel oldestDBOrderModel = orderDBService.getOldestDBOrder();
        Long newTime = oldestDBOrderModel.getCreationDate().getTime() - 1000l;
        Date queryDate = new Date(newTime);
        Map<String, String> params = new HashMap<>();
        params.put("createdBefore", Utility.getDateInString(queryDate));
        params.put("RESULTS_PER_PAGE", RESULTS_PER_PAGE);
        params.put("page", "1");
        downloadOrderPage(params);
    }

    // In case of multiple order pages this method iterates over order pages calls downloadOrderPage
    private void iterateOrderPages(Map<String, String> params) {
        OrderPage page = getPrepareDownloadOrderPage(params);
        if (page != null) {
            int totalPage = Utility.getLastPageNumber(page.getResults(), Double.parseDouble(RESULTS_PER_PAGE));

            for (int i = 1; i <= totalPage; i++) {
                Map<String, String> paramsForDownload = new HashMap<>();
                paramsForDownload.putAll(params);
                paramsForDownload.put("RESULTS_PER_PAGE", RESULTS_PER_PAGE);
                paramsForDownload.put("page", String.valueOf(i));
                downloadOrderPage(paramsForDownload);
            }

        }

    }


    // this method will iterate over orders in a given order page and trigger downloadDetailedOrder for the
    private void downloadOrderPage(Map<String, String> params) {
        try {
            Call<OrderPage> call = rest.getOrdersService(params);
            OrderPage page = call.execute().body();
            if (page.getResults() == 0)
                return;
            // Save orders
            for (OrderModel orderModel : page.getItems()) {
                downloadDetailedOrder(orderModel.getOrderId());
            }
        } catch (Exception e) {
        }
    }

    // this method gets detailed order and sends event to save it in database.
    private void downloadDetailedOrder(String orderId) {
        // Get single orderModel
        Call<OrderModel> call = rest.getOrderService(orderId);
        call.enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                OrderModel orderModel = response.body();
                EventBus.getDefault().post(new WriteOrderEvent(orderModel));
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {
            }
        });
    }


    // this method makes a call for computing total number of order pages before downloading
    private OrderPage getPrepareDownloadOrderPage(Map<String, String> params) {
        params.put("RESULTS_PER_PAGE", "1");
        params.put("page", "1");
        Call<OrderPage> call = rest.getOrdersService(params);
        try {
            return call.execute().body();
        } catch (Exception e) {
            return null;
        }
    }


    // get most recent order from the server
    private OrderModel getLatestNetworkOrder() {
        Call<OrderPage> call = rest.getOrdersService(null);
        OrderModel first = null;
        try {
            OrderPage page = call.execute().body();
            if (page.getResults() == 0)
                return null;
            first = page.getItems().get(0);
        } catch (Exception e) {
            return null;
        }
        return first;
    }

    // get one year old order from the server
    private OrderModel getOneYearOldNetworkOrder() {

        Map<String, String> params = new HashMap<>();
        params.put("createdAfter", Utility.getOneYearOldTimeStamp());
        params.put("RESULTS_PER_PAGE", "10");
        Call<OrderPage> call = rest.getOrdersService(params);
        OrderPage page = null;
        try {
            page = call.execute().body();
            if (page.getResults() == 0)
                return null;
            // Get Last orderModel
            if (page.getResults() < page.getResultsPerPage())
                return page.getItems().get(page.getResults() - 1);
            params.put("page", String.valueOf(Utility.getLastPageNumber(page.getResults(), page.getResultsPerPage())));
            int lastItemNumber = (page.getResults() % page.getResultsPerPage()) - 1;
            call = rest.getOrdersService(params);
            page = call.execute().body();
            return page.getItems().get(lastItemNumber);

        } catch (Exception e) {
            return null;
        }
    }


}