package week2assignment.codepath.gridimagesearch.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vincetulit on 2/12/15.
 */
public class ImageResult {
    public String fullUrl;
    public String thumbUrl;
    public String title;

    // new ImageRestult(..raw item json..)
    public ImageResult(JSONObject json) {
        try {
            this.fullUrl = json.getString("url");
            this.thumbUrl = json.getString("tbUrl");
            this.title = json.getString("title");
        } catch (JSONException e) {

        }
    }

    //Take an array of json images and return arraylist of image results
    // ImageResult.fromJSONArray([...,...])
    public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
        ArrayList<ImageResult> results = new ArrayList<ImageResult>();
        for(int i = 0; i < array.length(); i++) {
            try {
                results.add(new ImageResult(array.getJSONObject(i)));
            } catch (JSONException e) {

            }
        }

        return results;
    }
}
