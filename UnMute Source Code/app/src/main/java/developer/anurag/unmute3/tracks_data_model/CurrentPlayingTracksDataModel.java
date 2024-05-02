package developer.anurag.unmute3.tracks_data_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import developer.anurag.unmute3.unmute_3_api.Track;

public class CurrentPlayingTracksDataModel extends ViewModel {
    private ArrayList<Track> playingPlaylist=new ArrayList<>();
    private final MutableLiveData<ArrayList<Track>> currentPlayingPlaylist=new MutableLiveData<>(new ArrayList<>());

    public ArrayList<Track> getPlayingPlaylist() {
        return playingPlaylist;
    }

    public void setPlayingPlaylist(ArrayList<Track> playingPlaylist) {
        this.playingPlaylist = playingPlaylist;
    }

    public MutableLiveData<ArrayList<Track>> getCurrentPlayingPlaylist() {
        return currentPlayingPlaylist;
    }

    public void setCurrentPlayingPlaylist(ArrayList<Track> currentPlayingPlaylist) {
        this.currentPlayingPlaylist.setValue(currentPlayingPlaylist);
    }
}
