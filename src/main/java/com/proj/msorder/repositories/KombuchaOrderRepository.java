package com.proj.msorder.repositories;

import com.proj.msorder.domain.Customer;
import com.proj.msorder.domain.KombuchaOrder;
import com.proj.msorder.domain.KombuchaOrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KombuchaOrderRepository extends JpaRepository<KombuchaOrder, UUID> {

    Page<KombuchaOrder> findAllByCustomer(Customer customer, Pageable pageable);

    List<KombuchaOrder> findAllByOrderStatus(KombuchaOrderStatusEnum orderStatusEnum);
}
