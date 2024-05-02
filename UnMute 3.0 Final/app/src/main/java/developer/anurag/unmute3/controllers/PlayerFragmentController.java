package developer.anurag.unmute3.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.adapters.TrackPosterViewAdapter;
import developer.anurag.unmute3.adapters.TrackViewItemDecoration;
import developer.anurag.unmute3.databinding.FragmentPlayerBinding;
import developer.anurag.unmute3.fragments.PlayingPlaylistFragment;
import developer.anurag.unmute3.listeners.MainActivityListener;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.unmute_3_api.Track;
import developer.anurag.unmute3.unmute_3_api.UnMute3Service;
import developer.anurag.unmute3.unmute_3_api.UnMute3ServiceListener;

public class PlayerFragmentController implements UnMute3ServiceListener {
    private final Context context;
    private final FragmentPlayerBinding binding;
    private MediaController mediaController;
    private final MainActivityListener mainActivityListener;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    private boolean seekbarDragging=false;
    private final UnMute3Service unMute3Service;
    private TrackPosterViewAdapter trackPostersViewAdapter;
    private PlayingPlaylistFragment playingPlaylistFragment;
    private int playingIndex=0;

    public PlayerFragmentController(Context context,FragmentPlayerBinding binding){
        this.context=context;
        this.binding=binding;

        // set main activity listener
        this.mainActivityListener=(MainActivityListener)this.context;

        // call player progress tracker
        this.playerProgressTracker();

        this.unMute3Service=new UnMute3Service(this);
    }

    public void setCurrentPlayingTracksDataModel(CurrentPlayingTracksDataModel currentPlayingTracksDataModel){
        this.currentPlayingTracksDataModel=currentPlayingTracksDataModel;
    }

