package net.sf.antcontrib.net;

public interface IvyAdapter {
    void configure(URLImportTask task);
    void fileset(URLImportTask task, String setId);
}
