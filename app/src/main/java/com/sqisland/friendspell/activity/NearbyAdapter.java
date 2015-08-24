package com.sqisland.friendspell.activity;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sqisland.friendspell.R;
import com.sqisland.friendspell.storage.NearbyPerson;
import com.sqisland.friendspell.ui.CircleTransform;
import com.sqisland.friendspell.util.ViewUtil;
import com.squareup.picasso.Picasso;

public class NearbyAdapter extends ArrayAdapter<NearbyPerson> {
  private final CircleTransform circleTransform;

  public NearbyAdapter(Context context, int resource) {
    super(context, resource);
    circleTransform = new CircleTransform();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView textView = (TextView) super.getView(position, convertView, parent);
    NearbyPerson source = this.getItem(position);

    if (!TextUtils.isEmpty(source.imageUrl)) {
      Picasso.with(getContext())
          .load(source.imageUrl)
          .placeholder(R.drawable.nearby_person_thumbnail_placeholder)
          .resizeDimen(R.dimen.nearby_person_thumbnail_size, R.dimen.nearby_person_thumbnail_size)
          .transform(circleTransform)
          .into(new TextViewTarget(textView));
    }
    textView.setText(source.displayName);

    switch (source.state) {
      case NEW:
      case REDISCOVERED:
        ViewUtil.setDrawable(
            textView,
            ContextCompat.getDrawable(textView.getContext(), R.drawable.ic_newly_tagged),
            2);
        break;
    }

    return textView;
  }
}