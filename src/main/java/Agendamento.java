// src/main/java/com/eduspace/model/Agendamento.java
package com.eduspace.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Representa um agendamento de espaço/equipamento.
 */
public class Agendamento {

    public enum Status {
        CONFIRMADO, EM_USO, LIBERADO, CANCELADO
    }

    private int           id;
    private int           professorId;
    private String        professorNome;   // join (leitura)
    private int           espacoId;
    private String        espacoNome;      // join (leitura)
    private String        salaIdentificacao;
    private LocalDate     dataUso;
    private LocalTime     horarioInicio;
    private LocalTime     horarioFim;
    private int           qtdAulas;
    private int           qtdNotebooks;
    private int           qtdTablets;
    private String        observacoes;
    private String        problemaRelatado;
    private Status        status;
    private LocalDateTime confirmadoEm;
    private LocalDateTime iniciadoEm;
    private LocalDateTime liberadoEm;
    private LocalDateTime canceladoEm;

    // ---- Construtores ----
    public Agendamento() {}

    public Agendamento(int professorId, int espacoId, String salaIdentificacao,
                       LocalDate dataUso, LocalTime horarioInicio, LocalTime horarioFim,
                       int qtdAulas, int qtdNotebooks, int qtdTablets, String observacoes) {
        this.professorId       = professorId;
        this.espacoId          = espacoId;
        this.salaIdentificacao = salaIdentificacao;
        this.dataUso           = dataUso;
        this.horarioInicio     = horarioInicio;
        this.horarioFim        = horarioFim;
        this.qtdAulas          = qtdAulas;
        this.qtdNotebooks      = qtdNotebooks;
        this.qtdTablets        = qtdTablets;
        this.observacoes       = observacoes;
        this.status            = Status.CONFIRMADO;
    }

    // ---- Getters & Setters ----
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getProfessorId()                 { return professorId; }
    public void setProfessorId(int v)           { this.professorId = v; }

    public String getProfessorNome()            { return professorNome; }
    public void setProfessorNome(String v)      { this.professorNome = v; }

    public int getEspacoId()                    { return espacoId; }
    public void setEspacoId(int v)              { this.espacoId = v; }

    public String getEspacoNome()               { return espacoNome; }
    public void setEspacoNome(String v)         { this.espacoNome = v; }

    public String getSalaIdentificacao()        { return salaIdentificacao; }
    public void setSalaIdentificacao(String v)  { this.salaIdentificacao = v; }

    public LocalDate getDataUso()               { return dataUso; }
    public void setDataUso(LocalDate v)         { this.dataUso = v; }

    public LocalTime getHorarioInicio()         { return horarioInicio; }
    public void setHorarioInicio(LocalTime v)   { this.horarioInicio = v; }

    public LocalTime getHorarioFim()            { return horarioFim; }
    public void setHorarioFim(LocalTime v)      { this.horarioFim = v; }

    public int getQtdAulas()                    { return qtdAulas; }
    public void setQtdAulas(int v)              { this.qtdAulas = v; }

    public int getQtdNotebooks()                { return qtdNotebooks; }
    public void setQtdNotebooks(int v)          { this.qtdNotebooks = v; }

    public int getQtdTablets()                  { return qtdTablets; }
    public void setQtdTablets(int v)            { this.qtdTablets = v; }

    public String getObservacoes()              { return observacoes; }
    public void setObservacoes(String v)        { this.observacoes = v; }

    public String getProblemaRelatado()         { return problemaRelatado; }
    public void setProblemaRelatado(String v)   { this.problemaRelatado = v; }

    public Status getStatus()                   { return status; }
    public void setStatus(Status v)             { this.status = v; }

    public LocalDateTime getConfirmadoEm()      { return confirmadoEm; }
    public void setConfirmadoEm(LocalDateTime v){ this.confirmadoEm = v; }

    public LocalDateTime getIniciadoEm()        { return iniciadoEm; }
    public void setIniciadoEm(LocalDateTime v)  { this.iniciadoEm = v; }

    public LocalDateTime getLiberadoEm()        { return liberadoEm; }
    public void setLiberadoEm(LocalDateTime v)  { this.liberadoEm = v; }

    public LocalDateTime getCanceladoEm()       { return canceladoEm; }
    public void setCanceladoEm(LocalDateTime v) { this.canceladoEm = v; }

    @Override
    public String toString() {
        return String.format("Agendamento{id=%d, espaco='%s', data=%s, horario=%s-%s, status=%s}",
                id, espacoNome, dataUso, horarioInicio, horarioFim, status);
    }
}
