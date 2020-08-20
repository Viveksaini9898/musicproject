package com.example.myapplication;

import android.net.Uri;

public class AudioModel {
    String audioTitle;
    String audioDuration;
    String audioArtist;
    Uri audioUri;

    public String getAudioTitle(){
        return audioTitle;
    }
    public void setAudioTitle(String audioTitle){
        this.audioTitle=audioTitle;
    }
    public String getDuration(){
        return audioDuration;
    }
    public void setAudioDuration(String audioDuration){
        this.audioDuration=audioDuration;
    }

    public String getAudioArtist(){
        return audioArtist;
    }
    public void setAudioArtist(String audioArtist){
        this.audioArtist=audioArtist;
    }
    public Uri getAudioUri(){
        return audioUri;
    }
    public void setAudioUri(Uri audioUri){
        this.audioUri=audioUri;
    }
}
