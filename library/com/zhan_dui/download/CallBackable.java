package com.zhan_dui.download;


public interface CallBackable<V> {

    V callback(DownloadMission mission) throws Exception;
}
