package com.example.firebase;
public class Gasto {

    private String titulo;
    private double monto;
    private String categoria;
    private String tipo;
    private String fecha;
    private String usuarioId;

    // Constructor vacío obligatorio para Firebase
    public Gasto() {
    }

    public Gasto(String titulo,
                 double monto,
                 String categoria,
                 String tipo,
                 String fecha,
                 String usuarioId) {

        this.titulo = titulo;
        this.monto = monto;
        this.categoria = categoria;
        this.tipo = tipo;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
    }

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

    public String getFecha() {
        return fecha;
    }

    public String getUsuarioId() {
        return usuarioId;
    }
}
