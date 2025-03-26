package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        Optional<UserRecord> userOpt = Optional.ofNullable(userRepository.findById(userId));

        if (userOpt.isPresent()) {
            float amount = userOpt.get().getBalance();
            return new Balance(amount);
        } else {
            return new Balance(0.0f);
        }
    }
}
