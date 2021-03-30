package ru.softvillage.test_evo.services.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ru.softvillage.test_evo.services.ForegroundServiceDispatcher;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "StartBootReceiver", Toast.LENGTH_LONG).show();
        context.startService(new Intent(context, ForegroundServiceDispatcher.class));
    }
}
