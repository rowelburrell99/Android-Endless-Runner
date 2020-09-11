package com.example.myapplication;

        import android.content.Context;
        import android.media.AudioAttributes;
        import android.media.AudioManager;
        import android.media.SoundPool;
        import android.os.Build;

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;

    private static SoundPool soundPool;
    private static int hitcoinSound;
    private static int hitheartSound;
    private static int hitspikeSound;

    /**
     * Sound files for my objects
     * @param context
     */
    public SoundPlayer(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        hitcoinSound = soundPool.load(context, R.raw.coin, 1);  //loads the audio file for coins.
        hitheartSound = soundPool.load(context, R.raw.heart, 1); //loads the audio file for hearts.
        hitspikeSound = soundPool.load(context, R.raw.spike, 1); //loads the audio file for spikes.
    }

    public void playHitcoinSound() {
        soundPool.play(hitcoinSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitheartSound() {
        soundPool.play(hitheartSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitspikeSound() {
        soundPool.play(hitspikeSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}

