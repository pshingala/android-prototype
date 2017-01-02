package net.damroo.androidprototype.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.damroo.androidprototype.database.model.OrderModel;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pujit on 24.04.16.
 * Every rest call goes through EPRestAdapter. e.g. get/post/patch/put orders/orderModel/products/product/stats.  All of them! For now.
 */
public class EPRestAdapter {

    protected Retrofit retrofit;
    protected OrderApi orderApi;
    protected final String baseUrl = "https://sandbox.epages.com/";
    protected final String token = "Bearer xPJp9lif7gy9Tk07bp0nnMKsVNE9TBqP";


    public EPRestAdapter() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder() //
                .baseUrl(baseUrl) //
                .client(client) //
                .addConverterFactory(GsonConverterFactory.create(gson)) //
                .build();
    }

    public Call<OrderModel> getOrderService(String orderId) {
        orderApi = retrofit.create(OrderApi.class);
        return orderApi.getOrderbyId(orderId, token);
    }

    public Call<OrderPage> getOrdersService(Map<String, String> options) {
        orderApi = retrofit.create(OrderApi.class);
        if (options == null)
            return orderApi.getOrdersWithoutParams(token);
        return orderApi.getOrders(token, options);
    }

}