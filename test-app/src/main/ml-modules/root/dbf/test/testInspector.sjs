'use strict';
/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

function defaultNull(itemdef) {
  if (itemdef === void 0) {
    return null;
  }
  return itemdef;
}
function setDataKind(itemdef, errorList) {
  if (itemdef === null) {
    return null;
  }

  const itemType = itemdef.datatype;
  switch (itemType) {
  case 'boolean':
  case 'date':
  case 'dateTime':
  case 'dayTimeDuration':
  case 'decimal':
  case 'double':
  case 'float':
  case 'int':
  case 'long':
  case 'string':
  case 'time':
  case 'unsignedInt':
  case 'unsignedLong':
    itemdef.dataKind = 'atomic';
    break;
  case 'array':
  case 'object':
  case 'binaryDocument':
  case 'jsonDocument':
  case 'textDocument':
  case 'xmlDocument':
    itemdef.dataKind = 'document';
    break;
  default:
    errorList.push(`Test error - unknown dataType: ${itemType}`);
    break;
  }

  return itemdef;
}
function getDocumentFormat(dataType, errorList) {
  switch (dataType) {
  case 'boolean':
  case 'date':
  case 'dateTime':
  case 'dayTimeDuration':
  case 'decimal':
  case 'double':
  case 'float':
  case 'int':
  case 'long':
  case 'string':
  case 'time':
  case 'unsignedInt':
  case 'unsignedLong':
    return 'text';
  case 'array':
  case 'object':
    return 'json';
  case 'binaryDocument':
  case 'jsonDocument':
  case 'textDocument':
  case 'xmlDocument':
    return fn.substringBefore(dataType, 'Document');
  default:
    errorList.push(`Test error - unknown dataType: ${dataType}`);
    return null;
  }
}
function getContentType(documentFormat, errorList) {
  switch(documentFormat) {
  case 'binary': return 'application/x-unknown-content-type';
  case 'json':   return 'application/json';
  case 'text':   return 'text/plain';
  case 'xml':    return 'application/xml';
  default:
    errorList.push(`Test cannot get content type for unknown document format: ${documentFormat}`);
    return null;
  }
}
function getDocumentValue(documentFormat, testval, errorList) {
  const docStr = xs.string(testval);
  switch(documentFormat) {
  case 'binary':
    return new NodeBuilder().addBinary(xs.hexBinary(xs.base64Binary(docStr))).toNode();
  case 'json':
    return fn.head(xdmp.unquote(fn.translate(docStr, '\\', ''), null, 'format-'+documentFormat)).root;
  case 'text':
    return new NodeBuilder().addText(docStr).toNode();
  case 'xml':
    return fn.head(xdmp.unquote(docStr, null, 'format-'+documentFormat)).root;
  default:
    errorList.push(`Test cannot get document value for unknown document format: ${documentFormat}`);
    return null;
  }
}
function checkFieldName(fieldName, paramNames, errorList) {
  if (!paramNames.has(fieldName)) {
    errorList.push(`Request field without parameter definition: ${fieldName}`);
  }
}
function checkFieldNames(fieldNames, paramNames, errorList) {
  if (Array.isArray(fieldNames) && fieldNames.length > 0) {
    if (paramNames instanceof Set && paramNames.size > 0) {
      fieldNames.forEach(fieldName => checkFieldName(fieldName, paramNames, errorList));
    } else {
      fieldNames.forEach(fieldName => errorList.push(
        `Request field without any parameter definitions: ${fieldName}`
        ));
    }
  }
}
function checkRequest(funcTestPath, funcdef, fields, errorList) {
  const expectedBase = '/dbf/test/';
  const funcTestName = funcTestPath.substring(funcTestPath.indexOf("/", expectedBase.length) + 1);

  const funcName = funcdef.functionName;
  if (funcName !== funcTestName) {
    errorList.push(`mismatch between ${funcTestName} request and ${funcName} definition`);
  }

  const paramsraw   = funcdef.params;
  const paramdefs   = (!Array.isArray(paramsraw) || paramsraw.length === 0) ?
    [] : paramsraw.map(paramraw => setDataKind(paramraw, errorList));
  const returndef   = setDataKind(defaultNull(funcdef.return), errorList);

  const inputdef = (paramdefs === null) ?
    {kind:'none', atomics:[], documents:[]} :
    paramdefs.reduce(((input, paramdef) => {
        switch(paramdef.dataKind) {
          case 'document':
            input.documents.push(paramdef);
            switch(input.kind) {
              case 'none':
              case 'urlencoded':
                input.kind = 'multipart';
                break;
            }
            break;
          case 'atomic':
            input.atomics.push(paramdef);
            switch(input.kind) {
              case 'none':
                input.kind = 'urlencoded';
                break;
            }
            break;
          default:
            errorList.push(`Test error - unknown parameter dataKind: ${paramdef.dataKind}`);
            break;
        }
        return input;
      }),
      {kind:'none', atomics:[], documents:[]}
    );

  const endpointMethod = xdmp.getRequestMethod().toUpperCase();
  if (endpointMethod === 'DELETE' || endpointMethod === 'HEAD' || endpointMethod === 'PUT') {
    errorList.push(`Unsupported method: ${endpointMethod}`);
  }

  const requestMimeType     = fn.head(xdmp.getRequestHeader('content-type', ''));
  const responseMimeTypeRaw = fn.head(xdmp.getRequestHeader('accept',       ''));
  const responseMimeType    = (responseMimeTypeRaw === '*/*') ? '' : responseMimeTypeRaw;

  const reqBodydef  = inputdef.kind;
  const respBodydef =
    (returndef          === null)     ? 'none'      :
    (returndef.multiple === true)     ? 'multipart' :
    (returndef.dataKind === 'atomic') ? 'text'      :
                                        'document'  ;

// TODO: workaround for Bug:49409
  const rawReqBody  = (reqBodydef !== 'multipart') ? null : xdmp.getRequestBody();
  const requestBody =
    (requestMimeType === '' && rawReqBody instanceof Document &&
        rawReqBody.documentFormat === 'BINARY' && xdmp.binarySize(rawReqBody.root) === 0) ?
    null : rawReqBody;

  switch(reqBodydef) {
    case 'none':
      if (requestMimeType !== '') {
        errorList.push(
          `Request mime type specified for request body of none: ${requestMimeType}`
          );
      }
      if (inputdef.atomics.length > 0 || inputdef.documents.length > 0) {
        errorList.push(`Invalid input for request body of none`);
      }
      break;
    case 'urlencoded':
      if (endpointMethod !== 'POST') {
        errorList.push(
          `Invalid method other than POST for request body of urlencoded: ${endpointMethod}`
          );
      }
      if (requestMimeType === '') {
        if (requestBody !== null) {
          errorList.push(`No mime type for request body of urlencoded`);
        }
      } else if (!requestMimeType.startsWith('application/x-www-form-urlencoded')) {
        errorList.push(
          `Invalid mime type other than application/x-www-form-urlencoded for request body of urlencoded: ${requestMimeType}`
          );
      }
      if (inputdef.documents.length > 0) {
        errorList.push(`Invalid document input for request body of urlencoded}`);
      }
      break;
    case 'multipart':
      if (endpointMethod !== 'POST') {
        errorList.push(
          `Invalid method other than POST for request body of multipart: ${endpointMethod}`
          );
      }
      if (requestBody !== null) {
        if (requestMimeType === '') {
          errorList.push(`No mime type for request body of multipart`);
        } else if (!requestMimeType.startsWith('multipart/form-data')) {
          errorList.push(
            `Invalid mime type other than multipart/form-data for request body of multipart: ${requestMimeType}`
          );
        }
      }
      break;
    default:
      errorList.push(`Test error - unknown request body: ${reqBodydef}`);
      break;
  }

  const paramNames = paramdefs.reduce((nameSet, paramdef) => {
      const paramName     = paramdef.name;
      const paramType     = paramdef.datatype;
      const paramNullable = paramdef.nullable;
      const paramMultiple = paramdef.multiple;
      const paramKind     = paramdef.dataKind;
      nameSet.add(paramName);
      const paramField  = fields[paramName];
      const paramVals   = (paramField === void 0) ? null : paramField.values;
      if (paramVals === void 0 || paramVals === null) {
        if (paramNullable !== true) {
          errorList.push(`Null value for unnullable parameter: ${paramName}`);
          return nameSet;
        }
      } else {
        const valCount = paramVals.length;
        if (paramNullable !== true && valCount === 0) {
          errorList.push(`Empty value for unnullable parameter: ${paramName}`);
          return nameSet;
        } else if (paramMultiple !== true && valCount > 1) {
          errorList.push(`Multiple values for singular parameter: ${paramName}`);
          return nameSet;
        }
        paramVals.forEach((paramVal, valNum) => {
          switch(paramKind) {
            case 'atomic':
              if (!xdmp.castableAs('http://www.w3.org/2001/XMLSchema', paramType, paramVal)) {
                errorList.push(`Cannot cast parameter ${paramName} to ${paramType} for value: ${paramVal}`);
              }
              break;
            case 'document':
              const documentFormat = paramVal.documentFormat;
              switch(paramType) {
                case 'binaryDocument':
                  if (documentFormat !== 'BINARY') {
                    errorList.push(`Invalid format for binary document of ${paramName} parameter: ${documentFormat}`);
                  }
                  break;
                case 'array':
                  if (xdmp.nodeKind(paramVal) !== 'array') {
                    errorList.push(`Expected array node for ${paramName} parameter instead of: ${Object.prototype.toString.call(paramVal)}`);
                  }
                  break;
                case 'object':
                  if (xdmp.nodeKind(paramVal) !== 'object') {
                    errorList.push(`Expected object node for ${paramName} parameter instead of: ${Object.prototype.toString.call(paramVal)}`);
                  }
                  break;
                case 'jsonDocument':
                  if (documentFormat !== 'JSON') {
                    errorList.push(`Invalid format for JSON document of ${paramName} parameter: ${documentFormat}`);
                  }
                  break;
                case 'textDocument':
                  if (documentFormat !== 'TEXT') {
                    errorList.push(`Invalid format for text document of ${paramName} parameter: ${documentFormat}`);
                  }
                  break;
                case 'xmlDocument':
                  if (documentFormat !== 'XML') {
                    errorList.push(`Invalid format for XML document of ${paramName} parameter: ${documentFormat}`);
                  }
                  break;
                default:
                  errorList.push(`Test error - unknown document type of ${paramName} parameter: ${paramType}`);
                  break;
              }
              break;
            default:
              errorList.push(`Test error - unknown data kind of ${paramName} parameter: ${paramKind}`);
              break;
          }
        });
      }
      return nameSet;
    },
    new Set()
  );

  checkFieldNames(Object.keys(fields),  paramNames, errorList);

  const returnType     = (respBodydef === 'none') ? null : returndef.datatype;
  const returnNullable = (respBodydef === 'none') ? null : returndef.nullable;
  const returnMultiple = (respBodydef === 'none') ? null : returndef.multiple;
  const returnMapping  = (respBodydef === 'none') ? null : returndef.$javaClass;
  const hasMapping     = (returnMapping !== void 0 && returnMapping !== null)

  const testdefs = (respBodydef === 'none') ? null : cts.doc('/dbf/test.json').toObject();

  const mappedTestdefs = (!hasMapping) ? null : cts.doc('/dbf/mappedTest.json').toObject();
  const mappedTestvals = (!hasMapping) ? null : mappedTestdefs[returnMapping];

  const testvals =
      (respBodydef === 'none')                               ? null                 :
      (mappedTestvals !== void 0 && mappedTestvals !== null) ? mappedTestvals       :
                                                               testdefs[returnType];

  let output = null;

  switch(respBodydef) {
    case 'none':
      if (endpointMethod !== 'POST') {
        errorList.push(
          `Invalid method for response body of ${respBodydef}: ${endpointMethod}`
          );
      }
      if (responseMimeType !== '') {
        errorList.push(
          `Response mime type specified for response body of ${respBodydef}: ${responseMimeType}`
          );
      }
      break;
    case 'text':
      if (endpointMethod !== 'POST') {
        errorList.push(
          `Invalid method for response body of text: ${endpointMethod}`
          );
      }
      if (!responseMimeType.startsWith('text/plain')) {
        errorList.push(
          `Invalid mime type other than text/plain for response body of text: ${responseMimeType}`
          );
      }
      if (funcTestPath.endsWith('ReturnNull')) {
        output = null;
// TODO: 'MULTIPLE_ONE' - return one for many
      } else {
        output = testvals[0];
      }
      break;
    case 'document':
      if (endpointMethod !== 'POST') {
        errorList.push(
          `Invalid method for response body of document: ${endpointMethod}`
          );
      }
      if (!responseMimeType.startsWith('application/json') &&
        !responseMimeType.startsWith('application/xml')    &&
        !responseMimeType.startsWith('text/plain')         &&
        !responseMimeType.startsWith('application/x-unknown-content-type')) {
        errorList.push(
          `Invalid mime type for response body of document: ${responseMimeType}`
          );
      }
      if (funcTestPath.endsWith('ReturnNull')) {
        output = null;
      } else {
        const documentFormat = getDocumentFormat(returnType, errorList);
        const contentType    = getContentType(documentFormat, errorList);
        output = getDocumentValue(documentFormat, testvals[0], errorList)
      }
      break;
    case 'multipart':
      if (endpointMethod !== 'POST') {
        errorList.push(
          `Invalid method for response body of multipart: ${endpointMethod}`
          );
      }
      if (!responseMimeType.startsWith('multipart/mixed')) {
        errorList.push(
          `Invalid mime type other than multipart/mixed for response body of multipart: ${responseMimeType}`
          );
      }
      if (funcTestPath.endsWith('ReturnNull')) {
        output = null;
      } else {
        const boundary       = sem.uuidString();
        const documentFormat = getDocumentFormat(returnType, errorList);
        const contentType    = getContentType(documentFormat, errorList);
        const documentVals   = returnMultiple ?
          testvals.map(testval => getDocumentValue(documentFormat, testval, errorList)) :
          [ getDocumentValue(documentFormat, testvals[0], errorList) ];
        output = Sequence.from(documentVals);
      }
      break;
    default:
      errorList.push(`Test error - unknown response body: ${respBodydef}`);
      break;
  }

  if (errorList.length > 0) {
    return null;
  }

/* TODO:
      https://tools.ietf.org/html/rfc7578#section-4.3
       */

  return output;
}
function addField(testName, fields, fieldName, fieldValues) {
  fields[fieldName] = {
    name:   fieldName,
    values:
      (fn.count(fieldValues) === 0)      ? null                   :
      (fieldValues instanceof ArrayNode) ? fieldValues.toObject() :
      Array.isArray(fieldValues)         ? fieldValues            :
      (fieldValues instanceof Sequence)  ? fieldValues.toArray()  :
      [fieldValues]
    };
  return fields;
}
function getFields(funcdef, fields, errorList) {
  return fields;
}
function makeError(statusCode, statusMsg, message) {
  const err = {"errorResponse":{
      "statusCode": statusCode,
      "status":     statusMsg,
      "message":    message
      }};
  return err;
}
function makeResult(funcTestPath, funcdef, fields, errorList) {
  let result = checkRequest(funcTestPath, funcdef, fields, errorList);
  if (errorList.length > 0) {
      console.log(makeError(400, 'Bad Request', 'Bad request:\n'+errorList.join('\n')));
  }
  return result;
}
module.exports = {
  getFields:      getFields,
  addField:       addField,
  makeResult:     makeResult
}

/* TODO: error string
try {
} catch (err) {
  err = (err instanceof Sequence) ? fn.head(err) : err;
  xdmp.setResponseCode(500, 'Internal Error');

  console.log(Object.prototype.toString.call(err));
  console.log(err);
  console.log(JSON.stringify(err, null, 4));
  console.log(xdmp.quote(err));
  console.log(fn.string(err));
  console.log(xdmp.describe(err));
  xdmp.addResponseHeader('x-marklogic-error', fn.encodeForUri(xdmp.quote(err)));
}
 */
