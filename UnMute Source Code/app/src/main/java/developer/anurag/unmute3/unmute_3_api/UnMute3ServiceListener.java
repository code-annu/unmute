package developer.anurag.unmute3.unmute_3_api;

import java.util.ArrayList;
import java.util.List;

public interface UnMute3ServiceListener {
    void onFetchSomeTracks(ArrayList<Track> tracks);
    void onSearchTracks(ArrayList<Track> tracks);
    void onFetchTracksByArtists(ArrayList<Track> tracks);
    void onFetchMixedTracks(ArrayList<Track> tracks);
}
