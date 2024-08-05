/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.io.Format;

class DocumentPatchBuilderImpl
  extends DocumentMetadataPatchBuilderImpl
  implements DocumentPatchBuilder
{
/* TODO:
   insert values for JSON array items?
   awareness of popular fragment sources

    metadata - accept predicate for permission or property

    collect QName prefix bindings for output on root
 */

  static class ContentDeleteOperation extends PatchOperation {
    String      selectPath;
    Cardinality cardinality;
    ContentDeleteOperation(String selectPath, Cardinality cardinality) {
      super();
      this.selectPath  = selectPath;
      this.cardinality = cardinality;
    }
    @Override
    public void write(JSONStringWriter serializer) {
      writeDelete(serializer, selectPath, cardinality);
    }
    @Override
    public void write(XMLOutputSerializer out) throws Exception {
      writeDelete(out, selectPath, cardinality);
    }
  }
  static class ContentInsertOperation extends PatchOperation {
    String      contextPath;
    Position    position;
    Cardinality cardinality;
    String      fragment;
    ContentInsertOperation(
      String contextPath, Position position, Cardinality cardinality, Object fragment
    ) {
      super();
      this.contextPath = contextPath;
      this.position    = position;
      this.cardinality = cardinality;
      this.fragment    = (fragment instanceof String) ?
        (String) fragment : fragment.toString();
    }
    @Override
    public void write(JSONStringWriter serializer) {
      writeStartInsert(serializer, contextPath, position.toString(), cardinality);
      serializer.writeStartEntry("content");
      serializer.writeFragment(fragment);
      serializer.writeEndObject();
      serializer.writeEndObject();
    }
    @Override
    public void write(XMLOutputSerializer out) throws Exception {
      XMLStreamWriter serializer = out.getSerializer();
      writeStartInsert(out, contextPath, position.toString(), cardinality);
      serializer.writeCharacters(""); // force the tag close
      serializer.flush();
      out.getWriter().write(fragment);
      serializer.writeEndElement();
    }
  }
  static class ContentReplaceOperation extends PatchOperation {
    String      selectPath;
    Cardinality cardinality;
    boolean     isFragment = true;
    Object      input;
    String      inputAsString;
    ContentReplaceOperation(String selectPath, Cardinality cardinality, boolean isFragment,
                            Object input
    ) {
      super();
      this.selectPath  = selectPath;
      this.cardinality = cardinality;
      this.isFragment  = isFragment;
      this.input       = input;
      this.inputAsString = null;
      if (input != null) {
        inputAsString = (input instanceof String)  ? (String) input : input.toString();
      }
    }
    @Override
    public void write(JSONStringWriter serializer) {
      writeStartReplace(serializer, selectPath, cardinality);
      serializer.writeStartEntry("content");
      if (isFragment) {
        serializer.writeFragment(inputAsString);
      } else if (input instanceof Boolean) {
        serializer.writeBooleanValue(input);
      }
      else if (input instanceof Number) {
        serializer.writeNumberValue(input);
      } else {
        serializer.writeStringValue(input);
      }
      serializer.writeEndObject();
      serializer.writeEndObject();
    }
    @Override
    public void write(XMLOutputSerializer out) throws Exception {
      XMLStreamWriter serializer = out.getSerializer();
      writeStartReplace(out, selectPath, cardinality);
      if (isFragment) {
        serializer.writeCharacters(""); // force the tag close
        serializer.flush();
        out.getWriter().write(inputAsString);
      } else {
        if(inputAsString != null)
          serializer.writeCharacters(inputAsString);
      }
      serializer.writeEndElement();
    }
  }
  static class ContentReplaceInsertOperation extends PatchOperation {
    String      selectPath;
    String      contextPath;
    Position    position;
    Cardinality cardinality;
    String      fragment;
    ContentReplaceInsertOperation(
      String selectPath, String contextPath, Position position, Cardinality cardinality,
      Object fragment
    ) {
      super();
      this.selectPath  = selectPath;
      this.contextPath = contextPath;
      this.position    = position;
      this.cardinality = cardinality;
      this.fragment    = (fragment instanceof String) ?
        (String) fragment : fragment.toString();
    }
    @Override
    public void write(JSONStringWriter serializer) {
      writeStartReplaceInsert(
        serializer, selectPath, contextPath, position.toString(), cardinality
      );
      serializer.writeStartEntry("content");
      serializer.writeFragment(fragment);
      serializer.writeEndObject();
      serializer.writeEndObject();
    }
    @Override
    public void write(XMLOutputSerializer out) throws Exception {
      XMLStreamWriter serializer = out.getSerializer();
      writeStartReplaceInsert(
        out, selectPath, contextPath, position.toString(), cardinality
      );
      serializer.writeCharacters(""); // force the tag close
      serializer.flush();
      out.getWriter().write(fragment);
      serializer.writeEndElement();
    }
  }
  static class ContentReplaceApplyOperation extends PatchOperation {
    String      selectPath;
    Cardinality cardinality;
    CallImpl    call;
    ContentReplaceApplyOperation(String selectPath, Cardinality cardinality, CallImpl call) {
      super();
      this.selectPath  = selectPath;
      this.cardinality = cardinality;
      this.call        = call;
    }
    @Override
    public void write(JSONStringWriter serializer) {
      writeReplaceApply(serializer, selectPath, cardinality, call);
    }
    @Override
    public void write(XMLOutputSerializer out) throws Exception {
      writeReplaceApply(out, selectPath, cardinality, call);
    }
  }

  DocumentPatchBuilderImpl(Format format) {
    super(format);
  }

  @Override
  public DocumentPatchBuilder delete(String selectPath) {
    return delete(selectPath, null);
  }
  @Override
  public DocumentPatchBuilder delete(String selectPath, Cardinality cardinality) {
    onContent();
    operations.add(new ContentDeleteOperation(selectPath, cardinality));
    return this;
  }
  @Override
  public DocumentPatchBuilder insertFragment(
    String contextPath, Position position, Object fragment
  ) {
    return insertFragment(contextPath, position, null, fragment);
  }
  @Override
  public DocumentPatchBuilder insertFragment(
    String contextPath, Position position, Cardinality cardinality, Object fragment
  ) {
    onContent();
    operations.add(
      new ContentInsertOperation(contextPath, position, cardinality, fragment)
    );
    return this;
  }
  @Override
  public DocumentPatchBuilder replaceValue(String selectPath, Object value) {
    return replaceValue(selectPath, null, value);
  }
  @Override
  public DocumentPatchBuilder replaceValue(
    String selectPath, Cardinality cardinality, Object value
  ) {
    onContent();
    operations.add(new ContentReplaceOperation(selectPath, cardinality, false, value));
    return this;
  }
  @Override
  public DocumentPatchBuilder replaceFragment(String selectPath, Object fragment) {
    return replaceFragment(selectPath, null, fragment);
  }
  @Override
  public DocumentPatchBuilder replaceFragment(
    String selectPath, Cardinality cardinality, Object fragment
  ) {
    onContent();
    operations.add(new ContentReplaceOperation(selectPath, cardinality, true, fragment));
    return this;
  }
  @Override
  public DocumentPatchBuilder replaceInsertFragment(
    String selectPath, String contextPath, Position position, Object fragment
  ) {
    return replaceInsertFragment(
      selectPath, contextPath, position, null, fragment
    );
  }
  @Override
  public DocumentPatchBuilder replaceInsertFragment(
    String selectPath, String contextPath, Position position, Cardinality cardinality,
    Object fragment
  ) {
    onContent();
    operations.add(
      new ContentReplaceInsertOperation(
        selectPath, contextPath, position, cardinality, fragment
      )
    );
    return this;
  }
  @Override
  public DocumentPatchBuilder replaceApply(String selectPath, Call call) {
    return replaceApply(selectPath, null, call);
  }
  @Override
  public DocumentPatchBuilder replaceApply(
    String selectPath, Cardinality cardinality, Call call
  ) {
    if (!CallImpl.class.isAssignableFrom(call.getClass()))
      throw new IllegalArgumentException(
        "Cannot use external call implementation");
    onContent();
    operations.add(
      new ContentReplaceApplyOperation(selectPath, cardinality, (CallImpl) call)
    );
    return this;
  }
  private void onContent() {
    if (!onContent) {
      onContent = true;
    }
  }

  @Override
  public DocumentPatchBuilder pathLanguage(PathLanguage pathLang) {
    this.pathLang = pathLang;
    return this;
  }
}
