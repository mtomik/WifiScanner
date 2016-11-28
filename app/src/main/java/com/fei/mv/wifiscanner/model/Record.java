
package com.fei.mv.wifiscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Record {
    private int id;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("floor")
    @Expose
    private String floor;
    @SerializedName("edited_at")
    @Expose
    private Date edited_at;
    @SerializedName("wifiScan")
    @Expose
    private List<WifiScan> wifiScan = new ArrayList<WifiScan>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
     *     The edited_at
     */
    public Date getEdited_at() {
        return edited_at;
    }

    /**
     *
     * @param edited_at
     *     The section
     */
    public void setEdited_at(Date edited_at) {
        this.edited_at = edited_at;
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
