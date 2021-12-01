package org.lpw.clivia.chat;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ChatModel.NAME + ".ctrl")
@Execute(name = "/chat/", key = ChatModel.NAME, code = "160")
public class ChatCtrl {
    @Inject
    private Request request;
    @Inject
    private ChatService chatService;

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.ID, parameter = "group", failureCode = 3),
            @Validate(validator = Validators.ID, parameter = "sender", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "genre", failureCode = 5),
            @Validate(validator = Validators.DATE_TIME, parameter = "time", failureCode = 6),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        chatService.save(request.setToModel(ChatModel.class));

        return "";
    }
}