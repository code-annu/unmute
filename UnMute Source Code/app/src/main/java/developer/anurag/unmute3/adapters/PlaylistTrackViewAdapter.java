package developer.anurag.unmute3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.helpers.DPPixelConvertor;
import developer.anurag.unmute3.listeners.PlayingTrackViewAdapterListener;
import developer.anurag.unmute3.unmute_3_api.Track;

public class PlaylistTrackViewAdapter extends RecyclerView.Adapter<PlaylistTrackViewAdapter.PlaylistTrackViewHolder> {
    private ArrayList<Track> tracks;
    private int posterSize;
    private Context context;
    private PlayingTrackViewAdapterListener playingTrackViewAdapterListener;
    private MediaController mediaController;

    public PlaylistTrackViewAdapter(Context context,ArrayList<Track> tracks){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,50);
    }

    @NonNull
    @Override
    public PlaylistTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.context).inflate(R.layout.playing_track_view,parent,false);
        return new PlaylistTrackViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull PlaylistTrackViewHolder holder, int position) {
        Track track=this.tracks.get(position);
        holder.titleTV.setText(track.getTitle());
        holder.artistTV.setText(track.getArtists());
        this.loadPoster(holder.posterIV,track.getPosterUri());
        if(this.playingTrackViewAdapterListener!=null){
            holder.mainContainerCL.setOnClickListener(view->{
                this.playingTrackViewAdapterListener.onClick(position);
            });
        }

        if(this.mediaController!=null){
            if(position==this.mediaController.getCurrentMediaItemIndex()){
                holder.mainContainerCL.setSelected(true);
                holder.mainContainerCL.setBackgroundResource(R.drawable.playing_track_bg);
            }else {
                holder.mainContainerCL.setSelected(false);
                holder.mainContainerCL.setBackgroundResource(R.drawable.playing_track_bg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }


    public void addListener(PlayingTrackViewAdapterListener listener){
        this.playingTrackViewAdapterListener=listener;
    }


    public void setMediaController(MediaController mediaController) {
        this.mediaController = mediaController;
        this.mediaController.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                notifyDataSetChanged();

            }
        });
    }


    public void setPlayingTrackBG(PlaylistTrackViewHolder removeBGOfViewHolder,PlaylistTrackViewHolder setBGOfViewHolder){
        if(removeBGOfViewHolder!=null){
            removeBGOfViewHolder.mainContainerCL.setSelected(false);
            removeBGOfViewHolder.mainContainerCL.setBackgroundResource(R.drawable.playing_track_bg );
        }
        if(setBGOfViewHolder!=null){
            setBGOfViewHolder.mainContainerCL.setSelected(true);
            setBGOfViewHolder.mainContainerCL.setBackgroundResource(R.drawable.playing_track_bg);
        }

    }


    /*---------------------------------- Helper functions --------------------------------------*/
    private void loadPoster(ShapeableImageView imageView,String posterUri){
        Glide.with(this.context).load(posterUri).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                Bitmap orgBitmap= ((BitmapDrawable) drawable).getBitmap();
                float ratio= (float) orgBitmap.getWidth() /orgBitmap.getHeight();
                int height= (int) (posterSize/ratio);
                Bitmap newBitmap=Bitmap.createScaledBitmap(orgBitmap,posterSize,height,false);
                imageView.setImageBitmap(newBitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }


    public static class PlaylistTrackViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleTV,artistTV;
        private final ShapeableImageView posterIV;
        private final ImageButton dragTrackIB;
        private final ConstraintLayout mainContainerCL;
        public PlaylistTrackViewHolder(@NonNull View view) {
            super(view);
            this.titleTV=view.findViewById(R.id.ptvTrackTitleTV);
            this.posterIV=view.findViewById(R.id.ptvTrackPosterSIV);
            this.artistTV=view.findViewById(R.id.ptvTrackArtistTV);
            this.mainContainerCL=view.findViewById(R.id.ptvMainContainerCL);
            this.dragTrackIB=view.findViewById(R.id.ptvDragTrackIB);
        }
    }
}
