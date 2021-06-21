package org.mardep.ssrs.domain.codetable;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="CREW_CR006")
//@IdClass(CrewPK.class)
//@ToString(of={"vesselId", "coverYymm", "referenceNo"})
public class Crew006 extends AbstractPersistentEntity<Integer> {


	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_ACTIVE ="A";
	public static final String STATUS_INACTIVE ="I";

	@Id
	@Getter
	@Setter
	@Column(name = "ID")
	@GeneratedValue
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "REF_NO", nullable=false)
	private Integer referenceNo;
	
	
	@Getter
	@Setter
	@Column(name = "IMO_NO", nullable=false, length =9)
	private String imoNo;
	
//
//
//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumns(value={
//			@JoinColumn(name="VESSEL_ID", referencedColumnName="VESSEL_ID", updatable=false, insertable=false),
//			@JoinColumn(name="COVER_YYMM", referencedColumnName="COVER_YYMM", updatable=false, insertable=false)
//	    }, foreignKey=@ForeignKey(name="CREW_CLC_FK")
//		)
//	private CrewListCover crewListCover;

	@Getter
	@Setter
	@Column(name = "CREW_NAME", length=50)
	private String crewName;
	

	@Getter
	@Setter
	@Column(name = "SEX", length=10)
	private String sex;
	

	@Getter
	@Setter
	@Column(name = "SERB_NO", length=15)
	private String serbNo;
	
	@Getter
	@Setter
	@Column(name = "NATIONALITY", length=50)
	private String nationality;
	
	@Getter
	@Setter
	@Column(name = "NATIONALITY_ID", nullable=false)
	private Long nationalityId;
	
////	TODO no FK here
//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="NATIONALITY_ID", referencedColumnName="NATIONALITY_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="C_NATIONALITY_FK"))
//	private Nationality nationality;


	@Column(name = "BIRTH_DATE", nullable=false)
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date birthDate;

	@Getter
	@Setter
	@Column(name = "BIRTH_PLACE", length=50)
	private String birthPlace;

	@Getter
	@Setter
	@Column(name = "CREW_ADDRESS", length=250)
	private String address;

	
	@Getter
	@Setter
	@Column(name = "NOK_NAME", length=50)
	private String nokName;
	
	@Getter
	@Setter
	@Column(name = "NOK_ADDRESS", length=250)
	private String nokAddress;

	
	@Getter
	@Setter
	@Column(name = "CAPACITY", length=50)
	private String capacity;


	@Getter
	@Setter
	@Column(name = "CAPACITY_ID", nullable=false)
	private Long capacityId;
	
	
