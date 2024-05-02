package developer.anurag.unmute3.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;

import java.util.ArrayList;

import developer.anurag.unmute3.adapters.TrackViewItemDecoration;
import developer.anurag.unmute3.adapters.TrackViewRowAdapter;
import developer.anurag.unmute3.databinding.FragmentSearchBinding;
import developer.anurag.unmute3.listeners.MainActivityListener;
import developer.anurag.unmute3.listeners.TrackViewRowAdapterListener;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;
import developer.anurag.unmute3.unmute_3_api.Track;
import developer.anurag.unmute3.unmute_3_api.UnMute3Service;
import developer.anurag.unmute3.unmute_3_api.UnMute3ServiceListener;

public class SearchFragmentController implements UnMute3ServiceListener {
    private final Context context;
    private final FragmentSearchBinding binding;
    private TracksDataModel tracksDataModel;
    private final UnMute3Service unMute3Service;
    private Track clickedTrack;
    private TrackViewRowAdapter searchedTracksRVAdapter;
    private final MainActivityListener mainActivityListener;
    private final MediaController mediaController;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;

    public SearchFragmentController(Context context, FragmentSearchBinding binding){
        this.context=context;
        this.binding=binding;
        this.unMute3Service=new UnMute3Service(this);
        this.mainActivityListener=(MainActivityListener) this.context;
        this.mediaController=this.mainActivityListener.getPlayerMediaController();
    }


    public void setTracksDataModel(TracksDataModel tracksDataModel) {
        this.tracksDataModel = tracksDataModel;
    }


    public void setCurrentPlayingTracksDataModel(CurrentPlayingTracksDataModel currentPlayingTracksDataModel) {
        this.currentPlayingTracksDataModel = currentPlayingTracksDataModel;
    }


    public void setupSearchViewListener(){
        this.binding.fsTrackSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.trim().isEmpty()){
                    if(tracksDataModel.getSearchedTracks().getValue()!=null){
                        tracksDataModel.getSearchedTracks().getValue().clear();
                    }
                }
                else {
                    showProgressBar();
                    unMute3Service.searchTrack(query.trim());
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }



    public void setObserverForSearchedTracksList(LifecycleOwner lifecycleOwner){
        this.tracksDataModel.getSearchedTracks().observe(lifecycleOwner, tracks -> {
            if(this.searchedTracksRVAdapter==null){
                this.searchedTracksRVAdapter=new TrackViewRowAdapter(this.context,tracks);
                this.searchedTracksRVAdapter.addListener(new TrackViewRowAdapterListener() {
                    @Override
                    public void onClick(int position) {
                        onTrackClick(position);
                    }
                });
                this.binding.fsSearchedTracksContainerRV.setAdapter(searchedTracksRVAdapter);
            }else {
                this.searchedTracksRVAdapter.setTracks(tracks);
            }
            hideProgressBar();
        });
    }


    private void onTrackClick(int position){
        this.mainActivityListener.loadPlayerFragment();
        this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(new ArrayList<>());
        this.mediaController.clearMediaItems();
        ArrayList<Track> tracks=this.tracksDataModel.getSearchedTracks().getValue();
        if(tracks!=null){
            this.clickedTrack=this.tracksDataModel.getSearchedTracks().getValue().get(position);
        }
        this.unMute3Service.fetchTracksByArtists(this.clickedTrack.getSimilarArtists(),20);
    }

    public void showProgressBar(){
        this.binding.fsProgressBarContainerFL.setVisibility(View.VISIBLE);
        ObjectAnimator animator=ObjectAnimator.ofFloat(this.binding.fsProgressBarContainerFL,"alpha",0,1);
        animator.setDuration(200);
        animator.start();
    }

    public void hideProgressBar(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(this.binding.fsProgressBarContainerFL,"alpha",1,0);
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.fsProgressBarContainerFL.setVisibility(View.GONE);
            }
        });
        animator.start();
    }


    @Override
    public void onFetchSomeTracks(ArrayList<Track> tracks) {

    }

    @Override
    public void onSearchTracks(ArrayList<Track> tracks) {
        this.tracksDataModel.setSearchedTracks(tracks);
    }

    @Override
    public void onFetchTracksByArtists(ArrayList<Track> tracks) {
        if(this.clickedTrack!=null)tracks.add(0,this.clickedTrack);
        this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(tracks);
        Handler handler=new Handler();
        handler.post(()->{

            for(Track track:tracks){
                this.mediaController.addMediaItem(MediaItem.fromUri(track.getTrackUri()));
            }
            this.mediaController.prepare();
            this.mediaController.play();
        });
    }

    @Override
    public void onFetchMixedTracks(ArrayList<Track> tracks) {

    }
}

