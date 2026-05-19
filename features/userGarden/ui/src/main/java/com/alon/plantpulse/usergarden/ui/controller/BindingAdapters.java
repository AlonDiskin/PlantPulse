package com.alon.plantpulse.usergarden.ui.controller;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class BindingAdapters {

    /**
     * Custom adapter for 'imageUrl' that loads an image from a URL into an ImageView using Glide.
     */
    @BindingAdapter("imageUrl")
    public static void imageUrl(ImageView view, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(view.getContext())
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view);
        }
    }

}
