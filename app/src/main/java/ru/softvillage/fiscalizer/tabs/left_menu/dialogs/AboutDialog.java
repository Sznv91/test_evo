package ru.softvillage.fiscalizer.tabs.left_menu.dialogs;

import android.content.res.Configuration;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;


public class AboutDialog extends DialogFragment {
    private static final String ARG_TYPE = "type";
    public static final String TYPE_FEEDBACK = "feedback";
    public static final String TYPE_RATE_THE_APP = "rate_the_app";
    public static final String TYPE_PRIVACY_POLICY = "privacy_policy";
    public static final String TYPE_USER_AGREEMENT = "user_agreement";
    public static final String TYPE_LICENSES = "licenses";
    public static final String TYPE_DATA_PROTECTION = "data_protection";

    private String dialogType;

    private ImageView about_dialog_icon;
    private TextView about_dialog_title,
            about_dialog_content,
            about_dialog_button_close;
    private Drawable dIcon;
    private CheckBox about_dialog_checkbox;


    public AboutDialog() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.85).intValue();
            int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.7).intValue();
            getDialog().getWindow().setLayout(width, height);
        } else {
            int width = Double.valueOf(getResources().getDisplayMetrics().widthPixels * 0.50).intValue();
            int height = Double.valueOf(getResources().getDisplayMetrics().heightPixels * 0.498).intValue();
            getDialog().getWindow().setLayout(width, height);
        }

        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
        } else {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog_black);
        }
    }

    public static AboutDialog newInstance(String dialogType) {
        AboutDialog fragment = new AboutDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, dialogType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogType = getArguments().getString(ARG_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        about_dialog_icon = view.findViewById(R.id.about_dialog_icon);
        about_dialog_title = view.findViewById(R.id.about_dialog_title);
        about_dialog_content = view.findViewById(R.id.about_dialog_content);
        about_dialog_button_close = view.findViewById(R.id.about_dialog_button_close);
        about_dialog_checkbox = view.findViewById(R.id.about_dialog_checkbox);


        initContent();
        initColor();
        initButton();
    }

    private void initContent() {
        switch (dialogType) {
            case TYPE_FEEDBACK:
                about_dialog_title.setText(getText(R.string.about_menu_title_feedback));
                dIcon = ContextCompat.getDrawable(about_dialog_icon.getContext(), R.drawable.ic_icon_feedback);
                break;
            case TYPE_RATE_THE_APP:
                about_dialog_title.setText(getText(R.string.about_menu_title_rate_the_app));
                dIcon = ContextCompat.getDrawable(about_dialog_icon.getContext(), R.drawable.ic_about_menu_icon_rate_the_app);
                break;
            case TYPE_PRIVACY_POLICY:
                about_dialog_title.setText(getText(R.string.about_menu_title_privacy_policy));
                dIcon = ContextCompat.getDrawable(about_dialog_icon.getContext(), R.drawable.ic_icon_privacy_policy);
                break;
            case TYPE_USER_AGREEMENT:
                about_dialog_checkbox.setVisibility(View.VISIBLE);
                about_dialog_title.setText(getText(R.string.about_menu_title_user_agreement));
                dIcon = ContextCompat.getDrawable(about_dialog_icon.getContext(), R.drawable.ic_icon_user_agreement);
                break;
            case TYPE_LICENSES:
                about_dialog_title.setText(getText(R.string.about_menu_title_licenses));
                dIcon = ContextCompat.getDrawable(about_dialog_icon.getContext(), R.drawable.ic_icon_licenses);
                break;
            case TYPE_DATA_PROTECTION:
                about_dialog_title.setText(getText(R.string.about_menu_title_data_protection));
                dIcon = ContextCompat.getDrawable(about_dialog_icon.getContext(), R.drawable.ic_icon_data_protection);
                break;
            default:
                about_dialog_title.setText("unexpected type");
                dIcon = null;
                break;
        }
        about_dialog_icon.setImageDrawable(dIcon);
    }

    private void initButton() {
        if (dialogType.equals(TYPE_USER_AGREEMENT)) {
            if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT)
                about_dialog_button_close.setTextColor(ContextCompat.getColor(about_dialog_button_close.getContext(), R.color.active_fonts_lt));
            else
                about_dialog_button_close.setTextColor(ContextCompat.getColor(about_dialog_button_close.getContext(), R.color.active_fonts_dt));
            about_dialog_checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    SessionPresenter.getInstance().setIsCheckedUserAgreement(true);
                    about_dialog_button_close.setTextColor(ContextCompat.getColor(about_dialog_button_close.getContext(), R.color.header_lt));
                    about_dialog_button_close.setOnClickListener(v -> dismiss());
                } else {
                    SessionPresenter.getInstance().setIsCheckedUserAgreement(false);
                    if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT)
                        about_dialog_button_close.setTextColor(ContextCompat.getColor(about_dialog_button_close.getContext(), R.color.active_fonts_lt));
                    else
                        about_dialog_button_close.setTextColor(ContextCompat.getColor(about_dialog_button_close.getContext(), R.color.active_fonts_dt));
                    about_dialog_button_close.setOnClickListener(v -> {
                    });
                }
            });
            about_dialog_checkbox.setChecked(SessionPresenter.getInstance().getIsCheckedUserAgreement());
        } else {
            about_dialog_button_close.setOnClickListener(v -> dismiss());
        }
    }

    private void initColor() {
        int iconColor;

        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            iconColor = ContextCompat.getColor(about_dialog_icon.getContext(), R.color.active_fonts_lt);
            about_dialog_checkbox.setTextColor(ContextCompat.getColor(about_dialog_checkbox.getContext(), R.color.fonts_lt));
            about_dialog_title.setTextColor(ContextCompat.getColor(about_dialog_title.getContext(), R.color.fonts_lt));
            about_dialog_content.setTextColor(ContextCompat.getColor(about_dialog_content.getContext(), R.color.fonts_lt));

        } else {
            iconColor = ContextCompat.getColor(about_dialog_icon.getContext(), R.color.icon_dt);
            about_dialog_checkbox.setTextColor(ContextCompat.getColor(about_dialog_checkbox.getContext(), R.color.fonts_dt));
            about_dialog_title.setTextColor(ContextCompat.getColor(about_dialog_title.getContext(), R.color.fonts_dt));
            about_dialog_content.setTextColor(ContextCompat.getColor(about_dialog_content.getContext(), R.color.fonts_dt));
        }

        if (dIcon != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dIcon.setColorFilter(new BlendModeColorFilter(iconColor, BlendMode.SRC_IN));
        } else {
            if (dIcon != null)
                dIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        }

    }
}