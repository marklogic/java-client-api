/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl;

import com.marklogic.client.document.ContentDescriptor;

// exposes BaseHandle protected methods in implementation package
public abstract class HandleImplementation<R,W>
  implements ContentDescriptor
{
  private boolean resendable = false;
  private long serverTimestamp = -1;
  private long pointInTimeQueryTimestamp = -1;

  protected HandleImplementation() {
    super();
  }

  /**
   * As part of the contract between a read handle and the API,
   * declares the class of the content received from the database.
   * You should rarely if ever need to call this method directly when using the handle.
   *
   */
  protected Class<R> receiveAs() {
    throw new UnsupportedOperationException(this.getClass().getName()+" cannot receive content");
  }
  /**
   * As part of the contract between a read handle and the API,
   * receives content from the database.  You should rarely
   * if ever need to call this method directly when using the handle.
   *
   */
  protected void receiveContent(R content) {
    throw new UnsupportedOperationException(this.getClass().getName()+" cannot receive content");
  }

  /**
   * As part of the contract between a write handle and the API,
   * sends content to the database.  You should rarely
   * if ever need to call this method directly when using the handle.
   *
   */
  protected W sendContent() {
    throw new UnsupportedOperationException(this.getClass().getName()+" cannot send content");
  }

  /**
   * As part of the contract between a write handle and the API,
   * specifies whether the content can be sent again if the request
   * must be retried.  The method returns false unless overridden.
   * You should rarely if ever need to call this method directly
   * when using the handle.
   * @return true if the content can be sent again; false otherwise
   */
  protected boolean isResendable() {
    return resendable;
  }
  /**
   * Specifies whether the content can be sent again if the request
   * must be retried.
   * @param resendable	true if the content can be sent again
   */
  protected void setResendable(boolean resendable) {
    this.resendable = resendable;
  }

  /**
   * Returns the server timestamp, whether set by setPointInTimeQueryTimestamp
   * or setResponseServerTimestamp.
   * @return the server timestamp whether set by setResponseServerTimestamp()
   *  or setPointInTimeQueryTimestamp()
   */
  public long getServerTimestamp() {
    return serverTimestamp;
  }
  /**
   * Only tracks the server timestamp that comes back as part of the response.
   * This method is only called internally when responses are received.
   * @param serverTimestamp the server timestamp returned by the server as part of a response
   */
  public void setResponseServerTimestamp(long serverTimestamp) {
    this.serverTimestamp = serverTimestamp;
  }
  /**
   * Only returns the server timestamp set by applications, which we trust to
   * mean they want their request to run at this timestamp.  This is the method
   * called internally to decide if we're sending the timestamp parameter as
   * part of the request.
   * @return the server timestamp set by calling setPointInTimeQueryTimestamp()
   */
  public long getPointInTimeQueryTimestamp() {
    return pointInTimeQueryTimestamp;
  }
  /**
   * Only tracks the server timestamp set by applications.  This method is the
   * one called by BaseHandle.setServerTimestamp(String) (which is the method
   * called by applications).
   * @param serverTimestamp the server timestamp at which the request should run
   */
  public void setPointInTimeQueryTimestamp(long serverTimestamp) {
    this.serverTimestamp = serverTimestamp;
    this.pointInTimeQueryTimestamp = serverTimestamp;
  }
}
