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
package com.marklogic.client.dataservices.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.SessionState;
import com.marklogic.client.datamovement.impl.BatchEventImpl;
import com.marklogic.client.dataservices.CallBatcher;
import com.marklogic.client.dataservices.CallManager;
import com.marklogic.client.impl.BaseProxy;
import com.marklogic.client.impl.NodeConverter;
import com.marklogic.client.impl.RESTServices.CallField;
import com.marklogic.client.impl.RESTServices.UnbufferedMultipleAtomicCallField;
import com.marklogic.client.impl.RESTServices.BufferedMultipleAtomicCallField;
import com.marklogic.client.impl.RESTServices.MultipleCallResponse;
import com.marklogic.client.impl.RESTServices.UnbufferedMultipleNodeCallField;
import com.marklogic.client.impl.RESTServices.BufferedMultipleNodeCallField;
import com.marklogic.client.impl.RESTServices.SingleAtomicCallField;
import com.marklogic.client.impl.RESTServices.SingleCallResponse;
import com.marklogic.client.impl.RESTServices.BufferedSingleNodeCallField;
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

public class CallManagerImpl implements CallManager {
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

  public CallManagerImpl(DatabaseClient client) {
    if (client == null) {
      throw new IllegalArgumentException("cannot construct CallManager with null database primaryClient");
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
    } else if (!"mjs".equals(extension) && !"sjs".equals(extension) && !"xqy".equals(extension)) {
      throw new IllegalArgumentException("extension must be mjs, sjs, or xqy: "+extension);
    }

    return new CallableEndpointImpl(client, serviceDecl, endpointDecl, extension);
   }

   static class CallableEndpointImpl extends EndpointDefinerImpl implements CallableEndpoint {
     private DatabaseClient            primaryClient;
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
        this.primaryClient = client;

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
     DatabaseClient getPrimaryClient() {
       return primaryClient;
     }

