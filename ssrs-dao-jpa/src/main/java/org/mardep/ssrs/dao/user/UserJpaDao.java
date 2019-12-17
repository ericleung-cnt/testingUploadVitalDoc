package org.mardep.ssrs.dao.user;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.user.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserJpaDao extends AbstractJpaDao<User, String> implements IUserDao {

}
