package com.viv.gunchung.ngawamovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viv.gunchung.ngawamovie.R;
import com.viv.gunchung.ngawamovie.models.MovieReview;

import java.util.List;

/**
 * Created by gunawan on 05/08/17.
 */

public class MoviesReviewAdapter extends RecyclerView.Adapter<MoviesReviewAdapter.MoviesReviewAdapterViewHolder> {

    private static final String TAG = MoviesReviewAdapter.class.getSimpleName();

    private List<MovieReview> mReviewList;


    public MoviesReviewAdapter() {

    }

    public class MoviesReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public MoviesReviewAdapterViewHolder(View view) {
            super(view);

            mReviewAuthor = (TextView) view.findViewById(R.id.tv_detail_review_author);
            mReviewContent = (TextView) view.findViewById(R.id.tv_detail_review_content);
        }
    }

    @Override
    public MoviesReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MoviesReviewAdapterViewHolder viewHolder = new MoviesReviewAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesReviewAdapterViewHolder holder, int position) {
        MovieReview review = mReviewList.get(position);

        holder.mReviewAuthor.setText(review.getAuthor());
        holder.mReviewContent.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        if (null == mReviewList) return 0;
        return mReviewList.size();
    }

    public List<MovieReview> getReviewList() {
        return mReviewList;
    }

    public void setReviewList(List<MovieReview> mReviewList) {
        this.mReviewList = mReviewList;
        notifyDataSetChanged();
    }
}
