package com.marklogic.client.configpojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
// TODO check this inheritance.  might need to override some methods or break inheritence
@XmlRootElement(name="default-suggestion-source")
public final class DefaultSuggestionSource  extends SuggestionSource {


}
