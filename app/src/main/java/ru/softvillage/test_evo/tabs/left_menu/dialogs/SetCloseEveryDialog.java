package ru.softvillage.test_evo.tabs.left_menu.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

import static android.view.Gravity.CENTER;
import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_;
import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_UNIT_HOUR;
import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_UNIT_MIN;

public class SetCloseEveryDialog extends DialogFragment implements View.OnClickListener, AlertDialog.IAlertDialog {
    ConstraintLayout root;

    private TextView title;
    private WheelPicker value;
    private AppCompatSpinner unit;
    private TextView ok;
    private TextView cancel;

    private int everyUnit;
    private int everyValue;

    public SetCloseEveryDialog() {
    }

    public static SetCloseEveryDialog newInstance() {
        Bundle args = new Bundle();

        SetCloseEveryDialog fragment = new SetCloseEveryDialog();
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
        root = (ConstraintLayout) inflater.inflate(R.layout.dialog_set_close_every_time, container, false);
//        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = getView().findViewById(R.id.title);
        value = getView().findViewById(R.id.value);
        unit = getView().findViewById(R.id.unit);
        ok = getView().findViewById(R.id.ok);
        cancel = getView().findViewById(R.id.cancel);

        everyUnit = SessionPresenter.getInstance().getAutoCloseEveryUnit();
        everyValue = SessionPresenter.getInstance().getAutoCloseEveryValue();

        updateUI();

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUITheme();
    }

    private void updateUI() {
        unit.setSelection(everyUnit);
        updateWheel(everyUnit);
    }

    private void updateWheel(int position) {
        switch (position) {
            case AUTO_CLOSE_EVERY_UNIT_MIN:
                ArrayList<Integer> wheelData = new ArrayList<>();
                for (int i = 1; i <= 59; i++)
                    wheelData.add(i);

                value.setData(wheelData);
                value.setSelectedItemPosition(everyValue - 1, false);

                break;
            case AUTO_CLOSE_EVERY_UNIT_HOUR:
                wheelData = new ArrayList<>();
                for (int i = 1; i <= 24; i++)
                    wheelData.add(i);

                value.setData(wheelData);
                value.setSelectedItemPosition(everyValue - 1, false);
                break;
        }
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

    private void checkBeforeSave() {
        boolean showAlert = false;
        if (SessionPresenter.getInstance().getAutoCloseType() == AUTO_CLOSE_EVERY_) {
            long periodTime = (value.getCurrentItemPosition() + 1) * 60 * 1000;

            if (unit.getSelectedItemPosition() == AUTO_CLOSE_EVERY_UNIT_HOUR)
                periodTime *= 60;

            if (SessionPresenter.getInstance().getSessionTime() >= periodTime)
                showAlert = true;
        }

        if (showAlert) {
            AlertDialog dialog = AlertDialog.newInstance(getString(R.string.title_session_time_alert),
                    getString(R.string.title_session_time_alert_description));
            dialog.setiAlertDialog(this);
            dialog.show(getActivity().getSupportFragmentManager(), AlertDialog.class.getSimpleName());
        } else
            save();
    }

    @SuppressLint("LongLogTag")
    private void save() {
        if (unit.getSelectedItemPosition() == AUTO_CLOSE_EVERY_UNIT_HOUR) {
            long periodTime = (value.getCurrentItemPosition() + 1) * 60 * 1000;
            /**
             * Если выбранное количество часов > 24, то автоматически значение
             * заменяется на 24 часа.
             */
            long hours24 = 86400000L;
            periodTime *= 60;
            Log.d(EvoApp.TAG + "_SetCloseEveryDialog", String.format("periodTime: %s ; hours24: %s", periodTime, hours24));
            if (periodTime > hours24) {
                SessionPresenter.getInstance().setAutoCloseEveryValue(24);
            } else {
                SessionPresenter.getInstance().setAutoCloseEveryValue(value.getCurrentItemPosition() + 1);
            }

        } else {
            SessionPresenter.getInstance().setAutoCloseEveryValue(value.getCurrentItemPosition() + 1);
        }
        SessionPresenter.getInstance().setAutoCloseEveryUnit(unit.getSelectedItemPosition());
//        SessionPresenter.getInstance().setAutoCloseType(AUTO_CLOSE_EVERY_);

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

            value.setItemTextColor(ContextCompat.getColor(title.getContext(), R.color.color27));
            value.setAlpha(0.77f);
        } else {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog_black));

            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));

            value.setItemTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));
            value.setAlpha(1);
        }
    }
}
