package com.ucoz.kusfan.mygallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FlickFetcher {
    private final String TAG = "FlickFetcher";
    private final String API_KEY = "5947fe3d7e14a888a59bb400d6c76a89";

    public String getJSONString(String urlSpec) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlSpec)
                .build();

        Response response = client.newCall(request).execute();

        String result = response.body().string();
        return result;
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> galleryItems = new ArrayList<>();
        try {
            String url = Uri.parse("https://www.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String jsonString = getJSONString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(galleryItems, jsonBody);

        } catch (JSONException e) {
            Log.e(TAG, "ошибка парсинга JSON", e);
//            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "ошибка загрузки данных", e);
//            e.printStackTrace();
        }

        return galleryItems;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) throws JSONException, IOException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItem item = new GalleryItem();
            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }
            item.setUrl(photoJsonObject.getString("url_s"));
            items.add(item);
        }
    }
}
