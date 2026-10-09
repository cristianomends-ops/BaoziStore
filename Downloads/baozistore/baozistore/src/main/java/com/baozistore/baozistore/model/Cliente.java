package com.baozistore.baozistore.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 20)
    private String telefone; // Novo campo adicionado

    @Column(nullable = false)
    private LocalDate clienteDesde;

    public Cliente() {}

    public Cliente(Long id, String nome, String telefone, LocalDate clienteDesde) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.clienteDesde = clienteDesde;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getClienteDesde() { return clienteDesde; }
    public void setClienteDesde(LocalDate clienteDesde) { this.clienteDesde = clienteDesde; }
}