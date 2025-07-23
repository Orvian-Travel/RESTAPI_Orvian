package com.orvian.travelapi.controller.dto.payment;

import java.math.BigDecimal;

public interface PaymentInstallmentData {

    BigDecimal installmentAmount();

    Integer installment();

    BigDecimal valuePaid();
}
