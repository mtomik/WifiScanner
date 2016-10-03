
package com.fei.mv.wifiscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WifiScan {

    @SerializedName("SSID")
    @Expose
    private String sSID;
    @SerializedName("RSSI")
    @Expose
    private String rSSI;

    /**
     * 
     * @return
     *     The sSID
     */
    public String getSSID() {
        return sSID;
    }

    /**
     * 
     * @param sSID
     *     The SSID
     */
    public void setSSID(String sSID) {
        this.sSID = sSID;
    }

    /**
     * 
     * @return
     *     The rSSI
     */
    public String getRSSI() {
        return rSSI;
    }

    /**
     * 
     * @param rSSI
     *     The RSSI
     */
    public void setRSSI(String rSSI) {
        this.rSSI = rSSI;
    }

}
