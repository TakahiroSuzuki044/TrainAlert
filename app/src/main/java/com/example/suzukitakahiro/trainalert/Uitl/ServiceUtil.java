package com.example.suzukitakahiro.trainalert.Uitl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by suzukitakahiro on 2016/12/18.
 *
 * サービスUtil
 */
public class ServiceUtil {

    /**
     * 引数で与えられたサービスが実行中であるかチェックする
     *
     * @param activity アクティビティ
     * @param serviceName 実行中か調べるサービス名
     * @return 実行中の場合True.
     */
    public static boolean checkStartedService(Activity activity, String serviceName) {
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo serviceInfo: serviceInfos) {

            // 実行中のサービス名とチェックサービス名が一致
            if (serviceInfo.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
