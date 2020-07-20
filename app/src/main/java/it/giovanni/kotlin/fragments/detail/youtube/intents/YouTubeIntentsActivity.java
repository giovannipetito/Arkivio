package it.giovanni.kotlin.fragments.detail.youtube.intents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeIntents;

import java.util.ArrayList;
import java.util.List;

import it.giovanni.kotlin.R;

public class YouTubeIntentsActivity extends Activity implements AdapterView.OnItemClickListener {

    // This is the value of Intent.EXTRA_LOCAL_ONLY for API level 11 and above.
    private static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
    private static final String VIDEO_ID = "BRSpqZOPEas";
    private static final String PLAYLIST_ID = "PLPXvXoGcCrLdblRv-1FEA_mbla4qgqC6w";
    private static final String USER_ID = "Google";
    private static final String CHANNEL_ID = "UCVHFbqXqoYvEWM1Ddxl0QDg";
    private static final int SELECT_VIDEO_REQUEST = 1000;

    private List<YouTubeIntentsAdapter.ListViewItem> intentItems;

    private enum IntentType {
        PLAY_VIDEO,
        OPEN_PLAYLIST,
        PLAY_PLAYLIST,
        OPEN_USER,
        OPEN_CHANNEL,
        OPEN_SEARCH,
        UPLOAD_VIDEO
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_intents_activity);

        intentItems = new ArrayList<>();
        intentItems.add(new IntentItem("Play Video", IntentType.PLAY_VIDEO));
        intentItems.add(new IntentItem("Open Playlist", IntentType.OPEN_PLAYLIST));
        intentItems.add(new IntentItem("Play Playlist", IntentType.PLAY_PLAYLIST));
        intentItems.add(new IntentItem("Open User", IntentType.OPEN_USER));
        intentItems.add(new IntentItem("Open Channel", IntentType.OPEN_CHANNEL));
        intentItems.add(new IntentItem("Open Search Results", IntentType.OPEN_SEARCH));
        intentItems.add(new IntentItem("Upload Video", IntentType.UPLOAD_VIDEO));

        ListView listView = findViewById(R.id.intents_list);
        YouTubeIntentsAdapter adapter = new YouTubeIntentsAdapter(this, R.layout.intents_list_item, intentItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public boolean isIntentTypeEnabled(IntentType type) {
        switch (type) {
            case PLAY_VIDEO:
                return YouTubeIntents.canResolvePlayVideoIntent(this);
            case OPEN_PLAYLIST:
                return YouTubeIntents.canResolveOpenPlaylistIntent(this);
            case PLAY_PLAYLIST:
                return YouTubeIntents.canResolvePlayPlaylistIntent(this);
            case OPEN_SEARCH:
                return YouTubeIntents.canResolveSearchIntent(this);
            case OPEN_USER:
                return YouTubeIntents.canResolveUserIntent(this);
            case OPEN_CHANNEL:
                return YouTubeIntents.canResolveChannelIntent(this);
            case UPLOAD_VIDEO:
                return YouTubeIntents.canResolveUploadIntent(this);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IntentItem clickedIntentItem = (IntentItem) intentItems.get(position);

        Intent intent;
        switch (clickedIntentItem.type) {
            case PLAY_VIDEO:
                intent = YouTubeIntents.createPlayVideoIntentWithOptions(this, VIDEO_ID, true, false);
                startActivity(intent);
                break;
            case OPEN_PLAYLIST:
                intent = YouTubeIntents.createOpenPlaylistIntent(this, PLAYLIST_ID);
                startActivity(intent);
                break;
            case PLAY_PLAYLIST:
                intent = YouTubeIntents.createPlayPlaylistIntent(this, PLAYLIST_ID);
                startActivity(intent);
                break;
            case OPEN_SEARCH:
                intent = YouTubeIntents.createSearchIntent(this, USER_ID);
                startActivity(intent);
                break;
            case OPEN_USER:
                intent = YouTubeIntents.createUserIntent(this, USER_ID);
                startActivity(intent);
                break;
            case OPEN_CHANNEL:
                intent = YouTubeIntents.createChannelIntent(this, CHANNEL_ID);
                startActivity(intent);
                break;
            case UPLOAD_VIDEO:
                // The upload activity is started in the function onActivityResult.
                intent = new Intent(Intent.ACTION_PICK, null).setType("video/*");
                intent.putExtra(EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, SELECT_VIDEO_REQUEST);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_VIDEO_REQUEST) {
                Intent intent = YouTubeIntents.createUploadIntent(this, returnedIntent.getData());
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, returnedIntent);
    }

    private final class IntentItem implements YouTubeIntentsAdapter.ListViewItem {

        public final String title;
        public final IntentType type;

        IntentItem(String title, IntentType type) {
            this.title = title;
            this.type = type;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public Boolean isEnabled() {
            return isIntentTypeEnabled(type);
        }

        @Override
        public String getDisabledText() {
            return getString(R.string.intent_disabled);
        }
    }
}