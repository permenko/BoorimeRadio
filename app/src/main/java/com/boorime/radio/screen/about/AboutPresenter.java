package com.boorime.radio.screen.about;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

public class AboutPresenter {

    private AboutView mView;

    public AboutPresenter(AboutView view) {
        mView = view;
    }

    public void onVkClick(PackageManager packageManager) {
        mView.openSocial(createIntent(packageManager, getVkUri(), "com.vkontakte.android"));
    }

    public void onFacebookClick(PackageManager packageManager) {
        mView.openSocial(createIntent(packageManager, getFacebookUri(packageManager), "com.facebook.katana"));
    }

    public void onInstagramClick(PackageManager packageManager) {
        mView.openSocial(createIntent(packageManager, getInstagramUri(), "com.instagram.android"));
    }

    public void onMailClick() {
        mView.openSocial(createMailIntent());
    }

    @NonNull
    private Intent createIntent(PackageManager packageManager, Uri uri, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (packageManager.getPackageInfo(packageName, 0) != null) {
                intent.setData(uri);
                intent.setPackage(packageName);
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            //ignore
        }
        intent.setData(uri);
        return intent;
    }

    @NonNull
    private Intent createMailIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"boorime.feedback@gmail.com"});
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Boorime radio - android application");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        return intent;
    }

    private Uri getVkUri() {
        return Uri.parse("https://vk.com/boorimeradio");
    }

    private Uri getFacebookUri(PackageManager packageManager) {
        String url = "http://facebook.com/boorime";
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return Uri.parse("fb://facewebmodal/f?href=" + url);
            } else { //older versions of fb app
                return Uri.parse("fb://page/http://" + url);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return Uri.parse(url);
        }
    }

    private Uri getInstagramUri() {
        return Uri.parse("http://instagram.com/_u/boorimeradio/");
    }

}

