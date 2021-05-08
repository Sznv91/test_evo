package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    @Getter
    private FragmentDispatcher fragmentDispatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initRetrofit();
        initDbHelper();
        initFragmentDispatcher();
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
                .client(getUnsafeOkHttpClient().addInterceptor(logging).hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }).build())
//                .client(okHttpClient) // Необязательно
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build()
                .create(OrderInterface.class);

    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initFragmentDispatcher() {
        fragmentDispatcher = new FragmentDispatcher();
    }
}
