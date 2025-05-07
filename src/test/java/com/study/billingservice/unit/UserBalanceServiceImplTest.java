package com.study.billingservice.unit;

import com.study.billingservice.dto.BalanceInfoDto;
import com.study.billingservice.dto.TransferRequestDto;
import com.study.billingservice.entity.TransactionHistory;
import com.study.billingservice.entity.UserBalance;
import com.study.billingservice.enums.BalanceOperationType;
import com.study.billingservice.exception.BalanceNotEnoughException;
import com.study.billingservice.repo.TransactionHistoryRepo;
import com.study.billingservice.repo.UserBalanceRepo;
import com.study.billingservice.unit.impl.UserBalanceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserBalanceServiceImplTest {

    public static final Long TEST_USER_ID = 1L;
    public static final Long TEST_USER_ID2 = 2L;
    public static final String TEST_CURRENCY_CODE = "KZT";
    public static final String TEST_COMMENT = "TEST_COMMENT";
    public static final String TEST_ERROR_MESSAGE = "Недостаточный баланс";
    public static final BigDecimal TEST_BALANCE_AMOUNT = BigDecimal.valueOf(1234);
    public static final BigDecimal TEST_ADD_BALANCE_AMOUNT = BigDecimal.valueOf(100);

    @Mock
    private UserBalanceRepo userBalanceRepo;
    @Mock
    private TransactionHistoryRepo transactionHistoryRepo;
    @InjectMocks
    private UserBalanceServiceImpl userBalanceService;


    @Test
    public void getBalanceInfo_userBalanceNotFound_ReturnZero() {
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        BalanceInfoDto balanceInfoDto = userBalanceService.getBalanceInfo(TEST_USER_ID, TEST_CURRENCY_CODE);
        assertNotNull(balanceInfoDto);
        assertEquals(BigDecimal.ZERO, balanceInfoDto.amount());
    }

    @Test
    public void getBalanceInfo_userBalanceFound_ReturnBalance() {

        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(getTestUserBalanceData()));

        BalanceInfoDto balanceInfoDto = userBalanceService.getBalanceInfo(TEST_USER_ID, TEST_CURRENCY_CODE);
        assertNotNull(balanceInfoDto);
        assertEquals(TEST_BALANCE_AMOUNT, balanceInfoDto.amount());
    }

    @Test
    public void addBalance_userExisted_ReturnBalance() {

        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.ADD);
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(getTestUserBalanceData()));

        BalanceInfoDto balanceInfoDto = userBalanceService.addBalance(TEST_USER_ID, transferRequestDto);

        assertNotNull(balanceInfoDto);
        BigDecimal actual = TEST_BALANCE_AMOUNT.add(TEST_BALANCE_AMOUNT);
        assertEquals(0, actual.compareTo(balanceInfoDto.amount()));
    }

    @Test
    public void addBalance_userNotFound_ReturnBalance() {

        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.ADD);
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        BalanceInfoDto balanceInfoDto = userBalanceService.addBalance(TEST_USER_ID, transferRequestDto);

        assertNotNull(balanceInfoDto);
        assertEquals(0, TEST_BALANCE_AMOUNT.compareTo(balanceInfoDto.amount()));
    }

    @Test
    public void subtractBalance_throwsBalanceNotEnoughException() {

        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.SUBTRACT);
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        BalanceNotEnoughException exception = assertThrows(BalanceNotEnoughException.class, () -> {
            userBalanceService.subtractBalance(TEST_USER_ID, transferRequestDto);
        });

        assertEquals(TEST_ERROR_MESSAGE, exception.getMessage());
        verify(userBalanceRepo, times(0)).save(new UserBalance());
        verify(transactionHistoryRepo, times(0)).save(new TransactionHistory());
    }

    @Test
    public void subtractBalance_updatesData_returnBalance() {

        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.SUBTRACT);
        UserBalance testUserBalanceData = getTestUserBalanceData();
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testUserBalanceData));

        userBalanceService.subtractBalance(TEST_USER_ID, transferRequestDto);

        verify(userBalanceRepo, times(1)).save(testUserBalanceData);
        verify(transactionHistoryRepo, times(1)).save(getTestTransactionHistory());
    }

    @Test
    public void transferBalance_throwsBalanceNotEnoughException() {

        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.TRANSFER);
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        BalanceNotEnoughException exception = assertThrows(BalanceNotEnoughException.class, () -> {
            userBalanceService.transferBalance(TEST_USER_ID, transferRequestDto);
        });

        assertEquals(TEST_ERROR_MESSAGE, exception.getMessage());
        verify(userBalanceRepo, times(0)).save(new UserBalance());
        verify(transactionHistoryRepo, times(0)).save(new TransactionHistory());
    }

    @Test
    public void transferBalance_returnBalanceInfo() {

        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID2, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.SUBTRACT);
        UserBalance senderUserBalanceData = getTestUserBalanceData();
        UserBalance receiverUserBalanceData = getTestUserBalanceData();
        receiverUserBalanceData.setUserId(TEST_USER_ID2);
        TransactionHistory testTransactionHistory = getTestTransactionHistory();
        testTransactionHistory.setToUserId(TEST_USER_ID2);

        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(senderUserBalanceData));
        Mockito.when(userBalanceRepo.findByUserId(TEST_USER_ID2)).thenReturn(Optional.of(receiverUserBalanceData));

        BalanceInfoDto balanceInfoDto = userBalanceService.transferBalance(TEST_USER_ID, transferRequestDto);

        assertEquals(0, BigDecimal.ZERO.compareTo(balanceInfoDto.amount()));
        verify(userBalanceRepo, times(1)).save(senderUserBalanceData);
        verify(transactionHistoryRepo, times(1)).save(testTransactionHistory);
    }

    private UserBalance getTestUserBalanceData() {
        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(TEST_USER_ID);
        userBalance.setAmount(TEST_BALANCE_AMOUNT);
        return userBalance;
    }

    private TransactionHistory getTestTransactionHistory() {
        TransactionHistory transactionHistory = new TransactionHistory();
        transactionHistory.setFromUserId(TEST_USER_ID);
        transactionHistory.setAmount(TEST_BALANCE_AMOUNT);
        transactionHistory.setComment(TEST_COMMENT);
        return transactionHistory;
    }
}