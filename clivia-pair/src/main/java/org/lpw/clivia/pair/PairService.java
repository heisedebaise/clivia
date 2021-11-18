package org.lpw.clivia.pair;

import java.util.Set;

public interface PairService {
    int count(String owner);

    int count(String owner, String value);

    Set<String> values(String owner);

    boolean save(String owner, String value);

    void delete(String owner, String value);
}
