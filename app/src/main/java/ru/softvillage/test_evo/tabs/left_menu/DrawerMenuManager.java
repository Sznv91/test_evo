package ru.softvillage.test_evo.tabs.left_menu;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import net.cachapa.expandablelayout.ExpandableLayout;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;

import static androidx.core.view.GravityCompat.START;

public class DrawerMenuManager<T extends AppCompatActivity> implements View.OnClickListener {
    //ru.evotor.framework.kkt.api Посмотреть внимательней
    // KktApi
    private AppCompatActivity activity;
    private DrawerLayout drawer;
    private ConstraintLayout drawerMenu;

    private ImageView menu;
    private ImageView changeTheme;
    private LinearLayout layoutAutoClose;
    private TextView titleEvery;
    private TextView titleAt;
    private LinearLayout layoutSendSms;
    private LinearLayout layoutSendEmail;
    private ImageView iconExit;
    private TextView titleExit;

    private ExpandableLayout expandableAutoClose;
    private ImageView arrowAutoClose;


    public DrawerMenuManager(T activity) {
        this.activity = activity;
        initMenu();
    }

    public void setActivity(T activity) {
        this.activity = activity;
        initMenu();
    }

    private void initMenu() {
        drawer = activity.findViewById(R.id.drawer);
        drawerMenu = activity.findViewById(R.id.drawer_menu);
        ((ImageView) activity.findViewById(R.id.menu)).setOnClickListener(v -> showMenu());

        drawerMenu.post(new Runnable() {
            @Override
            public void run() {
                DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerMenu.getLayoutParams();
                params.width = Double.valueOf(activity.getResources().getDisplayMetrics().widthPixels * 0.87).intValue();
                drawerMenu.setLayoutParams(params);
            }
        });

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
//                ApiPresenter.getInstance().connectApp();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
//                closeExpandableAutoClose();
//                closeExpandableSendSms();
//                closeExpandableSendEmail();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        expandableAutoClose = activity.findViewById(R.id.expandableAutoClose);
        arrowAutoClose = activity.findViewById(R.id.arrowAutoClose);


        menu = activity.findViewById(R.id.menu);
        changeTheme = activity.findViewById(R.id.changeTheme);
        layoutAutoClose = activity.findViewById(R.id.layoutAutoClose);
        titleEvery = activity.findViewById(R.id.titleEvery);
        titleAt = activity.findViewById(R.id.titleAt);
        layoutSendSms = activity.findViewById(R.id.layoutSendSms);
        layoutSendEmail = activity.findViewById(R.id.layoutSendEmail);
        iconExit = activity.findViewById(R.id.iconExit);
        titleExit = activity.findViewById(R.id.titleExit);


        menu.setOnClickListener(this);
        menu.setOnClickListener(this);
        changeTheme.setOnClickListener(this);
        layoutAutoClose.setOnClickListener(this);
        titleEvery.setOnClickListener(this);
        titleAt.setOnClickListener(this);
        layoutSendSms.setOnClickListener(this);
        layoutSendEmail.setOnClickListener(this);
        iconExit.setOnClickListener(this);
        titleExit.setOnClickListener(this);
    }


    @SuppressLint({"LongLogTag", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        Log.d(EvoApp.TAG + "_left_menu", "click - click: " + v.getId());
        switch (v.getId()) {
            case R.id.menu:
                showMenu();
                break;
            case R.id.layoutAutoClose:
                toggleExpandableAutoClose();
                break;
        }
    }

    private void showMenu() {
        if (!drawer.isDrawerOpen(START))
            drawer.openDrawer(START);
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
}
