package org.lpw.clivia.olcs;

import org.lpw.clivia.user.UserModel;

public interface OlcsConfig {
    String getNick(UserModel user);

    int overdue();
}
