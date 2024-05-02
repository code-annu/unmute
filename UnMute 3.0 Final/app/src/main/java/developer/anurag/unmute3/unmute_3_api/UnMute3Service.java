package developer.anurag.unmute3.unmute_3_api;

import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class UnMute3Service {
    private final UnMute3ServiceListener unMute3ServiceListener;
    private static final String TRACK_DATA_COLLECTION_PATH="track_data";
    private final Random random;
    private final CollectionReference trackDataCollection;


    public UnMute3Service(UnMute3ServiceListener listener){
        this.unMute3ServiceListener=listener;
        this.random=new Random();
        this.trackDataCollection= FirebaseFirestore.getInstance().collection(TRACK_DATA_COLLECTION_PATH);
    }


    public void fetchSomeTracks(int noOfTracks){
        this.trackDataCollection.get().addOnSuccessListener(collection -> {
            List<DocumentSnapshot> documents=collection.getDocuments();
            int totalNoOfDocuments=documents.size();
            HashSet<Track> tracksSet=new HashSet<>();
            for(int i=0;i<noOfTracks;i++){
                DocumentSnapshot document= documents.get(this.random.nextInt(totalNoOfDocuments));
                Track track=document.toObject(Track.class);
                if(track!=null)tracksSet.add(track);
            }
            this.unMute3ServiceListener.onFetchSomeTracks(new ArrayList<>(tracksSet));
            tracksSet.clear();
        });
    }


    public void searchTrack(String searchQuery){
        this.trackDataCollection.get().addOnSuccessListener(collection->{
            List<DocumentSnapshot> documents=collection.getDocuments();
            ArrayList<Track> resultTracks=new ArrayList<>();
            for(DocumentSnapshot document:documents){
                Track track=document.toObject(Track.class);
                if(track!=null){
                    String title=track.getTitle().trim().toLowerCase();
                    String artists=track.getArtists().trim().toLowerCase();
                    if(title.contains(searchQuery.toLowerCase()) || artists.contains(searchQuery.toLowerCase())){
                        resultTracks.add(track);
                    }
                }
            }
            this.unMute3ServiceListener.onSearchTracks(resultTracks);

        });
    }


    public void fetchTracksByArtists(ArrayList<String> artistsList,int noOfTracks){
        this.trackDataCollection.get().addOnSuccessListener(collection -> {
            ArrayList<Track> tracks=new ArrayList<>();
            for(DocumentSnapshot document:collection.getDocuments()){
                Track track=document.toObject(Track.class);
                if(track!=null){
                    String artists=track.getArtists().trim();
                    if(artistsList.contains(artists)){
                        tracks.add(track);
                    }
                }
            }

            HashSet<Track> selectedTracks=new HashSet<>();
            int totalNoOfTracks=tracks.size();
            for(int i=0;i<noOfTracks;i++){
                Track track=tracks.get(this.random.nextInt(totalNoOfTracks));
                selectedTracks.add(track);
            }
            this.unMute3ServiceListener.onFetchTracksByArtists(new ArrayList<>(selectedTracks));
            tracks.clear();
            selectedTracks.clear();

        });
    }


    public static ArrayList<Track> getRandomTracks(ArrayList<Track> allTracks,int noOfTracks) {
        HashSet<Track> randomTracks = new HashSet<>();
        Random random=new Random();
        int totalNoOfTracks=allTracks.size();
        for(int i=0;i<noOfTracks;i++){
            randomTracks.add(allTracks.get(random.nextInt(totalNoOfTracks)));
        }
        return new ArrayList<>(randomTracks);
    }

    public void fetchMixingTracks(int noOfTracks){
        this.trackDataCollection.get().addOnSuccessListener(collection -> {
            List<DocumentSnapshot> documents=collection.getDocuments();
            int noOfDocuments=documents.size();
            HashSet<Track> mixedTracks=new HashSet<>();
            for(int i=0;i<noOfTracks;i++){
                mixedTracks.add(documents.get(random.nextInt(noOfDocuments)).toObject(Track.class));
            }
            this.unMute3ServiceListener.onFetchMixedTracks(new ArrayList<>(mixedTracks));
            mixedTracks.clear();
        });
    }



}
