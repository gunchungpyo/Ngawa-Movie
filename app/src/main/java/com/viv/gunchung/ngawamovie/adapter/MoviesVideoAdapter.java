package com.viv.gunchung.ngawamovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viv.gunchung.ngawamovie.R;
import com.viv.gunchung.ngawamovie.models.MovieVideo;
import com.viv.gunchung.ngawamovie.utilities.MovieUtils;

import java.util.List;

/**
 * Created by gunawan on 05/08/17.
 */

public class MoviesVideoAdapter extends RecyclerView.Adapter<MoviesVideoAdapter.MoviesVideoAdapterViewHolder> {

    private static final String TAG = MoviesVideoAdapter.class.getSimpleName();

    private final MoviesVideoAdapterOnClickHandler mClickHandler;

    private Context mContext;
    private List<MovieVideo> mVideoList;

    public interface MoviesVideoAdapterOnClickHandler {
        void onClick(MovieVideo selectedVideo);
    }

    public MoviesVideoAdapter(Context context, MoviesVideoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class MoviesVideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mVideoTrailer;
        public final ImageView mVideoPlay;
        public final TextView mVideoTitle;

        public MoviesVideoAdapterViewHolder(View view) {
            super(view);
            mVideoTrailer = (ImageView) view.findViewById(R.id.iv_detail_video_trailer);
            mVideoPlay = (ImageView) view.findViewById(R.id.iv_detail_video_play);
            mVideoTitle = (TextView) view.findViewById(R.id.tv_detail_video_title);
            mVideoPlay.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieVideo selectedVideo = mVideoList.get(adapterPosition);
            mClickHandler.onClick(selectedVideo);

        }
    }

    @Override
    public MoviesVideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MoviesVideoAdapterViewHolder viewHolder = new MoviesVideoAdapterViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesVideoAdapterViewHolder holder, int position) {
        MovieVideo video = mVideoList.get(position);

        holder.mVideoTitle.setText(video.getName());

        String trailerUrl = MovieUtils.generateYoutubeThumbUrl(video.getKey());
        Picasso.with(mContext).load(trailerUrl).into(holder.mVideoTrailer);
    }

    @Override
    public int getItemCount() {
        if (null == mVideoList) return 0;
        return mVideoList.size();
    }

    public void setVideoList(List<MovieVideo> mVideoList) {
        this.mVideoList = mVideoList;
        notifyDataSetChanged();
    }

    public List<MovieVideo> getVideoList() {
        return mVideoList;
    }
}