     String getEndpointDirectory() {
         return endpointDirectory;
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

	@Override
	public boolean isSameEndpoint(CallableEndpoint other) {
		if(other!=null && (other.equals(this) || other.equals(this.getEndpoint())))
			return true;
		return false;
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
  static class ReturndefImpl extends ValuedefImpl implements Returndef {
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
  static class ParamdefImpl extends ValuedefImpl implements Paramdef {
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

  static abstract class EndpointDefinerImpl implements EndpointDefiner {
    @Override
    public CallArgsImpl args() {
      return new CallArgsImpl(getEndpoint());
    }
    @Override
    public CallArgsImpl args(SessionState session) {
      return new CallArgsImpl(getEndpoint(), session);
    }

    abstract CallableEndpointImpl getEndpoint();
  }

  static class NoneCallerImpl extends CallerImpl<CallBatcher.CallEvent> implements NoneCaller {
    NoneCallerImpl(CallableEndpointImpl endpoint) {
       super(endpoint);
     }

    @Override
    public void call() {
      call(args());
    }
    @Override
    public void call(CallArgs args) {
      callImpl(getEndpoint().getPrimaryClient(), args);
    }
    private void callImpl(DatabaseClient client, CallArgs args) {
      if (!(args instanceof CallArgsImpl)) {
        throw new IllegalArgumentException("arguments not constructed by the builder");
      }
      startRequest(client, (CallArgsImpl) args).responseNone();
    }
    @Override
	public CallBatcher.CallBatcherBuilder<CallBatcher.CallEvent> batcher() {
      return new CallBatcherImpl.BuilderImpl(getEndpoint().getPrimaryClient(), this);
	}
    @Override
    public CallBatcher.CallEvent callForEvent(DatabaseClient client, CallArgs args) throws Exception {
      callImpl(client, args);
      return new CallBatcherImpl.CallEventImpl(client, args);
    }
  }
  private static class OneCallerImpl<R> extends CallerImpl<CallBatcher.OneCallEvent<R>> implements OneCaller<R> {
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
      return callImpl(getEndpoint().getPrimaryClient(), args);
    }
    private R callImpl(DatabaseClient client, CallArgs args) {
      if (!(args instanceof CallArgsImpl)) {
        throw new IllegalArgumentException("arguments not constructed by the builder");
      }
      return converter.one(startRequest(client, (CallArgsImpl) args).responseSingle(getEndpoint().isNullable(), format));
    }
    @Override
	public CallBatcher.CallBatcherBuilder<CallBatcher.OneCallEvent<R>> batcher() {
      return new CallBatcherImpl.BuilderImpl(getEndpoint().getPrimaryClient(),this);
	}
    @Override
    public CallBatcher.OneCallEvent<R> callForEvent(DatabaseClient client, CallArgs args) throws Exception {
      return new CallBatcherImpl.OneCallEventImpl(client, args, callImpl(client, args));
    }
  }
  static class ManyCallerImpl<R> extends CallerImpl<CallBatcher.ManyCallEvent<R>> implements ManyCaller<R> {
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
      return callImpl(getEndpoint().getPrimaryClient(), args);
    }
    private Stream<R> callImpl(DatabaseClient client, CallArgs args) {
      if (!(args instanceof CallArgsImpl)) {
        throw new IllegalArgumentException("arguments not constructed by the builder");
      }
      return converter.many(startRequest(client, (CallArgsImpl) args).responseMultiple(getEndpoint().isNullable(), format));
    }
    @Override
	public CallBatcher.CallBatcherBuilder<CallBatcher.ManyCallEvent<R>> batcher() {
      return new CallBatcherImpl.BuilderImpl(getEndpoint().getPrimaryClient(),this);
	}
    @Override
    public CallBatcher.ManyCallEvent<R> callForEvent(DatabaseClient client, CallArgs args) throws Exception {
      return new CallBatcherImpl.ManyCallEventImpl(client, args, callImpl(client, args));
    }
  }

  interface EventedCaller<E extends CallBatcher.CallEvent> extends EndpointDefiner {
    E callForEvent(DatabaseClient client, CallArgs args) throws Exception;
  }
  static abstract class CallerImpl<E extends CallBatcher.CallEvent> extends EndpointDefinerImpl implements EventedCaller<E> {
    private CallableEndpointImpl endpoint;
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

    void checkArgs(CallArgsImpl callArgs) {
      Map<String, CallField> callFields = callArgs.getCallFields();
      Set<String> requiredParams = endpoint.getRequiredParams();
      if (callFields != null && callFields.size() > 0) {
        Set<String> assignedParams = callFields.keySet();
        if (requiredParams != null && !assignedParams.containsAll(requiredParams)) {
          throw new IllegalArgumentException(
                  endpoint.getModule()+" called without some required parameters: "+
                          requiredParams.stream().filter(item-> !(assignedParams.contains(item))).collect(Collectors.joining(", "))
          );
        }
      } else if (requiredParams != null) {
        throw new IllegalArgumentException(
                endpoint.getModule()+" called without the required parameters: "+
                        requiredParams.stream().collect(Collectors.joining(", "))
        );
      }
    }
    BaseProxy.DBFunctionRequest startRequest(DatabaseClient client, CallArgsImpl callArgs) {
      checkArgs(callArgs);
      return makeRequest(client, callArgs);
    }
    BaseProxy.DBFunctionRequest makeRequest(DatabaseClient client, CallArgsImpl callArgs) {
      BaseProxy.DBFunctionRequest request = BaseProxy.request(
          client, endpoint.getEndpointDirectory(), endpoint.getModule(), endpoint.getParameterValuesKind()
      );

      Paramdef sessiondef = endpoint.getSessiondef();
      request = (sessiondef == null) ?
              request.withSession() :
              request.withSession(sessiondef.getParamName(), callArgs.getSession(), sessiondef.isNullable());

      Map<String,CallField> callFields = callArgs.getCallFields();
      int fieldSize = (callFields == null) ? 0 : callFields.size();
      if (fieldSize > 0) {
          request = request.withParams(callFields.values().toArray(new CallField[fieldSize]));
      }

      return request.withMethod("POST");
    }
  }

  static class CallArgsImpl implements CallArgs {
    private CallableEndpointImpl  endpoint;
    private Map<String,CallField> callFields;
    private SessionState          session;

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
    CallArgsImpl(CallableEndpointImpl endpoint, Map<String,CallField> callFields) {
        this(endpoint);
        this.callFields = callFields;
    }

    SessionState getSession() {
      return session;
    }
    Map<String,CallField> getCallFields() {
      return this.callFields;
    }

    CallableEndpointImpl getEndpoint() {
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

      if (callFields == null) {
        callFields = new HashMap<>();
      }
      callFields.put(field.getParamName(), field);

      return this;
    }

    @Override
    public String[] getAssignedParamNames() {
      return callFields.keySet().toArray(new String[callFields.size()]);
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
  static abstract class ParamFieldifier<T> {
    private String name;
    ParamFieldifier(String name) {
      this.name = name;
    }
    String getName() {
      return name;
    }
    abstract CallField field(T value);
    abstract CallField field(T[] values);
    abstract CallField field(Stream<T> values);
  }
  static abstract class BaseFieldifier {
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
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      if (type == null) {
        throw new IllegalArgumentException("null type for "+name+" parameter");

      }
      throw new IllegalArgumentException(
          "Cannot construct parameter of type "+type.getCanonicalName()+" for "+name+" parameter"
      );
    }
  }
  private static abstract class AtomicFieldifier extends BaseFieldifier {
    AtomicFieldifier(String typeName) {
      super(typeName);
    }
    @Override
    CallField field(String name, boolean nullable, String value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, String[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return String.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Stringifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, String value) {
      return new SingleAtomicCallField(name, value);
    }
    CallField field(String name, String[] values) {
      return new BufferedMultipleAtomicCallField(name, values);
    }
    CallField field(String name, Stream<String> values) {
      return new UnbufferedMultipleAtomicCallField(name, values);
    }
    private static class Stringifier extends ParamFieldifier<String> {
      private AtomicFieldifier fieldifier;
      Stringifier(String name, AtomicFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(String value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(String[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<String> values) {
        return fieldifier.field(getName(), values);
      }
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
    BufferableHandle[] formatAll(AbstractWriteHandle[] value) {
        BufferableHandle[] bufferableHandles = new BufferableHandle[value.length];
      for (int i=0; i<value.length; i++) {
          if(!(value[i] instanceof BufferableHandle))
              throw new IllegalArgumentException("AbstractWriteHandle value is not an instance of BufferableHandle.");
        NodeConverter.withFormat(value[i], getFormat());
        bufferableHandles[i] = (BufferableHandle) value[i];
      }
      return bufferableHandles;
    }
    Stream<? extends AbstractWriteHandle> formatAll(Stream<? extends AbstractWriteHandle> value) {
      return NodeConverter.streamWithFormat(value, getFormat());
    }
    @Override
    CallField field(String name, boolean nullable, AbstractWriteHandle value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, AbstractWriteHandle[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, byte[] value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, byte[][] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, File value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, File[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, InputStream value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, InputStream[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return AbstractWriteHandle.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new AbstractWriteHandlifier(name, this) :
             byte[].class.isAssignableFrom(type)              ? (ParamFieldifier<T>) new Bytesifier(name, this) :
             File.class.isAssignableFrom(type)                ? (ParamFieldifier<T>) new Filifier(name, this) :
             InputStream.class.isAssignableFrom(type)         ? (ParamFieldifier<T>) new InputStreamifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, AbstractWriteHandle value) {
      return formattedField(name, format(value));
    }
    CallField formattedField(String name, AbstractWriteHandle value) {
        if(!(value instanceof BufferableHandle))
            throw new IllegalArgumentException("AbstractWriteHandle value is not an instance of BufferableHandle.");
      return new BufferedSingleNodeCallField(name, (BufferableHandle) value);
    }
    CallField formattedField(String name, BufferableHandle[] values) {
        return new BufferedMultipleNodeCallField(name, values);
    }
    CallField field(String name, AbstractWriteHandle[] values) {
      return formattedField(name, formatAll(values));
    }
    CallField field(String name, Stream<? extends AbstractWriteHandle> values) {
      return formattedField(name, formatAll(values));
    }
    CallField formattedField(String name, Stream<? extends AbstractWriteHandle> values) {
      return new UnbufferedMultipleNodeCallField(name, values);
    }
    CallField field(String name, byte[] value) {
      return field(name, NodeConverter.BytesToHandle(value));
    }
    CallField field(String name, byte[][] values) {
      return field(name, NodeConverter.BytesToHandle(Stream.of(values)));
    }
    CallField field(String name, File value) {
      return field(name, NodeConverter.FileToHandle(value));
    }
    CallField field(String name, File[] values) {
      return field(name, NodeConverter.FileToHandle(Stream.of(values)));
    }
    CallField field(String name, InputStream value) {
      return field(name, NodeConverter.InputStreamToHandle(value));
    }
    CallField field(String name, InputStream[] values) {
      return field(name, NodeConverter.InputStreamToHandle(Stream.of(values)));
    }
    private static class AbstractWriteHandlifier extends ParamFieldifier<AbstractWriteHandle> {
      private NodeFieldifier fieldifier;
      AbstractWriteHandlifier(String name, NodeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(AbstractWriteHandle value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(AbstractWriteHandle[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<AbstractWriteHandle> values) {
        return fieldifier.field(getName(), values);
      }
    }
    private static class Bytesifier extends ParamFieldifier<byte[]> {
      private NodeFieldifier fieldifier;
      Bytesifier(String name, NodeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(byte[] value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(byte[][] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<byte[]> values) {
        return fieldifier.field(getName(), NodeConverter.BytesToHandle(values));
      }
    }
    private static class Filifier extends ParamFieldifier<File> {
      private NodeFieldifier fieldifier;
      Filifier(String name, NodeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(File value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(File[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<File> values) {
        return fieldifier.field(getName(), NodeConverter.FileToHandle(values));
      }
    }
    private static class InputStreamifier extends ParamFieldifier<InputStream> {
      private NodeFieldifier fieldifier;
      InputStreamifier(String name, NodeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(InputStream value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(InputStream[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<InputStream> values) {
        return fieldifier.field(getName(), NodeConverter.InputStreamToHandle(values));
      }
    }
  }
  private static abstract class CharacterNodeFieldifier extends NodeFieldifier {
    CharacterNodeFieldifier(String typeName, Format format) {
      super(typeName, format);
    }
    @Override
    CallField field(String name, boolean nullable, Reader value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Reader[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, String value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, String[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Reader.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Readerfier(name, this) :
             String.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Stringifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Reader value) {
      return field(name, NodeConverter.ReaderToHandle(value));
    }
    CallField field(String name, Reader[] values) {
      return field(name, NodeConverter.ReaderToHandle(Stream.of(values)));
    }
    CallField field(String name, String value) {
      return field(name, NodeConverter.StringToHandle(value));
    }
    CallField field(String name, String[] values) {
      return field(name, NodeConverter.StringToHandle(Stream.of(values)));
    }
    private static class Readerfier extends ParamFieldifier<Reader> {
      private CharacterNodeFieldifier fieldifier;
      Readerfier(String name, CharacterNodeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Reader value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Reader[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Reader> values) {
        return fieldifier.field(getName(), NodeConverter.ReaderToHandle(values));
      }
    }
    private static class Stringifier extends ParamFieldifier<String> {
      private CharacterNodeFieldifier fieldifier;
      Stringifier(String name, CharacterNodeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(String value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(String[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<String> values) {
        return fieldifier.field(getName(), NodeConverter.StringToHandle(values));
      }
    }
  }
  private static class BooleanFieldifier extends AtomicFieldifier {
    BooleanFieldifier() {
      super(BaseProxy.BooleanType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Boolean value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Boolean[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Boolean.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Booleanifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Boolean value) {
      return field(name, BaseProxy.BooleanType.fromBoolean(value));
    }
    CallField field(String name, Boolean[] values) {
      return field(name, BaseProxy.BooleanType.fromBoolean(Stream.of(values)));
    }
    private static class Booleanifier extends ParamFieldifier<Boolean> {
      private BooleanFieldifier fieldifier;
      Booleanifier(String name, BooleanFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Boolean value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Boolean[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Boolean> values) {
        return fieldifier.field(getName(), BaseProxy.BooleanType.fromBoolean(values));
      }
    }
  }
  private static class DateFieldifier extends AtomicFieldifier {
    DateFieldifier() {
      super(BaseProxy.DateType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, LocalDate value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, LocalDate[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return LocalDate.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new LocalDatifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, LocalDate value) {
      return field(name, BaseProxy.DateType.fromLocalDate(value));
    }
    CallField field(String name, LocalDate[] values) {
      return field(name, BaseProxy.DateType.fromLocalDate(Stream.of(values)));
    }
    private static class LocalDatifier extends ParamFieldifier<LocalDate> {
      private DateFieldifier fieldifier;
      LocalDatifier(String name, DateFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(LocalDate value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(LocalDate[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<LocalDate> values) {
        return fieldifier.field(getName(), BaseProxy.DateType.fromLocalDate(values));
      }
    }
  }
  private static class DateTimeFieldifier extends AtomicFieldifier {
    DateTimeFieldifier() {
      super(BaseProxy.DateTimeType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Date value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Date[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, LocalDateTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, LocalDateTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, OffsetDateTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, OffsetDateTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Date.class.isAssignableFrom(type)           ? (ParamFieldifier<T>) new Datifier(name, this) :
             LocalDateTime.class.isAssignableFrom(type)  ? (ParamFieldifier<T>) new LocalDateTimifier(name, this) :
             OffsetDateTime.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new OffsetDateTimifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Date value) {
      return field(name, BaseProxy.DateTimeType.fromDate(value));
    }
    CallField field(String name, Date[] values) {
      return field(name, BaseProxy.DateTimeType.fromDate(Stream.of(values)));
    }
    CallField field(String name, LocalDateTime value) {
      return field(name, BaseProxy.DateTimeType.fromLocalDateTime(value));
    }
    CallField field(String name, LocalDateTime[] values) {
      return field(name, BaseProxy.DateTimeType.fromLocalDateTime(Stream.of(values)));
    }
    CallField field(String name, OffsetDateTime value) {
      return field(name, BaseProxy.DateTimeType.fromOffsetDateTime(value));
    }
    CallField field(String name, OffsetDateTime[] values) {
      return field(name, BaseProxy.DateTimeType.fromOffsetDateTime(Stream.of(values)));
    }
    private static class Datifier extends ParamFieldifier<Date> {
      private DateTimeFieldifier fieldifier;
      Datifier(String name, DateTimeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Date value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Date[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Date> values) {
        return fieldifier.field(getName(), BaseProxy.DateTimeType.fromDate(values));
      }
    }
    private static class LocalDateTimifier extends ParamFieldifier<LocalDateTime> {
      private DateTimeFieldifier fieldifier;
      LocalDateTimifier(String name, DateTimeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(LocalDateTime value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(LocalDateTime[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<LocalDateTime> values) {
        return fieldifier.field(getName(), BaseProxy.DateTimeType.fromLocalDateTime(values));
      }
    }
    private static class OffsetDateTimifier extends ParamFieldifier<OffsetDateTime> {
      private DateTimeFieldifier fieldifier;
      OffsetDateTimifier(String name, DateTimeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(OffsetDateTime value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(OffsetDateTime[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<OffsetDateTime> values) {
        return fieldifier.field(getName(), BaseProxy.DateTimeType.fromOffsetDateTime(values));
      }
    }
  }
  private static class DayTimeDurationFieldifier extends AtomicFieldifier {
     DayTimeDurationFieldifier() {
      super(BaseProxy.DayTimeDurationType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Duration value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Duration[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Duration.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Durationifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Duration value) {
      return field(name, BaseProxy.DayTimeDurationType.fromDuration(value));
    }
    CallField field(String name, Duration[] values) {
      return field(name, BaseProxy.DayTimeDurationType.fromDuration(Stream.of(values)));
    }
    private static class Durationifier extends ParamFieldifier<Duration> {
      private DayTimeDurationFieldifier fieldifier;
      Durationifier(String name, DayTimeDurationFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Duration value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Duration[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Duration> values) {
        return fieldifier.field(getName(), BaseProxy.DayTimeDurationType.fromDuration(values));
      }
    }
  }
  private static class DecimalFieldifier extends AtomicFieldifier {
    DecimalFieldifier() {
      super(BaseProxy.DecimalType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, BigDecimal value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, BigDecimal[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return BigDecimal.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new BigDecimalifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, BigDecimal value) {
      return field(name, BaseProxy.DecimalType.fromBigDecimal(value));
    }
    CallField field(String name, BigDecimal[] values) {
      return field(name, BaseProxy.DecimalType.fromBigDecimal(Stream.of(values)));
    }
    private static class BigDecimalifier extends ParamFieldifier<BigDecimal> {
      private DecimalFieldifier fieldifier;
      BigDecimalifier(String name, DecimalFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(BigDecimal value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(BigDecimal[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<BigDecimal> values) {
        return fieldifier.field(getName(), BaseProxy.DecimalType.fromBigDecimal(values));
      }
    }
  }
  private static class DoubleFieldifier extends AtomicFieldifier {
    DoubleFieldifier() {
      super(BaseProxy.DoubleType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Double value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Double[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Double.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Doublifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Double value) {
      return field(name, BaseProxy.DoubleType.fromDouble(value));
    }
    CallField field(String name, Double[] values) {
      return field(name, BaseProxy.DoubleType.fromDouble(Stream.of(values)));
    }
    private static class Doublifier extends ParamFieldifier<Double> {
      private DoubleFieldifier fieldifier;
      Doublifier(String name, DoubleFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Double value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Double[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Double> values) {
        return fieldifier.field(getName(), BaseProxy.DoubleType.fromDouble(values));
      }
    }
  }
  private static class FloatFieldifier extends AtomicFieldifier {
    FloatFieldifier() {
      super(BaseProxy.FloatType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Float value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Float[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Float.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Floatifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Float value) {
      return field(name, BaseProxy.FloatType.fromFloat(value));
    }
    CallField field(String name, Float[] values) {
      return field(name, BaseProxy.FloatType.fromFloat(Stream.of(values)));
    }
    private static class Floatifier extends ParamFieldifier<Float> {
      private FloatFieldifier fieldifier;
      Floatifier(String name, FloatFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Float value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Float[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Float> values) {
        return fieldifier.field(getName(), BaseProxy.FloatType.fromFloat(values));
      }
    }
  }
  private static class IntegerFieldifier extends AtomicFieldifier {
    IntegerFieldifier() {
      super(BaseProxy.IntegerType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Integer value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Integer[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Integer.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Integerifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Integer value) {
      return field(name, BaseProxy.IntegerType.fromInteger(value));
    }
    CallField field(String name, Integer[] values) {
      return field(name, BaseProxy.IntegerType.fromInteger(Stream.of(values)));
    }
    private static class Integerifier extends ParamFieldifier<Integer> {
      private IntegerFieldifier fieldifier;
      Integerifier(String name, IntegerFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Integer value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Integer[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Integer> values) {
        return fieldifier.field(getName(), BaseProxy.IntegerType.fromInteger(values));
      }
    }
  }
  static public class LongFieldifier extends AtomicFieldifier {
    LongFieldifier() {
      super(BaseProxy.LongType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Long value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Long[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Long.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new Longifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Long value) {
      return field(name, BaseProxy.LongType.fromLong(value));
    }
    CallField field(String name, Long[] values) {
      return field(name, BaseProxy.LongType.fromLong(Stream.of(values)));
    }
    private static class Longifier extends ParamFieldifier<Long> {
      private LongFieldifier fieldifier;
      Longifier(String name, LongFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Long value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Long[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Long> values) {
        return fieldifier.field(getName(), BaseProxy.LongType.fromLong(values));
      }
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
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, LocalTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, OffsetTime value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, OffsetTime[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return LocalTime.class.isAssignableFrom(type)  ? (ParamFieldifier<T>) new LocalTimifier(name, this) :
             OffsetTime.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new OffsetTimifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, LocalTime value) {
      return field(name, BaseProxy.TimeType.fromLocalTime(value));
    }
    CallField field(String name, LocalTime[] values) {
      return field(name, BaseProxy.TimeType.fromLocalTime(Stream.of(values)));
    }
    CallField field(String name, OffsetTime value) {
      return field(name, BaseProxy.TimeType.fromOffsetTime(value));
    }
    CallField field(String name, OffsetTime[] values) {
      return field(name, BaseProxy.TimeType.fromOffsetTime(Stream.of(values)));
    }
    private static class LocalTimifier extends ParamFieldifier<LocalTime> {
      private TimeFieldifier fieldifier;
      LocalTimifier(String name, TimeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(LocalTime value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(LocalTime[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<LocalTime> values) {
        return fieldifier.field(getName(), BaseProxy.TimeType.fromLocalTime(values));
      }
    }
    private static class OffsetTimifier extends ParamFieldifier<OffsetTime> {
      private TimeFieldifier fieldifier;
      OffsetTimifier(String name, TimeFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(OffsetTime value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(OffsetTime[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<OffsetTime> values) {
        return fieldifier.field(getName(), BaseProxy.TimeType.fromOffsetTime(values));
      }
    }
  }
  private static class UnsignedIntegerFieldifier extends AtomicFieldifier {
    UnsignedIntegerFieldifier() {
      super(BaseProxy.UnsignedIntegerType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Integer value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Integer[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Integer.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new UnsignedIntegerifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Integer value) {
      return field(name, BaseProxy.UnsignedIntegerType.fromInteger(value));
    }
    CallField field(String name, Integer[] values) {
      return field(name, BaseProxy.UnsignedIntegerType.fromInteger(Stream.of(values)));
    }
    private static class UnsignedIntegerifier extends ParamFieldifier<Integer> {
      private UnsignedIntegerFieldifier fieldifier;
      UnsignedIntegerifier(String name, UnsignedIntegerFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Integer value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Integer[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Integer> values) {
        return fieldifier.field(getName(), BaseProxy.UnsignedIntegerType.fromInteger(values));
      }
    }
  }
  private static class UnsignedLongFieldifier extends AtomicFieldifier {
    UnsignedLongFieldifier() {
      super(BaseProxy.UnsignedLongType.NAME);
    }
    @Override
    CallField field(String name, boolean nullable, Long value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Long[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Long.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new UnsignedLongifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Long value) {
      return field(name, BaseProxy.UnsignedLongType.fromLong(value));
    }
    CallField field(String name, Long[] values) {
      return field(name, BaseProxy.UnsignedLongType.fromLong(Stream.of(values)));
    }
    private static class UnsignedLongifier extends ParamFieldifier<Long> {
      private UnsignedLongFieldifier fieldifier;
      UnsignedLongifier(String name, UnsignedLongFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Long value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Long[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Long> values) {
        return fieldifier.field(getName(), BaseProxy.UnsignedLongType.fromLong(values));
      }
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
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, JsonNode[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, JsonParser value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, JsonParser[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return JsonNode.class.isAssignableFrom(type)   ? (ParamFieldifier<T>) new JsonNodeifier(name, this) :
             JsonParser.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new JsonParserfiier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, JsonNode value) {
      return formattedField(name, BaseProxy.JsonDocumentType.fromJsonNode(value));
    }
    CallField field(String name, JsonNode[] values) {
      return formattedField(name, BaseProxy.JsonDocumentType.fromJsonNode(Stream.of(values)));
    }
    CallField field(String name, JsonParser value) {
      return formattedField(name, BaseProxy.JsonDocumentType.fromJsonParser(value));
    }
    CallField field(String name, JsonParser[] values) {
      return formattedField(name, BaseProxy.JsonDocumentType.fromJsonParser(Stream.of(values)));
    }
    private static class JsonNodeifier extends ParamFieldifier<JsonNode> {
      private JsonDocumentFieldifier fieldifier;
      JsonNodeifier(String name, JsonDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(JsonNode value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(JsonNode[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<JsonNode> values) {
        return fieldifier.formattedField(getName(), BaseProxy.JsonDocumentType.fromJsonNode(values));
      }
    }
    private static class JsonParserfiier extends ParamFieldifier<JsonParser> {
      private JsonDocumentFieldifier fieldifier;
      JsonParserfiier(String name, JsonDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(JsonParser value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(JsonParser[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<JsonParser> values) {
        return fieldifier.formattedField(getName(), BaseProxy.JsonDocumentType.fromJsonParser(values));
      }
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
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Document[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, InputSource value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, InputSource[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, Source value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, Source[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, XMLEventReader value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, XMLEventReader[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    CallField field(String name, boolean nullable, XMLStreamReader value) {
      if (isEmpty(name, nullable, value)) return null;
      return field(name, value);
    }
    @Override
    CallField field(String name, boolean nullable, boolean multiple, XMLStreamReader[] values) {
      if (isEmpty(name, nullable, values)) return null;
      return isMultiple(name, multiple, values) ? field(name, values) : field(name, values[0]);
    }
    @Override
    <T> ParamFieldifier<T> fieldifierFor(String name, Class<T> type) {
      return Document.class.isAssignableFrom(type)        ? (ParamFieldifier<T>) new Documentifier(name, this) :
             InputSource.class.isAssignableFrom(type)     ? (ParamFieldifier<T>) new InputSourcifier(name, this) :
             Source.class.isAssignableFrom(type)          ? (ParamFieldifier<T>) new Sourcifier(name, this) :
             XMLEventReader.class.isAssignableFrom(type)  ? (ParamFieldifier<T>) new XMLEventReaderifier(name, this) :
             XMLStreamReader.class.isAssignableFrom(type) ? (ParamFieldifier<T>) new XMLStreamReaderifier(name, this) :
             super.fieldifierFor(name, type);
    }
    CallField field(String name, Document value) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromDocument(value));
    }
    CallField field(String name, Document[] values) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromDocument(Stream.of(values)));
    }
    CallField field(String name, InputSource value) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromInputSource(value));
    }
    CallField field(String name, InputSource[] values) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromInputSource(Stream.of(values)));
    }
    CallField field(String name, Source value) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromSource(value));
    }
    CallField field(String name, Source[] values) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromSource(Stream.of(values)));
    }
    CallField field(String name, XMLEventReader value) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromXMLEventReader(value));
    }
    CallField field(String name, XMLEventReader[] values) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromXMLEventReader(Stream.of(values)));
    }
    CallField field(String name, XMLStreamReader value) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromXMLStreamReader(value));
    }
    CallField field(String name, XMLStreamReader[] values) {
      return formattedField(name, BaseProxy.XmlDocumentType.fromXMLStreamReader(Stream.of(values)));
    }
    private static class Documentifier extends ParamFieldifier<Document> {
      private XmlDocumentFieldifier fieldifier;
      Documentifier(String name, XmlDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Document value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Document[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Document> values) {
        return fieldifier.formattedField(getName(), BaseProxy.XmlDocumentType.fromDocument(values));
      }
    }
    private static class InputSourcifier extends ParamFieldifier<InputSource> {
      private XmlDocumentFieldifier fieldifier;
      InputSourcifier(String name, XmlDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(InputSource value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(InputSource[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<InputSource> values) {
        return fieldifier.formattedField(getName(), BaseProxy.XmlDocumentType.fromInputSource(values));
      }
    }
    private static class Sourcifier extends ParamFieldifier<Source> {
      private XmlDocumentFieldifier fieldifier;
      Sourcifier(String name, XmlDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(Source value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(Source[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<Source> values) {
        return fieldifier.formattedField(getName(), BaseProxy.XmlDocumentType.fromSource(values));
      }
    }
    private static class XMLEventReaderifier extends ParamFieldifier<XMLEventReader> {
      private XmlDocumentFieldifier fieldifier;
      XMLEventReaderifier(String name, XmlDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(XMLEventReader value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(XMLEventReader[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<XMLEventReader> values) {
        return fieldifier.formattedField(getName(), BaseProxy.XmlDocumentType.fromXMLEventReader(values));
      }
    }
    private static class XMLStreamReaderifier extends ParamFieldifier<XMLStreamReader> {
      private XmlDocumentFieldifier fieldifier;
      XMLStreamReaderifier(String name, XmlDocumentFieldifier fieldifier) {
        super(name);
        this.fieldifier = fieldifier;
      }
      @Override
      CallField field(XMLStreamReader value) {
        return fieldifier.field(getName(), value);
      }
      @Override
      CallField field(XMLStreamReader[] values) {
        return fieldifier.field(getName(), values);
      }
      @Override
      CallField field(Stream<XMLStreamReader> values) {
        return fieldifier.formattedField(getName(), BaseProxy.XmlDocumentType.fromXMLStreamReader(values));
      }
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
                "cannot convert server type "+serverType+" to primaryClient type "+as.getCanonicalName()
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
      put(File.class, new ReturnConverter<File>() {
        @Override
        public File one(SingleCallResponse response) {
          return NodeConverter.InputStreamToFile(response.asInputStream());
        }
        @Override
        public Stream<File> many(MultipleCallResponse response) {
          return NodeConverter.InputStreamToFile(response.asStreamOfInputStream());
        }
      });
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
