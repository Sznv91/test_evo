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

public class ExitDialog extends DialogFragment implements View.OnClickListener {
    public interface IExitDialog {
        void onCloseClick();
    }

    ConstraintLayout root;

    private TextView title;
    private TextView cancel;
    private TextView close;

    private IExitDialog iExitDialog;

    public ExitDialog() {
    }

    public static ExitDialog newInstance() {
        Bundle args = new Bundle();

        ExitDialog fragment = new ExitDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public void setiExitDialog(IExitDialog iExitDialog) {
        this.iExitDialog = iExitDialog;
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
        root = (ConstraintLayout) inflater.inflate(R.layout.dialog_exit, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = getView().findViewById(R.id.title);
        cancel = getView().findViewById(R.id.cancel);
        close = getView().findViewById(R.id.close);
//        ButterKnife.bind(this, root);

        cancel.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUITheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.close:
                if (iExitDialog != null) iExitDialog.onCloseClick();
                dismiss();
                break;
        }
    }

    private void updateUITheme() {
        if (SessionPresenter.getInstance().getCurrentTheme() == 0) {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog));

            title.setTextColor(ContextCompat.getColor(title.getContext(), android.R.color.black));
            title.setAlpha(0.6f);
        } else {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.drawable.bg_dialog_black));

            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.color15));
            title.setAlpha(1);
        }
    }
}
