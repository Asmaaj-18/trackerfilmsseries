package com.example.trackerfilmsseries.model;

public class Genre {

    private int id;
    private String nom;

    public Genre() {}

    public Genre(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    @Override
    public String toString() {
        return nom; // important pour ComboBox
    }
}