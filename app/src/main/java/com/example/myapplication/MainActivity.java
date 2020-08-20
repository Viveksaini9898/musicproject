package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.R.id.recycler_view;

public class MainActivity extends AppCompatActivity {
    int initial=0;
    ArrayList<AudioModel> audioArrayList;
    RecyclerView recyclerView;
    MediaPlayer mediaPlayer;
    TextView audio_name,current,total;
    ImageView prev, next, pause;
    SeekBar seekBar;
    int audio_index = 0;
    public static final int PERMISSION_READ = 0;
    double current_pos, total_duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkPermission()) {
            setAudio();
        }

    }

    public void setAudio(){
        recyclerView=findViewById(recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        current=findViewById(R.id.current);
        total=findViewById(R.id.total);
        prev=findViewById(R.id.prev);
        pause=findViewById(R.id.pause);
        audio_name=findViewById(R.id.audio_name);
        next=findViewById(R.id.next);
        seekBar=findViewById(R.id.seekbar);

        audioArrayList=new ArrayList<>();
        mediaPlayer=new MediaPlayer();
        getAudiofiles();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos=seekBar.getProgress();
                mediaPlayer.seekTo((int) current_pos);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                audio_index++;
                if (audio_index<(audioArrayList.size())){
                    playAudio((audio_index));
                }else {
                    audio_index=0;
                    playAudio(audio_index);
                }
            }
        });
        if (!audioArrayList.isEmpty()){
            playAudio(audio_index);
            prevAudio();
            nextAudio();
            setPause();
        }

    }
    private void playAudio(int pos) {
        try{
            mediaPlayer.reset();
            if(audioArrayList!=null && audioArrayList.size()>0) {
                Uri audio=audioArrayList.get(pos).getAudioUri();
                if(audio!=null) {
                    mediaPlayer.setDataSource(this, audio);
                }
            }
            if(initial==0)
            {
                mediaPlayer.prepare();
                pause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                audio_name.setText(audioArrayList.get(pos).getAudioTitle());
                audio_index=pos;
                initial=1;
            }
            else {
                mediaPlayer.prepare();
                mediaPlayer.start();
                pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                audio_name.setText(audioArrayList.get(pos).getAudioTitle());
                audio_index = pos;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setAudioPogress();
    }

    private void setAudioPogress() {
        current_pos = mediaPlayer.getCurrentPosition();
        total_duration = mediaPlayer.getDuration();
        total.setText(timerConversion((long) total_duration));
        current.setText(timerConversion((long) current_pos));
        seekBar.setMax((int) total_duration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = mediaPlayer.getCurrentPosition();
                    current.setText(timerConversion((long) current_pos));
                    seekBar.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable,1000);
    }

    private void setPause() {
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    pause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }else {
                    mediaPlayer.start();
                    pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
            }
        });
    }

    private void nextAudio() {
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio_index<(audioArrayList.size())){
                    audio_index++;
                    playAudio(audio_index);
                }else {
                    audio_index=0;
                    playAudio(audio_index);
                }
            }
        });
    }

    private void prevAudio() {
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio_index>0){
                    audio_index--;
                    playAudio(audio_index);
                }else {
                    audio_index=audioArrayList.size();
                    playAudio(audio_index);
                }
            }
        });
    }
    public String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }

    public void getAudiofiles(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                AudioModel audioModel=new AudioModel();
                audioModel.setAudioTitle(title);
                audioModel.setAudioUri(Uri.fromFile(new File(url)));
                audioModel.setAudioArtist(artist);
                audioModel.setAudioDuration(duration);
                audioArrayList.add(audioModel);
            }while (cursor.moveToNext());
            }
        AudioAdapter adapter=new AudioAdapter(this,audioArrayList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                playAudio(pos);
            }
        });
    }

    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                        setAudio();
                    }
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!=null){
            mediaPlayer.release();
        }
    }
}