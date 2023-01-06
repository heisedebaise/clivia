package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.olcs.member.MemberService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;

@Service(OlcsModel.NAME + ".service")
public class OlcsServiceImpl implements OlcsService {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Generator generator;
    @Inject
    private Message message;
    @Inject
    private Session session;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private MemberService memberService;
    @Inject
    private OlcsDao olcsDao;
    private final String casual = "olcsolcs-";

    @Override
    public JSONArray query(String user, Timestamp time) {
        JSONArray array = new JSONArray();
        olcsDao.query(user, time).getList().forEach(olcs -> {
            JSONObject object = new JSONObject();
            object.put("id", olcs.getId());
            if (olcs.getReplier() != null)
                object.put("replier", userService.get(olcs.getReplier()));
            object.put("genre", olcs.getGenre());
            object.put("content", olcs.getContent());
            object.put("time", dateTime.toString(olcs.getTime()));
            array.add(object);
        });

        return array;
    }

    @Override
    public JSONArray user(Timestamp time) {
        return query(userService.id(), time);
    }

    @Override
    public void ask(String genre, String content) {
        String user = userService.id();
        if (validator.isEmpty(user)) {
            user = session.get(OlcsModel.NAME);
            if (validator.isEmpty(user)) {
                user = casual + dateTime.toString(dateTime.now(), "yyyy-MMdd-HHmm-ssSSS") + generator.random(7);
                session.set(OlcsModel.NAME, user);
            }
        }
        save(user, null, genre, content);
    }

    @Override
    public void reply(String user, String genre, String content) {
        save(user, userService.id(), genre, content);
    }

    private void save(String user, String replier, String genre, String content) {
        OlcsModel olcs = new OlcsModel();
        olcs.setUser(user);
        olcs.setReplier(replier);
        olcs.setGenre(genre);
        olcs.setContent(content);
        olcs.setTime(dateTime.now());
        olcsDao.save(olcs);
        memberService.save(user, genre.equals("text") ? content : message.get(OlcsModel.NAME + ".genre." + genre));
    }
}
