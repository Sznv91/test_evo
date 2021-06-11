package ru.softvillage.fiscalizer.tabs.left_menu.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

import static android.graphics.Color.WHITE;
import static android.view.Gravity.CENTER;

public class NotExistSmsServerDialog extends DialogFragment implements View.OnClickListener {
    public interface IExitDialog {
        void onCloseClick();
    }

    ConstraintLayout root;

    private TextView title, content, cancel, ok;
    private ImageView qr_holder;

    private IExitDialog iExitDialog;

    public NotExistSmsServerDialog() {
    }

    public static NotExistSmsServerDialog newInstance() {
        Bundle args = new Bundle();

        NotExistSmsServerDialog fragment = new NotExistSmsServerDialog();
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
        root = (ConstraintLayout) inflater.inflate(R.layout.dialog_sms_server_not_exist, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = getView().findViewById(R.id.title);
        content = getView().findViewById(R.id.content);
        cancel = getView().findViewById(R.id.cancel);
        ok = getView().findViewById(R.id.ok);
        qr_holder = getView().findViewById(R.id.qr_holder);
//        ButterKnife.bind(this, root);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

        String toQrData = getString(R.string.sms_server_google_play_link);
        Bitmap barcode_bitmap = null;
        try {
            barcode_bitmap = encodeAsBitmap(toQrData, BarcodeFormat.QR_CODE, 200, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        qr_holder.setImageBitmap(barcode_bitmap);
//        qr_holder.setBackground(new BitmapDrawable(getResources(), barcode_bitmap));
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
            case R.id.ok:
                SessionPresenter.getInstance().checkInitSmsServer();
                dismiss();
                break;
        }
    }

    private void updateUITheme() {
        if (SessionPresenter.getInstance().getCurrentTheme() == 0) {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.color.background_lt));
            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.fonts_lt));
            content.setTextColor(ContextCompat.getColor(content.getContext(), R.color.fonts_lt));
        } else {
            root.setBackground(ContextCompat.getDrawable(root.getContext(), R.color.divider_dt));
            title.setTextColor(ContextCompat.getColor(title.getContext(), R.color.fonts_dt));
            content.setTextColor(ContextCompat.getColor(content.getContext(), R.color.fonts_dt));
        }
    }

    @SuppressLint("LongLogTag")
    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height)
            throws WriterException {
        Log.d(EvoApp.TAG + "_NotExistSmsServerDialog", "encodeAsBitmap-> int img_width, int img_height: " + img_width + " " + img_height);
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
                    pixels[offset + x] = result.get(x, y) ? ContextCompat.getColor(EvoApp.getInstance(), R.color.fonts_lt) : WHITE;
                } else {
                    pixels[offset + x] = result.get(x, y) ? ContextCompat.getColor(EvoApp.getInstance(), R.color.fonts_dt) : ContextCompat.getColor(EvoApp.getInstance(), R.color.divider_dt );
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
