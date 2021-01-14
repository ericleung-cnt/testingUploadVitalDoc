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
@Table(name="VITALDOC_CREATE_IMO_FOLDER")
public class VitaldocCreateImoFolder  extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "LOG_ID", nullable=false)
	private Long logId;
	
	@Getter
	@Setter
	@Column(name = "IMO", nullable=false)
	private String imo;
	
	@Getter
	@Setter
	@Column(name = "IMO_FOLDER_CREATED")
	private String imoFolderCreated;
	
	@Getter
	@Setter
	@Column(name = "VITALDOC_RETURN")
	private String vitaldocReturn;
	
	@Override
	public Long getId() {
		return logId;
	}


}
