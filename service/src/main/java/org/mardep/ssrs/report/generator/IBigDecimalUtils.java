package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public interface IBigDecimalUtils {

	BigDecimal average(List<BigDecimal> bigDecimals, RoundingMode roundingMode);

}
