package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Configures a range, for use in grouping range index values in facets.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Bucket {

	/**
	 * The textual label for the bucket.
	 */
	@XmlValue
	private String content;
	/**
	 * The low end of the bucket's range.  Stands for "greater than or equal to".
	 */
	@XmlAttribute
	private String ge;
	/**
	 * The high end of the bucket's range.  Stands for "less than"
	 */
	@XmlAttribute
	private String lt;
	/**
	 * A unique name to reference this bucket.
	 */
	@XmlAttribute
	private String name;

	public String getContent() {
		return content;
	}

	public String getGe() {
		return ge;
	}

	public String getLt() {
		return lt;
	}

	public String getName() {
		return name;
	}

	public void setContent(String content) {
		this.content = content;
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
