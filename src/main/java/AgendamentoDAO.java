
package com.eduspace.dao;

import com.eduspace.config.DatabaseConfig;
import com.eduspace.model.Agendamento;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AgendamentoDAO {

    // ================================================================
    // CREATE
    // ================================================================
    /**
     * Persiste um novo agendamento e retorna o ID gerado.
     */
    public int criar(Agendamento a) throws SQLException {
        final String SQL = """
            INSERT INTO agendamentos
              (professor_id, espaco_id, sala_identificacao,
               data_uso, horario_inicio, horario_fim,
               qtd_aulas, qtd_notebooks, qtd_tablets,
               observacoes, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1,    a.getProfessorId());
            ps.setInt(2,    a.getEspacoId());
            ps.setString(3, a.getSalaIdentificacao());
            ps.setDate(4,   Date.valueOf(a.getDataUso()));
            ps.setTime(5,   Time.valueOf(a.getHorarioInicio()));
            ps.setTime(6,   Time.valueOf(a.getHorarioFim()));
            ps.setInt(7,    a.getQtdAulas());
            ps.setInt(8,    a.getQtdNotebooks());
            ps.setInt(9,    a.getQtdTablets());
            ps.setString(10,a.getObservacoes());
            ps.setString(11, a.getStatus().name().toLowerCase());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Nenhuma linha inserida.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    a.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }

    // ================================================================
    // READ — Por ID
    // ================================================================
    public Agendamento buscarPorId(int id) throws SQLException {
        final String SQL = """
            SELECT a.*, u.nome AS professor_nome, e.nome AS espaco_nome
            FROM agendamentos a
            JOIN usuarios u ON u.id = a.professor_id
            JOIN espacos  e ON e.id = a.espaco_id
            WHERE a.id = ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ================================================================
    // READ — Todos (com paginação simples)
    // ================================================================
    public List<Agendamento> listarTodos(int offset, int limit) throws SQLException {
        final String SQL = """
            SELECT a.*, u.nome AS professor_nome, e.nome AS espaco_nome
            FROM agendamentos a
            JOIN usuarios u ON u.id = a.professor_id
            JOIN espacos  e ON e.id = a.espaco_id
            ORDER BY a.data_uso DESC, a.horario_inicio DESC
            LIMIT ? OFFSET ?
            """;

        List<Agendamento> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // ================================================================
    // READ — Por professor
    // ================================================================
    public List<Agendamento> listarPorProfessor(int professorId) throws SQLException {
        final String SQL = """
            SELECT a.*, u.nome AS professor_nome, e.nome AS espaco_nome
            FROM agendamentos a
            JOIN usuarios u ON u.id = a.professor_id
            JOIN espacos  e ON e.id = a.espaco_id
            WHERE a.professor_id = ?
            ORDER BY a.data_uso DESC, a.horario_inicio
            """;

        List<Agendamento> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, professorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    // ================================================================
    // READ — Relatório semanal
    // ================================================================
    public List<Agendamento> relatorioSemanal() throws SQLException {
        final String SQL = """
            SELECT a.*, u.nome AS professor_nome, e.nome AS espaco_nome
            FROM agendamentos a
            JOIN usuarios u ON u.id = a.professor_id
            JOIN espacos  e ON e.id = a.espaco_id
            WHERE a.data_uso BETWEEN
                  DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)
              AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY)
            ORDER BY a.data_uso, a.horario_inicio
            """;

        List<Agendamento> lista = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    // ================================================================
    // READ — Verificar conflito de horário
    // ================================================================
    public boolean existeConflito(int espacoId, LocalDate data,
                                  java.time.LocalTime inicio,
                                  java.time.LocalTime fim,
                                  int excluirId) throws SQLException {
        final String SQL = """
            SELECT COUNT(*) FROM agendamentos
            WHERE espaco_id = ?
              AND data_uso  = ?
              AND id        != ?
              AND status NOT IN ('cancelado','liberado')
              AND horario_inicio < ? AND horario_fim > ?
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1,    espacoId);
            ps.setDate(2,   Date.valueOf(data));
            ps.setInt(3,    excluirId);
            ps.setTime(4,   Time.valueOf(fim));
            ps.setTime(5,   Time.valueOf(inicio));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ================================================================
    // UPDATE — Atualizar campos gerais
    // ================================================================
    public boolean atualizar(Agendamento a) throws SQLException {
        final String SQL = """
            UPDATE agendamentos SET
              sala_identificacao = ?,
              data_uso           = ?,
              horario_inicio     = ?,
              horario_fim        = ?,
              qtd_aulas          = ?,
              qtd_notebooks      = ?,
              qtd_tablets        = ?,
              observacoes        = ?
            WHERE id = ? AND status = 'confirmado'
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, a.getSalaIdentificacao());
            ps.setDate(2,   Date.valueOf(a.getDataUso()));
            ps.setTime(3,   Time.valueOf(a.getHorarioInicio()));
            ps.setTime(4,   Time.valueOf(a.getHorarioFim()));
            ps.setInt(5,    a.getQtdAulas());
            ps.setInt(6,    a.getQtdNotebooks());
            ps.setInt(7,    a.getQtdTablets());
            ps.setString(8, a.getObservacoes());
            ps.setInt(9,    a.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // UPDATE — Iniciar uso da sala (confirmado → em_uso)
    // ================================================================
    public boolean iniciarUso(int id) throws SQLException {
        final String SQL = """
            UPDATE agendamentos
            SET status = 'em_uso', iniciado_em = NOW()
            WHERE id = ? AND status = 'confirmado'
            """;
        return executarUpdate(SQL, id);
    }

    // ================================================================
    // UPDATE — Liberar sala (em_uso → liberado)
    // ================================================================
    public boolean liberarSala(int id, String problemaRelatado) throws SQLException {
        final String SQL = """
            UPDATE agendamentos
            SET status = 'liberado',
                liberado_em = NOW(),
                problema_relatado = ?
            WHERE id = ? AND status = 'em_uso'
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, problemaRelatado);
            ps.setInt(2,    id);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // DELETE — Cancelar (soft delete via status)
    // ================================================================
    public boolean cancelar(int id) throws SQLException {
        final String SQL = """
            UPDATE agendamentos
            SET status = 'cancelado', cancelado_em = NOW()
            WHERE id = ? AND status IN ('confirmado','em_uso')
            """;
        return executarUpdate(SQL, id);
    }

    // ================================================================
    // DELETE — Hard delete (apenas admin / desenvolvimento)
    // ================================================================
    public boolean deletarFisico(int id) throws SQLException {
        final String SQL = "DELETE FROM agendamentos WHERE id = ?";
        return executarUpdate(SQL, id);
    }

    // ================================================================
    // PRIVADO — helpers
    // ================================================================
    private boolean executarUpdate(String sql, int id) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Agendamento mapRow(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getInt("id"));
        a.setProfessorId(rs.getInt("professor_id"));
        a.setEspacoId(rs.getInt("espaco_id"));
        a.setSalaIdentificacao(rs.getString("sala_identificacao"));
        a.setDataUso(rs.getDate("data_uso").toLocalDate());
        a.setHorarioInicio(rs.getTime("horario_inicio").toLocalTime());
        a.setHorarioFim(rs.getTime("horario_fim").toLocalTime());
        a.setQtdAulas(rs.getInt("qtd_aulas"));
        a.setQtdNotebooks(rs.getInt("qtd_notebooks"));
        a.setQtdTablets(rs.getInt("qtd_tablets"));
        a.setObservacoes(rs.getString("observacoes"));
        a.setProblemaRelatado(rs.getString("problema_relatado"));
        a.setStatus(Agendamento.Status.valueOf(rs.getString("status").toUpperCase()));

        // campos opcionais do JOIN
        try { a.setProfessorNome(rs.getString("professor_nome")); } catch (SQLException ignored) {}
        try { a.setEspacoNome(rs.getString("espaco_nome"));       } catch (SQLException ignored) {}

        // timestamps
        Timestamp conf = rs.getTimestamp("confirmado_em");
        if (conf != null) a.setConfirmadoEm(conf.toLocalDateTime());

        Timestamp ini = rs.getTimestamp("iniciado_em");
        if (ini != null) a.setIniciadoEm(ini.toLocalDateTime());

        Timestamp lib = rs.getTimestamp("liberado_em");
        if (lib != null) a.setLiberadoEm(lib.toLocalDateTime());

        Timestamp can = rs.getTimestamp("cancelado_em");
        if (can != null) a.setCanceladoEm(can.toLocalDateTime());

        return a;
    }
}
