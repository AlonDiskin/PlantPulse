package com.alon.plantpulse.usergarden.ui.controller;

import android.content.res.ColorStateList;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.alon.plantpulse.plantsdetail.ui.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.button.MaterialButton;

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

    /**
     * Dynamically sets the UI state of the add plant button based on whether the plant is already added.
     *
     * @param button  The MaterialButton to update.
     * @param isAdded True if the plant is already in the user's garden.
     */
    @BindingAdapter("plantAddedState")
    public static void setPlantAddedState(MaterialButton button, boolean isAdded) {
        if (isAdded) {
            button.setIconResource(R.drawable.plant_24dp);
            int color = ContextCompat.getColor(button.getContext(), R.color.light_green_added);
            button.setBackgroundTintList(ColorStateList.valueOf(color));
            button.setEnabled(false);
            button.setText(R.string.label_added);
        } else {
            button.setIconResource(R.drawable.add_24dp);
            // Revert to default background tint from the style (TonalButton)
            //button.setBackgroundTintList(null);
            button.setEnabled(true);
            button.setText(R.string.label_add_plant);
        }
    }
}
