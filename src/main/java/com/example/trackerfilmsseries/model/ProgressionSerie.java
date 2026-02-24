package com.example.trackerfilmsseries.model;

public class ProgressionSerie {

    private int contenuId;
    private int saisonsTotales;
    private int saisonsVues;
    private int episodesTotaux;
    private int episodesVus;

    // ===== GETTERS =====

    public int getContenuId() {
        return contenuId;
    }

    public int getSaisonsTotales() {
        return saisonsTotales;
    }

    public int getSaisonsVues() {
        return saisonsVues;
    }

    public int getEpisodesTotaux() {
        return episodesTotaux;
    }

    public int getEpisodesVus() {
        return episodesVus;
    }

    // ===== SETTERS =====

    public void setContenuId(int contenuId) {
        this.contenuId = contenuId;
    }

    public void setSaisonsTotales(int saisonsTotales) {
        this.saisonsTotales = saisonsTotales;
    }

    public void setSaisonsVues(int saisonsVues) {
        this.saisonsVues = saisonsVues;
    }

    public void setEpisodesTotaux(int episodesTotaux) {
        this.episodesTotaux = episodesTotaux;
    }

    public void setEpisodesVus(int episodesVus) {
        this.episodesVus = episodesVus;
    }

    // ===== CALCUL PROGRESSION =====

    public double getProgression() {
        if (episodesTotaux == 0) return 0;
        return (double) episodesVus / episodesTotaux;
    }
}