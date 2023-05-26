package org.lpw.clivia.aliyun;

import com.aliyun.imagesearch20201214.Client;
import com.aliyun.imagesearch20201214.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service(AliyunModel.NAME + ".image")
public class AliyunImageImpl implements AliyunImage {
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private AliyunService aliyunService;
    @Inject
    private AliyunDao aliyunDao;

    @Override
    public boolean add(String key, String id, String name, String group, String uri) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return false;

        try {
            AddImageAdvanceRequest request = new AddImageAdvanceRequest();
            request.instanceName = aliyun.getInstanceName();
            request.productId = id;
            request.picName = name;
            request.strAttr = group;
            request.picContentObject = new FileInputStream(context.getAbsolutePath(uri));
            AddImageResponseBody body = new Client(aliyunService.config(aliyun)).addImageAdvance(request, new RuntimeOptions()).getBody();
            if (logger.isInfoEnable())
                logger.info("添加阿里云图片搜索[{}:{}]。", body.success, body.message);

            return true;
        } catch (Throwable throwable) {
            logger.warn(throwable, "添加阿里云图片搜索时发生异常！");

            return false;
        }
    }

    @Override
    public boolean add(String key, String id, String group, Map<String, String> map) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return false;

        try {
            AddImageAdvanceRequest request = new AddImageAdvanceRequest();
            request.instanceName = aliyun.getInstanceName();
            request.productId = id;
            request.strAttr = group;
            Client client = new Client(aliyunService.config(aliyun));
            for (String name : map.keySet()) {
                request.picName = name;
                request.picContentObject = new FileInputStream(context.getAbsolutePath(map.get(name)));
                AddImageResponseBody body = client.addImageAdvance(request, new RuntimeOptions()).getBody();
                if (logger.isInfoEnable())
                    logger.info("添加阿里云图片搜索[{}:{}]。", body.success, body.message);
            }

            return true;
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

        Set<String> set = new HashSet<>();
        String id = null;
        float s = 0;
        try {
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
                logger.info("搜索阿里云图片[{}:{}]。", group, set);
        } catch (Throwable throwable) {
            logger.warn(throwable, "搜索阿里云图片时发生异常！");
        }

        return s > score ? id : null;
    }

    @Override
    public boolean delete(String key, String id, String name) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return false;

        try {
            DeleteImageRequest request = new DeleteImageRequest();
            request.instanceName = aliyun.getInstanceName();
            request.productId = id;
            if (!validator.isEmpty(name))
                request.picName = name;
            DeleteImageResponseBody body = new Client(aliyunService.config(aliyun)).deleteImage(request).getBody();
            if (logger.isInfoEnable())
                logger.info("删除阿里云图片搜索[{}:{}]。", body.success, body.message);

            return true;
        } catch (Throwable throwable) {
            logger.warn(throwable, "删除阿里云图片搜索时发生异常！");

            return false;
        }
    }

    @Override
    public boolean delete(String key, String id, Set<String> names) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return false;

        try {
            DeleteImageRequest request = new DeleteImageRequest();
            request.instanceName = aliyun.getInstanceName();
            request.productId = id;
            Client client = new Client(aliyunService.config(aliyun));
            for (String name : names) {
                request.picName = name;
                DeleteImageResponseBody body = client.deleteImage(request).getBody();
                if (logger.isInfoEnable())
                    logger.info("删除阿里云图片搜索[{}:{}]。", body.success, body.message);
            }

            return true;
        } catch (Throwable throwable) {
            logger.warn(throwable, "添加阿里云图片搜索时发生异常！");

            return false;
        }
    }
}
