package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.codetable.Port;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="APPL_DETAILS")
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo"})
@EntityListeners(SrEntityListener.class)
public class ApplDetail extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="BU_RMR_FK")
	)
	private RegMaster regMaster;

	@Getter
	@Setter
	@Column(name = "APPL_DATE")
	@Temporal(TemporalType.DATE)
	private Date applDate;

	@Getter
	@Setter
	@Column(name = "HULL_NO",length=20)
	private String hullNo;

	@Getter
	@Setter
	@Column(name = "PREV_NAME", length=40)
	private String prevName;

	@Getter
	@Setter
	@Column(name = "PREV_CNAME", length=30)
	private String prevChiName;

	@Getter
	@Setter
	@Column(name = "BUILDER_CERT", length=1)
	private String builderCert;

	@Getter
	@Setter
	@Column(name = "BILL_OF_SALE", length=1)
	private String billOfSale;

	@Getter
	@Setter
	@Column(name = "INCORP_HKID", length=1)
	private String incorpHkid;

	@Getter
	@Setter
	@Column(name = "FORM_AUTH", length=1)
	private String formAuth;

	@Getter
	@Setter
	@Column(name = "ENTITLE", length=1)
	private String entitle;

	@Getter
	@Setter
	@Column(name = "DELETION", length=1)
	private String deletion;

	@Getter
	@Setter
	@Column(name = "SURVEY_CERT", length=1)
	private String survey_cert;

	@Getter
	@Setter
	@Column(name = "CURR_TON_CERT", length=1)
	private String currTonCert;

	@Getter
	@Setter
	@Column(name = "UNDERTAKING", length=1)
	private String undertaking;

	@Getter
	@Setter
	@Column(name = "COURT_ORDER", length=1)
	private String courtOrder;

	@Getter
	@Setter
	@Column(name = "NOTE_ISSUE", length=1)
	private String noteIssue;

	@Getter
	@Setter
	@Column(name = "NOTE_RETURN", length=1)
	private String noteReturn;

	@Getter
	@Setter
	@Column(name = "OWNER_ENCUM", length=1)
	private String ownerEncum;

	@Getter
	@Setter
	@Column(name = "APPL_FULL_REG", length=1)
	private String appFullReg;

	@Getter
	@Setter
	@Column(name = "DEMISE_ENTITLE", length=1)
	private String demiseEntitle;

	@Getter
	@Setter
	@Column(name = "APPL_FORM", length=1)
	private String applForm;

	@Getter
	@Setter
	@Column(name = "DEMISE_EX_A", length=1)
	private String demiseExA;

	@Getter
	@Setter
	@Column(name = "DEMISE_EX_B", length=1)
	private String demiseExB;

	@Getter
	@Setter
	@Column(name = "DEMISE_INCORP_HKID", length=1)
	private String demiseIncorpHkid;

	@Getter
	@Setter
	@Column(name = "DEMISE_FORM_AUTH", length=1)
	private String demiseFormAuth;

	@Getter
	@Setter
	@Column(name = "REP_INCORP_HKID", length=1)
	private String repIncorpHkid;

	@Getter
	@Setter
	@Column(name = "REP_MEMO_ASSOC", length=1)
	private String repMemoAssoc;

	@Getter
	@Setter
	@Column(name = "REP_PREV_APPL", length=1)
	private String repPrevAppl;

	@Getter
	@Setter
	@Column(name = "AUDIT_INSP", length=1)
	private String auditInsp;

	@Getter
	@Setter
	@Column(name = "PREV_PORT", length=50)
	private String prevPort;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PREV_PORT", referencedColumnName="CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="RM_PREV_PORT_FK"))
