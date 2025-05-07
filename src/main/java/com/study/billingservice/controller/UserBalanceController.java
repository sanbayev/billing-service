package com.study.billingservice.controller;

import com.study.billingservice.dto.BalanceInfoDto;
import com.study.billingservice.dto.RequestTransactionDto;
import com.study.billingservice.dto.TransactionHistoryDto;
import com.study.billingservice.dto.TransferRequestDto;
import com.study.billingservice.unit.UserBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Api(value = "Ресты по балансу пользователя", tags = {"Ресты по балансу пользователя"})
@RestController
@RequestMapping("/api/rest/user")
public class UserBalanceController {

    private final UserBalanceService userBalanceService;

    public UserBalanceController(UserBalanceService userBalanceService) {
        this.userBalanceService = userBalanceService;
    }

    @ApiOperation(value = "Данные баланса пользователя",
            notes = "Данные баланса пользователя")
    @GetMapping("/{userId}/balance")
    public BalanceInfoDto getUserBalanceInfo(@PathVariable Long userId,
                                               @RequestParam String currencyCode) {
        return userBalanceService.getBalanceInfo(userId, currencyCode);
    }

    @ApiOperation(value = "Манипулияция с балансом",
            notes = "Манипулияция с балансом")
    @PostMapping("/{userId}/balance")
    public BalanceInfoDto transferBalance(@PathVariable Long userId,
                                          @RequestBody TransferRequestDto transferRequestDto) {
        return switch(transferRequestDto.balanceOperationType()) {
            case ADD -> userBalanceService.addBalance(userId,transferRequestDto);
            case SUBTRACT -> userBalanceService.subtractBalance(userId,transferRequestDto);
            case TRANSFER -> userBalanceService.transferBalance(userId,transferRequestDto);
        };
    }

    @ApiOperation(value = "Транзакции по счету",
            notes = "Транзакции по счету")
    @PostMapping("/balance/transactions")
    public Page<TransactionHistoryDto> getTransactionHistory(@RequestBody RequestTransactionDto requestTransactionDto) {
        return userBalanceService.getTransactionHistoryList(requestTransactionDto);
    }


}
