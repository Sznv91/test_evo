package ru.softvillage.test_evo.services.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.services.ForegroundServiceDispatcher;

public class InstallReceiver extends BroadcastReceiver {

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Intent startIntent = new Intent(context, ForegroundServiceDispatcher.class);
        startIntent.setAction("start");
        context.startService(startIntent);

        Log.d("ru.evotor." + EvoApp.TAG, "Screen ACTION");
        Toast.makeText(context, "Screen ACTION", Toast.LENGTH_LONG).show();
    }
}