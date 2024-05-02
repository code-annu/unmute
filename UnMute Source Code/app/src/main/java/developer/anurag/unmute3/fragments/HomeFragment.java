package developer.anurag.unmute3.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.controllers.HomeFragmentController;
import developer.anurag.unmute3.databinding.FragmentHomeBinding;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeFragmentController controller;
    private TracksDataModel tracksDataModel;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding=FragmentHomeBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.tracksDataModel=new ViewModelProvider(requireActivity()).get(TracksDataModel.class);
        this.currentPlayingTracksDataModel=new ViewModelProvider(requireActivity()).get(CurrentPlayingTracksDataModel.class);

        // attach and setup controller
        this.controller=new HomeFragmentController(this.requireContext(),this.binding);
        this.controller.setTracksDataModel(this.tracksDataModel);
        this.controller.setCurrentPlayingTracksDataModel(this.currentPlayingTracksDataModel);
        this.controller.setupQuickPicksContainerRV();
        this.controller.setupRandomPicksContainerRV();
        this.controller.setupMixingTracksContainerRV();
        this.controller.setupPlayAllClickListener();
        this.controller.setupOnRefresh();

    }

}