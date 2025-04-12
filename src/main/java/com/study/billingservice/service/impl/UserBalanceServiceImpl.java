package com.study.billingservice.service.impl;

import com.study.billingservice.dto.BalanceInfoDto;
import com.study.billingservice.dto.RequestTransactionDto;
import com.study.billingservice.dto.TransactionHistoryDto;
import com.study.billingservice.dto.TransferRequestDto;
import com.study.billingservice.entity.TransactionHistory;
import com.study.billingservice.entity.UserBalance;
import com.study.billingservice.exception.BalanceNotEnoughException;
import com.study.billingservice.mapper.TransactionMapper;
import com.study.billingservice.repo.TransactionHistoryRepo;
import com.study.billingservice.repo.TransactionSearchSpecification;
import com.study.billingservice.repo.UserBalanceRepo;
import com.study.billingservice.service.UserBalanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserBalanceServiceImpl implements UserBalanceService {

    public static final String DEFAULT_CURRENCY = "KZT";
    private final TransactionHistoryRepo transactionHistoryRepo;
    private final UserBalanceRepo userBalanceRepo;
    private final TransactionMapper transactionMapper;

    public UserBalanceServiceImpl(TransactionHistoryRepo transactionHistoryRepo, UserBalanceRepo userBalanceRepo,
                                  TransactionMapper transactionMapper) {
        this.transactionHistoryRepo = transactionHistoryRepo;
        this.userBalanceRepo = userBalanceRepo;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public BalanceInfoDto getBalanceInfo(Long userId, String currency) {
        Optional<UserBalance> userBalanceOptional = userBalanceRepo.findByUserId(userId);
        if (userBalanceOptional.isEmpty()) {
            return new BalanceInfoDto(BigDecimal.ZERO, DEFAULT_CURRENCY);
        }
        UserBalance userBalance = userBalanceOptional.get();
        return new BalanceInfoDto(userBalance.getAmount(), DEFAULT_CURRENCY);
    }

    @Transactional
    @Override
    public BalanceInfoDto addBalance(Long userId, TransferRequestDto transferRequestDto) {
        LocalDateTime currDate = LocalDateTime.now();
        BigDecimal amount = transferRequestDto.amount();

        UserBalance userBalance = getOrCreateUserBalance(userId, currDate);
        TransactionHistory transactionHistory = new TransactionHistory(null, userId, amount, transferRequestDto.comment());
        transactionHistory.setCreateDate(currDate);
        transactionHistory.setUpdateDate(currDate);
        userBalance.addBalance(amount);

        userBalanceRepo.save(userBalance);
        transactionHistoryRepo.save(transactionHistory);
        return new BalanceInfoDto(userBalance.getAmount(), DEFAULT_CURRENCY);
    }

    @Transactional
    @Override
    public BalanceInfoDto subtractBalance(Long userId, TransferRequestDto transferRequestDto) {
        UserBalance userBalance = userBalanceRepo.findByUserId(userId).orElse(null);
        LocalDateTime currDate = LocalDateTime.now();
        BigDecimal amount = transferRequestDto.amount();

        checkBalanceAmount(userBalance, amount);

        userBalance.setUpdateDate(currDate);
        userBalance.subtractBalance(amount);

        TransactionHistory transactionHistory = new TransactionHistory(userId, null, amount, transferRequestDto.comment());
        transactionHistory.setCreateDate(currDate);
        transactionHistory.setUpdateDate(currDate);

        userBalanceRepo.save(userBalance);
        transactionHistoryRepo.save(transactionHistory);
        return new BalanceInfoDto(userBalance.getAmount(), DEFAULT_CURRENCY);
    }

    @Transactional
    @Override
    public BalanceInfoDto transferBalance(Long fromUserId, TransferRequestDto transferRequestDto) {
        UserBalance senderUserBalance = userBalanceRepo.findByUserId(fromUserId).orElse(null);
        LocalDateTime currDate = LocalDateTime.now();
        Long toUserId = transferRequestDto.toUserId();
        BigDecimal amount = transferRequestDto.amount();


        checkBalanceAmount(senderUserBalance, amount);

        UserBalance receiverUserBalance = getOrCreateUserBalance(toUserId,currDate);
        TransactionHistory transactionHistory = new TransactionHistory(fromUserId, toUserId, amount, transferRequestDto.comment());
        transactionHistory.setCreateDate(currDate);
        transactionHistory.setUpdateDate(currDate);

        senderUserBalance.subtractBalance(amount);
        receiverUserBalance.addBalance(amount);

        userBalanceRepo.save(senderUserBalance);
        userBalanceRepo.save(receiverUserBalance);
        transactionHistoryRepo.save(transactionHistory);

        return new BalanceInfoDto(senderUserBalance.getAmount(), DEFAULT_CURRENCY);
    }

    @Override
    public Page<TransactionHistoryDto> getTransactionHistoryList(RequestTransactionDto requestTransactionDto) {
        Sort sort = Sort.by(requestTransactionDto.transactionSortType().getValue());
        Pageable pageable = PageRequest.of(requestTransactionDto.page(), requestTransactionDto.size(), sort);
        TransactionSearchSpecification searchSpecification = new TransactionSearchSpecification(requestTransactionDto);
        Page<TransactionHistory> transactionHistoryPage = transactionHistoryRepo.findAll(searchSpecification, pageable);
        return transactionHistoryPage.map(transactionMapper::entityToDto);
    }

    private void checkBalanceAmount(UserBalance userBalance, BigDecimal subtractAmount) {
        if (userBalance == null || userBalance.getAmount().compareTo(subtractAmount) < 0) {
            throw new BalanceNotEnoughException("Недостаточный баланс");
        }
    }

    private UserBalance getOrCreateUserBalance(Long userId, LocalDateTime currDate) {
        Optional<UserBalance> userBalanceOptional = userBalanceRepo.findByUserId(userId);

        UserBalance userBalance;
        if (userBalanceOptional.isEmpty()) {
            userBalance = new UserBalance();
            userBalance.setUserId(userId);
            userBalance.setAmount(BigDecimal.ZERO);
            userBalance.setCreateDate(currDate);
            userBalance.setUpdateDate(currDate);
        } else {
            userBalance = userBalanceOptional.get();
            userBalance.setUpdateDate(currDate);
        }
        return userBalance;
    }
}
