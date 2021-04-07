package ru.softvillage.test_evo.services.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.services.ForegroundServiceDispatcher;
import ru.softvillage.test_evo.utils.PrintUtil;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d(EvoApp.TAG+"_Boot", "receive boot");
        }, 5000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ForegroundServiceDispatcher.class));
        } else {
            context.startService(new Intent(context, ForegroundServiceDispatcher.class));
        }

        Toast.makeText(EvoApp.getInstance().getApplicationContext(), "StartBootReceiver", Toast.LENGTH_LONG).show();
//        context.startService(new Intent(context, ForegroundServiceDispatcher.class));
    }
}
