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
package net.sf.antcontrib.property;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;

/**
 * @author <a href="mailto:mattinger@yahoo.com">Matthew Inger</a>
 */
public class SortList extends AbstractPropertySetterTask {
    /**
     * Field value.
     */
    private String value;

    /**
     * Field ref.
     */
    private Reference ref;

    /**
     * Field casesensitive.
     */
    private boolean casesensitive = true;

    /**
     * Field numeric.
     */
    private boolean numeric = false;

    /**
     * Field delimiter.
     */
    private String delimiter = ",";

    /**
     * Field orderPropertyFile.
     */
    private File orderPropertyFile;

    /**
     * Field orderPropertyFilePrefix.
     */
    private String orderPropertyFilePrefix;

    /**
     * Constructor for SortList.
     */
    public SortList() {
        super();
    }

    /**
     * Method setNumeric.
     *
     * @param numeric boolean
     */
    public void setNumeric(boolean numeric) {
        this.numeric = numeric;
    }

    /**
     * Method setValue.
     *
     * @param value String
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Method setRefid.
     *
     * @param ref Reference
     */
    public void setRefid(Reference ref) {
        this.ref = ref;
    }

    /**
     * Method setCasesensitive.
     *
     * @param casesenstive boolean
     */
    public void setCasesensitive(boolean casesenstive) {
        this.casesensitive = casesenstive;
    }

    /**
     * Method setDelimiter.
     *
     * @param delimiter String
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Method setOrderPropertyFile.
     *
     * @param orderPropertyFile File
     */
    public void setOrderPropertyFile(File orderPropertyFile) {
        this.orderPropertyFile = orderPropertyFile;
    }

    /**
     * Method setOrderPropertyFilePrefix.
     *
     * @param orderPropertyFilePrefix String
     */
    public void setOrderPropertyFilePrefix(String orderPropertyFilePrefix) {
        this.orderPropertyFilePrefix = orderPropertyFilePrefix;
    }

    /**
     * Method mergeSort.
     *
     * @param src           String[]
     * @param dest          String[]
     * @param low           int
     * @param high          int
     * @param caseSensitive boolean
     * @param numeric       boolean
     */
    private static void mergeSort(String[] src,
                                  String[] dest,
                                  int low,
                                  int high,
                                  boolean caseSensitive,
                                  boolean numeric) {
        int length = high - low;

        // Insertion sort on smallest arrays
        if (length < 7) {
            for (int i = low; i < high; i++)
                for (int j = i; j > low
                        && compare(dest[j - 1], dest[j], caseSensitive, numeric) > 0; j--)
                    swap(dest, j, j - 1);
            return;
        }

        // Recursively sort halves of dest into src
        int mid = (low + high) / 2;
        mergeSort(dest, src, low, mid, caseSensitive, numeric);
        mergeSort(dest, src, mid, high, caseSensitive, numeric);

        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if (compare(src[mid - 1], src[mid], caseSensitive, numeric) <= 0) {
            System.arraycopy(src, low, dest, low, length);
            return;
        }

        // Merge sorted halves (now in src) into dest
        for (int i = low, p = low, q = mid; i < high; i++) {
            if (q >= high || p < mid && compare(src[p], src[q], caseSensitive, numeric) <= 0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    /**
     * Method compare.
     *
     * @param s1            String
     * @param s2            String
     * @param casesensitive boolean
     * @param numeric       boolean
     * @return int
     */
    private static int compare(String s1,
                               String s2,
                               boolean casesensitive,
                               boolean numeric) {
        int res = 0;

        if (numeric) {
            double d1 = Double.parseDouble(s1);
            double d2 = Double.parseDouble(s2);
            if (d1 < d2)
                res = -1;
            else if (d1 == d2)
                res = 0;
            else
                res = 1;
        } else if (casesensitive) {
            res = s1.compareTo(s2);
        } else {
            Locale l = Locale.getDefault();
            res = s1.toLowerCase(l).compareTo(s2.toLowerCase(l));
        }

        return res;
    }

    /**
     * Swaps x[a] with x[b].
     *
     * @param x Object[]
     * @param a int
     * @param b int
     */
    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    /**
     * Method sortByOrderPropertyFile.
     *
     * @param props Lis&lt;String&gt;
     * @return List&lt;String&gt;
     * @throws IOException if readline fails
     */
    private List<String> sortByOrderPropertyFile(List<String> props)
            throws IOException {
        FileReader fr = null;
        List<String> orderedProps = new ArrayList<String>();

        try {
            fr = new FileReader(orderPropertyFile);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            String pname = "";
            int pos = 0;
            while ((line = br.readLine()) != null) {
                pos = line.indexOf('#');
                if (pos != -1)
                    line = line.substring(0, pos).trim();

                if (line.length() > 0) {
                    pos = line.indexOf('=');
                    if (pos != -1)
                        pname = line.substring(0, pos).trim();
                    else
                        pname = line.trim();

                    String prefPname = pname;
                    if (orderPropertyFilePrefix != null)
                        prefPname = orderPropertyFilePrefix + "." + prefPname;

                    if (props.contains(prefPname)
                            && !orderedProps.contains(prefPname)) {
                        orderedProps.add(prefPname);
                    }
                }
            }

            fr.close();

            for (String prop : props) {
                if (!orderedProps.contains(prop))
                    orderedProps.add(prop);
            }

            return orderedProps;
        } finally {
            try {
                if (fr != null)
                    fr.close();
            } catch (IOException e) {
                // gulp
            }
        }
    }

    /**
     * Method validate.
     */
    protected void validate() {
        super.validate();
    }

    /**
     * Method execute.
     */
    public void execute() {
        validate();

        String val = value;
        if (val == null && ref != null)
            val = ref.getReferencedObject(getProject()).toString();

        if (val == null)
            throw new BuildException("Either the 'Value' or 'Refid' attribute must be set.");

        StringTokenizer st = new StringTokenizer(val, delimiter);
        List<String> vec = new ArrayList<String>(st.countTokens());
        while (st.hasMoreTokens())
            vec.add(st.nextToken());

        String[] propList = null;

        if (orderPropertyFile != null) {
            try {
                List<String> sorted = sortByOrderPropertyFile(vec);
                propList = sorted.toArray(new String[sorted.size()]);
            } catch (IOException e) {
                throw new BuildException(e);
            }
        } else {
            String[] s = vec.toArray(new String[vec.size()]);
            propList = new String[s.length];
            System.arraycopy(s, 0, propList, 0, s.length);
            mergeSort(s, propList, 0, s.length, casesensitive, numeric);
        }

        StringBuilder sb = new StringBuilder();
        for (String prop : propList) {
            if (sb.length() != 0) sb.append(delimiter);
            sb.append(prop);
        }

        setPropertyValue(sb.toString());
    }
}
