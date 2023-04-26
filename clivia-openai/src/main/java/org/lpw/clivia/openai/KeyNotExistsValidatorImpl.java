package org.lpw.clivia.openai;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(OpenaiService.KEY_NOT_EXISTS_VALIDATOR)
public class KeyNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private OpenaiDao openaiDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        OpenaiModel openai = openaiDao.findByKey(parameters[1]);

        return openai == null || openai.getId().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return OpenaiModel.NAME + ".key.exists";
    }
}
