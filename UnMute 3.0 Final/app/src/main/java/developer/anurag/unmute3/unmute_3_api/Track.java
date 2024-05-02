package developer.anurag.unmute3.unmute_3_api;

import java.util.ArrayList;

public class Track {
    private long id;
    private String title,artists,posterUri,trackUri;
    private ArrayList<String> similarArtists;

    public Track() {
    }

    public Track(long id, String title, String artists, String posterUri, String trackUri, ArrayList<String> similarArtists) {
        this.id = id;
        this.title = title;
        this.artists = artists;
        this.posterUri = posterUri;
        this.trackUri = trackUri;
        this.similarArtists = similarArtists;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtists() {
        return artists;
    }

    public void setArtists(String artists) {
        this.artists = artists;
    }

    public String getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(String posterUri) {
        this.posterUri = posterUri;
    }

    public String getTrackUri() {
        return trackUri;
    }

    public void setTrackUri(String trackUri) {
        this.trackUri = trackUri;
    }

    public ArrayList<String> getSimilarArtists() {
        return similarArtists;
    }

    public void setSimilarArtists(ArrayList<String> similarArtists) {
        this.similarArtists = similarArtists;
    }
}
