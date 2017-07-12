package com.example.suzukitakahiro.trainalert.Db.Dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 都道府県Dto
 * <p>
 * Created by suzukitakahiro on 2017/07/08.
 */

public class AreaDto implements Parcelable {

    /**
     * 都道府県コード
     */
    public int pref_cd = -1;

    /**
     * 都道府県名
     */
    public String pref_name = null;

    public AreaDto() {

    }

    protected AreaDto(Parcel in) {
        pref_cd = in.readInt();
        pref_name = in.readString();
    }

    public static final Creator<AreaDto> CREATOR = new Creator<AreaDto>() {
        @Override
        public AreaDto createFromParcel(Parcel in) {
            return new AreaDto(in);
        }

        @Override
        public AreaDto[] newArray(int size) {
            return new AreaDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pref_cd);
        dest.writeString(pref_name);
    }
}
