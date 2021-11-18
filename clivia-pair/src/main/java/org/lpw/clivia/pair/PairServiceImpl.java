package org.lpw.clivia.pair;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

@Service(PairModel.NAME + ".service")
public class PairServiceImpl implements PairService {
    @Inject
    private PairDao pairDao;

    @Override
    public int count(String owner) {
        return pairDao.count(owner);
    }

    @Override
    public int count(String owner, String value) {
        return pairDao.count(owner, value);
    }

    @Override
    public Set<String> values(String owner) {
        Set<String> set = new HashSet<>();
        pairDao.query(owner).getList().forEach(pair -> set.add(pair.getValue()));

        return set;
    }

    @Override
    public boolean save(String owner, String value) {
        if (pairDao.count(owner, value) > 0)
            return false;

        PairModel pair = new PairModel();
        pair.setOwner(owner);
        pair.setValue(value);
        try {
            pairDao.save(pair);

            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    public void delete(String owner, String value) {
        pairDao.delete(owner, value);
    }
}
