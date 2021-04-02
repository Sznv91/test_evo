package ru.softvillage.test_evo.network;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.softvillage.test_evo.network.entity.NetworkAnswer;

public interface OrderInterface {

    @GET("test_app/order_false.php")
    Call<NetworkAnswer> getFalse();

    @GET("test_app/order.php")
    Call<NetworkAnswer> getOrder();

    @GET("test_app/index.php")
    Call<NetworkAnswer> getMainRequest();
}