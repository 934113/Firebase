package com.example.firebase;

import com.google.firebase.firestore.Exclude;

public class Gasto {

    private String documentId; // Nuevo campo para el ID de Firestore
    private String titulo;
    private double monto;
    private String categoria;
    private String tipo;
    private String usuarioId;

    // Constructor vacío obligatorio
    public Gasto() {
    }

    public Gasto(String titulo,
                 double monto,
                 String categoria,
                 String tipo,
                 String usuarioId) {

        this.titulo = titulo;
        this.monto = monto;
        this.categoria = categoria;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
    }

    // GETTERS Y SETTERS PARA documentId (Excluido de Firestore al subir)
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    // GETTERS

    public String getTitulo() {
        return titulo;
    }

    public double getMonto() {
        return monto;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    // SETTERS

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}