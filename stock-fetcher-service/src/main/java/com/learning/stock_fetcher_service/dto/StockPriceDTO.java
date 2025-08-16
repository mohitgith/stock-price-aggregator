package com.learning.stock_fetcher_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StockPriceDTO {
    private String symbol;
    private BigDecimal price;
    private Instant timestamp;
    private String source;
}
