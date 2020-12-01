package org.lpw.clivia.user;

import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.ctrl.Forward;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(UserModel.NAME + ".ctrl")
@Execute(name = "/user/", key = UserModel.NAME, code = "151")
public class UserCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private Forward forward;
    @Inject
    private UserService userService;

    @Execute(name = "inviter")
    public Object inviter() {
        return userService.inviter(request.get("code"));
    }

    @Execute(name = "sign-up", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "uid", failureCode = 2),
            @Validate(validator = UserService.VALIDATOR_EXISTS_TYPE, parameter = "type", failureCode = 27),
            @Validate(validator = UserService.VALIDATOR_PASSWORD, parameters = {"password", "type"}, failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_SIGN_UP_ENABLE, failureCode = 99),
            @Validate(validator = AuthService.VALIDATOR_UID_NOT_EXISTS, parameters = {"uid", "password", "type"}, failureCode = 4)
    })
    public Object signUp() {
        userService.signUp(request.get("uid"), request.get("password"), request.get("type"), request.get("inviter"), request.get("grade"));

        return sign();
    }

    @Execute(name = "sign-in", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_EXISTS_TYPE, parameter = "type", failureCode = 27),
            @Validate(validator = UserService.VALIDATOR_PASSWORD, parameters = {"password", "type"}, failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_SIGN_IN, parameters = {"uid", "password", "type", "grade"}, failureCode = 6)
    })
    public Object signIn() {
        return sign();
    }

    @Execute(name = "sign")
    public Object sign() {
        return templates.get().success(userService.sign(), null);
    }

    @Execute(name = "sign-out")
    public Object signOut() {
        userService.signOut();

        return "";
    }

    @Execute(name = "modify", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "idcard", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "email", failureCode = 12),
            @Validate(validator = Validators.BETWEEN, number = {0, 2}, parameter = "gender", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "portrait", failureCode = 32),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object modify() {
        userService.modify(request.setToModel(UserModel.class));

        return sign();
    }

    @Execute(name = "password", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "new", failureCode = 14, failureArgKeys = {UserModel.NAME + ".password.new"}),
            @Validate(validator = Validators.NOT_EQUALS, parameters = {"old", "new"}, failureCode = 15, failureArgKeys = {UserModel.NAME + ".password.new", UserModel.NAME + ".password.old"}),
            @Validate(validator = Validators.EQUALS, parameters = {"new", "repeat"}, failureCode = 16, failureArgKeys = {UserModel.NAME + ".password.repeat", UserModel.NAME + ".password.new"}),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object password() {
        return userService.password(request.get("old"), request.get("new")) ? "" :
                templates.get().failure(151017, message.get(UserModel.NAME + ".password.illegal"), null, null);
    }

    @Execute(name = "secret", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "new", failureCode = 14, failureArgKeys = {UserModel.NAME + ".password.new"}),
            @Validate(validator = Validators.NOT_EQUALS, parameters = {"old", "new"}, failureCode = 15, failureArgKeys = {UserModel.NAME + ".password.new", UserModel.NAME + ".password.old"}),
            @Validate(validator = Validators.EQUALS, parameters = {"new", "repeat"}, failureCode = 16, failureArgKeys = {UserModel.NAME + ".password.repeat", UserModel.NAME + ".password.new"}),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object secret() {
        return userService.secret(request.get("old"), request.get("new")) ? "" :
                templates.get().failure(151018, message.get(UserModel.NAME + ".secret.illegal"), null, null);
    }

    @Execute(name = "get", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "ids", failureCode = 21),
            @Validate(validator = Validators.SIGN)
    })
    public Object get() {
        return userService.get(request.getAsArray("ids"));
    }

    @Execute(name = "find-by-code", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 26),
            @Validate(validator = Validators.SIGN)
    })
    public Object find() {
        return templates.get().success(userService.findByCode(request.get("code")), null);
    }

    @Execute(name = "find-by-uid", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 27),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = AuthService.VALIDATOR_UID_EXISTS, parameter = "uid", failureCode = 28)
    })
    public Object findByUid() {
        return templates.get().success(userService.findByUid(request.get("uid")), null);
    }

    @Execute(name = "find-sign", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "idUidCode", failureCode = 31),
            @Validate(validator = Validators.SIGN)
    })
    public Object findOrSign() {
        return templates.get().success(userService.findOrSign(request.get("idUidCode")), null);
    }

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return userService.query(request.get("uid"), request.get("idcard"), request.get("name"), request.get("nick"),
                request.get("mobile"), request.get("email"), request.get("code"), request.getAsInt("minGrade", -1),
                request.getAsInt("maxGrade", -1), request.getAsInt("state", -1), request.get("register"));
    }

    @Execute(name = "create", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "uid", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "idcard", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "email", failureCode = 12),
            @Validate(validator = Validators.BETWEEN, number = {0, 2}, parameter = "gender", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "portrait", failureCode = 32),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 24),
            @Validate(validator = AuthService.VALIDATOR_UID_NOT_EXISTS, parameter = "uid", failureCode = 4)
    })
    public Object create() {
        userService.create(request.get("uid"), request.get("password"), request.get("idcard"), request.get("name"), request.get("nick"),
                request.get("mobile"), request.get("email"), request.get("portrait"), request.getAsInt("gender"), request.getAsSqlDate("birthday"),
                userService.id(), 0, request.getAsInt("state"));

        return "";
    }

    @Execute(name = "update", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "idcard", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "email", failureCode = 12),
            @Validate(validator = Validators.BETWEEN, number = {0, 2}, parameter = "gender", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "portrait", failureCode = 32),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 24),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)
    })
    public Object update() {
        userService.update(request.setToModel(UserModel.class));

        return "";
    }

    @Execute(name = "reset-password", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)
    })
    public Object resetPassword() {
        return templates.get().success("", UserModel.NAME + ".reset-password.success", userService.resetPassword(request.get("id")));
    }

    @Execute(name = "grade", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.BETWEEN, number = {0, 98}, parameter = "grade", failureCode = 23),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)
    })
    public Object grade() {
        userService.grade(request.get("id"), request.getAsInt("grade"));

        return "";
    }

    @Execute(name = "state", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 24),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)
    })
    public Object state() {
        userService.state(request.get("id"), request.getAsInt("state"));

        return "";
    }

    @Execute(name = "count", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object count() {
        return userService.count();
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)
    })
    public Object delete() {
        userService.delete(request.get("id"));

        return "";
    }
}
