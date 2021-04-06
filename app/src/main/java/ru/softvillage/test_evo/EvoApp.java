package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.softvillage.test_evo.network.OrderInterface;
import ru.softvillage.test_evo.roomDb.DbHelper;
import ru.softvillage.test_evo.roomDb.LocalDataBase;

public class EvoApp extends Application {
    public static final String TAG = "ru.softvillage.test_evo";

    @Getter
    private static EvoApp instance;
    @Getter
    private OrderInterface orderInterface;
    @Getter
    private DbHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initRetrofit();
        initDbHelper();
    }

    private void initDbHelper() {
        LocalDataBase db = LocalDataBase.getDataBase(this);
        dbHelper = new DbHelper(db);

    }

    private void initRetrofit() {
        @SuppressLint("LongLogTag") HttpLoggingInterceptor logging = new HttpLoggingInterceptor(s -> Log.d(TAG + "_Network", s));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();


        orderInterface = new Retrofit.Builder()
                .baseUrl("https://kkt-evotor.ru/")
                .client(okHttpClient) // Необязательно
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(OrderInterface.class);

    }
}
