package ru.tbank.bookit.book_it_backend.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.bookit.book_it_backend.service.TicketService;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketController {
    TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/area-names")
    public List<String> findAllNames() {
        return ticketService.findAllAreaNames();
    }
}