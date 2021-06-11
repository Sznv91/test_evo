package ru.softvillage.fiscalizer.tabs.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.Objects;

import ru.evotor.framework.system.SystemStateApi;
import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.roomDb.Entity.SessionStatisticData;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.fiscalizer.tabs.viewModel.StatisticViewModel;

import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_AT_;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_DAY;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.THEME_LIGHT;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.getInstance;

public class StatisticFragment extends Fragment implements StatisticDisplayUpdate {
    private Handler timerHandler;
    private Runnable timerRun;
    private StatisticViewModel mViewModel;
    ImageView network_quality;

    private ConstraintLayout statistic_information;
    private ConstraintLayout statisticFragment;
    private ConstraintLayout sessionNumberHolder;
    private ConstraintLayout timeToCloseHolder;
    private ConstraintLayout sumFiscalizationHolder;
    private ConstraintLayout countReceiptHolder;
    private ConstraintLayout sendSmsHolder;
    private ConstraintLayout sendEmailHolder;

    private TextView tab_title_statistic_information;
    private TextView statistic_session_number;
    private TextView title_statistic_information;
    private TextView time_to_close;
    private TextView sum_fiscalization;
    private TextView sum_receipt;
    private TextView send_sms;
    private TextView send_email;

    private TextView statistic_current_data;
    private TextView time_ticker_holder;
    private TextView sum;
    private TextView receipt_count;
    private TextView sms_count;
    private TextView email_count;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistic_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNetworkQuality();
        SessionPresenter.getInstance().getDrawerManager().showUpButton(false);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            float landscape_text_size = Float.parseFloat("14.4");
            tab_title_statistic_information.setText("Статистика");
            statistic_session_number.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            title_statistic_information.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            time_to_close.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            sum_fiscalization.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            sum_receipt.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            send_sms.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            send_email.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            statistic_current_data.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            time_ticker_holder.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            sum.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            receipt_count.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            sms_count.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);
            email_count.setTextSize(TypedValue.COMPLEX_UNIT_SP, landscape_text_size);


           /* if (SessionPresenter.getInstance().getCurrentTheme() == THEME_LIGHT) {
                title_statistic_information.setTextColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.fonts_lt));
            } else {
                title_statistic_information.setTextColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.fonts_dt));
            }*/

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SessionPresenter.getInstance().getDrawerManager().showUpButton(false);

        getInstance().setIstatisticDisplayUpdate(this);
        network_quality = view.findViewById(R.id.network_quality);
        statistic_information = view.findViewById(R.id.statistic_information);
        tab_title_statistic_information = getActivity().findViewById(R.id.tab_title_statistic_information);
        statisticFragment = view.findViewById(R.id.statistic_fragment);
        sessionNumberHolder = view.findViewById(R.id.session_number_holder);
        timeToCloseHolder = view.findViewById(R.id.time_to_close_holder);
        sumFiscalizationHolder = view.findViewById(R.id.sum_fiscalization_holder);
        countReceiptHolder = view.findViewById(R.id.count_receipt_holder);
        sendSmsHolder = view.findViewById(R.id.send_sms_holder);
        sendEmailHolder = view.findViewById(R.id.send_email_holder);

        statistic_session_number = view.findViewById(R.id.statistic_session_number);
        title_statistic_information = view.findViewById(R.id.title_statistic_information);
        time_to_close = view.findViewById(R.id.time_to_close);
        sum_fiscalization = view.findViewById(R.id.sum_fiscalization);
        sum_receipt = view.findViewById(R.id.sum_receipt);
        send_sms = view.findViewById(R.id.send_sms);
        send_email = view.findViewById(R.id.send_email);

        statistic_current_data = view.findViewById(R.id.statistic_current_data);
        time_ticker_holder = view.findViewById(R.id.time_ticker_holder);
        sum = view.findViewById(R.id.sum);
        receipt_count = view.findViewById(R.id.receipt_count);
        sms_count = view.findViewById(R.id.sms_count);
        email_count = view.findViewById(R.id.email_count);


/**
 * Из класса статистики достаем все нреобходимые данные
 */
