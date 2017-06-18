package com.example.suzukitakahiro.trainalert.Db.Dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 登録駅情報Dto
 *
 * @author suzukitakahiro on 2017/06/17.
 */

public class RegisterStationDto implements Parcelable{

    /** ID */
    public int _id = -1;

    /** 路線コード */
    public String line_cd = null;

    /** 路線名 */
    public String line_name = null;

    /** 駅コード */
    public String station_cd = null;

    /** 駅名 */
    public String station_name = null;

    /** 経度 */
    public double st_latitude = 0;

    /** 緯度 */
    public double st_longitude = 0;

    public RegisterStationDto() {}

    protected RegisterStationDto(Parcel in) {
        line_cd = in.readString();
        line_name = in.readString();
        station_cd = in.readString();
        station_name = in.readString();
        st_latitude = in.readDouble();
        st_longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(line_cd);
        dest.writeString(line_name);
        dest.writeString(station_cd);
        dest.writeString(station_name);
        dest.writeDouble(st_latitude);
        dest.writeDouble(st_longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
}
