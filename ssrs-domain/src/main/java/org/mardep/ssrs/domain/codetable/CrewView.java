package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="v_CrewView")
@Immutable
public class CrewView extends AbstractPersistentEntity<String> {


	private static final long serialVersionUID = 1L;

	
	@Id
	@Getter
	@Setter
	@Column(name = "uuid" )
	private String uuid;
	
	@Getter
	@Setter
	@Column(name = "IMO_NO" )
	private String imoNo;
	
	@Getter
	@Setter
	@Column(name = "SHIP_NAME")
	private String shipName;
	

	@Getter
	@Setter
	@Column(name = "REG_PORT")
	private String regPort;
	

	@Getter
	@Setter
	@Column(name = "OFF_NO")
	private String offcialNo;

	@Getter
	@Setter
	@Column(name = "ID", length=50)
	private String crewId;
	
	@Getter
	@Setter
	@Column(name = "CREW_NAME")
	private String crewName;

	@Getter
	@Setter
	@Column(name = "SERB_NO")
	private String serbNo;


	@Getter
	@Setter
	@Column(name = "REF_NO")
	private String refNo;
	
	


	@Override
	public String getId() {
		return crewId;
	}
	

}
