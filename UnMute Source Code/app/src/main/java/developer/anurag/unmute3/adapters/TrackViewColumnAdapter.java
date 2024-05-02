package developer.anurag.unmute3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.helpers.DPPixelConvertor;
import developer.anurag.unmute3.listeners.TrackViewColumnAdapterListener;
import developer.anurag.unmute3.unmute_3_api.Track;

public class TrackViewColumnAdapter extends RecyclerView.Adapter<TrackViewColumnAdapter.ColumnTrackViewHolder> {
    private ArrayList<Track> tracks;
    private final Context context;
    private final int posterSize;
    private TrackViewColumnAdapterListener adapterListener;


    public TrackViewColumnAdapter(Context context,ArrayList<Track> tracks){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,100);
    }

    public TrackViewColumnAdapter(Context context,ArrayList<Track> tracks,float posterSize){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,posterSize);
    }


    @NonNull
    @Override
    public ColumnTrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.context).inflate(R.layout.track_view_col,parent,false);
        return new ColumnTrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColumnTrackViewHolder holder, int position) {
        Track track=this.tracks.get(position);
        holder.titleTV.setText(track.getTitle());
        this.loadPoster(holder.posterSIV,track.getPosterUri());
        if(this.adapterListener!=null){
            holder.mainContainerLL.setOnClickListener(view->{
                this.adapterListener.onClick(position);
            });
        }

    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }


    public void addListener(TrackViewColumnAdapterListener listener){
        this.adapterListener=listener;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setTracks(ArrayList<Track> tracks){
        this.tracks=tracks;
        this.notifyDataSetChanged();
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

    public static class ColumnTrackViewHolder extends RecyclerView.ViewHolder{
        private final TextView titleTV;
        private final LinearLayout mainContainerLL;
        private final ShapeableImageView posterSIV;

        public ColumnTrackViewHolder(@NonNull View view) {
            super(view);
            this.titleTV=view.findViewById(R.id.tvcTrackTitleTV);
            this.mainContainerLL=view.findViewById(R.id.tvcMainContainerLL);
            this.posterSIV=view.findViewById(R.id.tvcTrackPosterSIV);
        }
    }
}
