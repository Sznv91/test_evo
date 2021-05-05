package ru.softvillage.test_evo.tabs.left_menu.presenter;

import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.evotor.framework.system.SystemStateApi;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.roomDb.Entity.SessionStatisticData;
import ru.softvillage.test_evo.tabs.fragments.StatisticDisplayUpdate;
import ru.softvillage.test_evo.tabs.left_menu.IMainView1;
import ru.softvillage.test_evo.tabs.left_menu.util.Prefs;
import ru.softvillage.test_evo.utils.StatisticConsider;

import static ru.softvillage.test_evo.EvoApp.TAG;

/*import ru.softvillage.fiscalizer.FApp;
import ru.softvillage.fiscalizer.api.ApiService;
import ru.softvillage.fiscalizer.model.PaymentSystemCheckItem;
import ru.softvillage.fiscalizer.model.ReceiptItem;
import ru.softvillage.fiscalizer.model.ReceiptStatus;
import ru.softvillage.fiscalizer.model.ReceiptsResponse;
import ru.softvillage.fiscalizer.model.SessionViewData;
import ru.softvillage.fiscalizer.ui.activities.IMainView;
import ru.softvillage.fiscalizer.ui.activities.IMainView1;*/
/*import static ru.softvillage.fiscalizer.FApp.TAG_EVOTOR;
import static ru.softvillage.fiscalizer.ui.dialogs.SelectSmsServerDialogFragment.EVOTOR_SERVICE;
import static ru.softvillage.fiscalizer.ui.dialogs.SelectSmsServerDialogFragment.SOFT_VILLAGE_SERVICE;*/

@SuppressLint("LongLogTag")
public class SessionPresenter {
    public final static String PRINT_AFTER_CLOSE = "printAfterClose";
    public final static String AUTO_CLOSE = "autoClose";
    public final static String PRINT_CHECKS = "printChecks";
    public final static String SEND_SMS = "sendSms";
    public final static String SEND_EMAIL = "sendEmail";
    public final static String DEFAULT_SMS_SERVICE = "defaultSmsService";
    public final static String DEFAULT_EMAIL_SERVICE = "defaultEmailService";
    public final static String SMS_SERVICE_INIT = "smsServiceInit";
    public final static String EMAIL_SERVICE_INIT = "emailServiceInit";
    public final static String AUTO_CLOSE_TYPE = "autoCloseType";
    public final static String AUTO_CLOSE_EVERY_UNIT = "autoEveryUnit";
    public final static String AUTO_CLOSE_EVERY_VALUE = "autoEveryValue";
    public final static String AUTO_CLOSE_HOUR = "autoCloseHour";
    public final static String AUTO_CLOSE_MINUTE = "autoCloseMinute";
    public final static String CURRENT_THEME = "currentTheme";

    public final static String KEY_LAST_SESSION_NUMBER = "lastSessionNumber";
    public final static String KEY_SESSION_START = "sessionStartDateTime";
    public final static String KEY_SESSION_DATA = "sessionData";
    public final static long CLOSED_SESSION_ID = -1;
    public static final String PREVIOUS_SESSION_STATUS = "previousSessionStatus";
    public boolean previousSessionStatus;


    public final static String KEY_KKT_SERIAL_NUMBER = "kktSerialNumber";
    public final static String KEY_KKT_REG_NUMBER = "kktRegNumber";
    public final static String KEY_FS_SERIAL_NUMBER = "fsSerialNumber";

    public final static int AUTO_CLOSE_EVERY_DAY = 0;
    public final static int AUTO_CLOSE_EVERY_ = 1;
    public final static int AUTO_CLOSE_AT_ = 2;

    public final static int AUTO_CLOSE_EVERY_UNIT_MIN = 0;
    public final static int AUTO_CLOSE_EVERY_UNIT_HOUR = 1;

    public final static String LAST_OPEN_SESSION = "lastOpenSession";
    public final static String LAST_CLOSE_SESSION = "lastCloseSession";


    /**
     * Константа для интервала проверки открытия сессии в миллисекундах.
     */
    private final static long SESSION_CHECK_INTERVAL = 2 * 1000;

    private /*final*/ static SessionPresenter instance;/* = new SessionPresenter();*/

    private LocalDateTime dateLastOpenSession;
    private LocalDateTime dateLastCloseSession;
    private SessionStatisticData data;
    private StatisticDisplayUpdate IstatisticDisplayUpdate;


    private boolean init = false;
    private volatile Boolean isSessionOpen = false;
    private volatile Long lastSessionNumber = -1L;
    private volatile long startDateTime = -1;
    private volatile long endDateTime = -1;
    //    private ArrayList<ReceiptItem> data = new ArrayList<>();
    private AtomicBoolean needInitSession = new AtomicBoolean(false);

/*    private ArrayList<ReceiptItem> printQueue = new ArrayList<>();
    private Map<Integer, ReceiptStatus> statuses = new HashMap<>();*/

    private volatile boolean printing = false;
    /*private Runnable printNext = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG_EVOTOR, "SessionPresenter; printNext; getDeltaToClose(): " + getDeltaToClose());

            boolean fiscalize = getDeltaToClose() > 10 * 1000 || undefinedSession;

            if (isSessionOpen == null || !isSessionOpen) {
                fiscalize = false;

                if (needInitSession.get())
                    fiscalize = true;
            }

            if (isSessionOpen != null && isSessionOpen && startDateTime < 0)
                fiscalize = true;

            Log.d(TAG_EVOTOR, "SessionPresenter; printNext; fiscalize: " + fiscalize + "; printQueue.size(): " + printQueue.size()
                    + "; printing: " + printing + "; stoppingSession.get(): " + stoppingSession.get());

            if (printQueue.size() > 0 && !printing && !stoppingSession.get()) {
                if (fiscalize)
                    print(printQueue.get(0));
            }
        }
    };*/

    private Handler dataHandler;
    private HandlerThread dataHandlerThread;
    private Handler printerHandler;
    private HandlerThread printerHandlerThread;
    private Handler timerHandler;
    private HandlerThread timerHandlerThread;
    private Handler sessionCheckerHandler;
    private HandlerThread sessionCheckerHandlerThread;
    private Handler checkInetHandler;
    private HandlerThread checkInetHandlerThread;

   /* private TimerTick timerTick;

    private SessionChecker sessionChecker;*/

    private AtomicBoolean stoppingSession = new AtomicBoolean(false);

    private volatile boolean printReportOnClose;
    private volatile boolean autoClose;
    private volatile boolean printChecks;
    private volatile boolean sendSms;
    private volatile boolean sendEmail;

    private int autoCloseType = -1;
    private int autoCloseEveryUnit;
    private int autoCloseEveryValue;
    private int autoCloseAtHour;
    private int autoCloseAtMinute;

    private volatile boolean printerServiceConnected = false;

    private String defaultSmsService;
    private boolean smsServiceInit = false;

    private String defaultEmailService;
    private boolean emailServiceInit = false;

    //    private IMainView iMainView;
    private IMainView1 iMainView1;

    private int printSessionReportAttemptsCount = 0;
    private int reconnectToPrinterServiceAttempts = 0;

    private AtomicBoolean isSendsError5 = new AtomicBoolean(false);

    private MutableLiveData<Boolean> dismissAutoCloseDialog = new MutableLiveData<>();

    private MutableLiveData<Integer> notifyCount = new MutableLiveData<>();
    private MutableLiveData<Boolean> undefinedSessionDetected = new MutableLiveData<>();

    private String kktSerialNumber;
    private String kktRegNumber;
    private String fsSerialNumber;

    private AtomicBoolean hasConnection = new AtomicBoolean(true);
    private AnimatorSet noSignalAnimator = new AnimatorSet();

    private boolean undefinedSession = false;

    /**
     * 0 - светаля тема
     * 1 - темная тема
     */
    private int currentTheme = 0;

