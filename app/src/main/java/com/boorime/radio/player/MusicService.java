package com.boorime.radio.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.boorime.radio.R;
import com.boorime.radio.model.Metadata;
import com.boorime.radio.screen.player.PlayerActivity;
import com.vodyasov.amr.AudiostreamMetadataManager;

import org.greenrobot.eventbus.EventBus;

//todo: add remote control
public class MusicService extends Service implements MediaPlayer.MediaPlayerState, AudioManager.OnAudioFocusChangeListener {


    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private final int NOTIFICATION_ID = 5315;
    private int mVolume = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
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
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        requestFocus();
        mMediaPlayer.startStream(this);

        EventBus.getDefault().postSticky(new MediaPlayerEvent.Play());
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getBooleanExtra(HeadsetReceiver.UNPLUGGED, false)) {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stopStream();
    }

    @Override
    public void buffering() {
        sendNotification(getString(R.string.buffering));
    }

    @Override
    public void ended() {
        EventBus.getDefault().postSticky(new MediaPlayerEvent.Pause());
        abandonFocus();
        mMediaPlayer.stopStream();
        try {
            AudiostreamMetadataManager.getInstance().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopSelf();
    }

    private void sendMetadata(Metadata metadata) {
        EventBus.getDefault().postSticky(new MediaPlayerEvent.UpdateMetadata(metadata));
    }

    private void sendNotification(String title) {
        Intent showTaskIntent = new Intent(getApplicationContext(), PlayerActivity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mVolume, AudioManager.FLAG_PLAY_SOUND);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_PLAY_SOUND);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //ignore for now
                break;
            default:
        }
    }
}