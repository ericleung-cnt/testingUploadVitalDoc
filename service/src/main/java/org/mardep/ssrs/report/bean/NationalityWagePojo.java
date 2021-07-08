package org.mardep.ssrs.report.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public class NationalityWagePojo{

	
	@Getter
	@Setter
	private String  rank;
	
	
	@Getter
	@Setter
	private String  nationality_ID;
	
	@Getter
	@Setter
	private String nationalityEngDesc ;
	
	@Getter
	@Setter
	private String currency ;
	
	@Getter
	@Setter
	private BigDecimal salary ;
	
	@Getter
	@Setter
	private BigDecimal USDsalary ;
	
	@Getter
	@Setter
	private BigDecimal sumUSDsalary ;
	
	@Getter
	@Setter
	private Long count ;
	
	
	@Getter
	@Setter
	private String shipTypeCode ;
	
	
	
	class RankShipTypeCodenation{
		public RankShipTypeCodenation(String rank2, String nationalityEngDesc2, String shipTypeCode2) {
			 rank =rank2;
			 nationalityEngDesc=nationalityEngDesc2;
			 shipTypeCode=shipTypeCode2;
		}
		private String  rank;
		private String nationalityEngDesc ;
		private String shipTypeCode ;
	}
	
	
	private RankShipTypeCodenation rankShipTypeCodenation ;
	
	
	public RankShipTypeCodenation getRankShipTypeCodenation() {
		return new RankShipTypeCodenation(rank,nationalityEngDesc,shipTypeCode);
	}
	

}
