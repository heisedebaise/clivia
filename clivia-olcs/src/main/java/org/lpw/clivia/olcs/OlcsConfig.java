package org.lpw.clivia.olcs;

import org.lpw.clivia.user.UserModel;

public interface OlcsConfig {
    String getName(UserModel user);

    int overdue();
}
