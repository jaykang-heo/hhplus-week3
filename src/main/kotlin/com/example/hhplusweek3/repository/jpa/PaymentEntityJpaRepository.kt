package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentEntityJpaRepository : JpaRepository<PaymentEntity, Long>
