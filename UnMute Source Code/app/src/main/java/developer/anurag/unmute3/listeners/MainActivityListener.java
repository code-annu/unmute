package developer.anurag.unmute3.listeners;

import androidx.media3.session.MediaController;

public interface MainActivityListener {
    MediaController getPlayerMediaController();
    void loadPlayerFragment();

    void hidePlayerFragment();
    void showMiniPlayer();
    void showBottomNavigation();
    void onBackNavigation();
}
