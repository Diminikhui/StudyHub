package com.fintrack.repository;

import com.fintrack.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("""
            select
              t.category.id,
              t.category.name,
              sum(case when t.type = 'EXPENSE' then t.amount else 0 end),
              sum(case when t.type = 'INCOME' then t.amount else 0 end)
            from Transaction t
            where t.user.id = :userId
              and t.timestamp >= :from
              and t.timestamp < :to
            group by t.category.id, t.category.name
            order by t.category.name
            """)
    List<Object[]> monthlyStatsByCategory(
            @Param("userId") Long userId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to
    );
}