//	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	private Port port;

	/**
	 * alter table APPL_DETAILS add PREV_PORT_COUNTRY nvarchar(50);
	 */
	@Getter
	@Setter
	@Column(name = "PREV_PORT_COUNTRY", length=50)
	private String prevPortCountry;

	@Getter
	@Setter
	@Column(name = "PREV_REG_YEAR")
	private Integer prevRegYear;

	@Getter
	@Setter
	@Column(name = "PRE_OFF_NO", length=9)
	private String preOffNo;

	@Column(name = "PROPOSE_REG_DATE")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date proposeRegDate;

	@Getter
	@Setter
	@Column(name = "PLACE_UPON_REGISTER", length=50)
	private String placeUponRegister;

	@Column(name = "CF_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date cfTime;

	@Getter
	@Setter
	@Column(name = "CS1_CLASS_SOC_CODE", length=50)
	private String cs1ClassSocCode;  //TODO

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CS1_CLASS_SOC_CODE", referencedColumnName="CLASS_SOC_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="CS1_CLASS_SOC_CODE"))
	private ClassSociety ClassSociety;

	@Getter
	@Setter
	@Column(name = "CC_COUNTRY_CODE_PREV_REG", length=3)
	private String ccCountryCodePrevReg;

	@Getter
	@Setter
	@Column(name = "EVIDENCE_DELETION", length=1)
	private String evidenceDeletion;

	@Getter
	@Setter
	@Column(name = "OWNER_APPOINT_REP", length=1)
	private String ownerAppoinRep;

	@Getter
	@Setter
	@Column(name = "LET_CONF_INCORP_CERT", length=1)
	private String letConfIncorpCert;

	@Getter
	@Setter
	@Column(name = "DOC_CERTIFIED_INCORP_CERT", length=1)
	private String docCertifiedIncorpCert;

	@Getter
	@Setter
	@Column(name="APPL_REG_REQUIRE", length=1)
	private String applRegRequire;
	@Getter
	@Setter
	@Column(name="APPL_SCENARIO", length=1)
	private String applScenario;
	@Getter
	@Setter
	@Column(name="BILL_OF_SALE_ATTORNEY", length=1)
	private String billOfSaleAttorney;
	@Getter
	@Setter
	@Column(name="BILL_OF_SALE_ATTORNEY_REQUIRE", length=1)
	private String billOfSaleAttorneyReq;
	@Getter
	@Setter
	@Column(name="BILL_OF_SALE_REQUIRE", length=1)
	private String billOfSaleReq;
	@Getter
	@Setter
	@Column(name="BUILDER_CERT_ATTORNEY", length=1)
	private String builderCertAttorney;
	@Getter
	@Setter
	@Column(name="BUILDER_CERT_ATTORNEY_REQUIRE", length=1)
	private String builderCertAttorneyReq;
	@Getter
	@Setter
	@Column(name="BUILDER_CERT_REQUIRE", length=1)
	private String builderCertReq;
	@Getter
	@Setter
	@Column(name="BUILDER_DECL_NOSEAL_REQUIRE", length=1)
	private String builderDeclNosealReq;
	@Getter
	@Setter
	@Column(name="BUILDER_DECLARATION_NOSEAL", length=1)
	private String builderDeclarationNoseal;
	@Getter
	@Setter
	@Column(name="CERT_DELETION_2REG", length=1)
	private String certDeletion2Reg;
	@Getter
	@Setter
	@Column(name="CERT_DELETION_2REG_REQUIRE", length=1)
	private String certDeletion2RegReq;
	@Getter
	@Setter
	@Column(name="CERT_DELETION_REQUIRE", length=1)
	private String certDeletionReq;
	@Getter
	@Setter
	@Column(name="CERT_OWNERSHIP_2REG", length=1)
	private String certOwnership2Reg;
	@Getter
	@Setter
	@Column(name="CERT_OWNERSHIP_2REG_REQUIRE", length=1)
	private String certOwnership2RegReq;
	@Getter
	@Setter
	@Column(name="CERT_OWNERSHIP_REQUIRE", length=1)
	private String certOwnershipReq;
	@Getter
	@Setter
	@Column(name="CERT_SURVEY_REQUIRE", length=1)
	private String certSurveyReq;
	@Getter
	@Setter
	@Column(name="COURT_ORDER_REQUIRE", length=1)
	private String courtOrderReq;
	@Getter
	@Setter
	@Column(name="DC_BUSINESS_REQUIRE", length=1)
	private String dcBusinessReq;
	@Getter
	@Setter
	@Column(name="DC_DECL_NOSEAL_REQUIRE", length=1)
	private String dcDeclNosealReq;
	@Getter
	@Setter
	@Column(name="DC_FORM_AUTH_REQUIRE", length=1)
	private String dcFormAuthReq;
	@Getter
	@Setter
	@Column(name="DC_REG_DOC_REQUIRE", length=1)
	private String dcReqDocReq;
	@Getter
	@Setter
	@Column(name="DECL_ENTITLE_DC_REQUIRE", length=1)
	private String declEntitleDcReq;
	@Getter
	@Setter
	@Column(name="DECL_ENTITLE_REQUIRE", length=1)
	private String declEntitleReq;
	@Getter
	@Setter
	@Column(name="DELIVERY_CONFRIM_REQUIRE", length=1)
	private String deliveryConfirmReq;
	@Getter
	@Setter
	@Column(name="EVIDENCE_DELETION_2REG", length=1)
	private String evidenceDeletion2Reg;
	@Getter
	@Setter
	@Column(name="EVIDENCE_DELETION_2REG_REQUIRE", length=1)
	private String evidenceDeletion2RegReq;
	@Getter
	@Setter
	@Column(name="EVIDENCE_DELETION_REQUIRE", length=1)
	private String evidenceDeletionReq;
	@Getter
	@Setter
	@Column(name="ITC_PROREG_REQUIRE", length=1)
	private String itcProregReq;
	@Getter
	@Setter
	@Column(name="MARKING_REQUIRE", length=1)
	private String markingReq;
	@Getter
	@Setter
	@Column(name="MORTGAGEE_CONSENT_2REG_REQUIRE", length=1)
	private String mortgageeConsent2RegReq;
	@Getter
	@Setter
	@Column(name="OWNER_BUSINESS_REQUIRE", length=1)
	private String ownerBusinessReq;
	@Getter
	@Setter
	@Column(name="OWNER_DC_TYPE", length=1)
	private String ownerDcType;
	@Getter
	@Setter
	@Column(name="OWNER_DECL_NOSEAL_REQUIRE", length=1)
	private String ownerDeclNosealReq;
	@Getter
	@Setter
	@Column(name="OWNER_ENCUM_REQUIRE", length=1)
	private String ownerEncumReq;
	@Getter
	@Setter
	@Column(name="OWNER_FORM_AUTH_REQUIRE", length=1)
	private String ownerFormAuthReq;
	@Getter
	@Setter
	@Column(name="OWNER_REG_DOC_REQUIRE", length=1)
	private String ownerRegDocReq;
	@Getter
	@Setter
	@Column(name="PRQC_CONFIRM_REQUIRE", length=1)
	private String prqcConfirmReq;
	@Getter
	@Setter
	@Column(name="RP_BUSINESS_REQUIRE", length=1)
	private String rpBusinessReq;
	@Getter
	@Setter
	@Column(name="RP_COMP_DOC_REQUIRE", length=1)
	private String rpCompDocReq;
	@Getter
	@Setter
	@Column(name="SELLER_DECL_NOSEAL_REQUIRE", length=1)
	private String sellerDeclNosealReq;
	@Getter
	@Setter
	@Column(name="SELLER_DECLARATION_NOSEAL", length=1)
	private String sellerDeclarationNoseal;
	@Getter
	@Setter
	@Column(name="TELEX_FAX_MARKING", length=1)
	private String telexFaxMarking;

	@Getter
	@Setter
	@Column(name="COS_INFO_STATE", length=3, nullable=false)
	private String cosInfoState = "APP";
	
	/**
	 * alter table APPL_DETAILS add CLIENT_DEREG_REASON integer;
	 * alter table APPL_DETAILS add CLIENT_DEREG_REMARK nvarchar(100);
	 */
	@Getter
	@Setter
	@Column(name="CLIENT_DEREG_REASON")
	private Integer clientDeregReason;
	
	@Getter
	@Setter
	@Column(name="CLIENT_DEREG_REMARK", length=100)
	private String clientDeregRemark;
	
	@Getter
	@Setter
	@Column(name="BUILDER_CERT_DOC")
	private String builderCertDoc;
	
	@Getter
	@Setter
	@Column(name="BILL_OF_SALE_DOC")
	private String billOfSaleDoc;
	
	@Getter
	@Setter
	@Column(name="OWNER_CO_REG_DOC")
	private String ownerCoRegDoc;
	
	@Getter
	@Setter
	@Column(name="FORM_AUTH_DOC")
	private String formAuthDoc;
	
	@Getter
	@Setter
	@Column(name="ENTITLE_DOC")
	private String entitleDoc;
	
	@Getter
	@Setter
	@Column(name="CERT_OF_DELETION_DOC")
	private String certOfDeletionDoc;
	
	@Getter
	@Setter
	@Column(name="CERT_OF_SURVEY_DOC")
	private String certOfSurveyDoc;
	
	@Getter
	@Setter
	@Column(name="PRQC_CONFIRMATION_DOC")
	private String prqcConfirmationDoc;
	
	@Getter
	@Setter
	@Column(name="COURT_ORDER_DOC")
	private String courtOrderDoc;
	
	@Getter
	@Setter
	@Column(name="DECLARATION_MARKING_DOC")
	private String declarationMarkingDoc;
	
	@Getter
	@Setter
	@Column(name="PDA_DOC")
	private String pdaDoc;
	
	@Getter
	@Setter
	@Column(name="DC_DECLARATION_DOC")
	private String dcDeclaratioDoc;
	
	@Getter
	@Setter
	@Column(name="APPLICATION_FORM_DOC")
	private String applicationFormDoc;
	
	@Getter
	@Setter
	@Column(name="DC_BUSINESS_REG_DOC")
	private String dcBusinessRegDoc;
	
	@Getter
	@Setter
	@Column(name="DC_COMPANY_REG_DOC")
	private String dcCompanyRegDoc;
	
	@Getter
	@Setter
	@Column(name="DC_FORM_AUTH_DOC")
	private String dcFormAuthDoc;
	
	@Getter
	@Setter
	@Column(name="RP_COMPANY_REG_DOC")
	private String rpCompanyRegDoc;
	
	@Getter
	@Setter
	@Column(name="RP_BUSINESS_REG_DOC")
	private String rpBusinessRegDoc;
	
	@Getter
	@Setter
	@Column(name="EVIDENCE_DELETION_DOC")
	private String evidenceDeletionDoc;
	
	@Getter
	@Setter
	@Column(name="ITC_FOR_PROREG_DOC")
	private String itcForProRegDoc;
	
	@Getter
	@Setter
	@Column(name="CERT_OF_OWNERSHIP_DOC")
	private String certOfOwnershipDoc;
	
	@Getter
	@Setter
	@Column(name="OWNER_BUSINESS_REG_DOC")
	private String ownerBusinessRegDoc;
	
	@Getter
	@Setter
	@Column(name="BILL_OF_SALE_ATTORNEY_DOC")
	private String billOfSaleAttorneyDoc;
	
	@Getter
	@Setter
	@Column(name="BUILDER_CERT_ATTORNEY_DOC")
	private String builderCertAttorneyDoc;
	
	@Getter
	@Setter
	@Column(name="BUILDER_DECLARATION_NOSEAL_DOC")
	private String builderDeclarationNosealDoc;
	
	@Getter
	@Setter
	@Column(name="CERT_DELETION_2REG_DOC")
	private String certDeletion2RegDoc;
	
	@Getter
	@Setter
	@Column(name="CERT_OWNERSHIP_2REG_DOC")
	private String certOwnership2RegDoc;
	
	@Getter
	@Setter
	@Column(name="EVIDENCE_DELETION_2REG_DOC")
	private String evidenceDeletion2RegDoc;
	
	@Getter
	@Setter
	@Column(name="SELLER_DECLARATION_NOSEAL_DOC")
	private String sellerDeclarationNosealDoc;
	
	@Getter
	@Setter
	@Column(name="OWNER_DECLARATION_NOSEAL_DOC")
	private String ownerDeclarationNosealDoc;
	
	@Getter
	@Setter
	@Column(name="OWNER_ENCUM_DOC")
	private String ownerEncumDoc;
	
	@Getter
	@Setter
	@Column(name="MORTGAGEE_CONSENT_DOC")
	private String mortgageeConsentDoc;
	
	@Override
	public String getId() {
		return getApplNo();
	}

}
