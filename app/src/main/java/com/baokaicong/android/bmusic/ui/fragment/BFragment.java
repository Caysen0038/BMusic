package com.baokaicong.android.bmusic.ui.fragment;

import android.view.KeyEvent;

import androidx.fragment.app.Fragment;

public abstract class BFragment extends Fragment {

    public abstract void resume();

    public abstract boolean keyDown(int keyCode, KeyEvent event);
}
