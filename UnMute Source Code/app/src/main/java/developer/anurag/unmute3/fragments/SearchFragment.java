package developer.anurag.unmute3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.controllers.SearchFragmentController;
import developer.anurag.unmute3.databinding.FragmentSearchBinding;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchFragmentController controller;
    private TracksDataModel tracksDataModel;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding=FragmentSearchBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setupSearchView();

        // setup TrackDataModel and CurrentPlayingPlaylistDataModel
        this.tracksDataModel=new ViewModelProvider(this.requireActivity()).get(TracksDataModel.class);
        this.currentPlayingTracksDataModel=new ViewModelProvider(this.requireActivity()).get(CurrentPlayingTracksDataModel.class);

        // setup controller
        this.controller=new SearchFragmentController(this.requireContext(),this.binding);
        this.controller.setTracksDataModel(this.tracksDataModel);
        this.controller.setCurrentPlayingTracksDataModel(this.currentPlayingTracksDataModel);
        this.controller.setupSearchViewListener();
        this.controller.setObserverForSearchedTracksList(requireActivity());
    }

    /*-------------------- Functions to init UI -------------------------*/
    private void setupSearchView(){
        EditText editText =this.binding.fsTrackSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setTextColor(requireContext().getColor(R.color.primaryWhite));
        editText.setHintTextColor(requireContext().getColor(R.color.graphite));
        editText.setTextSize(14f);
    }
}