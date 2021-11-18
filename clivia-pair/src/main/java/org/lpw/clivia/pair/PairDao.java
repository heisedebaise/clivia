package org.lpw.clivia.pair;

interface PairDao {
    int count(String owner, String value);

    int count(String owner);

    void save(PairModel pair);

    void delete(String owner, String value);
}