    /*private ConnectionWrapper connectionWrapper = new ConnectionWrapper() {
        @Override
        public void onPrinterServiceConnected(IPrinterServiceWrapper printerService) {
            Log.d(TAG_EVOTOR, "SessionPresenter; ConnectionWrapper; onPrinterServiceConnected");
            printerServiceConnected = true;
        }

        @Override
        public void onPrinterServiceDisconnected() {
            Log.d(TAG_EVOTOR, "SessionPresenter; ConnectionWrapper; onPrinterServiceDisconnected");
            printerServiceConnected = false;
        }

        @Override
        public void onScalesServiceConnected(IScalesServiceWrapper scalesService) {

        }

        @Override
        public void onScalesServiceDisconnected() {

        }
    };

    class CheckInet implements Runnable {
        @Override
        public void run() {
            if (!hasInternetAccess()) {
                hasConnection.set(false);

                if (iMainView1 != null) iMainView1.showNoConnectionIcon();

                checkInetHandler.postDelayed(this, 2 * 1000);
            }
            else {
                hasConnection.set(true);

                if (iMainView1 != null) iMainView1.showNoConnectionIcon();

                checkInetHandler.postDelayed(this, 10 * 1000);
            }
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager = (ConnectivityManager) FApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }

        private boolean hasInternetAccess() {
            if (isNetworkAvailable()) {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(ApiService.BASE_URL).openConnection());
                    httpURLConnection.setRequestProperty("User-Agent", "Android");
                    httpURLConnection.setRequestProperty("Connection", "close");
                    httpURLConnection.setConnectTimeout(1500);
                    httpURLConnection.connect();

                    return (httpURLConnection.getResponseCode() == 200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return false;
        }
    }

    class AddDataToQueue implements Runnable {
        private ArrayList<ReceiptItem> newItems;

        public AddDataToQueue(ArrayList<ReceiptItem> newItems) {
            this.newItems = newItems;
        }

        @Override
        public void run() {
            Log.d(TAG_EVOTOR, "SessionPresenter; AddDataToQueue; add items to queue");

            data.addAll(newItems);

            printQueue.addAll(new ArrayList<>(newItems));

            if (!needInitSession.get())
                needInitSession.set(true);

            Log.d(TAG_EVOTOR, "SessionPresenter; AddDataToQueue; call printNext");
            dataHandler.post(printNext);
        }
    }

    class UpdateDataItem implements Runnable {
        private int id;
        private boolean fiscalized;
        private boolean sendInSms = false;
        private boolean sendInEmail = false;

        public UpdateDataItem(int id, boolean fiscalized, boolean sendInSms, boolean sendInEmail) {
            this.id = id;
            this.fiscalized = fiscalized;
            this.sendInSms = sendInSms;
            this.sendInEmail = sendInEmail;
        }

        @Override
        public void run() {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).id == id) {
                    data.get(i).fiscalized = fiscalized;
                    data.get(i).sendInSms = sendInSms;
                    data.get(i).sendInEmail = sendInEmail;
                    break;
                }
            }

            Log.d(TAG_EVOTOR, "SessionPresenter; UpdateDataItem; updateStatistic");
            if (iMainView != null) iMainView.updateStatistic(formatSessionStatistics());
            if (iMainView1 != null) iMainView1.updateStatistic(formatSessionStatistics());
        }
    }

    class UpdateStatistics implements Runnable {
        @Override
        public void run() {
            Log.d(TAG_EVOTOR, "SessionPresenter; UpdateStatistics; updateStatistic");
            if (iMainView1 != null) iMainView1.updateStatistic(formatSessionStatistics());
        }
    }

    private SessionViewData formatSessionStatistics() {
        if (*//*(data == null || data.size() <= 0) && *//*(isSessionOpen == null || !isSessionOpen)) {
            notifyCount.postValue(-1);
            return null;
        }

        SessionViewData result = new SessionViewData();
        result.number = lastSessionNumber;
        result.startDateTime = startDateTime;

        double sum = 0;
        int checkCount = 0;
        int sendInSms = 0;
        int sendInEmail = 0;

        for (int i = 0; i < data.size(); i++) {
            boolean contains = false;

            for (int j = 0; j < result.fromSources.size(); j++) {
                if (result.fromSources.get(j).id == data.get(i).paymentSystem) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                result.fromSources.add(new PaymentSystemCheckItem(data.get(i).paymentSystem));
        }

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).fiscalized) {
                checkCount++;

                if (data.get(i).sendInSms)
                    sendInSms++;

                if (data.get(i).sendInEmail)
                    sendInEmail++;

                for (int j = 0; j < data.get(i).goods.size(); j++) {
                    sum += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                }

                for (int k = 0; k < result.fromSources.size(); k++) {
                    if (result.fromSources.get(k).id == data.get(i).paymentSystem) {
                        result.fromSources.get(k).count++;

                        for (int j = 0; j < data.get(i).goods.size(); j++) {
                            result.fromSources.get(k).sum
                                    += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                        }
                    }
                }
            }
        }

        notifyCount.postValue(checkCount);

        result.sum = sum;
        result.checksCount = checkCount;
        result.sendInSms = sendInSms;
        result.sendInEmail = sendInEmail;

        return result;
    }

    class StopSession implements Runnable {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            int fiscalizedCount = 0;
            double sum = 0;
            int sendInSmsOrEmailCount = 0;

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).fiscalized) {
                    fiscalizedCount++;

                    if (data.get(i).sendInSms || data.get(i).sendInEmail)
                        sendInSmsOrEmailCount++;

                    for (int j = 0; j < data.get(i).goods.size(); j++) {
                        sum += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                    }
                }
            }

            Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; fiscalizedCount: " + fiscalizedCount + "; sum: " + sum
                    + "; sendInSmsOrEmailCount: " + sendInSmsOrEmailCount);

            int finalFiscalizedCount = fiscalizedCount;
            double finalSum = sum;
            int finalSendInSmsOrEmailCount = sendInSmsOrEmailCount;

            Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; print z report");

            new PrintZReportCommand().process(FApp.getContext(), new IntegrationManagerCallback() {
                @Override
                public void run(IntegrationManagerFuture future) {
                    // отправляем запрос о закрытии смены
                    endDateTime = Calendar.getInstance().getTimeInMillis();

                    if (statuses.size() > 0) {
                        ApiPresenter.getInstance().setStatusesReceipt(new HashMap<>(statuses));
                        statuses.clear();
                    }

                    Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; print z report callback; endDateTime: " + endDateTime);

                    Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; print z report callback; ApiPresenter.getInstance().closeSession(endDateTime);");
                    //ApiPresenter.getInstance().closeSession(endDateTime);

                    Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; updateStatistic");
                    if (iMainView != null) iMainView.updateStatistic(null);
                    if (iMainView1 != null) iMainView1.updateStatistic(null);

                    notifyCount.postValue(-1);

                    ArrayList<ReceiptItem> newData = new ArrayList<>();
                    for (int i = 0; i < data.size(); i++) {
                        if (!data.get(i).fiscalized)
                            newData.add(data.get(i));
                    }

                    data = newData;

                    // печатаем отчет, если установлен флажок
                    if (printReportOnClose) {
                        Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; print z report callback; print report on close");

                        printerHandler.post(new PrintSessionReport(finalFiscalizedCount, finalSum, finalSendInSmsOrEmailCount,
                                startDateTime, endDateTime, true));
                    }

                    Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; print z report callback; clear session data");

                    undefinedSession = false;
                    isSessionOpen = false;
                    lastSessionNumber = -1L;
                    startDateTime = -1;
                    endDateTime = -1;
                    *//*data.clear();
                    printQueue.clear();*//*
                    statuses.clear();
                    isSendsError5.set(false);
                    needInitSession.set(false);

                    Prefs.getInstance().removeKey(KEY_LAST_SESSION_NUMBER);
                    Prefs.getInstance().removeKey(KEY_SESSION_START);
                    Prefs.getInstance().removeKey(KEY_SESSION_DATA);

                    if (!printReportOnClose) {
                        stoppingSession.set(false);

                        printerHandler.post(printNext);

                        if (timerTick != null) {
                            timerHandler.removeCallbacks(timerTick);
                            timerTick = null;
                        }

                        if (sessionChecker != null) {
                            sessionCheckerHandler.removeCallbacks(sessionChecker);
                            sessionChecker = null;
                        }

                        timerTick = new TimerTick();
                        timerHandler.post(timerTick);

                        sessionChecker = new SessionChecker();
                        sessionCheckerHandler.postDelayed(sessionChecker, 2000);
                    }

                    try {
                        if (future.getResult().getType() == IntegrationManagerFuture.Result.Type.ERROR) {
                            Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; future.getResult().getType() error: "
                                    + future.getResult().getError().getMessage());

                            // показываем ошибку
                            if (iMainView != null) {
                                iMainView.onPrintZReportError("Не удалось распечатать Z отчет: " + future.getResult().getError().getMessage());
                            }

                            if (iMainView1 != null) {
                                iMainView1.onPrintZReportError("Не удалось распечатать Z отчет: " + future.getResult().getError().getMessage());
                            }
                        }
                    } catch (IntegrationException e) {
                        e.printStackTrace();
                        Log.d(TAG_EVOTOR, "SessionPresenter; StopSession; future.getResult().getType() exception: " + e.toString());
                    }
                }
            });
        }
    }

    class OnStopSessionByEvent implements Runnable {
        @Override
        public void run() {
            int fiscalizedCount = 0;
            double sum = 0;
            int sendInSmsOrEmailCount = 0;

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).fiscalized) {
                    fiscalizedCount++;

                    if (data.get(i).sendInSms || data.get(i).sendInEmail)
                        sendInSmsOrEmailCount++;

                    for (int j = 0; j < data.get(i).goods.size(); j++) {
                        sum += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                    }
                }
            }

            Log.d(TAG_EVOTOR, "SessionPresenter; OnStopSessionByEvent; fiscalizedCount: " + fiscalizedCount + "; sum: " + sum
                    + "; sendInSmsOrEmailCount: " + sendInSmsOrEmailCount);

            endDateTime = Calendar.getInstance().getTimeInMillis();

            if (statuses.size() > 0) {
                ApiPresenter.getInstance().setStatusesReceipt(new HashMap<>(statuses));
                statuses.clear();
            }

            //ApiPresenter.getInstance().closeSession(endDateTime);

            Log.d(TAG_EVOTOR, "SessionPresenter; OnStopSessionByEvent; updateStatistic");
            if (iMainView != null) iMainView.updateStatistic(null);
            if (iMainView1 != null) iMainView1.updateStatistic(null);

            notifyCount.postValue(-1);

            ArrayList<ReceiptItem> newData = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).fiscalized)
                    newData.add(data.get(i));
            }

            data = newData;

            // печатаем отчет, если установлен флажок
            if (printReportOnClose) {
                Log.d(TAG_EVOTOR, "SessionPresenter; OnStopSessionByEvent; print z report callback; print report on close");

                printerHandler.post(new PrintSessionReport(fiscalizedCount, sum, sendInSmsOrEmailCount,
                        startDateTime, endDateTime, true));
            }

            undefinedSession = false;
            isSessionOpen = false;
            lastSessionNumber = -1L;
            startDateTime = -1;
            endDateTime = -1;
                    *//*data.clear();
                    printQueue.clear();*//*
            statuses.clear();
            isSendsError5.set(false);
            needInitSession.set(false);

            Prefs.getInstance().removeKey(KEY_LAST_SESSION_NUMBER);
            Prefs.getInstance().removeKey(KEY_SESSION_START);
            Prefs.getInstance().removeKey(KEY_SESSION_DATA);

            if (!printReportOnClose) {
                stoppingSession.set(false);

                printerHandler.post(printNext);

                if (timerTick != null) {
                    timerHandler.removeCallbacks(timerTick);
                    timerTick = null;
                }

                if (sessionChecker != null) {
                    sessionCheckerHandler.removeCallbacks(sessionChecker);
                    sessionChecker = null;
                }

                timerTick = new TimerTick();
                timerHandler.post(timerTick);

                sessionChecker = new SessionChecker();
                sessionCheckerHandler.postDelayed(sessionChecker, 2000);
            }
        }
    }


    class FormatDataForSessionReport implements Runnable {
        @Override
        public void run() {
            int fiscalizedCount = 0;
            double sum = 0;
            int sendInSmsOrEmailCount = 0;

            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).fiscalized) {
                    fiscalizedCount++;

                    if (data.get(i).sendInSms || data.get(i).sendInEmail)
                        sendInSmsOrEmailCount++;

                    for (int j = 0; j < data.get(i).goods.size(); j++) {
                        sum += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                    }
                }
            }

            Log.d(TAG_EVOTOR, "SessionPresenter; FormatDataForSessionReport; print report");

            printerHandler.post(new PrintSessionReport(fiscalizedCount, sum, sendInSmsOrEmailCount,
                    startDateTime, endDateTime, false));
        }
    }

    class PrintSessionReport implements Runnable {
        private int fiscalizedCount = 0;
        private double sum = 0;
        private int sendInSmsOrEmailCount = 0;
        private long startDateTime = -1;
        private long endDateTime = -1;
        private boolean printOnClose = false;

        public PrintSessionReport(int fiscalizedCount, double sum, int sendInSmsOrEmailCount,
                                  long startDateTime, long endDateTime, boolean printOnClose) {
            this.fiscalizedCount = fiscalizedCount;
            this.sum = sum;
            this.sendInSmsOrEmailCount = sendInSmsOrEmailCount;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.printOnClose = printOnClose;
        }

        @Override
        public void run() {
            if (printerServiceConnected) {
                List<IPrintable> report = new ArrayList<>();

                if (isSessionOpen != null && isSessionOpen) {
                    if (startDateTime >= 0) {
                        if (endDateTime >= 0) {
                            report.add(new PrintableText(String.format(Locale.getDefault(), "Смена №%d открыта %s, закрыта %s",
                                    lastSessionNumber, formatSessionDateTime(this.startDateTime), formatSessionDateTime(this.endDateTime))));
                        } else {
                            report.add(new PrintableText(String.format(Locale.getDefault(), "Смена №%d открыта %s, смена открыта",
                                    lastSessionNumber, formatSessionDateTime(this.startDateTime))));
                        }
                    }
                    else {
                        report.add(new PrintableText(String.format(Locale.getDefault(), "Смена №%d открыта", lastSessionNumber)));
                    }

                    report.add(new PrintableText(String.format(Locale.getDefault(), "Всего чеков фискализировано %d", fiscalizedCount)));
                    report.add(new PrintableText(String.format(Locale.getDefault(), "Общая сумма %f", sum)));
                    report.add(new PrintableText(String.format(Locale.getDefault(), "Отправлено электронных (смс\\email) чеков %d", sendInSmsOrEmailCount)));
                } else {
                    report.add(new PrintableText("Смена в данный момент закрыта."));
                }

                try {
                    DeviceServiceConnector.getPrinterService()
                            .printDocument(DEFAULT_DEVICE_INDEX, new PrinterDocument(report.toArray(new IPrintable[report.size()])));
                } catch (DeviceServiceException e) {
                    e.printStackTrace();
                    Log.d(TAG_EVOTOR, "SessionPresenter; PrintSessionReport; error on print report:" + e.toString());

                    if (e instanceof DeviceNotFoundException) {
                        if (reconnectToPrinterServiceAttempts < 3) {
                            reconnectToPrinterServiceAttempts++;
                            Log.d(TAG_EVOTOR, "SessionPresenter; PrintSessionReport; try reconnect to printer service");
                            reconnect(fiscalizedCount, sum, sendInSmsOrEmailCount, startDateTime, endDateTime, false);
                        }
                        else {
                            Log.d(TAG_EVOTOR, "SessionPresenter; PrintSessionReport; reconnect to printer service attempts > 3");
                            reconnectToPrinterServiceAttempts = 0;
                        }
                    }
                }
            }
            else {
                if (printSessionReportAttemptsCount < 3) {
                    printSessionReportAttemptsCount++;

                    printerHandler.postDelayed(new PrintSessionReport(fiscalizedCount, sum, sendInSmsOrEmailCount,
                            startDateTime, endDateTime, false), 10 * 1000);
                }
                else {
                    Log.d(TAG_EVOTOR, "SessionPresenter; PrintSessionReport; print session attempts > 3");
                    printSessionReportAttemptsCount = 0;
                }
            }

            if (printOnClose) {
                //printQueue.clear();
                stoppingSession.set(false);
                printerHandler.post(printNext);

                if (timerTick != null) {
                    timerHandler.removeCallbacks(timerTick);
                    timerTick = null;
                }

                if (sessionChecker != null) {
                    sessionCheckerHandler.removeCallbacks(sessionChecker);
                    sessionChecker = null;
                }

                timerTick = new TimerTick();
                timerHandler.post(timerTick);

                sessionChecker = new SessionChecker();
                sessionCheckerHandler.postDelayed(sessionChecker, 2000);
            }
        }
    }

    private void reconnect(int fiscalizedCount, double sum, int sendInSmsOrEmailCount,
                           long startDateTime, long endDateTime, boolean printOnClose) {
        //printerServiceConnected = false;
        DeviceServiceConnector.startInitConnections(FApp.getContext());

        printerHandler.postDelayed(new PrintSessionReport(fiscalizedCount, sum, sendInSmsOrEmailCount,
                startDateTime, endDateTime, false), 10 * 1000);
    }*/

