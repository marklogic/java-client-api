package com.marklogic.client.config.search.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marklogic.client.config.search.FunctionRef;
import com.marklogic.client.config.search.JAXBBackedQueryOption;
import com.marklogic.client.config.search.MarkLogicUnhandledElementException;
import com.marklogic.client.config.search.jaxb.Collection;
import com.marklogic.client.config.search.jaxb.Custom;
import com.marklogic.client.config.search.jaxb.ElementQuery;
import com.marklogic.client.config.search.jaxb.GeoAttrPair;
import com.marklogic.client.config.search.jaxb.GeoElem;
import com.marklogic.client.config.search.jaxb.GeoElemPair;
import com.marklogic.client.config.search.jaxb.Properties;
import com.marklogic.client.config.search.jaxb.Range;
import com.marklogic.client.config.search.jaxb.Value;
import com.marklogic.client.config.search.jaxb.Word;

public class JAXBHelper {

	static final private Logger logger = LoggerFactory
			.getLogger(JAXBHelper.class);

	static JAXBElement<String> wrapString(QName elementName, String str) {

		JAXBElement<String> elem = new JAXBElement<String>(elementName,
				String.class, str);
		return elem;
	}

	static JAXBElement<String> wrapString(String elementName, String str) {

		JAXBElement<String> elem = new JAXBElement<String>(
				JAXBHelper.newQNameFor(elementName), String.class, str);
		return elem;
	}

	static JAXBElement<FunctionRef> wrapFunction(QName elementName,
			FunctionRef function) {
		JAXBElement<FunctionRef> elem = new JAXBElement<FunctionRef>(
				elementName, FunctionRef.class, FunctionRefImpl.class, function);
		return elem;
	}

	static QName newQNameFor(String localName) {
		return new QName("http://marklogic.com/appservices/search", localName);
	}

