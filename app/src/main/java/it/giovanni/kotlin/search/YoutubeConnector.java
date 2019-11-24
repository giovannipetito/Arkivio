package it.giovanni.kotlin.search;

import android.util.Log;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class YoutubeConnector {

    private YouTube.Search.List query;
    static final String API_KEY = "AIzaSyCSFUynztrGEOTVyFjUmD2z98H4G9ilEtM";
    private static final String PACKAGENAME = "it.giovanni.kotlin";
    private static final String SHA1 = "03:29:32:E7:87:94:51:CA:67:F5:33:0E:53:50:BD:69:66:2F:F0:B0";
    private static final long MAXRESULTS = 25;

    YoutubeConnector() {

        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
            request.getHeaders().set("X-Android-Package", PACKAGENAME);
            request.getHeaders().set("X-Android-Cert", SHA1);
        }).setApplicationName("SearchYoutube").build();

        try {
            query = youtube.search().list("id,snippet");
            query.setKey(API_KEY);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            query.setType("video"); // channel, playlist, video

            query.setFields("items(id/kind,id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url)");

        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    List<Video> search(String keywords) {
        query.setQ(keywords);
        query.setMaxResults(MAXRESULTS);

        try {
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            List<Video> items = new ArrayList<>();

            if (results != null)
                items = setItemsList(results.iterator());

            return items;

        } catch (IOException e) {
            Log.d("YC", "Could not search: " + e);
            return null;
        }
    }

    private static List<Video> setItemsList(Iterator<SearchResult> iteratorSearchResults) {

        List<Video> list = new ArrayList<>();

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            if (rId.getKind().equals("youtube#video")) {

                Video item = new Video();
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getHigh();

                item.setId(singleVideo.getId().getVideoId());
                item.setTitle(singleVideo.getSnippet().getTitle());
                item.setDescription(singleVideo.getSnippet().getDescription());
                item.setUrl(thumbnail.getUrl());

                list.add(item);
            }
        }
        return list;
    }
}