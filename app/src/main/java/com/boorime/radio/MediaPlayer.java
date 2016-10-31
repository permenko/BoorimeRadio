package com.boorime.radio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.boorime.radio.utils.Const;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.vodyasov.amr.AudiostreamMetadataManager;
import com.vodyasov.amr.OnNewMetadataListener;
import com.vodyasov.amr.UserAgent;

import java.util.List;

class MediaPlayer implements ExoPlayer.Listener {

    private final String LOG_TAG = MediaPlayer.class.getSimpleName();
    private ExoPlayer mediaPlayer;
    private final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private final int BUFFER_SEGMENT_COUNT = 160;
    private final String USER_AGENT = "Android";
    private boolean playWhenReady;
    private int state;
    private Context context;

    MediaPlayer(Context context) {
        this.context = context;
    }

    private void prepareMediaPlayer() {
        Log.d(LOG_TAG, "prepareMediaPlayer");
        mediaPlayer = ExoPlayer.Factory.newInstance(1);
        mediaPlayer.addListener(this);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(context, null, USER_AGENT);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(BuildConfig.STREAM_URL), dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
        mediaPlayer.prepare(audioRenderer);
        mediaPlayer.setPlayWhenReady(getPlayWhenReady());
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {

            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    void startStream() {
        Log.d(LOG_TAG, "startStream");
        setPlayWhenReady(true);
        if (mediaPlayer == null) {
            prepareMediaPlayer();
        }
    }
    void stopStream() {
        Log.d(LOG_TAG, "stopStream");
        setState(ExoPlayer.STATE_ENDED);
        mediaPlayerState.ended();

        releaseMediaPlayer();
        unregisterMetadataListener();
        setPlayWhenReady(false);
    }

    private void setPlayWhenReady(Boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }

    boolean getPlayWhenReady() {
        return playWhenReady;
    }

    int getState() {
        return state;
    }

    private void setState(int state) {
        this.state = state;
    }

    interface MediaPlayerState {
        void buffering();
        void ended();
        void idle();
        void preparing();
        void ready();
        void unknown();
    }

    private MediaPlayerState mediaPlayerState;

    void setStateListener(MediaPlayerState mediaPlayerState) {
        this.mediaPlayerState = mediaPlayerState;
    }

    @Override
    public void onPlayerStateChanged(boolean b, int playerState) {
        switch(playerState) {
            case ExoPlayer.STATE_BUFFERING:
                mediaPlayerState.buffering();
                setState(ExoPlayer.STATE_BUFFERING);
                break;
            case ExoPlayer.STATE_ENDED:
                mediaPlayerState.ended();
                setState(ExoPlayer.STATE_ENDED);
                break;
            case ExoPlayer.STATE_IDLE:
                mediaPlayerState.idle();
                setState(ExoPlayer.STATE_IDLE);
                break;
            case ExoPlayer.STATE_PREPARING:
                mediaPlayerState.preparing();
                setState(ExoPlayer.STATE_PREPARING);
                break;
            case ExoPlayer.STATE_READY:
                mediaPlayerState.ready();
                setState(ExoPlayer.STATE_READY);
                registerMetadataListener();
                break;
            default:
                mediaPlayerState.unknown();
                break;
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        mediaPlayerState.ended();
    }

    interface MetadataListener {
        void onChanged(@NonNull String title);
    }

    private MetadataListener mMetadataListener;

    void setMetadataListener(MetadataListener metadataListener) {
        mMetadataListener = metadataListener;
    }

    private void unregisterMetadataListener() {
        AudiostreamMetadataManager.getInstance().stop();
    }

    private void registerMetadataListener() {
        AudiostreamMetadataManager.getInstance()
                .setUri(BuildConfig.STREAM_URL)
                .setOnNewMetadataListener(new OnNewMetadataListener() {
                    @Override
                    public void onNewHeaders(String stringUri, List<String> name, List<String> desc, List<String> br, List<String> genre, List<String> info) {

                    }

                    @Override
                    public void onNewStreamTitle(String stringUri, String streamTitle) {
                        if (mMetadataListener != null) mMetadataListener.onChanged(streamTitle);
                    }
                })
                .setUserAgent(UserAgent.WINDOWS_MEDIA_PLAYER)
                .start();
    }

}
