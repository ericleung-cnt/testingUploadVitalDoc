package org.mardep.ssrs.dao.codetable;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.codetable.Office;
import org.springframework.stereotype.Repository;

@Repository
public class OfficeJpaDao extends AbstractJpaDao<Office, Integer> implements IOfficeDao {

}
