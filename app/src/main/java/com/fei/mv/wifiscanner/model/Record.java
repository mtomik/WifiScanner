
package com.fei.mv.wifiscanner.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Record {

    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("floor")
    @Expose
    private String floor;
    @SerializedName("wifiScan")
    @Expose
    private List<WifiScan> wifiScan = new ArrayList<WifiScan>();

    /**
     * 
     * @return
     *     The section
     */
    public String getSection() {
        return section;
    }

    /**
     * 
     * @param section
     *     The section
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * 
     * @return
     *     The floor
     */
    public String getFloor() {
        return floor;
    }

    /**
     * 
     * @param floor
     *     The floor
     */
    public void setFloor(String floor) {
        this.floor = floor;
    }

    /**
     * 
     * @return
     *     The wifiScan
     */
    public List<WifiScan> getWifiScan() {
        return wifiScan;
    }

    /**
     * 
     * @param wifiScan
     *     The wifiScan
     */
    public void setWifiScan(List<WifiScan> wifiScan) {
        this.wifiScan = wifiScan;
    }

}
