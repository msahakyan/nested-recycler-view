package com.android.msahakyan.nestedrecycler.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.msahakyan.nestedrecycler.R;
import com.android.msahakyan.nestedrecycler.application.AppController;
import com.android.msahakyan.nestedrecycler.model.Backdrop;
import com.android.msahakyan.nestedrecycler.net.Endpoint;
import com.android.msahakyan.nestedrecycler.view.FadeInNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * @author msahakyan
 */
public class BackdropPagerAdapter extends PagerAdapter {

    private LayoutInflater mLayoutInflater;
    private List<Backdrop> mBackdropList;

    public BackdropPagerAdapter(Context context, List<Backdrop> backdropList) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBackdropList = backdropList;
    }

    @Override
    public int getCount() {
        return mBackdropList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.backdrop_pager_item, container, false);

        FadeInNetworkImageView backdropImageView = (FadeInNetworkImageView) itemView.findViewById(R.id.backdrop_image);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        backdropImageView.setImageUrl(Endpoint.IMAGE + "/w500/" + mBackdropList.get(position).getFilePath(), imageLoader);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FadeInNetworkImageView) object);
    }
}
