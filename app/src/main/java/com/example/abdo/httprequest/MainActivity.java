
package com.example.abdo.httprequest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.abdo.httprequest.Utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements GreenAdapter.ListItemClickedListener{

    final String TAG = "Main activity";
    GreenAdapter greenAdapter;
    RecyclerView recyclerView;
    EditText searchBoxEditText;
    TextView searchRes,errorMsg;
    ArrayList<String> globalNames;
    final String ADAPTER_KEY="adapter";
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = findViewById(R.id.pb_loading);
        errorMsg = findViewById(R.id.tv_error_msg);
        searchBoxEditText = findViewById(R.id.et_search_box);
        searchRes=findViewById(R.id.tv_github_search_results_json);
        recyclerView=findViewById(R.id.rv_numbers);
        recyclerView.setVisibility(View.INVISIBLE);

        if(savedInstanceState!=null && savedInstanceState.containsKey(ADAPTER_KEY)){
            greenAdapter=savedInstanceState.getParcelable(ADAPTER_KEY);
            if(greenAdapter!=null) {
                showJSONdata();
                recyclerViewStuff(greenAdapter.getNames(), greenAdapter);
            }
        }
    }
    public void recyclerViewStuff(ArrayList<String> names,GreenAdapter adapter){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(adapter==null)
            greenAdapter=new GreenAdapter(names,this);
        else
            greenAdapter=adapter;
        recyclerView.setAdapter(greenAdapter);
        globalNames=names;
    }
    @Override
    public void onListItemClicked(int index) {
        Log.d(TAG,"on item clicked");
        Intent intent=new Intent(this,SecondActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,globalNames.get(index));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(greenAdapter!=null)
            outState.putParcelable(ADAPTER_KEY,greenAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void showJSONdata(){
        searchRes.setVisibility(View.INVISIBLE);
        errorMsg.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMsg(){
        searchRes.setVisibility(View.INVISIBLE);
        errorMsg.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void makeGithubReq(){
        String query=searchBoxEditText.getText().toString();
        URL url= NetworkUtils.buildUrl(query);
        new GithubQueryTask().execute(url);
    }

    public class GithubQueryTask extends AsyncTask<URL,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recyclerView.setVisibility(View.INVISIBLE);
            errorMsg.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.VISIBLE);
            searchRes.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL url=urls[0];
            String result="";
            try {
                result= NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pb.setVisibility(View.INVISIBLE);
            if(s!=null && !s.equals("")) {
                showJSONdata();
                try {
                    JSONObject jsonObject=new JSONObject(s);
                    JSONArray items=jsonObject.getJSONArray("items");
                    int n=Math.min(50,items.length());
                    ArrayList<String> names=new ArrayList<>();
                    for(int i=0 ; i<n ; i++){
                        JSONObject js=items.getJSONObject(i);
                        names.add(js.getString("name")+"\n");
                    }
                    recyclerViewStuff(names,null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                showErrorMsg();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG,"before the request");
                makeGithubReq();
                break;
            default:
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}