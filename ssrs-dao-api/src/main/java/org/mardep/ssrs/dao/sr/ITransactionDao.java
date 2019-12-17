package org.mardep.ssrs.dao.sr;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.Transaction;

public interface ITransactionDao extends IBaseDao<Transaction, Long> {

	Transaction save(String applNo, String code, Transaction tx);

}
