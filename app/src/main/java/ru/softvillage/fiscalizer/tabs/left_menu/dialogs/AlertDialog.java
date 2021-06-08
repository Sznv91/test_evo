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

import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

import static android.view.Gravity.CENTER;

public class AlertDialog extends DialogFragment implements View.OnClickListener {
    public interface IAlertDialog {
        void onAlertClickYes();

        void onAlertClickNo();
    }

    private ConstraintLayout root;
    private TextView title;
    private TextView description;
    private TextView yes;
    private TextView no;

    private String titleValue;
    private String descriptionValue;
    private IAlertDialog iAlertDialog;

    public AlertDialog() {
    }

    public static AlertDialog newInstance(String title, String description) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("description", description);

        AlertDialog fragment = new AlertDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setiAlertDialog(IAlertDialog iAlertDialog) {
        this.iAlertDialog = iAlertDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        titleValue = getArguments().getString("title");
        descriptionValue = getArguments().getString("description");
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
        root = (ConstraintLayout) inflater.inflate(R.layout.dialog_alert_1, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = getView().findViewById(R.id.title);
        description = getView().findViewById(R.id.description);
        yes = getView().findViewById(R.id.yes);
        no = getView().findViewById(R.id.no);
//        ButterKnife.bind(this, root);

        title.setText(titleValue);
        description.setText(descriptionValue);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUITheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:
                if (iAlertDialog != null) iAlertDialog.onAlertClickYes();
                dismiss();
                break;
            case R.id.no:
                if (iAlertDialog != null) iAlertDialog.onAlertClickNo();
                dismiss();
                break;
        }
    }

    private void updateUITheme() {
        if (SessionPresenter.getInstance().getCurrentTheme() == 0) {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog));

            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color19));
            title.setAlpha(0.87f);

            description.setTextColor(ContextCompat.getColor(title.getContext(), android.R.color.black));
            description.setAlpha(0.6f);
        } else {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog_black));

            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));
            title.setAlpha(1);

            description.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color33));
            description.setAlpha(1);
        }
    }
}
