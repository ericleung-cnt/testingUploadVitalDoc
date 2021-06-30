package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "NATIONALITY_Mapping")
@NoArgsConstructor
public class NationalityMapping extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter
	@Getter
	@Column(name = "ID ", nullable = false)
	private Long mapID;

	@Getter
	@Setter
	@Column(name = "INPUT ", nullable = false, length = 50)
	private String INPUT;

	@Getter
	@Setter
	@Column(name = "OUTPUT ", length = 50)
	private String OUTPUT;

	@Override
	public Long getId() {
		return mapID;
	}



}
