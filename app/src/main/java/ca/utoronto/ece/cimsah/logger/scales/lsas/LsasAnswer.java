package ca.utoronto.ece.cimsah.logger.scales.lsas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dandm on 3/17/2016.
 */
public class LsasAnswer implements Parcelable {
    public int fear;
    public int avoidance;

    public int describeContents() {
        return 0;
    }

    public LsasAnswer(int fear, int avoidance) {
        this.fear = fear;
        this.avoidance = avoidance;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(fear);
        out.writeInt(avoidance);
    }

    public static final Parcelable.Creator<LsasAnswer> CREATOR = new Parcelable.Creator<LsasAnswer>() {
        public LsasAnswer createFromParcel(Parcel in) {
            return new LsasAnswer(in);
        }
        public LsasAnswer[] newArray(int size) {
            return new LsasAnswer[size];
        }
    };

    public LsasAnswer(Parcel in) {
        fear = in.readInt();
        avoidance = in.readInt();
    }

    @Override
    public String toString() {
        return "(" + fear + "," + avoidance + ")";
    }

    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (other instanceof LsasAnswer) {
            LsasAnswer that = (LsasAnswer) other;
            result = (this.fear == that.fear && this.avoidance == that.avoidance);
        }
        return result;
    }
}
