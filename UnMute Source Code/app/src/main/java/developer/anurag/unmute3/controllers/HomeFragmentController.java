package developer.anurag.unmute3.controllers;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Objects;

import developer.anurag.unmute3.adapters.TrackMixedViewAdapter;
import developer.anurag.unmute3.adapters.TrackViewColumnAdapter;
import developer.anurag.unmute3.adapters.TrackViewItemDecoration;
import developer.anurag.unmute3.adapters.TrackViewRowAdapter;
import developer.anurag.unmute3.databinding.FragmentHomeBinding;
import developer.anurag.unmute3.listeners.MainActivityListener;
import developer.anurag.unmute3.listeners.TrackMixedViewAdapterListener;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;
import developer.anurag.unmute3.unmute_3_api.Track;
import developer.anurag.unmute3.unmute_3_api.UnMute3Service;
import developer.anurag.unmute3.unmute_3_api.UnMute3ServiceListener;

public class HomeFragmentController implements UnMute3ServiceListener{
    private Context context;
    private FragmentHomeBinding binding;
    private UnMute3Service unMute3Service;
    private TracksDataModel tracksDataModel;
    private TrackViewRowAdapter quickPicksAdapter;
    private TrackViewColumnAdapter randomPicksAdapter;
    private MediaController mediaController;
    private MainActivityListener mainActivityListener;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    private Track trackClicked;
    private TrackMixedViewAdapter mixedTrackAdapter;
    public HomeFragmentController(Context context,FragmentHomeBinding binding){
        this.context=context;
        this.binding=binding;
        this.unMute3Service=new UnMute3Service(this);

        // get the media controller
        this.mainActivityListener=(MainActivityListener) this.context;
        this.mediaController=this.mainActivityListener.getPlayerMediaController();
    }

    public void setTracksDataModel(TracksDataModel tracksDataModel){
        this.tracksDataModel=tracksDataModel;
    }

    public void setCurrentPlayingTracksDataModel(CurrentPlayingTracksDataModel currentPlayingTracksDataModel){
        this.currentPlayingTracksDataModel=currentPlayingTracksDataModel;
    }


    public void setupQuickPicksContainerRV(){
        Handler handler=new Handler();
        new Thread(()->{
            this.tracksDataModel.setQuickPicksTracks(UnMute3Service.getRandomTracks(this.tracksDataModel.getFetchedTracks(),20));
            handler.post(()->{
                this.quickPicksAdapter=new TrackViewRowAdapter(this.context,this.tracksDataModel.getQuickPicksTracks(),50,350);
                this.binding.fhQuickPicksTracksContainerRV.setAdapter(this.quickPicksAdapter);
                GridLayoutManager gridLayoutManager=new GridLayoutManager(this.context,4, RecyclerView.HORIZONTAL,false);
                this.binding.fhQuickPicksTracksContainerRV.setLayoutManager(gridLayoutManager);
                this.quickPicksAdapter.addListener(position -> {
                    Handler playHandler=new Handler();
                    playHandler.post(()->{
                        this.mainActivityListener.loadPlayerFragment();
                        ArrayList<Track> tracks=this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue();
                        if(tracks!=null){
                            tracks.clear();
                        }
                        this.mediaController.clearMediaItems();
                        try{
                            Track track=this.tracksDataModel.getQuickPicksTracks().get(position);
                            this.trackClicked=track;
                            this.currentPlayingTracksDataModel.getPlayingPlaylist().add(track);
                            this.unMute3Service.fetchTracksByArtists(track.getSimilarArtists(),25);
                        }catch (IndexOutOfBoundsException ignored){}
                    });
                });
            });
        }).start();

    }


    public void setupRandomPicksContainerRV(){
        Handler handler=new Handler();
        new Thread(()->{
            this.tracksDataModel.setRandomPickTracks(UnMute3Service.getRandomTracks(this.tracksDataModel.getFetchedTracks(),20));
            handler.post(()->{
                this.randomPicksAdapter=new TrackViewColumnAdapter(this.context,this.tracksDataModel.getRandomPickTracks());
                this.binding.fhRandomPicksTracksContainerRV.setAdapter(this.randomPicksAdapter);
                GridLayoutManager gridLayoutManager=new GridLayoutManager(this.context,2,RecyclerView.HORIZONTAL,false);
                this.binding.fhRandomPicksTracksContainerRV.setLayoutManager(gridLayoutManager);
                TrackViewItemDecoration itemDecoration=new TrackViewItemDecoration(this.context,10,10,10,10);
                this.binding.fhRandomPicksTracksContainerRV.addItemDecoration(itemDecoration);
                this.randomPicksAdapter.addListener(position -> {
                    Handler playHandler=new Handler();
                    playHandler.post(()->{
                        this.mainActivityListener.loadPlayerFragment();
                        ArrayList<Track> tracks=this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue();
                        if(tracks!=null){
                            tracks.clear();
                        }
                        this.mediaController.clearMediaItems();
                        try {
                            Track track=this.tracksDataModel.getRandomPickTracks().get(position);
                            this.trackClicked=track;
                            this.currentPlayingTracksDataModel.getPlayingPlaylist().add(track);
                            this.unMute3Service.fetchTracksByArtists(track.getSimilarArtists(),25);
                        }catch (IndexOutOfBoundsException ignored){}

                    });
                });
            });
        }).start();
    }


