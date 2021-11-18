package org.lpw.clivia.pair;

import java.util.Set;

public interface PairService {
    int count(String owner);

    Set<String> values(String owner);

    boolean save(String owner, String value);

    void delete(String owner, String value);
}
