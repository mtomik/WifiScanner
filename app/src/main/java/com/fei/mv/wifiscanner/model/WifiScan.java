
package com.fei.mv.wifiscanner.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WifiScan{

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
     *     The is_used
     */
    public void setIs_used(int is_used) {
        this.is_used = is_used;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WifiScan)) return false;

        WifiScan wifiScan = (WifiScan) o;

        if (rSSI != null ? !rSSI.equals(wifiScan.rSSI) : wifiScan.rSSI != null) return false;
        return mAc != null ? mAc.equals(wifiScan.mAc) : wifiScan.mAc == null;

    }

    @Override
    public int hashCode() {
        int result = rSSI != null ? rSSI.hashCode() : 0;
        result = 31 * result + (mAc != null ? mAc.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WifiScan{" +
                "sSID='" + sSID + '\'' +
                ", rSSI='" + rSSI + '\'' +
                ", mAc='" + mAc + '\'' +
                ", is_used=" + is_used +
                '}';
    }
}

