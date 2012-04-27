package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Corresponds to the &lt;properties&gt; constraint type in the MarkLogic Search API
 * 
 * @author cgreer
 *
 */
@XmlRootElement(name="properties")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Properties extends ConstraintDefinition<Properties> {


	public Properties() {
	}

}
