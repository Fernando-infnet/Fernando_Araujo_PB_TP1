package com.example.monolith;

import static com.example.monolith.dto.ApiDtos.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.monolith.client.TransactionServiceClient;
import com.example.monolith.service.TransactionGatewayService;

@ExtendWith(MockitoExtension.class)
class TransactionGatewayServiceTests {
    @Mock TransactionServiceClient client;

    @Test
    void delegatesToTransactionMicroserviceAndCapsListLimit() {
        BalanceView balance = new BalanceView(7L, "BRL", new BigDecimal("42.00"));
        when(client.balance(7L)).thenReturn(balance);
        when(client.list(7L, 100)).thenReturn(List.of());
        TransactionGatewayService service = new TransactionGatewayService(client);

        assertThat(service.balance(7L)).isEqualTo(balance);
        assertThat(service.list(7L, 999)).isEmpty();
        verify(client).list(7L, 100);
    }
}
