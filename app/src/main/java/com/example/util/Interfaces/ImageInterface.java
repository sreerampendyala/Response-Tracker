package com.example.util.Interfaces;

import android.net.Uri;

public interface ImageInterface {

    void statusAndUri(boolean isSuccess, Uri uri);
    void onFailure(String errMessage);
}
