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
@Table(name="CREW_LIST_COVER006")
//@IdClass(CrewPK.class)
//@ToString(of={"vesselId", "coverYymm", "referenceNo"})
public class CrewListCover006 extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "IMO_NO" , length =9)
	private String imoNo;
	
	@Getter
	@Setter
	@Column(name = "SHIP_NAME", length =140)
	private String shipName;
	

	@Getter
	@Setter
	@Column(name = "REG_PORT", length=100)
	private String regPort;
	

	@Getter
	@Setter
	@Column(name = "OFF_NO", length=18)
	private String offcialNo;


	@Override
	public String getId() {
		return imoNo;
	}
	




}
