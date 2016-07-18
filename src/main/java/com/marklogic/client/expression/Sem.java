/*
 * Copyright 2016 MarkLogic Corporation
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
package com.marklogic.client.expression;

// TODO: single import
import com.marklogic.client.expression.BaseType;

import com.marklogic.client.expression.Xs;
 import com.marklogic.client.expression.BaseType;


// IMPORTANT: Do not edit. This file is generated. 
public interface Sem {
    public Sem.BlankExpr bnode();
    public Sem.BlankExpr bnode(Xs.AnyAtomicTypeExpr value);
    public BaseType.ItemSeqExpr coalesce(BaseType.ItemExpr... parameter1);
    public Sem.IriExpr datatype(Xs.AnyAtomicTypeExpr value);
    public Sem.IriExpr defaultGraphIri();
    public BaseType.ItemSeqExpr ifExpr(boolean condition, BaseType.ItemSeqExpr then, BaseType.ItemExpr... elseExpr);
    public BaseType.ItemSeqExpr ifExpr(Xs.BooleanExpr condition, BaseType.ItemSeqExpr then, BaseType.ItemSeqExpr elseExpr);
    public Sem.InvalidExpr invalid(String string, Sem.IriExpr datatype);
    public Sem.InvalidExpr invalid(Xs.StringExpr string, Sem.IriExpr datatype);
    public Sem.IriExpr invalidDatatype(Sem.InvalidExpr val);
    public Sem.IriExpr iri(Xs.AnyAtomicTypeExpr stringIri);
    public Xs.QNameExpr iriToQName(String arg1);
    public Xs.QNameExpr iriToQName(Xs.StringExpr arg1);
    public Xs.BooleanExpr isBlank(Xs.AnyAtomicTypeExpr value);
    public Xs.BooleanExpr isIRI(Xs.AnyAtomicTypeExpr value);
    public Xs.BooleanExpr isLiteral(Xs.AnyAtomicTypeExpr value);
    public Xs.BooleanExpr isNumeric(Xs.AnyAtomicTypeExpr value);
    public Xs.StringExpr lang(Xs.AnyAtomicTypeExpr value);
    public Xs.BooleanExpr langMatches(String langTag, String langRange);
    public Xs.BooleanExpr langMatches(Xs.StringExpr langTag, Xs.StringExpr langRange);
    public Sem.IriExpr QNameToIri(Xs.QNameExpr arg1);
    public Xs.DoubleExpr random();
    public Xs.BooleanExpr sameTerm(Xs.AnyAtomicTypeExpr a, Xs.AnyAtomicTypeExpr b);
    public Xs.StringExpr timezoneString(Xs.DateTimeExpr value);
    public Xs.AnyAtomicTypeExpr typedLiteral(String value, Sem.IriExpr datatype);
    public Xs.AnyAtomicTypeExpr typedLiteral(Xs.StringExpr value, Sem.IriExpr datatype);
    public Sem.UnknownExpr unknown(String string, Sem.IriExpr datatype);
    public Sem.UnknownExpr unknown(Xs.StringExpr string, Sem.IriExpr datatype);
    public Sem.IriExpr unknownDatatype(Sem.UnknownExpr val);
    public Sem.IriExpr uuid();
    public Xs.StringExpr uuidString();     public Sem.BlankSeqExpr blank(Sem.BlankExpr... items);
     public Sem.InvalidSeqExpr invalid(Sem.InvalidExpr... items);
     public Sem.IriSeqExpr iri(Sem.IriExpr... items);
     public Sem.UnknownSeqExpr unknown(Sem.UnknownExpr... items);
        public interface BlankSeqExpr extends BaseType.ItemSeqExpr { }
        public interface BlankExpr extends BlankSeqExpr, BaseType.ItemExpr { }
         public interface InvalidSeqExpr extends BaseType.ItemSeqExpr { }
        public interface InvalidExpr extends InvalidSeqExpr, BaseType.ItemExpr { }
         public interface IriSeqExpr extends BaseType.ItemSeqExpr , PlanBuilder.TriplePosition { }
        public interface IriExpr extends IriSeqExpr, BaseType.ItemExpr { }
         public interface UnknownSeqExpr extends BaseType.ItemSeqExpr { }
        public interface UnknownExpr extends UnknownSeqExpr, BaseType.ItemExpr { }

}