//	TODO 
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="CAPACITY_ID", referencedColumnName="CAPACITY_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="C_R_FK"))
//	private Rank rank;

	@Getter
	@Setter
	@Column(name = "CREW_CERT", length=50)
	private String crewCert;

	@Getter
	@Setter
	@Column(name = "CURRENCY", nullable=false, length= 5)
	private String currency;
	
	
	@Getter
	@Setter
	@Column(name = "SALARY", nullable=false)
	private BigDecimal salary;
	
	@Getter
	@Setter
	@Column(name = "STATUS", length=1)
	private String status;
	

	@Column(name = "ENGAGE_DATE", nullable=false)
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	protected Date engageDate;

	@Getter
	@Setter
	@Column(name = "ENGAGE_PLACE", length=50)
	private String engagePlace;
	
	
	@Column(name = "EMPLOY_DATE", nullable=false)
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	private Date employDate;

	@Column(name = "EMPLOY_DURATION", nullable=false)
	@Getter
	@Setter
	protected Double employDuration;
	
	

	@Column(name = "DISCHARGE_DATE")
	@Temporal(TemporalType.DATE)
	@Getter
	@Setter
	protected Date dischargeDate;

	@Getter
	@Setter
	@Column(name = "DISCHARGE_PLACE", length=50)
	private String dischargePlace;


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((birthPlace == null) ? 0 : birthPlace.hashCode());
		result = prime * result + ((capacity == null) ? 0 : capacity.hashCode());
		result = prime * result + ((capacityId == null) ? 0 : capacityId.hashCode());
		result = prime * result + ((crewCert == null) ? 0 : crewCert.hashCode());
		result = prime * result + ((crewName == null) ? 0 : crewName.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((dischargeDate == null) ? 0 : dischargeDate.hashCode());
		result = prime * result + ((dischargePlace == null) ? 0 : dischargePlace.hashCode());
		result = prime * result + ((employDate == null) ? 0 : employDate.hashCode());
		result = prime * result + ((employDuration == null) ? 0 : employDuration.hashCode());
		result = prime * result + ((engageDate == null) ? 0 : engageDate.hashCode());
		result = prime * result + ((engagePlace == null) ? 0 : engagePlace.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imoNo == null) ? 0 : imoNo.hashCode());
		result = prime * result + ((nationality == null) ? 0 : nationality.hashCode());
		result = prime * result + ((nationalityId == null) ? 0 : nationalityId.hashCode());
		result = prime * result + ((nokAddress == null) ? 0 : nokAddress.hashCode());
		result = prime * result + ((nokName == null) ? 0 : nokName.hashCode());
		result = prime * result + ((referenceNo == null) ? 0 : referenceNo.hashCode());
		result = prime * result + ((salary == null) ? 0 : salary.hashCode());
		result = prime * result + ((serbNo == null) ? 0 : serbNo.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Crew006 other = (Crew006) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (birthPlace == null) {
			if (other.birthPlace != null)
				return false;
		} else if (!birthPlace.equals(other.birthPlace))
			return false;
		if (capacity == null) {
			if (other.capacity != null)
				return false;
		} else if (!capacity.equals(other.capacity))
			return false;
		if (capacityId == null) {
			if (other.capacityId != null)
				return false;
		} else if (!capacityId.equals(other.capacityId))
			return false;
		if (crewCert == null) {
			if (other.crewCert != null)
				return false;
		} else if (!crewCert.equals(other.crewCert))
			return false;
		if (crewName == null) {
			if (other.crewName != null)
				return false;
		} else if (!crewName.equals(other.crewName))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (dischargeDate == null) {
			if (other.dischargeDate != null)
				return false;
		} else if (!dischargeDate.equals(other.dischargeDate))
			return false;
		if (dischargePlace == null) {
			if (other.dischargePlace != null)
				return false;
		} else if (!dischargePlace.equals(other.dischargePlace))
			return false;
		if (employDate == null) {
			if (other.employDate != null)
				return false;
		} else if (!employDate.equals(other.employDate))
			return false;
		if (employDuration == null) {
			if (other.employDuration != null)
				return false;
		} else if (!employDuration.equals(other.employDuration))
			return false;
		if (engageDate == null) {
			if (other.engageDate != null)
				return false;
		} else if (!engageDate.equals(other.engageDate))
			return false;
		if (engagePlace == null) {
			if (other.engagePlace != null)
				return false;
		} else if (!engagePlace.equals(other.engagePlace))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (imoNo == null) {
			if (other.imoNo != null)
				return false;
		} else if (!imoNo.equals(other.imoNo))
			return false;
		if (nationality == null) {
			if (other.nationality != null)
				return false;
		} else if (!nationality.equals(other.nationality))
			return false;
		if (nationalityId == null) {
			if (other.nationalityId != null)
				return false;
		} else if (!nationalityId.equals(other.nationalityId))
			return false;
		if (nokAddress == null) {
			if (other.nokAddress != null)
				return false;
		} else if (!nokAddress.equals(other.nokAddress))
			return false;
		if (nokName == null) {
			if (other.nokName != null)
				return false;
		} else if (!nokName.equals(other.nokName))
			return false;
		if (referenceNo == null) {
			if (other.referenceNo != null)
				return false;
		} else if (!referenceNo.equals(other.referenceNo))
			return false;
		if (salary == null) {
			if (other.salary != null)
				return false;
		} else if (!salary.equals(other.salary))
			return false;
		if (serbNo == null) {
			if (other.serbNo != null)
				return false;
		} else if (!serbNo.equals(other.serbNo))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}


}
