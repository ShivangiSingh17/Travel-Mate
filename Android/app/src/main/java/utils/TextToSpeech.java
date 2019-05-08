import android.app.Activity;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import java.util.Locale;
import android.widget.Toast;
private class CustomTextToSpeech extends TextToSpeech {
    long startTimestamp = 0;
    private Handler handler;
    private Runnable speakRunnable;

    StringBuilder textToSpeechBuilder;

    private boolean isPaused = false;

    public CustomTextToSpeech(Context context, OnInitListener initListener){
        super(context, initListener);

        setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onDone(String arg0) {
                Log.d(TAG, "tts done. " + arg0);
                startTimestamp = 0;
                pauseTimestamp = 0;
                handler.postDelayed(speakRunnable, TTS_INTERVAL_MS);
            }

            @Override
            public void onError(String arg0) {
                Log.e(TAG, "tts error. " + arg0);
            }

            @Override
            public void onStart(String arg0) {
                Log.d(TAG, "tts start. " + arg0);
                setStartTimestamp(System.currentTimeMillis());
            }
        });

        handler = new Handler();

        speakRunnable = new Runnable() {
            @Override
            public void run() {
                speak();
            }
        };

        textToSpeechBuilder = new StringBuilder(getResources().getString(R.string.talkback_tips));
    }

    public void setStartTimestamp(long timestamp) {
        startTimestamp = timestamp;
    }
    public void setPauseTimestamp(long timestamp) {
        pauseTimestamp = timestamp;
    }

    public boolean isPaused(){
        return (startTimestamp > 0 && pauseTimestamp > 0);
    }

    public void resume(){
        if(handler != null && isPaused){
            if(startTimestamp > 0 && pauseTimestamp > 0){
                handler.postDelayed(speakRunnable, TTS_SETUP_TIME_MS);
            } else {
                handler.postDelayed(speakRunnable, TTS_INTERVAL_MS);
            }
        }

        isPaused = false;
    }

    public void pause(){
        isPaused = true;

        if (handler != null) {
            handler.removeCallbacks(speakRunnable);
            handler.removeMessages(1);
        }

        if(isSpeaking()){
            setPauseTimestamp(System.currentTimeMillis());
        }
