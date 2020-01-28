package com.example.abdo.httprequest.ClickListeners;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.abdo.httprequest.Adapters.GreenAdapter;
import com.example.abdo.httprequest.Activities.MainActivity;
import com.example.abdo.httprequest.Activities.SecondActivity;

public class MainActivityListItemClickListener
        implements GreenAdapter.ListItemClickedListener{

    private static final String TAG = "MainActivityListItem";

    MainActivity mainActivity;
    public MainActivityListItemClickListener(MainActivity mainActivity){
        this.mainActivity=mainActivity;
    }

    @Override
    public void onListItemClicked(int index) {
        Toast.makeText(mainActivity,"clicked",Toast.LENGTH_SHORT).show();
        Log.d(TAG, "on item clicked");
        Intent intent = new Intent(mainActivity, SecondActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, mainActivity.getGlobalNames().get(index));
        mainActivity.startActivity(intent);
    }
}
