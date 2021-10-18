package org.lpw.clivia.console;

import javax.inject.Inject;

import org.lpw.photon.ctrl.context.Request;
import org.springframework.stereotype.Service;

@Service(ConsoleModel.NAME + ".helper")
public class ConsoleHelperImpl implements ConsoleHelper {
    @Inject
    private Request request;

    @Override
    public boolean search() {
        return request.getAsBoolean("console-grid-search");
    }
}
