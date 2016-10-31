package com.boorime.radio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.boorime.radio.StreamService;
import com.boorime.radio.utils.Const;

public class HeadsetReceiver extends BroadcastReceiver {

    public static final String UNPLUGGED = "com.boorime.HEADSET_UNPLUGGED_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, StreamService.class).putExtra(UNPLUGGED, true));
    }

}