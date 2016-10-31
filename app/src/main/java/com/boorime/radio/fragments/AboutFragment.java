package com.boorime.radio.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boorime.radio.R;
import com.boorime.radio.utils.Const;

import butterknife.Bind;
import butterknife.ButterKnife;

// TODO: change button animation

public class AboutFragment extends Fragment {

    @Bind(R.id.goto_vk)
    ImageButton gotoVk;
    @Bind(R.id.goto_fb)
    ImageButton gotoFb;
    @Bind(R.id.goto_instagram)
    ImageButton gotoInstagram;
    @Bind(R.id.goto_contact_us)
    ImageButton gotoContactUs;

    @Bind(R.id.rate_us)
    TextView rateUs;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        gotoInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                startActivity(createInstagramProfileIntent(rootView.getContext().getPackageManager(), Const.INSTAGRAM_URL));
            }
        });

        gotoVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                startActivity(createVkProfileIntent(rootView.getContext().getPackageManager(), Const.VK_URL));
            }
        });

        gotoFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                startActivity(createFacebookProfileIntent(rootView.getContext().getPackageManager(), Const.FACEBOOK_URL));
            }
        });

        gotoContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.image_click));
                startActivity(Intent.createChooser(createEmailIntent(Const.CONTACT_US_EMAIL), getString(R.string.send_email)));
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appName = view.getContext().getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return true;
    }

    /*
    * copy-pasted with a little changes from
    * http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
     */
    private Intent createInstagramProfileIntent(PackageManager pm, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        try {
            if (pm.getPackageInfo("com.instagram.android", 0) != null) {
                intent.setData(uri);
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(uri);
        return intent;
    }

    private Intent createVkProfileIntent(PackageManager pm, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        try {
            if (pm.getPackageInfo("com.vkontakte.android", 0) != null) {
                intent.setData(uri);
                intent.setPackage("com.vkontakte.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(uri);
        return intent;
    }

    private Intent createFacebookProfileIntent(PackageManager pm, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        try {
            if (pm.getPackageInfo("com.facebook.katana", 0) != null) {
                /*
                * copy-pasted from http://stackoverflow.com/questions/34564211/open-facebook-page-in-facebook-app-if-installed-on-android
                 */
                int versionCode = pm.getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) { //newer versions of fb app
                    url = "fb://facewebmodal/f?href=" + url;
                } else { //older versions of fb app
                    url = "fb://page/" + url;
                }

                intent.setData(Uri.parse(url));
                intent.setPackage("com.facebook.katana");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(uri);
        return intent;
    }

    private Intent createEmailIntent(String email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Boorime radio - android application");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        return intent;
    }
}
