package ru.redw4y.HomeAccounting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.redw4y.HomeAccounting.models.CashAccount;

@Repository
public interface CashAccountRepository extends JpaRepository<CashAccount, Integer>{

}
