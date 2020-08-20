package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.viewHolder> {
    Context context;
    ArrayList<AudioModel>audioArrayList;
    public OnItemClickListener onItemClickListener;

    public AudioAdapter(Context context,ArrayList<AudioModel>audioArrayList){
        this.context =context;
        this.audioArrayList=audioArrayList;
    }
    @Override
    public AudioAdapter.viewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_list, viewGroup, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(final  AudioAdapter.viewHolder holder, final int i) {
        holder.title.setText(audioArrayList.get(i).getAudioTitle());
        holder.artist.setText(audioArrayList.get(i).getAudioArtist());
    }

    @Override
    public int getItemCount() {
        return audioArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title, artist;
        public viewHolder(final View itemview) {
            super(itemview);
            title=  itemView.findViewById(R.id.title);
            artist= itemView.findViewById(R.id.artist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = (OnItemClickListener) onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
