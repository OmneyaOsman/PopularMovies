package com.omni.moviewdb.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;



public abstract class BaseFragment extends Fragment {



    protected boolean isNetworkConnected() {
        boolean isConnected = false;

        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }
}
