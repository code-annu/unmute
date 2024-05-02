package developer.anurag.unmute3.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import developer.anurag.unmute3.unmute_3_api.Track;

public class TrackPosterViewAdapter extends RecyclerView.Adapter<TrackPosterViewAdapter.TrackPosterViewHolder> {
    private ArrayList<Track> tracks;
    private Context context;
    private int posterSize;

    public TrackPosterViewAdapter(Context context,ArrayList<Track> tracks){
        this.context=context;
        this.tracks=tracks;
        this.posterSize= DPPixelConvertor.dpToPixel(this.context,350);
    }

    public TrackPosterViewAdapter(Context context,ArrayList<Track> tracks,float posterSize){
        this.context=context;
        this.tracks=tracks;
        this.posterSize=DPPixelConvertor.dpToPixel(this.context,posterSize);
    }

    @NonNull
    @Override
    public TrackPosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(this.context).inflate(R.layout.track_poster_view,parent,false);
        return new TrackPosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackPosterViewHolder holder, int position) {
        Track track=tracks.get(position);

        // load poster
        Glide.with(this.context).load(track.getPosterUri()).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                Bitmap orgBitmap= ((BitmapDrawable) drawable).getBitmap();
                float ratio= (float) orgBitmap.getWidth() /orgBitmap.getHeight();
                int height= (int) (posterSize/ratio);
                Bitmap newBitmap=Bitmap.createScaledBitmap(orgBitmap,posterSize,height,false);
                holder.trackPosterSIV.setImageBitmap(newBitmap);


            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.tracks.size();
    }


//    @SuppressLint("NotifyDataSetChanged")
    public void setTracks(ArrayList<Track> tracks) {
        this.tracks = tracks;
        this.notifyDataSetChanged();
    }

    public static class TrackPosterViewHolder extends RecyclerView.ViewHolder{
        final private ShapeableImageView trackPosterSIV;
        final private LinearLayout mainContainerLL;
        public TrackPosterViewHolder(@NonNull View view) {
            super(view);
            this.trackPosterSIV=view.findViewById(R.id.tpvTrackPosterView);
            this.mainContainerLL=view.findViewById(R.id.tpvMainContainerLL);
        }
    }
}
