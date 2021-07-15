package org.mardep.ssrs.dmi.codetable;

import org.mardep.ssrs.domain.codetable.CurrencyExchangeCode;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CurrencyExchangeCodeDMI extends AbstractCodeTableDMI<CurrencyExchangeCode> {

	@Override
	public DSResponse fetch(CurrencyExchangeCode entity, DSRequest dsRequest){
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CurrencyExchangeCode entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(CurrencyExchangeCode entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
