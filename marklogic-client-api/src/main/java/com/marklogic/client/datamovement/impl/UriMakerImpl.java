/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */

package com.marklogic.client.datamovement.impl;

import com.marklogic.client.datamovement.Splitter;
import com.marklogic.client.io.marker.AbstractWriteHandle;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriMakerImpl<T extends AbstractWriteHandle>  implements Splitter.UriMaker {

    private String inputAfter;
    private String inputName;
    private String extension;
    private String name; //added for convenience

    @Override
    public String getInputAfter() {
        return inputAfter;
    }

    public void setInputAfter(String base) {
        inputAfter = base; // can be null, ""
    }

    public String getSplitFilename() {
        return inputName;
    }

    public void setSplitFilename(String name) {
        inputName = name; //can be null, ""
        setExtensionAndName();
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getName() {
        return this.name;
    }

    public void setExtensionAndName() {
        if (getSplitFilename() == null || getSplitFilename().length() == 0) {
            return;
        }

        Pattern extensionRegex = Pattern.compile("^(.*)\\.([^.]*)$");
        Matcher matcher = extensionRegex.matcher(getSplitFilename());
        boolean found = matcher.find();
        if (!found) {
            this.name = getSplitFilename();
            this.extension = null;
        } else {
            this.name = matcher.group(1);
            this.extension = matcher.group(2);
        }

    }

    public String makeUri(long num, T handle) {
        StringBuilder uri = new StringBuilder();

        if (getInputAfter() != null && getInputAfter().length() != 0) {
            uri.append(getInputAfter());
        }

        if (getSplitFilename() != null && getSplitFilename().length() != 0) {
            uri.append(getName());
        }

        if (uri.length() == 0) {
            uri.append("/");
        }

        uri.append(num).append("_").append(UUID.randomUUID()).append(".").append(getExtension());
        return uri.toString();
    }
}
