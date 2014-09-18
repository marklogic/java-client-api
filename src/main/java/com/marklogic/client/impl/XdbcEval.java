package com.marklogic.client.impl;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class XdbcEval {
    public static void main(String[] args) {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.local.LocalTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.util.pipe.StandaloneTubeAssembler.dump", "true");
        Client client = ClientBuilder.newClient();
        // set up digest auth
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.digest("admin", "admin");
        client.register(feature);
        // set up multipart response parsing
        client.register(MultiPartFeature.class);
        // let's see what types we're getting
        String xquery = 
          "declare variable $myvar1 external;" +
          "declare variable $myvar2 external;" +
          "declare variable $myvar3 external;" +
          "declare variable $myvar4 external;" +
          "declare variable $myvar5 external;" +
          "declare variable $myvar6 external;" +
          "declare function local:getType($var) {" +
              "typeswitch($var) " +
                "case attribute() return 'attribute()'" +
                "case comment() return 'comment()'" +
                //"case document() return 'document()'" +
                "case element() return 'element()'" +
                "case node() return 'node()'" +
                //"case processingInstruction() return 'processingInstruction()'" +
                "case xs:base64Binary return 'xs:base64Binary'" +
                "case xs:boolean return 'xs:boolean'" +
                "case xs:byte return 'xs:byte'" +
                "case xs:date return 'xs:date'" +
                "case xs:dateTime return 'xs:dateTime'" +
                "case xs:dayTimeDuration return 'xs:dayTimeDuration'" +
                "case xs:short return 'xs:short'" +
                "case xs:integer return 'xs:integer'" +
                "case xs:int return 'xs:int'" +
                "case xs:long return 'xs:long'" +
                "case xs:decimal return 'xs:decimal'" +
                "case xs:float return 'xs:float'" +
                "case xs:double return 'xs:double'" +
                "case xs:duration return 'xs:duration'" +
                "case xs:unsignedInt return 'xs:unsignedInt'" +
                "case xs:unsignedLong return 'xs:unsignedLong'" +
                "case xs:unsignedShort return 'xs:unsignedShort'" +
                "case xs:string return 'xs:string'" +
                "case xs:time return 'xs:time'" +
                "case xs:yearMonthDuration return 'xs:yearMonthDuration'" +
                "case xs:anyAtomicType return 'xs:anyAtomicType'" +
                "case xs:anySimpleType return 'xs:anySimpleType'" +
                "case item() return 'item()'" +
                "default return 'unknown'" +
          "};" +
          "for $var at $i in ($myvar1, $myvar2, $myvar3, $myvar4, $myvar5, $myvar6) " +
          "return (" +
              "$var," +
              "'type' || $i || ': ' || local:getType($var)" +
          ")";
        // set up x-www-form-urlencoded request params
        Form params = new Form()
            .param("xquery", xquery)
            .param("evn0", "")
            .param("evl0", "myvar1")
            .param("evt0", "xs:int")
            .param("evv0", "1")
            .param("evn1", "")
            .param("evl1", "myvar2")
            .param("evt1", "xs:float")
            .param("evv1", "1.1")
            .param("evn2", "")
            .param("evl2", "myvar3")
            .param("evt2", "xs:decimal")
            .param("evv2", "1.1")
            .param("evn3", "")
            .param("evl3", "myvar4")
            .param("evt3", "xs:double")
            .param("evv3", "1.1")
            .param("evn4", "")
            .param("evl4", "myvar5")
            .param("evt4", "xs:long")
            .param("evv4", "999999999999999999")
            .param("evn5", "")
            .param("evl5", "myvar6")
            .param("evt5", "xs:string")
            .param("evv5", "test")
            .param("locale", "en_US")
            .param("tzoffset", "-21600")
            .param("dbname", "java-unittest");
        // make request
        Response res = client.target("http://localhost:8020/eval")
            .request()
            .buildPost(Entity.form(params))
            .invoke();
        // read response as multipart even though there's only one part
        MultiPart body = res.readEntity(MultiPart.class);
        System.out.println("response=[" + res  + "]");
        for ( BodyPart part : body.getBodyParts() ) {
            System.out.println("part body2=[" + part.getEntityAs(String.class) + "]");
        }
    }
}
