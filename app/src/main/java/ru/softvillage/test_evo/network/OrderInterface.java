package ru.softvillage.test_evo.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.softvillage.test_evo.network.entity.FiscalizedAnswer;
import ru.softvillage.test_evo.network.entity.NetworkAnswer;
import ru.softvillage.test_evo.network.entity.OrgInfo;

public interface OrderInterface {

    @GET("test_app/order_false.php")
    Call<NetworkAnswer> getFalse();

    @GET("test_app/order.php")
    Call<NetworkAnswer> getOrder();

    @GET("test_app/index.php")
    Call<NetworkAnswer> getMainRequest();

    @GET("test_app/firm_info.php")
    Call<OrgInfo> getOrgInfo();

    @POST("test_app/update_status.php")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<NetworkAnswer> postUpdateReceipt(@Body FiscalizedAnswer answer);
}