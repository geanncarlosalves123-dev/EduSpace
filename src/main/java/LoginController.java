package com.eduspace;

import com.eduspace.dao.UsuarioDAO;
import com.eduspace.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LoginController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @PostMapping("/login")
    public Usuario login(@RequestBody Map<String, String> dados) throws SQLException {
        String email = dados.get("email");
        String senha = dados.get("senha");
        String role = dados.get("role");

        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario == null) {
            throw new RuntimeException("Usuário não encontrado");
        }

        if (!usuario.getPerfil().name().equalsIgnoreCase(role)) {
            throw new RuntimeException("Perfil inválido");
        }

        if (!BCrypt.checkpw(senha, usuario.getSenhaHash())) {
            throw new RuntimeException("Senha inválida");
        }

        usuario.setSenhaHash(null);
        return usuario;
    }
}