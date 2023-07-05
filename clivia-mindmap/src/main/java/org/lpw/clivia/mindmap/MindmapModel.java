package org.lpw.clivia.mindmap;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(MindmapModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = MindmapModel.NAME)
@Table(name = "t_mindmap")
public class MindmapModel extends ModelSupport {
    static final String NAME = "clivia.mindmap";

}