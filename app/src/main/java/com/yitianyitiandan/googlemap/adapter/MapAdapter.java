package com.yitianyitiandan.googlemap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yitianyitiandan.googlemap.viewholder.MapViewHolder;
import com.yitianyitiandan.googlemap.R;
import com.yitianyitiandan.googlemap.model.Hospital;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapViewHolder> {
    private Context context;
    private List<Hospital> hospitals;
    public MapAdapter(Context context, List<Hospital> hospitals) {
        this.hospitals = hospitals;
        this.context = context;
    }
    @NonNull
    @Override
    public MapViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.map_item_rv, viewGroup, false);
        return new MapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapViewHolder mapViewHolder, int i) {
        mapViewHolder.bind(hospitals.get(i));
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }
}
