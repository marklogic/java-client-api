/*
 * Copyright 2018-2019 MarkLogic Corporation
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

import com.marklogic.client.SessionState;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionStateImpl implements SessionState {
   private List<ClientCookie> cookies;
   private String sessionId;
   private AtomicBoolean setCreatedTimestamp;
   private Calendar created;

   public SessionStateImpl() {
      sessionId = Long.toUnsignedString(ThreadLocalRandom.current().nextLong(), 16);
      cookies = new ArrayList<>();
      setCreatedTimestamp = new AtomicBoolean(false);
   }

   @Override
   public String getSessionId() {
      return sessionId;
   }

   List<ClientCookie> getCookies() {
      return cookies;
   }

   void setCookies(List<ClientCookie> cookies) {
      if ( cookies != null ) {
         if(setCreatedTimestamp.compareAndSet(false, true)) {
            for (ClientCookie cookie : cookies) {
               // Drop the SessionId cookie received from the server. We add it every
               // time we make a request with a SessionState object passed
               if(cookie.getName().equalsIgnoreCase("SessionId")) continue;
               // make a clone to ensure we're not holding on to any resources
               // related to an HTTP connection that need to be released
               this.cookies.add(new ClientCookie(cookie));
            }
            created = Calendar.getInstance();
         }
      }
   }

   Calendar getCreatedTimestamp() {
      return created;
   }
}
