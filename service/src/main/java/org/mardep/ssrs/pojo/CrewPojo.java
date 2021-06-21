package org.mardep.ssrs.pojo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class CrewPojo {


	@Getter
	@Setter
	private Integer id;
	
	@Id
	@Getter
	@Setter
	private Integer referenceNo;


	@Getter
	@Setter
	private String crewName;
	

	@Getter
	@Setter
	private String sex;
	

	@Getter
	@Setter
	private String serbNo;
	
	@Getter
	@Setter
	private String nationality;
	
	@Getter
	@Setter
	private Long nationalityId;
	



	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyyMMdd")
	@Getter
	@Setter
	private Date birthDate;

	@Getter
	@Setter
	private String birthPlace;

	@Getter
	@Setter
	private String address;

	
	@Getter
	@Setter
	private String nokName;
	
	@Getter
	@Setter
	private String nokAddress;

	
	@Getter
	@Setter
	private String capacity;


	@Getter
	@Setter
	private Long capacityId;
	
	


	@Getter
	@Setter
	private String crewCert;

	@Getter
	@Setter
	private String currency;
	
	
	@Getter
	@Setter
	private BigDecimal salary;
	
	@Getter
	@Setter
	private String status;
	

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyyMMdd")
	@Getter
	@Setter
	protected Date engageDate;

	@Getter
	@Setter
	private String engagePlace;
	
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyyMMdd")
	@Getter
	@Setter
	private Date employDate;

	@Getter
	@Setter
	protected Double employDuration;
	
	

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern ="yyyyMMdd")
	@Getter
	@Setter
	protected Date dischargeDate;

	@Getter
	@Setter
	private String dischargePlace;




}