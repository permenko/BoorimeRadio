package com.boorime.radio.fragments;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boorime.radio.EventBus.MediaPlayerStateEvent;
import com.boorime.radio.EventBus.UpdateFavoriteEvent;
import com.boorime.radio.EventBus.UpdateMetadataEvent;
import com.boorime.radio.Metadata;
import com.boorime.radio.R;
import com.boorime.radio.StreamService;
import com.google.android.exoplayer.ExoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlayerFragment extends Fragment implements AudioManager.OnAudioFocusChangeListener {

    @Bind(R.id.playPauseButton)
    ImageButton playPauseButton;
    @Bind(R.id.shareButton)
    ImageButton shareButton;
    @Bind(R.id.favoriteButton)
    ImageButton favoriteButton;

    @Bind(R.id.trackName)
    TextView trackName;
    @Bind(R.id.artistName)
    TextView artistName;

    private View rootView;
    private Intent streamIntent;
    private Intent shareIntent = new Intent();
    private SharedPreferences sharedPreferences;
    private Metadata metadata;
    private AudioManager audioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, rootView);
        streamIntent = new Intent(rootView.getContext(), StreamService.class);
        sharedPreferences = rootView.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying(StreamService.class)) {
                    rootView.getContext().stopService(streamIntent);
                    abandonFocus();
                } else {
                    rootView.getContext().startService(streamIntent);
                    requestFocus();
                }
                updatePlayPauseButton(isPlaying(StreamService.class));
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateShareIntent();
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavorite = updateFavoriteButton();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(metadata.getArtistName() + " - " + metadata.getTrackName(), !isFavorite).apply();
                EventBus.getDefault().post(new UpdateFavoriteEvent(metadata.getArtistName() + " - " + metadata.getTrackName(), !isFavorite));
            }
        });

        updatePlayPauseButton(isPlaying(StreamService.class));
        audioManager = (AudioManager) rootView.getContext().getSystemService(Context.AUDIO_SERVICE);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MediaPlayerStateEvent event) {
        switch (event.getState()) {
            case ExoPlayer.STATE_BUFFERING:
                //ignore
                break;
            case ExoPlayer.STATE_ENDED:
                updatePlayPauseButton(false);
                break;
            case ExoPlayer.STATE_IDLE:
                //ignore
                break;
            case ExoPlayer.STATE_PREPARING:
                //ignore
                break;
            case ExoPlayer.STATE_READY:
                //ignore
                break;
            default:
                //ignore
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(UpdateFavoriteEvent event) {
        updateFavoriteButton();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(UpdateMetadataEvent event) {
        updateMetadata(event.getMetadata());
    }

    private void updateMetadata(Metadata data) {
        metadata = data;
        artistName.setText(metadata.getArtistName());
        trackName.setText(metadata.getTrackName());
        artistName.setVisibility(View.VISIBLE);
        trackName.setVisibility(View.VISIBLE);

        favoriteButton.setVisibility(View.VISIBLE);
        updateFavoriteButton();

        shareButton.setVisibility(View.VISIBLE);
        updateShareIntent();
    }

    private void updatePlayPauseButton(boolean isPlaying) {
        if (isPlaying) {
            playPauseButton.setImageResource(R.drawable.button_pause);
        } else {
            playPauseButton.setImageResource(R.drawable.button_play);
        }
    }

    private boolean updateFavoriteButton() {
        boolean isFavorite = sharedPreferences.getBoolean(metadata.getArtistName() + " - " + metadata.getTrackName(), false);
        favoriteButton.setImageResource(isFavorite ? R.drawable.selected_favorite : R.drawable.unselected_favorite);
        return isFavorite;
    }

    private void updateShareIntent() {
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.text_to_share, metadata.getArtistName() + " - " + metadata.getTrackName()));
        shareIntent.setType("text/plain");
    }

    private boolean isPlaying(Class<?> serviceClass) { //checking is service running (if not that's mean mediaplayer not playing)
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public boolean requestFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public boolean abandonFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                rootView.getContext().startService(streamIntent);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                rootView.getContext().stopService(streamIntent);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                rootView.getContext().stopService(streamIntent);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //ignore for now
                break;
            default:
        }
        updatePlayPauseButton(isPlaying(StreamService.class));
    }
}