    private String formatSessionDateTime(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\\HH:mm:ss", Locale.getDefault());
        return format.format(new Date(millis));
    }

/*    class TimerTick implements Runnable {
        @SuppressLint("LongLogTag")
        @Override
        public void run() {
            Calendar current = Calendar.getInstance();
            //long startPoint = System.currentTimeMillis();

            if (isSessionOpen != null
                    && isSessionOpen
                    && (current.getTimeInMillis() - startDateTime) > 24 * 60 * 60 * 1000
                    && !isSendsError5.get()) {
                isSendsError5.set(true);
                ApiPresenter.getInstance().setError(5, "", "");
            }

            if (autoClose && !undefinedSession) {
                long delta = 0;

                // автозакрытие смены в определенное время
                if (autoCloseType != AUTO_CLOSE_AT_) {
                    delta = current.getTimeInMillis() - startDateTime;

                    //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; current - start = " + delta);

                    if (autoCloseType == AUTO_CLOSE_EVERY_) {
                        long periodTime = autoCloseEveryValue * 60 * 1000;

                        if (autoCloseEveryUnit == AUTO_CLOSE_EVERY_UNIT_HOUR)
                            periodTime *= 60;

                        delta = periodTime - delta;

                        *//*Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; auto close every (" + autoCloseEveryValue
                                + "/" + autoCloseEveryUnit + "); delta = " + delta);*//*
                    }
                    else if (autoCloseType == AUTO_CLOSE_EVERY_DAY) {
                        delta = 24 * 60 * 60 * 1000 - delta;
                        //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; AUTO_CLOSE_EVERY_DAY; delta: " + delta);
                    }
                }
                // автозакрытие смены через интервал
                else {
                    Calendar closeAt = Calendar.getInstance();
                    closeAt.set(Calendar.HOUR_OF_DAY, autoCloseAtHour);
                    closeAt.set(Calendar.MINUTE, autoCloseAtMinute);
                    closeAt.set(Calendar.SECOND, 0);
                    closeAt.set(Calendar.MILLISECOND, 0);

                    *//*Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; closeAt; current: " + current.getTimeInMillis()
                            + "; closeAt: " + closeAt.getTimeInMillis());*//*

                    if (current.getTimeInMillis() > closeAt.getTimeInMillis()) {
                        //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; add day to closeAt");
                        closeAt.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    *//*Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; closeAt; SessionPresenter.getInstance().getSessionTime(): "
                            + SessionPresenter.getInstance().getSessionTime());*//*

                    if (SessionPresenter.getInstance().getSessionTime()
                            + (closeAt.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) >= 24 * 60 * 60 * 1000) {
                        delta = -1;
                    }
                    else
                        delta = closeAt.getTimeInMillis() - current.getTimeInMillis();

                    //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; closeAt; delta: " + delta);

                    *//*if (delta >= 0)
                        delta = -1;
                    else
                        delta = Math.abs(delta);*//*

     *//*if (delta < 0) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        delta = current.getTimeInMillis() - calendar.getTimeInMillis();
                    }*//*

     *//*Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; auto close at "
                            + autoCloseAtHour + ":" + autoCloseAtMinute + "; delta = " + delta);*//*
                }

                // если время вышло, закрываем смену
                if (delta <= 0 && isSessionOpen != null && isSessionOpen && lastSessionNumber != null && startDateTime > 0) {
                    //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; stopping session");

                    //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; stoppingSession: " + stoppingSession.get());
                    if (!stoppingSession.get()) {
                        stoppingSession.set(true);
                        printerHandler.removeCallbacks(printNext);

                        //Log.d(TAG_EVOTOR, "SessionPresenter; TimerTick; dataHandler.post(new StopSession());");
                        dataHandler.post(new StopSession());
                    }
                }
                else {
                    if (isSessionOpen && lastSessionNumber >= 0 && startDateTime > 0) {
                        if (iMainView != null) iMainView.onSessionTimerTick(delta);
                        if (iMainView1 != null) iMainView1.onSessionTimerTick(delta);
                    }
                }
            }
            else {
                if (isSessionOpen && lastSessionNumber >= 0 && startDateTime > 0) {
                    if (iMainView != null)
                        iMainView.showSessionWholeTime(current.getTimeInMillis() - startDateTime);

                    if (iMainView1 != null)
                        iMainView1.showSessionWholeTime(current.getTimeInMillis() - startDateTime);
                }
            }

            timerHandler.postDelayed(timerTick, 500);
        }
    }

    private long getDeltaToClose() {
        long delta = -1;

        Calendar current = Calendar.getInstance();

        Log.d(TAG_EVOTOR, "SessionPresenter; getDeltaToClose; autoClose: " + autoClose + "; isSessionOpen: " + isSessionOpen);

        if (autoClose && isSessionOpen != null && isSessionOpen) {
            // автозакрытие смены в определенное время
            if (autoCloseType != AUTO_CLOSE_AT_) {
                delta = current.getTimeInMillis() - startDateTime;

                if (autoCloseType == AUTO_CLOSE_EVERY_) {
                    long periodTime = autoCloseEveryValue * 60 * 1000;

                    if (autoCloseEveryUnit == AUTO_CLOSE_EVERY_UNIT_HOUR)
                        periodTime *= 60;

                    Log.d(TAG_EVOTOR, "SessionPresenter; getDeltaToClose; AUTO_CLOSE_EVERY_ periodTime: " + periodTime + "; delta: " + delta);

                    delta = periodTime - delta;
                }
                else if (autoCloseType == AUTO_CLOSE_EVERY_DAY) {
                    Log.d(TAG_EVOTOR, "SessionPresenter; getDeltaToClose; AUTO_CLOSE_EVERY_DAY delta: " + delta);

                    delta = 24 * 60 * 60 * 1000 - delta;
                }
            }
            // автозакрытие смены через интервал
            else {
                Calendar closeAt = Calendar.getInstance();
                closeAt.set(Calendar.HOUR_OF_DAY, autoCloseAtHour);
                closeAt.set(Calendar.MINUTE, autoCloseAtMinute);
                closeAt.set(Calendar.SECOND, 0);
                closeAt.set(Calendar.MILLISECOND, 0);

                if (Calendar.getInstance().getTimeInMillis() > closeAt.getTimeInMillis()) {
                    closeAt.add(Calendar.DAY_OF_MONTH, 1);
                }

                Log.d(TAG_EVOTOR, "SessionPresenter; getDeltaToClose; AUTO_CLOSE_AT_ SessionPresenter.getInstance().getSessionTime(): "
                        + SessionPresenter.getInstance().getSessionTime() + "; closeAt.getTimeInMillis(): " + closeAt.getTimeInMillis()
                        + "; Calendar.getInstance().getTimeInMillis(): " + Calendar.getInstance().getTimeInMillis());

                delta = 24 * 60 * 60 * 1000 - (SessionPresenter.getInstance().getSessionTime()
                        + (closeAt.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()));
            }
        }

        return delta;
    }

    class SessionChecker implements Runnable {
        @Override
        public void run() {
            if (!stoppingSession.get() && !needInitSession.get()) {
                //Log.d(TAG_EVOTOR, "SessionPresenter; SessionChecker; isSessionOpen: " + isSessionOpen);

                if (isSessionOpen == null || !isSessionOpen) {
                    getSessionOpen();

                    //Log.d(TAG_EVOTOR, "SessionPresenter; SessionChecker; getSessionOpen(): " + isSessionOpen);
                    if (isSessionOpen != null && isSessionOpen) {
                        startDateTime = Calendar.getInstance().getTimeInMillis();
                        getSessionNumber();

                        //ApiPresenter.getInstance().openSession(startDateTime);

                        Prefs.getInstance().saveLong(KEY_SESSION_START, startDateTime);
                        Prefs.getInstance().saveLong(KEY_LAST_SESSION_NUMBER, lastSessionNumber);

                        Log.d(TAG_EVOTOR, "SessionPresenter; SessionChecker; startDateTime: " + startDateTime);
                        Log.d(TAG_EVOTOR, "SessionPresenter; SessionChecker; lastSessionNumber: " + lastSessionNumber);
                    }
                } *//*else *//**//*if (isSessionOpen != null && isSessionOpen)*//**//* {
                    getSessionOpen();

                    Log.d(TAG_EVOTOR, "SessionPresenter; SessionChecker; getSessionOpen(): " + isSessionOpen);

                    if (isSessionOpen != null && !isSessionOpen) {
                        Log.d(TAG_EVOTOR, "SessionPresenter; SessionChecker; stoppingSession: " + stoppingSession.get());
                        if (!stoppingSession.get()) {
                            stoppingSession.set(true);
                            printerHandler.removeCallbacks(printNext);

                            dataHandler.post(new StopSession());
                        }
                    }
                }*//*
            }

            sessionCheckerHandler.postDelayed(sessionChecker, SESSION_CHECK_INTERVAL);
        }
    }

    public void stopSessionFromBroadcast() {
        if (!stoppingSession.get()) {
            Log.d(TAG_EVOTOR, "SessionPresenter; stopSessionFromBroadcast; : stopping session");

            stoppingSession.set(true);

            printerHandler.removeCallbacks(printNext);
            timerHandler.removeCallbacks(timerTick);
            sessionCheckerHandler.removeCallbacks(sessionChecker);

            dataHandler.post(new OnStopSessionByEvent());
        }
    }

    public void stopSession() {
        if (!stoppingSession.get()) {
            stoppingSession.set(true);
            printerHandler.removeCallbacks(printNext);

            dataHandler.post(new StopSession());
        }
    }

    class Stop implements Runnable {
        @Override
        public void run() {
            if (statuses.size() > 0) {
                ApiPresenter.getInstance().setStatusesReceipt(new HashMap<>(statuses));
                statuses.clear();
            }

            if (!undefinedSession) {
                Prefs.getInstance().saveLong(KEY_LAST_SESSION_NUMBER, lastSessionNumber);
                Prefs.getInstance().saveLong(KEY_SESSION_START, startDateTime);
            }
            Prefs.getInstance().saveString(KEY_SESSION_DATA, new Gson().toJson(data));

            init = false;

            data.clear();
            printQueue.clear();
        }
    }

    class StopOnExit implements Runnable {
        @Override
        public void run() {
            if (statuses.size() > 0) {
                ApiPresenter.getInstance().setStatusesReceipt(new HashMap<>(statuses));
                statuses.clear();
            }

            if (!undefinedSession) {
                Prefs.getInstance().saveLong(KEY_LAST_SESSION_NUMBER, lastSessionNumber);
                Prefs.getInstance().saveLong(KEY_SESSION_START, startDateTime);
            }
            Prefs.getInstance().saveString(KEY_SESSION_DATA, new Gson().toJson(data));

            init = false;
        }
    }*/

