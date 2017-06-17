package com.example.suzukitakahiro.trainalert.Db.Dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by suzukitakahiro on 2017/06/17.
 *
 * 登録駅情報Dto
 */

public class RegisterStationDto implements Parcelable {

    public static String line_cd = "line_cd";

    public static String line_name = "line_name";

    public static String station_cd = "station_cd";

    public static String st_latitude = "st_latitude";

    public static String st_longitude = "st_longitude";

    protected RegisterStationDto(Parcel in) {
    }

    public RegisterStationDto(){}

    public static final Creator<RegisterStationDto> CREATOR = new Creator<RegisterStationDto>() {
        @Override
        public RegisterStationDto createFromParcel(Parcel in) {
            return new RegisterStationDto(in);
        }

        @Override
        public RegisterStationDto[] newArray(int size) {
            return new RegisterStationDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
