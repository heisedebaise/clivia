package org.lpw.clivia.group.member;

import java.util.List;
import java.util.Set;

public interface MemberService {
    Set<String> user(int type);

    List<MemberModel> list(String group);

    void create(String group, String[] users, String owner);
}
