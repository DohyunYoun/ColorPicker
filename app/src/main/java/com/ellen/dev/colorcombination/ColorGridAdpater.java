package com.ellen.dev.colorcombination;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ColorGridAdpater extends RecyclerView.Adapter<ColorGridAdpater.ViewHolder> {

    List<MainActivity.MyColor> colors;

    public ColorGridAdpater(List<MainActivity.MyColor> colors) {
        this.colors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View mView = inflater.inflate(R.layout.item_color_grid, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(mView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainActivity.MyColor color = colors.get(position);
        holder.item_color_grid_ll.setBackgroundColor(color.getColor());
//        holder.item_color_grid_ll.setBackgroundColor(Color.rgb(Color.red(color.getColor()), Color.green(color.getColor()), Color.blue(color.getColor())));
        holder.item_position_x_tv.setText(color.getX_coordinate());
        holder.item_position_y_tv.setText(color.getY_coordinate());

    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_color_grid_ll;
        private TextView item_position_x_tv;
        private TextView item_position_y_tv;

        public ViewHolder(View convertView) {
            super(convertView);
            item_color_grid_ll = (LinearLayout) convertView.findViewById(R.id.item_color_grid_ll);
            item_position_x_tv = (TextView) convertView.findViewById(R.id.item_position_x_tv);
            item_position_y_tv = (TextView) convertView.findViewById(R.id.item_position_y_tv);
        }
    }


}
