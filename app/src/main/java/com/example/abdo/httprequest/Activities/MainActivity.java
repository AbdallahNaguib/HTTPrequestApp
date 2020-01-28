
package com.example.abdo.httprequest.Activities;

import android.support.v4.app.FragmentManager;
import android.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abdo.httprequest.Adapters.GreenAdapter;
import com.example.abdo.httprequest.AsyncTaskLoaders.GithubTaskLoader;
import com.example.abdo.httprequest.ClickListeners.MainActivityListItemClickListener;
import com.example.abdo.httprequest.R;
import com.example.abdo.httprequest.Utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.support.v4.app.LoaderManager;

import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    final String TAG = "Main activity";
    private final static int GITHUB_SEARCH_LOADER = 22;
    GreenAdapter greenAdapter;
    RecyclerView recyclerView;
    EditText searchBoxEditText;
    TextView searchRes, errorMsg;
    public final String EXTRA_SEARCH_KEY="search_key";
    ArrayList<String> globalNames;
    final String ADAPTER_KEY = "adapter";
    ProgressBar pb;

    public ArrayList<String> getGlobalNames() {
        return globalNames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb = findViewById(R.id.pb_loading);
        errorMsg = findViewById(R.id.tv_error_msg);
        searchBoxEditText = findViewById(R.id.et_search_box);
        searchRes = findViewById(R.id.tv_github_search_results_json);
        recyclerView = findViewById(R.id.rv_numbers);
        recyclerView.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null && savedInstanceState.containsKey(ADAPTER_KEY)) {
            greenAdapter = savedInstanceState.getParcelable(ADAPTER_KEY);
            if (greenAdapter != null) {
                showJSONdata();
                recyclerViewStuff(greenAdapter.getNames(), greenAdapter);
            }
        }
    }

    public void recyclerViewStuff(ArrayList<String> names, GreenAdapter adapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if (adapter == null) {
            greenAdapter = new GreenAdapter(names, new MainActivityListItemClickListener(this));
        }else {
            greenAdapter = adapter;
        }
        recyclerView.setAdapter(greenAdapter);
        globalNames = names;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (greenAdapter != null)
            outState.putParcelable(ADAPTER_KEY, greenAdapter);
    }


    private void showJSONdata() {
        searchRes.setVisibility(View.INVISIBLE);
        errorMsg.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMsg() {
        searchRes.setVisibility(View.INVISIBLE);
        errorMsg.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    public void updateUIForLoading(){
        recyclerView.setVisibility(View.INVISIBLE);
        errorMsg.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
        searchRes.setVisibility(View.INVISIBLE);
    }

    public void makeGithubReq() {
        String query = searchBoxEditText.getText().toString();
        URL url = NetworkUtils.buildUrl(query);
        Bundle queryBundle=new Bundle();
        queryBundle.putString(EXTRA_SEARCH_KEY,url.toString());
        LoaderManager loaderManager=getSupportLoaderManager();
        Loader<String> githubSearchLoader=loaderManager.getLoader(GITHUB_SEARCH_LOADER);

        if(githubSearchLoader==null){
            Log.d(TAG,"starting loading");
            loaderManager.initLoader(GITHUB_SEARCH_LOADER,queryBundle,this);
        }else{
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER,queryBundle,this);
        }
    }

    @Override
    @NonNull
    public Loader<String> onCreateLoader(int i,final Bundle bundle) {
        Log.d(TAG,"first");
        return new GithubTaskLoader(this,bundle);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     * <p>
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that has finished.
     * @param s   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        Log.d(TAG,"load finished");
        pb.setVisibility(View.INVISIBLE);
        if (s != null && !s.equals("")) {
            showJSONdata();
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray items = jsonObject.getJSONArray("items");
                int n = Math.min(50, items.length());
                ArrayList<String> names = new ArrayList<>();
                for (int i = 0; i < n; i++) {
                    JSONObject js = items.getJSONObject(i);
                    names.add(js.getString("name") + "\n");
                }
                recyclerViewStuff(names, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            showErrorMsg();
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     * <p>
     * <p>This will always be called from the process's main thread.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG, "before the request");
                makeGithubReq();
                break;
            default:
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}