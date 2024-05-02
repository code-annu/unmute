package developer.anurag.unmute3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.helpers.DPPixelConvertor;
import developer.anurag.unmute3.listeners.TrackViewRowAdapterListener;
import developer.anurag.unmute3.unmute_3_api.Track;

public class TrackViewRowAdapter extends RecyclerView.Adapter<TrackViewRowAdapter.RowTrackViewHolder> {
    private ArrayList<Track> tracks;
    private final Context context;
    private final int posterSize;
    private int containerWidth=ConstraintLayout.LayoutParams.MATCH_PARENT;
    private TrackViewRowAdapterListener adapterListener;

    public TrackViewRowAdapter(Context context,ArrayList<Track> tracks){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,50);
    }

    public TrackViewRowAdapter(Context context,ArrayList<Track> tracks,float posterSize,float containerWidth){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,posterSize);
        this.containerWidth=DPPixelConvertor.dpToPixel(this.context,containerWidth);
    }

    @NonNull
    @Override
    public RowTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.context).inflate(R.layout.track_view_row,parent,false);
        return new RowTrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowTrackViewHolder holder, int position) {
        try{
            Track track=this.tracks.get(position);
            holder.titleTV.setText(track.getTitle());
            holder.artistsTV.setText(track.getArtists());
            ConstraintLayout.LayoutParams layoutParams=new ConstraintLayout.LayoutParams(this.containerWidth,ConstraintLayout.LayoutParams.WRAP_CONTENT);
            holder.mainContainerCL.setLayoutParams(layoutParams);
            this.loadPoster(holder.posterSIV,track.getPosterUri());
            if(this.adapterListener!=null){
                holder.mainContainerCL.setOnClickListener(view->{
                    this.adapterListener.onClick(position);
                });
            }
        }catch (IndexOutOfBoundsException ignored){}
    }


    @Override
    public int getItemCount() {
        return this.tracks.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setTracks(ArrayList<Track> tracks){
        this.tracks=tracks;
        this.notifyDataSetChanged();
    }

    public void addListener(TrackViewRowAdapterListener listener){
        this.adapterListener=listener;
    }

    /* ----------------------- Helper functions ---------------------------*/
    private void loadPoster(ShapeableImageView imageView,String posterUri){
        Glide.with(this.context).load(Uri.parse(posterUri)).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                Bitmap orgBitmap= ((BitmapDrawable) drawable).getBitmap();
                float ratio= (float) orgBitmap.getWidth() /orgBitmap.getHeight();
                int height= (int) (posterSize/ratio);
                Bitmap newBitmap=Bitmap.createScaledBitmap(orgBitmap,posterSize,height,true);
                imageView.setImageBitmap(newBitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public static class RowTrackViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTV,artistsTV;
        private final ShapeableImageView posterSIV;
        private final ConstraintLayout mainContainerCL;
        private final ImageView actionIV;
        public RowTrackViewHolder(@NonNull View view) {
            super(view);
            this.titleTV=view.findViewById(R.id.tvrTrackTitleTV);
            this.artistsTV=view.findViewById(R.id.tvrTrackArtistTV);
            this.posterSIV=view.findViewById(R.id.tvrTrackPosterIV);
            this.mainContainerCL=view.findViewById(R.id.tvrMainContainerCL);
            this.actionIV=view.findViewById(R.id.tvrActionMenuIVBtn);
        }
    }
}
