package org.lpw.clivia.aliyun;

import com.aliyun.imagesearch20201214.Client;
import com.aliyun.imagesearch20201214.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Set;

@Service(AliyunModel.NAME + ".image")
public class AliyunImageImpl implements AliyunImage {
    @Inject
    private Context context;
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
    public Set<String> search(String key, String group, String uri) {
        AliyunModel aliyun = aliyunDao.findByKey(key);
        if (aliyun == null)
            return null;

        Set<String> set = new HashSet<>();
        try {
            SearchImageByPicAdvanceRequest request = new SearchImageByPicAdvanceRequest();
            request.instanceName = aliyun.getInstanceName();
            request.filter = "str_attr=\"" + group + "\"";
            request.picContentObject = new FileInputStream(context.getAbsolutePath(uri));
            SearchImageByPicResponseBody body = new Client(aliyunService.config(aliyun)).searchImageByPicAdvance(request, new RuntimeOptions()).getBody();
            for (SearchImageByPicResponseBody.SearchImageByPicResponseBodyAuctions actions : body.getAuctions())
                set.add(actions.productId);
            if (logger.isInfoEnable())
                logger.info("搜索阿里云图片[{}:{}]。", group, set);
        } catch (Throwable throwable) {
            logger.warn(throwable, "搜索阿里云图片时发生异常！");
        }

        return set;
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
}
