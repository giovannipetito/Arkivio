package it.giovanni.kotlin.search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.giovanni.kotlin.R;

public class SearchActivity extends AppCompatActivity implements YoutubeAdapter.OnItemViewClicked {

    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private Handler handler;
    private List<Video> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        mProgressDialog = new ProgressDialog(this);
        EditText searchInput = findViewById(R.id.search_input);
        mRecyclerView = findViewById(R.id.videos_recycler_view);

        mProgressDialog.setTitle("Searching...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler();

        searchInput.setOnEditorActionListener((v, actionId, event) -> {

            if(actionId == EditorInfo.IME_ACTION_SEARCH) {

                mProgressDialog.setMessage("Finding videos for " + v.getText().toString());
                mProgressDialog.show();

                searchOnYoutube(v.getText().toString());

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                return false;
            }
            return true;
        });

    }

    private void searchOnYoutube(final String keywords) {

        new Thread() {
            public void run() {

                YoutubeConnector yc = new YoutubeConnector();
                searchResults = yc.search(keywords);

                handler.post(() -> {

                    fillYoutubeVideos();
                    mProgressDialog.dismiss();
                });
            }
        }.start();
    }

    private void fillYoutubeVideos() {
        YoutubeAdapter youtubeAdapter = new YoutubeAdapter(getApplicationContext(), searchResults, this);
        mRecyclerView.setAdapter(youtubeAdapter);
        youtubeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemToPlayer1Clicked(Video video) {

        Intent intent = new Intent(this, Player1Activity.class);

        intent.putExtra("VIDEO_ID", video.getId());
        intent.putExtra("VIDEO_TITLE", video.getTitle());
        intent.putExtra("VIDEO_DESCRIPTION", video.getDescription());

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }

    @Override
    public void onItemToPlayer2Clicked(Video video) {

        Intent intent = new Intent(this, Player2Activity.class);
        intent.putExtra("VIDEO_ID", video.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}