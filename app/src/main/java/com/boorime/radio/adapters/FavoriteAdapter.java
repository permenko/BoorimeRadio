package com.boorime.radio.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boorime.radio.EventBus.UpdateFavoriteEvent;
import com.boorime.radio.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FavoriteAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> songs;
    ImageButton favoriteButton;

    public FavoriteAdapter(Context context, ArrayList<String> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_listview, parent, false);

        ((TextView) view.findViewById(R.id.songTitle)).setText(getTrackInfo(position));
        favoriteButton = (ImageButton) view.findViewById(R.id.favoriteButtonListView);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences;
                sharedPreferences = v.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                //String check = sharedPreferences.getString(getTrackInfo(position).split(" - ")[0], "empty");
                boolean isFavorite = sharedPreferences.getBoolean(getTrackInfo(position), false);
                Log.d("FavoriteAdapter", "" + isFavorite);

                if (isFavorite) {
                    favoriteButton.setImageResource(R.drawable.unselected_favorite);
                } else {
                    favoriteButton.setImageResource(R.drawable.selected_favorite);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getTrackInfo(position), !isFavorite).apply();
                EventBus.getDefault().post(new UpdateFavoriteEvent(getTrackInfo(position), !isFavorite));
            }
        });

        return view;
    }

    private String getTrackInfo(int position) {
        return ((String) getItem(position));
    }
}