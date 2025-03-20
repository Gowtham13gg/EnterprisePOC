package com.uno.bank.account.repository;

import com.uno.bank.account.model.InterfaceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterfaceConfigRepository extends JpaRepository<InterfaceConfig, String> {
    InterfaceConfig findByInterfaceId(String interfaceId);
}
