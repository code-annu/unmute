package developer.anurag.unmute3.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.databinding.ActivityMainBinding;
import developer.anurag.unmute3.fragments.HomeFragment;
import developer.anurag.unmute3.fragments.LibraryFragment;
import developer.anurag.unmute3.fragments.LoadingFragment;
import developer.anurag.unmute3.fragments.PlayerFragment;
import developer.anurag.unmute3.fragments.SearchFragment;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;
import developer.anurag.unmute3.unmute_3_api.Track;
import developer.anurag.unmute3.unmute_3_api.UnMute3Service;
import developer.anurag.unmute3.unmute_3_api.UnMute3ServiceListener;

public class MainActivityController implements UnMute3ServiceListener {
    private final ActivityMainBinding binding;
    private final Context context;
    private final HomeFragment homeFragment;
    private final SearchFragment searchFragment;
    private final LoadingFragment loadingFragment;
    private final LibraryFragment libraryFragment;
    private final PlayerFragment playerFragment;
    private FragmentManager fragmentManager;
    private UnMute3Service unMute3Service;
    private TracksDataModel tracksDataModel;
    private MediaController mediaController;
    private boolean isMiniPlayerVisible;
    private final long animShortDuration=200;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    public MainActivityController(Context context,ActivityMainBinding binding){
        this.context=context;
        this.binding=binding;
        this.unMute3Service=new UnMute3Service(this);

        // init fragments
        this.homeFragment=new HomeFragment();
        this.searchFragment=new SearchFragment();
        this.loadingFragment=new LoadingFragment();
        this.playerFragment=new PlayerFragment();
        this.libraryFragment=new LibraryFragment();
    }


    public void setTracksDataModelAndFetchTracks(TracksDataModel tracksDataModel){
        this.tracksDataModel=tracksDataModel;

        // fetch some tracks
        this.loadFragment(this.loadingFragment);
        this.unMute3Service.fetchSomeTracks(50);
    }


    public void setCurrentPlayingTracksDataModel(CurrentPlayingTracksDataModel currentPlayingTracksDataModel){
        this.currentPlayingTracksDataModel=currentPlayingTracksDataModel;
    }


    public void setFragmentManager(FragmentManager fragmentManager){
        this.fragmentManager=fragmentManager;
    }


