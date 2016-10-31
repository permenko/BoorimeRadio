package com.boorime.radio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.boorime.radio.EventBus.MediaPlayerStateEvent;
import com.boorime.radio.EventBus.UpdateMetadataEvent;
import com.boorime.radio.receivers.HeadsetReceiver;
import com.boorime.radio.utils.Const;
import com.google.android.exoplayer.ExoPlayer;
import com.vodyasov.amr.AudiostreamMetadataManager;
import com.vodyasov.amr.OnNewMetadataListener;
import com.vodyasov.amr.UserAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StreamService extends Service implements MediaPlayer.MediaPlayerState {

    private final String TAG = StreamService.class.getSimpleName();
    private final String STATE = "Player State";

    private MediaPlayer mMediaPlayer;

    private HeadsetReceiver headsetReceiver;
    private int notificationId = 5315;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mMediaPlayer = new MediaPlayer(StreamService.this);
        mMediaPlayer.setStateListener(this);
        mMediaPlayer.setMetadataListener(new MediaPlayer.MetadataListener() {
            @Override
            public void onChanged(@NonNull String title) {
                if (title.split(" - ").length != 2) title = "Unknown - Unknown";
                String artistName = title.split(" - ")[0];
                String trackName = title.split(" - ")[1];

                sendMetadata(new Metadata(artistName, trackName));
                sendNotification(title);
            }
        });

        mMediaPlayer.startStream();
        headsetReceiver = new HeadsetReceiver();
        registerReceiver(headsetReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)); // register headphones unplug receiver
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null && intent.getBooleanExtra(HeadsetReceiver.UNPLUGGED, false)) {
            Log.d(TAG, "headsetUnplugged, stopSelf");
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stopStream();
        unregisterReceiver(headsetReceiver);
    }

    @Override
    public void buffering() {
        Log.d(STATE, "BUFFERING");
        sendStateEvent(ExoPlayer.STATE_BUFFERING);
        sendNotification("Buffering...");
    }

    @Override
    public void ended() {
        Log.d(STATE, "ENDED");
        sendStateEvent(ExoPlayer.STATE_ENDED);
        if (mMediaPlayer.getState() != ExoPlayer.STATE_ENDED) {
            mMediaPlayer.stopStream();
        }
        try {
            AudiostreamMetadataManager.getInstance().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    @Override
    public void idle() {
        Log.d(STATE, "IDLE");
        sendStateEvent(ExoPlayer.STATE_IDLE);
    }

    @Override
    public void preparing() {
        Log.d(STATE, "PREPARING");
        sendStateEvent(ExoPlayer.STATE_PREPARING);
    }

    @Override
    public void ready() {
        Log.d(STATE, "READY");
        sendStateEvent(ExoPlayer.STATE_READY);
    }

    @Override
    public void unknown() {
        Log.d(STATE, "UNKNOWN"); //ignore for now
    }

    private void sendStateEvent(int state) {
        EventBus.getDefault().postSticky(new MediaPlayerStateEvent(state));
    }

    private void sendMetadata(Metadata metadata) {
        EventBus.getDefault().postSticky(new UpdateMetadataEvent(metadata));
    }

    private void sendNotification(String title) { // TODO: support big notifications
        Log.d(TAG, "notification sent");
        Notification notification = new Notification(R.mipmap.ic_launcher, null, System.currentTimeMillis());
        Intent intent = new Intent(this, BaseActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, getString(R.string.app_name), title, pIntent);
        startForeground(notificationId, notification);
    }

}
