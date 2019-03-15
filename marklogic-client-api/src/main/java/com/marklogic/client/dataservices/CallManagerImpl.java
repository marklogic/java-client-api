/*
 * Copyright 2019 MarkLogic Corporation
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
package com.marklogic.client.dataservices;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.impl.RESTServices.CallField;
import com.marklogic.client.impl.RESTServices.MultipleAtomicCallField;
import com.marklogic.client.impl.RESTServices.MultipleCallResponse;
import com.marklogic.client.impl.RESTServices.MultipleNodeCallField;
import com.marklogic.client.impl.RESTServices.SingleAtomicCallField;
import com.marklogic.client.impl.RESTServices.SingleCallResponse;
import com.marklogic.client.impl.RESTServices.SingleNodeCallField;
import com.marklogic.client.impl.SessionStateImpl;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.marker.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class CallManagerImpl implements CallManager {
  private static final Map<String, BaseFieldifier> paramFieldifiers = new HashMap<>();
  {
    paramFieldifiers.put(BaseProxy.BooleanType.NAME,         new BooleanFieldifier());
    paramFieldifiers.put(BaseProxy.DateType.NAME,            new DateFieldifier());
    paramFieldifiers.put(BaseProxy.DateTimeType.NAME,        new DateTimeFieldifier());
    paramFieldifiers.put(BaseProxy.DayTimeDurationType.NAME, new DayTimeDurationFieldifier());
    paramFieldifiers.put(BaseProxy.DecimalType.NAME,         new DecimalFieldifier());
    paramFieldifiers.put(BaseProxy.DoubleType.NAME,          new DoubleFieldifier());
    paramFieldifiers.put(BaseProxy.FloatType.NAME,           new FloatFieldifier());
    paramFieldifiers.put(BaseProxy.IntegerType.NAME,         new IntegerFieldifier());
    paramFieldifiers.put(BaseProxy.LongType.NAME,            new LongFieldifier());
    paramFieldifiers.put(BaseProxy.StringType.NAME,          new StringFieldifier());
    paramFieldifiers.put(BaseProxy.TimeType.NAME,            new TimeFieldifier());
    paramFieldifiers.put(BaseProxy.UnsignedIntegerType.NAME, new UnsignedIntegerFieldifier());
    paramFieldifiers.put(BaseProxy.UnsignedLongType.NAME,    new UnsignedLongFieldifier());
    paramFieldifiers.put(BaseProxy.ArrayType.NAME,           new ArrayFieldifier());
    paramFieldifiers.put(BaseProxy.BinaryDocumentType.NAME,  new BinaryDocumentFieldifier());
    paramFieldifiers.put(BaseProxy.JsonDocumentType.NAME,    new JsonDocumentFieldifier());
    paramFieldifiers.put(BaseProxy.ObjectType.NAME,          new ObjectFieldifier());
    paramFieldifiers.put(BaseProxy.TextDocumentType.NAME,    new TextDocumentFieldifier());
    paramFieldifiers.put(BaseProxy.XmlDocumentType.NAME,     new XmlDocumentFieldifier());
  };

  private static final Map<String, Format> typeFormats= new HashMap<>();
  {
    typeFormats.put(BaseProxy.ArrayType.NAME,           BaseProxy.ArrayType.FORMAT);
    typeFormats.put(BaseProxy.BinaryDocumentType.NAME,  BaseProxy.BinaryDocumentType.FORMAT);
    typeFormats.put(BaseProxy.JsonDocumentType.NAME,    BaseProxy.JsonDocumentType.FORMAT);
    typeFormats.put(BaseProxy.ObjectType.NAME,          BaseProxy.ObjectType.FORMAT);
    typeFormats.put(BaseProxy.TextDocumentType.NAME,    BaseProxy.TextDocumentType.FORMAT);
    typeFormats.put(BaseProxy.XmlDocumentType.NAME,     BaseProxy.XmlDocumentType.FORMAT);
  };

  private static final Map<String, ServerTypeConverter> returnConverters = new HashMap<>();
  {
    new BooleanTypeConverter().putTo(returnConverters);
    new DateTypeConverter().putTo(returnConverters);
    new DateTimeTypeConverter().putTo(returnConverters);
    new DayTimeDurationTypeConverter().putTo(returnConverters);
    new DecimalTypeConverter().putTo(returnConverters);
    new DoubleTypeConverter().putTo(returnConverters);
    new FloatTypeConverter().putTo(returnConverters);
    new IntegerTypeConverter().putTo(returnConverters);
    new LongTypeConverter().putTo(returnConverters);
    new StringTypeConverter().putTo(returnConverters);
    new TimeTypeConverter().putTo(returnConverters);
    new UnsignedIntegerTypeConverter().putTo(returnConverters);
    new UnsignedLongTypeConverter().putTo(returnConverters);
    new BinaryDocumentTypeConverter().putTo(returnConverters);
    ServerTypeConverter converter = new JsonDocumentTypeConverter();
    returnConverters.put(BaseProxy.JsonDocumentType.NAME, converter);
    returnConverters.put(BaseProxy.ArrayType.NAME, converter);
    returnConverters.put(BaseProxy.ObjectType.NAME, converter);
    new TextDocumentTypeConverter().putTo(returnConverters);
    new XMLDocumentTypeConverter().putTo(returnConverters);
  }

   private DatabaseClient client;

   CallManagerImpl(DatabaseClient client) {
     if (client == null) {
       throw new IllegalArgumentException("cannot construct CallManager with null database client");
     }
     this.client = client;
   }

   @Override
   public SessionState newSessionState() {
      return new SessionStateImpl();
   }

   @Override
   public CallableEndpoint endpoint(JSONWriteHandle serviceDeclaration, JSONWriteHandle endpointDeclaration, String extension) {
     JsonNode serviceDecl  = NodeConverter.handleToJsonNode(serviceDeclaration);
     JsonNode endpointDecl = NodeConverter.handleToJsonNode(endpointDeclaration);

     if (serviceDecl == null) {
       throw new IllegalArgumentException("cannot construct CallableEndpoint with null serviceDeclaration");
     } else if (endpointDecl == null) {
       throw new IllegalArgumentException("cannot construct CallableEndpoint with null endpointDeclaration");
     } else if (extension == null || extension.length() == 0) {
       throw new IllegalArgumentException("cannot construct CallableEndpoint with null or empty extension");
     } else if (!"sjs".equals(extension) && !"xqy".equals(extension)) {
       throw new IllegalArgumentException("extension must be sjs or xqy: "+extension);
     }

     return new CallableEndpointImpl(client, serviceDecl, endpointDecl, extension);
   }

   private static class CallableEndpointImpl extends EndpointDefinerImpl implements CallableEndpoint {
     private BaseProxy                 baseProxy;
     private String                    endpointDirectory;
     private String                    module;
     private String                    endpointPath;
     private ReturndefImpl             returndef;
     private Map<String, ParamdefImpl> paramdefs;
     private Map<String, Paramdef>     params;
     private ParamdefImpl              sessiondef;
     private Set<String>               required;

     private BaseProxy.ParameterValuesKind parameterValuesKind = BaseProxy.ParameterValuesKind.NONE;

     CallableEndpointImpl(
       DatabaseClient client, JsonNode serviceDeclaration, JsonNode endpointDeclaration, String extension
     ) {
        endpointDirectory = getText(serviceDeclaration.get("endpointDirectory"));
        if (endpointDirectory == null || endpointDirectory.length() == 0) {
          throw new IllegalArgumentException(
              "no endpointDirectory in service declaration: "+serviceDeclaration.toString()
          );
        }
        baseProxy = new BaseProxy(client, endpointDirectory);

        String functionName = getText(endpointDeclaration.get("functionName"));
        if (functionName == null || functionName.length() == 0) {
          throw new IllegalArgumentException(
              "no functionName in endpoint declaration: "+endpointDeclaration.toString()
          );
        }
        module = functionName+"."+extension;

        JsonNode functionParams = endpointDeclaration.get("params");
        if (functionParams != null) {
          if (!functionParams.isArray()) {
            throw new IllegalArgumentException(
                "params not array in endpoint declaration: "+endpointDeclaration.toString()
            );
          }

          int paramCount = functionParams.size();
          if (paramCount > 0) {
            for (JsonNode functionParam: functionParams) {
              if (!functionParam.isObject()) {
                throw new IllegalArgumentException(
                    "parameter is not object in endpoint declaration: "+functionParam.toString()
                );
              }
              ParamdefImpl paramdef = new ParamdefImpl(functionParam);

              String datatype = paramdef.getDataType();
              if (datatype == "session:") {
                if (sessiondef != null) {
                  throw new IllegalArgumentException(
                          module+" has two session parameters: "+sessiondef.getParamName()+" and "+paramdef.getParamName()
                  );
                }
                sessiondef = paramdef;
                continue;
              }

              if (parameterValuesKind != BaseProxy.ParameterValuesKind.MULTIPLE_MIXED) {
                boolean isNode = typeFormats.containsKey(datatype);
                switch (parameterValuesKind) {
                  case NONE:
                    boolean isMultiple = paramdef.isMultiple();
                    parameterValuesKind =
                            (isNode && isMultiple)  ? BaseProxy.ParameterValuesKind.MULTIPLE_NODES   :
                            (!isNode && isMultiple) ? BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS :
                            isNode                  ? BaseProxy.ParameterValuesKind.SINGLE_NODE      :
                            BaseProxy.ParameterValuesKind.SINGLE_ATOMIC;
                    break;
                  case SINGLE_ATOMIC:
                    parameterValuesKind =
                            (!isNode) ? BaseProxy.ParameterValuesKind.MULTIPLE_ATOMICS :
                            BaseProxy.ParameterValuesKind.MULTIPLE_MIXED;
                    break;
                  case SINGLE_NODE:
                    parameterValuesKind =
                            isNode ? BaseProxy.ParameterValuesKind.MULTIPLE_NODES :
                            BaseProxy.ParameterValuesKind.MULTIPLE_MIXED;
                    break;
                  case MULTIPLE_ATOMICS:
                    if (isNode) {
                      parameterValuesKind = BaseProxy.ParameterValuesKind.MULTIPLE_MIXED;
                    }
                    break;
                  case MULTIPLE_NODES:
                    if (!isNode) {
                      parameterValuesKind = BaseProxy.ParameterValuesKind.MULTIPLE_MIXED;
                    }
                    break;
                  default:
                    throw new InternalError("unknown case for "+parameterValuesKind);
                }
              }

              if (paramdefs == null) {
                paramdefs = new HashMap<>();
              }
              String paramName = paramdef.getParamName();
              paramdefs.put(paramName, paramdef);

              if (!paramdef.isNullable()) {
                if (required == null) {
                  required  = new HashSet<>();
                }
                required.add(paramName);
              }
            }
          }
        }

        JsonNode functionReturn = endpointDeclaration.get("return");
        if (functionReturn != null) {
          if (!functionReturn.isObject()) {
            throw new IllegalArgumentException(
                "return is not object in endpoint declaration: "+functionReturn.toString()
            );
          }
          returndef = new ReturndefImpl(functionReturn);
        }
      }

     CallableEndpointImpl getEndpoint() {
       return this;
     }

     @Override
     public String getEndpointPath() {
       if (endpointPath == null) {
         String endpointDir =  endpointDirectory;
         if (!endpointDir.endsWith("/")) {
           endpointDir = endpointDir+"/";
         }
         endpointPath = endpointDir+module;
       }
       return endpointPath;
     }
     @Override
     public Boolean isSessionRequired() {
       return (sessiondef != null && !sessiondef.isNullable());
     }
     @Override
     public Map<String, Paramdef> getParamdefs() {
       if (params == null && paramdefs != null) {
         Map<String, ? extends Paramdef> paramstemp = this.paramdefs;
         params = Collections.unmodifiableMap(paramstemp);
       }
       return params;
     }
     @Override
     public Returndef getReturndef() {
       return returndef;
     }

     @Override
     public NoneCaller returningNone() {
       if (returndef != null && isNullable() == false) {
         throw new IllegalArgumentException(module+" has a required return");
       }
       return new NoneCallerImpl(this);
     }
     @Override
     public <R> OneCaller<R> returningOne(Class<R> as) {
       if (returndef == null) {
         throw new IllegalArgumentException(module+" does not return values");
       } else if (returndef.isMultiple() == true) {
         throw new IllegalArgumentException(module+" returns multiple values");
       }
       return new OneCallerImpl(this, returndef.getFormat(), returndef.getTypeConverter().forClientType(as));
     }
     @Override
     public <R> ManyCaller<R> returningMany(Class<R> as) {
       if (returndef == null) {
         throw new IllegalArgumentException(module+" does not return values");
       } else if (returndef.isMultiple() != true) {
         throw new IllegalArgumentException(module+" returns a single value");
       }
       return new ManyCallerImpl(this, returndef.getFormat(), returndef.getTypeConverter().forClientType(as));
     }

     boolean isNullable() {
       return returndef.isNullable();
     }
     BaseProxy getBaseProxy() {
        return baseProxy;
     }
     String getModule() {
        return module;
     }
     ParamdefImpl getSessiondef() {
       return sessiondef;
     }
     ParamdefImpl getParamDef(String name) {
       if (paramdefs == null) {
         return null;
       }
       return paramdefs.get(name);
     }
     Set<String> getRequiredParams() {
        return required;
     }
     BaseProxy.ParameterValuesKind getParameterValuesKind() {
       return parameterValuesKind;
     }
  }

  private static abstract class ValuedefImpl {
    private String  datatype   = null;
    private boolean isNullable = false;
    private boolean isMultiple = false;

    ValuedefImpl(JsonNode valueDecl) {
      String datatype = getText(valueDecl.get("datatype"));
      if (datatype == null || datatype.length() == 0) {
        throw new IllegalArgumentException(
                "no datatype in parameter or return declaration: "+valueDecl.toString()
        );
      }
      this.datatype   = datatype;
      this.isNullable = getBoolean(valueDecl.get("nullable"), false);
      this.isMultiple = getBoolean(valueDecl.get("multiple"), false);
    }

    public String getDataType() {
      return datatype;
    }
    public boolean isNullable() {
      return isNullable;
    }
    public boolean isMultiple() {
      return isMultiple;
    }
  }
  private static class ReturndefImpl extends ValuedefImpl implements Returndef {
    private ServerTypeConverter typeConverter;
    private Format format;

    ReturndefImpl(JsonNode returnDecl) {
      super(returnDecl);
      String serverType = getDataType();
      typeConverter = returnConverters.get(serverType);
      if (typeConverter == null) {
        throw new IllegalArgumentException(
             "unknown data type in return declaration: "+returnDecl.toString()
        );
      }
      format = typeFormats.get(serverType);
    }

    ServerTypeConverter getTypeConverter() {
       return typeConverter;
    }
    Format getFormat() {
      return format;
    }
  }
  private static class ParamdefImpl extends ValuedefImpl implements Paramdef {
    private String         name;
    private BaseFieldifier fieldifier;

    ParamdefImpl(JsonNode paramDecl) {
      super(paramDecl);

      String paramName = getText(paramDecl.get("name"));
      if (paramName == null || paramName.length() == 0) {
        throw new IllegalArgumentException(
                "no name in parameter declaration: "+paramDecl.toString()
        );
      }
      this.name = paramName;

      this.fieldifier = paramFieldifiers.get(getDataType());
      if (fieldifier == null) {
        throw new IllegalArgumentException(
                "unknown data type in parameter declaration: "+paramDecl.toString()
        );
      }
    }

    @Override
    public String getParamName() {
      return name;
    }
    BaseFieldifier getFielder() {
      return fieldifier;
    }
  }

  private static boolean getBoolean(JsonNode property, boolean defaultValue) {
    if (property == null)
      return defaultValue;
    return property.asBoolean(defaultValue);
  }
  private static String getText(JsonNode property) {
    if (property == null)
      return null;
    return property.asText();
  }

  private static abstract class EndpointDefinerImpl implements EndpointDefiner {
    @Override
    public CallArgs args() {
      return new CallArgsImpl(getEndpoint());
    }
    @Override
    public CallArgs args(SessionState session) {
      return new CallArgsImpl(getEndpoint(), session);
    }

    abstract CallableEndpointImpl getEndpoint();
  }

  private static class NoneCallerImpl extends CallerImpl implements NoneCaller {
    NoneCallerImpl(CallableEndpointImpl endpoint) {
       super(endpoint);
     }

    @Override
    public void call() {
      call(args());
    }
    @Override
    public void call(CallArgs args) {
       if (!(args instanceof CallArgsImpl)) {
         throw new IllegalArgumentException("arguments not constructed by the builder");
       }
       startRequest((CallArgsImpl) args).responseNone();
    }

	@Override
	public CallBatcherBuilder<CallEvent> batcher() {
		// TODO Auto-generated method stub
		return null;
	}
  }
  private static class OneCallerImpl<R> extends CallerImpl implements OneCaller<R> {
    private ReturnConverter<R> converter;
    private Format             format;

    OneCallerImpl(CallableEndpointImpl endpoint, Format format, ReturnConverter<R> converter) {
      super(endpoint);
      this.converter = converter;
      this.format    = format;
    }

    @Override
    public R call() {
      return call(args());
    }
    @Override
    public R call(CallArgs args) {
      if (!(args instanceof CallArgsImpl)) {
        throw new IllegalArgumentException("arguments not constructed by the builder");
      }
      return converter.one(startRequest((CallArgsImpl) args).responseSingle(getEndpoint().isNullable(), format));
    }

	@Override
	public CallBatcherBuilder<CallOneEvent<R>> batcher() {
		// TODO Auto-generated method stub
		return null;
	}
  }
  private static class ManyCallerImpl<R> extends CallerImpl implements ManyCaller<R> {
    private ReturnConverter<R> converter;
    private Format             format;

    ManyCallerImpl(CallableEndpointImpl endpoint, Format format, ReturnConverter<R> converter) {
      super(endpoint);
      this.converter = converter;
      this.format    = format;
    }

    @Override
    public Stream<R> call() {
      return call(args());
    }
    @Override
    public Stream<R> call(CallArgs args) {
      if (!(args instanceof CallArgsImpl)) {
        throw new IllegalArgumentException("arguments not constructed by the builder");
      }
      return converter.many(startRequest((CallArgsImpl) args).responseMultiple(getEndpoint().isNullable(), format));
    }

	@Override
	public CallBatcherBuilder<CallManyEvent<R>> batcher() {
		// TODO Auto-generated method stub
		return null;
	}
  }
  private static abstract class CallerImpl extends EndpointDefinerImpl {
    private CallableEndpointImpl endpoint;
    // an uncloned cache
    private BaseProxy.DBFunctionRequest request;

    CallerImpl(CallableEndpointImpl endpoint) {
      this.endpoint = endpoint;
    }

    @Override
    public String getEndpointPath() {
      return endpoint.getEndpointPath();
    }
    @Override
    public Boolean isSessionRequired() {
      return endpoint.isSessionRequired();
    }
    @Override
    public Map<String, Paramdef> getParamdefs() {
      return endpoint.getParamdefs();
    }
    @Override
    public Returndef getReturndef() {
      return endpoint.getReturndef();
    }

    CallableEndpointImpl getEndpoint() {
      return endpoint;
    }
    BaseProxy.DBFunctionRequest startRequest(CallArgsImpl callArgs) {
      if (this.request == null) {
        // no concern about a race condition because any thread will cache the same fully built value
        BaseProxy.DBFunctionRequest request = endpoint.getBaseProxy()
                .request(endpoint.getModule(), endpoint.getParameterValuesKind());

        Paramdef sessiondef = endpoint.getSessiondef();
        request = (sessiondef == null) ?
                request.withSession() :
                request.withSession(sessiondef.getParamName(), callArgs.getSession(), sessiondef.isNullable());

        List<CallField> callFields = callArgs.getCallFields();
        int fieldSize = (callFields == null) ? 0 : callFields.size();
        Set<String> requiredParams = endpoint.getRequiredParams();
        if (fieldSize > 0) {
          Set<String> assignedParams = callArgs.getAssignedParams();
          if (requiredParams != null && !assignedParams.containsAll(requiredParams)) {
            throw new IllegalArgumentException(
                    endpoint.getModule()+" called without some required parameters: "+
                            requiredParams.stream().filter(assignedParams::contains).collect(Collectors.joining(", "))
            );
          }
          request = request.withParams(callFields.toArray(new CallField[fieldSize]));
        } else if (requiredParams != null) {
          throw new IllegalArgumentException(
                  endpoint.getModule()+" called without the required parameters: "+
                          requiredParams.stream().collect(Collectors.joining(", "))
          );
        }

        this.request = request.withMethod("POST");
      }

      return this.request;
    }
  }

  private static class CallArgsImpl implements CallArgs {
    private CallableEndpointImpl endpoint;
    private List<CallField>      callFields;
    private SessionState         session;
    private Set<String>          assignedParams;

    CallArgsImpl(CallableEndpointImpl endpoint) {
      this.endpoint = endpoint;
    }
    CallArgsImpl(CallableEndpointImpl endpoint, SessionState session) {
      this(endpoint);

      ParamdefImpl sessiondef = endpoint.getSessiondef();
      if (sessiondef == null) {
        throw new IllegalArgumentException(getEndpoint().getModule()+" does not support sessions");
      }
      this.session = session;
    }

    SessionState getSession() {
      return session;
    }
    List<CallField> getCallFields() {
      return this.callFields;
    }
    Set<String> getAssignedParams() {
      return assignedParams;
    }

    private CallableEndpointImpl getEndpoint() {
      return this.endpoint;
    }
    private ParamdefImpl getParamdef(String name) {
      if (name == null || name.length() == 0) {
        throw new IllegalArgumentException("empty parameter name");
      }
      ParamdefImpl paramdef = getEndpoint().getParamDef(name);
      if (paramdef == null) {
        throw new IllegalArgumentException("no parameter with name: "+name);
      }
      return paramdef;
    }

    private CallArgs addField(CallField field) {
      if (field == null) return this;

      if (getEndpoint().getRequiredParams() != null) {
        if (assignedParams == null) {
          assignedParams = new HashSet<>();
        }
        assignedParams.add(field.getParamName());
      }

      if (callFields == null) {
        callFields = new ArrayList<>();
      }
      callFields.add(field);

      return this;
    }

    @Override
    public CallArgs param(String name, AbstractWriteHandle value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, AbstractWriteHandle[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, BigDecimal value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, BigDecimal[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Boolean value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Boolean[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, byte[] value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, byte[][] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Date value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Date[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Document value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Document[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Double value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Double[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Duration value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Duration[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, File value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, File[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Float value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Float[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, InputSource value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, InputSource[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, InputStream value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, InputStream[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Integer value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Integer[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, JsonNode value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, JsonNode[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, JsonParser value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, JsonParser[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, LocalDate value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, LocalDate[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, LocalDateTime value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, LocalDateTime[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, LocalTime value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, LocalTime[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Long value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Long[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, OffsetDateTime value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, OffsetDateTime[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, OffsetTime value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, OffsetTime[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Reader value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Reader[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, Source value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, Source[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, String value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, String[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, XMLEventReader value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, XMLEventReader[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
    @Override
    public CallArgs param(String name, XMLStreamReader value) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), value));
    }
    @Override
    public CallArgs param(String name, XMLStreamReader[] values) {
      ParamdefImpl paramdef = getParamdef(name);
      return addField(paramdef.getFielder().field(name, paramdef.isNullable(), paramdef.isMultiple(), values));
    }
  }

  // conversion from request with Java data types to server data types
  private static abstract class BaseFieldifier {
    private String typeName;
    BaseFieldifier(String typeName) {
      this.typeName = typeName;
    }
    String getTypeName() {
      return typeName;
    }

    boolean isEmpty(String name, boolean nullable, Object value) {
      if (value == null) {
        if (!nullable) {
          throw new BaseProxy.RequiredParamException("null value for required parameter: " + name);
        }
        return true;
      }
      return false;
    }
    boolean isEmpty(String name, boolean nullable, Object[] values) {
      if (values == null || values.length == 0) {
        if (!nullable) {
          throw new BaseProxy.RequiredParamException("null value for required parameter: " + name);
        }
        return true;
      }
      return false;
    }
    boolean isMultiple(String name, boolean multiple, Object[] values) {
      if (values.length > 1) {
        if (!multiple) {
          throw new IllegalArgumentException("multiple values not accepted for "+name+" parameter");
        }
        return true;
      }
      return false;
    }

    // overridden where the Java input type is valid for the server data type
    CallField field(String name, boolean nullable, AbstractWriteHandle value) {
      throw new IllegalArgumentException("AbstractWriteHandle value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, AbstractWriteHandle[] values) {
      throw new IllegalArgumentException("AbstractWriteHandle values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, BigDecimal value) {
      throw new IllegalArgumentException("BigDecimal value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, BigDecimal[] values) {
      throw new IllegalArgumentException("BigDecimal values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, byte[] value) {
      throw new IllegalArgumentException("byte[] value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, byte[][] values) {
      throw new IllegalArgumentException("byte[] values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Boolean value) {
      throw new IllegalArgumentException("Boolean value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Boolean[] values) {
      throw new IllegalArgumentException("Boolean values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Date value) {
      throw new IllegalArgumentException("Date value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Date[] values) {
      throw new IllegalArgumentException("Date values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Document value) {
      throw new IllegalArgumentException("Document value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Document[] values) {
      throw new IllegalArgumentException("Document values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Double value) {
      throw new IllegalArgumentException("Double value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Double[] values) {
      throw new IllegalArgumentException("Double values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Duration value) {
      throw new IllegalArgumentException("Duration value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Duration[] values) {
      throw new IllegalArgumentException("Duration values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, File value) {
      throw new IllegalArgumentException("File value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, File[] values) {
      throw new IllegalArgumentException("File values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Float value) {
      throw new IllegalArgumentException("Float value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Float[] values) {
      throw new IllegalArgumentException("Float values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, InputSource value) {
      throw new IllegalArgumentException("InputSource value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, InputSource[] values) {
      throw new IllegalArgumentException("InputSource values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, InputStream value) {
      throw new IllegalArgumentException("InputStream value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, InputStream[] values) {
      throw new IllegalArgumentException("InputStream values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Integer value) {
      throw new IllegalArgumentException("Integer value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Integer[] values) {
      throw new IllegalArgumentException("Integer values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, JsonNode value) {
      throw new IllegalArgumentException("JsonNode value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, JsonNode[] values) {
      throw new IllegalArgumentException("JsonNode values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, JsonParser value) {
      throw new IllegalArgumentException("JsonParser value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, JsonParser[] values) {
      throw new IllegalArgumentException("JsonParser values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, LocalDate value) {
      throw new IllegalArgumentException("LocalDate value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, LocalDate[] values) {
      throw new IllegalArgumentException("LocalDate values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, LocalDateTime value) {
      throw new IllegalArgumentException("LocalDateTime value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, LocalDateTime[] values) {
      throw new IllegalArgumentException("LocalDateTime values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, LocalTime value) {
      throw new IllegalArgumentException("LocalTime value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, LocalTime[] values) {
      throw new IllegalArgumentException("LocalTime values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Long value) {
      throw new IllegalArgumentException("Long value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Long[] values) {
      throw new IllegalArgumentException("Long values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, OffsetDateTime value) {
      throw new IllegalArgumentException("OffsetDateTime value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, OffsetDateTime[] values) {
      throw new IllegalArgumentException("OffsetDateTime values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, OffsetTime value) {
      throw new IllegalArgumentException("OffsetTime value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, OffsetTime[] values) {
      throw new IllegalArgumentException("OffsetTime values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Reader value) {
      throw new IllegalArgumentException("Reader value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Reader[] values) {
      throw new IllegalArgumentException("Reader values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, Source value) {
      throw new IllegalArgumentException("Source value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, Source[] values) {
      throw new IllegalArgumentException("Source values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, String value) {
      throw new IllegalArgumentException("String value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, String[] values) {
      throw new IllegalArgumentException("String values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, XMLEventReader value) {
      throw new IllegalArgumentException("XMLEventReader value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, XMLEventReader[] values) {
      throw new IllegalArgumentException("XMLEventReader values not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, XMLStreamReader value) {
      throw new IllegalArgumentException("XMLStreamReader value not accepted for "+name+" parameter");
    }
    CallField field(String name, boolean nullable, boolean multiple, XMLStreamReader[] values) {
      throw new IllegalArgumentException("XMLStreamReader values not accepted for "+name+" parameter");
    }
  }
  private static abstract class AtomicFieldifier extends BaseFieldifier {
    AtomicFieldifier(String typeName) {
      super(typeName);
    }
    @Override
    CallField field(String name, boolean nullable, String value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, String[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, Stream.of(values)) :
              new SingleAtomicCallField(name, values[0]);
    }
  }
  private static abstract class NodeFieldifier extends BaseFieldifier {
    private Format format;
    NodeFieldifier(String typeName, Format format) {
      super(typeName);
      this.format = format;
    }
    Format getFormat() {
      return format;
    }
    AbstractWriteHandle format(AbstractWriteHandle value) {
      return NodeConverter.withFormat(value, getFormat());
    }
    Stream<? extends AbstractWriteHandle> formatAll(AbstractWriteHandle[] value) {
      for (AbstractWriteHandle handle: value) {
        NodeConverter.withFormat(handle, getFormat());
      }
      return Stream.of(value);
    }
    Stream<? extends AbstractWriteHandle> formatAll(Stream<? extends AbstractWriteHandle> value) {
      return NodeConverter.streamWithFormat(value, getFormat());
    }
    @Override
    CallField field(String name, boolean nullable, AbstractWriteHandle value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, format(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, AbstractWriteHandle[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleNodeCallField(name, formatAll(values)) :
              new SingleNodeCallField(name, format(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, byte[] value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, format(NodeConverter.BytesToHandle(value)));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, byte[][] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, formatAll(NodeConverter.BytesToHandle(Stream.of(values)))) :
              new SingleNodeCallField(name, format(NodeConverter.BytesToHandle(values[0])));
    }
    @Override
    CallField field(String name, boolean nullable, File value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, format(NodeConverter.FileToHandle(value)));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, File[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, formatAll(NodeConverter.FileToHandle(Stream.of(values)))) :
              new SingleNodeCallField(name, format(NodeConverter.FileToHandle(values[0])));
    }
    @Override
    CallField field(String name, boolean nullable, InputStream value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, format(NodeConverter.InputStreamToHandle(value)));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, InputStream[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, formatAll(NodeConverter.InputStreamToHandle(Stream.of(values)))) :
              new SingleNodeCallField(name, format(NodeConverter.InputStreamToHandle(values[0])));
    }
  }
  private static abstract class CharacterNodeFieldifier extends NodeFieldifier {
    CharacterNodeFieldifier(String typeName, Format format) {
      super(typeName, format);
    }
    @Override
    CallField field(String name, boolean nullable, Reader value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, format(NodeConverter.ReaderToHandle(value)));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Reader[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, formatAll(NodeConverter.ReaderToHandle(Stream.of(values)))) :
              new SingleNodeCallField(name, format(NodeConverter.ReaderToHandle(values[0])));
    }
    @Override
    CallField field(String name, boolean nullable, String value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, format(NodeConverter.StringToHandle(value)));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, String[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, formatAll(NodeConverter.StringToHandle(Stream.of(values)))) :
              new SingleNodeCallField(name, format(NodeConverter.StringToHandle(values[0])));
    }
  }
  private static class BooleanFieldifier extends AtomicFieldifier {
    BooleanFieldifier() {
      super(BaseProxy.BooleanType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Boolean value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.BooleanType.fromBoolean(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Boolean[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.BooleanType.fromBoolean(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.BooleanType.fromBoolean(values[0]));
    }
  }
  private static class DateFieldifier extends AtomicFieldifier {
    DateFieldifier() {
      super(BaseProxy.DateType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, LocalDate value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DateType.fromLocalDate(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, LocalDate[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DateType.fromLocalDate(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DateType.fromLocalDate(values[0]));
    }
  }
  private static class DateTimeFieldifier extends AtomicFieldifier {
    DateTimeFieldifier() {
      super(BaseProxy.DateTimeType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Date value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DateTimeType.fromDate(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Date[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DateTimeType.fromDate(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DateTimeType.fromDate(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, LocalDateTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DateTimeType.fromLocalDateTime(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, LocalDateTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DateTimeType.fromLocalDateTime(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DateTimeType.fromLocalDateTime(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, OffsetDateTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DateTimeType.fromOffsetDateTime(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, OffsetDateTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DateTimeType.fromOffsetDateTime(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DateTimeType.fromOffsetDateTime(values[0]));
    }
  }
  private static class DayTimeDurationFieldifier extends AtomicFieldifier {
     DayTimeDurationFieldifier() {
      super(BaseProxy.DayTimeDurationType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Duration value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DayTimeDurationType.fromDuration(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Duration[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DayTimeDurationType.fromDuration(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DayTimeDurationType.fromDuration(values[0]));
    }
  }
  private static class DecimalFieldifier extends AtomicFieldifier {
    DecimalFieldifier() {
      super(BaseProxy.DecimalType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, BigDecimal value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DecimalType.fromBigDecimal(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, BigDecimal[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DecimalType.fromBigDecimal(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DecimalType.fromBigDecimal(values[0]));
    }
  }
  private static class DoubleFieldifier extends AtomicFieldifier {
    DoubleFieldifier() {
      super(BaseProxy.DoubleType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Double value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.DoubleType.fromDouble(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Double[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.DoubleType.fromDouble(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.DoubleType.fromDouble(values[0]));
    }
  }
  private static class FloatFieldifier extends AtomicFieldifier {
    FloatFieldifier() {
      super(BaseProxy.FloatType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Float value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.FloatType.fromFloat(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Float[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.FloatType.fromFloat(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.FloatType.fromFloat(values[0]));
    }
  }
  private static class IntegerFieldifier extends AtomicFieldifier {
    IntegerFieldifier() {
      super(BaseProxy.IntegerType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Integer value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.IntegerType.fromInteger(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Integer[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.IntegerType.fromInteger(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.IntegerType.fromInteger(values[0]));
    }
  }
  static public class LongFieldifier extends AtomicFieldifier {
    LongFieldifier() {
      super(BaseProxy.LongType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Long value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.LongType.fromLong(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Long[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.LongType.fromLong(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.LongType.fromLong(values[0]));
    }
  }
  private static class StringFieldifier extends AtomicFieldifier {
    StringFieldifier() {
      super(BaseProxy.StringType.NAME);
    }
  }
  private static class TimeFieldifier extends AtomicFieldifier {
    TimeFieldifier() {
      super(BaseProxy.TimeType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, LocalTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.TimeType.fromLocalTime(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, LocalTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.TimeType.fromLocalTime(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.TimeType.fromLocalTime(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, OffsetTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.TimeType.fromOffsetTime(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, OffsetTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.TimeType.fromOffsetTime(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.TimeType.fromOffsetTime(values[0]));
    }
  }
  private static class UnsignedIntegerFieldifier extends AtomicFieldifier {
    UnsignedIntegerFieldifier() {
      super(BaseProxy.UnsignedIntegerType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Integer value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.UnsignedIntegerType.fromInteger(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Integer[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.UnsignedIntegerType.fromInteger(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.UnsignedIntegerType.fromInteger(values[0]));
    }
  }
  private static class UnsignedLongFieldifier extends AtomicFieldifier {
    UnsignedLongFieldifier() {
      super(BaseProxy.UnsignedLongType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Long value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleAtomicCallField(name, BaseProxy.UnsignedLongType.fromLong(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Long[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? new MultipleAtomicCallField(name, BaseProxy.UnsignedLongType.fromLong(Stream.of(values))) :
              new SingleAtomicCallField(name, BaseProxy.UnsignedLongType.fromLong(values[0]));
    }
  }
  private static class BinaryDocumentFieldifier extends NodeFieldifier {
    BinaryDocumentFieldifier() {
      super(BaseProxy.BinaryDocumentType.NAME, Format.BINARY);
    }
  }
  private static class JsonDocumentFieldifier extends CharacterNodeFieldifier {
    JsonDocumentFieldifier(String typeName) {
      super(typeName, Format.JSON);
    }
    JsonDocumentFieldifier() {
      this(BaseProxy.JsonDocumentType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, JsonNode value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.JsonDocumentType.fromJsonNode(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, JsonNode[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.JsonDocumentType.fromJsonNode(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.JsonDocumentType.fromJsonNode(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, JsonParser value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.JsonDocumentType.fromJsonParser(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, JsonParser[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.JsonDocumentType.fromJsonParser(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.JsonDocumentType.fromJsonParser(values[0]));
    }
  }
  static public class ArrayFieldifier extends JsonDocumentFieldifier {
    ArrayFieldifier() {
      super(BaseProxy.ArrayType.NAME);
    }
  }
  private static class ObjectFieldifier extends JsonDocumentFieldifier {
    ObjectFieldifier() {
      super(BaseProxy.ObjectType.NAME);
    }
  }
  private static class TextDocumentFieldifier extends CharacterNodeFieldifier {
    TextDocumentFieldifier() {
      super(BaseProxy.TextDocumentType.NAME, Format.TEXT);
    }
  }
  private static class XmlDocumentFieldifier extends CharacterNodeFieldifier {
    XmlDocumentFieldifier() {
      super(BaseProxy.XmlDocumentType.NAME, Format.XML);
    }
    @Override
    CallField field(String name, boolean nullable, Document value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromDocument(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Document[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.XmlDocumentType.fromDocument(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromDocument(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, InputSource value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromInputSource(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, InputSource[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.XmlDocumentType.fromInputSource(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromInputSource(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, Source value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromSource(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Source[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.XmlDocumentType.fromSource(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromSource(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, XMLEventReader value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromXMLEventReader(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, XMLEventReader[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.XmlDocumentType.fromXMLEventReader(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromXMLEventReader(values[0]));
    }
    @Override
    CallField field(String name, boolean nullable, XMLStreamReader value) {
      if (isEmpty(name, nullable, value)) return null;
      return new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromXMLStreamReader(value));
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, XMLStreamReader[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ?
              new MultipleNodeCallField(name, BaseProxy.XmlDocumentType.fromXMLStreamReader(Stream.of(values))) :
              new SingleNodeCallField(name, BaseProxy.XmlDocumentType.fromXMLStreamReader(values[0]));
    }
  }

  // conversion from response with server data types to Java data types
  interface ReturnConverter<T> {
    T         one(SingleCallResponse response);
    Stream<T> many(MultipleCallResponse response);
  }

  static abstract class ServerTypeConverter {
    final static ReturnConverter<String> toStringConverter = new ReturnConverter<String>() {
      @Override
      public String one(SingleCallResponse response) {
        return response.asString();
      }
      @Override
      public Stream<String> many(MultipleCallResponse response) {
        return response.asStreamOfString();
      }
    };

    Map<Class<?>, ReturnConverter<?>> converters;
    String serverType;

    ServerTypeConverter(String serverType) {
      this.serverType = serverType;
      converters = new HashMap<>();
    }

    protected <T> void put(Class<T> key, ReturnConverter<T> value) {
      converters.put(key, value);
    }
    void putTo(Map<String, ? super ServerTypeConverter> map) {
      map.put(serverType, this);
    }
    <T> ReturnConverter<T> forClientType(Class<T> as) {
      ReturnConverter<?> converter = converters.get(as);
      if (converter == null) {
        throw new IllegalArgumentException(
                "cannot convert server type "+serverType+" to client type "+as.getCanonicalName()
        );
      }
      return (ReturnConverter<T>) converter;
    }
  }
  static abstract class AtomicTypeConverter extends ServerTypeConverter {
    AtomicTypeConverter(String serverType) {
      super(serverType);
      put(String.class, toStringConverter);
    }
  }
  static abstract class NodeTypeConverter extends ServerTypeConverter {
    NodeTypeConverter(String serverType) {
      super(serverType);
      put(byte[].class, new ReturnConverter<byte[]>() {
        @Override
        public byte[] one(SingleCallResponse response) {
          return response.asBytes();
        }
        @Override
        public Stream<byte[]> many(MultipleCallResponse response) {
          return response.asStreamOfBytes();
        }
      });
/* TODO: related to #1055
      put(File.class, new ReturnConverter<File>() {
        @Override
        public File one(SingleCallResponse response) {
          return null;
        }
        @Override
        public Stream<File> many(MultipleCallResponse response) {
          return null;
        }
      });
 */
      put(InputStream.class, new ReturnConverter<InputStream>() {
        @Override
        public InputStream one(SingleCallResponse response) {
          return response.asInputStream();
        }
        @Override
        public Stream<InputStream> many(MultipleCallResponse response) {
          return response.asStreamOfInputStream();
        }
      });
    }
  }
  static abstract class CharacterNodeTypeConverter extends NodeTypeConverter {
    CharacterNodeTypeConverter(String serverType) {
      super(serverType);
      put(Reader.class, new ReturnConverter<Reader>() {
        @Override
        public Reader one(SingleCallResponse response) {
          return response.asReader();
        }
        @Override
        public Stream<Reader> many(MultipleCallResponse response) {
          return response.asStreamOfReader();
        }
      });
      put(String.class, toStringConverter);
    }
  }
  private static class BooleanTypeConverter extends AtomicTypeConverter {
    BooleanTypeConverter() {
      super(BaseProxy.BooleanType.NAME);
      put(Boolean.class, new ReturnConverter<Boolean>() {
        @Override
        public Boolean one(SingleCallResponse response) {
          return BaseProxy.BooleanType.toBoolean(response);
        }
        @Override
        public Stream<Boolean> many(MultipleCallResponse response) {
          return BaseProxy.BooleanType.toBoolean(response);
        }
      });
    }
  }
  private static class DateTypeConverter extends AtomicTypeConverter {
    DateTypeConverter() {
      super(BaseProxy.DateType.NAME);
      put(LocalDate.class, new ReturnConverter<LocalDate>() {
        @Override
        public LocalDate one(SingleCallResponse response) {
          return BaseProxy.DateType.toLocalDate(response);
        }
        @Override
        public Stream<LocalDate> many(MultipleCallResponse response) {
          return BaseProxy.DateType.toLocalDate(response);
        }
      });
    }
  }
  private static class DateTimeTypeConverter extends AtomicTypeConverter {
    DateTimeTypeConverter() {
      super(BaseProxy.DateTimeType.NAME);
      put(Date.class, new ReturnConverter<Date>() {
        @Override
        public Date one(SingleCallResponse response) {
          return BaseProxy.DateTimeType.toDate(response);
        }
        @Override
        public Stream<Date> many(MultipleCallResponse response) {
          return BaseProxy.DateTimeType.toDate(response);
        }
      });
      put(LocalDateTime.class, new ReturnConverter<LocalDateTime>() {
        @Override
        public LocalDateTime one(SingleCallResponse response) {
          return BaseProxy.DateTimeType.toLocalDateTime(response);
        }
        @Override
        public Stream<LocalDateTime> many(MultipleCallResponse response) {
          return BaseProxy.DateTimeType.toLocalDateTime(response);
        }
      });
      put(OffsetDateTime.class, new ReturnConverter<OffsetDateTime>() {
        @Override
        public OffsetDateTime one(SingleCallResponse response) {
          return BaseProxy.DateTimeType.toOffsetDateTime(response);
        }
        @Override
        public Stream<OffsetDateTime> many(MultipleCallResponse response) {
          return BaseProxy.DateTimeType.toOffsetDateTime(response);
        }
      });
    }
  }
  private static class DayTimeDurationTypeConverter extends AtomicTypeConverter {
    DayTimeDurationTypeConverter() {
      super(BaseProxy.DayTimeDurationType.NAME);
      put(Duration.class, new ReturnConverter<Duration>() {
        @Override
        public Duration one(SingleCallResponse response) {
          return BaseProxy.DayTimeDurationType.toDuration(response);
        }
        @Override
        public Stream<Duration> many(MultipleCallResponse response) {
          return BaseProxy.DayTimeDurationType.toDuration(response);
        }
      });
    }
  }
  private static class DecimalTypeConverter extends AtomicTypeConverter {
    DecimalTypeConverter() {
      super(BaseProxy.DecimalType.NAME);
      put(BigDecimal.class, new ReturnConverter<BigDecimal>() {
        @Override
        public BigDecimal one(SingleCallResponse response) {
          return BaseProxy.DecimalType.toBigDecimal(response);
        }
        @Override
        public Stream<BigDecimal> many(MultipleCallResponse response) {
          return BaseProxy.DecimalType.toBigDecimal(response);
        }
      });
    }
  }
  private static class DoubleTypeConverter extends AtomicTypeConverter {
    DoubleTypeConverter() {
      super(BaseProxy.DoubleType.NAME);
      put(Double.class, new ReturnConverter<Double>() {
        @Override
        public Double one(SingleCallResponse response) {
          return BaseProxy.DoubleType.toDouble(response);
        }
        @Override
        public Stream<Double> many(MultipleCallResponse response) {
          return BaseProxy.DoubleType.toDouble(response);
        }
      });
    }
  }
  private static class FloatTypeConverter extends AtomicTypeConverter {
    FloatTypeConverter() {
      super(BaseProxy.FloatType.NAME);
      put(Float.class, new ReturnConverter<Float>() {
        @Override
        public Float one(SingleCallResponse response) {
          return BaseProxy.FloatType.toFloat(response);
        }
        @Override
        public Stream<Float> many(MultipleCallResponse response) {
          return BaseProxy.FloatType.toFloat(response);
        }
      });
    }
  }
  private static class IntegerTypeConverter extends AtomicTypeConverter {
    IntegerTypeConverter() {
      super(BaseProxy.IntegerType.NAME);
      put(Integer.class, new ReturnConverter<Integer>() {
        @Override
        public Integer one(SingleCallResponse response) {
          return BaseProxy.IntegerType.toInteger(response);
        }
        @Override
        public Stream<Integer> many(MultipleCallResponse response) {
          return BaseProxy.IntegerType.toInteger(response);
        }
      });
    }
  }
  private static class LongTypeConverter extends AtomicTypeConverter {
    LongTypeConverter() {
      super(BaseProxy.LongType.NAME);
      put(Long.class, new ReturnConverter<Long>() {
        @Override
        public Long one(SingleCallResponse response) {
          return BaseProxy.LongType.toLong(response);
        }
        @Override
        public Stream<Long> many(MultipleCallResponse response) {
          return BaseProxy.LongType.toLong(response);
        }
      });
    }
  }
  private static class StringTypeConverter extends AtomicTypeConverter {
    StringTypeConverter() {
      super(BaseProxy.StringType.NAME);
    }
  }
  private static class TimeTypeConverter extends AtomicTypeConverter {
    TimeTypeConverter() {
      super(BaseProxy.TimeType.NAME);
      put(LocalTime.class, new ReturnConverter<LocalTime>() {
        @Override
        public LocalTime one(SingleCallResponse response) {
          return BaseProxy.TimeType.toLocalTime(response);
        }
        @Override
        public Stream<LocalTime> many(MultipleCallResponse response) {
          return BaseProxy.TimeType.toLocalTime(response);
        }
      });
      put(OffsetTime.class, new ReturnConverter<OffsetTime>() {
        @Override
        public OffsetTime one(SingleCallResponse response) {
          return BaseProxy.TimeType.toOffsetTime(response);
        }
        @Override
        public Stream<OffsetTime> many(MultipleCallResponse response) {
          return BaseProxy.TimeType.toOffsetTime(response);
        }
      });
    }
  }
  private static class UnsignedIntegerTypeConverter extends AtomicTypeConverter {
    UnsignedIntegerTypeConverter() {
      super(BaseProxy.UnsignedIntegerType.NAME);
      put(Integer.class, new ReturnConverter<Integer>() {
        @Override
        public Integer one(SingleCallResponse response) {
          return BaseProxy.UnsignedIntegerType.toInteger(response);
        }
        @Override
        public Stream<Integer> many(MultipleCallResponse response) {
          return BaseProxy.UnsignedIntegerType.toInteger(response);
        }
      });
    }
  }
  private static class UnsignedLongTypeConverter extends AtomicTypeConverter {
    UnsignedLongTypeConverter() {
      super(BaseProxy.UnsignedLongType.NAME);
      put(Long.class, new ReturnConverter<Long>() {
        @Override
        public Long one(SingleCallResponse response) {
          return BaseProxy.UnsignedLongType.toLong(response);
        }
        @Override
        public Stream<Long> many(MultipleCallResponse response) {
          return BaseProxy.UnsignedLongType.toLong(response);
        }
      });
    }
  }
  private static class BinaryDocumentTypeConverter extends NodeTypeConverter {
    BinaryDocumentTypeConverter() {
      super(BaseProxy.BinaryDocumentType.NAME);
      put(BinaryReadHandle.class, new ReturnConverter<BinaryReadHandle>() {
        @Override
        public BinaryReadHandle one(SingleCallResponse response) {
          return BaseProxy.BinaryDocumentType.toBinaryReadHandle(response);
        }
        @Override
        public Stream<BinaryReadHandle> many(MultipleCallResponse response) {
          return BaseProxy.BinaryDocumentType.toBinaryReadHandle(response);
        }
      });
    }
  }
  private static class JsonDocumentTypeConverter extends CharacterNodeTypeConverter {
    JsonDocumentTypeConverter() {
      super(BaseProxy.JsonDocumentType.NAME);
      put(JSONReadHandle.class, new ReturnConverter<JSONReadHandle>() {
        @Override
        public JSONReadHandle one(SingleCallResponse response) {
          return BaseProxy.JsonDocumentType.toJSONReadHandle(response);
        }
        @Override
        public Stream<JSONReadHandle> many(MultipleCallResponse response) {
          return BaseProxy.JsonDocumentType.toJSONReadHandle(response);
        }
      });
      put(JsonNode.class, new ReturnConverter<JsonNode>() {
        @Override
        public JsonNode one(SingleCallResponse response) {
          return BaseProxy.JsonDocumentType.toJsonNode(response);
        }
        @Override
        public Stream<JsonNode> many(MultipleCallResponse response) {
          return BaseProxy.JsonDocumentType.toJsonNode(response);
        }
      });
      put(JsonParser.class, new ReturnConverter<JsonParser>() {
        @Override
        public JsonParser one(SingleCallResponse response) {
          return BaseProxy.JsonDocumentType.toJsonParser(response);
        }
        @Override
        public Stream<JsonParser> many(MultipleCallResponse response) {
          return BaseProxy.JsonDocumentType.toJsonParser(response);
        }
      });
    }
  }
  private static class TextDocumentTypeConverter extends CharacterNodeTypeConverter {
    TextDocumentTypeConverter() {
      super(BaseProxy.TextDocumentType.NAME);
      put(TextReadHandle.class, new ReturnConverter<TextReadHandle>() {
        @Override
        public TextReadHandle one(SingleCallResponse response) {
          return BaseProxy.TextDocumentType.toTextReadHandle(response);
        }
        @Override
        public Stream<TextReadHandle> many(MultipleCallResponse response) {
          return BaseProxy.TextDocumentType.toTextReadHandle(response);
        }
      });
    }
  }
  private static class XMLDocumentTypeConverter extends CharacterNodeTypeConverter {
    XMLDocumentTypeConverter() {
      super(BaseProxy.XmlDocumentType.NAME);
      put(XMLReadHandle.class, new ReturnConverter<XMLReadHandle>() {
        @Override
        public XMLReadHandle one(SingleCallResponse response) {
          return BaseProxy.XmlDocumentType.toXMLReadHandle(response);
        }
        @Override
        public Stream<XMLReadHandle> many(MultipleCallResponse response) {
          return BaseProxy.XmlDocumentType.toXMLReadHandle(response);
        }
      });
      put(Document.class, new ReturnConverter<Document>() {
        @Override
        public Document one(SingleCallResponse response) {
          return BaseProxy.XmlDocumentType.toDocument(response);
        }
        @Override
        public Stream<Document> many(MultipleCallResponse response) {
          return BaseProxy.XmlDocumentType.toDocument(response);
        }
      });
      put(InputSource.class, new ReturnConverter<InputSource>() {
        @Override
        public InputSource one(SingleCallResponse response) {
          return BaseProxy.XmlDocumentType.toInputSource(response);
        }
        @Override
        public Stream<InputSource> many(MultipleCallResponse response) {
          return BaseProxy.XmlDocumentType.toInputSource(response);
        }
      });
      put(Source.class, new ReturnConverter<Source>() {
        @Override
        public Source one(SingleCallResponse response) {
          return BaseProxy.XmlDocumentType.toSource(response);
        }
        @Override
        public Stream<Source> many(MultipleCallResponse response) {
          return BaseProxy.XmlDocumentType.toSource(response);
        }
      });
      put(XMLEventReader.class, new ReturnConverter<XMLEventReader>() {
        @Override
        public XMLEventReader one(SingleCallResponse response) {
          return BaseProxy.XmlDocumentType.toXMLEventReader(response);
        }
        @Override
        public Stream<XMLEventReader> many(MultipleCallResponse response) {
          return BaseProxy.XmlDocumentType.toXMLEventReader(response);
        }
      });
      put(XMLStreamReader.class, new ReturnConverter<XMLStreamReader>() {
        @Override
        public XMLStreamReader one(SingleCallResponse response) {
          return BaseProxy.XmlDocumentType.toXMLStreamReader(response);
        }
        @Override
        public Stream<XMLStreamReader> many(MultipleCallResponse response) {
          return BaseProxy.XmlDocumentType.toXMLStreamReader(response);
        }
      });
    }
  }
}
