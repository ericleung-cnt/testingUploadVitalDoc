package org.mardep.ssrs.domain.fsqc;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class FsqcCertProgress {

	@Getter
	@Setter
	private String imono;
	
	@Getter
	@Setter
	private String certType;
	
	@Getter
	@Setter
	private String certStatus;
	
	@Getter
	@Setter
	private Date certCompleteDate;
	
	@Getter
	@Setter
	private Date certExpiryDate;
}
