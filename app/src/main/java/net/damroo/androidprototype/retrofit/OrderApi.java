package net.damroo.androidprototype.retrofit;

import net.damroo.androidprototype.database.model.OrderModel;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface OrderApi {

    // TODO either put shopId in var or put /rs/shops/shopId i.e. second part of api url in var to make it work with retrofit.

    @Headers({"Accept: application/vnd.epages.v1+json"})
    @GET("/rs/shops/Hackathon11/orders/{orderId}")
    Call<OrderModel> getOrderbyId(
            @Path("orderId") String orderId, @Header("Authorization") String token
    );

    @Headers({"Accept: application/vnd.epages.v1+json"})
    @GET("/rs/shops/Hackathon11/orders/")
    Call<OrderPage> getOrders(@Header("Authorization") String token, @QueryMap Map<String, String> options
    );

    @Headers({"Accept: application/vnd.epages.v1+json"})
    @GET("/rs/shops/Hackathon11/orders/")
    Call<OrderPage> getOrdersWithoutParams(@Header("Authorization") String token
    );


}