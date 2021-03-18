package org.mardep.ssrs.domain.dn;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name="FSQC_DEMAND_NOTE_RECEIPTS")
@NoArgsConstructor
@Audited
@ToString(of={"demandNoteNo", "receiptNo"})
public class FSQCDemandNoteReceipt extends AbstractPersistentEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "RECEIPT_ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long receiptId;



//	@Id
	@Getter
	@Setter
	@Column(name = "DN_DEMAND_NOTE_NO", nullable=false, length=15)
	private String demandNoteNo;

////	TODO
//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="DN_DEMAND_NOTE_NO", referencedColumnName="DEMAND_NOTE_NO", updatable=false, insertable=false, foreignKey=@ForeignKey(name="DNRECEIPT_DN_FK"))
//	private DemandNoteHeader demandNoteHeader;

//	@Id
	@Getter
	@Setter
	@Column(name = "RECEIPT_NO", length=10, nullable=false)
	private String receiptNo;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INPUT_TIME", nullable=false)
	private Date inputTime;

	@Getter
	@Setter
	@Column(name = "AMOUNT", nullable=false, precision=10, scale=2)
	private BigDecimal amount;

	@Getter
	@Setter
	@Column(name = "CAN_ADJ_STATUS", length=1)
	private String canAdjStatus; // TODO enum ?

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CAN_ADJ_TIME")
	private Date canAdjTime;

	@Getter
	@Setter
	@Column(name = "CAN_ADJ_REMARK", length=180)
	private String canAdjRemark;

	@Getter
	@Setter
	@Column(name = "CAN_ADJ_BY", length=10)
	private String canAdjBy;

	@Getter
	@Setter
	@Column(name = "ACCOUNTED", length=1)
	private String accounted; // TODO boolean ?

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "REMARK", length=255)
	private String remark;


	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "STATUS", length=1)
	private String status;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "PAYMENT_TYPE", length=2)
	private String paymentType;


	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "ANNUAL_FEE_CODE", length=3)
	private String annualFeeCode;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "CANCEL_DATE")
	@Temporal(TemporalType.DATE)
	private Date cancelDate;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "CANCEL_BY", length=10)
	private String cancelById;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "REFUND_BY", length=10)
	private String refundById;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "REFUND_AMOUNT")
	private BigDecimal refundAmount;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "REFUND_REMARK", length=255)
	private String refundRemark;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "PAYMENT_VOUCHER_NO", length=50)
	private String paymentVoucherNo;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "REFUND_PAYMENT_DATE")
	@Temporal(TemporalType.DATE)
	private Date refundPaymentDate;

	/**
	 * add
	 */
	@Getter
	@Setter
	@Column(name = "REFUND_VOUCHER_DATE")
	@Temporal(TemporalType.DATE)
	private Date refundVoucherDate;

	@Getter
	@Setter
	@Column(name = "MACHINE_CODE", length=8)
	private String machineCode;

	@Override
	public Long getId() {
		return receiptId;
	}

}
