package com.hogl.eregister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hogl.eregister.activities.HomeActivity;
import com.hogl.eregister.connect.MainActivity;

public class TimeChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TimeChangedReceiver", "onReceive");
        Intent serviceIntent = new Intent(context, MainActivity.class);
        context.startService(serviceIntent);
        Intent intent1 = new Intent(context, HomeActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}

