package com.eduspace;

import com.eduspace.dao.AgendamentoDAO;
import com.eduspace.model.Agendamento;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoDAO dao;

    public AgendamentoController(AgendamentoDAO dao) {
        this.dao = dao;
    }

    @GetMapping("/agendamentos")
    public List<Agendamento> listar() throws SQLException {
        return dao.listarTodos(0, 100);
    }

    @PostMapping("/agendamentos")
    public Agendamento criar(@RequestBody Agendamento agendamento) throws SQLException{
        dao.criar(agendamento);
        return agendamento;
    }
}