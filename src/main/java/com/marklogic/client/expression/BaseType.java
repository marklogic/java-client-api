package com.marklogic.client.expression;

// types for server expressions
public interface BaseType {
	public interface ItemSeqExpr { }
	public interface ItemExpr extends ItemSeqExpr { }

	public interface ItemParam extends ItemExpr {}

	public interface ItemSeqVal extends ItemSeqExpr {
		public ItemVal[] getItems();
	}
    public interface ItemVal extends ItemSeqVal, ItemParam { }

    public interface NamedItemParam extends ItemParam {
    	String getName();
    }

    public interface XMLMixedContentSeqExpr { }
    public interface XMLContentSeqExpr extends XMLMixedContentSeqExpr { }

    public interface NodeSeqExpr       extends ItemSeqExpr { }
	public interface NodeExpr          extends ItemExpr, NodeSeqExpr { }
	public interface DocumentSeqExpr   extends NodeSeqExpr { }
    public interface DocumentExpr      extends NodeExpr { }
	public interface ArraySeqExpr      extends NodeSeqExpr { }
    public interface ArrayExpr         extends NodeExpr, ArraySeqExpr { }
	public interface ObjectSeqExpr     extends NodeSeqExpr { }
    public interface ObjectExpr        extends NodeExpr, ObjectSeqExpr { }
	public interface ElementSeqExpr    extends NodeSeqExpr, XMLContentSeqExpr { }
    public interface ElementExpr       extends NodeExpr, ElementSeqExpr { }
	public interface AttributeSeqExpr  extends NodeSeqExpr { }
    public interface AttributeExpr     extends NodeExpr { }
	public interface CommentSeqExpr    extends NodeSeqExpr, XMLContentSeqExpr { }
    public interface CommentExpr       extends NodeExpr, CommentSeqExpr { }
	public interface PISeqExpr         extends NodeSeqExpr, XMLContentSeqExpr { }
    public interface PIExpr            extends NodeExpr, PISeqExpr { }
	public interface TextSeqExpr       extends NodeSeqExpr, XMLMixedContentSeqExpr { }
    public interface TextExpr          extends NodeExpr, TextSeqExpr { }
	public interface NumberSeqExpr     extends NodeSeqExpr { }
    public interface NumberExpr        extends NodeExpr, NumberSeqExpr { }
	public interface BooleanSeqExpr    extends NodeSeqExpr { }
    public interface BooleanExpr       extends NodeExpr, BooleanSeqExpr { }
	public interface NullSeqExpr       extends NodeSeqExpr { }
    public interface NullExpr          extends NodeExpr, NullSeqExpr { }
}
