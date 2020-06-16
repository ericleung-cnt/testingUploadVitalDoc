package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="FSQC_CERT_RESULT")
@NoArgsConstructor
public class FsqcCertResult extends AbstractPersistentEntity<Long> {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "FSQC_CERT_RESULT_ID", nullable=false)
	private Long id;

	
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", length=9)
	private String applNo;
	
	@Getter
	@Setter
	@Column(name = "IMO_NO", length=9)
	private String imo;
	
	@Getter
	@Setter
	@Column(name = "DOC_LINK_ID")
	private Long docLinkId;
	
	@Getter
	@Setter
	@Column(name = "CERT_TYPE", length=9)
	private String certType;
	
	@Getter
	@Setter
	@Column(name = "CERT_RESULT", length=9)
	private String certResult;
	
	@Getter
	@Setter
	@Column(name = "CERT_RESULT_DATE")
	@Temporal(TemporalType.DATE)
	private Date certResultDate;

	@Getter
	@Setter
	@Column(name = "CERT_EXPIRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date certExpiryDate;

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