    public void setupBottomNavigationListener(){
        this.binding.amBottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id= menuItem.getItemId();
            if(id==R.id.am_bnmHomeMI){
                this.loadFragment(this.homeFragment);
            }

            if(id==R.id.am_bnmLibraryMI){
                this.loadFragment(this.libraryFragment);
            }

            if(id==R.id.am_bnmSearchMI){
                this.loadFragment(this.searchFragment);
            }
            return true;
        });
    }


    public void onBackPress(AppCompatActivity compatActivity){
        Fragment fragmentOnScreen=this.tracksDataModel.getFragmentOnScreen().getValue();
        if(fragmentOnScreen!=this.playerFragment){
            Intent closeIntent = new Intent(Intent.ACTION_MAIN);
            closeIntent.addCategory(Intent.CATEGORY_HOME);
            compatActivity.startActivity(closeIntent);
        }else {
            Fragment stackedFragment=this.tracksDataModel.getBackStackedFragment();
            if(stackedFragment!=null && fragmentOnScreen!=null){
                FragmentTransaction transaction=this.fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.fragment_fade_in,R.anim.fragment_slide_down);
                transaction.hide(fragmentOnScreen);
                transaction.show(stackedFragment);
                transaction.commit();
                this.tracksDataModel.setFragmentOnScreen(stackedFragment);
            }
        }
    }


    public void setObserverForFragmentOnScreen(LifecycleOwner lifecycleOwner){
        this.tracksDataModel.getFragmentOnScreen().observe(lifecycleOwner, fragment -> {
            if(fragment==this.playerFragment){
                this.hideMiniPlayer();
                this.hideBottomNavigation();
                this.isMiniPlayerVisible=false;
            }else {
                if(!this.isMiniPlayerVisible && this.mediaController.getMediaItemCount()>0){
                    this.showMiniPlayer();
                    this.showBottomNavigation();
                    this.isMiniPlayerVisible=true;
                }
            }
        });
    }


    public void setMediaControllerListener(){
        this.mediaController.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                try{
                    int currentMediaIndex=mediaController.getCurrentMediaItemIndex();
                    if(currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue()!=null){
                        Track track=currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue().get(currentMediaIndex);
                        binding.mpTrackTitleTV.setText(track.getTitle());
                        binding.mpTrackArtistTV.setText(track.getArtists());
                        Glide.with(context).load(track.getPosterUri()).into(binding.mpTrackPosterIV);
                    }

                }catch (IndexOutOfBoundsException ignored){}
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if(isPlaying){
                    binding.mpPlayPauseIVBtn.setImageResource(R.drawable.pause_icon);
                }else {
                    binding.mpPlayPauseIVBtn.setImageResource(R.drawable.play_icon);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState==Player.STATE_READY){
                    if((binding.amMiniPlayerCL.getVisibility()==View.GONE || binding.amBottomNavigationView.getVisibility()==View.GONE) && tracksDataModel.getFragmentOnScreen().getValue()!=playerFragment){
                        showMiniPlayer();
                        showBottomNavigation();
                    }
                }
            }
        });
    }

    public void setupMiniPlayerListener(){
        // on mini player click
        this.binding.amMiniPlayerCL.setOnClickListener(view->{
            this.loadPlayerFragment();
        });

        // on play pause button click
        this.binding.mpPlayPauseIVBtn.setOnClickListener(view->{
            if(this.mediaController.isPlaying()){
                this.mediaController.pause();
            }else {
                this.mediaController.play();
            }
        });

    }


    public void setMediaController(MediaController mediaController){
        this.mediaController=mediaController;
    }


    public void showMiniPlayer(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(this.binding.amMiniPlayerCL,"translationY",400,0);
        this.binding.amMiniPlayerCL.setVisibility(View.VISIBLE);
        animator.setDuration(this.animShortDuration);
        animator.start();
    }

    public void showBottomNavigation(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(this.binding.amBottomNavigationView,"translationY",400,0);
        this.binding.amMiniPlayerCL.setVisibility(View.VISIBLE);
        animator.setDuration(this.animShortDuration);
        animator.start();
    }

    public void hideMiniPlayer(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(this.binding.amMiniPlayerCL,"translationY",0,400);
        animator.setDuration(this.animShortDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.amMiniPlayerCL.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    public void hideBottomNavigation(){
        ObjectAnimator animator=ObjectAnimator.ofFloat(this.binding.amBottomNavigationView,"translationY",0,400);
        animator.setDuration(this.animShortDuration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.amMiniPlayerCL.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private void loadFragment(Fragment fragmentToShow){
        Fragment fragmentToHide=this.tracksDataModel.getFragmentOnScreen().getValue();
        FragmentTransaction transaction=this.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in,R.anim.fragment_fade_out);
        if(fragmentToHide!=null){
            transaction.hide(fragmentToHide);
        }
        if(!this.fragmentManager.getFragments().contains(fragmentToShow)){
            transaction.add(R.id.amMainFragmentContainerView,fragmentToShow);
        }
        transaction.show(fragmentToShow);
        transaction.commit();
        this.tracksDataModel.setFragmentOnScreen(fragmentToShow);
    }

    public void loadPlayerFragment(){
        Fragment fragmentToHide=this.tracksDataModel.getFragmentOnScreen().getValue();
        this.tracksDataModel.setBackStackedFragment(fragmentToHide);
        FragmentTransaction transaction=this.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_slide_up,R.anim.fragment_fade_out);
        if(fragmentToHide!=null){
            transaction.hide(fragmentToHide);
        }
        if(!this.fragmentManager.getFragments().contains(this.playerFragment)){
            transaction.add(R.id.amMainFragmentContainerView,this.playerFragment);
        }
        transaction.show(this.playerFragment);
        transaction.commit();
        this.tracksDataModel.setFragmentOnScreen(this.playerFragment);
    }

    public void hidePlayerFragment(){
        Fragment fragmentToShow=this.tracksDataModel.getBackStackedFragment();
        FragmentTransaction transaction=this.fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_in,R.anim.fragment_slide_down);
        transaction.hide(this.playerFragment);
        if(fragmentToShow!=null)transaction.show(fragmentToShow);
        transaction.commit();
    }


    @Override
    public void onFetchSomeTracks(ArrayList<Track> tracks) {
        this.tracksDataModel.setFetchedTracks(tracks);
        this.loadFragment(this.homeFragment);
    }

    @Override
    public void onSearchTracks(ArrayList<Track> tracks) {

    }

    @Override
    public void onFetchTracksByArtists(ArrayList<Track> tracks) {

    }

    @Override
    public void onFetchMixedTracks(ArrayList<Track> tracks) {

    }
}
