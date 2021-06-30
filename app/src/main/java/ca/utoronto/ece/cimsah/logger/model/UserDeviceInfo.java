package ca.utoronto.ece.cimsah.logger.model;

import com.jaredrummler.android.device.DeviceName;

import java.util.Date;

public class UserDeviceInfo {
    // doesn't contain UID because the object is created with the UID as its key
    private Date timestamp;
    private String manufacturer;
    private String marketName;
    private String model;
    private Integer apiLevel;

    public UserDeviceInfo() {}

    public UserDeviceInfo(Date timestamp, DeviceName.DeviceInfo info, Integer apiLevel) {
        this.timestamp = timestamp;
        this.manufacturer = info.manufacturer;
        this.marketName = info.marketName;
        this.model = info.model;
        this.apiLevel = apiLevel;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(Integer apiLevel) {
        this.apiLevel = apiLevel;
    }

    @Override
    public String toString() {
        return "UserDeviceInfo{" +
                "timestamp=" + timestamp +
                ", manufacturer='" + manufacturer + '\'' +
                ", marketName='" + marketName + '\'' +
                ", model='" + model + '\'' +
                ", apiLevel=" + apiLevel +
                '}';
    }
}
