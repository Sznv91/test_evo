package ru.softvillage.fiscalizer.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import ru.softvillage.fiscalizer.network.entity.FiscalizationRequest;
import ru.softvillage.fiscalizer.network.entity.FiscalizedAnswer;
import ru.softvillage.fiscalizer.network.entity.NetworkAnswer;
import ru.softvillage.fiscalizer.network.entity.OrgInfo;
import ru.softvillage.fiscalizer.network.entity.SmsServerInitResponse;

public interface OrderInterface {

    @GET("app_fiscalizer/get_receipt.php")
    Call<NetworkAnswer> getMainRequest();

    @GET("cloud/organization_data.php")
    Call<OrgInfo> getOrgInfo();

    @POST("app_fiscalizer/set_status_receipt.php")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<NetworkAnswer> postUpdateReceipt(@Body FiscalizedAnswer answer);

    @GET("app_fiscalizer/check_sms_server.php")
    Call<SmsServerInitResponse> getIsInitSmsServer();
}