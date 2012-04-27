package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;


@XmlAccessorType(XmlAccessType.FIELD)
public class Heatmap {

    @XmlAttribute
    private double n;
    @XmlAttribute
    private double s;
    @XmlAttribute
    private double e;
    @XmlAttribute
    private double w;
    @XmlAttribute
    private int latdivs;
    @XmlAttribute
    private int londivs;
	public double getN() {
		return n;
	}
	public Heatmap setN(double n) {
		this.n = n;
		return this;
	}
	public double getS() {
		return s;
	}
	public Heatmap setS(double s) {
		this.s = s;
		return this;
	}
	
	public double getE() {
		return e;
	}
	public Heatmap setE(double e) {
		this.e = e;
		return this;
	}
	public double getW() {
		return w;
	}
	public Heatmap setW(double w) {
		this.w = w;
		return this;
	}
	public int getLatdivs() {
		return latdivs;
	}
	public Heatmap setLatdivs(int latdivs) {
		this.latdivs = latdivs;
		return this;
	}
	public int getLondivs() {
		return londivs;
	}
	public Heatmap setLondivs(int londivs) {
		this.londivs = londivs;
		return this;
	}
}