    public void setupSeekbarListener(){
        this.binding.fpPlayerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarDragging=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaController.seekTo(seekBar.getProgress()* 1000L);
                seekbarDragging=false;
            }
        });
    }

    public void setupPlayPauseIBClickListener(){
        this.binding.fpPlayPauseIB.setOnClickListener(view->{
            if(this.mediaController.isPlaying())this.mediaController.pause();
            else this.mediaController.play();
        });
    }

    public void setupNextPreviousIBClickListener(){
        // on next button click
        this.binding.fpNextIB.setOnClickListener(view->{
            this.playNextTrack();
        });

        // on previous button click
        this.binding.fpPreviousIB.setOnClickListener(view->{
            if(!this.playPreviousTrack()){
                this.mediaController.seekTo(0);
            }
        });
    }


    public void playNextTrack(){
        if(this.mediaController.hasNextMediaItem()){
            this.mediaController.seekToNextMediaItem();
        }else {
            Track track=this.currentPlayingTracksDataModel.getPlayingPlaylist().get(0);
            this.unMute3Service.fetchTracksByArtists(track.getSimilarArtists(),15);
        }
    }

    public boolean playPreviousTrack(){
        if(this.mediaController.hasPreviousMediaItem()){
            this.mediaController.seekToPreviousMediaItem();
            return true;
        }else {
            return false;
        }
    }


    public void setupPlaylistBtnClickListener(FragmentManager fragmentManager){
        this.binding.fpPlaylistIB.setOnClickListener(view->{
            if(this.playingPlaylistFragment==null){
                this.playingPlaylistFragment=new PlayingPlaylistFragment();
            }
            this.playingPlaylistFragment.show(fragmentManager,null);
        });
    }


    public void setupMediaControllerListener(){
        this.mediaController=this.mainActivityListener.getPlayerMediaController();
        this.mediaController.addListener(new Player.Listener() {

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                updateUICurrentMediaItem();
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if(playbackState==Player.STATE_READY){
                    updateUICurrentMediaItem();
                    int duration=(int) (mediaController.getDuration()/1000);
                    binding.fpTrackDurationTV.setText(getTimeStrFromSeconds((duration)));
                    binding.fpPlayerSeekbar.setMax(duration);
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if(isPlaying){
                    binding.fpPlayPauseIB.setImageResource(R.drawable.pause_circle_icon);
                }else binding.fpPlayPauseIB.setImageResource(R.drawable.play_circle_icon);
            }
        });
    }


    public void setupClosIBClickListener(){
        this.binding.fpClosePlayerFragmentIB.setOnClickListener(view->{
            this.mainActivityListener.onBackNavigation();
        });
    }

    public void closePlayerFragment(){
        this.mainActivityListener.onBackNavigation();
    }
    private void playerProgressTracker(){
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,1000);
                if(!seekbarDragging){
                    int duration= (int) (mediaController.getCurrentPosition()/1000);
                    binding.fpPlayerPlayedDurationTV.setText(getTimeStrFromSeconds(duration));
                    binding.fpPlayerSeekbar.setProgress(duration);
                }
            }
        });

    }


    private void updateUICurrentMediaItem(){
        try{
            int currentMediaIndex=mediaController.getCurrentMediaItemIndex();
            if(currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue()!=null){
                Track track=currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue().get(currentMediaIndex);
                binding.fpTrackTitleTV.setText(track.getTitle());
                binding.fpTrackArtistTV.setText(track.getArtists());
                this.binding.fpTrackPostersContainerRV.scrollToPosition(this.mediaController.getCurrentMediaItemIndex());
            }

        }catch (IndexOutOfBoundsException ignored){}
    }


    public void setupCurrentPlayingPlaylistObserver(LifecycleOwner lifecycleOwner){
        this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().observe(lifecycleOwner, tracks -> {
            if(this.trackPostersViewAdapter==null){
                this.trackPostersViewAdapter=new TrackPosterViewAdapter(this.context,tracks,350);
                PagerSnapHelper pagerSnapHelper=new PagerSnapHelper();
                pagerSnapHelper.attachToRecyclerView(this.binding.fpTrackPostersContainerRV);
                TrackViewItemDecoration itemDecoration=new TrackViewItemDecoration(this.context,10,80,10,80);
                this.binding.fpTrackPostersContainerRV.addItemDecoration(itemDecoration);
                this.binding.fpTrackPostersContainerRV.setAdapter(this.trackPostersViewAdapter);
            }else {
                this.trackPostersViewAdapter.setTracks(tracks);
            }

        });
    }

    public void setupTrackPostersContainerRV(){
        this.binding.fpTrackPostersContainerRV.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                if(linearLayoutManager!=null){
                    int firstVisiblePosition=linearLayoutManager.findFirstVisibleItemPosition();
                    int lastVisiblePosition=linearLayoutManager.findLastVisibleItemPosition();
                    if(firstVisiblePosition==lastVisiblePosition && lastVisiblePosition!=playingIndex){
                        try{
                            mediaController.seekTo(lastVisiblePosition,0);
                            playingIndex=lastVisiblePosition;
                        }catch (IllegalArgumentException ignored){}
                    }
                }
            }
        });
    }


    @SuppressLint("DefaultLocale")
    private String getTimeStrFromSeconds(int seconds){
        int min=seconds/60;
        seconds=seconds%60;
        return String.format("%02d:%02d",min,seconds);
    }

    private void fetchMoreTracks(ArrayList<String> artistsList){
        this.unMute3Service.fetchTracksByArtists(artistsList,20);
    }


    @Override
    public void onFetchSomeTracks(ArrayList<Track> tracks) {

    }

    @Override
    public void onSearchTracks(ArrayList<Track> tracks) {

    }

    @Override
    public void onFetchTracksByArtists(ArrayList<Track> tracks) {
        Handler handler=new Handler();
        handler.post(()->{
            if(this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue()!=null){
                this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue().addAll(tracks);
            }
            for(Track track:tracks){
                this.mediaController.addMediaItem(MediaItem.fromUri(track.getTrackUri()));
            }
            this.mediaController.seekToNextMediaItem();
        });
    }


    @Override
    public void onFetchMixedTracks(ArrayList<Track> tracks) {

    }
}
