package org.lpw.clivia.group.member;

import java.util.List;
import java.util.Set;

public interface MemberService {
    String VALIDATOR_EXISTS = MemberModel.NAME + ".exists";
    String VALIDATOR_IN_GROUP = MemberModel.NAME + ".validator.in-group";

    Set<String> user(int type);

    List<MemberModel> list(String group);

    MemberModel find(String group, String user);

    void create(String group, Set<String> users, String owner);

    void memo(String id, String memo);
}
