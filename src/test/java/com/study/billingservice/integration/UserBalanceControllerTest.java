package com.study.billingservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.billingservice.dto.RequestTransactionDto;
import com.study.billingservice.dto.TransferRequestDto;
import com.study.billingservice.enums.BalanceOperationType;
import com.study.billingservice.enums.TransactionSortType;
import com.study.billingservice.enums.TransactionType;
import com.study.billingservice.repo.TransactionHistoryRepo;
import com.study.billingservice.repo.UserBalanceRepo;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.study.billingservice.service.UserBalanceServiceImplTest.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserBalanceControllerTest extends AbstractIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserBalanceRepo userBalanceRepo;

    @Autowired
    private TransactionHistoryRepo transactionHistoryRepo;

    @BeforeEach
    void setUp() {
        userBalanceRepo.deleteAll();
        transactionHistoryRepo.deleteAll();
    }

    @Test
    @DisplayName("Test get balance by userId get zero")
    void whenGetBalance_thenSuccessResponseWithZero() throws Exception {
        //given

        //when
        ResultActions result = mockMvc.perform(get("/api/rest/user/" + TEST_USER_ID + "/balance" + "?currencyCode=" + TEST_CURRENCY_CODE)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", CoreMatchers.is(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", CoreMatchers.is(TEST_CURRENCY_CODE)));
    }

    @Test
    @DisplayName("Test get balance by userId get added balance")
    void givenAddBalance_whenGetBalance_thenSuccessResponseWithBalance() throws Exception {
        //given
        TransferRequestDto transferRequestDto = new TransferRequestDto(null, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.ADD);

        mockMvc.perform(post("/api/rest/user/" + TEST_USER_ID + "/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequestDto)));
        //when
        ResultActions result = mockMvc.perform(get("/api/rest/user/" + TEST_USER_ID + "/balance" + "?currencyCode=" + TEST_CURRENCY_CODE)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", CoreMatchers.is(TEST_BALANCE_AMOUNT.doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", CoreMatchers.is(TEST_CURRENCY_CODE)));
    }

    @Test
    @DisplayName("Test add balance by userId")
    void givenTransferDto_whenAddBalance_thenSuccessResponse() throws Exception {
        //given
        TransferRequestDto transferRequestDto = new TransferRequestDto(null, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.ADD);

        //when
        ResultActions result = mockMvc.perform(post("/api/rest/user/" + TEST_USER_ID + "/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequestDto)));
        //then
        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", CoreMatchers.is(TEST_BALANCE_AMOUNT.doubleValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency", CoreMatchers.is(TEST_CURRENCY_CODE)));
    }

    @Test
    @DisplayName("Test not enough balance to subtract by userId")
    void givenTransferDto_whenSubtractBalance_thenBadRequestResponse() throws Exception {
        //given
        TransferRequestDto transferRequestDto = new TransferRequestDto(null, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.SUBTRACT);

        //when
        ResultActions result = mockMvc.perform(post("/api/rest/user/" + TEST_USER_ID + "/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequestDto)));
        //then
        result
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Недостаточный баланс")));
    }

    @Test
    @DisplayName("Test transfer balance")
    void givenTransferDto_whenTransferBalance_thenSuccessResponse() throws Exception {
        //given
        TransferRequestDto addBalanceDto = new TransferRequestDto(null, TEST_BALANCE_AMOUNT.add(TEST_ADD_BALANCE_AMOUNT), TEST_COMMENT, BalanceOperationType.ADD);
        TransferRequestDto transferRequestDto = new TransferRequestDto(TEST_USER_ID2, TEST_BALANCE_AMOUNT, TEST_COMMENT, BalanceOperationType.SUBTRACT);

        mockMvc.perform(post("/api/rest/user/" + TEST_USER_ID + "/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addBalanceDto)));
        //when

        ResultActions result = mockMvc.perform(post("/api/rest/user/" + TEST_USER_ID + "/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequestDto)));
        //then
        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.amount", CoreMatchers.is(TEST_ADD_BALANCE_AMOUNT.doubleValue())));
    }

    @Test
    @DisplayName("Test get transaction page")
    void givenAddBalanceDto_whenGetTransactions_thenSuccessResponse() throws Exception {
        //given
        TransferRequestDto addBalanceDto = new TransferRequestDto(null, TEST_BALANCE_AMOUNT.add(TEST_ADD_BALANCE_AMOUNT), TEST_COMMENT, BalanceOperationType.ADD);
        RequestTransactionDto requestTransactionDto = new RequestTransactionDto(TEST_USER_ID, TransactionType.IN, TransactionSortType.AMOUNT, 0, 100);

        mockMvc.perform(post("/api/rest/user/" + TEST_USER_ID + "/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addBalanceDto)));
        //when

        ResultActions result = mockMvc.perform(post("/api/rest/user/balance/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)));
        //then
        result
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)));
    }

}
