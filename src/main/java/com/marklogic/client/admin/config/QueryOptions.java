/*
 * Copyright 2012-2015 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.admin.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import com.marklogic.client.admin.config.support.Annotatable;
import com.marklogic.client.admin.config.support.Buckets;
import com.marklogic.client.admin.config.support.ConstraintSource;
import com.marklogic.client.admin.config.support.Indexed;
import com.marklogic.client.admin.config.support.MetadataExtract;
import com.marklogic.client.admin.config.support.RangeIndexed;
import com.marklogic.client.admin.config.support.TermIndexed;
import com.marklogic.client.admin.config.support.TermOptions;
import com.marklogic.client.admin.config.support.TermSource;
import com.marklogic.client.admin.config.support.TupleSource;
import com.marklogic.client.impl.Utilities;
import com.marklogic.client.util.EditableNamespaceContext;

/**
 * @deprecated Use a JSON or XML 
 * {@link com.marklogic.client.io.marker.StructureWriteHandle write handle} or
 * {@link com.marklogic.client.io.marker.StructureReadHandle read handle}
 * implementation instead of this class to write or read
 * query options.  For instance:
 * <pre>{@code
 *  String opts = new StringBuilder()
 *      .append("<options xmlns=\"http://marklogic.com/appservices/search\">")
 *      .append(    "<debug>true</debug>")
 *      .append("</options>")
 *      .toString();
 *  optsMgr.writeOptions("debug", new StringHandle(opts)); }</pre>
 * or
 * <pre>{@code
 *  String opts = "{\"options\":{\"debug\":true}}";
 *  optsMgr.writeOptions("debug", new StringHandle(opts).withFormat(Format.JSON)); }</pre>
 */
@Deprecated
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = QueryOptions.SEARCH_NS, name = "options")
public final class QueryOptions implements Annotatable<QueryOptions> {

    /**
     * Two possible states for faceting.  Used in range index definitions.
     */
	public static enum Facets { FACETED, UNFACETED };

	public static final String DEFAULT_COLLATION = "http://marklogic.com/collation/";
	
	/**
	 * Models a constraint node in Search API configuration.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryConstraint 
			implements Annotatable<QueryConstraint> {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "custom")
		private QueryCustom custom;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element-query")
		private QueryElementQuery elementQuery;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attr-pair")
		private QueryGeospatialAttributePair geoAttrPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem")
		private QueryGeospatialElement geoElem;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem-pair")
		private QueryGeospatialElementPair geoElemPair;

		@XmlAttribute
		private String name;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "properties")
		private QueryProperties properties;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

		/**
		 * Zero-argument constructor.
		 */
		public QueryConstraint() {
			super();
		}

		/**
		 * Construct with the name of the constraint.
		 */
		public QueryConstraint(String name) {
			this();
			annotations = new ArrayList<QueryAnnotation>();
			setName(name);
		}

