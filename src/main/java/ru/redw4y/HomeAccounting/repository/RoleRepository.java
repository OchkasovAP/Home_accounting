package ru.redw4y.HomeAccounting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.redw4y.HomeAccounting.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>{
	Optional<Role> findByName(String name);
}
