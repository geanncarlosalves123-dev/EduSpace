// src/main/java/com/eduspace/model/Usuario.java
package com.eduspace.model;

public class Usuario {

    public enum Perfil { ADMIN, PROFESSOR }

    private int    id;
    private String nome;
    private String email;
    private String senhaHash;
    private Perfil perfil;
    private boolean ativo = true;

    public Usuario() {}

    // Getters & Setters
    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public String getNome()              { return nome; }
    public void setNome(String v)        { this.nome = v; }
    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }
    public String getSenhaHash()         { return senhaHash; }
    public void setSenhaHash(String v)   { this.senhaHash = v; }
    public Perfil getPerfil()            { return perfil; }
    public void setPerfil(Perfil v)      { this.perfil = v; }
    public boolean isAtivo()             { return ativo; }
    public void setAtivo(boolean v)      { this.ativo = v; }
}
