package com.example.simplesearch.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.simplesearch.R

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, imageUrl: String?)  {
    imageUrl?.let {
        imageView.load(imageUrl){
            //placeholder(R.drawable.ic_launcher_foreground)
            error(R.drawable.baseline_broken_image_24)
        }
    }
}
