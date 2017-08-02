package com.boorime.radio.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetReceiver extends BroadcastReceiver {

    public static final String UNPLUGGED = "com.boorime.HEADSET_UNPLUGGED_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MusicService.class).putExtra(UNPLUGGED, true));
    }

}