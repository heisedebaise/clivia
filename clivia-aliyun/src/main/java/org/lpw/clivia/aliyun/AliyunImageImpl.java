package org.lpw.clivia.aliyun;

import com.aliyun.imagesearch20201214.Client;
import com.aliyun.imagesearch20201214.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import org.lpw.photon.util.Thread;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service(AliyunModel.NAME + ".image")
public class AliyunImageImpl implements AliyunImage {
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Thread thread;
    @Inject
    private Logger logger;
    @Inject
    private AliyunService aliyunService;
    @Inject
    private AliyunDao aliyunDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public boolean add(String key, String group, String id, String uri) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return false;

        delete(aliyun, id);
        if (validator.isEmpty(uri))
            return true;

        Future<Boolean> future = submit(aliyun, () -> {
            AddImageAdvanceRequest request = new AddImageAdvanceRequest();
            request.instanceName = aliyun.getInstanceName();
            request.productId = id;
            request.strAttr = group;
            Client client = new Client(aliyunService.config(aliyun));
            String[] uris = uri.split(",");
            for (int i = 0; i < uris.length; i++) {
                request.picName = numeric.toString(i);
                request.picContentObject = new FileInputStream(context.getAbsolutePath(uris[i]));
                AddImageResponseBody body = client.addImageAdvance(request, new RuntimeOptions()).getBody();
                if (logger.isInfoEnable())
                    logger.info("添加阿里云图片搜索[{}:{}]。", body.success, body.message);
            }

            return true;
        });

        try {
            return future.get();
        } catch (Throwable throwable) {
            logger.warn(throwable, "添加阿里云图片搜索时发生异常！");

            return false;
        }
    }

    @Override
    public String search(String key, String group, String uri, float score) {
        try (InputStream inputStream = new FileInputStream(context.getAbsolutePath(uri))) {
            return search(key, group, inputStream, score);
        } catch (Throwable throwable) {
            logger.warn(throwable, "搜索阿里云图片时发生异常！");

            return null;
        }
    }

    @Override
    public String search(String key, String group, InputStream inputStream, float score) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return null;

        Future<String> future = submit(aliyun, () -> {
            String id = null;
            float s = 0;
            SearchImageByPicAdvanceRequest request = new SearchImageByPicAdvanceRequest();
            request.instanceName = aliyun.getInstanceName();
            request.filter = "str_attr=\"" + group + "\"";
            request.picContentObject = inputStream;
            SearchImageByPicResponseBody body = new Client(aliyunService.config(aliyun)).searchImageByPicAdvance(request, new RuntimeOptions()).getBody();
            for (SearchImageByPicResponseBody.SearchImageByPicResponseBodyAuctions auctions : body.getAuctions()) {
                if (auctions.score > s) {
                    id = auctions.productId;
                    s = auctions.score;
                }
            }
            if (logger.isInfoEnable())
                logger.info("搜索阿里云图片[{}:{}]。", group);

            return s > score ? id : null;

        });

        try {
            return future.get();
        } catch (Throwable throwable) {
            logger.warn(throwable, "搜索阿里云图片时发生异常！");

            return null;
        }
    }

    @Override
    public boolean delete(String key, String id) {
        AliyunModel aliyun = aliyunDao.findByKey(key);

        return aliyun != null && delete(aliyun, id);
    }

    private boolean delete(AliyunModel aliyun, String id) {
        Future<Boolean> future = submit(aliyun, () -> {
            DeleteImageRequest request = new DeleteImageRequest();
            request.instanceName = aliyun.getInstanceName();
            request.productId = id;
            DeleteImageResponseBody body = new Client(aliyunService.config(aliyun)).deleteImage(request).getBody();
            if (logger.isInfoEnable())
                logger.info("删除阿里云图片搜索[{}:{}]。", body.success, body.message);

            return true;
        });

        try {
            return future.get();
        } catch (Throwable throwable) {
            logger.warn(throwable, "删除阿里云图片搜索时发生异常！");

            return false;
        }
    }

    private synchronized <T> Future<T> submit(AliyunModel aliyun, Callable<T> callable) {
        Future<T> future = executorService.submit(callable);
        executorService.submit(() -> sleep(aliyun));

        return future;
    }

    private void sleep(AliyunModel aliyun) {
        if (aliyun.getConcurrency() > 0)
            thread.sleep(1000 / aliyun.getConcurrency(), TimeUnit.MilliSecond);
    }
}
