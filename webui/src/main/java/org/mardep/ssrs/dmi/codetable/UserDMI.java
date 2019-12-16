package org.mardep.ssrs.dmi.codetable;

import java.util.HashSet;
import java.util.Set;

import org.mardep.ssrs.domain.user.User;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class UserDMI extends AbstractCodeTableDMI<User> {

	@Override
	public DSResponse fetch(User entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(User entity, DSRequest dsRequest) throws Exception {
		if (entity.getCreatedBy() == null) {
			Set<Long> roles = entity.getRoleIds();
			entity.setRoleIds(new HashSet<>());
			DSResponse response = super.update(entity, dsRequest);
			if (response.getStatus() == DSResponse.STATUS_SUCCESS) {
				entity = (User) response.getData();
				entity.setRoleIds(roles);
				return super.update(entity, dsRequest);
			} else {
				return response;
			}
		}
		return super.update(entity, dsRequest);
	}

	@Override
	public DSResponse add(User entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

}
