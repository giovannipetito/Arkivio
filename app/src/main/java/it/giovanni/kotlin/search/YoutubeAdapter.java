package it.giovanni.kotlin.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.giovanni.kotlin.R;

public class YoutubeAdapter extends RecyclerView.Adapter<YoutubeAdapter.MyViewHolder> {

    private Context context;
    private List<Video> list;
    private OnItemViewClicked onItemViewClicked;

    public YoutubeAdapter(Context mContext, List<Video> mList, OnItemViewClicked onItemViewClicked) {
        this.context = mContext;
        this.list = mList;
        this.onItemViewClicked = onItemViewClicked;
    }

    public void setVideoList(List<Video> mList) {
        this.list = mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final Video video = list.get(position);

        holder.title.setText(video.getTitle());
        holder.description.setText(video.getDescription());
        holder.id.setText("Video ID: " + video.getId());

        Glide.with(context).
                load(video.getUrl()).
                override(480, 270).
                centerCrop().
                into(holder.preview);

        holder.goToPlayer1.setOnClickListener(view ->
                onItemViewClicked.onItemToPlayer1Clicked(video));

        holder.goToPlayer2.setOnClickListener(view ->
                onItemViewClicked.onItemToPlayer2Clicked(video));
    }

    @Override
    public int getItemCount() {
        if (list == null)
            return 0;
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView preview;
        TextView title;
        TextView description;
        TextView id;

        LinearLayout goToPlayer1;
        LinearLayout goToPlayer2;

        MyViewHolder(View view) {
            super(view);
            preview = view.findViewById(R.id.video_preview);
            title = view.findViewById(R.id.video_title);
            description = view.findViewById(R.id.video_description);
            id = view.findViewById(R.id.video_id);

            goToPlayer1 = view.findViewById(R.id.go_to_player1);
            goToPlayer2 = view.findViewById(R.id.go_to_player2);
        }
    }

    public interface OnItemViewClicked {
        void onItemToPlayer1Clicked(Video video);
        void onItemToPlayer2Clicked(Video video);
    }
}