    @SuppressLint("LongLogTag")
    public SessionPresenter() {
        dataHandlerThread = new HandlerThread("Fiscalizer:SessionData");
        dataHandlerThread.start();
        dataHandler = new Handler(dataHandlerThread.getLooper());

        printerHandlerThread = new HandlerThread("Fiscalizer:SessionPrinter");
        printerHandlerThread.start();
        printerHandler = new Handler(printerHandlerThread.getLooper());

        timerHandlerThread = new HandlerThread("Fiscalizer:SessionTimer");
        timerHandlerThread.start();
        timerHandler = new Handler(timerHandlerThread.getLooper());

        sessionCheckerHandlerThread = new HandlerThread("Fiscalizer:SessionOpenChecker");
        sessionCheckerHandlerThread.start();
        sessionCheckerHandler = new Handler(sessionCheckerHandlerThread.getLooper());

        checkInetHandlerThread = new HandlerThread("Fiscalizer:CheckInet");
        checkInetHandlerThread.start();
        checkInetHandler = new Handler(checkInetHandlerThread.getLooper());

        currentTheme = Prefs.getInstance().loadInt(CURRENT_THEME);
        if (currentTheme < 0) {
            currentTheme = 0;
            Prefs.getInstance().saveInt(CURRENT_THEME, currentTheme);
        }

        init();
    }

