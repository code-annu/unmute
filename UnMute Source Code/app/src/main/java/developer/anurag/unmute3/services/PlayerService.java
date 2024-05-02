package developer.anurag.unmute3.services;

import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class PlayerService extends MediaSessionService {
    private MediaSession mediaSession;
    private ExoPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        this.player=new ExoPlayer.Builder(this).build();
        this.mediaSession=new MediaSession.Builder(this,this.player).build();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return this.mediaSession;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mediaSession.getPlayer().release();
        this.mediaSession.release();
        this.mediaSession=null;
    }
}
