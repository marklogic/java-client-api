package com.marklogic.client;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.ReaderHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

import java.io.InputStream;
import java.io.Reader;
import java.util.stream.Stream;

public class CallParts {

  public enum HttpMethod {POST}

  // TODO: maybe rename form field instead of function param; form-data representation vs higher level?
  static public abstract class CallField {
    private String paramName;
    CallField(String paramName) {
      this.paramName = paramName;
    }
    public String getParamName() {
      return paramName;
    }
  }
  static public class SingleAtomicCallField extends CallField {
    private String paramValue;
    public SingleAtomicCallField(String paramName, String paramValue) {
      super(paramName);
      this.paramValue = paramValue;
    }
    public String getParamValue() {
      return paramValue;
    }
  }
  static public class MultipleAtomicCallField extends CallField {
    private Stream<String> paramValues;
    public MultipleAtomicCallField(String paramName, Stream<String> paramValues) {
      super(paramName);
      this.paramValues = paramValues;
    }
    public Stream<String> getParamValues() {
      return paramValues;
    }
  }
  static public class SingleNodeCallField extends CallField {
    private AbstractWriteHandle paramValue;
    public SingleNodeCallField(String paramName, AbstractWriteHandle paramValue) {
      super(paramName);
      this.paramValue = paramValue;
    }
    public AbstractWriteHandle getParamValue() {
      return paramValue;
    }
  }
  static public class MultipleNodeCallField extends CallField {
    private Stream<? extends AbstractWriteHandle> paramValues;

    public MultipleNodeCallField(String paramName, Stream<? extends AbstractWriteHandle> paramValues) {
      super(paramName);
      this.paramValues = paramValues;
    }
    public Stream<? extends AbstractWriteHandle> getParamValues() {
      return paramValues;
    }
  }

  public interface CallRequest {
    boolean hasStreamingPart();
    SessionState getSession();
    String getEndpoint();
    HttpMethod getHttpMethod();
    public CallResponse withEmptyResponse();
    public SingleCallResponse withDocumentResponse(Format format);
    public MultipleCallResponse withMultipartMixedResponse(Format format);
  }

  public interface CallResponse {
    public boolean isNull();
    public int     getStatusCode();
    public String  getStatusMsg();
    public String  getErrorBody();
  }
  public interface SingleCallResponse extends CallResponse {
    public byte[]            asBytes();
    public InputStream asInputStream();
    public InputStreamHandle asInputStreamHandle();
    public Reader asReader();
    public ReaderHandle asReaderHandle();
    public String            asString();
  }
  public interface MultipleCallResponse extends CallResponse {
    public Stream<byte[]> asStreamOfBytes();
    public Stream<InputStream>       asStreamOfInputStream();
    public Stream<InputStreamHandle> asStreamOfInputStreamHandle();
    public Stream<Reader>            asStreamOfReader();
    public Stream<ReaderHandle>      asStreamOfReaderHandle();
    public Stream<String>            asStreamOfString();
  }

}
