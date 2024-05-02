package developer.anurag.unmute3.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.controllers.PlayingPlaylistFragmentController;
import developer.anurag.unmute3.databinding.FragmentPlayingPlaylistBinding;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;

public class PlayingPlaylistFragment extends BottomSheetDialogFragment {
    private FragmentPlayingPlaylistBinding binding;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    private PlayingPlaylistFragmentController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding=FragmentPlayingPlaylistBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.currentPlayingTracksDataModel=new ViewModelProvider(requireActivity()).get(CurrentPlayingTracksDataModel.class);

        // setup controller
        this.controller=new PlayingPlaylistFragmentController(this.requireContext(),this.binding);
        this.controller.setCurrentPlayingTracksDataModel(this.currentPlayingTracksDataModel);
        this.controller.setPlayingPlaylistContainerRV();

    }
}