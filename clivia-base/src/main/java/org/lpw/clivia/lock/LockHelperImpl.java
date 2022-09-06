package org.lpw.clivia.lock;

import org.lpw.photon.atomic.Atomicable;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Thread;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Service(LockModel.NAME + ".helper")
public class LockHelperImpl implements LockHelper, Atomicable, SecondsJob {
    @Inject
    private Digest digest;
    @Inject
    private Thread thread;
    @Inject
    private Validator validator;
    @Inject
    private LockDao lockDao;
    private final ThreadLocal<Set<String>> ids = new ThreadLocal<>();

    @Override
    public String lock(String key, long wait, int alive) {
        if (key == null)
            return null;

        String md5 = digest.md5(key);
        LockModel lock = new LockModel();
        lock.setMd5(md5);
        lock.setKey(key);
        lock.setCreate(System.currentTimeMillis());
        lock.setExpire(lock.getCreate() + 1000L * (alive > 0 ? alive : 5));
        lockDao.save(lock);

        if (ids.get() == null)
            ids.set(new HashSet<>());
        ids.get().add(lock.getId());
        for (long i = 0L; i < wait; i++) {
            LockModel model = lockDao.findByMd5(md5);
            if (model == null)
                return null;

            if (lock.getId().equals(model.getId()))
                return lock.getId();

            thread.sleep(1, TimeUnit.MilliSecond);
        }
        unlock(lock.getId());

        return null;
    }

    @Override
    public void unlock(String id) {
        if (id == null)
            return;

        lockDao.delete(id);
        ids.get().remove(id);
    }

    @Override
    public void fail(Throwable throwable) {
        close();
    }

    @Override
    public void close() {
        if (validator.isEmpty(ids.get()))
            return;

        Set<String> set = new HashSet<>(ids.get());
        set.forEach(this::unlock);
    }

    @Override
    public void executeSecondsJob() {
        lockDao.delete(System.currentTimeMillis());
    }
}
