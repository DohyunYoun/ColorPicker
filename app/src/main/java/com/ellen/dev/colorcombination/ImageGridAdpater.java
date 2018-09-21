package com.ellen.dev.colorcombination;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ImageGridAdpater extends RecyclerView.Adapter<ImageGridAdpater.ViewHolder> {

    List<Bitmap> colors;
    Context context;

    public ImageGridAdpater(List<Bitmap> colors, Context context) {
        this.colors = colors;
        this.context = context;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Bitmap bitmap = colors.get(position);
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        holder.item_color_grid_ll.setBackground(d);

//        Palette palette = Palette.from(colors.get(position)).generate();
//        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
//        if (vibrantSwatch != null) {
//            int rgb = vibrantSwatch.getRgb();
//            holder.item_color_grid_background_ll.setBackgroundColor(rgb);
//        }


        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
                Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
                if (vibrantSwatch != null) {
                    int rgb = vibrantSwatch.getRgb();
                    holder.item_color_grid_background_ll.setBackgroundColor(rgb);
                }
            }
        });

        holder.item_color_grid_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Palette palette = Palette.from(colors.get(position)).generate();
                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                if (vibrantSwatch != null) {
                    int rgb = vibrantSwatch.getRgb();
                    holder.item_color_grid_background_ll.setBackgroundColor(rgb);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item_color_grid_ll;
        private LinearLayout item_color_grid_background_ll;
        private TextView item_position_x_tv;
        private TextView item_position_y_tv;

        public ViewHolder(View convertView) {
            super(convertView);
            item_color_grid_ll = (LinearLayout) convertView.findViewById(R.id.item_color_grid_ll);
            item_color_grid_background_ll = (LinearLayout) convertView.findViewById(R.id.item_color_grid_background_ll);
            item_position_x_tv = (TextView) convertView.findViewById(R.id.item_position_x_tv);
            item_position_y_tv = (TextView) convertView.findViewById(R.id.item_position_y_tv);
        }
    }


}
