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


@NoArgsConstructor
@Entity
@Table(name="CURRENCY_EXCHANGE_CODE")
public class CurrencyExchangeCode extends AbstractPersistentEntity<Integer> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "id", nullable=false, length=20)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "from_Dollar", length=50)
	private String from_Dollar;

	@Getter
	@Setter
	@Column(name = "to_Dollar", length=100)
	private String to_Dollar;

	@Getter
	@Setter
	@Column(name = "rate", nullable = false)
	private String rate;
	
	@Override
	public Integer getId() {
		return id;
	}
	


}
