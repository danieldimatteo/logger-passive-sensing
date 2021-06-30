package ca.utoronto.ece.cimsah.logger.scales.sds;

import android.os.Parcel;
import android.os.Parcelable;

public class SdsResult implements Parcelable {
    public int getWorkDisruption() {
        return workDisruption;
    }

    public void setWorkDisruption(int workDisruption) {
        this.workDisruption = workDisruption;
    }

    public Boolean getNoWorkUnrelatedToDisorder() {
        return noWorkUnrelatedToDisorder;
    }

    public void setNoWorkUnrelatedToDisorder(Boolean noWorkUnrelatedToDisorder) {
        this.noWorkUnrelatedToDisorder = noWorkUnrelatedToDisorder;
    }

    public int getSocialLifeDisruption() {
        return socialLifeDisruption;
    }

    public void setSocialLifeDisruption(int socialLifeDisruption) {
        this.socialLifeDisruption = socialLifeDisruption;
    }

    public int getFamilyLifeDisruption() {
        return familyLifeDisruption;
    }

    public void setFamilyLifeDisruption(int familyLifeDisruption) {
        this.familyLifeDisruption = familyLifeDisruption;
    }

    public int getDaysLost() {
        return daysLost;
    }

    public void setDaysLost(int daysLost) {
        this.daysLost = daysLost;
    }

    public int getDaysUnproductive() {
        return daysUnproductive;
    }

    public void setDaysUnproductive(int daysUnproductive) {
        this.daysUnproductive = daysUnproductive;
    }

    private int workDisruption;
    private Boolean noWorkUnrelatedToDisorder;
    private int socialLifeDisruption;
    private int familyLifeDisruption;
    private int daysLost;
    private int daysUnproductive;

    public SdsResult() {
        workDisruption = 0;
        noWorkUnrelatedToDisorder = false;
        socialLifeDisruption = 0;
        familyLifeDisruption = 0;
        daysLost = 0;
        daysUnproductive = 0;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public SdsResult(Parcel in){
        workDisruption = in.readInt();
        noWorkUnrelatedToDisorder = in.readInt() != 0;
        socialLifeDisruption = in.readInt();
        familyLifeDisruption = in.readInt();
        daysLost = in.readInt();
        daysUnproductive = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(workDisruption);
        out.writeInt(noWorkUnrelatedToDisorder ? 1 : 0);
        out.writeInt(socialLifeDisruption);
        out.writeInt(familyLifeDisruption);
        out.writeInt(daysLost);
        out.writeInt(daysUnproductive);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SdsResult createFromParcel(Parcel in) {
            return new SdsResult(in);
        }

        public SdsResult[] newArray(int size) {
            return new SdsResult[size];
        }
    };

    @Override
    public String toString() {
        return "SdsResult{" +
                "workDisruption=" + workDisruption +
                ", noWorkUnrelatedToDisorder=" + noWorkUnrelatedToDisorder +
                ", socialLifeDisruption=" + socialLifeDisruption +
                ", familyLifeDisruption=" + familyLifeDisruption +
                ", daysLost=" + daysLost +
                ", daysUnproductive=" + daysUnproductive +
                '}';
    }
}
