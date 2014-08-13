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
        Client client = ClientBuilder.newClient();
        // set up digest auth
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.digest("admin", "admin");
        client.register(feature);
        // set up multipart response parsing
        client.register(MultiPartFeature.class);
        // set up x-www-form-urlencoded request params
        Form params = new Form()
            .param("xquery", "declare variable $myvar external;\"Hello+World: \"||$myvar")
            .param("evn0", "")
            .param("evl0", "myvar")
            .param("evt0", "xs:string")
            .param("evv0", "monsters")
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
            System.out.println("part body=[" + part.getEntityAs(String.class) + "]");
        }
    }
}
