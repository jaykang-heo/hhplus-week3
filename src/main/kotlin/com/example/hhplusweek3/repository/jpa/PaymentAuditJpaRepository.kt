package com.example.hhplusweek3.repository.jpa

import com.example.hhplusweek3.repository.model.PaymentAuditEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentAuditJpaRepository : JpaRepository<PaymentAuditEntity, Long>
