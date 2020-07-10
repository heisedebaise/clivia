package org.lpw.clivia.wps.file;

import org.lpw.clivia.wps.WpsModel;

/**
 * @author lpw
 */
public interface FileService {
    String preview(WpsModel wps, String uri, String name, int permission, String creator, long create);

    void delete(String wps);
}
