package org.lpw.clivia.pair;

public interface PairService {
    int count(String owner);

    boolean save(String owner, String value);

    void delete(String owner, String value);
}
