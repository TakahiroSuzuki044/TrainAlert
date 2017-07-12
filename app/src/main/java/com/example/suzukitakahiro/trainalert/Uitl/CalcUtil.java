package com.example.suzukitakahiro.trainalert.Uitl;

/**
 * @author suzukitakahiro on 2016/09/12.
 */
public class CalcUtil {

    /**
     * 二点間の緯度経度から距離を求めます
     *
     * @param latitudeA  緯度A
     * @param longitudeA 経度A
     * @param latitudeB  緯度B
     * @param longitudeB 経度B
     * @return 二点間の距離
     */
    public double calcTwoPointDistance(double latitudeA, double longitudeA, double latitudeB, double longitudeB) {

        // 緯度経度の差の絶対値
        double deltaLatitude = Math.abs(latitudeA - latitudeB);
        double deltaLongitude = Math.abs(longitudeA - longitudeB);

        // sin^2 (d/2) = sin^2 (Δδ/2) + (cosδA)×(cosδB)×sin^2 (Δλ/2)    (δ = 緯度、λ = 経度、d = 二点間の距離)
        double temp = Math.pow(Math.sin(deltaLatitude / 2), 2) + Math.cos(latitudeA) * Math.cos(latitudeB) * Math.pow(Math.sin(deltaLongitude / 2), 2);
        double distance = (Math.asin(Math.sqrt(temp)) * 2) * 6370 * 1000;

        return distance;
    }
}
