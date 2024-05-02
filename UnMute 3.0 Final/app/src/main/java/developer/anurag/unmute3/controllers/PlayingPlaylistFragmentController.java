package developer.anurag.unmute3.controllers;

import android.content.Context;

import androidx.media3.session.MediaController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import developer.anurag.unmute3.adapters.PlaylistTrackViewAdapter;
import developer.anurag.unmute3.databinding.FragmentPlayingPlaylistBinding;
import developer.anurag.unmute3.listeners.MainActivityListener;
import developer.anurag.unmute3.listeners.PlayingTrackViewAdapterListener;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;

public class PlayingPlaylistFragmentController {
    private final FragmentPlayingPlaylistBinding binding;
    private final Context context;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    private MainActivityListener mainActivityListener;
    private final MediaController mediaController;

    public PlayingPlaylistFragmentController(Context context,FragmentPlayingPlaylistBinding binding){
        this.context=context;
        this.binding=binding;

        // setup media controller
        this.mainActivityListener=(MainActivityListener) context;
        this.mediaController=this.mainActivityListener.getPlayerMediaController();
    }

    public void setCurrentPlayingTracksDataModel(CurrentPlayingTracksDataModel currentPlayingTracksDataModel) {
        this.currentPlayingTracksDataModel = currentPlayingTracksDataModel;
    }


    public void setPlayingPlaylistContainerRV(){
        PlaylistTrackViewAdapter playlistTrackViewAdapter=new PlaylistTrackViewAdapter(this.context,this.currentPlayingTracksDataModel.getCurrentPlayingPlaylist().getValue());
        playlistTrackViewAdapter.setMediaController(this.mediaController);
        playlistTrackViewAdapter.addListener(new PlayingTrackViewAdapterListener() {
            @Override
            public void onClick(int position) {
                if(position!=mediaController.getCurrentMediaItemIndex()){
                    playlistTrackViewAdapter.setPlayingTrackBG((PlaylistTrackViewAdapter.PlaylistTrackViewHolder) binding.fppPlayingPlaylistContainerRV.findViewHolderForAdapterPosition(mediaController.getCurrentMediaItemIndex()),(PlaylistTrackViewAdapter.PlaylistTrackViewHolder) binding.fppPlayingPlaylistContainerRV.findViewHolderForAdapterPosition(position));
                    mediaController.seekTo(position,0);
                }
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this.context, RecyclerView.VERTICAL,false);
        this.binding.fppPlayingPlaylistContainerRV.setLayoutManager(linearLayoutManager);
        this.binding.fppPlayingPlaylistContainerRV.setAdapter(playlistTrackViewAdapter);
    }
}
