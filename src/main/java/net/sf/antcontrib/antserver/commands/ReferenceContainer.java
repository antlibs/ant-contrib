/*
 * Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.antcontrib.antserver.commands;

import java.io.Serializable;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
@SuppressWarnings("serial")
public class ReferenceContainer implements Serializable {
    /**
     * Field refId.
     */
    private String refId;

    /**
     * Field toRefId.
     */
    private String toRefId;

    /**
     * Constructor for ReferenceContainer.
     */
    public ReferenceContainer() {
        super();
    }

    /**
     * Method getRefId.
     *
     * @return String
     */
    public String getRefId() {
        return refId;
    }

    /**
     * Method setRefid.
     *
     * @param refId String
     */
    public void setRefid(String refId) {
        this.refId = refId;
    }

    /**
     * Method getToRefId.
     *
     * @return String
     */
    public String getToRefId() {
        return toRefId;
    }

    /**
     * Method setToRefId.
     *
     * @param toRefId String
     */
    public void setToRefId(String toRefId) {
        this.toRefId = toRefId;
    }
}
