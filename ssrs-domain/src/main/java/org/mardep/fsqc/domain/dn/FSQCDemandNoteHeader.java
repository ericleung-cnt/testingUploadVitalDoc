package org.mardep.fsqc.domain.dn;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="DEMAND_NOTE_HEADERS")
@NoArgsConstructor
//@Audited   //TODO: UNCOMMENT 
@ToString(of={"demandNoteNo"})
public class FSQCDemandNoteHeader extends AbstractPersistentEntity<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "DEMAND_NOTE_NO", nullable = false, length=15)
	private String demandNoteNo;
	
	@Getter
	@Setter
	@Column(name = "Imono", nullable = false, length=10)
	private String Imono;
	
	@Getter
	@Setter
	@Column(name = "GENERATION_TIME", nullable = false)
	private Date generationTime;
	
	
	
	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=80)
	private String address1;
	
	
	
	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=80)
	private String address2;
	
	
	
	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=80)
	private String address3;
	
	
	
	@Getter
	@Setter
	@Column(name = "BILLING_PERSON_TEL", length=18)
	private String BILLING_PERSON_TEL;
	
	
	
//	@Getter
//	@Setter
//	@Column(name = "BILLING_PERSON_EMAIL", length=50)
//	private String BILLING_PERSON_EMAIL;    
	
	
	@Getter
	@Setter
	@Column(name = "AMOUNT", nullable = false)
	private BigDecimal amount;
	
	
	
	@Getter
	@Setter
	@Column(name = "ADJUST_REASON", length=40)
	private String adjustReason;
	
	
	
	@Getter
	@Setter
	@Column(name = "ADJUST_TIME")
	private Date adjustTime;
	
	
	
	@Getter
	@Setter
	@Column(name = "RCP_AMOUNT")
	private BigDecimal rcpAmount;
	
	
	
	@Getter
	@Setter
	@Column(name = "ACCT_AMOUNT")
	private BigDecimal acctAmount;
	
	
	
	@Getter
	@Setter
	@Column(name = "CW_TIME")
	private Date cwTime;
	
	
	//  	 CW_STATUS_C="C"; // Cancel
	//       CW_STATUS_W="W"; //Write-off
	@Getter
	@Setter
	@Column(name = "CW_STATUS", length=1)
	private String cwStatus;
	
	
	
	@Getter
	@Setter
	@Column(name = "CW_BY", length=10)
	private String cwBy;
	
	
	
	@Getter
	@Setter
	@Column(name = "EBS_FLAG", length=1)
	private String ebsFlag;
	
	
	
	@Getter
	@Setter
	@Column(name = "FIRST_REMINDER_FLAG", length=1)
	private String firstReminderFlag;
	
	
	
	@Getter
	@Setter
	@Column(name = "FIRST_REMINDER_DATE")
	private Date firstReminderDate;
	
	
	
	@Getter
	@Setter
	@Column(name = "SECOND_REMINDER_FLAG", length=1)
	private String secondReminderFlag;
	
	
	
	@Getter
	@Setter
	@Column(name = "SECOND_REMINDER_DATE")
	private Date secondReminderDate;
	
	
	
	@Getter
	@Setter
	@Column(name = "DOJ_FLAG", length=1)
	private String DOJ_FLAG;
	
	
	
	@Getter
	@Setter
	@Column(name = "DOJ_DATE")
	private Date DOJ_DATE;
	
	
	
	@Getter
	@Setter
	@Column(name = "DEMAND_NOTE_STATUS", length=50)
	private String status;
	
	
	
	// 0 or 1  
	@Getter
	@Setter
	@Column(name = "PAYMENT_STATUS", length=50)
	private String paymentStatus;
	
	
	
	
	@Getter
	@Setter
	@Column(name = "CW_REMARK", length=180)
	private String cwRemark;
	
	
	
	@Getter
	@Setter
	@Column(name = "AMOUNT_PAID")
	private BigDecimal amountPaid;
	
	
	
	@Getter
	@Setter
	@Column(name = "DUE_DATE")
	private Date dueDate;
	
	
	
	@Getter
	@Setter
	@Column(name = "CO_NAME", length=160)
	private String coName;
	
	
	
	@Getter
	@Setter
	@Column(name = "BILL_NAME", length=160)
	private String billName;
	
	
	
	@Getter
	@Setter
	@Column(name = "GROSS_TON")
	private BigDecimal grossTon;
	
	
	
	@Getter
	@Setter
	@Column(name = "NET_TON")
	private BigDecimal netTon;
	
	
	
	@Getter
	@Setter
	@Column(name = "SHIP_NAME_CHI", length=255)
	private String shipNameChi;
	
	
	
	@Getter
	@Setter
	@Column(name = "SHIP_NAME_ENG", length=255)
	private String shipNameEng;
	
	
	
	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;
	
	
	
	@Getter
	@Setter
	@Column(name = "TEL", length=18)
	private String tel;
	
	
	
	@Getter
	@Setter
	@Column(name = "FAX", length=18)
	private String fax;



	@Override
	public String getId() {
		 return demandNoteNo;
	}

}
