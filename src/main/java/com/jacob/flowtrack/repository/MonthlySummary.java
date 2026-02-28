package com.jacob.flowtrack.repository;

import java.math.BigDecimal;

public interface MonthlySummary {

    Integer getYear();
    Integer getMonth();
    BigDecimal getTotal();

}
