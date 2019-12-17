package org.mardep.ssrs.domain.sr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="BUILDER_MAKERS")
@IdClass(BuilderMakerPK.class)
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "builderCode", "name"})
@EntityListeners(SrEntityListener.class)
public class BuilderMaker extends AbstractPersistentEntity<BuilderMakerPK> implements Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "BUILDER_CODE", nullable=false, length=1)
	private String builderCode;

	@Id
	@Getter
	@Setter
	@Column(name = "BUILDER_NAME1", nullable=false, length=80)
	private String name;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="FK_BUILDER_MAKERS_REG_MASTERS")
	)
	private RegMaster regMaster;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=40)
	private String address1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=40)
	private String address2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=40)
	private String address3;

	@Getter
	@Setter
	@Column(name = "BUILDER_EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "MAJOR", length=1)
	private String major;

	@Override
	public BuilderMakerPK getId() {
		return new BuilderMakerPK(getApplNo(), getBuilderCode(), getName());
	}

	@Override
	public BuilderMaker clone() {
		try {
			BuilderMaker result = (BuilderMaker) super.clone();
			result.setVersion(null);
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
