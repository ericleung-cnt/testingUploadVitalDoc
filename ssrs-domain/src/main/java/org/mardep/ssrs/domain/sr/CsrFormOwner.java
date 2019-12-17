package org.mardep.ssrs.domain.sr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="CSR_FORM_OWNERS")
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"id", "imoNo", "formSeq"})
public class CsrFormOwner extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "IMO_NO", nullable=false, length=9)
	private String imoNo;

	@Getter
	@Setter
	@Column(name = "FORM_SEQ", nullable=false)
	private Integer formSeq;

	@Getter
	@Setter
	@Column(name = "OWNER_TYPE", length=1)
	private String ownerType;// TODO enum?

	@Getter
	@Setter
	@Column(name = "OWNER_NAME", length=80)
	private String ownerName;

	@Getter
	@Setter
	@Column(name = "OWNER_ADDRESS1", length=80)
	private String address1;

	@Getter
	@Setter
	@Column(name = "OWNER_ADDRESS2", length=80)
	private String address2;

	@Getter
	@Setter
	@Column(name = "OWNER_EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "OWNER_ADDRESS3", length=80)
	private String address3;

	@Getter
	@Setter
	@Column(name = "RECORD_NO")
	private Integer recordNo;

	@Getter
	@Setter
	@Column(name = "REG_OWNER_ID", length=20)
	private String regOwnerId;

	@Getter
	@Setter
	@Column(name = "COMPANY_ID", length=20)
	private String companyId;

	@Override
	public Long getId() {
		return id;
	}

}
