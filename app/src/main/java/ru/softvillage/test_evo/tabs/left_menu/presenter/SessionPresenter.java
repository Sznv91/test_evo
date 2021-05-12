
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
import ru.softvillage.test_evo.tabs.left_menu.IMainView;
import ru.softvillage.test_evo.tabs.left_menu.IMainView1;
import ru.softvillage.test_evo.tabs.left_menu.util.Prefs;
import ru.softvillage.test_evo.utils.StatisticConsider;

import static ru.softvillage.test_evo.EvoApp.TAG;

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
    private AtomicBoolean needInitSession = new AtomicBoolean(false);

    private volatile boolean printing = false;

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

    private IMainView iMainView;
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
    public static int THEME_LIGHT = 0;
    public static int THEME_DARK = 1;
    private int currentTheme = 0;

    private String formatSessionDateTime(long millis) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy\\HH:mm:ss", Locale.getDefault());
        return format.format(new Date(millis));
    }

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
        sendSms = Prefs.getInstance().loadBoolean(SEND_SMS); // Изначально не было.

        defaultEmailService = Prefs.getInstance().loadString(DEFAULT_EMAIL_SERVICE);
        emailServiceInit = Prefs.getInstance().loadBoolean(EMAIL_SERVICE_INIT);
        sendEmail = Prefs.getInstance().loadBoolean(SEND_EMAIL); //Изначально не было.

        lastSessionNumber = Prefs.getInstance().loadLong(KEY_LAST_SESSION_NUMBER);
        startDateTime = Prefs.getInstance().loadLong(KEY_SESSION_START);

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

    public void setiMainView(IMainView iMainView) {
        this.iMainView = iMainView;

       /* if (iMainView != null) {
            Log.d(TAG_EVOTOR, "SessionPresenter; setiMainView; dataHandler.post(new UpdateStatistics());");
            dataHandler.post(new UpdateStatistics());
        }*/
    }

    public void setiMainView1(IMainView1 iMainView1) {
        this.iMainView1 = iMainView1;
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

    public String getDefaultEmailService() {
        Log.d(TAG, "defaultEmailService: " + defaultEmailService);
        return defaultEmailService;
    }

    public void setDefaultEmailService(String defaultEmailService) {
        this.defaultEmailService = defaultEmailService;
        Prefs.getInstance().saveString(DEFAULT_EMAIL_SERVICE, defaultEmailService);
    }

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

    public long getSessionTime() {
        if (isSessionOpen != null && isSessionOpen)
            return Calendar.getInstance().getTimeInMillis() - startDateTime;
        else
            return -1;
    }

    public void toggleTheme() {
        currentTheme++;

        if (currentTheme > 1)
            currentTheme = 0;

        Prefs.getInstance().saveInt(CURRENT_THEME, currentTheme);
        if (IstatisticDisplayUpdate != null) {
            IstatisticDisplayUpdate.updateTheme();
        }
    }

    public int getCurrentTheme() {
        return currentTheme;
    }
}
