package com.yitianyitiandan.googlemap.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yitianyitiandan.googlemap.R;
import com.yitianyitiandan.googlemap.model.Hospital;

public class MapViewHolder extends RecyclerView.ViewHolder {
    private TextView tvName;
    private TextView tvZone;
    public MapViewHolder(@NonNull View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvName);
        tvZone = itemView.findViewById(R.id.tvZone);
    }

    public void bind(Hospital data) {
        this.tvName.setText(data.getName());
        this.tvZone.setText(data.getZone());
    }
}
