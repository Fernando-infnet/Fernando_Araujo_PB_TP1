package com.example.monolith;

import static com.example.monolith.dto.ApiDtos.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.example.monolith.client.TransactionServiceClient;
import com.example.monolith.exception.BusinessException;
import com.example.monolith.repository.UserRepository;
import com.example.monolith.repository.WalletRepository;
import com.example.monolith.repository.WalletEventOutboxRepository;
import com.example.monolith.service.PersistenceService;

@SpringBootTest
@ActiveProfiles("test")
class PersistenceIntegrationTests {
    @Autowired PersistenceService service;
    @Autowired WalletRepository wallets;
    @Autowired UserRepository users;
    @Autowired WalletEventOutboxRepository outbox;
    @MockBean TransactionServiceClient transactionServiceClient;

    @BeforeEach
    void clean() { outbox.deleteAll(); wallets.deleteAll(); users.deleteAll(); }

    @Test
    void persistsUserAndWalletRelationship() {
        UserView user = service.createUser(new CreateUser("Ada", "ADA@example.com"));
        WalletView wallet = service.createWallet(new CreateWallet(user.id(), "brl"));

        assertThat(service.getUser(user.id()).email()).isEqualTo("ada@example.com");
        assertThat(service.getWallet(wallet.id()).currency()).isEqualTo("BRL");
        assertThat(service.listWalletsByUser(user.id())).extracting(WalletView::id).containsExactly(wallet.id());
        assertThat(outbox.findAll()).singleElement().satisfies(event -> {
            assertThat(event.getWalletId()).isEqualTo(wallet.id());
            assertThat(event.getCurrency()).isEqualTo("BRL");
            assertThat(event.getPublishedAt()).isNull();
        });
    }

    @Test
    void preservesUniqueEmailIntegrity() {
        UserView user = service.createUser(new CreateUser("Grace", "grace@example.com"));
        assertThatThrownBy(() -> service.createUser(new CreateUser("Outra", "GRACE@example.com")))
                .isInstanceOf(BusinessException.class);
        assertThat(service.createWallet(new CreateWallet(user.id(), "usd")).currency()).isEqualTo("USD");
    }
}
