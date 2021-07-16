package org.mardep.ssrs.domain.codetable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="v_CrewView")
@Immutable
@ToString
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
	
	@Column(name = "EMPLOY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date employDate;
	
	
	@Getter
	@Setter
	@Column(name = "NATIONALITY_ID")
	private Long nationalityId;
	
	@Getter
	@Setter
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="NATIONALITY_ID", referencedColumnName="NATIONALITY_ID", updatable=false, insertable=false,foreignKey=@ForeignKey(name="C_NATIONALITY_FK0"))
	private Nationality nationality;
	

	@Getter
	@Setter
	@Column(name = "CAPACITY_ID")
	private Long capacityId;
	


	@Override
	public String getId() {
		return crewId;
	}
	

}
