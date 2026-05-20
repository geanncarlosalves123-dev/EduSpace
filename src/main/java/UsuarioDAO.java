// src/main/java/com/eduspace/dao/UsuarioDAO.java
package com.eduspace.dao;

import com.eduspace.config.DatabaseConfig;
import com.eduspace.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gerenciamento de usuários (admin e professores).
 */
public class UsuarioDAO {

    // ---- CREATE ----
    public int criar(Usuario u) throws SQLException {
        final String SQL = """
            INSERT INTO usuarios (nome, email, senha_hash, perfil)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenhaHash()); // já deve ser BCrypt
            ps.setString(4, u.getPerfil().name().toLowerCase());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) { int id = keys.getInt(1); u.setId(id); return id; }
            }
        }
        return -1;
    }

    // ---- READ por e-mail (login) ----
    public Usuario buscarPorEmail(String email) throws SQLException {
        final String SQL = "SELECT * FROM usuarios WHERE email = ? AND ativo = 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ---- READ por ID ----
    public Usuario buscarPorId(int id) throws SQLException {
        final String SQL = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ---- READ — listar professores ----
    public List<Usuario> listarProfessores() throws SQLException {
        final String SQL = "SELECT * FROM usuarios WHERE perfil='professor' AND ativo=1 ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    // ---- UPDATE ----
    public boolean atualizar(Usuario u) throws SQLException {
        final String SQL = "UPDATE usuarios SET nome=?, email=? WHERE id=?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setInt(3,    u.getId());
            return ps.executeUpdate() > 0;
        }
    }

    // ---- DELETE — soft ----
    public boolean desativar(int id) throws SQLException {
        final String SQL = "UPDATE usuarios SET ativo=0 WHERE id=?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---- helper ----
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setSenhaHash(rs.getString("senha_hash"));
        u.setPerfil(Usuario.Perfil.valueOf(rs.getString("perfil").toUpperCase()));
        u.setAtivo(rs.getBoolean("ativo"));
        return u;
    }
}
