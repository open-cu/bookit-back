package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.TicketEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.TicketMapper;
import com.opencu.bookit.adapter.out.persistence.repository.TicketRepository;
import com.opencu.bookit.application.port.out.ticket.DeleteTicketPort;
import com.opencu.bookit.application.port.out.ticket.LoadTicketPort;
import com.opencu.bookit.application.port.out.ticket.SaveTicketPort;
import com.opencu.bookit.domain.model.ticket.TicketModel;
import com.opencu.bookit.domain.model.ticket.TicketType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TicketPersistenceAdapter implements SaveTicketPort, LoadTicketPort,
        DeleteTicketPort {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    public TicketModel save(TicketModel ticketModel) {
        return ticketMapper.toModel(ticketRepository.save(ticketMapper.toEntity(ticketModel)));
    }
    @Override
    public List<TicketModel> findAll() {
        return ticketMapper.toModelList(ticketRepository.findAll());
    }

    @Override
    public Optional<TicketModel> findById(UUID id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::toModel);
    }

    @Override
    public Page<TicketModel> findWithFilters(LocalDate startDate, LocalDate endDate, String search, TicketType type, Pageable pageable) {
        Specification<TicketEntity> spec = Specification.where(null);
        if (type != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("type"), type));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("createdAt"),
                            LocalDateTime.of(startDate, LocalTime.of(0, 0, 0)),
                            LocalDateTime.of(endDate, LocalTime.of(0,0,0))
                    )
            );
        }

        if (search != null && !search.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("description")), "%" + search.toLowerCase() + "%")
            );
        }
        return ticketRepository.findAll(spec, pageable)
                .map(ticketMapper::toModel);
    }

    @Override
    public void deleteById(UUID ticketId) {
        ticketRepository.deleteById(ticketId);
    }
}