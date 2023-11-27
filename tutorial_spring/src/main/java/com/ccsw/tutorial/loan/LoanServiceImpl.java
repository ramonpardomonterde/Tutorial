package com.ccsw.tutorial.loan;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ccsw.tutorial.client.ClientService;
import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    @Autowired
    LoanRepository loanRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private ClientService clientService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Loan> findPage(LoanSearchDto dto, String gameTitle, String clientName, Date date) {

        Specification<Loan> spec = Specification.where(null);

        if (gameTitle != null && !gameTitle.isEmpty()) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("game.title", ":", gameTitle)));
        }
        if (clientName != null && !clientName.isEmpty()) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("client.name", ":", clientName)));
        }
        if (date != null) {
            spec = spec.and(new LoanSpecification(new SearchCriteria("startDate", ":<", date)));
            spec = spec.and(new LoanSpecification(new SearchCriteria("endDate", ":>", date)));
        }

        return this.loanRepository.findAll(spec, dto.getPageable().getPageable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(Long id, LoanDto dto) throws IllegalArgumentException {

        // Validación 1: Fecha de fin no anterior a la fecha de inicio
        if (dto.getEndDate().before(dto.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        // Obtener las fechas como objetos Date
        Date startDate = dto.getStartDate();
        Date endDate = dto.getEndDate();

        // Calcular la diferencia en milisegundos
        long differenceInMilliseconds = endDate.getTime() - startDate.getTime();
        // Convertir la diferencia a días
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMilliseconds);

        // Validacion 2: si la diferencia es mayor a 14 días
        if (differenceInDays > 14) {
            throw new IllegalArgumentException("Plazo de tiempo mayor a 14 dias");
        }

        // Validación 3: El mismo juego no puede estar prestado a dos clientes distintos
        // en un mismo día
        List<Loan> existingGameLoans = loanRepository.findConflictingGameLoansForClient(dto.getGame().getId(),
                startDate, endDate, dto.getClient().getId());
        if (existingGameLoans.stream().anyMatch(existingLoan -> !existingLoan.getClient().equals(dto.getClient()))) {
            throw new IllegalArgumentException(
                    "El mismo juego no puede estar prestado a dos clientes distintos en un mismo día");
        }

        // Validación 4: Un mismo cliente no puede tener prestados más de 2 juegos en un
        // mismo día
        int clientLoanCount = loanRepository.countClientLoansForDateRange(dto.getClient().getId(), dto.getStartDate(),
                dto.getEndDate());
        if (clientLoanCount + (id == null ? 1 : 0) > 2) {
            throw new IllegalArgumentException(
                    "Un mismo cliente no puede tener prestados más de 2 juegos en un mismo día");
        }

        Loan loan;

        if (id == null) {
            loan = new Loan();
        } else {
            loan = this.loanRepository.findById(id).orElse(null);
        }

        BeanUtils.copyProperties(dto, loan, "id", "game", "client");
        loan.setClient(clientService.get(dto.getClient().getId()));
        loan.setGame(gameService.get(dto.getGame().getId()));

        this.loanRepository.save(loan);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) throws Exception {

        if (this.loanRepository.findById(id).orElse(null) == null) {
            throw new Exception("Not exists");
        }

        this.loanRepository.deleteById(id);
    }
}
