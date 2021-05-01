package ru.softvillage.test_evo.tabs.left_menu.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;


import ru.softvillage.test_evo.tabs.left_menu.IMainView1;


public class ApiPresenter {
    public final static int ERROR_PAPER_OUT = 1;
    public final static int ERROR_ELECTRICITY_OFF = 2;
    public final static int ERROR_WRONG_PRINT_DATA = 3;
    public final static int ERROR_SHIFT_ENDS_WITH_NO_AUTO_CLOSE = 4;

    private final static String TAG = "ApiPresenter";
    private static final ApiPresenter instance = new ApiPresenter();

    private boolean checkingConnectApp = false;
    private boolean isAppConnected = false;
//    private IMainView iMainView;
    private IMainView1 iMainView1;

    private ApiPresenter() {}

    public static ApiPresenter getInstance() {
        return instance;
    }

    /*public void setiMainView(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    public void setiMainView1(IMainView1 iMainView1) {
        this.iMainView1 = iMainView1;
    }

    public void connectApp() {
        if (!checkingConnectApp) {
            checkingConnectApp = true;

            ApiService.getInstance().getApi().connectApp()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<BaseResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onError(Throwable e) {
                            SessionPresenter.getInstance().checkInetConnection();

                            Log.d(TAG_EVOTOR, "connect_app error: " + e.toString());
                            checkingConnectApp = false;

                            if (iMainView != null) iMainView.onConnectAppError(e.toString());
                            if (iMainView1 != null) iMainView1.onConnectAppError(e.toString());
                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onNext(BaseResponse response) {
                            if (response.success != 0) {
                                isAppConnected = Boolean.parseBoolean(response.message);
                                if (iMainView != null) {
                                    iMainView.onConnectAppSuccess(isAppConnected);
                                    iMainView1.onConnectAppSuccess(isAppConnected);
                                }
                            } else {
                                if (iMainView != null) iMainView.onConnectAppError("Ошибка");
                                if (iMainView1 != null) iMainView1.onConnectAppError("Ошибка");
                            }

                            checkingConnectApp = false;
                        }
                    });
        }
    }

    public void setSendSms() {
        boolean isChecked = SessionPresenter.getInstance().isSendSms();
        String status = "false";

        if (isChecked) {
            status = SessionPresenter.getInstance().getDefaultSmsService();

            if (TextUtils.isEmpty(status)) {
                status = EVOTOR_SERVICE;
                SessionPresenter.getInstance().setDefaultSmsService(status);
            }
        }

        ApiService.getInstance().getApi().setSendSms(status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (iMainView != null) iMainView.onSetSendSmsError(e.toString());
                    }

                    @Override
                    public void onNext(BaseResponse response) {
                        if (response.success != 0) {
                            if (iMainView != null) iMainView.onSetSendSmsSuccess();
                        }
                        else {
                            if (iMainView != null) iMainView.onSetSendSmsError(response.message);
                        }
                    }
                });
    }

    public void setError(int code, String message, String data) {
        ApiService.getInstance().getApi().setError(code, message, data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        SessionPresenter.getInstance().checkInetConnection();

                        if (iMainView != null) iMainView.onSetErrorError(e.toString());
                        if (iMainView1 != null) iMainView1.onSetErrorError(e.toString());
                    }

                    @Override
                    public void onNext(BaseResponse response) {
                        if (response.success != 0) {
                            if (iMainView1 != null) iMainView1.onSetErrorSuccess();
                        }
                        else {
                            if (iMainView1 != null) iMainView1.onSetErrorError(response.message);
                        }
                    }
                });
    }

    public void closeSession(long timestamp) {
        ApiService.getInstance().getApi().setSessionInfo("close", timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResponse response) {

                    }
                });
    }

    public void openSession(long timestamp) {
        ApiService.getInstance().getApi().setSessionInfo("open", timestamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(BaseResponse response) {

                    }
                });
    }

    public void setStatusesReceipt(Map<Integer, ReceiptStatus> statuses) {
        ApiService.getInstance().getApi().setStatusReceipt(new Gson().toJson(statuses))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        SessionPresenter.getInstance().checkInetConnection();
                    }

                    @Override
                    public void onNext(BaseResponse response) {

                    }
                });
    }

    public void getReceipt() {
        ApiService.getInstance().getApi().getReceipt()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ReceiptsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        SessionPresenter.getInstance().checkInetConnection();

                        if (iMainView != null) iMainView.onGetReceiptError("Не удалось загрузить чеки: " + e.toString());
                        if (iMainView1 != null) iMainView1.onGetReceiptError("Не удалось загрузить чеки: " + e.toString());

                        Logger.getInstance().addLine("ApiPresenter; getReceipt; onError: " + e.toString(), true);
                    }

                    @Override
                    public void onNext(ReceiptsResponse response) {
                        if (response.success != 0)
                            SessionPresenter.getInstance().onGetReceipt(response);
                        else {
                            if (iMainView != null) iMainView.onGetReceiptError("Не удалось загрузить чеки.");
                            if (iMainView1 != null) iMainView1.onGetReceiptError("Не удалось загрузить чеки.");

                            Logger.getInstance().addLine("ApiPresenter; getReceipt; onNext: response.success" + response.success, true);
                        }
                    }
                });
    }

    *//*public void getReceiptTemp() {
        ApiService.getInstance().getApi().getReceipt()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ReceiptsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (iMainView != null) iMainView.onGetReceiptError("Не удалось загрузить чеки: " + e.toString());
                    }

                    @Override
                    public void onNext(ReceiptsResponse response) {
                        if (response.success != 0) {
                            SessionPresenter.getInstance().onGetReceiptTemp(response);
                        }
                        else {
                            if (iMainView != null) iMainView.onGetReceiptError("Не удалось загрузить чеки.");
                        }
                    }
                });
    }*//*

    public boolean isCheckingConnectApp() {
        return checkingConnectApp;
    }

    public boolean isAppConnected() {
        return isAppConnected;
    }

    public void terminalDefinition() {
        ApiService.getInstance().getApi().terminalDefinition("FISKALIZER", SessionPresenter.getInstance().getKktSerialNumber(),
                SessionPresenter.getInstance().getKktRegNumber(), SessionPresenter.getInstance().getFsSerialNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        SessionPresenter.getInstance().checkInetConnection();

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                terminalDefinition();
                            }
                        }, 5000);
                    }

                    @Override
                    public void onNext(BaseResponse baseResponse) {

                    }
                });
    }*/
}
