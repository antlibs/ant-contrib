package org.apache.tools.ant;

import java.util.Locale;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * DefaultLocaleRule class.
 */
public class DefaultLocaleRule extends TestWatcher {
    /**
     * Field originalDefault.
     */
    private Locale originalDefault;

    /**
     * Field currentDefault.
     */
    private Locale currentDefault;

    /**
     * Empty constructor.
     */
    public DefaultLocaleRule() {
        this(null);
    }

    /**
     * Constructor for a specific locale.
     *
     * @param defaultForTests Locale
     */
    public DefaultLocaleRule(final Locale defaultForTests) {
        currentDefault = defaultForTests;
    }

    @Override
    protected void starting(final Description description) {
        originalDefault = Locale.getDefault();

        if (null != currentDefault) {
            Locale.setDefault(currentDefault);
        }
    }

    @Override
    protected void finished(final Description description) {
        Locale.setDefault(originalDefault);
    }

    /**
     * Replace default locale
     *
     * @param locale Locale
     */
    public void setDefault(Locale locale) {
        if (null == locale) {
            locale = originalDefault;
        }
        Locale.setDefault(locale);
    }

    /**
     * A shorthand for ENGLISH locale
     *
     * @return DefaultLocaleRule
     */
    public static DefaultLocaleRule en() {
        return new DefaultLocaleRule(Locale.ENGLISH);
    }

    /**
     * A shorthand for GERMAN locale
     *
     * @return DefaultLocaleRule
     */
    public static DefaultLocaleRule de() {
        return new DefaultLocaleRule(Locale.GERMAN);
    }

    /**
     * A shorthand for FRENCH locale
     *
     * @return DefaultLocaleRule
     */
    public static DefaultLocaleRule fr() {
        return new DefaultLocaleRule(Locale.FRENCH);
    }
}
