package week2assignment.codepath.gridimagesearch.Activites;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import week2assignment.codepath.gridimagesearch.Adapters.ImageResultsAdapter;
import week2assignment.codepath.gridimagesearch.Models.EndlessScrollListener;
import week2assignment.codepath.gridimagesearch.Models.ImageResult;
import week2assignment.codepath.gridimagesearch.Models.SearchOptions;
import week2assignment.codepath.gridimagesearch.R;


public class SearchActivity extends ActionBarActivity {
    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;

    private SearchOptions searchOptions;
    private String searchQuery;
    public static String SEARCH_OPTIONS = "searchoptions";
    public static final int SEARCH_OPTIONS_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        // Creates the data source
        imageResults = new ArrayList<ImageResult>();
        // Attaches the data source to an adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        // Link the adapter to a gridview
        gvResults.setAdapter(aImageResults);
        // Search options for advance search
        searchOptions = new SearchOptions();
    }

    private void setupViews()
    {
        gvResults = (GridView) findViewById(R.id.gvResults);

        // Launch the ImageDisplayActivity
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Creating an intent
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                //Get the image result to display
                ImageResult result = imageResults.get(position);
                //Pass the image result into the intent
                i.putExtra("url", result.fullUrl);
                //Launch the new intent
                startActivity(i);
            }
        });

        gvResults.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the image result display
                ImageResult result = imageResults.get(position);

                Toast.makeText(SearchActivity.this,result.fullUrl,Toast.LENGTH_SHORT).show();

                // Construct a ShareIntent with link to image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(result.fullUrl));
                shareIntent.setType("image/*");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent,"Share Image"));

                return true;
            }
        });

                // Attach the listener to the  grid view results
                gvResults.setOnScrollListener(new EndlessScrollListener() {
                    @Override
                    public void onLoadMore(int page, int totalItemCount) {
                        // Triggered only when new data needs to be appended to the list
                        loadDataFromAPI(page);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Pass query to searchQuery
                searchQuery = query;
                if (isNetworkAvailable()) {
                    // New search, load data with offset reset to 0
                    loadDataFromAPI(0);
                }
                else {
                   Toast toast =  Toast.makeText(SearchActivity.this, "NO NETWORK CONNECTION", Toast.LENGTH_LONG);
                   toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL,0,0);
                   toast.show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Launch the SearchSettingsActivity with existing SearchOptions
            Intent i = new Intent(this, SearchSettingsActivity.class);
            i.putExtra(SEARCH_OPTIONS, searchOptions);
            startActivityForResult(i, SEARCH_OPTIONS_ID);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == SEARCH_OPTIONS_ID) && (resultCode == RESULT_OK))
        {
            searchOptions = (SearchOptions) data.getSerializableExtra(SEARCH_OPTIONS);
        }
    }

    // Return the complete Google Image Search url with query and advanced option selection
    public String buildUrl(int offset) {

        return "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8" +
                    "&q=" + searchQuery +
                    "&imgcolor=" + getResources().getStringArray(R.array.color_filter_array)[searchOptions.getPosColorFilter()] +
                    "&imgsz=" + getResources().getStringArray(R.array.image_size_array)[searchOptions.getPosImageSize()] +
                    "&imgtype=" + getResources().getStringArray(R.array.type_array)[searchOptions.getPosType()] +
                    "&as_sitesearch=" + searchOptions.getSite() +
                    "&start=" + String.valueOf(offset);
    }

    //Loads data from API and appends data into the adapter at position defined by offset
    public void loadDataFromAPI(final int offset) {
        String searchUrl = buildUrl(offset);

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(searchUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                //responseData => results => [x] => tbUrl, title, url, width, height
                try {
                    JSONArray imageResultJSON = response.getJSONObject("responseData").getJSONArray("results");
                    //clear the existing images from the array (in cases where its a new search)
                    if (offset == 0 ) {
                        imageResults.clear();
                    }
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultJSON));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    //Check for internet connection
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
