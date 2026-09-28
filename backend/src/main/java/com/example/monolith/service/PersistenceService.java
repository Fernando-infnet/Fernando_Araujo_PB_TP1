package com.example.monolith.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolith.domain.User;
import com.example.monolith.domain.Wallet;
import com.example.monolith.dto.ApiDtos.CreateUser;
import com.example.monolith.dto.ApiDtos.CreateWallet;
import com.example.monolith.dto.ApiDtos.UpdateUser;
import com.example.monolith.dto.ApiDtos.UserView;
import com.example.monolith.dto.ApiDtos.WalletView;
import com.example.monolith.exception.BusinessException;
import com.example.monolith.exception.ResourceNotFoundException;
import com.example.monolith.repository.UserRepository;
import com.example.monolith.repository.WalletRepository;

@Service
@Transactional
public class PersistenceService {
    private final UserRepository users;
    private final WalletRepository wallets;
    private final WalletEventOutboxService walletEvents;

    public PersistenceService(UserRepository users, WalletRepository wallets,
                              WalletEventOutboxService walletEvents) {
        this.users = users; this.wallets = wallets; this.walletEvents = walletEvents;
    }

    public UserView createUser(CreateUser input) {
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        if (users.existsByEmailIgnoreCase(email)) throw new BusinessException("E-mail já cadastrado");
        return userView(users.save(new User(input.name().trim(), email)));
    }

    @Transactional(readOnly = true)
    public List<UserView> listUsers() { return users.findAll().stream().map(this::userView).toList(); }

    @Transactional(readOnly = true)
    public UserView getUser(Long id) { return userView(requireUser(id)); }

    public UserView updateUser(Long id, UpdateUser input) {
        User user = requireUser(id);
        String email = input.email().trim().toLowerCase(Locale.ROOT);
        if (!user.getEmail().equalsIgnoreCase(email) && users.existsByEmailIgnoreCase(email))
            throw new BusinessException("E-mail já cadastrado");
        user.setName(input.name().trim()); user.setEmail(email);
        return userView(user);
    }

    public WalletView createWallet(CreateWallet input) {
        String currency = input.currency().toUpperCase(Locale.ROOT);
        Wallet wallet = wallets.save(new Wallet(requireUser(input.userId()), currency));
        walletEvents.recordWalletCreated(wallet);
        return walletView(wallet);
    }

    @Transactional(readOnly = true)
    public WalletView getWallet(Long id) { return walletView(requireWallet(id)); }

    @Transactional(readOnly = true)
    public List<WalletView> listWalletsByUser(Long userId) {
        requireUser(userId);
        return wallets.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::walletView).toList();
    }

    private User requireUser(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuário", id)); }
    private Wallet requireWallet(Long id) { return wallets.findById(id).orElseThrow(() -> new ResourceNotFoundException("Carteira", id)); }
    private UserView userView(User u) { return new UserView(u.getId(), u.getName(), u.getEmail(), u.getCreatedAt(), u.getUpdatedAt()); }
    private WalletView walletView(Wallet w) { return new WalletView(w.getId(), w.getUser().getId(), w.getCurrency(), w.getCreatedAt(), w.getUpdatedAt()); }
}
