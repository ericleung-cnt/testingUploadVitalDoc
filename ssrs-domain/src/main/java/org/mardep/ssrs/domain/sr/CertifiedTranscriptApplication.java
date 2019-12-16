package org.mardep.ssrs.domain.sr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.user.FuncEntitle;

import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the OCR_TRANSCRIPT database table.
 *
 */
@Entity
@Table(name="TRANSCRIPT_APPLICATION")
//@NamedQuery(name="OcrTranscript.findAll", query="SELECT o FROM OcrTranscript o")
public class CertifiedTranscriptApplication extends AbstractPersistentEntity<Long> {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "APPLICATION_ID", nullable=false)
	private Long id;
	
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", length=9)
	private String applNo;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ISSUE_DATE")
	private Date generationTime;
	
	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REPORT_DATE")
	private Date reportDate;
	
	@Getter
	@Setter
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="APPLICATION_ID", referencedColumnName="APPLICATION_ID", updatable=false, insertable=false)
	private Set<FuncEntitle> funcEntitles;
	
	@Getter
	@Setter
	@Column(name = "REPORT_GEN_BY", length=50)
	private String genBy;
	
	@Getter
	@Setter
	@Column(name = "PAYMENT_REQUIRED_IND")
	private Boolean paymentRequired;
	
	@Getter
	@Setter
	@Column(name = "DELAY_PAYMENT_REQUIRED_IND")
	private Boolean delayPaymentRequired;
	
	@Getter
	@Setter
	@Column(name = "REGISTRAR")
	private Long registrar;
	
	@Getter
	@Setter
	@Column(name = "IS_CERTIFIED_IND")
	private Boolean certified;
	
	@Getter
	@Setter
	@Column(name = "NOPAYMENTREASON", length=50)
	private String reason;
	
	@Getter
	@Setter
	@Column(name = "DELAYPAYMENTREASON", length=50)
	private String delayPaymentReason;
	
	@Getter
	@Setter
	@Column(name = "OFFICE_CODE", length=10, nullable=true)
	private String officeCode;
	
	@Getter
	@Setter
	@Column(name = "PAYMENT_STATUS", length=30)
	private String paymentStatus;
	
	@Getter
	@Setter
	@Column(name = "IS_PRINT_MORTGAGE_IND")
	private Boolean printMortgage;
	
	@Getter
	@Setter
	@Column(name = "DEMAND_NOTE_NO", length=50)
	private String demandNoteNo;
	
	@Getter
	@Setter
	@Column(name = "ISSUE_OFFICE", length=30)
	private String issueOffice;
	
	public String getIssueDateStr() {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.generationTime);
	}
	
	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
