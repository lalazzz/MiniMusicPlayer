package com.mialearningdroid.minimusicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int length;

    private Button playButton1,pauseButton1;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private SeekBar seekbar;
    private Handler myHandler = new Handler(); // to do the timing for 1 secs for seekbar
    private double starttime = 0;
    private double endtime= 0;
    public static int onetimeonly = 0;

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT|| focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Pause playback because your Audio Focus was temporarily stolen, but will be back soon.
                // i.e. for a phone call Lower the volume, because something else is also
                mediaPlayer.pause();
                //saving the last state of the music being played.
                length=mediaPlayer.getCurrentPosition();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS ) {
                // Stop playback, because you lost the Audio Focus.i.e. the user started some other playback app
                // Remember to unregister your controls/buttons here.And release the kra — Audio Focus!
                releaseMediaPlayer();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback, because you hold the Audio Focus again!
                // i.e. the phone call ended or the nav directions
                // If you implement ducking and lower the volume, be sure to return it to normal here, as well.
                mediaPlayer.seekTo(length);
                mediaPlayer.start();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        playButton1 = (Button) findViewById(R.id.playbutton);
        pauseButton1= (Button)findViewById(R.id.pausebutton);
       // mediaPlayer = MediaPlayer.create(this, R.raw.azufeatseamo);

        pauseButton1.setEnabled(false); // pause if false initally
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);// for searching the source

        pauseButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                length=mediaPlayer.getCurrentPosition();
                pauseButton1.setEnabled(false);
                playButton1.setEnabled(true);
            }
        });

        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();
                //mediaPlayer.start();
                int result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // We have audio focus now. Therefore time to get the audioRes
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.azufeatseamo);
                    mediaPlayer.seekTo(length);
                    mediaPlayer.start();
                    endtime = mediaPlayer.getDuration();
                    starttime = mediaPlayer.getCurrentPosition();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(getApplicationContext(), "I'm Done",Toast.LENGTH_SHORT).show();
                    }
                });}
                //endtime = mediaPlayer.getDuration();
                //starttime = mediaPlayer.getCurrentPosition();


                if (onetimeonly == 0) {
                    seekbar.setMax((int) endtime);
                    onetimeonly = 1;
                }

                pauseButton1.setEnabled(true);
                playButton1.setEnabled(false);
                seekbar.setProgress((int)starttime);
               myHandler.postDelayed(UpdateSongTime,100);
            }

        });


    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            starttime = mediaPlayer.getCurrentPosition();
//          tx1 is a tx1 = (TextView) findViewById(R.id.textView2);
//          tx1.setText(String.format("%d min, %d sec",
//                    TimeUnit.MILLISECONDS.toMinutes((long) starttime),
//                    TimeUnit.MILLISECONDS.toSeconds((long) starttime) -
//                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                    toMinutes((long) starttime)))
//            );
            seekbar.setProgress((int)starttime);
            myHandler.postDelayed(this, 100);
        }
    };
//    private Context mContext;
//    private boolean mAudioFocusGranted = false;
//    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
//
//    private boolean requestAudioFocus() {
//        if (!mAudioFocusGranted) {
//            AudioManager am = (AudioManager) mContext
//                    .getSystemService(Context.AUDIO_SERVICE);
//            // Request audio focus for play back
//            int result = am.requestAudioFocus(mOnAudioFocusChangeListener,
//                    // Use the music stream.
//                    AudioManager.STREAM_MUSIC,
//                    // Request permanent focus.
//                    AudioManager.AUDIOFOCUS_GAIN);
//
//            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//                mAudioFocusGranted = true;
//            } else {
//                // FAILED
//                Log.e("MainActivity",">>>>>>>>>>>>> FAILED TO GET AUDIO FOCUS <<<<<<<<<<<<<<<<<<<<<<<<");
//            }
//        }
//        return mAudioFocusGranted;
//    }

    private void releaseMediaPlayer() {

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}