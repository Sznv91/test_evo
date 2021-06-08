package ru.softvillage.fiscalizer.tabs.left_menu.dialogs;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.fiscalizer.tabs.left_menu.util.Prefs;

import static android.view.Gravity.CENTER;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_AT_;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_HOUR;
import static ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_MINUTE;

public class SetCloseAtDialog extends DialogFragment implements View.OnClickListener, AlertDialog.IAlertDialog {
    ConstraintLayout root;

    private TextView title;
    private WheelPicker hours;
    private TextView divider;
    private WheelPicker minutes;
    private TextView ok;
    private TextView cancel;

    private int hour;
    private int minute;

    public SetCloseAtDialog() {
    }

    public static SetCloseAtDialog newInstance() {

        Bundle args = new Bundle();

        SetCloseAtDialog fragment = new SetCloseAtDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();

            if (window != null) {
                Point size = new Point();
                getActivity().getWindowManager().getDefaultDisplay().getSize(size);

                window.setLayout(size.x - getResources().getDimensionPixelSize(R.dimen.margin_72) * 2, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setGravity(CENTER);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = (ConstraintLayout) inflater.inflate(R.layout.dialog_set_close_at_time, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = getView().findViewById(R.id.title);
        hours = getView().findViewById(R.id.hours);
        divider = getView().findViewById(R.id.divider);
        minutes = getView().findViewById(R.id.minutes);
        ok = getView().findViewById(R.id.ok);
        cancel = getView().findViewById(R.id.cancel);
//        ButterKnife.bind(this, root);

        updateUI();

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUITheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                checkBeforeSave();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    private void updateUI() {
        hour = Prefs.getInstance().loadInt(AUTO_CLOSE_HOUR);
        if (hour == -1) {
            hour = 14;
            Prefs.getInstance().saveInt(AUTO_CLOSE_HOUR, hour);
        }

        minute = Prefs.getInstance().loadInt(AUTO_CLOSE_MINUTE);
        if (minute == -1) {
            minute = 0;
            Prefs.getInstance().saveInt(AUTO_CLOSE_MINUTE, minute);
        }

        ArrayList<String> hoursData = new ArrayList<>();
        for (int i = 0; i < 24; i++)
            hoursData.add(String.format(Locale.getDefault(), "%02d", i));

        ArrayList<String> minutesData = new ArrayList<>();
        for (int i = 0; i < 60; i++)
            minutesData.add(String.format(Locale.getDefault(), "%02d", i));

        hours.setData(hoursData);
        hours.setSelectedItemPosition(hour, false);

        minutes.setData(minutesData);
        minutes.setSelectedItemPosition(minute, false);
    }

    private void checkBeforeSave() {
        boolean showAlert = false;

        if (SessionPresenter.getInstance().getAutoCloseType() == AUTO_CLOSE_AT_) {
            Calendar closeAt = Calendar.getInstance();
            closeAt.set(Calendar.HOUR_OF_DAY, hours.getCurrentItemPosition());
            closeAt.set(Calendar.MINUTE, minutes.getCurrentItemPosition());
            closeAt.set(Calendar.SECOND, 0);
            closeAt.set(Calendar.MILLISECOND, 0);

            if (Calendar.getInstance().getTimeInMillis() > closeAt.getTimeInMillis())
                closeAt.add(Calendar.DAY_OF_MONTH, 1);

            if (SessionPresenter.getInstance().getSessionTime()
                    + (closeAt.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) >= 24 * 60 * 60 * 1000) {
                showAlert = true;
            }
        }

        if (showAlert) {
            AlertDialog dialog = AlertDialog.newInstance(getString(R.string.title_session_time_alert),
                    getString(R.string.title_session_time_alert_description));
            dialog.setiAlertDialog(this);
            dialog.show(getActivity().getSupportFragmentManager(), AlertDialog.class.getSimpleName());
        } else {
            save();
        }
    }

    private void save() {
        SessionPresenter.getInstance().setAutoCloseAtHour(hours.getCurrentItemPosition());
        SessionPresenter.getInstance().setAutoCloseAtMinute(minutes.getCurrentItemPosition());
        /*SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_AT_);*/

        dismiss();
    }

    @Override
    public void onAlertClickYes() {
        save();
    }

    @Override
    public void onAlertClickNo() {

    }

    private void updateUITheme() {
        if (SessionPresenter.getInstance().getCurrentTheme() == 0) {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog));

            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color20));

            hours.setItemTextColor(ContextCompat.getColor(title.getContext(), R.color.color27));
            hours.setAlpha(0.77f);

            divider.setTextColor(ContextCompat.getColor(title.getContext(), android.R.color.black));
            divider.setAlpha(0.87f);

            minutes.setItemTextColor(ContextCompat.getColor(title.getContext(), R.color.color27));
            minutes.setAlpha(0.77f);
        } else {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog_black));

            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));

            hours.setItemTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));
            hours.setAlpha(1);

            divider.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));
            divider.setAlpha(1f);

            minutes.setItemTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));
            minutes.setAlpha(1);
        }
    }
}
