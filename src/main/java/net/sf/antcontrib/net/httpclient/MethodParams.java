/*
 * Copyright (c) 2001-2006 Ant-Contrib project.  All rights reserved.
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
package net.sf.antcontrib.net.httpclient;

import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.tools.ant.BuildException;

/**
 */
public class MethodParams extends HttpMethodParams {
    /**
     * Field serialVersionUID.
     * (value is -1)
     */
    private static final long serialVersionUID = -1;

    /**
     * Method setStrict.
     *
     * @param strict boolean
     */
    public void setStrict(boolean strict) {
        if (strict) {
            makeStrict();
        } else {
            makeLenient();
        }
    }

    /**
     * Method setVersion.
     *
     * @param version String
     */
    public void setVersion(String version) {
        try {
            setVersion(HttpVersion.parse(version));
        } catch (ProtocolException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Method addConfiguredDouble.
     *
     * @param param Params.DoubleParam
     */
    public void addConfiguredDouble(Params.DoubleParam param) {
        setDoubleParameter(param.getName(), param.getValue());
    }

    /**
     * Method addConfiguredInt.
     *
     * @param param Params.IntParam
     */
    public void addConfiguredInt(Params.IntParam param) {
        setIntParameter(param.getName(), param.getValue());
    }

    /**
     * Method addConfiguredLong.
     *
     * @param param Params.LongParam
     */
    public void addConfiguredLong(Params.LongParam param) {
        setLongParameter(param.getName(), param.getValue());
    }

    /**
     * Method addConfiguredString.
     *
     * @param param Params.StringParam
     */
    public void addConfiguredString(Params.StringParam param) {
        setParameter(param.getName(), param.getValue());
    }

    /**
     * Method addConfiguredBoolean.
     *
     * @param param Params.BooleanParam
     */
    public void addConfiguredBoolean(Params.BooleanParam param) {
        setBooleanParameter(param.getName(), param.getValue());
    }
}
