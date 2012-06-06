package com.marklogic.client.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.marklogic.client.config.QueryOptionsBuilder.IndexReference;
import com.marklogic.client.config.QueryOptionsBuilder.QueryAnnotations;
import com.marklogic.client.config.QueryOptionsBuilder.QueryOptionsItem;
import com.marklogic.client.config.QueryOptionsBuilder.QueryRangeItem;
import com.marklogic.client.config.QueryOptionsBuilder.QueryStateItem;
import com.marklogic.client.config.QueryOptionsBuilder.QuerySuggestionSourceItem;
import com.marklogic.client.config.QueryOptionsBuilder.QueryTermItem;
import com.marklogic.client.config.QueryOptionsBuilder.QueryValueItem;
import com.marklogic.client.config.QueryOptionsBuilder.QueryValuesItem;
import com.marklogic.client.config.QueryOptionsBuilder.QueryWordItem;

/**
 * Models MarkLogic Search API Configurations.
 * <p>
 * Use a {@link com.marklogic.client.io.QueryOptionsHandle} as the top-level
 * interface to QueryOptions. The classes contained within QueryOptions
 * implement the low-level XML binding to the MarkLogic Search API, along with
 * accessor methods to all configurable options.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = QueryOptions.SEARCH_NS, name = "options")
public final class QueryOptions implements QueryAnnotations {

	/**
	 * Corresponds to aggregate element in Search API configuration. Configures
	 * inclusion of aggregate function in call to a values endpoint.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Aggregate implements
			QueryOptionsBuilder.QueryValuesItem {

		/**
		 * A string corresponding to a fixed list of built-in aggregate
		 * functions. TODO reference docs elsewhere.
		 */
		@XmlAttribute
		private String apply;
		@XmlAttribute
		private String udf;

		@Override
		public void build(QueryValues values) {
			values.setAggregate(this);
		}

		public String getApply() {
			return apply;
		}

		public String getUdf() {
			return udf;
		}

		public void setApply(String apply) {
			this.apply = apply;
		}

		public void setUdf(String udf) {
			this.udf = udf;
		}
	}

	/**
	 * Wraps any element, for those places in the Search API schema where any
	 * XML element may be used.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class AnyElement implements QueryOptionsItem {

		@XmlAnyElement
		private org.w3c.dom.Element element;

		public AnyElement() {
		}

		public AnyElement(String wrapperName, org.w3c.dom.Element ctsQuery) {
			org.w3c.dom.Element wrapperElement = QueryOptionsBuilder.domElement("<search:"+wrapperName+" xmlns:search=\"http://marklogic.com/appservices/search\"/>");
			Node n = wrapperElement.getOwnerDocument().importNode(ctsQuery,  true);
			wrapperElement.appendChild(n);
			element = wrapperElement;
		}

		public AnyElement(org.w3c.dom.Element ctsQuery) {
			element = ctsQuery;
		}

		@Override
		public void build(QueryOptions options) {
			if (element.getLocalName().equals("searchable-expression")) {
				options.setSearchableExpression(element);
			} else if (element.getLocalName().equals("additional-query")) {
				options.setAdditionalQuery((org.w3c.dom.Element) element.getFirstChild());
			} else {
				logger.error(
						"Unhandled element caught while building options: {}",
						element.getLocalName());
			}
		}

		public org.w3c.dom.Element getValue() {
			return element;
		}

	}

	public static class Attribute extends MarkLogicQName implements
			QueryWordItem, QueryRangeItem, QueryValueItem {

		public Attribute() {

		}

		public Attribute(String ns, String name) {
			super(ns, name);
		}

		@Override
		protected void innerBuild(QueryOptionsBuilder.Indexable indexable) {
			indexable.setAttribute(this);
		}

	}

	/**
	 * Each constraint in the MarkLogic Search API is of a certain type. This
	 * class is the root of the class hierarchy of Range, WordOption,
	 * ValueOption, etc. Note: It contains convenience methods for helping with
	 * index definitions, which are not applicable to all constraint types.
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static abstract class BaseConstraintItem extends
			BaseQueryOptionConfiguration implements
			QueryOptionsBuilder.QuerySuggestionSourceItem,
			QueryOptionsBuilder.QueryConstraintItem {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "attribute")
		private MarkLogicQName attributeReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private MarkLogicQName elementReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field fieldReference;

        @XmlElement(namespace = QueryOptions.SEARCH_NS, name = "path-index")
        private PathIndex pathIndexReference;

		@XmlElement(name = "fragment-scope")
		private FragmentScope fragmentScope;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term-option")
		private List<String> termOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "weight")
		private Double weight = null;

		public BaseConstraintItem() {
			this.termOptions = new ArrayList<String>();
		}

		public void addTermOption(String termOption) {
			this.termOptions.add(termOption);
		}

		@Override
		public void build(QuerySuggestionSource suggestionSource) {
			suggestionSource.setImplementation(this);
		}

		public void build(QueryValues values) {
			values.setSource(this);
		}

		public QName getAttribute() {
			return attributeReference.asQName();
		}

		public QName getElement() {
			return elementReference.asQName();
		}

        public String getPath() {
            return pathIndexReference.getPath();
        }

		public String getFieldName() {
			return this.fieldReference.getName();
		}

		public FragmentScope getFragmentScope() {
			return fragmentScope;
		}

		public List<String> getTermOptions() {
			return termOptions;
		}

		public Double getWeight() {
			return this.weight;
		}

		public void setAttribute(Attribute attribute) {
			this.attributeReference = attribute;
		}

		/**
		 * Add a reference to an element to this ConstraintBase
		 */
		public void setElement(Element element) {
			this.elementReference = element;
		}

		public void setField(Field field) {
			this.fieldReference = field;
		}

        public void setPath(PathIndex pathIndex) {
            this.pathIndexReference = pathIndex;
        }

		public void setFragmentScope(FragmentScope fragmentScope) {
			this.fragmentScope = fragmentScope;
		}

		public void setTermOptions(List<String> termOptions) {
			this.termOptions = termOptions;
		}

		public void setWeight(Double weight) {
			this.weight = weight;
		}
	}

	public abstract static class BaseQueryOptionConfiguration {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "annotation", required = false)
		private List<QueryAnnotation> annotations;

		BaseQueryOptionConfiguration() {
			this.annotations = new ArrayList<QueryAnnotation>();
		}

		protected void addElementAsAnnotation(org.w3c.dom.Element element) {
			QueryAnnotation annotation = new QueryAnnotation();
			annotation.add(element);
			annotations.add(annotation);
		}

		protected void deleteAnnotations() {
			annotations = new ArrayList<QueryAnnotation>();
		}

		protected List<QueryAnnotation> getAnnotations() {
			return annotations;
		}

	}

	public static class Element extends MarkLogicQName {
		public Element() {
		}

		public Element(String ns, String name) {
			super(ns, name);
		}

		@Override
		protected void innerBuild(QueryOptionsBuilder.Indexable indexable) {
			indexable.setElement(this);
		}

	}

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PathIndex implements QueryRangeItem {
        private static String[] bindings = null;

        @XmlValue
        private String path = null;

        public PathIndex() {

        }

        public PathIndex(String path) {
            this.path = path;
        }

        public PathIndex(String path, String... bindings) {
            this.path = path;
            this.bindings = bindings;
        }

        public String getPath() {
            return path;
        }

        public String[] getBindings() {
            return bindings;
        }

        @Override
        public void build(QueryRange range) {
            range.setPath(this);
        }
    }

	@XmlAccessorType(XmlAccessType.FIELD)
	public abstract static class FacetableConstraintConfiguration extends
			BaseConstraintItem {

		@XmlAttribute
		private String collation;

		@XmlAttribute(name = "facet")
		private boolean doFacets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "facet-option")
		private List<String> facetOptions;

		@XmlAttribute
		private QName type;

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
		 *            set to true to configure facets, false otherwise.
		 */
		public void doFacets(boolean doFacets) {
			this.doFacets = doFacets;
		}

		public String getCollation() {
			return collation;
		}

		/**
		 * @return true if this constraint is configured for facets. False
		 *         otherwise.
		 */
		public boolean getDoFacets() {
			return doFacets;
		}

		/**
		 * get the list of facet options.
		 * 
		 * @return
		 */
		public List<String> getFacetOptions() {
			return facetOptions;
		}

		public QName getType() {
			return type;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public void setDoFacets(boolean doFacets) {
			this.doFacets = doFacets;
		}

		public void setFacetOptions(List<String> facetOptions) {
			this.facetOptions = new ArrayList<String>();
			for (String option : facetOptions) {
				this.facetOptions.add(option);
			}
		}

		public void setType(QName type) {
			this.type = type;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Field implements QueryOptionsBuilder.QueryWordItem,
			QueryOptionsBuilder.QueryRangeItem,
			QueryOptionsBuilder.QueryValueItem,
			QueryOptionsBuilder.QueryValuesItem, IndexReference {

		@XmlAttribute
		private String name;

		public Field() {

		}

		public Field(String name) {
			this.name = name;
		}

		@Override
		public void build(QueryRange range) {
			this.innerBuild(range);

		}

		@Override
		public void build(QuerySortOrder sortOrder) {
			this.innerBuild(sortOrder);
		}

		@Override
		public void build(QueryValue value) {
			this.innerBuild(value);
		}

		@Override
		public void build(QueryValues values) {
			values.setField(this);
		}

		@Override
		public void build(QueryWord word) {
			this.innerBuild(word);

		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private void innerBuild(QueryOptionsBuilder.Indexable indexable) {
			indexable.setField(this);
		}

	}

    public enum FragmentScope {
        DOCUMENTS, PROPERTIES;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
	public static class Heatmap implements
			QueryOptionsBuilder.QueryGeospatialItem {

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

		@Override
		public void build(QueryGeospatial geospatial) {
			geospatial.setHeatmap(this);
		}

		public double getE() {
			return e;
		}

		public int getLatdivs() {
			return latdivs;
		}

		public int getLondivs() {
			return londivs;
		}

		public double getN() {
			return n;
		}

		public double getS() {
			return s;
		}

		public double getW() {
			return w;
		}

		public Heatmap setE(double e) {
			this.e = e;
			return this;
		}

		public Heatmap setLatdivs(int latdivs) {
			this.latdivs = latdivs;
			return this;
		}

		public Heatmap setLondivs(int londivs) {
			this.londivs = londivs;
			return this;
		}

		public Heatmap setN(double n) {
			this.n = n;
			return this;
		}

		public Heatmap setS(double s) {
			this.s = s;
			return this;
		}

		public Heatmap setW(double w) {
			this.w = w;
			return this;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MarkLogicQName implements IndexReference {

		@XmlAttribute
		private String name;
		@XmlAttribute
		private String ns;

		public MarkLogicQName() {

		}

		public MarkLogicQName(String ns, String name) {
			this.ns = ns;
			this.name = name;
		}

		public QName asQName() {
			return new QName(getNs(), getName());
		}

		@Override
		public void build(QueryRange range) {
			this.innerBuild(range);
		}

		@Override
		public void build(QuerySortOrder sortOrder) {
			this.innerBuild(sortOrder);
		}

		@Override
		public void build(QueryValue value) {
			this.innerBuild(value);
		}

		@Override
		public void build(QueryWord word) {
			this.innerBuild(word);
		}

		public String getName() {
			return name;
		}

		public String getNs() {
			return ns;
		}

		protected void innerBuild(QueryOptionsBuilder.Indexable indexable) {
			// need to override, JAXB sez no abstract classes.
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class PreferredElements {
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private List<Element> elements;

		public PreferredElements() {
			elements = new ArrayList<Element>();
		}

		public void addElement(Element element) {
			this.elements.add(element);
		}

		public List<Element> getElements() {
			return elements;
		}

		public void setElements(List<Element> elements) {
			this.elements = elements;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryAnnotation implements
			QueryOptionsBuilder.QueryStateItem,
			QueryOptionsBuilder.QueryOptionsItem,
			QueryOptionsBuilder.QueryCustomItem {

		@XmlAnyElement
		private List<org.w3c.dom.Element> annotations;

		public QueryAnnotation() {
			annotations = new ArrayList<org.w3c.dom.Element>();
		}

		public void add(org.w3c.dom.Element value) {
			this.annotations.add(value);
		}

		@Override
		public void build(QueryCustom custom) {
			custom.addAnnotation(this);
		}

		@Override
		public void build(QueryOptions options) {
			options.addAnnotation(this);
		}

		public org.w3c.dom.Element get(int i) {
			return annotations.get(i);
		}

		public List<org.w3c.dom.Element> getAll() {
			return annotations;
		}

	}

	/**
	 * Models a constraint on collectionOption URI.
	 * 
	 */
	public static class QueryCollection extends
			FacetableConstraintConfiguration implements
			QueryOptionsBuilder.QueryConstraintItem,
			QueryOptionsBuilder.QueryValuesItem {

		/**
		 * This value is removed from collectionOption URIs when creating facet
		 * labels.
		 */
		@XmlAttribute
		private String prefix;

		/**
		 * 
		 * @return The prefix to be removed from collectionOption URIs in
		 *         generating facet labels.
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

	}

	/**
	 * Models a constraint node in Search API configuration.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryConstraint extends BaseQueryOptionConfiguration
			implements QueryOptionsBuilder.QueryOptionsItem,
			QueryOptionsBuilder.QueryAnnotations {

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

		public QueryConstraint() {
			super();
		}

		public QueryConstraint(String name) {
			this();
			setName(name);
		}

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void build(QueryOptions options) {
			options.getQueryConstraints().add(this);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		public String getName() {
			return name;
		}

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

		public void setName(String name) {
			this.name = name;
		}

		public void setSource(BaseConstraintItem constraintDefinition) {
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

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryConstraintConfigurationBag extends
			BaseQueryOptionConfiguration {
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

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "properties")
		private QueryProperties properties;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "range")
		private QueryRange range;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "value")
		private QueryValue value;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "word")
		private QueryWord word;

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
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryCustom extends FacetableConstraintConfiguration
			implements QueryOptionsBuilder.QueryAnnotations,
			QueryOptionsBuilder.QueryConstraintItem {

		public static class FinishFacet extends XQueryExtension implements
				QueryOptionsBuilder.QueryCustomItem {

			public FinishFacet() {
			}

			public FinishFacet(String apply, String ns, String at) {
				super(apply, ns, at);
			}

			@Override
			public void build(QueryCustom custom) {
				custom.setFinishFacet(this);
			}
		}

		public static class Parse extends XQueryExtension implements
				QueryOptionsBuilder.QueryCustomItem {

			public Parse() {
			}

			public Parse(String apply, String ns, String at) {
				super(apply, ns, at);
			}

			@Override
			public void build(QueryCustom custom) {
				custom.setParse(this);
			}
		}

		public static class StartFacet extends XQueryExtension implements
				QueryOptionsBuilder.QueryCustomItem {

			public StartFacet() {
			}

			public StartFacet(String apply, String ns, String at) {
				super(apply, ns, at);
			}

			@Override
			public void build(QueryCustom custom) {
				custom.setStartFacet(this);
			}
		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "finish-facet")
		private FinishFacet finishFacet;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "parse")
		private Parse parse;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "start-facet")
		private StartFacet startFacet;

		public QueryCustom(boolean doFacets) {
			this.doFacets(doFacets);
		}

		public QueryCustom(boolean doFacets, Parse parseExtension) {
			this.doFacets(doFacets);
			this.parse = parseExtension;
		}

		QueryCustom() {
		}

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		public XQueryExtension getFinishFacet() {
			return finishFacet;
		}

		public XQueryExtension getParse() {
			return parse;
		}

		public XQueryExtension getStartFacet() {
			return startFacet;
		}

		public void setFinishFacet(FinishFacet finishFacet) {
			this.finishFacet = finishFacet;
		}

		public void setParse(Parse parse) {
			this.parse = parse;
		}

		public void setStartFacet(StartFacet startFacet) {
			this.startFacet = startFacet;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryDefaultSuggestionSource extends
			QuerySuggestionSource implements
			QueryOptionsBuilder.QueryOptionsItem {

		public QueryDefaultSuggestionSource() {
			super();
		}

		@Override
		public void build(QueryOptions options) {
			options.setDefaultSuggestionSource(this);
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryElementQuery extends BaseConstraintItem implements
			QueryOptionsBuilder.QueryConstraintItem {

		@XmlAttribute
		private String name;

		@XmlAttribute
		private String ns;

		public String getName() {
			return name;
		}

		public String getNs() {
			return ns;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setNs(String ns) {
			this.ns = ns;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryGeospatialAttributePair extends QueryGeospatial {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryGeospatialElement extends QueryGeospatial {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryGeospatialElementPair extends QueryGeospatial {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class QueryGrammar implements
			QueryOptionsBuilder.QueryOptionsItem {

		@XmlAccessorType(XmlAccessType.FIELD)
		public static final class QueryJoiner implements
				QueryOptionsBuilder.QueryGrammarItem {

			public enum Comparator {
				EQ, GE, GT, LE, LT, NE;
			}

			public enum JoinerApply {

				CONSTRAINT, INFIX, NEAR2, PREFIX;

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
			private String comparator;

			@XmlAttribute
			private int consume;

			@XmlAttribute
			private String delimiter;
			@XmlAttribute
			private QName element;
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
				return Comparator.valueOf(this.comparator);
			}

			public int getConsume() {
				return consume;
			}

			public String getDelimiter() {
				return delimiter;
			}

			public QName getElement() {
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

			public void setCompare(Comparator comparator) {
				this.comparator = comparator.toString();
			}

			public void setConsume(int consume) {
				this.consume = consume;
			}

			public void setElement(QName qName) {
				this.element = qName;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}

			public void setTokenize(Tokenize tokenize) {
				this.tokenize = tokenize.toXMLString();
			}

		}

		@XmlAccessorType(XmlAccessType.FIELD)
		public final static class QueryStarter implements
				QueryOptionsBuilder.QueryGrammarItem {

			public enum StarterApply {

				GROUPING, PREFIX;

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
			private QName element;
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

			public QueryStarter(String text) {
				this.starterText = text;
			}

			public StarterApply getApply() {
				return StarterApply.fromXMLString(apply);
			}

			public String getDelimiter() {
				return delimiter;
			}

			public QName getElement() {
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

			public void setElement(QName element) {
				this.element = element;
			}

			public void setOptions(String options) {
				this.options = options;
			}

			public void setStrength(int strength) {
				this.strength = strength;
			}

			public void setText(String text) {
				this.starterText = text;
			}

			public void setTokenize(String tokenize) {
				this.tokenize = tokenize;
			}

		}

		public enum Tokenize {

			DEFAULT, WORD;

			static Tokenize fromXMLString(String xmlString) {
				return Tokenize.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			String toXMLString() {
				return this.toString().toLowerCase().replace("_", "-");
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "implicit")
		private AnyElement implicit;

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

		@Override
		public void build(QueryOptions options) {
			options.setGrammar(this);
		}

		public org.w3c.dom.Element getImplicit() {
			return implicit.getValue();
		}

		public List<QueryJoiner> getJoiners() {
			return joiners;
		}

		public String getQuotation() {
			return quotation;
		}

		public List<QueryStarter> getStarters() {
			return starters;
		}

		public void setImplicit(org.w3c.dom.Element implicit) {
			this.implicit = new AnyElement(implicit);
		}

		public void setQuotation(String quotation) {
			this.quotation = quotation;
		}
	}

	/**
	 * Represents how query terms are to be combined.
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryOperator extends BaseQueryOptionConfiguration
			implements QueryOptionsBuilder.QueryOptionsItem,
			QueryOptionsBuilder.QueryAnnotations {

		@XmlAttribute
		private String name;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "state")
		private List<QueryState> states;

		public QueryOperator() {
			states = new ArrayList<QueryState>();
		}

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		public void addState(QueryState state) {
			states.add(state);
		}

		@Override
		public void build(QueryOptions options) {
			options.getQueryOperators().add(this);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		public String getName() {
			return name;
		}

		public List<QueryState> getStates() {
			return states;
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
	 * Corresponds to the &lt;properties&gt; constraint type in the MarkLogic
	 * Search API
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryProperties extends BaseConstraintItem
			implements QueryOptionsBuilder.QueryConstraintItem {

		public QueryProperties() {
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryRange extends
			FacetableConstraintConfiguration implements
			QueryOptionsBuilder.Indexable,
			QueryOptionsBuilder.QuerySuggestionSourceItem,
			QueryOptionsBuilder.QueryConstraintItem,
			QueryOptionsBuilder.QueryValuesItem {

		/**
		 * Configures a range, for use in grouping range index values in facets.
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Bucket implements
				QueryOptionsBuilder.QueryRangeItem {

			/**
			 * The textual label for the bucket.
			 */
			@XmlValue
			private String content;
			/**
			 * The low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 */
			@XmlAttribute
			private String ge;
			/**
			 * The high end of the bucket's range. Stands for "less than"
			 */
			@XmlAttribute
			private String lt;
			/**
			 * A unique name to reference this bucket.
			 */
			@XmlAttribute
			private String name;

			@Override
			public void build(QueryRange range) {
				range.addBucket(this);
			}

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

		/**
		 * Models a bucket on a range constraint whose values are anchored to
		 * time, and computed based on the current time.
		 * 
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class ComputedBucket implements
				QueryOptionsBuilder.QueryRangeItem {

			/**
			 * Defines values for use in computed buckets anchored to time.
			 * 
			 */
			public static enum AnchorValue {

				NOW, START_OF_DAY, START_OF_MONTH, START_OF_YEAR;
				static AnchorValue fromXmlString(String xmlString) {
					return AnchorValue.valueOf(xmlString.toUpperCase().replace(
							"-", "_"));
				}

				String toXmlString() {
					return this.toString().toLowerCase().replace("_", "-");
				}

			}

			/**
			 * A value for anchoring this computed bucket.
			 */
			@XmlAttribute(name = "anchor")
			private String anchor;

			/**
			 * The low end of the bucket's range. Stands for
			 * "greater than or equal to".
			 */
			@XmlAttribute
			private String ge;

			/**
			 * A value for anchoring the "greate than or equal" value for this
			 * computed bucket.
			 */
			@XmlAttribute(name = "ge-anchor")
			private String geAnchor;

			/**
			 * The textual label for the bucket.
			 */
			@XmlValue
			private String label;

			/**
			 * The high end of the bucket's range. Stands for "less than"
			 */
			@XmlAttribute
			private String lt;

			/**
			 * A value for anchoring the "less than" value for this computed
			 * bucket.
			 */
			@XmlAttribute(name = "lt-anchor")
			private String ltAnchor;

			/**
			 * A unique name to reference this bucket.
			 */
			@XmlAttribute
			private String name;

			@Override
			public void build(QueryRange range) {
				range.addComputedBucket(this);
			}

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

			public void setGe(String ge) {
				this.ge = ge;
			}

			public void setLabel(String content) {
				this.label = content;
			}

			public void setLt(String lt) {
				this.lt = lt;
			}

			public void setName(String name) {
				this.name = name;
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "bucket")
		private List<Bucket> buckets;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "computed-bucket")
		private List<ComputedBucket> computedBuckets;

		public QueryRange() {
			buckets = new ArrayList<Bucket>();
			computedBuckets = new ArrayList<ComputedBucket>();
		}

		/**
		 * Add a bucket to this RangeOption's buckets.
		 * 
		 * @param bucket
		 *            a Bucket object for use with this RangeOption
		 */
		public void addBucket(Bucket bucket) {
			buckets.add(bucket);
		}

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
		 * get the list of buckets for this RangeOption
		 * 
		 * @return
		 */
		public List<Bucket> getBuckets() {
			return buckets;
		}

		/**
		 * @return this RangeOptions's List of ComputedBuckets
		 */
		public List<ComputedBucket> getComputedBuckets() {
			return computedBuckets;
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QuerySortOrder extends BaseQueryOptionConfiguration
			implements QueryOptionsBuilder.Indexable,
			QueryOptionsBuilder.QueryStateItem,
			QueryOptionsBuilder.QueryOptionsItem,
			QueryOptionsBuilder.QueryAnnotations {

		public enum Direction implements QueryOptionsBuilder.QuerySortOrderItem {

			ASCENDING, DESCENDING;

			static Direction fromXMLString(String xmlString) {
				return Direction.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			@Override
			public void build(QuerySortOrder sortOrder) {
				sortOrder.setDirection(this);
			}

			String toXMLString() {
				return this.toString().toLowerCase().replace("_", "-");
			}
		}

		public enum Score implements QueryOptionsBuilder.QuerySortOrderItem {
			YES;

			@Override
			public void build(QuerySortOrder sortOrder) {
				sortOrder.setScore();
			}
		};

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "attribute")
		private Attribute attributeReference;

		@XmlAttribute
		private String collation;

		@XmlAttribute
		private String direction;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "element")
		private Element elementReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field fieldReference;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "score")
		private String score;

		@XmlAttribute
		private QName type;

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void build(QueryOptions options) {
			options.getSortOrders().add(this);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		@Override
		public QName getAttribute() {
			return attributeReference.asQName();
		}

		public String getCollation() {
			return collation;
		}

		public Direction getDirection() {
			return Direction.fromXMLString(direction);
		}

		@Override
		public QName getElement() {
			return elementReference.asQName();
		}

		@Override
		public String getFieldName() {
			return this.fieldReference.getName();
		}

		public QName getType() {
			return type;
		}

		@Override
		public void setAttribute(Attribute attribute) {
			this.attributeReference = attribute;
		}

		public void setCollation(String collation) {
			this.collation = collation;
		}

		public void setDirection(Direction direction) {
			this.direction = direction.toXMLString();
		}

		@Override
		public void setElement(Element element) {
			this.elementReference = element;
		}

		@Override
		public void setField(Field field) {
			this.fieldReference = field;
		}

        @Override
        public void setPath(PathIndex pathIndex) {
            // TODO: Is this the right thing to do?
            throw new UnsupportedOperationException("Path indexes are not part of sort orders");
        }

        public void setScore() {
			score = "";
		}
		
		public Score getScore() {
			return (this.score != null && this.score.equals("")) ? Score.YES : null;
		}
		
		public void unsetScore() {
			score = null;
		}

		public void setType(QName type) {
			this.type = type;
		}
		

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryState extends BaseQueryOptionConfiguration
			implements QueryAnnotations {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "additional-query")
		private AnyElement additionalQuery;

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

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "searchable-expression")
		private String searchableExpression;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "search-option")
		private List<String> searchOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "sort-order")
		private List<QuerySortOrder> sortOrders;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "transform-results")
		private QueryTransformResults transformResultsOption;

		public QueryState() {
			sortOrders = new ArrayList<QuerySortOrder>();
		}

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		public void addForest(Long forest) {
			if (forests == null) {
				forests = new ArrayList<Long>();
			}
			this.forests.add(forest);
		}

		public void addOption(QueryStateItem option) {
			if (option instanceof org.w3c.dom.Element) {
				setAdditionalQuery((org.w3c.dom.Element) option);
			} else if (option instanceof QuerySortOrder) {
				addSortOrder((QuerySortOrder) option);
			} else if (option instanceof QueryAnnotation) {
				getAnnotations().add((QueryAnnotation) option);
			} else {
			}
		}

		public void addSortOrder(QuerySortOrder sortOrder) {
			sortOrders.add(sortOrder);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		public void deleteSortOrders() {
			sortOrders = new ArrayList<QuerySortOrder>();
		}

		public org.w3c.dom.Element getAdditionalQuery() {
			return additionalQuery.getValue();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
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

		public String getSearchableExpression() {
			return searchableExpression;
		}

		public List<String> getSearchOptions() {
			return searchOptions;
		}

		public List<QuerySortOrder> getSortOrders() {
			return sortOrders;
		}

		public QueryTransformResults getTransformResultsOption() {
			return transformResultsOption;
		}

		public void setAdditionalQuery(AnyElement additionalQuery) {
			this.additionalQuery = additionalQuery;
		}

		public void setAdditionalQuery(org.w3c.dom.Element element) {
			additionalQuery = new AnyElement(element);
		}

		public void setDebug(Boolean debug) {
			this.debug = debug;
		}

		public void setForests(List<Long> forests) {
			this.forests = forests;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setPageLength(Long pageLength) {
			this.pageLength = pageLength;
		}

		public void setQualityWeight(Double qualityWeight) {
			this.qualityWeight = qualityWeight;
		}

		public void setSearchableExpression(String searchableExpression) {
			this.searchableExpression = searchableExpression;
		}

		public void setSearchOptions(List<String> searchOptions) {
			this.searchOptions = searchOptions;
		}

		public void setSortOrders(List<QuerySortOrder> sortOrders) {
			this.sortOrders = sortOrders;
		}

		public void setTransformResultsOption(
				QueryTransformResults transformResultsOption) {
			this.transformResultsOption = transformResultsOption;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QuerySuggestionSource extends
			BaseQueryOptionConfiguration implements QueryOptionsItem,
			QueryAnnotations {

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
			this.suggestionOptions = new ArrayList<String>();
		}

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			this.addElementAsAnnotation(element);
		}

		public void addSuggestionOption(String suggestionOption) {
			this.suggestionOptions.add(suggestionOption);
		}

		@Override
		public void build(QueryOptions options) {
			options.getSuggestionSources().add(this);
		}

		@Override
		public void deleteAnnotations() {
			this.deleteAnnotations();
		}

		public void deleteSuggestionOptions() {
			suggestionOptions = new ArrayList<String>();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return this.getAnnotations();
		}

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
	 * Provides configuration for a search API term.
	 * <p>
	 * If a search term doesn't match a named constraint, it uses Term. You can
	 * thus override default search behavior by changing a QueryOptions term
	 * element.
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class QueryTerm extends BaseQueryOptionConfiguration
			implements QueryOptionsBuilder.QueryOptionsItem, TermOptions,
			QueryOptionsBuilder.QueryAnnotations {

		/**
		 * The TermApply enumeration provides the special circumstances of an
		 * empty search string.
		 * 
		 * {@see
		 * com.marklogic.client.config.QueryOptions.QueryTerm.getEmptyApply()}
		 * 
		 */
		public enum TermApply implements QueryTermItem {

			ALL_RESULTS, NO_RESULTS;

			public static TermApply fromXmlString(String xmlString) {
				return TermApply.valueOf(xmlString.toUpperCase().replace("-",
						"_"));
			}

			public String toXmlString() {
				return this.toString().toLowerCase().replace("_", "-");
			}

		}

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "default")
		private QueryConstraintConfigurationBag defaultConstraint;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "empty")
		private XQueryExtension empty;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "term-option")
		private List<String> termOptions;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "weight")
		private double weight;

		private XQueryExtension xQueryExtension;

		public QueryTerm() {
			this.defaultConstraint = new QueryConstraintConfigurationBag();
		}

		@Override
		public void addAnnotation(QueryAnnotation queryAnnotation) {
			this.getAnnotations().add(queryAnnotation);
		}

		@Override
		public void addElementAsAnnotation(org.w3c.dom.Element element) {
			super.addElementAsAnnotation(element);
		}

		@Override
		public void build(QueryOptions options) {
			options.setTerm(this);
		}

		@Override
		public void deleteAnnotations() {
			super.deleteAnnotations();
		}

		@Override
		public List<QueryAnnotation> getAnnotations() {
			return super.getAnnotations();
		}

		public <T extends BaseConstraintItem> T getConstraintConfiguration() {
			return defaultConstraint.getConstraintConfiguration();
		}

		public TermApply getEmptyApply() {
			return TermApply.fromXmlString(empty.getApply());
		}

		public XQueryExtension getTermFunction() {
			return xQueryExtension;
		}

		@Override
		public List<String> getTermOptions() {
			return termOptions;
		}

		@Override
		public Double getWeight() {
			return this.weight;
		}

		public void setConstraintItem(BaseConstraintItem constraintConfiguration) {
			this.defaultConstraint.setImplementation(constraintConfiguration);
		}

		public void setEmptyApply(TermApply termApply) {
			empty = new XQueryExtension();
			empty.setApply(termApply.toXmlString());
		}

		@Override
		public void setTermOptions(List<String> termOptions) {
			this.termOptions = termOptions;
		}

		@Override
		public void setWeight(Double weight) {
			this.weight = weight;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	// TODO examine schema for this -- not sufficient for all the use cases we
	// have.
	public static class QueryTransformResults implements QueryOptionsItem {

		@XmlAttribute
		private String apply;
		@XmlAttribute
		private String at;
		@XmlAttribute
		private String ns;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "preferred-elements")
		private PreferredElements preferredElements;

		public QueryTransformResults() {
			preferredElements = new PreferredElements();
		}

		public void addPreferredElement(Element element) {
			preferredElements.addElement(element);
		}

		@Override
		public void build(QueryOptions options) {
			options.setTransformResults(this);
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

		public List<Element> getPreferredElements() {
			return preferredElements.getElements();
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

		public void setPreferredElements(List<Element> elements) {
			this.preferredElements.setElements(elements);
		}
	}

	public enum QueryUri implements QueryValuesItem {

		YES;

		@Override
		public void build(QueryValues values) {
			values.setUri();
		}

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryValue extends BaseConstraintItem implements
			QueryOptionsBuilder.Indexable,
			QueryOptionsBuilder.QueryConstraintItem, TermOptions {

	}

	/**
	 * 
	 *
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class QueryValues implements QueryOptionsItem {

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "aggregate")
		private Aggregate aggregate;
		@XmlTransient
		private QueryConstraintConfigurationBag constraintConfiguration;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "field")
		private Field field;
		@XmlAttribute
		private String name;
		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "uri")
		private QueryUri uri;

		@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "values-option")
		private List<String> valuesOptions;

		public QueryValues() {
			this.valuesOptions = new ArrayList<String>();
		}

		public QueryValues(String name) {
			this.name = name;
			this.valuesOptions = new ArrayList<String>();
		}

		public void addValuesOption(String valuesOption) {
			this.valuesOptions.add(valuesOption);
		}

		@Override
		public void build(QueryOptions options) {
			options.getQueryValues().add(this);
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

		public <T extends BaseConstraintItem> T getSource() {
			return constraintConfiguration.getConstraintConfiguration();
		}

		public QueryUri getUri() {
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

		public void setSource(BaseConstraintItem source) {
			this.constraintConfiguration = new QueryConstraintConfigurationBag();
			constraintConfiguration.setImplementation(source);
		}

		public void setUri() {
			this.uri = QueryUri.YES;
		}

		public void setValuesOptions(List<String> valuesOptions) {
			this.valuesOptions = valuesOptions;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public final static class QueryWord extends BaseConstraintItem implements
			QueryOptionsBuilder.Indexable, TermOptions,
			QueryOptionsBuilder.QueryConstraintItem,
			QueryOptionsBuilder.QueryTermItem {

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class WordLexicon implements QuerySuggestionSourceItem {

		@XmlAttribute
		private String collation;

		@XmlElement(name = "fragment-scope")
		private FragmentScope fragmentScope;

		@Override
		public void build(QuerySuggestionSource suggestionSource) {
			suggestionSource.setWordLexicon(this);
		}

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
	 * Models elements that locate XQuery functions with use of "ns", "apply"
	 * and "at" attributes.
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

	@XmlAccessorType(XmlAccessType.FIELD)
	abstract static class QueryGeospatial extends BaseConstraintItem {

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

		public QueryGeospatial() {
			this.geoOptions = new ArrayList<String>();
			this.facetOptions = new ArrayList<String>();

		}

		/**
		 * Add a facet option to this constraint type
		 * 
		 * @param facetOption
		 */
		public void addFacetOption(String facetOption) {
			this.facetOptions.add(facetOption);
		}

		public void addGeoOption(String geoOption) {
			this.geoOptions.add(geoOption);
		}

		/**
		 * get the list of facet options.
		 * 
		 * @return
		 */
		public List<String> getFacetOptions() {
			return facetOptions;
		}

		public Heatmap getHeatmap() {
			return heatmap;
		}

		public MarkLogicQName getLatitude() {
			return latitude;
		}

		public MarkLogicQName getLongitude() {
			return longitude;
		}

		public MarkLogicQName getParent() {
			return parent;
		}

		public void setGeoOptions(List<String> geoOptions) {
			this.geoOptions = geoOptions;
		}

		public void setHeatmap(Heatmap heatmap) {
			this.heatmap = heatmap;
		}

		public void setLatitude(MarkLogicQName latitude) {
			this.latitude = latitude;
		}

		public void setLongitude(MarkLogicQName longitude) {
			this.longitude = longitude;
		}

		public void setParent(MarkLogicQName parent) {
			this.parent = parent;
		}

	}

	public static final String SEARCH_NS = "http://marklogic.com/appservices/search";

	private static final Logger logger = LoggerFactory
			.getLogger(QueryOptionsBuilder.class);

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "additional-query")
	private AnyElement additionalQuery;

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

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-aggregates")
	private Boolean returnAggregates;

	@XmlElement(namespace = QueryOptions.SEARCH_NS, name = "return-constraintOptions")
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

	@XmlAnyElement
	private org.w3c.dom.Element searchableExpression;

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

	}

	@Override
	public void addAnnotation(QueryAnnotation queryAnnotation) {
		this.getAnnotations().add(queryAnnotation);
	}

	@Override
	public void addElementAsAnnotation(org.w3c.dom.Element element) {
		QueryAnnotation annotation = new QueryAnnotation();
		annotation.add(element);
		annotations.add(annotation);
	}

	public void addForest(Long forest) {
		this.forests.add(forest);
	}

	public void addSearchOption(String searchOption) {
		this.searchOptions.add(searchOption);
	}

	@Override
	public void deleteAnnotations() {
		annotations = new ArrayList<QueryAnnotation>();
	}

	public org.w3c.dom.Element getAdditionalQuery() {
		org.w3c.dom.Element e = additionalQuery.getValue();
		return (org.w3c.dom.Element) e.getFirstChild();
	}

	@Override
	public List<QueryAnnotation> getAnnotations() {
		return annotations;
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

	public org.w3c.dom.Element getSearchableExpression() {
		return searchableExpression;
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
		this.additionalQuery = new AnyElement("additional-query",
				additionalQuery);
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

	public void setForests(List<Long> forests) {
		this.forests = forests;
	}

	public void setFragmentScope(FragmentScope fragmentScope) {
		this.fragmentScope = fragmentScope.toString().toLowerCase();
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

	public void setSearchableExpression(org.w3c.dom.Element searchableExpression) {
		this.searchableExpression = searchableExpression;
	}

	public void setSearchOptions(List<String> searchOptions) {
		this.searchOptions = searchOptions;
	}

	public void setSortOrders(List<QuerySortOrder> sortOrders) {
		this.sortOrders = sortOrders;
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

}
