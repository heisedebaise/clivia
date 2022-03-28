package org.lpw.clivia.group.member;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MemberService {
    String VALIDATOR_EXISTS = MemberModel.NAME + ".exists";
    String VALIDATOR_IN_GROUP = MemberModel.NAME + ".validator.in-group";

    Set<String> groups(String user, int type);

    Map<String, Integer> grades(String user, int type);

    List<MemberModel> list(String group);

    MemberModel find(String group, String user);

    String users(String group);

    String groups();

    void create(String group, Set<String> users, String owner);

    void memo(String id, String memo);

    void delete(String group);

    void delete(String group, String user);
}
