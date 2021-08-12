package org.lpw.clivia.group;

import javax.inject.Inject;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;

public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private GroupDao groupDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return groupDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return GroupModel.NAME + ".not-exists";
    }
}