    public void init() {
        if (init) {
            init = true;
            return;
        }

        previousSessionStatus = Prefs.getInstance().loadBoolean(PREVIOUS_SESSION_STATUS);

        if (!TextUtils.isEmpty(Prefs.getInstance().loadString(KEY_SESSION_DATA))) {
            Gson gson = new Gson();
            data = gson.fromJson(Prefs.getInstance().loadString(KEY_SESSION_DATA), SessionStatisticData.class);
        } else {
            data = StatisticConsider.getEmptySessionData();
            if (SystemStateApi.isSessionOpened(EvoApp.getInstance().getApplicationContext())) {
                data.setSessionId(SystemStateApi.getLastSessionNumber(EvoApp.getInstance()));
            }
        }

        if (!TextUtils.isEmpty(Prefs.getInstance().loadString(LAST_OPEN_SESSION))) {
            dateLastOpenSession = LocalDateTime.parse(Prefs.getInstance().loadString(LAST_OPEN_SESSION));
        } else {
            dateLastOpenSession = null;
        }

        if (!TextUtils.isEmpty(Prefs.getInstance().loadString(LAST_CLOSE_SESSION))) {
            dateLastCloseSession = LocalDateTime.parse(Prefs.getInstance().loadString(LAST_CLOSE_SESSION));
        } else {
            dateLastCloseSession = null;
        }


        // инициализируем принтер
        /*DeviceServiceConnector.startInitConnections(FApp.getContext());
        DeviceServiceConnector.addConnectionWrapper(connectionWrapper);*/

        printReportOnClose = Prefs.getInstance().loadBoolean(PRINT_AFTER_CLOSE);
        printChecks = Prefs.getInstance().loadBoolean(PRINT_CHECKS);
        sendSms = Prefs.getInstance().loadBoolean(SEND_SMS);

        // если ключа автозыкрытия нет - первый запуск
        // ставим автозакрытие в true
        // тип автозакрытия каждые 24 часа
        if (!Prefs.getInstance().isKeyExist(AUTO_CLOSE)) {
            autoClose = true;
            Prefs.getInstance().saveBoolean(AUTO_CLOSE, true);

            autoCloseType = AUTO_CLOSE_EVERY_DAY;
            Prefs.getInstance().saveInt(AUTO_CLOSE_TYPE, autoCloseType);
        } else {
            autoClose = Prefs.getInstance().loadBoolean(AUTO_CLOSE);

            autoCloseType = Prefs.getInstance().loadInt(AUTO_CLOSE_TYPE);

            if (autoCloseType == -1) {
                autoCloseType = AUTO_CLOSE_EVERY_DAY;
                Prefs.getInstance().saveInt(AUTO_CLOSE_TYPE, autoCloseType);
            }

            if (autoClose) {
                if (autoCloseType == -1) {
                    autoCloseType = AUTO_CLOSE_EVERY_DAY;
                    Prefs.getInstance().saveInt(AUTO_CLOSE_TYPE, autoCloseType);
                }
            }
        }

        if (autoCloseType == -1)
            autoCloseType = Prefs.getInstance().loadInt(AUTO_CLOSE_TYPE);

        autoCloseEveryUnit = Prefs.getInstance().loadInt(AUTO_CLOSE_EVERY_UNIT);
        if (autoCloseEveryUnit == -1) {
            autoCloseEveryUnit = AUTO_CLOSE_EVERY_UNIT_MIN;
            Prefs.getInstance().saveInt(AUTO_CLOSE_EVERY_UNIT, autoCloseEveryUnit);
        }

        autoCloseEveryValue = Prefs.getInstance().loadInt(AUTO_CLOSE_EVERY_VALUE);
        if (autoCloseEveryValue == -1) {
            autoCloseEveryValue = 30;
            Prefs.getInstance().saveInt(AUTO_CLOSE_EVERY_VALUE, autoCloseEveryValue);
        }

        autoCloseAtHour = Prefs.getInstance().loadInt(AUTO_CLOSE_HOUR);
        if (autoCloseAtHour == -1) {
            autoCloseAtHour = 14;
            Prefs.getInstance().saveInt(AUTO_CLOSE_HOUR, autoCloseAtHour);
        }

        autoCloseAtMinute = Prefs.getInstance().loadInt(AUTO_CLOSE_MINUTE);
        if (autoCloseAtMinute == -1) {
            autoCloseAtMinute = 0;
            Prefs.getInstance().saveInt(AUTO_CLOSE_MINUTE, autoCloseAtMinute);
        }

        defaultSmsService = Prefs.getInstance().loadString(DEFAULT_SMS_SERVICE);
        smsServiceInit = Prefs.getInstance().loadBoolean(SMS_SERVICE_INIT);
        /*sendSms = Prefs.getInstance().loadBoolean(SEND_SMS);*/ // Изначально не было.

        defaultEmailService = Prefs.getInstance().loadString(DEFAULT_EMAIL_SERVICE);
        emailServiceInit = Prefs.getInstance().loadBoolean(EMAIL_SERVICE_INIT);
        sendEmail = Prefs.getInstance().loadBoolean(SEND_EMAIL); //Изначально не было.

        /*if (timerTick != null) {
            timerHandler.removeCallbacks(timerTick);
            timerTick = null;
        }

        if (sessionChecker != null) {
            sessionCheckerHandler.removeCallbacks(sessionChecker);
            sessionChecker = null;
        }*/

        lastSessionNumber = Prefs.getInstance().loadLong(KEY_LAST_SESSION_NUMBER);
        startDateTime = Prefs.getInstance().loadLong(KEY_SESSION_START);
        /*data = new Gson().fromJson(Prefs.getInstance().loadString(KEY_SESSION_DATA), new TypeToken<ArrayList<ReceiptItem>>(){}.getType());
        if (data == null) data = new ArrayList<>();

        Log.d(TAG_EVOTOR, "SessionPresenter; ctr; lastSessionNumber: " + lastSessionNumber + "; startDateTime: " + startDateTime
                + "data: " + data.toString());

        // если смена октрыта
        if (getSessionOpen()) {
            Log.d(TAG_EVOTOR, "SessionPresenter; ctr; session is open, check saved session");
            // если id последней сохраненной смены не соттветствует id открытой смены, удаляем данные посл сохраненной смены
            Long currentSessionNumber = SystemStateApi.getLastSessionNumber(FApp.getContext());
            if (currentSessionNumber != null && (currentSessionNumber.longValue() != lastSessionNumber.longValue() || startDateTime <= 0)) {
                Log.d(TAG_EVOTOR, "SessionPresenter; ctr; current session (" + currentSessionNumber
                        +  ") != saved session (" + lastSessionNumber + "), clear saved data");

                //undefinedSessionDetected.postValue(true);

                undefinedSession = true;

                lastSessionNumber = currentSessionNumber;
                startDateTime = -1;
                endDateTime = -1;
                statuses.clear();
                isSendsError5.set(false);
                needInitSession.set(false);

                Prefs.getInstance().removeKey(KEY_LAST_SESSION_NUMBER);
                Prefs.getInstance().removeKey(KEY_SESSION_START);
                Prefs.getInstance().removeKey(KEY_SESSION_DATA);
            }

            if (!undefinedSession) {
                getSessionNumber();
                Prefs.getInstance().saveLong(KEY_LAST_SESSION_NUMBER, lastSessionNumber);
            }
        }

        Log.d(TAG_EVOTOR, "SessionPresenter; ctr; start timer");
        timerTick = new TimerTick();
        timerHandler.post(timerTick);

        Log.d(TAG_EVOTOR, "SessionPresenter; ctr; start session checker");
        sessionChecker = new SessionChecker();
        sessionCheckerHandler.post(sessionChecker);

        dataHandler.post(new UpdateStatistics());

        kktSerialNumber = Prefs.getInstance().loadString(KEY_KKT_SERIAL_NUMBER);
        String current = KktApi.receiveKktSerialNumber(FApp.getContext());

        if (!TextUtils.isEmpty(current) && !current.equals(kktSerialNumber)) {
            kktSerialNumber = current;
            Prefs.getInstance().saveString(KEY_KKT_SERIAL_NUMBER, kktSerialNumber);
        }

        kktRegNumber = Prefs.getInstance().loadString(KEY_KKT_REG_NUMBER);
        current = KktApi.receiveKktRegNumber(FApp.getContext());

        if (!TextUtils.isEmpty(current) && !current.equals(kktRegNumber)) {
            kktRegNumber = current;
            Prefs.getInstance().saveString(KEY_KKT_REG_NUMBER, kktRegNumber);
        }

        fsSerialNumber = Prefs.getInstance().loadString(KEY_FS_SERIAL_NUMBER);
        current = KktApi.getFsSerialNumber(FApp.getContext());

        if (!TextUtils.isEmpty(current) && !current.equals(fsSerialNumber)) {
            fsSerialNumber = current;
            Prefs.getInstance().saveString(KEY_FS_SERIAL_NUMBER, fsSerialNumber);
        }

        checkInetConnection();*/
    }

