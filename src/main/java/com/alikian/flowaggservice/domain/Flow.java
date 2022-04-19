package com.alikian.flowaggservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicInteger;

public class Flow {

    @JsonProperty("src_app")
    private String srcApp;
    @JsonProperty("dest_app")
    private String destApp;
    @JsonProperty("vpc_id")
    private String vpcId;
    @JsonProperty("bytes_tx")
    private AtomicInteger bytesTx;
    @JsonProperty("bytes_rx")
    private AtomicInteger bytesRx;
    private int hour;

    public String getSrcApp() {
        return srcApp;
    }

    public void setSrcApp(String srcApp) {
        this.srcApp = srcApp;
    }

    public String getDestApp() {
        return destApp;
    }

    public void setDestApp(String destApp) {
        this.destApp = destApp;
    }

    public String getVpcId() {
        return vpcId;
    }

    public void setVpcId(String vpcId) {
        this.vpcId = vpcId;
    }

    public int getBytesTx() {
        return bytesTx.get();
    }

    public void setBytesTx(int bytesTx) {
        this.bytesTx = new AtomicInteger(bytesTx);
    }

    public int getBytesRx() {
        return bytesRx.get();
    }

    public void setBytesRx(int bytesRx) {
        this.bytesRx = new AtomicInteger(bytesRx);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @JsonIgnore
    public String getKey() {
        return srcApp + "|" + destApp + "|" + vpcId;
    }

    public synchronized void addTransfer(Flow flow) {
        bytesRx.addAndGet(flow.getBytesRx());
        bytesTx.addAndGet(flow.getBytesTx());
    }

}
