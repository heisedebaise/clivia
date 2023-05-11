package org.lpw.clivia.aliyun;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(AliyunService.KEY_NOT_EXISTS_VALIDATOR)
public class KeyNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private AliyunDao aliyunDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        AliyunModel aliyun = aliyunDao.findByKey(parameters[1]);

        return aliyun == null || aliyun.getId().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return AliyunModel.NAME + ".key.exists";
    }
}