    public void setupMixingTracksContainerRV(){
        this.unMute3Service.fetchMixingTracks(20);
    }


    public void setupPlayAllClickListener(){
        // quick picks play all
        this.binding.fhPlayQuickPicksGroupBtnIV.setOnClickListener(view->{
            Handler handler=new Handler();
            handler.postDelayed(()->{
                this.mainActivityListener.loadPlayerFragment();
                this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(new ArrayList<>());
                this.mediaController.clearMediaItems();
                this.playAllTracks(this.tracksDataModel.getQuickPicksTracks());
            },100);
        });

        // random picks play all
        this.binding.fhPlayRandomPicksGroupBtnIV.setOnClickListener(view->{
            Handler handler=new Handler();
            handler.postDelayed(()->{
                this.mainActivityListener.loadPlayerFragment();
                this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(new ArrayList<>());
                this.mediaController.clearMediaItems();
                this.playAllTracks(this.tracksDataModel.getRandomPickTracks());
            },100);
        });
    }


    public void setupOnRefresh(){
        this.binding.fhSwipeRefreshLayout.setOnRefreshListener(() -> {
            this.unMute3Service.fetchSomeTracks(50);
        });
    }

    private void playAllTracks(ArrayList<Track> tracks){
        this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(tracks);
        Handler handler=new Handler();
        handler.post(()->{
            if(this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue()!=null){
                for(Track track:this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue()){
                    this.mediaController.addMediaItem(MediaItem.fromUri(track.getTrackUri()));
                }
                this.mediaController.prepare();
                this.mediaController.play();
            }

        });
    }

    @Override
    public void onFetchSomeTracks(ArrayList<Track> tracks) {
        this.tracksDataModel.setFetchedTracks(tracks);
        this.binding.fhSwipeRefreshLayout.setRefreshing(false);
        this.tracksDataModel.setQuickPicksTracks(UnMute3Service.getRandomTracks(tracks,20));
        this.tracksDataModel.setRandomPickTracks(UnMute3Service.getRandomTracks(tracks,20));
        this.quickPicksAdapter.setTracks(this.tracksDataModel.getQuickPicksTracks());
        this.randomPicksAdapter.setTracks(this.tracksDataModel.getRandomPickTracks());
    }

    @Override
    public void onSearchTracks(ArrayList<Track> tracks) {
    }

    @Override
    public void onFetchTracksByArtists(ArrayList<Track> tracks) {
        if(this.trackClicked!=null)tracks.add(0,this.trackClicked);
        this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(tracks);
        Handler handler=new Handler();
        handler.post(()->{
            for(Track track: Objects.requireNonNull(this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue())){
                this.mediaController.addMediaItem(MediaItem.fromUri(track.getTrackUri()));
            }
            this.mediaController.prepare();
            this.mediaController.play();

        });
    }


    @Override
    public void onFetchMixedTracks(ArrayList<Track> tracks) {
        if (this.tracksDataModel.getMixingTracks().size() <= 10) {
            this.tracksDataModel.getMixingTracks().add(tracks);
            this.unMute3Service.fetchMixingTracks(20);
        }
        if(this.mixedTrackAdapter==null){
            this.mixedTrackAdapter=new TrackMixedViewAdapter(this.context,this.tracksDataModel.getMixingTracks());
            this.mixedTrackAdapter.addListener(position -> {
                this.mainActivityListener.loadPlayerFragment();
                ArrayList<Track> mixingTracks=this.tracksDataModel.getMixingTracks().get(position);
                this.currentPlayingTracksDataModel.setCurrentPlayingPlaylist(mixingTracks);
                this.mediaController.clearMediaItems();
                for(Track track:mixingTracks){
                    this.mediaController.addMediaItem(MediaItem.fromUri(track.getTrackUri()));
                }
                this.mediaController.prepare();
                this.mediaController.play();
            });
            this.binding.fhMixingTracksContainerRV.setAdapter(this.mixedTrackAdapter);
        }else {
            this.mixedTrackAdapter.setTracks(this.tracksDataModel.getMixingTracks());
        }

    }
}
