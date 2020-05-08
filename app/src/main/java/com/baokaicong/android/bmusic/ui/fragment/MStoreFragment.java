package com.baokaicong.android.bmusic.ui.fragment;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baokaicong.android.bmusic.R;


public class MStoreFragment extends BFragment {

    public MStoreFragment() {

    }

    public static MStoreFragment newInstance(String param1, String param2) {
        MStoreFragment fragment = new MStoreFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mstore, container, false);
    }

    @Override
    public void resume() {

    }

    @Override
    public boolean keyDown(int keyCode, KeyEvent event) {
        return false;
    }

}
