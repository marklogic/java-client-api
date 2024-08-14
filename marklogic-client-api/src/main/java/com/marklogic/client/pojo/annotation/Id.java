/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.pojo.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import com.marklogic.client.pojo.PojoRepository;

/** Use this annotation to specify the Id property for each pojo class.
 * To work properly with {@link PojoRepository#write PojoRepository.write},
 * each pojo class must have one and only one property annotated with
 * the Id annotation. The property annotated with Id is used to
 * generate a unique URI in MarkLogic Server
 * for each persisted instance, and thus should be a property with a
 * unique value for each instance.  <br><br>
 *
 * This annotation can be associated with a public field:
 * <pre>    import com.marklogic.client.pojo.annotation.Id;
 *    public class MyClass {
 *      <b>{@literal @}Id</b>
 *      public Long myId;
 *    }</pre>
 *
 * or with a public getter method:
 * <pre>
 *    public class MyClass {
 *      private Long myId;
 *      <b>{@literal @}Id</b>
 *      public Long getMyId() {
 *        return myId;
 *      }
 *      // ... setter methods ...
 *    }</pre>
 *
 * or with a public setter method:
 * <pre>
 *    public class MyClass {
 *      private Long myId;
 *
 *      // ... getter methods ...
 *
 *      <b>{@literal @}Id</b>
 *      public void setMyId(Long myId) {
 *        this.myId = myId;
 *      }
 *    }</pre>
 *
 * This annotation is used only at
 * runtime to generate unique uris, so there is no need to run
 * {@link com.marklogic.client.pojo.util.GenerateIndexConfig} to do
 * anything with this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
