package com.example.ahmed.movieapp2;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Ahmed on 3/7/2018.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();
    String[] users, reviews;

    ReviewAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String user = users[position];
        String review = reviews[position];

        holder.user.setText(user);
        holder.review.setText(review);
    }

    @Override
    public int getItemCount() {
        if (null == users) return 0;
        return users.length;
    }

    public void setData(String[] newUsers, String[] newReviews) {

        users = newUsers;
        reviews = newReviews;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView user, review;

        //TODO Bind views
        public ViewHolder(View itemView) {
            super(itemView);

            user = itemView.findViewById(R.id.user_name);
            review = itemView.findViewById(R.id.user_review);

        }

    }
}