package developer.anurag.unmute3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import developer.anurag.unmute3.listeners.TrackMixedViewAdapterListener;
import developer.anurag.unmute3.unmute_3_api.Track;

public class TrackMixedViewAdapter extends RecyclerView.Adapter<TrackMixedViewAdapter.TrackMixedViewHolder> {
    private ArrayList<ArrayList<Track>> tracks;
    private Context context;
    private int posterSize;
    private TrackMixedViewAdapterListener adapterListener;

    public TrackMixedViewAdapter(Context context,ArrayList<ArrayList<Track>> tracks){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,120);
    }




    @NonNull
    @Override
    public TrackMixedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.context).inflate(R.layout.track_mixed_view,parent,false);
        return new TrackMixedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackMixedViewHolder holder, int position) {
        try{
            ArrayList<Track> mixedTracks=this.tracks.get(position);
            if(!mixedTracks.isEmpty()){
                Track track=mixedTracks.get(0);

                // load poster
                Glide.with(this.context).load(track.getPosterUri()).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        Bitmap orgBitmap= ((BitmapDrawable) drawable).getBitmap();
                        Bitmap newBitmap=Bitmap.createScaledBitmap(orgBitmap,posterSize,posterSize,false);
                        holder.mixingPosterSIV.setImageBitmap(newBitmap);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
            }
        }catch (IndexOutOfBoundsException ignore){}

        // set click listener
        holder.mixingPosterSIV.setOnClickListener(view->{
            if(this.adapterListener!=null){
                this.adapterListener.onClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setTracks(ArrayList<ArrayList<Track>> tracks) {
        this.tracks = tracks;
        this.notifyDataSetChanged();
    }

    public void addListener(TrackMixedViewAdapterListener listener){
        this.adapterListener=listener;
    }

    public static class TrackMixedViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView mixingPosterSIV;
        public TrackMixedViewHolder(@NonNull View view) {
            super(view);
            this.mixingPosterSIV=view.findViewById(R.id.tmvMixingPosterIV);

        }
    }
}
