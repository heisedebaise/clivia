package org.lpw.clivia.pair;

import org.springframework.stereotype.Service;

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
