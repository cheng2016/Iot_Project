package com.cds.iot.module.review;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.cds.iot.R;
import com.cds.iot.view.photoview.PhotoViewAttacher;
import com.cds.iot.view.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private AppCompatImageView mImageView;
	private ProgressBar progressBar;
	private PhotoViewAttacher mAttacher;

	public static ImageDetailFragment newInstance(String imageUrl) {
		final ImageDetailFragment f = new ImageDetailFragment();

		final Bundle args = new Bundle();
		args.putString("url", imageUrl);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
		mImageView = (AppCompatImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}
		});
		
		progressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        new AsyncTask<Void,Void,Bitmap>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    Bitmap bitmap = Picasso.with(getActivity()).load(mImageUrl).get();
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mImageView.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
                mAttacher.update();
            }
        }.execute();
	}

}
