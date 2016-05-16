package com.l24o.syberiasoftprojecttest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.l24o.syberiasoftprojecttest.model.Image;
import com.l24o.syberiasoftprojecttest.realm.RealmHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.realm.Realm;

/**
 * @author Alexander Popov on 16.05.2016.
 */
public class ImageAdapter extends PagerAdapter {

    private final LayoutInflater inflater;
    Context context;
    private List<Image> images;

    ImageAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("pref_all_or_favorites", false))
            images = RealmHelper.getAll(Realm.getDefaultInstance(), Image.class);
        else
            images = RealmHelper.getAllF(Realm.getDefaultInstance());
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        final View view = inflater.inflate(R.layout.page_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        final Image image = images.get(position);
        Picasso.with(context)
                .load(image.getUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .into(imageView);
        final TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(image.getComment());
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageResource(image.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image.isFavorite()) {
                    Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            image.setFavorite(false);
                            image.setComment("");
                        }
                    });
                    textView.setText("");
                    fab.setImageResource(image.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setHint("Type comment");
                builder.setView(input);
                builder.setTitle("Comment");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String comment = input.getText().toString();
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                image.setFavorite(true);
                                image.setComment(comment);
                            }
                        });
                        textView.setText(comment);
                        fab.setImageResource(image.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

}
