package org.mardep.ssrs.service;

import org.springframework.transaction.annotation.Transactional;

@Transactional("transactionManager_fsqcdb")
public abstract class AbstractFSQCService extends AbstractService implements IBaseService {

}
