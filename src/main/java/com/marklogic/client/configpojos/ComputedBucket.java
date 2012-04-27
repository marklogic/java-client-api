package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import com.marklogic.client.configpojos.AnchorValue;

/**
 * Models a bucket on a range constraint whose values are anchored to time, and computed based on the current time.
 * @see com.marklogic.client.configpojos.Range
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ComputedBucket {

	/**
	 * A value for anchoring this computed bucket.
	 * @see com.marklogic.client.configpojos.AnchorValue
	 */
	@XmlAttribute(name="anchor")
	private String anchor;

	/**
	 * The low end of the bucket's range.  Stands for "greater than or equal to".
	 */
	@XmlAttribute
	private String ge;

	/**
	 * A value for anchoring the "greate than or equal" value for this computed bucket.
	 * @see com.marklogic.client.configpojos.AnchorValue
	 */
	@XmlAttribute(name="ge-anchor")
	private String geAnchor;

	/**
	 * The textual label for the bucket.
	 */
	@XmlValue
	private String label;
	
	/**
	 * The high end of the bucket's range.  Stands for "less than"
	 */
	@XmlAttribute
	private String lt;
	
	/**
	 * A value for anchoring the "less than" value for this computed bucket.
	 * @see com.marklogic.client.configpojos.AnchorValue
	 */	
	@XmlAttribute(name="lt-anchor")
	private String ltAnchor;
	
	/**
	 * A unique name to reference this bucket.
	 */
	@XmlAttribute
	private String name;
	public String getAnchor() {
		return anchor;
	}
	public AnchorValue getAnchorValue() {
		return AnchorValue.fromXmlString(anchor);
	}
	public String getGe() {
		return ge;
	}
	
	
	public String getGeAnchor() {
		return geAnchor;
	}

	public String getLabel() {
		return label;
	}


	public String getLt() {
		return lt;
	}

	public String getLtAnchor() {
		return ltAnchor;
	}

	public String getName() {
		return name;
	}

	public void setAnchor(AnchorValue anchorValue) {
		this.anchor = anchorValue.toXmlString();
	}

	public void setContent(String content) {
		this.label = content;
	}

	public void setGe(String ge) {
		this.ge = ge;
	}

	public void setLt(String lt) {
		this.lt = lt;
	}

	public void setName(String name) {
		this.name = name;
	}


}
