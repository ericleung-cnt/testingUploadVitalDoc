package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.sr.ICsrFormDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.CsrFormOwner;
import org.mardep.ssrs.domain.sr.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Leo.LIANG
 *
 */
@Service
@Transactional
public class CsrService extends AbstractService implements ICsrService{

	@Autowired private ICsrFormDao csrDao;
	@Autowired private IOwnerDao ownerDao;

	@Override
	public CsrForm save(CsrForm entity) {
		CsrForm last = getLast(entity.getImoNo());
		if (last != null && entity.getFormSeq() <= last.getFormSeq()) {
			CsrForm existing = csrDao.findById(entity.getId());
			if (existing == null) {
				throw new IllegalStateException("Form Seq not exists for update");
			}
			entity.setOwners(existing.getOwners());
		} else {
			// new csr form, copy owners to csrform owners
			List<Owner> owners = ownerDao.findByImo(entity.getImoNo());
			owners.forEach(owner->{
				CsrFormOwner csro = new CsrFormOwner();
				csro.setAddress1(owner.getAddress1());
				csro.setAddress2(owner.getAddress2());
				csro.setAddress3(owner.getAddress3());
				csro.setEmail(owner.getEmail());
				csro.setFormSeq(entity.getFormSeq());
				csro.setImoNo(entity.getImoNo());
				csro.setOwnerName(owner.getName());
				csro.setOwnerType(owner.getType());
				entity.getOwners().add(csro);
			});
		}
		return csrDao.save(entity);
	}

	@Override
	public CsrForm getLast(String imo) {
		CsrForm last = null;
		CsrForm criteria = new CsrForm();
		criteria.setImoNo(imo);
		List<CsrForm> existing = csrDao.findByCriteria(criteria);
		if (!existing.isEmpty()) {
			existing.sort((f1,f2)->{return f1.getFormSeq().compareTo(f2.getFormSeq());});
			last = existing.get(existing.size() - 1);
		}
		return last;
	}


}
