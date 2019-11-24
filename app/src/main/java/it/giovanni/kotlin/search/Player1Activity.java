package it.giovanni.kotlin.search;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import it.giovanni.kotlin.R;

public class Player1Activity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_player_1);

        YouTubePlayerView playerView = findViewById(R.id.player_view);
        TextView title = findViewById(R.id.player_title);
        TextView description = findViewById(R.id.player_description);
        TextView id = findViewById(R.id.player_id);

        playerView.initialize(YoutubeConnector.API_KEY, this);
        title.setText(getIntent().getStringExtra("VIDEO_TITLE"));
        description.setText(getIntent().getStringExtra("VIDEO_DESCRIPTION"));
        id.setText((getIntent().getStringExtra("VIDEO_ID")));
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {

        if (!restored)
            player.cueVideo(getIntent().getStringExtra("VIDEO_ID"));
    }
}