//        getInstance().getSessionData().toString();

        initDateSession();
        updateView(SessionPresenter.getInstance().getSessionData());
        updateTheme();
        updateNetworkQuality();

        /**
         * Тикер для отрисовки таймера обратного отсчета закрытия смены
         */
        timerRun = new Runnable() {
            @Override
            public void run() {
                if (timerRun != null && timerHandler != null) {
                    timeTicker();
                    timerHandler.postDelayed(timerRun, 1000);
                }

            }
        };
        timerHandler = new Handler();
        timerHandler.postDelayed(timerRun, 1000);


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        getInstance().setIstatisticDisplayUpdate(null);
        timerRun = null;
        timerHandler = null;
        super.onDestroyView();
    }

    private void initDateSession() {
        if (getInstance().getDateLastOpenSession() != null) {
            /*startSession.setText(getInstance().getDateLastOpenSession().toString());*/
        }
        if (getInstance().getDateLastCloseSession() != null) {
//            endSession.setText(getInstance().getDateLastCloseSession().toString());
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void updateView(SessionStatisticData data) {
        if (data.getSessionId() == -1) {
            statistic_session_number.setText(String.format("Смена №%03d закрыта", SystemStateApi.getLastSessionNumber(EvoApp.getInstance())));
            time_ticker_holder.setText("00:00:00");
        } else {
            if (getActivity() != null) {
                statistic_session_number.setText(String.format(getActivity().getString(R.string.title_current_session), data.getSessionId()));
            }
        }

        if (getInstance().getDateLastOpenSession() != null) {
            statistic_current_data.setText(getInstance().getDateLastOpenSession().toString("YYYY-MM-dd"));
            changeDateTimeColour();
        }

        if (getActivity() != null) {
            sum.setText(String.format(getActivity().getString(R.string.template_rub_count), data.getSumFiscalization()));
            receipt_count.setText(String.format(getActivity().getString(R.string.template_count), data.getCountReceipt()));
            sms_count.setText(String.format(getActivity().getString(R.string.template_count), data.getSendSms()));
            email_count.setText(String.format(getActivity().getString(R.string.template_count), data.getSendEmail()));
        }

        /*Objects.requireNonNull(getActivity()).runOnUiThread(() -> {


         *//*statisticContainer.setText(data.toString());*//*
        });*/
    }

    @Override
    public void updateTheme() {
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        Drawable calendar_icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_statistic_calendar);

        if (currentTheme == THEME_LIGHT) {
            statistic_information.setBackgroundColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.main_lt));
            statisticFragment.setBackgroundColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.divider_lt));
            sessionNumberHolder.setBackgroundColor(ContextCompat.getColor(sessionNumberHolder.getContext(), R.color.background_lt));
            timeToCloseHolder.setBackgroundColor(ContextCompat.getColor(timeToCloseHolder.getContext(), R.color.background_lt));
            sumFiscalizationHolder.setBackgroundColor(ContextCompat.getColor(sumFiscalizationHolder.getContext(), R.color.background_lt));
            countReceiptHolder.setBackgroundColor(ContextCompat.getColor(countReceiptHolder.getContext(), R.color.background_lt));
            sendSmsHolder.setBackgroundColor(ContextCompat.getColor(sendSmsHolder.getContext(), R.color.background_lt));
            sendEmailHolder.setBackgroundColor(ContextCompat.getColor(sendEmailHolder.getContext(), R.color.background_lt));

            statistic_session_number.setTextColor(ContextCompat.getColor(statistic_session_number.getContext(), R.color.fonts_lt));
            title_statistic_information.setTextColor(ContextCompat.getColor(title_statistic_information.getContext(), R.color.active_fonts_lt));
            time_to_close.setTextColor(ContextCompat.getColor(time_to_close.getContext(), R.color.fonts_lt));
            sum_fiscalization.setTextColor(ContextCompat.getColor(sum_fiscalization.getContext(), R.color.fonts_lt));
            sum_receipt.setTextColor(ContextCompat.getColor(sum_receipt.getContext(), R.color.fonts_lt));
            send_sms.setTextColor(ContextCompat.getColor(send_sms.getContext(), R.color.fonts_lt));
            send_email.setTextColor(ContextCompat.getColor(send_email.getContext(), R.color.fonts_lt));


            changeDateTimeColour();
            int tabIconColor = ContextCompat.getColor(getContext(), R.color.fonts_lt);
            calendar_icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            statistic_current_data.setCompoundDrawablesRelativeWithIntrinsicBounds(calendar_icon, null, null, null);
            sum.setTextColor(ContextCompat.getColor(sum.getContext(), R.color.active_fonts_lt));
            receipt_count.setTextColor(ContextCompat.getColor(receipt_count.getContext(), R.color.active_fonts_lt));
            sms_count.setTextColor(ContextCompat.getColor(sms_count.getContext(), R.color.active_fonts_lt));
            email_count.setTextColor(ContextCompat.getColor(email_count.getContext(), R.color.active_fonts_lt));
        } else {
            statistic_information.setBackgroundColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.main_dt));
            statisticFragment.setBackgroundColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.divider_dt));
            sessionNumberHolder.setBackgroundColor(ContextCompat.getColor(sessionNumberHolder.getContext(), R.color.background_dt));
            timeToCloseHolder.setBackgroundColor(ContextCompat.getColor(timeToCloseHolder.getContext(), R.color.background_dt));
            sumFiscalizationHolder.setBackgroundColor(ContextCompat.getColor(sumFiscalizationHolder.getContext(), R.color.background_dt));
            countReceiptHolder.setBackgroundColor(ContextCompat.getColor(countReceiptHolder.getContext(), R.color.background_dt));
            sendSmsHolder.setBackgroundColor(ContextCompat.getColor(sendSmsHolder.getContext(), R.color.background_dt));
            sendEmailHolder.setBackgroundColor(ContextCompat.getColor(sendEmailHolder.getContext(), R.color.background_dt));

            statistic_session_number.setTextColor(ContextCompat.getColor(statistic_session_number.getContext(), R.color.fonts_dt));
            title_statistic_information.setTextColor(ContextCompat.getColor(title_statistic_information.getContext(), R.color.active_fonts_dt));
            time_to_close.setTextColor(ContextCompat.getColor(time_to_close.getContext(), R.color.fonts_dt));
            sum_fiscalization.setTextColor(ContextCompat.getColor(sum_fiscalization.getContext(), R.color.fonts_dt));
            sum_receipt.setTextColor(ContextCompat.getColor(sum_receipt.getContext(), R.color.fonts_dt));
            send_sms.setTextColor(ContextCompat.getColor(send_sms.getContext(), R.color.fonts_dt));
            send_email.setTextColor(ContextCompat.getColor(send_email.getContext(), R.color.fonts_dt));

            changeDateTimeColour();
            int tabIconColor = ContextCompat.getColor(getContext(), R.color.active_fonts_dt);
            calendar_icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            statistic_current_data.setCompoundDrawablesRelativeWithIntrinsicBounds(calendar_icon, null, null, null);
            sum.setTextColor(ContextCompat.getColor(sum.getContext(), R.color.active_fonts_dt));
            receipt_count.setTextColor(ContextCompat.getColor(receipt_count.getContext(), R.color.active_fonts_dt));
            sms_count.setTextColor(ContextCompat.getColor(sms_count.getContext(), R.color.active_fonts_dt));
            email_count.setTextColor(ContextCompat.getColor(email_count.getContext(), R.color.active_fonts_dt));
        }
    }

    @Override
    public void updateNetworkQuality() {
        if (SessionPresenter.getInstance().isPingResult()) {
            endAnimation();
        } else {
            startAnimation();
        }
    }

    private void changeDateTimeColour() {
        if (getInstance().getSessionData().getSessionId() == -1) {
            statistic_current_data.setTextColor(ContextCompat.getColor(statistic_current_data.getContext(), R.color.color17));
            time_ticker_holder.setTextColor(ContextCompat.getColor(time_ticker_holder.getContext(), R.color.color17));
        } else {
            if (getInstance().getCurrentTheme() == THEME_LIGHT) {
                statistic_current_data.setTextColor(ContextCompat.getColor(statistic_current_data.getContext(), R.color.active_fonts_lt));
                time_ticker_holder.setTextColor(ContextCompat.getColor(time_ticker_holder.getContext(), R.color.active_fonts_lt));
            } else {
                statistic_current_data.setTextColor(ContextCompat.getColor(statistic_current_data.getContext(), R.color.active_fonts_dt));
                time_ticker_holder.setTextColor(ContextCompat.getColor(time_ticker_holder.getContext(), R.color.active_fonts_dt));
            }
        }
    }

    @SuppressLint("LongLogTag")
    private void timeTicker() {
        if (SystemStateApi.isSessionOpened(getContext())) {

            LocalDateTime calcCloseTime;
            Duration deltaFromLastClose = null;
            int minutes = 0;
            int seconds = 0;
            int hours = 0;

            if (SessionPresenter.getInstance().isAutoClose()) {
                time_to_close.setText(getActivity().getText(R.string.title_time_to_close));
                int autoCloseType = SessionPresenter.getInstance().getAutoCloseType();
                switch (autoCloseType) {
                    case AUTO_CLOSE_EVERY_DAY:
                        calcCloseTime = SessionPresenter.getInstance().getDateLastOpenSession().plusHours(24);
                        deltaFromLastClose = new Duration(
                                LocalDateTime.now().toDateTime(),
                                calcCloseTime.toDateTime());
                        break;
                    case AUTO_CLOSE_EVERY_:
                        int value = SessionPresenter.getInstance().getAutoCloseEveryValue();
                        int unit = SessionPresenter.getInstance().getAutoCloseEveryUnit();
                        if (unit == SessionPresenter.AUTO_CLOSE_EVERY_UNIT_HOUR) {
                            calcCloseTime = SessionPresenter.getInstance().getDateLastOpenSession().plusHours(value);
                        } else {
                            calcCloseTime = SessionPresenter.getInstance().getDateLastOpenSession().plusMinutes(value);
                        }
                        deltaFromLastClose = new Duration(
                                LocalDateTime.now().toDateTime(),
                                calcCloseTime.toDateTime());

                        break;
                    case AUTO_CLOSE_AT_:
                        int hourToClose = SessionPresenter.getInstance().getAutoCloseAtHour();
                        int minutesToClose = SessionPresenter.getInstance().getAutoCloseAtMinute();
                        LocalTime localTimeToClose = LocalDateTime.now().withTime(hourToClose, minutesToClose, 0, 0).toLocalTime();
                        if (localTimeToClose.isAfter(LocalTime.now())) {
                            deltaFromLastClose = new Duration(LocalDateTime.now().toDateTime(), LocalDateTime.now().withTime(localTimeToClose.getHourOfDay(), localTimeToClose.getMinuteOfHour(), localTimeToClose.getSecondOfMinute(), 0).toDateTime());
                        } else {
                            deltaFromLastClose = new Duration(LocalDateTime.now().toDateTime(), LocalDateTime.now().plusDays(1).withTime(localTimeToClose.getHourOfDay(), localTimeToClose.getMinuteOfHour(), localTimeToClose.getSecondOfMinute(), 0).toDateTime());
                        }
                        break;
                }
            } else {
                time_to_close.setText(getActivity().getText(R.string.title_time_from_open));
                calcCloseTime = SessionPresenter.getInstance().getDateLastOpenSession();
                deltaFromLastClose = new Duration(calcCloseTime.toDateTime(), LocalDateTime.now().toDateTime());
            }

            hours = deltaFromLastClose.toStandardHours().getHours();
            minutes = deltaFromLastClose.toStandardMinutes().getMinutes() - (hours * 60);
            seconds = deltaFromLastClose.toStandardSeconds().getSeconds() - ((hours * 60) * 60 + (minutes * 60));
            if (hours < 0) {
                hours = 0;
            }
            if (minutes < 0) {
                minutes = 0;
            }
            if (seconds < 0) {
                seconds = 0;
            }
            if (timerHandler != null && timerRun != null) {
                time_ticker_holder.setText(String.format("%02d:%02d:%02d",
                        hours,
                        minutes,
                        seconds));
            }
        }
    }

    private AlphaAnimation animation1;

    @SuppressLint("LongLogTag")
    public void startAnimation() {
        Log.d(EvoApp.TAG + "_pinger", "startAnimation");
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            network_quality.setVisibility(View.VISIBLE);
            network_quality.setImageDrawable(ContextCompat.getDrawable(network_quality.getContext(), R.drawable.ic_network));
            animation1 = new AlphaAnimation(0.2f, 1.0f);
            animation1.setDuration(1000);
//                animation1.setFillAfter(true);
            animation1.setRepeatCount(AlphaAnimation.INFINITE);
            animation1.setRepeatMode(AlphaAnimation.REVERSE);
            network_quality.startAnimation(animation1);
        });
    }

    @SuppressLint("LongLogTag")
    public void endAnimation() {
        Log.d(EvoApp.TAG + "_pinger", "endAnimation");
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            animation1 = new AlphaAnimation(0, 0);
            animation1.setFillEnabled(true);
            animation1.setFillAfter(true);
            network_quality.startAnimation(animation1);
            network_quality.setImageDrawable(null);
            network_quality.setVisibility(View.INVISIBLE);
        });
    }
}