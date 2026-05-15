package com.sadjier.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "商品更新DTO")
public class ProductUpdateDTO{
	private String name;
	private String category;
	private BigDecimal purchasePrice;
	private BigDecimal salePrice;
	private Integer stock;
	private String imagePath;
}