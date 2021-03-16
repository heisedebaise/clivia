package org.lpw.clivia.module;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ModuleService.VALIDATOR_NAME_NOT_EXISTS)
public class NameNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private ModuleDao moduleDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        ModuleModel module = moduleDao.findByName(parameters[1]);

        return module == null || module.getId().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return ModuleModel.NAME + ".name.exists";
    }
}