    /*public MutableLiveData<Integer> getNotifyCount() {
        return notifyCount;
    }

    public MutableLiveData<Boolean> getUndefinedSessionDetected() {
        return undefinedSessionDetected;
    }

    public String getKktSerialNumber() {
        return kktSerialNumber;
    }

    public String getKktRegNumber() {
        return kktRegNumber;
    }

    public String getFsSerialNumber() {
        return fsSerialNumber;
    }*/

    public static SessionPresenter getInstance() {
        if (instance == null) {
            instance = new SessionPresenter();
        }
        return instance;
    }

    /*public void setiMainView(IMainView iMainView) {
        this.iMainView = iMainView;

        if (iMainView != null) {
            Log.d(TAG_EVOTOR, "SessionPresenter; setiMainView; dataHandler.post(new UpdateStatistics());");
            dataHandler.post(new UpdateStatistics());
        }
    }*/

    public void setiMainView1(IMainView1 iMainView1) {
        this.iMainView1 = iMainView1;

        /*if (iMainView1 != null)
            dataHandler.post(new UpdateStatistics());*/
    }

    public boolean isPrintReportOnClose() {
        Log.d(TAG, "printReportOnClose: " + printReportOnClose);
        return printReportOnClose;
    }

    public void setPrintReportOnClose(boolean printReportOnClose) {
        if (this.printReportOnClose != printReportOnClose) {
            this.printReportOnClose = printReportOnClose;
            Prefs.getInstance().saveBoolean(PRINT_AFTER_CLOSE, printReportOnClose);
        }
    }

    public boolean isPrintChecks() {
        Log.d(TAG, "printChecks: " + printChecks);
        return printChecks;
    }

    public void setPrintChecks(boolean printChecks) {
        if (this.printChecks != printChecks) {
            this.printChecks = printChecks;
            Prefs.getInstance().saveBoolean(PRINT_CHECKS, printChecks);
        }
    }

    public boolean isSendSms() {
        Log.d(TAG, "sendSms: " + sendSms);
        return sendSms;
    }

    public void setSendSms(boolean sendSms) {
        if (this.sendSms != sendSms) {
            this.sendSms = sendSms;
            Prefs.getInstance().saveBoolean(SEND_SMS, sendSms);
        }
    }

    public boolean isSendEmail() {
        Log.d(TAG, "sendEmail: " + sendEmail);
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        if (this.sendEmail != sendEmail) {
            this.sendEmail = sendEmail;
            Prefs.getInstance().saveBoolean(SEND_EMAIL, sendEmail);
        }
    }

    public boolean isAutoClose() {
        return autoClose;
    }

    public void setAutoClose(boolean autoClose) {
        Log.d(TAG, "SessionPresenter; setAutoClose; this.autoClose: " + this.autoClose + "; autoClose: " + autoClose);

        if (this.autoClose != autoClose) {
            this.autoClose = autoClose;
            Prefs.getInstance().saveBoolean(AUTO_CLOSE, autoClose);
        }
    }

    public int getAutoCloseType() {
//        Log.d(TAG, "autoCloseType: " + autoCloseType);
        return autoCloseType;
    }

    public void setAutoCloseType(int autoCloseType) {
        Log.d(TAG, "SessionPresenter; autoCloseType: " + autoCloseType);

        if (this.autoCloseType != autoCloseType) {
            Log.d(TAG, "SessionPresenter; this.autoCloseType: " + this.autoCloseType + "; autoCloseType: " + autoCloseType);

            this.autoCloseType = autoCloseType;
            Prefs.getInstance().saveInt(AUTO_CLOSE_TYPE, autoCloseType);

            if (autoCloseType == -1) {
                if (autoClose) {
                    autoClose = false;
                    Prefs.getInstance().saveBoolean(AUTO_CLOSE, autoClose);
                }
            } else {
                if (!autoClose) {
                    autoClose = true;
                    Prefs.getInstance().saveBoolean(AUTO_CLOSE, autoClose);
                }
            }
        } else {
            if (!autoClose) {
                autoClose = true;
                Prefs.getInstance().saveBoolean(AUTO_CLOSE, autoClose);
            }
        }
    }

    public int getAutoCloseEveryUnit() {
        return autoCloseEveryUnit;
    }

    public void setAutoCloseEveryUnit(int autoCloseEveryUnit) {
        if (this.autoCloseEveryUnit != autoCloseEveryUnit) {
            this.autoCloseEveryUnit = autoCloseEveryUnit;
            //SessionPresenter.getInstance().setAutoCloseEveryUnit(autoCloseEveryUnit);
            Prefs.getInstance().saveInt(AUTO_CLOSE_EVERY_UNIT, autoCloseEveryUnit);

            if (iMainView1 != null) iMainView1.updateEveryTitle();
        }
    }

    public int getAutoCloseEveryValue() {
        return autoCloseEveryValue;
    }

    public void setAutoCloseEveryValue(int autoCloseEveryValue) {
        if (this.autoCloseEveryValue != autoCloseEveryValue) {
            this.autoCloseEveryValue = autoCloseEveryValue;
            Prefs.getInstance().saveInt(AUTO_CLOSE_EVERY_VALUE, autoCloseEveryValue);

            if (iMainView1 != null) iMainView1.updateEveryTitle();
        }
    }

    public int getAutoCloseAtHour() {
        return autoCloseAtHour;
    }

