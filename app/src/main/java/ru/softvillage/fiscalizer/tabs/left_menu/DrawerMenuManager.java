package ru.softvillage.fiscalizer.tabs.left_menu;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Calendar;
import java.util.Locale;

import ru.softvillage.fiscalizer.BuildConfig;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.services.ForegroundServiceDispatcher;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.AboutDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.AlertDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.ExitDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.NotExistSmsServerDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.SetCloseAtDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.SetCloseEveryDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

import static ru.softvillage.fiscalizer.EvoApp.TAG;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_AT_;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_DAY;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_UNIT_HOUR;

public class DrawerMenuManager<T extends AppCompatActivity> implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        ExitDialog.IExitDialog,
        IMainView1 {
    //ru.evotor.framework.kkt.api Посмотреть внимательней
    // KktApi
    public final static String EVOTOR_SERVICE = "Evotor";
    public final static String SOFT_VILLAGE_SERVICE = "Soft-Village";

    private AppCompatActivity activity;
    private DrawerLayout drawer;
    private ConstraintLayout drawerMenu,
            main_menu,
            about_menu,
            about_button,
            about_menu_feedback,
            about_menu_rate_the_app,
            about_menu_privacy_policy,
            about_menu_user_agreement,
            about_menu_licenses,
            about_menu_data_protection;
    ;

    private ActionBarDrawerToggle toggle;
    private ActionBar mActionBar;
    private boolean mToolBarNavigationListenerIsRegistered = false;

    /*private ImageView menu;*/
    private ImageView changeTheme,
            iconExit,

    icon_about,
            about_menu_image_back,
            about_menu_icon_feedback,
            about_menu_icon_rate_the_app,
            about_menu_icon_privacy_policy,
            about_menu_icon_user_agreement,
            about_menu_icon_licenses,
            about_menu_icon_data_protection;
    private View about_menu_divider_title;

    private FrameLayout changeThemeBottom,
            about_menu_button_back;

    private TextView titleEvery,

    title_about,
            about_menu_title_about,
            about_menu_version,
            about_menu_title_feedback,
            about_menu_title_rate_the_app,
            about_menu_title_privacy_policy,
            about_menu_title_user_agreement,
            about_menu_title_licenses,
            about_menu_title_data_protection;
    private TextView titleAt;

    private TextView titleExit;
    private TextView version;

    private Drawable dIconAbout,
            dIcon_about_menu_button_back,
            dIcon_about_menu_icon_feedback,
            dIcon_about_menu_icon_rate_the_app,
            dIcon_about_menu_icon_privacy_policy,
            dIcon_about_menu_icon_user_agreement,
            dIcon_about_menu_icon_licenses,
            dIcon_about_menu_icon_data_protection;

    /**
     * Переключатели
     */
    private SwitchCompat printReportAfterClose;
    private SwitchCompat printReceipts;
    private SwitchCompat everyDay;
    private SwitchCompat every;
    private SwitchCompat at;
    private SwitchCompat sendSmsEvotor;
    private SwitchCompat sendEmailSv;
    private SwitchCompat sendEmailEvotor;
    private SwitchCompat sendSmsSv;

    /**
     * Сворачивающиеся пункты меню
     */
    private LinearLayout layoutAutoClose;
    private LinearLayout layoutSendSms;
    private LinearLayout layoutSendEmail;

    private ExpandableLayout expandableAutoClose;
    private ImageView arrowAutoClose;
    private ExpandableLayout expandableSendSms;
    private ImageView arrowSendSms;
    private ExpandableLayout expandableSendEmail;
    private ImageView arrowSendEmail;

    /**
     * Элементы в которых необходимо только изменить цвет текста при смене темы
     */
    private LinearLayout main;
    private /*ConstraintLayout*/ Toolbar toolbar;
    private TextView titleParams;
    private View dividerPrintAfterClose;
    private View dividerPrintReceipts;
    private TextView titleAutoClose;
    private View dividerAutoClose;
    private TextView titleSendSms;
    private View dividerSendSms;
    private TextView titleSendEmail;
    private View dividerExit;

    public DrawerMenuManager(T activity) {
        this.activity = activity;
        initMenu();
        SessionPresenter.getInstance().setiMainView1(this);
        SessionPresenter.getInstance().setDrawerMenuManager(this);
    }

    public void setActivity(T activity) {
        this.activity = activity;
        initMenu();
        SessionPresenter.getInstance().setiMainView1(this);
        SessionPresenter.getInstance().setDrawerMenuManager(this);
    }

    private void initMenu() {
        main_menu = activity.findViewById(R.id.main_menu);
        about_menu = activity.findViewById(R.id.about_menu);
        /**
         * Бинд элементов которые ничего не делают, но надо поменять цвет текста при смене темы.
         */
        main = activity.findViewById(R.id.main);
        toolbar = activity.findViewById(R.id.toolbar);
        titleParams = activity.findViewById(R.id.titleParams);
        dividerPrintAfterClose = activity.findViewById(R.id.dividerPrintAfterClose);
        dividerPrintReceipts = activity.findViewById(R.id.dividerPrintReceipts);
        titleAutoClose = activity.findViewById(R.id.titleAutoClose);
        dividerAutoClose = activity.findViewById(R.id.dividerAutoClose);
        titleSendSms = activity.findViewById(R.id.titleSendSms);
        dividerSendSms = activity.findViewById(R.id.dividerSendSms);
        titleSendEmail = activity.findViewById(R.id.titleSendEmail);
        dividerExit = activity.findViewById(R.id.dividerExit);

        drawer = activity.findViewById(R.id.drawer);
        drawerMenu = activity.findViewById(R.id.drawer_menu);

        /**
         * Бинд сворачивающихся пунктов
         */
        expandableAutoClose = activity.findViewById(R.id.expandableAutoClose);
        arrowAutoClose = activity.findViewById(R.id.arrowAutoClose);
        expandableSendSms = activity.findViewById(R.id.expandableSendSms);
        arrowSendSms = activity.findViewById(R.id.arrowSendSms);
        expandableSendEmail = activity.findViewById(R.id.expandableSendEmail);
        arrowSendEmail = activity.findViewById(R.id.arrowSendEmail);

        /**
         * Бинд переключателей
         */
        printReportAfterClose = activity.findViewById(R.id.printReportAfterClose);
        printReceipts = activity.findViewById(R.id.printReceipts);
        everyDay = activity.findViewById(R.id.everyDay);
        every = activity.findViewById(R.id.every);
        at = activity.findViewById(R.id.at);
        sendSmsEvotor = activity.findViewById(R.id.sendSmsEvotor);
        sendEmailSv = activity.findViewById(R.id.sendEmailSv);
        sendEmailEvotor = activity.findViewById(R.id.sendEmailEvotor);
        sendSmsSv = activity.findViewById(R.id.sendSmsSv);


        /*menu = activity.findViewById(R.id.menu);*/
        changeTheme = activity.findViewById(R.id.changeTheme);
        changeThemeBottom = activity.findViewById(R.id.changeThemeBottom);
        layoutAutoClose = activity.findViewById(R.id.layoutAutoClose);
        titleEvery = activity.findViewById(R.id.titleEvery);
        titleAt = activity.findViewById(R.id.titleAt);
        layoutSendSms = activity.findViewById(R.id.layoutSendSms);
        layoutSendEmail = activity.findViewById(R.id.layoutSendEmail);
        iconExit = activity.findViewById(R.id.iconExit);
        titleExit = activity.findViewById(R.id.titleExit);
        version = activity.findViewById(R.id.version);

        about_button = activity.findViewById(R.id.about_button);
        icon_about = activity.findViewById(R.id.icon_about);
        title_about = activity.findViewById(R.id.title_about);

        about_menu_title_about = activity.findViewById(R.id.about_menu_title_about);
        about_menu_feedback = activity.findViewById(R.id.about_menu_feedback);
        about_menu_rate_the_app = activity.findViewById(R.id.about_menu_rate_the_app);
        about_menu_privacy_policy = activity.findViewById(R.id.about_menu_privacy_policy);
        about_menu_user_agreement = activity.findViewById(R.id.about_menu_user_agreement);
        about_menu_licenses = activity.findViewById(R.id.about_menu_licenses);
        about_menu_data_protection = activity.findViewById(R.id.about_menu_data_protection);
        about_menu_button_back = activity.findViewById(R.id.about_menu_button_back);
        about_menu_icon_feedback = activity.findViewById(R.id.about_menu_icon_feedback);
        about_menu_icon_rate_the_app = activity.findViewById(R.id.about_menu_icon_rate_the_app);
        about_menu_icon_privacy_policy = activity.findViewById(R.id.about_menu_icon_privacy_policy);
        about_menu_icon_user_agreement = activity.findViewById(R.id.about_menu_icon_user_agreement);
        about_menu_icon_licenses = activity.findViewById(R.id.about_menu_icon_licenses);
        about_menu_icon_data_protection = activity.findViewById(R.id.about_menu_icon_data_protection);
        about_menu_version = activity.findViewById(R.id.about_menu_version);
        about_menu_title_feedback = activity.findViewById(R.id.about_menu_title_feedback);
        about_menu_title_rate_the_app = activity.findViewById(R.id.about_menu_title_rate_the_app);
        about_menu_title_privacy_policy = activity.findViewById(R.id.about_menu_title_privacy_policy);
        about_menu_title_user_agreement = activity.findViewById(R.id.about_menu_title_user_agreement);
        about_menu_title_licenses = activity.findViewById(R.id.about_menu_title_licenses);
        about_menu_title_data_protection = activity.findViewById(R.id.about_menu_title_data_protection);
        about_menu_divider_title = activity.findViewById(R.id.about_menu_divider_title);

        dIconAbout = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_about);
        dIcon_about_menu_button_back = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_left_menu_arrow_back);
        dIcon_about_menu_icon_feedback = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_icon_feedback);
        dIcon_about_menu_icon_rate_the_app = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_about_menu_icon_rate_the_app);
        dIcon_about_menu_icon_privacy_policy = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_icon_privacy_policy);
        dIcon_about_menu_icon_user_agreement = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_icon_user_agreement);
        dIcon_about_menu_icon_licenses = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_icon_licenses);
        dIcon_about_menu_icon_data_protection = ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.ic_icon_data_protection);
        about_menu_image_back = activity.findViewById(R.id.about_menu_image_back);

        drawerMenu.post(new Runnable() {
            @Override
            public void run() {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerMenu.getLayoutParams();

                DisplayMetrics displaymetrics = activity.getResources().getDisplayMetrics();
                if (displaymetrics.widthPixels == 1280 && displaymetrics.heightPixels == 740) {
                    params.width = Double.valueOf(activity.getResources().getDisplayMetrics().widthPixels * 0.3).intValue();
                } else {
                    params.width = Double.valueOf(activity.getResources().getDisplayMetrics().widthPixels * 0.87).intValue();

                }
                drawerMenu.setLayoutParams(params);
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @SuppressLint("LongLogTag")
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                SessionPresenter.getInstance().checkInitSmsServer();
                Log.d(TAG + "_onDrawerOpened", String.format("ClassName: %s, Action: onDrawerOpened", "DrawerMenuManager.java"));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                closeAllToggle();
                makeVisiblyMainMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        about_button.setOnClickListener(this);
        about_menu_button_back.setOnClickListener(this);
        about_menu_feedback.setOnClickListener(this);
        about_menu_rate_the_app.setOnClickListener(this);
        about_menu_privacy_policy.setOnClickListener(this);
        about_menu_user_agreement.setOnClickListener(this);
        about_menu_licenses.setOnClickListener(this);
        about_menu_data_protection.setOnClickListener(this);

 /*       menu.setOnClickListener(this);
        menu.setOnClickListener(this);*/
//        changeTheme.setOnClickListener(this);
        changeThemeBottom.setOnClickListener(this);
        layoutAutoClose.setOnClickListener(this);
        titleEvery.setOnClickListener(this);
        titleAt.setOnClickListener(this);
        layoutSendSms.setOnClickListener(this);
        layoutSendEmail.setOnClickListener(this);
        iconExit.setOnClickListener(this);
        titleExit.setOnClickListener(this);

        toolbar.setTitle(activity.getString(R.string.app_name));
        activity.setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(activity/*this*/, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActionBar = activity.getSupportActionBar();
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        closeAllToggle();
        initSwitch();
        updateVersion();
        updateUITheme();
    }

    @SuppressLint("LongLogTag")
    private void initSwitch() {
        printReportAfterClose.setOnCheckedChangeListener(null);
        printReceipts.setOnCheckedChangeListener(null);

        everyDay.setOnCheckedChangeListener(null);
        every.setOnCheckedChangeListener(null);
        at.setOnCheckedChangeListener(null);

        sendSmsEvotor.setOnCheckedChangeListener(null);
        sendSmsSv.setOnCheckedChangeListener(null);

        sendEmailEvotor.setOnCheckedChangeListener(null);
        sendEmailSv.setOnCheckedChangeListener(null);

        printReportAfterClose.setChecked(SessionPresenter.getInstance().isPrintReportOnClose());
        printReceipts.setChecked(SessionPresenter.getInstance().isPrintChecks());

        printReportAfterClose.setOnCheckedChangeListener(this);
        printReceipts.setOnCheckedChangeListener(this);

        if (!SessionPresenter.getInstance().isAutoClose()) {
            expandableAutoClose.setActiveChildId(-1);

            everyDay.setChecked(false);
            every.setChecked(false);
            at.setChecked(false);
        } else {
            switch (SessionPresenter.getInstance().getAutoCloseType()) {
                case AUTO_CLOSE_EVERY_DAY:
                    expandableAutoClose.setActiveChildId(0);
                    everyDay.setChecked(true);
                    break;
                case AUTO_CLOSE_EVERY_:
                    expandableAutoClose.setActiveChildId(1);
                    every.setChecked(true);
                    break;
                case AUTO_CLOSE_AT_:
                    expandableAutoClose.setActiveChildId(2);
                    at.setChecked(true);
                    break;
            }
        }

        titleEvery.setText(String.format(Locale.getDefault(), activity.getString(R.string.every_),
                SessionPresenter.getInstance().getAutoCloseEveryValue(),
                activity.getResources().getStringArray(R.array.time_unit)[SessionPresenter.getInstance().getAutoCloseEveryUnit()]));

        titleAt.setText(String.format(Locale.getDefault(), activity.getString(R.string.at_), SessionPresenter.getInstance().getAutoCloseAtHour(),
                SessionPresenter.getInstance().getAutoCloseAtMinute()));

        everyDay.setOnCheckedChangeListener(this);
        every.setOnCheckedChangeListener(this);
        at.setOnCheckedChangeListener(this);

        if (!SessionPresenter.getInstance().isSendSms()) {
            expandableSendSms.setActiveChildId(-1);
            sendSmsEvotor.setChecked(false);
            sendSmsSv.setChecked(false);
        } else {
            if (SessionPresenter.getInstance().getDefaultSmsService().equals(SOFT_VILLAGE_SERVICE)) {
                expandableSendSms.setActiveChildId(1);
                sendSmsEvotor.setChecked(false);
                sendSmsSv.setChecked(true);
            } else if (SessionPresenter.getInstance().getDefaultSmsService().equals(EVOTOR_SERVICE)) {
                expandableSendSms.setActiveChildId(0);
                sendSmsEvotor.setChecked(true);
                sendSmsSv.setChecked(false);
            }
        }

        sendSmsEvotor.setOnCheckedChangeListener(this);
        sendSmsSv.setOnCheckedChangeListener(this);

        if (!SessionPresenter.getInstance().isSendEmail()) {
            expandableSendEmail.setActiveChildId(-1);
            sendEmailEvotor.setChecked(false);
            sendEmailSv.setChecked(false);
        } else {
            if (SessionPresenter.getInstance().getDefaultEmailService().equals(SOFT_VILLAGE_SERVICE)) {
                Log.d(TAG + "_EmailService", "if");
                expandableSendEmail.setActiveChildId(1);
                sendEmailEvotor.setChecked(false);
                sendEmailSv.setChecked(true);
            } else if (SessionPresenter.getInstance().getDefaultEmailService().equals(EVOTOR_SERVICE)) {
                Log.d(TAG + "_EmailService", "else - if");
                expandableSendEmail.setActiveChildId(0);
                sendEmailEvotor.setChecked(true);
                sendEmailSv.setChecked(false);
            }
        }

        sendEmailEvotor.setOnCheckedChangeListener(this);
        sendEmailSv.setOnCheckedChangeListener(this);
    }

    private void updateVersion() {
        version.setText(String.format(Locale.getDefault(), "v %s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        about_menu_version.setText(String.format("Версия %s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
    }


    @SuppressLint({"LongLogTag", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        Log.d(TAG + "_left_menu", "click - click: " + v.getId());
        AboutDialog dialog;
        switch (v.getId()) {
            /*case R.id.menu:
                if (!drawer.isDrawerOpen(START))
                    drawer.openDrawer(START);
                break;*/
            case R.id.changeThemeBottom:
                toggleTheme();
                break;
            case R.id.layoutAutoClose:
                toggleExpandableAutoClose();
                break;
            case R.id.titleEvery:
                SetCloseEveryDialog setCloseEveryDialog = SetCloseEveryDialog.newInstance();
                setCloseEveryDialog.show(activity.getSupportFragmentManager(), SetCloseEveryDialog.class.getSimpleName());
                break;
            case R.id.titleAt:
                SetCloseAtDialog setCloseAtDialog = SetCloseAtDialog.newInstance();
                setCloseAtDialog.show(activity.getSupportFragmentManager(), SetCloseAtDialog.class.getSimpleName());
                break;
            case R.id.layoutSendSms:
                toggleExpandableSendSms();
                break;
            case R.id.layoutSendEmail:
                toggleExpandableSendEmail();
                break;
            case R.id.iconExit:
            case R.id.titleExit:
                ExitDialog exitDialog = ExitDialog.newInstance();
                exitDialog.setiExitDialog(this);
                exitDialog.setCancelable(false);
                exitDialog.show(activity.getSupportFragmentManager(), ExitDialog.class.getSimpleName());
                break;

            case R.id.about_button:
                makeVisiblyAboutMenu();
                break;
            case R.id.about_menu_button_back:
                makeVisiblyMainMenu();
                break;
            case R.id.about_menu_feedback:
                dialog = AboutDialog.newInstance(AboutDialog.TYPE_FEEDBACK);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), AboutDialog.TYPE_FEEDBACK);
                Log.d(TAG + "_DrawerMenuManager", "tap on about_menu_feedback");
                break;
            case R.id.about_menu_rate_the_app:
                dialog = AboutDialog.newInstance(AboutDialog.TYPE_RATE_THE_APP);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), AboutDialog.TYPE_RATE_THE_APP);
                Log.d(TAG + "_DrawerMenuManager", "tap on about_menu_rate_the_app");
                break;
            case R.id.about_menu_privacy_policy:
                dialog = AboutDialog.newInstance(AboutDialog.TYPE_PRIVACY_POLICY);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), AboutDialog.TYPE_PRIVACY_POLICY);
                Log.d(TAG + "_DrawerMenuManager", "tap on about_menu_privacy_policy");
                break;
            case R.id.about_menu_user_agreement:
                dialog = AboutDialog.newInstance(AboutDialog.TYPE_USER_AGREEMENT);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), AboutDialog.TYPE_USER_AGREEMENT);
                Log.d(TAG + "_DrawerMenuManager", "tap on about_menu_user_agreement");
                break;
            case R.id.about_menu_licenses:
                dialog = AboutDialog.newInstance(AboutDialog.TYPE_LICENSES);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), AboutDialog.TYPE_LICENSES);
                Log.d(TAG + "_DrawerMenuManager", "tap on about_menu_licenses");
                break;
            case R.id.about_menu_data_protection:
                dialog = AboutDialog.newInstance(AboutDialog.TYPE_DATA_PROTECTION);
                dialog.setCancelable(false);
                dialog.show(activity.getSupportFragmentManager(), AboutDialog.TYPE_DATA_PROTECTION);
                Log.d(TAG + "_DrawerMenuManager", "tap on about_menu_data_protection");
                break;
        }
    }

    @SuppressLint({"LongLogTag", "NonConstantResourceId"})
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG + "_Check", "onCheckedChanged");
        switch (buttonView.getId()) {
            case R.id.printReportAfterClose:
                SessionPresenter.getInstance().setPrintReportOnClose(isChecked);
                break;
            case R.id.printReceipts:
                SessionPresenter.getInstance().setPrintChecks(isChecked);
                break;
            case R.id.everyDay:
                Log.d(TAG + "_Check", "isChecked");
                everyDayChecked(isChecked);
                break;
            case R.id.every:
                everyChecked(isChecked);
                break;
            case R.id.at:
                atChecked(isChecked);
                break;
            case R.id.sendSmsEvotor:
                sendSmsEvotorChecked(isChecked);
                break;
            case R.id.sendSmsSv:
                sendSmsSvChecked(isChecked);
                break;
            case R.id.sendEmailEvotor:
                sendEmailEvotorChecked(isChecked);
                break;
            case R.id.sendEmailSv:
                sendEmailSvChecked(isChecked);
                break;
        }
    }

    private void closeAllToggle() {
        closeExpandableAutoClose();
        closeExpandableSendSms();
        closeExpandableSendEmail();
    }

    private void toggleExpandableAutoClose() {
        if (expandableAutoClose.isExpanded()) {
            arrowAutoClose.animate().setDuration(150).rotation(180);
            expandableAutoClose.collapse();
        } else {
            arrowAutoClose.animate().setDuration(150).rotation(0);
            expandableAutoClose.expand();
        }
    }

    private void closeExpandableAutoClose() {
        arrowAutoClose.animate().rotation(180);
        expandableAutoClose.collapse();
    }

    private void closeExpandableSendSms() {
        arrowSendSms.animate().rotation(180);
        expandableSendSms.collapse();
    }

    private void toggleExpandableSendSms() {
        if (expandableSendSms.isExpanded()) {
            arrowSendSms.animate().setDuration(150).rotation(180);
            expandableSendSms.collapse();
        } else {
            arrowSendSms.animate().setDuration(150).rotation(0);
            expandableSendSms.expand();
        }
    }

    private void closeExpandableSendEmail() {
        arrowSendEmail.animate().rotation(180);
        expandableSendEmail.collapse();
    }

    private void toggleExpandableSendEmail() {
        if (expandableSendEmail.isExpanded()) {
            arrowSendEmail.animate().setDuration(150).rotation(180);
            expandableSendEmail.collapse();
        } else {
            arrowSendEmail.animate().setDuration(150).rotation(0);
            expandableSendEmail.expand();
        }
    }


    @SuppressLint("LongLogTag")
    private void everyDayChecked(boolean isChecked) {
        Log.d(TAG + "_Check", "everyDayChecked");
        if (isChecked) {
            Log.d(TAG + "_Check", "isChecked");
            if (SessionPresenter.getInstance().getSessionTime() >= 24 * 60 * 60 * 1000) {
                AlertDialog dialog = AlertDialog.newInstance(activity.getString(R.string.title_session_time_alert),
                        activity.getString(R.string.title_session_time_alert_description));
                dialog.setiAlertDialog(new AlertDialog.IAlertDialog() {
                    @Override
                    public void onAlertClickYes() {
                        expandableAutoClose.setActiveChildId(0);

                        every.setOnCheckedChangeListener(null);
                        at.setOnCheckedChangeListener(null);

                        every.setChecked(false);
                        at.setChecked(false);

                        every.setOnCheckedChangeListener(DrawerMenuManager.this);
                        at.setOnCheckedChangeListener(DrawerMenuManager.this);

                        SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_EVERY_DAY);
                    }

                    @Override
                    public void onAlertClickNo() {
                        everyDay.setOnCheckedChangeListener(null);
                        everyDay.setChecked(false);
                        everyDay.setOnCheckedChangeListener(DrawerMenuManager.this);
                    }
                });
                dialog.show(activity.getSupportFragmentManager(), AlertDialog.class.getSimpleName());
            } else {
                Log.d(TAG + "_Check", "else");
                expandableAutoClose.setActiveChildId(0);

                every.setOnCheckedChangeListener(null);
                at.setOnCheckedChangeListener(null);

                every.setChecked(false);
                at.setChecked(false);

                every.setOnCheckedChangeListener(DrawerMenuManager.this);
                at.setOnCheckedChangeListener(DrawerMenuManager.this);

                SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_EVERY_DAY);
            }
        } else {
            if (!every.isChecked() && !at.isChecked()) {
                expandableAutoClose.setActiveChildId(-1);
                SessionPresenter.getInstance().setAutoClose(false);
            }
        }
    }

    private void everyChecked(boolean isChecked) {
        if (isChecked) {
            long periodTime = SessionPresenter.getInstance().getAutoCloseEveryValue() * 60 * 1000;

            if (SessionPresenter.getInstance().getAutoCloseEveryUnit() == AUTO_CLOSE_EVERY_UNIT_HOUR)
                periodTime *= 60;

            if (SessionPresenter.getInstance().getSessionTime() >= periodTime) {
                AlertDialog dialog = AlertDialog.newInstance(activity.getString(R.string.title_session_time_day_alert),
                        activity.getString(R.string.title_session_time_day_alert_description));
                dialog.setiAlertDialog(new AlertDialog.IAlertDialog() {
                    @Override
                    public void onAlertClickYes() {
                        expandableAutoClose.setActiveChildId(1);

                        everyDay.setOnCheckedChangeListener(null);
                        at.setOnCheckedChangeListener(null);

                        everyDay.setChecked(false);
                        at.setChecked(false);

                        everyDay.setOnCheckedChangeListener(DrawerMenuManager.this);
                        at.setOnCheckedChangeListener(DrawerMenuManager.this);

                        SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_EVERY_);
                    }

                    @Override
                    public void onAlertClickNo() {
                        every.setOnCheckedChangeListener(null);
                        every.setChecked(false);
                        every.setOnCheckedChangeListener(DrawerMenuManager.this);
                    }
                });
                dialog.show(activity.getSupportFragmentManager(), AlertDialog.class.getSimpleName());
            } else {
                expandableAutoClose.setActiveChildId(1);

                everyDay.setOnCheckedChangeListener(null);
                at.setOnCheckedChangeListener(null);

                everyDay.setChecked(false);
                at.setChecked(false);

                everyDay.setOnCheckedChangeListener(DrawerMenuManager.this);
                at.setOnCheckedChangeListener(DrawerMenuManager.this);

                SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_EVERY_);
            }
        } else {
            if (!everyDay.isChecked() && !at.isChecked()) {
                expandableAutoClose.setActiveChildId(-1);
                SessionPresenter.getInstance().setAutoClose(false);
            }
        }
    }

    private void atChecked(boolean isChecked) {
        if (isChecked) {
            Calendar closeAt = Calendar.getInstance();
            closeAt.set(Calendar.HOUR_OF_DAY, SessionPresenter.getInstance().getAutoCloseAtHour());
            closeAt.set(Calendar.MINUTE, SessionPresenter.getInstance().getAutoCloseAtMinute());
            closeAt.set(Calendar.SECOND, 0);
            closeAt.set(Calendar.MILLISECOND, 0);

            if (Calendar.getInstance().getTimeInMillis() > closeAt.getTimeInMillis())
                closeAt.add(Calendar.DAY_OF_MONTH, 1);

            if (SessionPresenter.getInstance().getSessionTime()
                    + (closeAt.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())
                    >= 24 * 60 * 60 * 1000) {
                AlertDialog dialog = AlertDialog.newInstance(activity.getString(R.string.title_session_time_alert),
                        activity.getString(R.string.title_session_time_alert_description));
                dialog.setiAlertDialog(new AlertDialog.IAlertDialog() {
                    @Override
                    public void onAlertClickYes() {
                        expandableAutoClose.setActiveChildId(2);

                        everyDay.setOnCheckedChangeListener(null);
                        every.setOnCheckedChangeListener(null);

                        everyDay.setChecked(false);
                        every.setChecked(false);

                        everyDay.setOnCheckedChangeListener(DrawerMenuManager.this);
                        every.setOnCheckedChangeListener(DrawerMenuManager.this);

                        SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_AT_);
                    }

                    @Override
                    public void onAlertClickNo() {
                        at.setOnCheckedChangeListener(null);
                        at.setChecked(false);
                        at.setOnCheckedChangeListener(DrawerMenuManager.this);
                    }
                });
                dialog.show(activity.getSupportFragmentManager(), AlertDialog.class.getSimpleName());
            } else {
                expandableAutoClose.setActiveChildId(2);

                everyDay.setOnCheckedChangeListener(null);
                every.setOnCheckedChangeListener(null);

                everyDay.setChecked(false);
                every.setChecked(false);

                everyDay.setOnCheckedChangeListener(DrawerMenuManager.this);
                every.setOnCheckedChangeListener(DrawerMenuManager.this);

                SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_AT_);
            }
        } else {
            if (!everyDay.isChecked() && !every.isChecked()) {
                expandableAutoClose.setActiveChildId(-1);
                SessionPresenter.getInstance().setAutoClose(false);
            }
        }
    }

    private void sendSmsEvotorChecked(boolean isChecked) {
        if (isChecked) {
            expandableSendSms.setActiveChildId(0);

            sendSmsSv.setOnCheckedChangeListener(null);
            sendSmsSv.setChecked(false);
            sendSmsSv.setOnCheckedChangeListener(this);

            if (!SessionPresenter.getInstance().getDefaultSmsService().equals(EVOTOR_SERVICE)) {
                SessionPresenter.getInstance().setDefaultSmsService(EVOTOR_SERVICE);
                SessionPresenter.getInstance().setSendSms(true);
            }
        } else {
            if (!sendSmsEvotor.isChecked() && !sendSmsSv.isChecked()) {
                expandableSendSms.setActiveChildId(-1);
                SessionPresenter.getInstance().setSendSms(false);
            }
        }
    }

    private void sendSmsSvChecked(boolean isChecked) {
        if (isChecked) {
            if (!SessionPresenter.getInstance().isSmsServiceInit()) {
                sendSmsSv.setChecked(false);
                NotExistSmsServerDialog notExistSmsServerDialog = NotExistSmsServerDialog.newInstance();
                notExistSmsServerDialog.setCancelable(false);
                notExistSmsServerDialog.show(activity.getSupportFragmentManager(), ExitDialog.class.getSimpleName());

                expandableSendSms.setActiveChildId(-1);
                SessionPresenter.getInstance().setSendSms(false);
                SessionPresenter.getInstance().setDefaultSmsService(EVOTOR_SERVICE);
            } else {
                expandableSendSms.setActiveChildId(1);
                sendSmsEvotor.setOnCheckedChangeListener(null);
                sendSmsEvotor.setChecked(false);
                sendSmsEvotor.setOnCheckedChangeListener(this);

                if (!SessionPresenter.getInstance().getDefaultSmsService().equals(SOFT_VILLAGE_SERVICE)) {
                    SessionPresenter.getInstance().setDefaultSmsService(SOFT_VILLAGE_SERVICE);
                    SessionPresenter.getInstance().setSendSms(true);
                }
            }
        } else {
            if (!sendSmsEvotor.isChecked() && !sendSmsSv.isChecked()) {
                expandableSendSms.setActiveChildId(-1);
                SessionPresenter.getInstance().setSendSms(false);
            }
        }
    }

    private void sendEmailEvotorChecked(boolean isChecked) {
        if (isChecked) {
            expandableSendEmail.setActiveChildId(0);
            sendEmailSv.setOnCheckedChangeListener(null);
            sendEmailSv.setChecked(false);
            sendEmailSv.setOnCheckedChangeListener(this);

            if (!SessionPresenter.getInstance().getDefaultEmailService().equals(EVOTOR_SERVICE)) {
                SessionPresenter.getInstance().setDefaultEmailService(EVOTOR_SERVICE);
                SessionPresenter.getInstance().setSendEmail(true);
            }
        } else {
            if (!sendEmailEvotor.isChecked() && !sendEmailSv.isChecked()) {
                expandableSendEmail.setActiveChildId(-1);
                SessionPresenter.getInstance().setSendSms(false);
            }
        }
    }

    private void sendEmailSvChecked(boolean isChecked) {
        if (isChecked) {
            expandableSendEmail.setActiveChildId(1);
            sendEmailEvotor.setOnCheckedChangeListener(null);
            sendEmailEvotor.setChecked(false);
            sendEmailEvotor.setOnCheckedChangeListener(this);

            if (!SessionPresenter.getInstance().getDefaultEmailService().equals(SOFT_VILLAGE_SERVICE)) {
                SessionPresenter.getInstance().setDefaultEmailService(SOFT_VILLAGE_SERVICE);
                SessionPresenter.getInstance().setSendEmail(true);
            }
        } else {
            if (!sendEmailEvotor.isChecked() && !sendEmailSv.isChecked()) {
                expandableSendEmail.setActiveChildId(-1);
                SessionPresenter.getInstance().setSendSms(false);
            }
        }
    }

    /**
     * Обработка диалога Exit
     */
    @Override
    public void onCloseClick() {
//        stopNotifyService();
        activity.finish();
    }

    private void stopNotifyService() {
        if (isMyServiceRunning(ForegroundServiceDispatcher.class))
            activity.stopService(new Intent(activity, ForegroundServiceDispatcher.class));
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateEveryTitle() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleEvery.setText(String.format(Locale.getDefault(), activity.getString(R.string.every_),
                        SessionPresenter.getInstance().getAutoCloseEveryValue(),
                        activity.getResources().getStringArray(R.array.time_unit)[SessionPresenter.getInstance().getAutoCloseEveryUnit()]));
            }
        });

    }

    @Override
    public void updateAtTitle() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titleAt.setText(String.format(Locale.getDefault(), activity.getString(R.string.at_), SessionPresenter.getInstance().getAutoCloseAtHour(),
                        SessionPresenter.getInstance().getAutoCloseAtMinute()));
            }
        });

    }

    @Override
    public void updateUserHasSmsServer() {
        if (SessionPresenter.getInstance().isSmsServiceInit()) {
            if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            } else {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            }
        } else {
            if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_lt));
            } else {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_dt));
            }
            if (SessionPresenter.getInstance().getDefaultSmsService() != null &&
                    SessionPresenter.getInstance().getDefaultSmsService().equals(SOFT_VILLAGE_SERVICE) &&
                    SessionPresenter.getInstance().isSendSms()) {
                sendSmsSv.setChecked(false);
                expandableSendSms.setActiveChildId(-1);
                SessionPresenter.getInstance().setSendSms(false);
                SessionPresenter.getInstance().setDefaultSmsService(EVOTOR_SERVICE);
            }
        }
    }

    private void toggleTheme() {
        SessionPresenter.getInstance().toggleTheme();

        updateUITheme();
//        activity.recreate();
    }

    private void updateUITheme() {
        Log.d(TAG, "CURRENT THEME: " + SessionPresenter.getInstance().getCurrentTheme());
        changeIconColor(SessionPresenter.getInstance().getCurrentTheme());
        if (SessionPresenter.getInstance().getCurrentTheme() == 0) {
            changeTheme.setImageResource(R.drawable.ic_moon);
            /*int tabIconColor = ContextCompat.getColor(changeTheme.getContext(), R.color.active_fonts_lt);
            changeTheme.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.LIGHTEN*//*PorterDuff.Mode.SRC_IN*//*);*/

            drawerMenu.post(new Runnable() {
                @Override
                public void run() {
                    drawerMenu.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_lt));
                }
            });

            main.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_lt));
            toolbar.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.header_lt));

