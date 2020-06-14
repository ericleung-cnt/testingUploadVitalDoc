package org.mardep.ssrs.fsqcwebService.pojo;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class FsqcCertResultPojo {

	@Getter
	@Setter
	private String applNo;
	
	@Getter
	@Setter
	private String imo;
	
	@Getter
	@Setter
	private Long docLinkId;
	
	@Getter
	@Setter
	private String certType;
	
	@Getter
	@Setter
	private String certResult;
	
	@Getter
	@Setter
	private Date certResultDate;
	
}
