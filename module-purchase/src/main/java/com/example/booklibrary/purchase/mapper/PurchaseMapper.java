package com.example.booklibrary.purchase.mapper;

import com.example.booklibrary.purchase.dto.PurchaseRequestDto;
import com.example.booklibrary.purchase.dto.PurchaseResponseDto;
import com.example.booklibrary.purchase.model.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseMapper {

    Purchase purchaseRequestDtoToPurchase(PurchaseRequestDto purchaseRequestDto);

    PurchaseResponseDto purchaseToPurchaseResponseDto(Purchase purchase);

    List<PurchaseResponseDto> purchasesToPurchaseResponseDtos(List<Purchase> purchases);
}
