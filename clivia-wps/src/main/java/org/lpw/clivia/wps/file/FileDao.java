package org.lpw.clivia.wps.file;

/**
 * @author lpw
 */
interface FileDao {
    FileModel findById(String id);

    FileModel find(String wps, String uri, int permission);

    void save(FileModel file);

    void delete(String wps);
}