//            updateTabs(viewPager.getCurrentItem());

            titleParams.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));

            printReportAfterClose.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            dividerPrintAfterClose.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_lt));

            printReceipts.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            dividerPrintReceipts.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_lt));

            titleAutoClose.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_lt));
            everyDay.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            titleEvery.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            titleAt.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            dividerAutoClose.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_lt));

            titleSendSms.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_lt));
            sendSmsEvotor.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            if (SessionPresenter.getInstance().isSmsServiceInit()) {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            } else {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_lt));
            }
            dividerSendSms.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_lt));

            titleSendEmail.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_lt));
            sendEmailEvotor.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));
            sendEmailSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_lt));

            titleExit.setTextColor(ContextCompat.getColor(titleExit.getContext(), R.color.fonts_lt));
            dividerExit.setBackgroundColor(ContextCompat.getColor(dividerExit.getContext(), R.color.divider_lt));
            version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.active_fonts_lt));

            title_about.setTextColor(ContextCompat.getColor(title_about.getContext(), R.color.fonts_lt));
            about_menu_title_about.setTextColor(ContextCompat.getColor(about_menu_title_about.getContext(), R.color.fonts_lt));
            about_menu_version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.active_fonts_lt));
            about_menu_title_feedback.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
            about_menu_title_rate_the_app.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
            about_menu_title_privacy_policy.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
            about_menu_title_user_agreement.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
            about_menu_title_licenses.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
            about_menu_title_data_protection.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_lt));
            about_menu_divider_title.setBackgroundColor(ContextCompat.getColor(about_menu_divider_title.getContext(), R.color.divider_lt));
        } else {
            changeTheme.setImageResource(R.drawable.ic_sun);
            int tabIconColor = ContextCompat.getColor(changeTheme.getContext(), R.color.active_fonts_dt);
            changeTheme.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            drawerMenu.post(new Runnable() {
                @Override
                public void run() {
                    drawerMenu.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.color28));
                }
            });

            main.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_dt));
            toolbar.setBackgroundColor(ContextCompat.getColor(drawerMenu.getContext(), R.color.background_dt));

