package org.lpw.clivia.account;

import org.lpw.photon.dao.orm.PageList;

/**
 * @author lpw
 */
interface AccountDao {
    PageList<AccountModel> query(String user, String owner, int type, int minBalance, int maxBalance, int pageSize, int pageNum);

    AccountModel findById(String id);

    AccountModel find(String user, String owner, int type);

    void save(AccountModel account);
}
