package com.marklogic.client.config.search;

public interface Heatmap extends JAXBBackedQueryOption {
	/**
     * Gets the northern boundary of the box.
     * 
     */
    public double getN();

    /**
     * Sets the northern boundary of the box.
     * 
     */
    public void setN(double value);

    /**
     * Gets the Southern boundary of the box.
     * 
     */
    public double getS();

    /**
     * Sets the Southern boundary of the box.
     * 
     */
    public void setS(double value);

    /**
     * Gets the Eastern boundary of the box.
     * 
     */
    public double getE();

    /**
     * Sets the Eastern boundary of the box.
     * 
     */
    public void setE(double value);
    /**
     * Gets the Western boundary of the box.
     * 
     */
    public double getW();

    /**
     * Sets the Western boundary of the box.
     * 
     */
    public void setW(double value);

    /**
     * Gets the number of latitudinal divisions.
     * 
     */
    public long getLatdivs();

    /**
     * Sets the number of latitudinal divisions.
     * 
     */
    public void setLatdivs(long value);
    
    /**
     * Gets the number of longitudinal divisions.
     * 
     */
    public long getLondivs();

    /**
     * Sets the number of longitudinal divisions.
     * 
     */
    public void setLondivs(long value);
}