//            updateTabs(viewPager.getCurrentItem());

            titleParams.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_dt));

            printReportAfterClose.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            dividerPrintAfterClose.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_dt));

            printReceipts.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            dividerPrintReceipts.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_dt));

            titleAutoClose.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_dt));
            everyDay.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            titleEvery.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            titleAt.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            dividerAutoClose.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_dt));

            titleSendSms.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_dt));
            sendSmsEvotor.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            if (SessionPresenter.getInstance().isSmsServiceInit()) {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            } else {
                sendSmsSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_dt));
            }
            dividerSendSms.setBackgroundColor(ContextCompat.getColor(titleParams.getContext(), R.color.divider_dt));

            titleSendEmail.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.active_fonts_dt));
            sendEmailEvotor.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));
            sendEmailSv.setTextColor(ContextCompat.getColor(titleParams.getContext(), R.color.fonts_dt));

            titleExit.setTextColor(ContextCompat.getColor(titleExit.getContext(), R.color.active_fonts_dt));
            dividerExit.setBackgroundColor(ContextCompat.getColor(dividerExit.getContext(), R.color.divider_dt));
            version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.active_fonts_dt));

            title_about.setTextColor(ContextCompat.getColor(title_about.getContext(), R.color.fonts_dt));
            about_menu_title_about.setTextColor(ContextCompat.getColor(about_menu_title_about.getContext(), R.color.active_fonts_dt));
            about_menu_version.setTextColor(ContextCompat.getColor(version.getContext(), R.color.active_fonts_dt));
            about_menu_title_feedback.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
            about_menu_title_rate_the_app.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
            about_menu_title_privacy_policy.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
            about_menu_title_user_agreement.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
            about_menu_title_licenses.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
            about_menu_title_data_protection.setTextColor(ContextCompat.getColor(version.getContext(), R.color.fonts_dt));
            about_menu_divider_title.setBackgroundColor(ContextCompat.getColor(about_menu_divider_title.getContext(), R.color.divider_dt));
        }

        icon_about.setImageDrawable(dIconAbout);
        about_menu_image_back.setImageDrawable(dIcon_about_menu_button_back);
        about_menu_icon_feedback.setImageDrawable(dIcon_about_menu_icon_feedback);
        about_menu_icon_rate_the_app.setImageDrawable(dIcon_about_menu_icon_rate_the_app);
        about_menu_icon_privacy_policy.setImageDrawable(dIcon_about_menu_icon_privacy_policy);
        about_menu_icon_user_agreement.setImageDrawable(dIcon_about_menu_icon_user_agreement);
        about_menu_icon_licenses.setImageDrawable(dIcon_about_menu_icon_licenses);
        about_menu_icon_data_protection.setImageDrawable(dIcon_about_menu_icon_data_protection);
        /*if (viewPager.getAdapter() != null)
            ((MainPagerAdapter) viewPager.getAdapter()).updateUITheme();*/
    }

    private void changeIconColor(int themeStyle) {
        int iconColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.icon_dt);
        int arrowBackColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.active_fonts_dt);
        if (themeStyle == SessionPresenter.THEME_LIGHT) {
            iconColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.fonts_lt);
            arrowBackColor = ContextCompat.getColor(activity.getApplicationContext(), R.color.active_fonts_lt);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dIconAbout.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));

            dIcon_about_menu_button_back.setColorFilter(new BlendModeColorFilter(arrowBackColor, BlendMode.SRC_IN));
            dIcon_about_menu_icon_feedback.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            dIcon_about_menu_icon_rate_the_app.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            dIcon_about_menu_icon_privacy_policy.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            dIcon_about_menu_icon_user_agreement.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            dIcon_about_menu_icon_licenses.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
            dIcon_about_menu_icon_data_protection.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
        } else {
            dIconAbout.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);

            dIcon_about_menu_button_back.setColorFilter(arrowBackColor, PorterDuff.Mode.SRC_IN);
            dIcon_about_menu_icon_feedback.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            dIcon_about_menu_icon_rate_the_app.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            dIcon_about_menu_icon_privacy_policy.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            dIcon_about_menu_icon_user_agreement.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            dIcon_about_menu_icon_licenses.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
            dIcon_about_menu_icon_data_protection.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        }
    }

    private void makeVisiblyMainMenu() {
        main_menu.setVisibility(View.VISIBLE);
        version.setVisibility(View.VISIBLE);
        about_menu.setVisibility(View.GONE);
    }

    private void makeVisiblyAboutMenu() {
        main_menu.setVisibility(View.GONE);
        version.setVisibility(View.GONE);
        about_menu.setVisibility(View.VISIBLE);
    }

    public void showUpButton(boolean show) {
        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (show) {
            //Запрещаяем выезжание меню справа
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            // Remove hamburger
            toggle.setDrawerIndicatorEnabled(false);
            // Show back button
            mActionBar.setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.onBackPressed();
                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            //Разрешаем выезжание меню справа
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Remove back button
            mActionBar.setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            toggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }

        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }
}
