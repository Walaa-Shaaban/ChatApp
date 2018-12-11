package com.chat.walaashaaban.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class GridChatImagesAdapter extends ArrayAdapter<ImageItemChat> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItemChat> data = new ArrayList<ImageItemChat>();

    public GridChatImagesAdapter(Context context, int layoutResourceId, ArrayList<ImageItemChat> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        GridChatImagesAdapter.ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new GridChatImagesAdapter.ViewHolder();

            holder.image = (ImageView) row.findViewById(R.id.images_chat);
            row.setTag(holder);
        } else {
            holder = (GridChatImagesAdapter.ViewHolder) row.getTag();
        }


        ImageItemChat item = data.get(position);
        Picasso.with(GridChatImagesAdapter.this.getContext()).load(item.getImage()).placeholder(R.drawable.profile).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryImageIntent = new Intent(context, ViewPagerAdapterImagesChatActivity.class);
               galleryImageIntent.putExtra("position", position);
                context.startActivity(galleryImageIntent);


            }
        });

        return row;
    }

    static class ViewHolder {
        ImageView image;

    }


}