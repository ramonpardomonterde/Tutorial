package com.ccsw.tutorial.loan;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ccsw.tutorial.loan.model.Loan;

/**
 * @author ccsw
 *
 */
public interface LoanRepository extends CrudRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {

    /**
     * Método para recuperar un listado paginado de {@link Loan}
     *
     * @param pageable pageable
     * @return {@link Page} de {@link Loan}
     */

    Page<Loan> findAll(Pageable pageable);

    @Query("SELECT l FROM Loan l WHERE l.game.id = :gameId AND :loanStartDate <= l.endDate AND :loanEndDate >= l.startDate AND l.client.id <> :clientId")
    List<Loan> findConflictingGameLoansForClient(@Param("gameId") Long gameId,
            @Param("loanStartDate") Date loanStartDate, @Param("loanEndDate") Date loanEndDate,
            @Param("clientId") Long clientId);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.client.id = :clientId AND :startDate <= l.endDate AND :endDate >= l.startDate")
    int countClientLoansForDateRange(@Param("clientId") Long clientId, @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

}