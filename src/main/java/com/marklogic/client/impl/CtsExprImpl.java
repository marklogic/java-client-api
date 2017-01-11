/*
 * Copyright 2016-2017 MarkLogic Corporation
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
package com.marklogic.client.impl;

import com.marklogic.client.expression.XsExpr;
import com.marklogic.client.expression.XsValue;

import com.marklogic.client.expression.CtsExpr;
import com.marklogic.client.type.CtsBoxExpr;
 import com.marklogic.client.type.CtsBoxSeqExpr;
 import com.marklogic.client.type.CtsCircleExpr;
 import com.marklogic.client.type.CtsCircleSeqExpr;
 import com.marklogic.client.type.CtsPointExpr;
 import com.marklogic.client.type.CtsPointSeqExpr;
 import com.marklogic.client.type.CtsPolygonExpr;
 import com.marklogic.client.type.CtsPolygonSeqExpr;
 import com.marklogic.client.type.CtsRegionExpr;
 import com.marklogic.client.type.CtsRegionSeqExpr;
 import com.marklogic.client.type.XsStringExpr;
 import com.marklogic.client.type.XsStringSeqExpr;

import com.marklogic.client.impl.BaseTypeImpl;

// IMPORTANT: Do not edit. This file is generated.

public class CtsExprImpl extends CtsQueryExprImpl implements CtsExpr {
    private XsExprImpl xs = null;
    public CtsExprImpl(XsExprImpl xs) {
        super(xs);
        this.xs = xs;
    }
     @Override
        public XsStringSeqExpr stem(XsStringExpr text) {
        return new XsExprImpl.XsStringSeqCallImpl("cts", "stem", new Object[]{ text });
    }
    @Override
        public XsStringSeqExpr stem(XsStringExpr text, String language) {
        return stem(text, (language == null) ? null : xs.string(language)); 
    }
    @Override
        public XsStringSeqExpr stem(XsStringExpr text, XsStringExpr language) {
        return new XsExprImpl.XsStringSeqCallImpl("cts", "stem", new Object[]{ text, language });
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr text) {
        return new XsExprImpl.XsStringSeqCallImpl("cts", "tokenize", new Object[]{ text });
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr text, String language) {
        return tokenize(text, (language == null) ? null : xs.string(language)); 
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr text, XsStringExpr language) {
        return new XsExprImpl.XsStringSeqCallImpl("cts", "tokenize", new Object[]{ text, language });
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr text, String language, String field) {
        return tokenize(text, (language == null) ? null : xs.string(language), (field == null) ? null : xs.string(field)); 
    }
    @Override
        public XsStringSeqExpr tokenize(XsStringExpr text, XsStringExpr language, XsStringExpr field) {
        return new XsExprImpl.XsStringSeqCallImpl("cts", "tokenize", new Object[]{ text, language, field });
    }     @Override
    public CtsBoxSeqExpr box(CtsBoxExpr... items) {
        return new CtsBoxSeqListImpl(items);
    }
     @Override
    public CtsCircleSeqExpr circle(CtsCircleExpr... items) {
        return new CtsCircleSeqListImpl(items);
    }
     @Override
    public CtsPointSeqExpr point(CtsPointExpr... items) {
        return new CtsPointSeqListImpl(items);
    }
     @Override
    public CtsPolygonSeqExpr polygon(CtsPolygonExpr... items) {
        return new CtsPolygonSeqListImpl(items);
    }
     @Override
    public CtsRegionSeqExpr region(CtsRegionExpr... items) {
        return new CtsRegionSeqListImpl(items);
    }
        static class CtsBoxSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxSeqExpr {
            CtsBoxSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsBoxSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxSeqExpr {
            CtsBoxSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsBoxCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsBoxExpr {
            CtsBoxCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsCircleSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleSeqExpr {
            CtsCircleSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsCircleSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleSeqExpr {
            CtsCircleSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsCircleCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsCircleExpr {
            CtsCircleCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsPointSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointSeqExpr {
            CtsPointSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsPointSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointSeqExpr {
            CtsPointSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsPointCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPointExpr {
            CtsPointCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsPolygonSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonSeqExpr {
            CtsPolygonSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsPolygonSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonSeqExpr {
            CtsPolygonSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsPolygonCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsPolygonExpr {
            CtsPolygonCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
         static class CtsRegionSeqListImpl extends BaseTypeImpl.BaseListImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionSeqExpr {
            CtsRegionSeqListImpl(Object[] items) {
                super(BaseTypeImpl.convertList(items));
            }
        }
        static class CtsRegionSeqCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionSeqExpr {
            CtsRegionSeqCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }
        static class CtsRegionCallImpl extends BaseTypeImpl.BaseCallImpl<BaseTypeImpl.BaseArgImpl> implements CtsRegionExpr {
            CtsRegionCallImpl(String fnPrefix, String fnName, Object[] fnArgs) {
                super(fnPrefix, fnName, BaseTypeImpl.convertList(fnArgs));
            }
        }

}