	public static JAXBBackedQueryOption newQueryOption(Object ot) {
		logger.debug("Making new query option for object of class "
				+ ot.getClass().getName());
		@SuppressWarnings("rawtypes")
		Class clazz = ot.getClass();
		if (clazz == com.marklogic.client.config.search.jaxb.Term.class) {
			return new TermImpl(
					(com.marklogic.client.config.search.jaxb.Term) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Grammar.class) {
			return new GrammarImpl(
					(com.marklogic.client.config.search.jaxb.Grammar) ot);
		} else if (ot instanceof com.marklogic.client.config.search.jaxb.Constraint) {
			com.marklogic.client.config.search.jaxb.Constraint constraint = (com.marklogic.client.config.search.jaxb.Constraint) ot;
			List<Object> constraintChildren = constraint.getConstraint();
			for (int i = 0; i < constraintChildren.size(); i++) {
				@SuppressWarnings("unchecked")
				JAXBElement<Object> constraintElement = (JAXBElement<Object>) constraintChildren
						.get(i);

				Object constraintSpec = constraintElement.getValue();
				logger.debug("Class of constraintSpec to dispatch "
						+ constraintSpec.getClass());
				if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Range) {
					return new RangeConstraintImpl(constraint,
							(Range) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Collection) {
					return new CollectionConstraintImpl(constraint,
							(Collection) constraintSpec);
				} else if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Value.class) {
					return new ValueConstraintImpl(constraint,
							(Value) constraintSpec);
				} else if (constraintSpec.getClass() == com.marklogic.client.config.search.jaxb.Word.class) {
					return new WordConstraintImpl(constraint,
							(Word) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.ElementQuery) {
					return new ElementQueryConstraintImpl(constraint,
							(ElementQuery) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Properties) {
					return new PropertiesConstraintImpl(constraint,
							(Properties) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Custom) {
					return new CustomConstraintImpl(constraint,
							(Custom) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.GeoElem) {
					return new GeoElementConstraintImpl(constraint,
							(GeoElem) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.GeoElemPair) {
					return new GeoElementPairConstraintImpl(constraint,
							(GeoElemPair) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.GeoAttrPair) {
					return new GeoAttrPairConstraintImpl(constraint,
							(GeoAttrPair) constraintSpec);
				} else if (constraintSpec instanceof com.marklogic.client.config.search.jaxb.Annotation) {
					// TODO verify this is handled elsewhere.
				}
			}
		} else if (clazz == com.marklogic.client.config.search.jaxb.Operator.class) {
			return new OperatorImpl(
					(com.marklogic.client.config.search.jaxb.Operator) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.TransformResults.class) {
			return new TransformResultsImpl(
					(com.marklogic.client.config.search.jaxb.TransformResults) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.DefaultSuggestionSource.class) {
			return new DefaultSuggestionSourceImpl(
					(com.marklogic.client.config.search.jaxb.DefaultSuggestionSource) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.SuggestionSource.class) {
			return new SuggestionSourceImpl(
					(com.marklogic.client.config.search.jaxb.SuggestionSource) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.Annotation.class) {
			//
		} else if (clazz == com.marklogic.client.config.search.jaxb.SortOrder.class) {
			return new SortOrderImpl(
					(com.marklogic.client.config.search.jaxb.SortOrder) ot);
		} else if (clazz == com.marklogic.client.config.search.jaxb.AdditionalQuery.class) {
		} else if (clazz == com.marklogic.client.config.search.jaxb.Attribute.class) {
		} else if (clazz == com.marklogic.client.config.search.jaxb.Element.class) {
		} else if (clazz == com.marklogic.client.config.search.jaxb.Parent.class) {
		}else if (clazz == com.marklogic.client.config.search.jaxb.Lat.class) {
		}else if (clazz == com.marklogic.client.config.search.jaxb.Lon.class) {
		} else if (clazz == com.marklogic.client.config.search.jaxb.Bucket.class) {
			return new BucketImpl((com.marklogic.client.config.search.jaxb.Bucket) ot);
		}else if (clazz == com.marklogic.client.config.search.jaxb.Heatmap.class) {
			return new HeatmapImpl((com.marklogic.client.config.search.jaxb.Heatmap) ot);
		} else if (clazz == javax.xml.bind.JAXBElement.class) {
		} else {
			throw new MarkLogicUnhandledElementException(ot.getClass()
					.getName());
		}
		return null;

	}

	/**
	 * 
	 * @param parent
	 * @param clazz
	 * @return a child object of parent based on class name clazz.
	 */
	public static <T extends JAXBBackedQueryOption> T getOneByClassName(
			JAXBBackedQueryOption parent, Class<T> clazz) {
		List<T> l = getByClassName(parent, clazz);
		if (l.size() == 0) {
			return null;
		} else {
			return l.get(0);
		}
	}

	public static <T extends JAXBBackedQueryOption> List<T> getByClassName(
			JAXBBackedQueryOption parent, Class<T> clazz) {
		List<JAXBBackedQueryOption> options = new ArrayList<JAXBBackedQueryOption>();
		for (Object ot : parent.getJAXBChildren()) {
			if (ot.getClass().getPackage().toString()
					.contains("com.marklogic.client.config.search.jaxb")) {
				logger.debug("Instantiating POJO class to wrap");
				JAXBBackedQueryOption newOption = JAXBHelper.newQueryOption(ot);
				// add all options that are handled by POJOs to the return
				// lists.
				if (newOption != null && clazz.isInstance(newOption)) {
					options.add(JAXBHelper.newQueryOption(ot));
				}
			} else {
				//
			}
		}
		return (List<T>) options;
	}

	public static <T extends Object> List<T> getSimpleByElementName(
			JAXBBackedQueryOption parent, String localName) {
		List<T> l = new ArrayList<T>();
		for (Object ot : parent.getJAXBChildren()) {
			if (ot instanceof JAXBElement<?>) {
				JAXBElement<T> e = (JAXBElement<T>) ot;
				if (e.getName().equals(
						new QName("http://marklogic.com/appservices/search",
								localName))) {
					l.add(e.getValue());
				}
			}
		}
		return l;
	}

	public static <T extends Object> T getOneSimpleByElementName(
			JAXBBackedQueryOption parent, String localName) {
		List<T> l = getSimpleByElementName(parent, localName);
		if (l.size() == 0) {
			return null;
		} else {
			return l.get(0);
		}
	}

	public static Object getOneJAXBByElementName(JAXBBackedQueryOption parent,
			String localName) {
		List<JAXBBackedQueryOption> options = new ArrayList<JAXBBackedQueryOption>();
		for (Object ot : parent.getJAXBChildren()) {
			if (ot instanceof JAXBElement<?>) {
				JAXBElement<Object> e = (JAXBElement<Object>) ot;
				if (e.getName().equals(
						new QName("http://marklogic.com/appservices/search",
								localName))) {
					return ot;
				}
			}
		}
		return null;
	}

	private static List<Object> getUnboundOptions(JAXBBackedQueryOption option,
			String localName) {
		List<Object> options = option.getJAXBChildren();
		List<Object> conformingOptions = new ArrayList<Object>();
		for (Object o : options) {
			if (o.getClass() == javax.xml.bind.JAXBElement.class) {
				@SuppressWarnings("unchecked")
				JAXBElement<Object> jaxbElement = (JAXBElement<Object>) o;
				if (jaxbElement.getName().equals(
						new QName("http://marklogic.com/appservices/search",
								localName))) {
					conformingOptions.add(o);
				}
			}
		}
		return conformingOptions;
	}

	public static <T extends Object> void setOneSimpleByElementName(
			JAXBBackedQueryOption option, String localName, T value) {
		List<Object> existingFlagAsList = getUnboundOptions(option, localName);
		if (existingFlagAsList.size() == 0) {
			JAXBElement<Object> newElement = new JAXBElement<Object>(new QName(
					"http://marklogic.com/appservices/search", localName),
					(Class<Object>) value.getClass(), value);
			logger.debug("Here is the new element " + newElement.getName()
					+ " and its value " + newElement.getValue());
			option.getJAXBChildren().add(newElement);
		} else {
			@SuppressWarnings("unchecked")
			JAXBElement<Object> existingOption = (JAXBElement<Object>) existingFlagAsList
					.get(0);
			existingOption.setValue(value);
		}
	}

	public static <T extends Object> void setSimpleByElementName(
			JAXBBackedQueryOption option, String localName, List<T> values) {
		List<Object> existingFlagAsList = getUnboundOptions(option, localName);
		option.getJAXBChildren().removeAll(existingFlagAsList);
		existingFlagAsList.addAll(values);

	}

	public static void setOneByClassName(JAXBBackedQueryOption parent,
			JAXBBackedQueryOption value) {
		for (Object ot : parent.getJAXBChildren()) {
			if (value.asJaxbObject().getClass().isInstance(ot)) {
				ot = value.asJaxbObject();
				return;
			}
		}
		parent.getJAXBChildren().add(value.asJaxbObject());
	}
}
