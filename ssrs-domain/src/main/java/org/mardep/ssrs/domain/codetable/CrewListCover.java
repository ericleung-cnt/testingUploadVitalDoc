package org.mardep.ssrs.domain.codetable;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="CREW_LIST_COVER006")
//@IdClass(CrewPK.class)
//@ToString(of={"vesselId", "coverYymm", "referenceNo"})
public class CrewListCover extends AbstractPersistentEntity<String> {

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
	
	@Getter
	@Setter
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "crewListCover")
	private List<Crew> Crew;
	


	@Override
	public String getId() {
		return imoNo;
	}
	
	




}
