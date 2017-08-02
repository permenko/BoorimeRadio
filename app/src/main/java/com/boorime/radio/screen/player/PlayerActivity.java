package com.boorime.radio.screen.player;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boorime.radio.R;
import com.boorime.radio.player.MusicService;
import com.boorime.radio.screen.about.AboutActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayerActivity extends AppCompatActivity implements PlayerView {

    @Bind(R.id.about)
    ImageButton mAbout;

    @Bind(R.id.playPause)
    ImageButton mPlayPause;
    @Bind(R.id.share)
    ImageButton mShare;

    @Bind(R.id.trackName)
    TextView mTrackName;
    @Bind(R.id.artistName)
    TextView mArtistName;

    private PlayerPresenter mPlayerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);
        mPlayerPresenter = new PlayerPresenter(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPlayerPresenter.init();
    }

    @Override
    public void onStop() {
        mPlayerPresenter.release();
        super.onStop();
    }

    @OnClick(R.id.about)
    void onAboutClick() {
        mPlayerPresenter.onAboutClick();
    }

    @OnClick(R.id.playPause)
    void onPlayPauseClick() {
        mPlayerPresenter.onPlayPauseClick();
    }

    @OnClick(R.id.share)
    void onShareClick() {
        mPlayerPresenter.onShareClick();
    }

    //check by service name not the best idea todo: find a better way to check is music playing or not
    @Override
    public boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void startService() {
        startService(new Intent(this, MusicService.class));
    }

    @Override
    public void stopService() {
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    public void share() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String textToShare;
        if (mArtistName.getText().length() == 0) {
            textToShare = getString(R.string.share_without_title);
        } else {
            textToShare = getString(R.string.share_with_title, mArtistName.getText() + " - " + mTrackName.getText());
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
    }

    @Override
    public void showPlay() {
        mPlayPause.setImageResource(R.drawable.ic_play);
    }

    @Override
    public void showPause() {
        mPlayPause.setImageResource(R.drawable.ic_pause);
    }

    @Override
    public void changeTitle(@NonNull String artistName, @NonNull String trackName) {
        mArtistName.setText(artistName);
        mTrackName.setText(trackName);
    }

    @Override
    public void openAboutScreen() {
        AboutActivity.start(this, mAbout.getMeasuredWidth() / 2, mAbout.getMeasuredHeight() / 2);
    }

}
