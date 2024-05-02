package developer.anurag.unmute3.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import developer.anurag.unmute3.R;
import developer.anurag.unmute3.controllers.PlayerFragmentController;
import developer.anurag.unmute3.databinding.FragmentPlayerBinding;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;

public class PlayerFragment extends Fragment {
    private FragmentPlayerBinding binding;
    private TracksDataModel tracksDataModel;
    private PlayerFragmentController controller;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;
    private GestureDetector gestureDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding=FragmentPlayerBinding.inflate(inflater,container,false);
        return this.binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.tracksDataModel=new ViewModelProvider(requireActivity()).get(TracksDataModel.class);
        this.currentPlayingTracksDataModel=new ViewModelProvider(requireActivity()).get(CurrentPlayingTracksDataModel.class);

        // setup controller
        this.controller=new PlayerFragmentController(this.requireContext(),this.binding);
        this.controller.setCurrentPlayingTracksDataModel(this.currentPlayingTracksDataModel);
        this.controller.setupMediaControllerListener();
        this.controller.setupSeekbarListener();
        this.controller.setupPlayPauseIBClickListener();
        this.controller.setupNextPreviousIBClickListener();
        this.controller.setupPlaylistBtnClickListener(getParentFragmentManager());
        this.controller.setupClosIBClickListener();
        this.controller.setupCurrentPlayingPlaylistObserver(requireActivity());
        this.controller.setupTrackPostersContainerRV();

        // Setup gesture detector
        setGestureDetector();
        view.setOnTouchListener((v, event) -> {
            this.gestureDetector.onTouchEvent(event);
            return true;
        });

    }

    public void setGestureDetector() {
        this.gestureDetector=new GestureDetector(requireActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(@NonNull MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {

            }

            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if(e1!=null){
                    float distanceY=e2.getY()-e1.getY();
                    if(distanceY>200){
                        controller.closePlayerFragment();
                    }

                }
                return true;
            }
        });
    }
}