package developer.anurag.unmute3;

import android.Manifest;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;

import developer.anurag.unmute3.controllers.MainActivityController;
import developer.anurag.unmute3.databinding.ActivityMainBinding;
import developer.anurag.unmute3.listeners.MainActivityListener;
import developer.anurag.unmute3.services.PlayerService;
import developer.anurag.unmute3.tracks_data_model.CurrentPlayingTracksDataModel;
import developer.anurag.unmute3.tracks_data_model.TracksDataModel;

public class MainActivity extends AppCompatActivity implements MainActivityListener{

    private MainActivityController controller;
    private MediaController mediaController;
    private ActivityMainBinding binding;
    private TracksDataModel tracksDataModel;
    private CurrentPlayingTracksDataModel currentPlayingTracksDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        this.binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(this.binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });


        // set data
        this.tracksDataModel=new ViewModelProvider(this).get(TracksDataModel.class);
        this.currentPlayingTracksDataModel=new ViewModelProvider(this).get(CurrentPlayingTracksDataModel.class);

        // attach and setup controller
        this.controller=new MainActivityController(this,this.binding);
        this.controller.setFragmentManager(getSupportFragmentManager());
        this.controller.setTracksDataModelAndFetchTracks(this.tracksDataModel);
        this.controller.setCurrentPlayingTracksDataModel(this.currentPlayingTracksDataModel);
        this.controller.setupBottomNavigationListener();
        this.controller.setupMiniPlayerListener();

        // start player service
        this.startPlayerService();

        // setup back pressed listener
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                controller.onBackPress(MainActivity.this);
            }
        });
    }


    private void startPlayerService(){
        SessionToken sessionToken=new SessionToken(this,new ComponentName(this, PlayerService.class));
        ListenableFuture<MediaController> controllerFuture=new MediaController.Builder(this,sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            try {
                this.mediaController=controllerFuture.get();
                this.controller.setMediaController(this.mediaController);
                this.controller.setMediaControllerListener();
                this.controller.setObserverForFragmentOnScreen(this);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, MoreExecutors.directExecutor());
    }



    @Override
    public MediaController getPlayerMediaController() {
        return this.mediaController;
    }

    @Override
    public void loadPlayerFragment() {
        this.controller.loadPlayerFragment();
    }

    @Override
    public void hidePlayerFragment() {
        this.controller.hidePlayerFragment();
    }

    @Override
    public void showMiniPlayer() {
        this.controller.showMiniPlayer();
    }

    @Override
    public void showBottomNavigation() {
        this.controller.showBottomNavigation();
    }

    @Override
    public void onBackNavigation() {
        this.controller.onBackPress(this);
    }
}