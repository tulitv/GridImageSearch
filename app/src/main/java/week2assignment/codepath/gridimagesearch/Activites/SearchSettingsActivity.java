package week2assignment.codepath.gridimagesearch.Activites;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import week2assignment.codepath.gridimagesearch.Models.SearchOptions;
import week2assignment.codepath.gridimagesearch.R;

public class SearchSettingsActivity extends ActionBarActivity {
    private Spinner spImageSize;
    private Spinner spColorFilter;
    private Spinner spType;
    private EditText etSite;
    SearchOptions searchOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_settings);

        spImageSize = (Spinner) findViewById(R.id.spImageSize);
        spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
        spType = (Spinner) findViewById(R.id.spType);
        etSite = (EditText) findViewById(R.id.etSite);

        searchOptions =
                (SearchOptions) getIntent().getSerializableExtra(SearchActivity.SEARCH_OPTIONS);

        spImageSize.setSelection(searchOptions.getPosImageSize());
        spColorFilter.setSelection(searchOptions.getPosColorFilter());
        spType.setSelection(searchOptions.getPosType());
        etSite.setText(searchOptions.getSite());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_settings, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Save button is pressed, set in android:onClick
    public void saveOptions(View view) {
        searchOptions.setPosImageSize(spImageSize.getSelectedItemPosition());
        searchOptions.setPosColorFilter(spColorFilter.getSelectedItemPosition());
        searchOptions.setPosType(spType.getSelectedItemPosition());
        searchOptions.setSite(etSite.getText().toString());

        Intent output = new Intent();
        output.putExtra(SearchActivity.SEARCH_OPTIONS, searchOptions);
        setResult(RESULT_OK, output);
        finish();
    }
}
