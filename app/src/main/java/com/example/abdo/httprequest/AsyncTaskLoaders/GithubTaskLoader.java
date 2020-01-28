package com.example.abdo.httprequest.AsyncTaskLoaders;

import android.support.v4.content.AsyncTaskLoader;
import android.os.Bundle;
import android.util.Log;

import com.example.abdo.httprequest.Activities.MainActivity;
import com.example.abdo.httprequest.Utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class GithubTaskLoader extends AsyncTaskLoader<String>{

    private static final String TAG = "GithubTaskLoader";
    String mGithubJson;
    MainActivity mainActivity;
    Bundle bundle;
    public GithubTaskLoader(MainActivity c,Bundle bundle){
        super(c);
        this.bundle=bundle;
        mainActivity=c;
    }

    @Override
    protected void onStartLoading() {
        Log.d(TAG,"in start loading");
        super.onStartLoading();
        if (bundle==null)return;
        mainActivity.updateUIForLoading();
        if (mGithubJson != null) {
            deliverResult(mGithubJson);
        } else {
            forceLoad();// forces the loadInBackground to be called
        }
    }
    @Override
    public void deliverResult(String githubJson) {
        mGithubJson = githubJson;
        super.deliverResult(githubJson);
    }

    @Override
    public String loadInBackground() {
        Log.d(TAG,"in background");
        String searchQueryURL=bundle.getString(mainActivity.EXTRA_SEARCH_KEY);
        if(searchQueryURL==null || searchQueryURL.isEmpty())return null;
        try {
            URL url = new URL(searchQueryURL);
            return NetworkUtils.getResponseFromHttpUrl(url);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
