package com.boorime.radio.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.boorime.radio.adapters.FavoriteAdapter;
import com.boorime.radio.EventBus.UpdateFavoriteEvent;
import com.boorime.radio.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;

// TODO: switch to SQLite

public class FavoriteFragment extends Fragment {

    private ArrayList<String> songs;
    private ListView favoriteList;
    private FavoriteAdapter favoriteAdapter;

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_favorite, container, false);
        favoriteList = (ListView) rootView.findViewById(R.id.favoritesList);

        setHasOptionsMenu(true);
        initFavoriteList();
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
    public void onEventMainThread(UpdateFavoriteEvent event) {
        updateFavoriteList(event.getSong(), event.getIsFavorite());
    }

    private void updateFavoriteList(String song, Boolean isFavorite) {
        if (songs == null) {
            songs = new ArrayList<>();

            Map<String, ?> objects = rootView.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE).getAll();

            for (Map.Entry<String, ?> entry : objects.entrySet()) {
                if (entry.getValue().toString().equals("true")) {
                    songs.add(entry.getKey());
                }
            }
        } else {
            if (isFavorite) {
                songs.add(song);
            } else {
                songs.remove(song);
            }
        }
        favoriteAdapter = new FavoriteAdapter(rootView.getContext(), songs);

        favoriteList.setAdapter(favoriteAdapter);
    }

    private void initFavoriteList() {
        updateFavoriteList(null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return  true;
    }
}
