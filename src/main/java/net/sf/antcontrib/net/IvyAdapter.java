package net.sf.antcontrib.net;

/**
 *
 */
public interface IvyAdapter {
    /**
     * Method configure().
     *
     * @param task URLImportTask
     */
    void configure(URLImportTask task);

    /**
     * Method fileset().
     *
     * @param task URLImportTask
     * @param setId setId
     */
    void fileset(URLImportTask task, String setId);
}
