package com.boorime.radio.player;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.boorime.radio.BuildConfig;
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

public class MediaPlayer implements ExoPlayer.Listener {

    private ExoPlayer mExoPlayer;
    private final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private final int BUFFER_SEGMENT_COUNT = 160;
    private final String USER_AGENT = "Android";

    private MediaPlayerState mMediaPlayerState;

    private void prepareMediaPlayer(Context context) {
        mExoPlayer = ExoPlayer.Factory.newInstance(1);
        mExoPlayer.addListener(this);
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        DataSource dataSource = new DefaultUriDataSource(context, null, USER_AGENT);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(BuildConfig.STREAM_URL), dataSource, allocator,
                BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, MediaCodecSelector.DEFAULT);
        mExoPlayer.prepare(audioRenderer);
        mExoPlayer.setPlayWhenReady(true);
    }

    private void releaseMediaPlayer() {
        if (mExoPlayer != null) {

            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    public void startStream(Context context) {
        if (mExoPlayer == null) {
            prepareMediaPlayer(context);
        }
    }

    public void stopStream() {
        if (mExoPlayer == null || mExoPlayer.getPlaybackState() == ExoPlayer.STATE_ENDED) return;
        releaseMediaPlayer();
        unregisterMetadataListener();
        mMediaPlayerState.ended();
    }

    public interface MediaPlayerState {
        void buffering();
        void ended();
    }

    public void setStateListener(MediaPlayerState mediaPlayerState) {
        mMediaPlayerState = mediaPlayerState;
    }

    @Override
    public void onPlayerStateChanged(boolean b, int playerState) {
        switch(playerState) {
            case ExoPlayer.STATE_BUFFERING:
                mMediaPlayerState.buffering();
                break;
            case ExoPlayer.STATE_ENDED:
                mMediaPlayerState.ended();
                break;
            case ExoPlayer.STATE_READY:
                registerMetadataListener();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        mMediaPlayerState.ended();
    }

    public interface MetadataListener {
        void onChanged(@NonNull String title);
    }

    private MetadataListener mMetadataListener;

    public void setMetadataListener(MetadataListener metadataListener) {
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
