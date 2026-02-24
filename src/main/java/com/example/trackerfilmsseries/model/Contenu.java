package com.example.trackerfilmsseries.model;

import java.time.LocalDateTime;

public class Contenu {

    private int id;
    private String titre;
    private String type; // FILM ou SERIE
    private int anneeSortie;
    private String realisateur;
    private String synopsis;
    private int genreId;
    private String genreNom;
    private String statut; // A_VOIR, EN_COURS, VU
    private int note; // 0 à 5
    private boolean watchlist;
    private LocalDateTime dateAjout;

    // 🔥 UNE SEULE déclaration
    private double progression;

    // ===== Constructeur vide =====
    public Contenu() {}

    // ===== GETTERS & SETTERS =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getAnneeSortie() { return anneeSortie; }
    public void setAnneeSortie(int anneeSortie) { this.anneeSortie = anneeSortie; }

    public String getRealisateur() { return realisateur; }
    public void setRealisateur(String realisateur) { this.realisateur = realisateur; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }

    public String getGenreNom() { return genreNom; }
    public void setGenreNom(String genreNom) { this.genreNom = genreNom; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public boolean isWatchlist() { return watchlist; }
    public void setWatchlist(boolean watchlist) { this.watchlist = watchlist; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }

    // 🔥 Progression (pour ProgressBar)
    public double getProgression() {
        return progression;
    }

    public void setProgression(double progression) {
        this.progression = progression;
    }
}