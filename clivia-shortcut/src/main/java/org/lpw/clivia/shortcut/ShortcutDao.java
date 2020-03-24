package org.lpw.clivia.shortcut;

/**
 * @author lpw
 */
interface ShortcutDao {
    ShortcutModel find(String code);

    ShortcutModel find(String md5, int length);

    void save(ShortcutModel shortcut);
}
