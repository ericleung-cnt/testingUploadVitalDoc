package org.mardep.ssrs.domain.seafarer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="SEAFARER_LICENSE")
@IdClass(CommonPK.class)
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"seafarerId", "seqNo"})
public class License extends AbstractPersistentEntity<CommonPK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "COURSE_NO", nullable=false)
	private Integer seqNo;

	@Id
	@Getter
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String seafarerId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFL_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "COURSE_DESC", length=400)
	private String courseDesc;
	
	@Override
	public CommonPK getId() {
		return new CommonPK(getSeqNo(), getSeafarerId());
	}

}
