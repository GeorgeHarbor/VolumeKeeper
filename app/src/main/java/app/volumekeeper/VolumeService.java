package app.volumekeeper;

import android.accessibilityservice.AccessibilityService;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;

/**
 * Keeps the media stream pinned to maximum volume.
 *
 * Some set-top boxes (e.g. the ZTE B866V2) zero the HDMI media-volume index every time an app
 * comes to the foreground. This service hears those window transitions and re-asserts max volume,
 * with a couple of delayed retries to win the race after the firmware settles the transition.
 */
public class VolumeService extends AccessibilityService {

    private AudioManager audio;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable maxOut = this::maxVolume;

    @Override
    public void onServiceConnected() {
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        maxVolume();
        handler.removeCallbacks(maxOut);
        handler.postDelayed(maxOut, 300);
        handler.postDelayed(maxOut, 1200);
    }

    private void maxVolume() {
        if (audio == null) return;
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) != max) {
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, max, 0);
        }
    }

    @Override
    public void onInterrupt() { }
}
