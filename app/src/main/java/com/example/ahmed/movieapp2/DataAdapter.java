package com.example.ahmed.movieapp2;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


/**
 * Created by Ahmed on 2/2/2018.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {


    private static final String TAG = "DataAdapter";
    String[] titles;
    Uri[] imageArray;
    float[] ratings;
    private ItemClickListener mClickListener;

    DataAdapter(){}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movies_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//        String title = titles[position];
//        double rating = ratings[position];
        Uri image = imageArray[position];
//        Log.i(TAG, "onBindViewHolder: image = " + image);
//        Log.i(TAG, "onBindViewHolder: title = " + title);
//        Log.i(TAG, "onBindViewHolder: rating = " + rating);

//        holder.title.setText(title);
//        holder.rating.setText(Double.toString(rating));

        Picasso.with(holder.imageView.getContext()).load(image).into(holder.imageView);
//        Log.i(TAG, "on/BindViewHolder: context = " + holder.imageView.getContext());
    }


    @Override
    public int getItemCount() {
//        Log.i(TAG, "getItemCount: image = " + titles.length);
        if(null == imageArray) return 0;
        return titles.length;
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setData(Uri[] newImgUrls, String[] newTitles, float[] newRatings) {

        imageArray = newImgUrls;
        titles = newTitles;
        ratings = newRatings;

//        Log.i(TAG, "setData: newImgUrls = " + Arrays.toString(newImgUrls));


        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView imageView;
//        final TextView title, rating;

        ViewHolder(View itemView) {
            super(itemView);
//            Log.i(TAG, "ViewHolder: ");
            imageView = itemView.findViewById(R.id.movie_image);
//            title = itemView.findViewById(R.id.title);
//            rating = itemView.findViewById(R.id.rating);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

}