		/**
		 * Returns the list of annotations on the constraint.
		 * @return	the annotations
		 */
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}

		/**
		 * Returns the name of the constraint.
		 * @return	the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the constrained index.
		 * @return	the index specification
		 */
		@SuppressWarnings("unchecked")
		public <T extends BaseConstraintItem> T getSource() {
			if (collection != null) {
				return (T) collection;
			} else if (value != null) {
				return (T) value;
			} else if (range != null) {
				return (T) range;
			} else if (word != null) {
				return (T) word;
			} else if (elementQuery != null) {
				return (T) elementQuery;
			} else if (properties != null) {
				return (T) properties;
			} else if (custom != null) {
				return (T) custom;
			} else if (geoElem != null) {
				return (T) geoElem;
			} else if (geoAttrPair != null) {
				return (T) geoAttrPair;
			} else if (geoElemPair != null) {
				return (T) geoElemPair;
			}
			return null;
		}

		/**
		 * Specifies the name of the constraint.
		 * @param name	the constraint name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Specifies the constrained index.
		 * @param constraintDefinition	the index specification
		 */
		public void setSource(ConstraintSource constraintDefinition) {
			if (constraintDefinition instanceof QueryCollection) {
				collection = (QueryCollection) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryValue.class) {
				value = (QueryValue) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryWord.class) {
				word = (QueryWord) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryRange.class) {
				range = (QueryRange) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryElementQuery.class) {
				elementQuery = (QueryElementQuery) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryProperties.class) {
				properties = (QueryProperties) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryCustom.class) {
				custom = (QueryCustom) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryGeospatialElement.class) {
				geoElem = (QueryGeospatialElement) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryGeospatialAttributePair.class) {
				geoAttrPair = (QueryGeospatialAttributePair) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryGeospatialElementPair.class) {
				geoElemPair = (QueryGeospatialElementPair) constraintDefinition;
			}
		}

		/**
		 * Adds an annotation and returns the constraint.
		 */
		public QueryConstraint annotate(String xmlAnnotation) {
			QueryAnnotation annotation = new QueryAnnotation();
			annotation.add(Utilities.domElement(xmlAnnotation));
			annotations.add(annotation);
			return this;
		}

	}

	/**
	 * Partial implementation of A ConstraintSource.
	 * 
	 * Each constraint in the MarkLogic Search API is of a certain type. This
	 * class is the root of the class hierarchy of Range, Word,
	 * Value, and so forth. Note: This partial implementation
	 * contains convenience methods for helping with
	 * index definitions, which are not applicable to all constraint types.
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static abstract class BaseConstraintItem {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "attribute")
		private MarkLogicQName attributeReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private MarkLogicQName elementReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field fieldReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "json-key")
		private JsonKey jsonKey;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "namespace-bindings")
		private ExpressionNamespaceBindings pathIndexBindings;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "path-index")
		private PathIndex pathIndexReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "fragment-scope")
		private String fragmentScope;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term-option")
		private List<String> termOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "weight")
		private Double weight = null;

		/**
		 * Construct a new BaseConstraintItem
		 */
		public BaseConstraintItem() {
			this.termOptions = new ArrayList<String>();
		}

		/**
		 * Adds a term option to this data source.
		 * @param termOption A term option.
		 */
		public void addTermOption(String termOption) {
			this.termOptions.add(termOption);
		}

		/**
		 * Gets the XML QName referring to an attribute.
		 * @return The attribute QName.  Returns null if this object does not contain an element-attribute index specification.
		 */
		public QName getAttribute() {
			return attributeReference.asQName();
		}

		/**
		 * Gets the XML QName referring to an element.
		 * @return The element QName.  Returns null if this object does not contain an element-attribute or element index specification.
		 */
		public QName getElement() {
			return elementReference.asQName();
		}

		/**
		 * Gets the string representing a path index data source.
		 * @return The XPath string.  Returns null if this object does not contain a path index specification.
		 */
		public String getPath() {
			if (pathIndexReference != null)
				return pathIndexReference.getPath();
			else return null;
		}

		/**
		 * Gets the namespace context in scope for a path index configuration.
		 * @return The namespace context with bindings.  Returns null if this object does not contain a path index specification.
		 */
		public NamespaceContext getPathNamespaceBindings() {
			if (pathIndexBindings == null) return null;
			else {
				EditableNamespaceContext context = new EditableNamespaceContext();
				for (ExpressionNamespaceBinding binding : pathIndexBindings.bindings) {
					String prefix = binding.getPrefix();
					String uri = binding.getNamespaceURI();
					if ("".equals(prefix)) {
						context.setDefaultNamespaceURI(uri);
					} else {
						context.setNamespaceURI(prefix, uri);
					}
				}
				return context;
			}
		}

		/**
		 * Sets the path index namespace bindings.
		 * @param bindings The namespace bindings.
		 */
		protected void setPathIndexNamespaceBindings(
				ExpressionNamespaceBindings bindings) {
			pathIndexBindings = bindings;
		}

		/**
		 * Gets the path index object that backs this data source.
		 * @return A PathIndex object containing an XPath expression and namespace bindings.
		 * Returns null if this object does not contain a path index specification.
		 */
		public PathIndex getPathIndex() {
			return pathIndexReference;
		}
		
		/**
		 * Gets the name of a field that backs this constraint.
		 * @return The field name.  Returns null if this is not a field-index-backed constraint.
		 */
		public String getFieldName() {
			if (this.fieldReference != null)
				return this.fieldReference.getName();
			else return null;
		}

		/**
		 * Gets the name of a json key that backs this constraint.
		 * @return The field name.  Returns null if this is not a json-key-index-backed constraint.
		 */
		public String getJsonKeyName() {
			return this.jsonKey.getName();
		}

		/**
		 * Gets the FragmenScope of this constraint source.
		 * @return The fragmentScope.  Returns null if this constraint source relies on system default.
		 */
		public FragmentScope getFragmentScope() {
			if (fragmentScope != null) 
				return FragmentScope.valueOf(fragmentScope.toUpperCase());
			else return null;
		}

		/**
		 * Gets the term options for this constraint source.
		 * @return The list of term options.
		 */
		public List<String> getTermOptions() {
			return termOptions;
		}

		/**
		 * Gets the relative weight assigned to this constraint source.
		 * @return The weight.  Returns null if not defined.  Undefined weights are interpreted as 1.0.
		 */
		public Double getWeight() {
			return this.weight;
		}

		/**
		 * Sets an attribute QName to partially find an element-attribute index.
		 * @param attribute The attribute QName.  To be meaningful needs setElement to be called as well.
		 */
		public void setAttribute(MarkLogicQName attribute) {
			this.attributeReference = attribute;
		}

		/**
		 * Sets an element QName to reference an element.
		 * @param element The element QName.
		 */
		public void setElement(MarkLogicQName element) {
			this.elementReference = element;
		}

		/**
		 * Sets a field to back this constraint source.
		 * @param field The field object.
		 */
		public void setField(Field field) {
			this.fieldReference = field;
		}

		/**
		 * Sets a path index specification for this constraint source.
		 * @param pathIndex The path index specification.
		 */
		public void setPath(PathIndex pathIndex) {
			this.pathIndexReference = pathIndex;
		}

		/**
		 * Sets a json key index specification for this constraint source.
		 * @param jsonKey The json key.
		 */
		public void setJsonKey(JsonKey jsonKey) {
			this.jsonKey = jsonKey;
		}

		/**
		 * Sets a fragment scope for this constraint source.
		 * @param fragmentScope the fragment scope.
		 */
		public void setFragmentScope(FragmentScope fragmentScope) {
			if (fragmentScope != null) {
				this.fragmentScope = fragmentScope.toString().toLowerCase();
			}
		}

		/**
		 * Sets the term options for this constraint source.
		 * @param termOptions A list of term options.
		 */
		public void setTermOptions(List<String> termOptions) {
			this.termOptions = new ArrayList<String>();
			this.termOptions.addAll(termOptions);
		}

		/**
		 * Sets the relative weight for this constraint source.
		 * @param weight The weight.
		 */
		public void setWeight(Double weight) {
			this.weight = weight;
		}
	}

    /**
     * Partial implementation of constraint sources that can have facets
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public abstract static class FacetableConstraintConfiguration extends
			BaseConstraintItem {

		@XmlAttribute
		private String collation;

		@XmlAttribute(name = "facet")
		private Boolean doFacets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "facet-option")
		private List<String> facetOptions;

		@XmlAttribute
		private String type;

		/**
		 * Zero-argument constructor.
		 */
		public FacetableConstraintConfiguration() {
			facetOptions = new ArrayList<String>();
		}

		/**
		 * Add a facet option to this constraint type
		 * 
		 * @param facetOption
		 */
		public void addFacetOption(String facetOption) {
			this.facetOptions.add(facetOption);
		}

		/**
		 * Perform facets on this constraint.
		 * 
		 * @param doFacets
		 *            set to true to configure facets, false otherwise.  This field is nullable because facets are irrelevant in values and tuples configuration.
		 */
		public void doFacets(Boolean doFacets) {
			this.doFacets = doFacets;
		}

		/**
		 * Returns the collation for this facetable constraint.
		 * @return	the collation
		 */
		public String getCollation() {
			return collation;
		}

		/**
		 * Return whether this constraint is configured for facets.
		 * @return true or false
		 */
		public Boolean getDoFacets() {
			return doFacets;
		}

		/**
		 * Get the list of facet options.
		 * 
		 * @return A list of the facet options for this facetable contstraint.
		 */
		public List<String> getFacetOptions() {
			return facetOptions;
		}

		/**
		 * Returns the datatype of the facetable constraint.
		 * @return	the datatype
		 */
		public String getType() {
			return type;
		}

		/**
		 * Specifies the collation for the constraint.
		 * @param collation	the collation
		 */
		public void setCollation(String collation) {
			this.collation = collation;
		}

		/**
		 * Specifies whether to configure this constraint for facets.
		 * @param doFacets	true to use the constraint for facets
		 */
		public void setDoFacets(boolean doFacets) {
			this.doFacets = doFacets;
		}

		/**
		 * Specifies the list of facet options.
		 * @param facetOptions	options for faceting on the constraint
		 */
		public void setFacetOptions(List<String> facetOptions) {
			this.facetOptions = new ArrayList<String>();
			for (String option : facetOptions) {
				this.facetOptions.add(option);
			}
		}

		/**
		 * Specifies the datatype for the constraint.
		 * @param type	the datatype
		 */
		public void setType(String type) {
			this.type = type;
		}
	}

    /**
     * Models a value constraint
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryValue extends BaseConstraintItem implements
			ConstraintSource, TermIndexed, TermSource {
	}

    /**
     * A custom constraint.  Custom constraints use XQuery extensions to implement parsing and faceting functionality.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryCustom extends FacetableConstraintConfiguration implements TermOptions, ConstraintSource, Annotatable<QueryCustom>
			 {


		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "finish-facet")
		private XQueryExtension finishFacet;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "parse")
		private XQueryExtension parse;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "start-facet")
		private XQueryExtension startFacet;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;

		/**
		 * Gets the annotations on this custom constraint.
		 */
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		/**
		 * Adds an annotation, returning this custom constraint.
		 */
		public QueryCustom annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}
		
		/**
		 * Constructor for the custom constraint.
		 * @param doFacets	whether to build a facet on the custom constraint
		 */
		public QueryCustom(boolean doFacets) {
			this.doFacets(doFacets);
		}

		/**
		 * Constructor for the custom constraint.
		 * @param doFacets	whether to build a facet on the custom constraint
		 * @param parseExtension	the identification of the XQuery function that implements the custom constraint
		 */
		public QueryCustom(boolean doFacets, XQueryExtension parseExtension) {
			this.doFacets(doFacets);
			this.parse = parseExtension;
		}

		QueryCustom() {
			annotations = new ArrayList<QueryAnnotation>();
		}

		/**
		 * Returns the XQuery function called to finish the facet.
		 * @return	the identification of the XQuery function 
		 */
		public XQueryExtension getFinishFacet() {
			return finishFacet;
		}

		/**
		 * Returns the XQuery function called to implement the custom constraint.
		 * @return	the identification of the XQuery function 
		 */
		public XQueryExtension getParse() {
			return parse;
		}

		/**
		 * Returns the XQuery function called to start the facet.
		 * @return	the identification of the XQuery function 
		 */
		public XQueryExtension getStartFacet() {
			return startFacet;
		}

		/**
		 * Specifies an XQuery function called to finish the facet.
		 * @param finishFacet	the identification of the XQuery function 
		 */
		public void setFinishFacet(XQueryExtension finishFacet) {
			this.finishFacet = finishFacet;
		}

		/**
		 * Specifies an XQuery function called to implement the custom constraint.
		 * @param parse	the identification of the XQuery function 
		 */
		public void setParse(XQueryExtension parse) {
			this.parse = parse;
		}

		/**
		 * Specifies an XQuery function called to start the facet.
		 * @param startFacet	the identification of the XQuery function 
		 */
		public void setStartFacet(XQueryExtension startFacet) {
			this.startFacet = startFacet;
		}

	}

    /**
     * Models a range constraint source.
     *
     * A QueryRange is a child of a QueryConstraint and contains facet and index source configuration
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryRange extends
			FacetableConstraintConfiguration implements TupleSource, ConstraintSource, RangeIndexed, TermSource {

		/**
		 * Configures a range, for use in grouping range index values in facets.
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Bucket implements Buckets {

			@XmlValue
			private String content;
			@XmlAttribute
			private String ge;
			@XmlAttribute
			private String lt;
			@XmlAttribute
			private String name;

			/**
			 * Returns the textual label for the bucket.
			 * @return	the label
			 */
			public String getContent() {
				return content;
			}

			/**
			 * Returns the low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 * @return	the low range boundary
			 */
			public String getGe() {
				return ge;
			}

			/**
			 * Returns the high end of the bucket's range. Stands for
			 * "less than".
			 * @return	the high range boundary
			 */
			public String getLt() {
				return lt;
			}

			/**
			 * Returns a unique name to reference this bucket.
			 * @return	the name distinguishing the bucket
			 */
			public String getName() {
				return name;
			}

			/**
			 * Specifies the textual label for the bucket.
			 * @param content	the label
			 */
			public void setContent(String content) {
				this.content = content;
			}

			/**
			 * Specifies the low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 * @param ge	the low range boundary
			 */
			public void setGe(String ge) {
				this.ge = ge;
			}

			/**
			 * Specifies the high end of the bucket's range. Stands for
			 * "less than".
			 * @param lt	the high range boundary
			 */
			public void setLt(String lt) {
				this.lt = lt;
			}

			/**
			 * Specifies a unique name to reference this bucket.
			 * @param name	the name distinguishing the bucket
			 */
			public void setName(String name) {
				this.name = name;
			}
		}

		/**
		 * Models a bucket on a range constraint whose values are anchored to
		 * time, and computed based on the current time.
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class ComputedBucket implements
				Buckets {

			/**
			 * Defines values for use in computed buckets anchored to time.
			 */
			public static enum AnchorValue {

				/**
				 * An anchor relative to the current date and time.
				 */
				NOW,
				/**
				 * An anchor relative to the start of the current day.
				 */
				START_OF_DAY,
				/**
				 * An anchor relative to the start of the current month.
				 */
				START_OF_MONTH,
				/**
				 * An anchor relative to the start of the current year.
				 */
				START_OF_YEAR;

				static AnchorValue fromXmlString(String xmlString) {
					return AnchorValue.valueOf(xmlString.toUpperCase().replace(
							"-", "_"));
				}

				String toXmlString() {
					return this.toString().toLowerCase().replace("_", "-");
				}

			}

			@XmlAttribute(name = "anchor")
			private String anchor;

			@XmlAttribute
			private String ge;

			@XmlAttribute(name = "ge-anchor")
			private String geAnchor;

			@XmlValue
			private String label;

			@XmlAttribute
			private String lt;

			@XmlAttribute(name = "lt-anchor")
			private String ltAnchor;

			@XmlAttribute
			private String name;

			/**
			 * Returns a value for anchoring this computed bucket.
			 * @return	the anchor value
			 */
			public String getAnchor() {
				return anchor;
			}

			public AnchorValue getAnchorValue() {
				return AnchorValue.fromXmlString(anchor);
			}

			/**
			 * Returns the low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 * @return	the low end for the range
			 */
			public String getGe() {
				return ge;
			}

			/**
			 * Returns a value for anchoring the "greater than or equal" value
			 * for this computed bucket.
			 * @return	the low boundary for the bucket
			 */
			public String getGeAnchor() {
				return geAnchor;
			}

			/**
			 * Returns the textual label for the bucket.
			 * @return	the label
			 */
			public String getLabel() {
				return label;
			}

			/**
			 * Returns the high end of the bucket's range. Stands
			 * for "less than".
			 * @return	the high end of the range
			 */
			public String getLt() {
				return lt;
			}

			/**
			 * Returns a value for anchoring the "less than" value
			 * for this computed bucket.
			 * @return	the high boundary for the bucket
			 */
			public String getLtAnchor() {
				return ltAnchor;
			}

			/**
			 * Returns the unique name to reference this bucket.
			 * @return	the bucket name
			 */
			public String getName() {
				return name;
			}

			/**
			 * Specifies a value for anchoring this computed bucket.
			 * @param anchorValue	the anchor value
			 */
			public void setAnchor(AnchorValue anchorValue) {
				this.anchor = anchorValue.toXmlString();
			}

			/**
			 * Specifies the low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 * @param ge	the low end for the range
			 */
			public void setGe(String ge) {
				this.ge = ge;
			}

			/**
			 * Specifies the textual label for the bucket.
			 * @param content	the label
			 */
			public void setLabel(String content) {
				this.label = content;
			}

			/**
			 * Specifies the high end of the bucket's range. Stands
			 * for "less than".
			 * @param lt	the high end of the range
			 */
			public void setLt(String lt) {
				this.lt = lt;
			}

			/**
			 * Specifies unique name to reference this bucket.
			 * @param name	the bucket name
			 */
			public void setName(String name) {
				this.name = name;
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "bucket")
		private List<Bucket> buckets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "computed-bucket")
		private List<ComputedBucket> computedBuckets;

		/**
		 * Zero-argument constructor.
		 */
		public QueryRange() {
			buckets = new ArrayList<Bucket>();
			computedBuckets = new ArrayList<ComputedBucket>();
		}

		/**
		 * Add a bucket to this QueryRange's buckets.
		 * 
		 * @param bucket
		 *            a Bucket object for use with this QueryRange
		 */
		public void addBucket(Bucket bucket) {
			buckets.add(bucket);
		}

		/**
		 * Add a computed bucket to this QueryRange's buckets.
		 * 
		 * @param computedBucket
		 *            a ComputedBucket object for use with this QueryRange
		 * @return	this QueryRange
		 */
		public QueryRange addComputedBucket(ComputedBucket computedBucket) {
			this.computedBuckets.add(computedBucket);
			return this;
		}

		/**
		 * remove all computed and defined buckets from a RangeOption
		 */
		public void deleteBuckets() {
			this.computedBuckets = new ArrayList<ComputedBucket>();
			this.buckets = new ArrayList<Bucket>();
		}

		/**
		 * Returns the list of buckets for this QueryRange.
		 * @return the bucket list
		 */
		public List<Bucket> getBuckets() {
			return buckets;
		}

		/**
		 * The list of computed buckets for this QueryRange.
		 * @return the bucket list
		 */
		public List<ComputedBucket> getComputedBuckets() {
			return computedBuckets;
		}

		/**
		 * Add a list of buckets to the buckets for this QueryRange.
		 * @param buckets	the bucket list
		 */
		public void addBuckets(List<Buckets> buckets) {
			if (buckets != null) {
				for (Buckets b : buckets) {
					if (buckets.get(0).getClass() == Bucket.class) {
						addBucket((Bucket) b);

					} else {
						addComputedBucket((ComputedBucket) b);
					}
				}
			}
		}
 		
		/**
		 * Add this QueryRange to the tuples query.
		 */
		public void build(QueryTuples tuples) {
			tuples.addRange(this);
		}


		/**
		 * Add this QueryRange to the values query.
		 */
		public void build(QueryValues values) {
			values.setRange(this);
		}

	}

    /**
     * Models a word constraint
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryWord extends BaseConstraintItem implements
			TermSource, ConstraintSource, TermIndexed {

	}

	/**
	 * Models a constraint on collection URIs.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryCollection extends
			FacetableConstraintConfiguration implements
			TupleSource, ConstraintSource {

		/**
		 * This value is removed from collectionOption URIs when creating facet
		 * labels.
		 */
		@XmlAttribute
		private String prefix;

		/**
		 * The prefix to be removed from collectionOption URIs in
		 * generating facet labels.
		 * @return	the prefix
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * Set the collectionOption prefix, returning the modified
		 * CollectionOption object.
		 * 
		 * @param prefix
		 *            The prefix to be removed from collectionOption URIs in
		 *            generating facet labels.
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		/**
		 * Add this QueryCollection to the tuples query.
		 */
		public void build(QueryTuples tuples) {
			tuples.setCollection(this);
		}

		
		/**
		 * Add this QueryCollection to the values query.
		 */
		public void build(QueryValues values) {
			values.setCollection(this);
		}

	}

	/**
	 * Corresponds to the &lt;properties&gt; constraint type in the MarkLogic
	 * Search API
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryProperties extends BaseConstraintItem implements ConstraintSource
			 {

		/**
		 * Zero-argument constructor.
		 */
		public QueryProperties() {
		}

	}

    /**
     * Models an Element Query constraint
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryElementQuery extends BaseConstraintItem implements ConstraintSource {

		@XmlAttribute
		private String name;

		@XmlAttribute
		private String ns;

		/**
		 * Returns the local name of the element.
		 * @return	the name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the namespace URI for the element.
		 * @return	the namespace URI
		 */
		public String getNs() {
			return ns;
		}

		/**
		 * Specifies the local name of the element.
		 * @param name	the local name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Specifies the namespace URI for the element.
		 * @param ns	the namespace URI
		 */
		public void setNs(String ns) {
			this.ns = ns;
		}

	}

    /**
     * Partial implementation of Geospatial constraint types
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	abstract static class QueryGeospatial extends BaseConstraintItem implements ConstraintSource {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "facet-option")
		private List<String> facetOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-option")
		private List<String> geoOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "heatmap")
		private Heatmap heatmap;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "lat")
		private MarkLogicQName latitude;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "lon")
		private MarkLogicQName longitude;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "parent")
		private MarkLogicQName parent;

		/**
		 * Zero-argument constructor.
		 */
		public QueryGeospatial() {
			this.geoOptions = new ArrayList<String>();
			this.facetOptions = new ArrayList<String>();

		}

		/**
		 * Add a facet option to this Geospatial constraint.
		 * 
		 * @param facetOption	the option
		 */
		public void addFacetOption(String facetOption) {
			this.facetOptions.add(facetOption);
		}

		/**
		 * Add a geospatial option to this constraint.
		 * @param geoOption	the option
		 */
		public void addGeoOption(String geoOption) {
			this.geoOptions.add(geoOption);
		}

		/**
		 * List the facet options for the GeoSpatial constraint.
		 * @return	the facet options
		 */
		public List<String> getFacetOptions() {
			return facetOptions;
		}

		/**
		 * Returns the heatmap for this GeoSpatial constraint.
		 * @return	the heatmap
		 */
		public Heatmap getHeatmap() {
			return heatmap;
		}

		/**
		 * Returns the latitude element or attribute as a MarkLogic QName.
		 * @return	the latitude
		 */
		public MarkLogicQName getLatitude() {
			return latitude;
		}

		/**
		 * Returns the longitude element or attribute as a MarkLogic QName.
		 * @return	the longitude
		 */
		public MarkLogicQName getLongitude() {
			return longitude;
		}

		/**
		 * Returns the latitude and longitude container as a MarkLogic QName.
		 * @return	the container
		 */
		public MarkLogicQName getParent() {
			return parent;
		}

		/**
		 * Specifies the geospatial options for this constraint.
		 * @param geoOptions	the option list
		 */
		public void setGeoOptions(List<String> geoOptions) {
			this.geoOptions = geoOptions;
		}

		/**
		 * Specifies the heatmap for this GeoSpatial constraint.
		 * @param heatmap	the heatmap
		 */
		public void setHeatmap(Heatmap heatmap) {
			this.heatmap = heatmap;
		}

		/**
		 * Specifies the latitude element or attribute as a MarkLogic QName.
		 * @param latitude	the latitude
		 */
		public void setLatitude(MarkLogicQName latitude) {
			this.latitude = latitude;
		}

		/**
		 * Specifies the longitude element or attribute as a MarkLogic QName.
		 * @param longitude	the longitude
		 */
		public void setLongitude(MarkLogicQName longitude) {
			this.longitude = longitude;
		}

		/**
		 * Specifies the latitude and longitude container as a MarkLogic QName.
		 * @param parent	the container
		 */
		public void setParent(MarkLogicQName parent) {
			this.parent = parent;
		}

	}
    /**
     * Models a geospatial index with coordinates stored in a pair of attributes
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryGeospatialAttributePair extends QueryGeospatial {

	}

    /**
     * Models a geospatial index with coordinates stored in a single element.
     * 
     * By default coordinates are stored as latitude,longitude points.  To reverse
     * the coordinate order, add the geo-option "long-lat-points" to the 
     * query options configuration.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryGeospatialElement extends QueryGeospatial {

	}

    /**
     * Models a geospatial index with coordinates stored in a pair of elements.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryGeospatialElementPair extends QueryGeospatial {

	}

    /**
     * Models a two-dimensional grid used to categorize data along two dimensions.
     * Use with geographic indexes
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Heatmap {
		@XmlAttribute
		private double e;
		@XmlAttribute
		private int latdivs;
		@XmlAttribute
		private int londivs;
		@XmlAttribute
		private double n;
		@XmlAttribute
		private double s;
		@XmlAttribute
		private double w;

		/**
		 * Returns the east longitude.
		 * @return	the longitude
		 */
		public double getE() {
			return e;
		}

		/**
		 * Returns the number of latitudinal divisions.
		 * @return	the number of divisions
		 */
		public int getLatdivs() {
			return latdivs;
		}

		/**
		 * Returns the number of longitudinal divisions.
		 * @return	the number of divisions
		 */
		public int getLondivs() {
			return londivs;
		}

		/**
		 * Returns the north latitude.
		 * @return	the latitude
		 */
		public double getN() {
			return n;
		}

		/**
		 * Returns the south latitude.
		 * @return	the latitude
		 */
		public double getS() {
			return s;
		}

		/**
		 * Returns the west longitude.
		 * @return	the longitude
		 */
		public double getW() {
			return w;
		}

		/**
		 * Specifies the east longitude.
		 * @param e	the longitude
		 * @return	this heatmap
		 */
		public Heatmap setE(double e) {
			this.e = e;
			return this;
		}

		/**
		 * Specifies the number of latitudinal divisions.
		 * @param latdivs	the number of divisions
		 * @return	this heatmap
		 */
		public Heatmap setLatdivs(int latdivs) {
			this.latdivs = latdivs;
			return this;
		}

		/**
		 * Specifies the number of longitudinal divisions.
		 * @param londivs	the number of divisions
		 * @return	this heatmap
		 */
		public Heatmap setLondivs(int londivs) {
			this.londivs = londivs;
			return this;
		}

		/**
		 * Specifies the north latitude.
		 * @param n	the latitude
		 * @return	this heatmap
		 */
		public Heatmap setN(double n) {
			this.n = n;
			return this;
		}

		/**
		 * Specifies the south latitude.
		 * @param s	the latitude
		 * @return	this heatmap
		 */
		public Heatmap setS(double s) {
			this.s = s;
			return this;
		}

		/**
		 * Specifies the west longitude.
		 * @param w	the longitude
		 * @return	this heatmap
		 */
		public Heatmap setW(double w) {
			this.w = w;
			return this;
		}
	}


    /**
     * Models a searchable expression.  
     * This expression controls the scope over which the search configuration is applied.  Default is an entire database.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QuerySearchableExpression {

		@XmlTransient
		private ExpressionNamespaceBinding[] bindings = null;

		@XmlValue
		private String path = null;

		/**
		 * Construct a QuerySearchableExpression
		 * 
		 */
		public QuerySearchableExpression() {
			
		}
		
        /**
         * Construct a QuerySearchableExpression.
         * @param path The XPath expression.
         * @param bindings Zero or more bindings of prefix to namespace URI, to support the prefixes in the XPath expression.
         */
		public QuerySearchableExpression(String path,
				ExpressionNamespaceBinding... bindings) {
			this.path = path;
			this.bindings = bindings;
		}

        /**
         * Gets the String xPath expression
         * @return the XPath expression, as a string.
         */
		public String getPath() {
			return path;
		}

        /** 
         * Gets the array of namespace to prefix bindings
         * @return the array of namespace bindings.
         */
		public ExpressionNamespaceBinding[] getBindings() {
			return bindings;
		}

	}


	/**
	 * Wraps a cts:query element to be used in addition to other
	 * query configuration options, as an ANDed query.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryAdditionalQuery {

		@XmlAnyElement
		private org.w3c.dom.Element element;

		/**
		 * Construct a QueryAdditionalQuery.
		 */
		public QueryAdditionalQuery() {
		}

		/**
		 * Construct a QueryAdditionalQuery from an existing dom Element.
		 * @param ctsQuery a dom element in the http://marklogic.com/cts namespace
		 */
		public QueryAdditionalQuery(org.w3c.dom.Element ctsQuery) {
			element = ctsQuery;
		}

		/**
		 * Get the element wrapped in this object, as a dom Element.
		 * @return a dom Element representing the additional query.
		 */
		public org.w3c.dom.Element getValue() {
			return element;
		}

	}


    /**
     * Models a Path Index specification.
     *
     * Path Indexes refer to nodes in the database using XPath expressions and a set of namespace bindings.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PathIndex {
		@XmlTransient
		private ExpressionNamespaceBinding[] bindings = null;

		@XmlValue
		private String path = null;

		/**
		 * Zero-argument constructor.
		 */
		public PathIndex() {
		}

		public PathIndex(String path) {
			this.path = path;
		}

		public PathIndex(String path,
				ExpressionNamespaceBinding... bindings) {
			this.path = path;
			this.bindings = bindings;
		}

		public String getPath() {
			return path;
		}

		public ExpressionNamespaceBinding[] getBindings() {
			return bindings;
		}

	}


    /**
     * Models a JSON Key. 
     *
     * JSON keys refer to keys in JSON data structures by name.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class JsonKey implements MetadataExtract {

		@XmlValue
		private String name;

		/**
		 * Zero-argument constructor.
		 */
		public JsonKey() {

		}

		/**
		 * Constructs the JSON key specifier with the key.
		 * @param name	the JSON key
		 */
		public JsonKey(String name) {
			this.name = name;
		}


		/**
		 * Returns the key.
		 * @return	the key
		 */
		public String getName() {
			return name;
		}

		/**
		 * Adds this JSON key to the metadata extraction specification.
		 */
		public void build(QueryExtractMetadata extractMetadata) {
			extractMetadata.getJsonKeys().add(this);
		}

		/**
		 * Adds this JSON key to the values query specification.
		 * @param values	the query
		 */
		public void build(QueryValues values) {
			values.setJsonKey(this);
		}

		/**
		 * Adds this JSON key to the tuples query specification.
		 * @param values	the query
		 */
		public void build(QueryTuples values) {
			values.addJsonKey(this);
		}


	}

    /**
     * Models a field.
     *
     * Fields are a MarkLogic server-side object to refer to sets of elements and attributes.
     * They are referenced by name.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Field implements TupleSource {

		@XmlAttribute
		private String name;

		/**
		 * Zero-argument constructor.
		 */
		public Field() {

		}

		/**
		 * Constructs the field with the name of the field.
		 * @param name	the field
		 */
		public Field(String name) {
			this.name = name;
		}


		/**
		 * Adds this field to the tuples query specification.
		 */
		public void build(QueryTuples tuples) {
			tuples.addField(this);
		}

		/**
		 * Returns the name of the field.
		 * @return	the field
		 */
		public String getName() {
			return name;
		}

		/**
		 * Specifies the name of the field.
		 * @param name	the field
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Adds this field to the indexed specification.
		 * @param indexable	the index specification
		 */
		public void build(Indexed indexable) {
			indexable.setField(this);
		}

		/**
		 * Adds this field to the values query specification.
		 */
		public void build(QueryValues values) {
			values.setField(this);
		}
		
	}

    /**
     * Enumeration of the two possibilities for Fragment scope.
     */
	public enum FragmentScope {
        DOCUMENTS, PROPERTIES;
	}

	
    /**
     * Models specification for extracting document metadata in search results.
     *
     * Can contain ConstraintValue, AttributeValue, ElementValue, or JSonKey
     */ 
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryExtractMetadata {

		@XmlElement(namespace=SEARCH_NS, name="qname")
		private List<AttributeOrElementValue> qnames;


		@XmlElement(namespace=SEARCH_NS, name="constraint-value")
		private List<ConstraintValue> constraintValues;


		@XmlElement(namespace=SEARCH_NS, name="json-key")
		private List<JsonKey> jsonKeys;


		/**
		 * Zero-argument constructor.
		 */
		public QueryExtractMetadata() {
			this.qnames = new ArrayList<AttributeOrElementValue>();
			this.constraintValues = new ArrayList<ConstraintValue>();
			this.jsonKeys = new ArrayList<JsonKey>();

		}

		public void addConstraintReference(ConstraintValue constraintValue) {
			this.constraintValues.add(constraintValue);
		}

		public List<AttributeOrElementValue> getQNames() {
			return this.qnames;
		}

		public List<ConstraintValue> getConstraintValues() {
			return this.constraintValues;
		}

		public List<JsonKey> getJsonKeys() {
			return this.jsonKeys;
		}
	}

    /**
     * Models a QName, as encoded with attributes for several parts of the Search API
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MarkLogicQName {

		@XmlAttribute
		private String name;
		@XmlAttribute
		private String ns;

		public MarkLogicQName() {

		}

		public MarkLogicQName(String ns, String name) {
			if (ns == null) {
				this.ns = "";
			} else {
				this.ns = ns;
			}
			this.name = name;
		}

		public QName asQName() {
			return new QName(getNs(), getName());
		}


		public String getName() {
			return name;
		}

		public String getNs() {
			return ns;
		}


	}

    /**
     * Wraps access to attributes and elements in one superclass.
     *
     * Implements all of the fields and methods in the subclasses.
     * Used with QueryExtractMetadata.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AttributeOrElementValue {

		@XmlAttribute(name = "elem-ns")
		private String elemNs;

		@XmlAttribute(name = "attr-ns")
		private String attrNs;

		@XmlAttribute(name = "elem-name")
		private String elemName;

		@XmlAttribute(name = "attr-name")
		private String attrName;

		public String getElemNs() {
			return elemNs;
		}

		public void setElemNs(String elem_ns) {
			this.elemNs = elem_ns;
		}

		public String getAttrNs() {
			return attrNs;
		}

		public void setAttrNs(String attr_ns) {
			this.attrNs = attr_ns;
		}

		public String getElemName() {
			return elemName;
		}

		public void setElemName(String elem) {
			this.elemName = elem;
		}

		public String getAttrName() {
			return this.attrName;
		}

		public void setAttrName(String attr) {
			this.attrName = attr;
		}

		public void build(QueryExtractMetadata extractMetadata) {
			extractMetadata.getQNames().add(this);
		}
	}

    /**
     * Models access to an element by QName for use in metadata extraction
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class ElementValue extends AttributeOrElementValue implements MetadataExtract {
		
	}
	
    /**
     * Models access to an attribute by QName for use in metadata extraction
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AttributeValue extends AttributeOrElementValue implements MetadataExtract {

	}

    /**
     * Models access to values returned by a constraint, by constraint name.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ConstraintValue implements MetadataExtract {

		public ConstraintValue() {

		}

		public ConstraintValue(String constraintReference) {
			this.ref = constraintReference;
		}

		@XmlAttribute
		private String ref;

		
		public void build(QueryExtractMetadata extractMetadata) {
			extractMetadata.addConstraintReference(this);
		}

		public String getRef() {
			return ref;
		}

		public void setRef(String ref) {
			this.ref = ref;
		}

	}


    /**
     * Models annotations for search API documents
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryAnnotation 
			{

		@XmlAnyElement
		private List<org.w3c.dom.Element> annotations;

		public QueryAnnotation() {
			annotations = new ArrayList<org.w3c.dom.Element>();
		}
		
		public QueryAnnotation(String xmlString) {
			annotations = new ArrayList<org.w3c.dom.Element>();
			annotations.add(Utilities.domElement(xmlString));
		}
		
		public void add(org.w3c.dom.Element value) {
			this.annotations.add(value);
		}

		public org.w3c.dom.Element get(int i) {
			return annotations.get(i);
		}

		public List<org.w3c.dom.Element> getAll() {
			return annotations;
		}

	}		





    /**
     * Models a source for calls to the search:suggest XQuery function.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QuerySuggestionSource implements Annotatable<QuerySuggestionSource>  {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QuerySuggestionSource annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}

		
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "custom")
		private QueryCustom custom;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element-query")
		private QueryElementQuery elementQuery;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attrName-pair")
		private QueryGeospatialAttributePair geoAttrPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elemName")
		private QueryGeospatialElement geoElem;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elemName-pair")
		private QueryGeospatialElementPair geoElemPair;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "properties")
		private QueryProperties properties;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlAttribute
		private String ref;

		@XmlElement(name = "suggestion-option")
		private List<String> suggestionOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word-lexicon")
		private WordLexicon wordLexicon;

		public QuerySuggestionSource() {			
			annotations = new ArrayList<QueryAnnotation>();
			suggestionOptions = new ArrayList<String>();
		}

		
		public void addSuggestionOption(String suggestionOption) {
			this.suggestionOptions.add(suggestionOption);
		}


		public void deleteSuggestionOptions() {
			suggestionOptions = new ArrayList<String>();
		}

		
		@SuppressWarnings("unchecked")
		public <T extends BaseConstraintItem> T getConstraintConfiguration() {
			if (collection != null) {
				return (T) collection;
			} else if (value != null) {
				return (T) value;
			} else if (range != null) {
				return (T) range;
			} else if (word != null) {
				return (T) word;
			} else if (elementQuery != null) {
				return (T) elementQuery;
			} else if (properties != null) {
				return (T) properties;
			} else if (custom != null) {
				return (T) custom;
			} else if (geoElem != null) {
				return (T) geoElem;
			} else if (geoAttrPair != null) {
				return (T) geoAttrPair;
			} else if (geoElemPair != null) {
				return (T) geoElemPair;
			}
			return null;
		}

		public String getRef() {
			return this.ref;
		}

		public List<String> getSuggestionOptions() {
			return suggestionOptions;
		}

		public WordLexicon getWordLexicon() {
			return wordLexicon;
		}

		public <T extends BaseConstraintItem> void setImplementation(
				T constraintDefinition) {
			if (constraintDefinition.getClass() == QueryCollection.class) {
				collection = (QueryCollection) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryValue.class) {
				value = (QueryValue) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryWord.class) {
				word = (QueryWord) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryRange.class) {
				range = (QueryRange) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryElementQuery.class) {
				elementQuery = (QueryElementQuery) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryProperties.class) {
				properties = (QueryProperties) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryCustom.class) {
				custom = (QueryCustom) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryGeospatialElement.class) {
				geoElem = (QueryGeospatialElement) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryGeospatialAttributePair.class) {
				geoAttrPair = (QueryGeospatialAttributePair) constraintDefinition;
			} else if (constraintDefinition.getClass() == QueryGeospatialElementPair.class) {
				geoElemPair = (QueryGeospatialElementPair) constraintDefinition;
			}
		}

		public void setRef(String constraintReference) {
			this.ref = constraintReference;
		}

		public void setWordLexicon(WordLexicon wordLexicon) {
			this.wordLexicon = wordLexicon;
		}
	}
    /**
     * Models the default source for calls to the search:suggest XQuery function.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryDefaultSuggestionSource extends
			QuerySuggestionSource  {

		public QueryDefaultSuggestionSource() {
			super();
		}
	}

    /**
     * Models the word lexicon, as a source of tems for suggestions.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class WordLexicon {

		@XmlAttribute
		private String collation;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "fragment-scope")
		private FragmentScope fragmentScope;

		public String getCollation() {
			return collation;
		}

		public FragmentScope getFragmentScope() {
			return fragmentScope;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public void setFragmentScope(FragmentScope fragmentScope) {
			this.fragmentScope = fragmentScope;
		}
	}

    /**
     * Models an extension to the default grammar for search strings.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class QueryGrammar {

		/**
		 * A Query Joiner combines two terms as part of a query grammar.
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static final class QueryJoiner  {

			/**
			 * A Comparator enumerates the possible binary relations
			 * for constraining an index with a value.
			 */
			public enum Comparator {
				/**
				 * The Equals operation.
				 */
				EQ,
				/**
				 * The Greater-than-or-equal-to operation.
				 */
				GE,
				/**
				 * The Greater-than operation.
				 */
				GT,
				/**
				 * The Less-than-or-equal-to operation.
				 */
				LE,
				/**
				 * The Less-than operation.
				 */
				LT,
				/**
				 * The Not-equals operation.
				 */
				NE;
			}

			/**
			 * A JoinerApply enumerates the built-in functions available 
			 * to join the terms.
			 */
			public enum JoinerApply {

				/**
				 * Constrains the index term with the value term, using the Comparator
				 * if specified.
				 */
				CONSTRAINT,
				/**
				 * Inserts an infix between the terms.
				 */
				INFIX,
				/**
				 * Specifies proximity between the terms.
				 */
				NEAR2,
				/**
				 * Prepends to the terms.
				 */
				PREFIX;

				static JoinerApply fromXMLString(String xmlString) {
					return JoinerApply.valueOf(xmlString.toUpperCase().replace(
							"-", "_"));
				}

				String toXMLString() {
					return this.toString().toLowerCase().replace("_", "-");
				}
			}

			@XmlAttribute
			private String apply;

			@XmlAttribute
			private String compare;

			@XmlAttribute
			private int consume;

			@XmlAttribute
			private String delimiter;
			@XmlAttribute
			private String element;
			@XmlValue
			private String joinerText;
			@XmlAttribute
			private String options;
			@XmlAttribute
			private int strength;
			@XmlAttribute
			private String tokenize;

			public QueryJoiner() {
			}

			public QueryJoiner(String joinerText) {
				this.joinerText = joinerText;
			}

			public JoinerApply getApply() {
				return JoinerApply.valueOf(apply.toUpperCase());
			};

			public Comparator getComparator() {
				return Comparator.valueOf(this.compare);
			}

			public int getConsume() {
				return consume;
			}

			public String getDelimiter() {
				return delimiter;
			}

			public String getElement() {
				return element;
			}

			public String getJoinerText() {
				return joinerText;
			}

			public String getOptions() {
				return options;
			}

			public int getStrength() {
				return strength;
			}

			public Tokenize getTokenize() {
				return Tokenize.valueOf(tokenize.toUpperCase());
			}

			public void setApply(JoinerApply apply) {
				this.apply = apply.toXMLString();
			}

			public void setApply(String apply) {
				this.apply = apply;
			}

			public void setCompare(Comparator compare) {
				this.compare = compare.toString();
			}

			public void setConsume(int consume) {
				this.consume = consume;
			}

			public void setElement(String element) {
				 this.element = element;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}

			public void setTokenize(Tokenize tokenize) {
				this.tokenize = tokenize.toXMLString();
			}


		}

		/**
		 * A Query Starter delimits a term as part of a query grammar.
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public final static class QueryStarter {

			/**
			 * A StarterApply enumerates the built-in functions available 
			 * to start terms.
			 */
			public enum StarterApply {

				/**
				 * Groups the terms
				 */
				GROUPING,
				/**
				 * Prepends to the terms
				 */
				PREFIX;

				static StarterApply fromXMLString(String xmlString) {
					return StarterApply.valueOf(xmlString.toUpperCase()
							.replace("-", "_"));
				}

				String toXMLString() {
					return this.toString().toLowerCase().replace("_", "-");
				}
			}

			@XmlAttribute
			private String apply;
			@XmlAttribute
			private String delimiter;
			@XmlAttribute
			private String element;
			@XmlAttribute
			private String options;
			@XmlValue
			private String starterText;
			@XmlAttribute
			private int strength;

			@XmlAttribute
			private String tokenize;

			public QueryStarter() {

			}

			public QueryStarter(String starterText) {
				this.starterText = starterText;
			}

			public StarterApply getApply() {
				return StarterApply.fromXMLString(apply);
			}

			public String getDelimiter() {
				return delimiter;
			}

			public String getElement() {
				return element;
			}

			public String getOptions() {
				return options;
			}

			public String getStarterText() {
				return starterText;
			}

			public int getStrength() {
				return strength;
			}

			public Tokenize getTokenize() {
				return Tokenize.fromXMLString(tokenize);
			}

			public void setApply(StarterApply apply) {
				this.apply = apply.toXMLString();
			}

			public void setApply(String apply) {
				this.apply = apply;
			}

			public void setDelimiter(String delimiter) {
				this.delimiter = delimiter;
			}

			public void setElement(String element) {
				this.element = element;
			}

			public void setOptions(String options) {
				this.options = options;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}

			public void setStarterText(String starterText) {
				this.starterText = starterText;
			}

			public void setTokenize(String tokenize) {
				this.tokenize = tokenize;
			}

			
		}

		/**
		 * Tokenize enumerates how terms are distinguished for a joiner.
		 */
		public enum Tokenize {

			/**
			 * Uses the default tokenizing strategy for distinguishing terms.
			 */
			DEFAULT,
			/**
			 * Uses word boundaries to distinguish terms.
			 */
			WORD;

			static Tokenize fromXMLString(String xmlString) {
				return Tokenize.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			String toXMLString() {
				return this.toString().toLowerCase().replace("_", "-");
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "implicit")
		private QueryAdditionalQuery implicit;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "joiner")
		private List<QueryJoiner> joiners;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "quotation")
		private String quotation;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "starter")
		private List<QueryStarter> starters;

		public QueryGrammar() {
			joiners = new ArrayList<QueryJoiner>();
			starters = new ArrayList<QueryStarter>();
		}

		public void addJoiner(QueryJoiner joiner) {
			this.joiners.add(joiner);
		}

		public void addStarter(QueryStarter starter) {
			this.starters.add(starter);
		}

		public org.w3c.dom.Element getImplicit() {
			return implicit.getValue();
		}

		public List<QueryJoiner> getJoiners() {
			return joiners;
		}
		
		public void setJoiners(List<QueryJoiner> joiners) {
			this.joiners = joiners;
		}

		public String getQuotation() {
			return quotation;
		}

		public void setStarters(List<QueryStarter> starters) {
			this.starters = starters;
		}
		public List<QueryStarter> getStarters() {
			return starters;
		}

		public void setImplicit(org.w3c.dom.Element implicit) {
			this.implicit = new QueryAdditionalQuery(implicit);
		}

		public void setQuotation(String quotation) {
			this.quotation = quotation;
		}
	}

	/**
	 * Represents how query terms are to be combined.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryOperator  implements Annotatable<QueryOperator> 
			 {

		@XmlAttribute
		private String name;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "state")
		private List<QueryState> states;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QueryOperator annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}
		
		public QueryOperator() {
			annotations = new ArrayList<QueryAnnotation>();
			states = new ArrayList<QueryState>();
		}

		public String getName() {
			return name;
		}

		public List<QueryState> getStates() {
			return states;
		}

		public void addState(QueryState state) {
			states.add(state);
		}
		
		public void setName(String name) {
			this.name = name;
		}

		public QueryState getState(String name) {
			for (QueryState state : getStates()) {
				if (state.getName().equals(name)) {
					return state;
				}
			}
			return null;
		}
	}

    /**
     * Models a named state to be triggered by inclusion of this name in an operator search string
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryState 
			 {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "additional-query")
		private QueryAdditionalQuery additionalQuery;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "debug")
		private Boolean debug;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "forest")
		private List<Long> forests;

		@XmlAttribute(name = "name")
		private String name;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "page-length")
		private Long pageLength;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "quality-weight")
		private Double qualityWeight;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "namespace-bindings")
		private ExpressionNamespaceBindings searchableExpressionBindings;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "searchable-expression")
		private QuerySearchableExpression searchableExpression;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "search-option")
		private List<String> searchOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "sort-order")
		private List<QuerySortOrder> sortOrders;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "transform-results")
		private QueryTransformResults transformResults;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QueryState annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}

		public QueryState() {
			sortOrders = new ArrayList<QuerySortOrder>();
		}

		public void addForest(Long forest) {
			if (forests == null) {
				forests = new ArrayList<Long>();
			}
			this.forests.add(forest);
		}


		public void addSortOrder(QuerySortOrder sortOrder) {
			sortOrders.add(sortOrder);
		}

		public void deleteSortOrders() {
			sortOrders = new ArrayList<QuerySortOrder>();
		}

		public org.w3c.dom.Element getAdditionalQuery() {
			return additionalQuery.getValue();
		}

		public Boolean getDebug() {
			return debug;
		}

		public List<Long> getForests() {
			return forests;
		}

		public String getName() {
			return name;
		}

		public Long getPageLength() {
			return pageLength;
		}

		public Double getQualityWeight() {
			return qualityWeight;
		}

		public QuerySearchableExpression getSearchableExpression() {
			return searchableExpression;
		}

		public NamespaceContext getSearchableExpressionNamespaceBindings() {
			EditableNamespaceContext context = new EditableNamespaceContext();
			for (ExpressionNamespaceBinding binding : searchableExpressionBindings.bindings) {
				String prefix = binding.getPrefix();
				String uri = binding.getNamespaceURI();
				if ("".equals(prefix)) {
					context.setDefaultNamespaceURI(uri);
				} else {
					context.setNamespaceURI(prefix, uri);
				}
			}

			return context;
		}

		public List<String> getSearchOptions() {
			return searchOptions;
		}

		public List<QuerySortOrder> getSortOrders() {
			return sortOrders;
		}

		public QueryTransformResults getTransformResultsOption() {
			return transformResults;
		}

		public QueryState withAdditionalQuery(QueryAdditionalQuery additionalQuery) {
			this.additionalQuery = additionalQuery;
			return this;
		}

		public QueryState withAdditionalQuery(org.w3c.dom.Element element) {
			additionalQuery = new QueryAdditionalQuery(element);
			return this;
		}

		public void setDebug(Boolean debug) {
			this.debug = debug;
		}

		public QueryState withForests(List<Long> forests) {
			this.forests = forests;
			return this;
		}

		public void setName(String name) {
			this.name = name;
		}

		public QueryState withPageLength(Long pageLength) {
			this.pageLength = pageLength;
			return this;
		}

		public QueryState withQualityWeight(Double qualityWeight) {
			this.qualityWeight = qualityWeight;
			return this;
		}

		public QueryState withSearchableExpression(QuerySearchableExpression querySearchableExpression) {
			this.searchableExpression = querySearchableExpression;
			return this;
		}

		public QueryState withSearchOptions(List<String> searchOptions) {
			this.searchOptions = searchOptions;
			return this;
		}

		public QueryState withSortOrders(QuerySortOrder... sortOrders) {
			this.sortOrders = Arrays.asList(sortOrders);
			return this;
		}

		public QueryState withTransformResults(
				QueryTransformResults transformResults) {
			this.transformResults= transformResults;
			return this;
		}
	}

    /**
     * Models criteria for sorting results.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QuerySortOrder  implements Indexed, Annotatable<QuerySortOrder> 
			 {

		/**
		 * Direction enumerates the order for sorting values.
		 */
		public enum Direction  {

			/**
			 * Sorts values in ascending order.
			 */
			ASCENDING,
			/**
			 * Sorts values in descending order.
			 */
			DESCENDING;

			static Direction fromXMLString(String xmlString) {
				return Direction.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			String toXMLString() {
				return this.toString().toLowerCase().replace("_", "-");
			}
		}

		/**
		 * Score enumerates whether or not the relevance score is used.
		 */
		public enum Score  {
			/**
			 * Uses the score.
			 */
			YES;
		};

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "attribute")
		private MarkLogicQName attributeReference;

		@XmlAttribute
		private String collation;

		@XmlAttribute
		private String direction;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private MarkLogicQName elementReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field fieldReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "json-key")
		private JsonKey jsonKey;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "score")
		private String score;

		@XmlAttribute
		private String type;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public QuerySortOrder() {
			annotations = new ArrayList<QueryAnnotation>();
		}
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QuerySortOrder annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}

		
		public QName getAttribute() {
			return attributeReference.asQName();
		}

		public String getCollation() {
			return collation;
		}

		public Direction getDirection() {
			return Direction.fromXMLString(direction);
		}

		public QName getElement() {
			return elementReference.asQName();
		}

		public String getFieldName() {
			return this.fieldReference.getName();
		}

		public String getJsonKey() {
			return this.jsonKey.getName();
		}

		public String getType() {
			return type;
		}

		
		public void setAttribute(MarkLogicQName attribute) {
			this.attributeReference = attribute;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public void setDirection(Direction direction) {
			this.direction = direction.toXMLString();
		}

		public void setElement(MarkLogicQName element) {
			this.elementReference = element;
		}

		public void setField(Field field) {
			this.fieldReference = field;
		}

		public void setPath(PathIndex pathIndex) {
			// TODO: Is this the right thing to do?
			throw new UnsupportedOperationException(
					"Path indexes are not part of sort orders");
		}

		public void setScore() {
			score = "";
		}

		public Score getScore() {
			return (this.score != null && this.score.equals("")) ? Score.YES
					: null;
		}

		public void unsetScore() {
			score = null;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setJsonKey(JsonKey jsonKey) {
			this.jsonKey = jsonKey;
		}

	}



	/**
	 * Models configuration for a search API term.
	 * <p>
	 * If a search term doesn't match a named constraint, it uses Term. You can
	 * thus override default search behavior by changing a QueryOptions term
	 * element.
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryTerm  implements
			TermOptions, Annotatable<QueryTerm> {

		/**
		 * The TermApply enumeration provides the special circumstances of an
		 * empty search string.
		 * 
		 */
		public enum TermApply {

			/**
			 * Treats an empty string as a request for all results.
			 */
			ALL_RESULTS,
			/**
			 * Treats an empty string as a request for no results.
			 */
			NO_RESULTS;

			public static TermApply fromXmlString(String xmlString) {
				return TermApply.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			public String toXmlString() {
				return this.toString().toLowerCase().replace("_", "-");
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "default")
		private DefaultTermSource defaultConstraint;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "empty")
		private XQueryExtension empty;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term-option")
		private List<String> termOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "weight")
		private Double weight;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QueryTerm annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}
		
		private XQueryExtension xQueryExtension;

		public QueryTerm() {
			annotations = new ArrayList<QueryAnnotation>();
		}


		/**
		 * get the constraint definition that backs default term queries.
		 * @return A value, word, or value definition that backs default queries.
		 */
		public <T extends BaseConstraintItem> T getSource() {
			if (defaultConstraint == null) {
				return null;
			}
			else {
				return defaultConstraint.getSource();
			}
		}

		public TermApply getEmptyApply() {
			return TermApply.fromXmlString(empty.getApply());
		}

		public XQueryExtension getTermFunction() {
			return xQueryExtension;
		}
		
		public void setTermFunction(XQueryExtension extension) {
			this.xQueryExtension = extension;
		}

		
		public List<String> getTermOptions() {
			return termOptions;
		}

		
		public Double getWeight() {
			return this.weight;
		}

		/**
		 * Sets the source of data that backs default (unprefixed) search terms.
		 * @param termSource A value, word, or range that backs term queries.
		 */
		public void setSource(TermSource termSource) {
			this.defaultConstraint = new DefaultTermSource();
			this.defaultConstraint.setSource(termSource);
		}
		
		public void setEmptyApply(TermApply termApply) {
			empty = new XQueryExtension();
			empty.setApply(termApply.toXmlString());
		}

		
		public void setTermOptions(List<String> termOptions) {
			this.termOptions = termOptions;
		}

		
		public void setWeight(Double weight) {
			this.weight = weight;
		}

		public void setRef(String defaultSourceName) {
			this.defaultConstraint = new DefaultTermSource();
			defaultConstraint.setRef(defaultSourceName);
		}


		public String getRef() {
			return defaultConstraint.getRef();
		}
	}

    /**
     * The source of data for a default term search.  Used in QueryTerm configurations.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DefaultTermSource implements Annotatable<DefaultTermSource>
			 {
	
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlAttribute(name="ref")
		private String ref;
		
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public DefaultTermSource() {
			annotations = new ArrayList<QueryAnnotation>();
		}
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public DefaultTermSource annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends BaseConstraintItem> T getSource() {
			if (value != null) {
				return (T) value;
			} else if (range != null) {
				return (T) range;
			} else if (word != null) {
				return (T) word;
			}
			return null;
		}

		public void setSource(
				TermSource constraintDefinition) {
			if (constraintDefinition instanceof QueryValue) {
				value = (QueryValue) constraintDefinition;
			} else if (constraintDefinition instanceof QueryWord) {
				word = (QueryWord) constraintDefinition;
			} else if (constraintDefinition instanceof QueryRange) {
				range = (QueryRange) constraintDefinition;
			}
		}

		public void setRef(String defaultSourceName) {
			this.ref = defaultSourceName;
		}

		public String getRef() {
			return ref;
		}
	}

	/**
     * Models configurations that transform search results.
     *
     * Includes pre-configured methods to get raw results, empty results, and to 
     * extract metadata from properties fragments.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryTransformResults  {

		@XmlAttribute
		private String apply;
		@XmlAttribute
		private String at;
		@XmlAttribute
		private String ns;
		
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "per-match-tokens")
		private Integer perMatchTokens;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "max-matches")
		private Integer maxMatches;
		
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "max-snippet-chars")
		private Integer maxSnippetChars;
		
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "preferred-elements")
		private PreferredElements preferredElements;

		public Integer getPerMatchTokens() {
			return perMatchTokens;
		}

		public void setPerMatchTokens(Integer perMatchTokens) {
			this.perMatchTokens = perMatchTokens;
		}

		public Integer getMaxMatches() {
			return maxMatches;
		}

		public void setMaxMatches(Integer maxMatches) {
			this.maxMatches = maxMatches;
		}

		public Integer getMaxSnippetChars() {
			return maxSnippetChars;
		}

		public void setMaxSnippetChars(Integer maxSnippetChars) {
			this.maxSnippetChars = maxSnippetChars;
		}

		
		public QueryTransformResults() {
			
		}

		public void addPreferredElement(MarkLogicQName element) {
			if (preferredElements == null) {
				preferredElements = new PreferredElements();
			}
			preferredElements.addElement(element);
		}

		public String getApply() {
			return apply;
		}

		public String getAt() {
			return at;
		}

		public String getNs() {
			return ns;
		}

		public List<MarkLogicQName> getPreferredElements() {
			return (preferredElements == null) ? null : preferredElements.getElements();
		}

		public void setApply(String apply) {
			this.apply = apply;
		}

		public void setAt(String at) {
			this.at = at;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

		public void setPreferredElements(List<MarkLogicQName> elements) {
			this.preferredElements = new PreferredElements();
			this.preferredElements.setElements(elements);
		}
	}

    /**
     * Models preferred elements.  
     *
     * Used in snippet transforms to prefer inclusion of certain elements over others.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class PreferredElements {
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private List<MarkLogicQName> elements;

		public PreferredElements() {
			elements = new ArrayList<MarkLogicQName>();
		}

		public void addElement(MarkLogicQName element) {
			this.elements.add(element);
		}

		public List<MarkLogicQName> getElements() {
			return elements;
		}

		public void setElements(List<MarkLogicQName> elements) {
			this.elements = elements;
		}

	}

    /**
     * Models configurations that extract co-occurring values from lexicons
     *
     * Use two or more sources to retrieve tuples from the database.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryTuples  implements Annotatable<QueryTuples>  {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "aggregate")
		private Aggregate aggregate;
		@XmlAttribute
		private String name;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "values-option")
		private List<String> valuesOptions;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private List<Field> field;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "uri")
		private String uri;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "json-key")
		private List<JsonKey> jsonKey;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attr-pair")
		private List<QueryGeospatialAttributePair> geoAttrPair;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem")
		private List<QueryGeospatialElement> geoElem;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem-pair")
		private List<QueryGeospatialElementPair> geoElemPair;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private List<QueryRange> range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QueryTuples annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}
		
		public QueryTuples() {
			annotations = new ArrayList<QueryAnnotation>();
			field = new ArrayList<Field>();
			jsonKey = new ArrayList<JsonKey>();
			geoAttrPair = new ArrayList<QueryGeospatialAttributePair>();
			geoElem = new ArrayList<QueryGeospatialElement>();
			geoElemPair = new ArrayList<QueryGeospatialElementPair>();
			range = new ArrayList<QueryRange>();
			valuesOptions = new ArrayList<String>();
		}

		public void addRange(QueryRange queryRange) {
			this.range.add(queryRange);
		}

		public void setCollection(QueryCollection collection) {
			this.collection = collection;
		}

		public List<JsonKey> getJsonKey() {
			return jsonKey;
		}

		public QueryCollection getCollection() {
			return collection;
		}

		public List<QueryGeospatialAttributePair> getGeoAttrPair() {
			return geoAttrPair;
		}

		public List<QueryGeospatialElement> getGeoElem() {
			return geoElem;
		}

		public List<QueryGeospatialElementPair> getGeoElemPair() {
			return geoElemPair;
		}

		public List<QueryRange> getRange() {
			return range;
		}

		public void addJsonKey(JsonKey jsonKey) {
			this.jsonKey.add(jsonKey);
		}

		public void addValuesOption(String valuesOption) {
			this.valuesOptions.add(valuesOption);
		}

		public Aggregate getAggregate() {
			return aggregate;
		}

		public List<Field> getField() {
			return field;
		}

		public String getName() {
			return name;
		}

		public boolean getUri() {
			return uri != null && uri.equals("");
		}

		public List<String> getValuesOptions() {
			return valuesOptions;
		}

		public void setAggregate(Aggregate aggregate) {
			this.aggregate = aggregate;
		}

		public void addField(Field field) {
			this.field.add(field);
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setUri() {
			this.uri = "";
		}

		public void setValuesOptions(List<String> valuesOptions) {
			this.valuesOptions = valuesOptions;
		}
	}


    /**
     * Models configurations that extract values from lexicons
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryValues implements Annotatable<QueryValues>  {

		public JsonKey getJsonKey() {
			return jsonKey;
		}

		public void setRange(QueryRange queryRange) {
			this.range = queryRange;
		}

		public void setCollection(QueryCollection collection) {
			this.collection = collection;
		}

		public QueryCollection getCollection() {
			return collection;
		}

		public QueryGeospatialAttributePair getGeoAttrPair() {
			return geoAttrPair;
		}

		public QueryGeospatialElement getGeoElem() {
			return geoElem;
		}

		public QueryGeospatialElementPair getGeoElemPair() {
			return geoElemPair;
		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "aggregate")
		private Aggregate aggregate;
		@XmlAttribute
		private String name;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "values-option")
		private List<String> valuesOptions;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field field;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "uri")
		private String uri;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "json-key")
		private JsonKey jsonKey;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "collection")
		private QueryCollection collection;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-attr-pair")
		private QueryGeospatialAttributePair geoAttrPair;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem")
		private QueryGeospatialElement geoElem;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "geo-elem-pair")
		private QueryGeospatialElementPair geoElemPair;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;
		
		public List<QueryAnnotation> getAnnotations() {
			return this.annotations;
		}
		
		public QueryValues annotate(String xmlString) {
			this.annotations.add(new QueryAnnotation(xmlString));
			return this;
		}
		
		public QueryValues() {
			annotations = new ArrayList<QueryAnnotation>();
			this.valuesOptions = new ArrayList<String>();
		}

		public void setJsonKey(JsonKey jsonKey) {
			this.jsonKey = jsonKey;
		}

		public String getJsonKeyName() {
			return this.jsonKey.getName();
		}

		public QueryValues(String name) {
			this();
			this.name = name;
		}

		public void addValuesOption(String valuesOption) {
			this.valuesOptions.add(valuesOption);
		}

		public Aggregate getAggregate() {
			return aggregate;
		}

		public Field getField() {
			return field;
		}

		public String getName() {
			return name;
		}

		public String getUri() {
			return uri;
		}

		public List<String> getValuesOptions() {
			return valuesOptions;
		}

		public void setAggregate(Aggregate aggregate) {
			this.aggregate = aggregate;
		}

		public void setField(Field field) {
			this.field = field;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setUri() {
			this.uri = "";
		}

		public void setValuesOptions(List<String> valuesOptions) {
			this.valuesOptions = valuesOptions;
		}

		public QueryRange getRange() {
			return range;
		}

	}

	/**
	 * Corresponds to aggregate element in Search API configuration. Configures
	 * inclusion of aggregate function in call to a values endpoint.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Aggregate 
			 {

		@XmlAttribute
		private String apply;
		@XmlAttribute
		private String udf;

		/**
		 * Gets the apply function.
		 * @return A string corresponding to a built-in aggregate
		 * function, or to a user-defined function.
		 */
		public String getApply() {
			return apply;
		}

		/**
		 * Gets a user-defined function plugin
		 * @return The name of a user-defined plugin.  There is no guarantee that
		 * this plugin actually exists.
		 */
		public String getUdf() {
			return udf;
		}

		/**
		 * Sets the aggregate function name.
		 * @param apply The function name
		 */
		public void setApply(String apply) {
			this.apply = apply;
		}

		/**
		 * Sets the user-defined function plugin.
		 * @param udf The UDF plugin name.
		 */
		public void setUdf(String udf) {
			this.udf = udf;
		}
	}


    /**
     * A list of ExpressionNamespaceBinding objects.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ExpressionNamespaceBindings {
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "binding")
		private List<ExpressionNamespaceBinding> bindings;

		public ExpressionNamespaceBindings() {
			bindings = new ArrayList<ExpressionNamespaceBinding>();
		}

		public void addBinding(String prefix, String uri) {
			bindings.add(new ExpressionNamespaceBinding(prefix, uri));
		}

		public ExpressionNamespaceBinding[] toArray() {
			return bindings.toArray(new ExpressionNamespaceBinding[] {});
		}
	}

    /**
     * An encapsulation of a prefix and a namespace URI for use in xpath expression evaluation.
     */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ExpressionNamespaceBinding {
		@XmlAttribute(name = "prefix")
		String prefix;

		@XmlAttribute(name = "namespace-uri")
		String uri;

		public ExpressionNamespaceBinding() {
		}

		public ExpressionNamespaceBinding(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}

		public String getPrefix() {
			return prefix;
		}

		public String getNamespaceURI() {
			return uri;
		}
	}

	/**
	 * Models a method to locate XQuery functions with use of "ns", "apply"
	 * and "at" attributes.
     *
     * 'at' is the location of the module in the application server path.
     * 'ns' is the namespace uri of the module
     * 'apply' is the local name of the function to evaluate.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class XQueryExtension {

		/**
		 * Denotes a function within the XQuery module specified by "ns" and
		 * "at"
		 */
		@XmlAttribute
		private String apply;
		/**
		 * Where to find the XQuery module on the filesystem.
		 */
		@XmlAttribute
		private String at;
		/**
		 * The namespace URI of an XQuery module.
		 */
		@XmlAttribute
		private String ns;

		public XQueryExtension() {

		}

		public XQueryExtension(String apply, String ns, String at) {
			this.setApply(apply);
			this.setAt(at);
			this.setNs(ns);
		}

		public String getApply() {
			return apply;
		}

		public String getAt() {
			return at;
		}

		public String getNs() {
			return ns;
		}

		public void setApply(String apply) {
			this.apply = apply;
		}

		public void setAt(String at) {
			this.at = at;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

	}

	public static final String SEARCH_NS = "http://marklogic.com/appservices/search";

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "additional-query")
	private QueryAdditionalQuery additionalQuery;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
	private List<QueryAnnotation> annotations;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "concurrency-level")
	private Integer concurrencyLevel;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "debug")
	private Boolean debug;
	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "default-suggestion-source")
	private QueryDefaultSuggestionSource defaultSuggestionSource;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "forest")
	private List<Long> forests;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "fragment-scope")
	private String fragmentScope;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "grammar")
	private QueryGrammar grammarOption;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "operator")
	private List<QueryOperator> operators;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "page-length")
	private Long pageLength;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "quality-weight")
	private Double qualityWeight;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "constraint")
	private List<QueryConstraint> queryConstraints;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "values")
	private List<QueryValues> queryValues;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "tuples")
	private List<QueryTuples> queryTuples;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-aggregates")
	private Boolean returnAggregates;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-constraints")
	private Boolean returnConstraints;

	// Boolean options
	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-facets")
	private Boolean returnFacets;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-frequencies")
	private Boolean returnFrequencies;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-metrics")
	private Boolean returnMetrics;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-plan")
	private Boolean returnPlan;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-qtext")
	private Boolean returnQtext;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-query")
	private Boolean returnQuery;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-results")
	private Boolean returnResults;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-similar")
	private Boolean returnSimilar;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-values")
	private Boolean returnValues;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "namespace-bindings")
	private ExpressionNamespaceBindings searchableExpressionBindings;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "searchable-expression")
	private QuerySearchableExpression searchableExpression;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "search-option")
	private List<String> searchOptions;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "sort-order")
	private List<QuerySortOrder> sortOrders;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "suggestion-source")
	private List<QuerySuggestionSource> suggestionSources;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term")
	private QueryTerm termConfig;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "transform-results")
	private QueryTransformResults transformResultsOption;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "extract-metadata")
	private QueryExtractMetadata extractMetadata;

	public QueryOptions() {
		// options that can have more than one cardinality
		// are in lists.
		queryConstraints = new ArrayList<QueryConstraint>();
		operators = new ArrayList<QueryOperator>();
		sortOrders = new ArrayList<QuerySortOrder>();
		suggestionSources = new ArrayList<QuerySuggestionSource>();
		forests = new ArrayList<Long>();
		searchOptions = new ArrayList<String>();
		annotations = new ArrayList<QueryAnnotation>();
		queryValues = new ArrayList<QueryValues>();
		queryTuples = new ArrayList<QueryTuples>();
	}

	public void setSearchableExpressionNamespaceContext(
			ExpressionNamespaceBinding[] bindingsArray) {
		ExpressionNamespaceBindings bindings = new ExpressionNamespaceBindings();
		for (ExpressionNamespaceBinding binding : bindingsArray) {
			bindings.addBinding(binding.getPrefix(), binding.getNamespaceURI());
		}
		searchableExpressionBindings = bindings;
	}

	public void addForest(Long forest) {
		this.forests.add(forest);
	}

	public void addSearchOption(String searchOption) {
		this.searchOptions.add(searchOption);
	}

	public org.w3c.dom.Element getAdditionalQuery() {
		return additionalQuery.getValue();
	}

	
	public List<QueryAnnotation> getAnnotations() {
		return annotations;
	}
	
	public QueryOptions annotate(String xmlAnnotation) {
		annotations.add(new QueryAnnotation(xmlAnnotation));
		return this;
	}

	public Integer getConcurrencyLevel() {
		return concurrencyLevel;
	}

	public Boolean getDebug() {
		return debug;
	}

	public QueryDefaultSuggestionSource getDefaultSuggestionSource() {
		return defaultSuggestionSource;
	}

	public QueryExtractMetadata getExtractMetadata() {
		return extractMetadata;
	}

	public List<Long> getForests() {
		return forests;
	}

	public String getFragmentScope() {
		return fragmentScope;
	}

	public QueryGrammar getGrammar() {
		return grammarOption;
	}

	public Long getPageLength() {
		return pageLength;
	}

	public Double getQualityWeight() {
		return qualityWeight;
	}

	public List<QueryConstraint> getQueryConstraints() {
		if (queryConstraints == null) {
			return new ArrayList<QueryConstraint>();
		} else {
			return queryConstraints;
		}
	}

	public List<QueryOperator> getQueryOperators() {
		return operators;
	}

	public List<QueryValues> getQueryValues() {
		return this.queryValues;
	}

	public List<QueryTuples> getQueryTuples() {
		return this.queryTuples;
	}

	public Boolean getReturnAggregates() {
		return returnAggregates;
	}

	public Boolean getReturnConstraints() {
		return returnConstraints;
	}

	public Boolean getReturnFacets() {
		return returnFacets;
	}

	public Boolean getReturnFrequencies() {
		return returnFrequencies;
	}

	public Boolean getReturnMetrics() {
		return returnMetrics;
	}

	public Boolean getReturnPlan() {
		return returnPlan;
	}

	public Boolean getReturnQtext() {
		return returnQtext;
	}

	public Boolean getReturnQuery() {
		return returnQuery;
	}

	public Boolean getReturnResults() {
		return returnResults;
	}

	public Boolean getReturnSimilar() {
		return returnSimilar;
	}

	public Boolean getReturnValues() {
		return returnValues;
	}

	public EditableNamespaceContext getSearchableExpressionNamespaceContext() {
		EditableNamespaceContext context = new EditableNamespaceContext();
		if (searchableExpressionBindings != null
				&& searchableExpressionBindings.bindings != null) {
			for (ExpressionNamespaceBinding binding : searchableExpressionBindings.bindings) {
				String prefix = binding.getPrefix();
				String uri = binding.getNamespaceURI();
				if ("".equals(prefix)) {
					context.setDefaultNamespaceURI(uri);
				} else {
					context.setNamespaceURI(prefix, uri);
				}
			}
		}

		return context;
	}

	public String getSearchableExpression() {
		return searchableExpression.getPath();
	}

	public List<String> getSearchOptions() {
		return searchOptions;
	}

	public List<QuerySortOrder> getSortOrders() {
		return sortOrders;
	}

	public List<QuerySuggestionSource> getSuggestionSources() {
		return suggestionSources;
	}

	public QueryTerm getTerm() {
		return termConfig;
	}

	public QueryTransformResults getTransformResults() {
		return transformResultsOption;
	}

	public void setAdditionalQuery(org.w3c.dom.Element additionalQuery) {
		this.additionalQuery = new QueryAdditionalQuery(additionalQuery);
	}

	public void setConcurrencyLevel(Integer concurrencyLevel) {
		this.concurrencyLevel = concurrencyLevel;
	}

	public void setDebug(Boolean debug) {
		this.debug = debug;
	}

	public void setDefaultSuggestionSource(QueryDefaultSuggestionSource dss) {
		this.defaultSuggestionSource = dss;
	}

	public void setExtractMetadata(QueryExtractMetadata extractMetadata) {
		this.extractMetadata = extractMetadata;
	}

	public void setForests(List<Long> forests) {
		this.forests = new ArrayList<Long>();
		if (forests != null) this.forests.addAll(forests);
	}

	public void setFragmentScope(FragmentScope fragmentScope) {
		if (fragmentScope == null) {
			this.fragmentScope = null;
		} else {

			this.fragmentScope = fragmentScope.toString().toLowerCase();
		}
	}

	public void setGrammar(QueryGrammar grammarOption) {
		this.grammarOption = grammarOption;
	}

	public void setOperators(List<QueryOperator> operatorOptions) {
		this.operators = operatorOptions;
	}

	public void setPageLength(Long pageLength) {
		this.pageLength = pageLength;
	}

	public void setQualityWeight(Double qualityWeight) {
		this.qualityWeight = qualityWeight;
	}

	public void setQueryValues(List<QueryValues> values) {
		this.queryValues = values;
	}

	public void setQueryTuples(List<QueryTuples> tuples) {
		this.queryTuples = tuples;
	}

	public void setReturnAggregates(Boolean returnAggregates) {
		this.returnAggregates = returnAggregates;
	}

	public void setReturnConstraints(Boolean returnConstraints) {
		this.returnConstraints = returnConstraints;
	}

	public void setReturnFacets(Boolean returnFacets) {
		this.returnFacets = returnFacets;
	}

	public void setReturnFrequencies(Boolean returnFrequencies) {
		this.returnFrequencies = returnFrequencies;
	}

	public void setReturnMetrics(Boolean returnMetrics) {
		this.returnMetrics = returnMetrics;
	}

	public void setReturnPlan(Boolean returnPlan) {
		this.returnPlan = returnPlan;
	}

	public void setReturnQtext(Boolean returnQtext) {
		this.returnQtext = returnQtext;
	}

	public void setReturnQuery(Boolean returnQuery) {
		this.returnQuery = returnQuery;
	}

	public void setReturnResults(Boolean returnResults) {
		this.returnResults = returnResults;
	}

	public void setReturnSimilar(Boolean returnSimilar) {
		this.returnSimilar = returnSimilar;
	}

	public void setReturnValues(Boolean returnValues) {
		this.returnValues = returnValues;
	}

	public void setSearchableExpression(
			QuerySearchableExpression searchableExpression) {
		this.searchableExpression = searchableExpression;
		setSearchableExpressionNamespaceContext(this.searchableExpression.getBindings());
	}

	public void setSearchableExpressionNamespaceContext(
			EditableNamespaceContext context) {
		ExpressionNamespaceBindings bindings = new ExpressionNamespaceBindings();
		for (String pfx : context.getAllPrefixes()) {
			String uri = context.getNamespaceURI(pfx);
			bindings.addBinding(pfx, uri);
		}
		searchableExpressionBindings = bindings;
	}

	public void setSearchOptions(List<String> searchOptions) {
		this.searchOptions = new ArrayList<String>();
		if (searchOptions != null)		
			this.searchOptions.addAll(searchOptions);
	}

	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		this.sortOrders = new ArrayList<QuerySortOrder>();
		this.sortOrders.addAll(sortOrders);
	}

	public void setSuggestionSources(
			List<QuerySuggestionSource> suggestionSourceOptions) {
		this.suggestionSources = suggestionSourceOptions;
	}

	public void setTerm(QueryTerm termConfig) {
		this.termConfig = termConfig;
	}

	public void setTransformResults(QueryTransformResults transformResultsOption) {
		this.transformResultsOption = transformResultsOption;
	}

	/*
	 * Patch the POJO so that the QueryOptionsTransformInjectNS transform will
	 * insert the proper bindings into the XML during serialization.
	 */
	public void patchBindings() {
		for (QueryConstraint constraint : queryConstraints) {
			if (constraint.range != null) {
				QueryRange range = constraint.range;
				PathIndex index = range.getPathIndex();
				if (index != null && index.bindings != null) {
					ExpressionNamespaceBindings bindings = new ExpressionNamespaceBindings();
					for (ExpressionNamespaceBinding binding : index.bindings) {
						bindings.addBinding(binding.getPrefix(),
								binding.getNamespaceURI());
					}
					range.setPathIndexNamespaceBindings(bindings);
				}
			}
		}
	}


    /**
     * Sets the list of constraints, replacing existing ones.
     * @param constraints the constraints
     */
	public void setConstraints(List<QueryConstraint> constraints) {
		this.queryConstraints = new ArrayList<QueryConstraint>();
		this.queryConstraints.addAll(constraints);
	}

}

