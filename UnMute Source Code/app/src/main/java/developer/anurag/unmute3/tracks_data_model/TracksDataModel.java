package developer.anurag.unmute3.tracks_data_model;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import developer.anurag.unmute3.unmute_3_api.Track;

public class TracksDataModel extends ViewModel {
    private ArrayList<Track> fetchedTracks=new ArrayList<>();
    private ArrayList<Track> quickPicksTracks=new ArrayList<>();
    private ArrayList<Track> randomPickTracks=new ArrayList<>();
    private MutableLiveData<ArrayList<Track>> searchedTracks=new MutableLiveData<>();
    private ArrayList<ArrayList<Track>> mixingTracks=new ArrayList<>();

    private final MutableLiveData<Fragment> fragmentOnScreen=new MutableLiveData<>();
    private  Fragment backStackedFragment;

    public ArrayList<Track> getFetchedTracks() {
        return fetchedTracks;
    }

    public void setFetchedTracks(ArrayList<Track> fetchedTracks) {
        this.fetchedTracks = fetchedTracks;
    }

    public ArrayList<Track> getQuickPicksTracks() {
        return quickPicksTracks;
    }

    public void setQuickPicksTracks(ArrayList<Track> quickPicksTracks) {
        this.quickPicksTracks = quickPicksTracks;
    }

    public ArrayList<Track> getRandomPickTracks() {
        return randomPickTracks;
    }

    public void setRandomPickTracks(ArrayList<Track> randomPickTracks) {
        this.randomPickTracks = randomPickTracks;
    }

    public MutableLiveData<ArrayList<Track>> getSearchedTracks() {
        return searchedTracks;
    }

    public void setSearchedTracks(ArrayList<Track> searchedTracks) {
        this.searchedTracks.setValue(searchedTracks);
    }


    public MutableLiveData<Fragment> getFragmentOnScreen() {
        return fragmentOnScreen;
    }

    public void setFragmentOnScreen(Fragment fragmentOnScreen) {
        this.fragmentOnScreen.setValue(fragmentOnScreen);
    }

    public Fragment getBackStackedFragment() {
        return backStackedFragment;
    }

    public void setBackStackedFragment(Fragment backStackedFragment) {
        this.backStackedFragment = backStackedFragment;
    }

    public ArrayList<ArrayList<Track>> getMixingTracks() {
        return mixingTracks;
    }

    public void setMixingTracks(ArrayList<ArrayList<Track>> mixingTracks) {
        this.mixingTracks = mixingTracks;
    }
}
