package org.mardep.ssrs.domain.codetable;

import java.security.acl.Group;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.user.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="USER_GROUPS")
//@Deprecated
public class UserGroup extends AbstractPersistentEntity<Integer> {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USER_ID", nullable = false, length=10)
	private String userId;


	@Id
	@Column(name = "GROUP_ID", nullable = false)
	private Long groupId;
	
	@Column(name = "IS_SUPER")
	private Boolean isSuper;
	
	@Column(name = "EFFECTIVE_DATE")  
	@Temporal(TemporalType.DATE)
	private Date effectiveDate;

	@Column(name = "EXPIRY_DATE")  
	@Temporal(TemporalType.DATE)
	private Date expiryDate;
	
	public UserGroup(){
		super();
	}



	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Boolean getIsSuper() {
		return isSuper;
	}

	public void setIsSuper(Boolean isSuper) {
		this.isSuper = isSuper;
	}

	@Override
	public Integer getId() {
		return groupId.intValue();
	}
}
