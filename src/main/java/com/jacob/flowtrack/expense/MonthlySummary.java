package com.jacob.flowtrack.expense;

import java.math.BigDecimal;

public interface MonthlySummary {

    Integer getYear();
    Integer getMonth();
    BigDecimal getTotal();

}