    public void setAutoCloseAtHour(int autoCloseAtHour) {
        if (this.autoCloseAtHour != autoCloseAtHour) {
            this.autoCloseAtHour = autoCloseAtHour;
            Prefs.getInstance().saveInt(AUTO_CLOSE_HOUR, autoCloseAtHour);

            if (iMainView1 != null) iMainView1.updateAtTitle();
        }
    }

    public int getAutoCloseAtMinute() {
        return autoCloseAtMinute;
    }

    public void setAutoCloseAtMinute(int autoCloseAtMinute) {
        if (this.autoCloseAtMinute != autoCloseAtMinute) {
            this.autoCloseAtMinute = autoCloseAtMinute;
            Prefs.getInstance().saveInt(AUTO_CLOSE_MINUTE, autoCloseAtMinute);

            if (iMainView1 != null) iMainView1.updateAtTitle();
        }
    }

    public String getDefaultSmsService() {
        Log.d(TAG, "defaultSmsService: " + defaultSmsService);
        return defaultSmsService;
    }

    public void setDefaultSmsService(String defaultSmsService) {
        this.defaultSmsService = defaultSmsService;
        Prefs.getInstance().saveString(DEFAULT_SMS_SERVICE, defaultSmsService);
    }

  /*  public boolean isSmsServiceInit() {
        return smsServiceInit;
    }

    public void setSmsServiceInit(boolean smsServiceInit) {
        this.smsServiceInit = smsServiceInit;
        Prefs.getInstance().saveBoolean(SMS_SERVICE_INIT, smsServiceInit);
    }*/

    public String getDefaultEmailService() {
        Log.d(TAG, "defaultEmailService: " + defaultEmailService);
        return defaultEmailService;
    }

    public void setDefaultEmailService(String defaultEmailService) {
        this.defaultEmailService = defaultEmailService;
        Prefs.getInstance().saveString(DEFAULT_EMAIL_SERVICE, defaultEmailService);
    }

  /*  public boolean isEmailServiceInit() {
        return emailServiceInit;
    }

    public void setEmailServiceInit(boolean emailServiceInit) {
        this.emailServiceInit = emailServiceInit;
        Prefs.getInstance().saveBoolean(EMAIL_SERVICE_INIT, smsServiceInit);
    }*/

    public boolean getPreviousSessionStatus() {
        return previousSessionStatus;
    }

    public void setPreviousSessionStatus(Boolean isOpen) {
        previousSessionStatus = isOpen;
        Prefs.getInstance().saveBoolean(PREVIOUS_SESSION_STATUS, isOpen);
    }

    public LocalDateTime getDateLastOpenSession() {
        return dateLastOpenSession;
    }

    public void setDateLastOpenSession(LocalDateTime dateTimeOpen) {
        dateLastOpenSession = dateTimeOpen;
        Prefs.getInstance().saveString(LAST_OPEN_SESSION, dateLastOpenSession.toString());
    }

    public LocalDateTime getDateLastCloseSession() {
        return dateLastCloseSession;
    }

    public void setDateLastCloseSession(LocalDateTime dateTimeClose) {
        dateLastCloseSession = dateTimeClose;
        Prefs.getInstance().saveString(LAST_CLOSE_SESSION, dateLastCloseSession.toString());
    }

    public SessionStatisticData getSessionData() {
        return data;
    }

    public void setIstatisticDisplayUpdate(StatisticDisplayUpdate updater) {
        IstatisticDisplayUpdate = updater;
    }

    public void setSessionStatisticData(SessionStatisticData data) {
//        Log.d(TAG+"_update_session_data", data.toString() + "this.data id: " + this.data.getSessionId());

        if (!this.data.equals(data)) {
            Log.d(TAG + "_update_session_data", "Зашли в If-else");
            this.data = data;
            Gson gson = new Gson();
            Prefs.getInstance().saveString(KEY_SESSION_DATA, gson.toJson(data));
            if (IstatisticDisplayUpdate != null) {
                IstatisticDisplayUpdate.updateView(data);
            }
        }
    }

    /*private boolean getSessionOpen() {
        isSessionOpen = SystemStateApi.isSessionOpened(FApp.getContext());

        //Log.d(TAG_EVOTOR, "SessionPresenter; getSessionOpen; SystemStateApi.isSessionOpened(FApp.getContext()): " + isSessionOpen);

        if (isSessionOpen == null) isSessionOpen = false;
        return isSessionOpen;
    }

    private long getSessionNumber() {
        lastSessionNumber = SystemStateApi.getLastSessionNumber(FApp.getContext());
        if (lastSessionNumber == null) lastSessionNumber = -1L;

        return lastSessionNumber;
    }

    public void onGetReceipt(ReceiptsResponse response) {
        Log.d(TAG_EVOTOR, "SessionPresenter; onGetReceipt; response.data : " + response.data.toString());

        dataHandler.post(new AddDataToQueue(response.data));
    }*/

    public long getSessionTime() {
        if (isSessionOpen != null && isSessionOpen)
            return Calendar.getInstance().getTimeInMillis() - startDateTime;
        else
            return -1;
    }

