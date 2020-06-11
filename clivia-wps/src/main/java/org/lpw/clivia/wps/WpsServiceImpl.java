package org.lpw.clivia.wps;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Service(WpsModel.NAME + ".service")
public class WpsServiceImpl implements WpsService {
    @Inject
    private WpsDao wpsDao;
}
