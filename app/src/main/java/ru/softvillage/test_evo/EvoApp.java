package ru.softvillage.test_evo;

import android.app.Application;

import com.google.gson.Gson;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.softvillage.test_evo.network.OrderInterface;

public class EvoApp extends Application {
    public static final String TAG = "ru.softvillage.test_evo";

    @Getter
    private static EvoApp instance;
    @Getter
    private OrderInterface orderInterface;

    @Override
    public void onCreate() {
        instance = this;
        initRetrofit();
        super.onCreate();
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        orderInterface = new Retrofit.Builder()
                .baseUrl("https://kkt-evotor.ru/")
                .client(okHttpClient) // Необязательно
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(OrderInterface.class);
    }
}
