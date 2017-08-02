package com.boorime.radio.screen.about;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.TextView;

import com.boorime.radio.BuildConfig;
import com.boorime.radio.R;
import com.boorime.radio.widget.reveal.RevealLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity implements AboutView {

    private final static String REVEAL_TRANSITION_X = "REVEAL_TRANSITION_X";
    private final static String REVEAL_TRANSITION_Y = "REVEAL_TRANSITION_Y";

    public static void start(@NonNull Activity activity, int revealTransitionX, int revealTransitionY) {
        Intent intent = new Intent(activity, AboutActivity.class);
        intent.putExtra(REVEAL_TRANSITION_X, revealTransitionX);
        intent.putExtra(REVEAL_TRANSITION_Y, revealTransitionY);
        activity.startActivity(intent);
    }

    @Bind(R.id.parent)
    RevealLayout mRevealLayout;
    @Bind(R.id.version)
    TextView mVersion;

    private boolean mIsBackAnimationOngoing;

    private int mRevealTransitionX;
    private int mRevealTransitionY;

    private AboutPresenter mAboutPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        showRevealAnimation(getIntent(), savedInstanceState);

        mVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));
        mAboutPresenter = new AboutPresenter(this);

    }

    private void showRevealAnimation(Intent intent, Bundle savedInstanceState) {
        mRevealTransitionX = intent.getIntExtra(REVEAL_TRANSITION_X, 0);
        mRevealTransitionY = intent.getIntExtra(REVEAL_TRANSITION_Y, 0);

        if (savedInstanceState == null) {
            mRevealLayout.setContentShown(false);

            mRevealLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mRevealLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mRevealLayout.show(mRevealTransitionX, mRevealTransitionY);
                }
            });
        }
    }

    @OnClick(R.id.back)
    void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.vkontakte)
    void onVkClick() {
        mAboutPresenter.onVkClick(getPackageManager());
    }

    @OnClick(R.id.facebook)
    void onFacebookClick() {
        mAboutPresenter.onFacebookClick(getPackageManager());
    }

    @OnClick(R.id.instagram)
    void onInstagramClick() {
        mAboutPresenter.onInstagramClick(getPackageManager());
    }

    @OnClick(R.id.mail)
    void onMailClick() {
        mAboutPresenter.onMailClick();
    }

    @Override
    public void onBackPressed() {
        if (mIsBackAnimationOngoing) return;
        overridePendingTransition(0, 0);
        mRevealLayout.hide(mRevealTransitionX, mRevealTransitionY, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsBackAnimationOngoing = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AboutActivity.super.onBackPressed();
                mIsBackAnimationOngoing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void openSocial(@NonNull Intent intent) {
        startActivity(intent);
    }
}
