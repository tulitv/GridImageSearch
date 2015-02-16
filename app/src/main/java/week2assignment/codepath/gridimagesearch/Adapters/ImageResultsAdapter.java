package week2assignment.codepath.gridimagesearch.Adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import week2assignment.codepath.gridimagesearch.Models.ImageResult;
import week2assignment.codepath.gridimagesearch.R;

/**
 * Created by vincetulit on 2/12/15.
 */
public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {


    public ImageResultsAdapter(Context context, List<ImageResult> images) {
        super(context, R.layout.item_image_result, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        ImageResult imageInfo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Clear out image from last time
        viewHolder.ivImage.setImageResource(0);
        //Populate title and remote download image url
        viewHolder.tvTitle.setText(Html.fromHtml(imageInfo.title));
        Picasso.with(getContext()).load(imageInfo.thumbUrl).into(viewHolder.ivImage);
        //Return the completed view to be displayed
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
    }
}
