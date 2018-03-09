package com.example.ahmed.movieapp2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ahmed on 3/7/2018.
 */

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    String[] trailers;
    private ItemClickListener mClickListener;

    Adapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
//        Log.i(TAG, "onCreateViewHolder: ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
        String trailer = trailers[position];
//        Log.i(TAG, "onBindViewHolder: " + trailer);
//        Log.i(TAG, "onBindViewHolder: " + Arrays.toString(trailers));
        holder.trailer.setText(trailer);
    }

    @Override
    public int getItemCount() {
        if (null == trailers) return 0;
//        Log.i("length", "getItemCount: " + trailers.length);
        return trailers.length;
    }

    void setClickListener(Adapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setData(String[] newTrailers) {

        trailers = newTrailers;
//        Log.i(TAG, "setData: " + Arrays.toString(newTrailers));

        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView trailer;

        ViewHolder(View itemView) {
            super(itemView);
//            Log.i(TAG, "ViewHolder: ");
            trailer = itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