    /*public void printSessionReport() {
        dataHandler.post(new FormatDataForSessionReport());
    }

    private void print(ReceiptItem item) {
        Log.d(TAG_EVOTOR, "SessionPresenter; printNext; print #" + item.id);

        printing = true;

        BigDecimal difference_summ;
        BigDecimal receiptDiscount = new BigDecimal(item.checkDiscount);
        BigDecimal sum_price_with_discount = BigDecimal.ZERO;
        List<Position> list = new ArrayList<>();

        for (int i = 0; i < item.goods.size(); i++) {
            BigDecimal price_without_discount = new BigDecimal(item.goods.get(i).price);
            BigDecimal discount = new BigDecimal(item.goods.get(i).discount);
            BigDecimal quantity = new BigDecimal(item.goods.get(i).quantity);
            BigDecimal price_with_discount = price_without_discount.multiply(new BigDecimal("100").subtract(discount).divide(new BigDecimal("100")));
            sum_price_with_discount = sum_price_with_discount.add(price_with_discount.multiply(quantity));

            Position.Builder position = Position.Builder.newInstance(UUID.randomUUID().toString(), item.goods.get(i).productUUID,
                    item.goods.get(i).name, item.goods.get(i).measureName,
                    Integer.parseInt(item.goods.get(i).measurePrecision), price_without_discount, quantity);

            if (!discount.equals(BigDecimal.ZERO)) {
                position.setPriceWithDiscountPosition(price_with_discount);
            }

            switch (*//*Integer.parseInt(item.goods.get(i).type.number)*//*item.goods.get(i).type.number) {
                case 0:
                    position.toNormal();
                    break;
                case 1:
                    position.toService();
                    break;
            }

            switch (item.goods.get(i).nds) {
                case "0.00":
                    position.setTaxNumber(TaxNumber.NO_VAT);
                    break;
                case "18.00":
                    position.setTaxNumber(TaxNumber.VAT_18);
                    break;
            }

            list.add(position.build());
        }

        if (!receiptDiscount.equals(BigDecimal.ZERO)) {
            difference_summ = sum_price_with_discount.multiply(receiptDiscount.divide(new BigDecimal("100")));
            sum_price_with_discount = sum_price_with_discount.multiply(new BigDecimal("100").subtract(receiptDiscount).divide(new BigDecimal("100")));
        } else {
            difference_summ = BigDecimal.ZERO;
        }

        HashMap<Payment, BigDecimal> payments = new HashMap<>();
        PaymentSystem PaymentSystem = new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424");
        PaymentPerformer PaymentPerformer = new PaymentPerformer(PaymentSystem, null, null, null, null);
        payments.put(new Payment(UUID.randomUUID().toString(), sum_price_with_discount, PaymentSystem, PaymentPerformer, null, null, null), sum_price_with_discount);
        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null,
                null, isPrintChecks(), null, null); // пред последний параметр отвечает за печать чека на кассе
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(printGroup, list, payments, new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>());
        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);

        boolean sendInSms = false;
        boolean sendInEmail = false;

        String phone = null;
        if (isSendSms() && defaultSmsService.equals(EVOTOR_SERVICE))
            phone = item.phone;

        if (!TextUtils.isEmpty(phone) || (isSendSms() && defaultSmsService.equals(SOFT_VILLAGE_SERVICE)))
            sendInSms = true;

        if (!TextUtils.isEmpty(item.email))
            sendInEmail = true;

        boolean finalSendInSms = sendInSms;
        boolean finalSendInEmail = sendInEmail;
        new PrintSellReceiptCommand(listDocs, null, phone, item.email, difference_summ,
                null, null, item.userUUID).process(FApp.getContext(), new IntegrationManagerCallback() {
            @SuppressLint("LongLogTag")
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                if (isSessionOpen == null || !isSessionOpen) {
                    isSessionOpen = true;
                    //lastSessionNumber = -1L;
                    startDateTime = Calendar.getInstance().getTimeInMillis();
                    Prefs.getInstance().saveLong(KEY_SESSION_START, startDateTime);

                    getSessionNumber();
                    Prefs.getInstance().saveLong(KEY_LAST_SESSION_NUMBER, lastSessionNumber);

                    //ApiPresenter.getInstance().openSession(startDateTime);

                    Log.d(TAG_EVOTOR, "SessionPresenter; printNext; open new session; startDateTime: " + startDateTime);
                }
                else {
                    Log.d(TAG_EVOTOR, "SessionPresenter; printNext; current session startDateTime: " + startDateTime);
                }

                if (needInitSession.get())
                    needInitSession.set(false);

                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    PrintReceiptCommandResult receiptCommandResult = PrintReceiptCommandResult.create(integrationManagerFuture.getResult().getData());

                    switch (result.getType()) {
                        case OK:
                            ReceiptStatus status = new ReceiptStatus();
                            status.id = item.id;
                            status.status = 1;

                            if (receiptCommandResult != null) {
                                status.number = receiptCommandResult.getReceiptNumber();
                                status.uuid = receiptCommandResult.getReceiptUuid();
                            }

                            status.fsSerialNumber = fsSerialNumber;

                            if (receiptCommandResult != null) {
                                Cursor<FiscalReceipt> cursor = ReceiptApi.getFiscalReceipts(FApp.getContext(), receiptCommandResult.getReceiptUuid());

                                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                                    status.documentNumber = String.valueOf(cursor.getValue().getDocumentNumber());
                                    status.fiscalIdentifier = String.valueOf(cursor.getValue().getFiscalIdentifier());
                                }

                                if (cursor != null)
                                    cursor.close();
                            }

                            status.smsFlag = sendSms && defaultSmsService.equals(SOFT_VILLAGE_SERVICE);
                            status.emailFlag = sendEmail && defaultEmailService.equals(SOFT_VILLAGE_SERVICE);

                            statuses.put(item.id, status);

                            Log.d(TAG_EVOTOR, "SessionPresenter; printNext; print result OK, update data item");
                            dataHandler.post(new UpdateDataItem(item.id, true, finalSendInSms, finalSendInEmail));

                            printQueue.remove(0);
                            printing = false;
                            printerHandler.post(printNext);

                            break;
                        case ERROR:
                            status = new ReceiptStatus();
                            status.id = item.id;
                            status.status = 0;

                            if (receiptCommandResult != null) {
                                status.number = receiptCommandResult.getReceiptNumber();
                                status.uuid = receiptCommandResult.getReceiptUuid();
                            }

                            status.fsSerialNumber = fsSerialNumber;

                            if (receiptCommandResult != null) {
                                Cursor<FiscalReceipt> cursor = ReceiptApi.getFiscalReceipts(FApp.getContext(), receiptCommandResult.getReceiptUuid());

                                if (cursor != null && cursor.getCount() > 0) {
                                    status.documentNumber = String.valueOf(cursor.getLong(cursor.getColumnIndex("DOCUMENT_NUMBER")));
                                    status.fiscalIdentifier = String.valueOf(cursor.getLong(cursor.getColumnIndex("FISCAL_IDENTIFIER")));
                                }

                                if (cursor != null)
                                    cursor.close();
                            }

                            status.smsFlag = sendSms && defaultSmsService.equals(SOFT_VILLAGE_SERVICE);
                            status.emailFlag = sendEmail && defaultEmailService.equals(SOFT_VILLAGE_SERVICE);

                            statuses.put(item.id, status);

                            Bundle data = result.getError().getData();
                            String dataValue = "";

                            if (data != null) {
                                for (String key : data.keySet()) {
                                    if (data.get(key) != null)
                                        dataValue += key + ":" + data.get(key).toString() + ",";
                                }
                            }

                            ApiService.getInstance().getApi().setError(result.getError().getCode(),
                                    result.getError().getMessage(), dataValue);

                            // время сессии истекло
                            if (result.getError().getCode() == ERROR_CODE_SESSION_TIME_EXPIRED) {
                                if (autoClose) {
                                    Log.d(TAG_EVOTOR,
                                            "SessionPresenter; printNext; print result ERROR_CODE_SESSION_TIME_EXPIRED, autoClose = true, stop session");
                                    *//*dataHandler.post(new StopSession());
                                    printing = false;*//*

                                    if (!stoppingSession.get()) {
                                        stoppingSession.set(true);
                                        printerHandler.removeCallbacks(printNext);

                                        dataHandler.post(new StopSession());
                                    }

                                    printing = false;
                                }
                                else {
                                    Log.d(TAG_EVOTOR,
                                            "SessionPresenter; printNext; print result ERROR_CODE_SESSION_TIME_EXPIRED, autoClose = false, send error 4");
                                    //ApiService.getInstance().getApi().setError(4, "", "");
                                    printing = false;
                                }
                            }
                            else {
                                Log.d(TAG_EVOTOR,"SessionPresenter; printNext; result.getError().getCode(): " + result.getError().getCode());

                                printing = false;
                                printerHandler.post(printNext);
                            }

                            break;
                    }

                    if (statuses.size() >= 10 || printQueue.size() <= 0) {
                        if (statuses.size() > 0) {
                            ApiPresenter.getInstance().setStatusesReceipt(new HashMap<>(statuses));
                            statuses.clear();
                        }
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                    Log.d(TAG_EVOTOR,"SessionPresenter; printNext; exception: " + e.toString());
                }
            }
        });
    }

    *//*public void onGetReceiptTemp(ReceiptsResponse response) {
        data.addAll(response.data);

        SessionViewData result = new SessionViewData();
        result.number = lastSessionNumber;
        result.startDateTime = startDateTime;

        double sum = 0;
        int checkCount = 0;
        int sendInSmsEmail = 0;

        for (int i = 0; i < data.size(); i++) {
            boolean contains = false;

            for (int j = 0; j < result.fromSources.size(); j++) {
                if (result.fromSources.get(j).id == data.get(i).paymentSystem) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                result.fromSources.add(new PaymentSystemCheckItem(data.get(i).paymentSystem));
        }

        for (int i = 0; i < data.size(); i++) {
            if (*//**//*data.get(i).fiscalized*//**//*true) {
                checkCount++;

                if (data.get(i).sendInSms || data.get(i).sendInEmail)
                    sendInSmsEmail++;

                for (int j = 0; j < data.get(i).goods.size(); j++) {
                    sum += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                }

                for (int k = 0; k < result.fromSources.size(); k++) {
                    if (result.fromSources.get(k).id == data.get(i).paymentSystem) {
                        result.fromSources.get(k).count++;

                        for (int j = 0; j < data.get(i).goods.size(); j++) {
                            result.fromSources.get(k).sum
                                    += Double.parseDouble(data.get(i).goods.get(j).price) * Integer.parseInt(data.get(i).goods.get(j).quantity);
                        }
                    }
                }
            }
        }

        result.sum = sum;
        result.checksCount = checkCount;
        result.sendInSms = sendInSmsEmail;

        if (iMainView != null) iMainView.updateStatistic(result);
    }*//*

    public void stop() {
        printerHandler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacksAndMessages(null);
        sessionCheckerHandler.removeCallbacksAndMessages(null);

        dataHandler.post(new Stop());
    }

    public void stopOnExit() {
        printerHandler.removeCallbacksAndMessages(null);
        timerHandler.removeCallbacksAndMessages(null);
        sessionCheckerHandler.removeCallbacksAndMessages(null);

        dataHandler.post(new StopOnExit());
    }*/

    public MutableLiveData<Boolean> getDismissAutoCloseDialog() {
        return dismissAutoCloseDialog;
    }

    /*public void checkInetConnection() {
        checkInetHandler.removeCallbacksAndMessages(null);
        checkInetHandler.post(new CheckInet());
    }*/

    public AtomicBoolean getHasConnection() {
        return hasConnection;
    }

    public AnimatorSet getNoSignalAnimator() {
        return noSignalAnimator;
    }

    public void toggleTheme() {
        currentTheme++;

        if (currentTheme > 1)
            currentTheme = 0;

        Prefs.getInstance().saveInt(CURRENT_THEME, currentTheme);
    }

    public int getCurrentTheme() {
        return currentTheme;
    }

    /*public void updateStatistics() {
        dataHandler.post(new UpdateStatistics());
    }*/
}
