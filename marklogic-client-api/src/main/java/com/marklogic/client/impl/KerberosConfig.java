package com.marklogic.client.impl;

public class KerberosConfig {

    private boolean refreshKrb5Config = true;
    private String principal = null;
    private boolean useTicketCache = true;
    private String ticketCache = null;
    private boolean renewTGT = false;
    private boolean doNotPrompt = true;
    private boolean useKeyTab = false;
    private String keyTab = null;
    private boolean storeKey = false;
    private boolean isInitiator = true;
    private boolean useFirstPass = false;
    private boolean tryFirstPass = false;
    private boolean storePass = false;
    private boolean clearPass = false;
    private boolean debug = false;

    public KerberosConfig() {
    }

    public void setRefreshKrb5Config(boolean refreshKrb5Config) {
        this.refreshKrb5Config = refreshKrb5Config;
    }

    public String getRefreshKrb5Config() {
        return String.valueOf(this.refreshKrb5Config);
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPrincipal() {
        return this.principal;
    }

    public void setUseTicketCache(boolean useTicketCache) {
        this.useTicketCache = useTicketCache;
    }

    public String getUseTicketCache() {
        return String.valueOf(this.useTicketCache);
    }

    public void setTicketCache(String ticketCache) {
        this.ticketCache = ticketCache;
    }

    public String getTicketCache() {
        return this.ticketCache;
    }

    public void setRenewTGT(boolean renewTGT) {
        this.renewTGT = renewTGT;
    }

    public String getRenewTGT() {
        return String.valueOf(this.renewTGT);
    }

    public void setDoNotPrompt(boolean doNotPrompt) {
        this.doNotPrompt = doNotPrompt;
    }

    public String getDoNotPrompt() {
        return String.valueOf(this.doNotPrompt);
    }

    public void setUseKeyTab(boolean useKeyTab) {
        this.useKeyTab = useKeyTab;
    }

    public String getUseKeyTab() {
        return String.valueOf(this.useKeyTab);
    }

    public void setKeyTab(String keyTab) {
        this.keyTab = keyTab;
    }

    public String getKeyTab() {
        return this.keyTab;
    }

    public void setStoreKey(boolean storeKey) {
        this.storeKey = storeKey;
    }

    public String getStoreKey() {
        return String.valueOf(this.storeKey);
    }

    public void setUseFirstPassy(boolean useFirstPass) {
        this.useFirstPass = useFirstPass;
    }

    public String getUseFirstPass() {
        return String.valueOf(this.useFirstPass);
    }

    public void setTryFirstPass(boolean tryFirstPass) {
        this.tryFirstPass = tryFirstPass;
    }

    public String getTryFirstPass() {
        return String.valueOf(this.tryFirstPass);
    }

    public void setStorePass(boolean storePass) {
        this.storePass = storePass;
    }

    public String getStorePass() {
        return String.valueOf(this.storePass);
    }

    public void setClearPass(boolean clearPass) {
        this.clearPass = clearPass;
    }

    public String getClearPass() {
        return String.valueOf(this.clearPass);
    }

    public void setInitiator(boolean initiator) {
        this.isInitiator = initiator;
    }

    public String getInitiator() {
        return String.valueOf(this.isInitiator);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getDebug() {
        return String.valueOf(this.debug);
    }
}
