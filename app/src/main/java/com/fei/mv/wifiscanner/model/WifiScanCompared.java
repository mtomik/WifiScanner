
package com.fei.mv.wifiscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WifiScanCompared {

    @SerializedName("SSID")
    @Expose
    private String sSID;
    @SerializedName("RSSI")
    @Expose
    private String rSSI;

    @SerializedName("MAC")
    @Expose
    private String mAc;

    @SerializedName("is_used")
    @Expose
    private int is_used;

    @SerializedName("compareResult")
    @Expose
    private String compareResult;

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


    /**
     *
     * @return
     *     The mAC
     */
    public String getMAC() {
        return mAc;
    }

    /**
     *
     * @param mAC
     *     The RSSI
     */
    public void setMAC(String mAC) {
        this.mAc = mAC;
    }

    /**
     *
     * @return
     *     The is_used
     */
    public int getIs_used() {
        return is_used;
    }

    /**
     *
     * @param is_used
     *     The RSSI
     */
    public void setIs_used(int is_used) {
        this.is_used = is_used;
    }

    /**
     *
     * @return
     *     The compareResult
     */
    public String getcompareResult() {
        return compareResult;
    }

    /**
     *
     * @param compareResult
     *     The RSSI
     */
    public void setcompareResult(String compareResult) {
        this.compareResult = compareResult;
    }


}

