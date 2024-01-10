package ru.redw4y.HomeAccounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.redw4y.HomeAccounting.models.User;
import ru.redw4y.HomeAccounting.models.CashAccount;

@Repository
public interface CashAccountRepository extends JpaRepository<CashAccount, Integer>{
	Optional<CashAccount> findByNameAndUser(String name, User user);
}
