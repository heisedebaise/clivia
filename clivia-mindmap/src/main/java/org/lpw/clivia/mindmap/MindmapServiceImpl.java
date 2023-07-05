package org.lpw.clivia.mindmap;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(MindmapModel.NAME + ".service")
public class MindmapServiceImpl implements MindmapService {
    @Inject
    private MindmapDao mindmapDao;
}
