package com.chat.walaashaaban.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapterImagesChat extends PagerAdapter {


    // Declare Variables
    Context context;
    List<String> imagesChat;
    LayoutInflater inflater;

    public ViewPagerAdapterImagesChat(Context context, List<String> imagesChat) {
        this.context = context;
        this.imagesChat = imagesChat;
    }

    @Override
    public int getCount() {
        return imagesChat.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position) {


        ImageView imgChat;
        TextView number_images;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.show_item_images_chat, container,
                false);

        imgChat = (ImageView) itemView.findViewById(R.id.album_chat_images);
        number_images = (TextView) itemView.findViewById(R.id.txt_number_images_album);

        number_images.setText(String.valueOf(position+1) + "/"+imagesChat.size());

        Picasso.with(context).load(String.valueOf(imagesChat.get(position))).into(imgChat);


        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }


}




