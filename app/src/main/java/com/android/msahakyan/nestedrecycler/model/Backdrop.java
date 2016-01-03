package com.android.msahakyan.nestedrecycler.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * @author msahakyan
 */
public class Backdrop implements Parcelable {

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getCoteAverage() {
        return coteAverage;
    }

    public void setCoteAverage(double coteAverage) {
        this.coteAverage = coteAverage;
    }

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("vote_average")
    private double coteAverage;

    protected Backdrop(Parcel in) {
        filePath = in.readString();
        voteCount = in.readInt();
        coteAverage = in.readDouble();
    }

    public static final Creator<Backdrop> CREATOR = new Creator<Backdrop>() {
        @Override
        public Backdrop createFromParcel(Parcel in) {
            return new Backdrop(in);
        }

        @Override
        public Backdrop[] newArray(int size) {
            return new Backdrop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeInt(voteCount);
        dest.writeDouble(coteAverage);
    }
}
