package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportUtils implements IBigDecimalUtils {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public BigDecimal average(List<BigDecimal> bigDecimals, RoundingMode roundingMode) {
		BigDecimal sum = bigDecimals.stream().map(Objects::requireNonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		return sum.divide(new BigDecimal(bigDecimals.size()), roundingMode);
	}

}
