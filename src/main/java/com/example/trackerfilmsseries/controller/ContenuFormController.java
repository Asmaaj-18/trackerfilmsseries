package com.example.trackerfilmsseries.controller;

import com.example.trackerfilmsseries.model.Contenu;
import com.example.trackerfilmsseries.model.Genre;
import com.example.trackerfilmsseries.service.ApiService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ContenuFormController {

    @FXML private TextField txtTitre;
    @FXML private ComboBox<String> cbType;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtRealisateur;
    @FXML private ComboBox<Genre> cbGenre;
    @FXML private ComboBox<String> cbStatut;
    @FXML private Spinner<Integer> spNote;
    @FXML private TextField txtSaisonsTotales;
    @FXML private TextField txtSaisonsVues;
    @FXML private TextField txtEpisodesTotaux;
    @FXML private TextField txtEpisodesVus;

    private final ApiService apiService = new ApiService();
    private Contenu contenu;

    @FXML
    public void initialize() {

        cbType.getItems().addAll("FILM", "SERIE");
        cbStatut.getItems().addAll("A_VOIR", "EN_COURS", "VU");

        spNote.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5, 0)
        );

        cbGenre.getItems().addAll(apiService.getAllGenres());
    }

    public void setContenu(Contenu contenu) {
        this.contenu = contenu;

        txtTitre.setText(contenu.getTitre());
        cbType.setValue(contenu.getType());
        txtAnnee.setText(String.valueOf(contenu.getAnneeSortie()));
        txtRealisateur.setText(contenu.getRealisateur());
        cbStatut.setValue(contenu.getStatut());
        spNote.getValueFactory().setValue(contenu.getNote());

        for (Genre g : cbGenre.getItems()) {
            if (g.getId() == contenu.getGenreId()) {
                cbGenre.setValue(g);
                break;
            }
        }
    }

    @FXML
    private void handleSave() {

        if (txtTitre.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Titre obligatoire").show();
            return;
        }

        if (contenu == null) {
            contenu = new Contenu();
        }

        contenu.setTitre(txtTitre.getText());
        contenu.setType(cbType.getValue());
        contenu.setAnneeSortie(Integer.parseInt(txtAnnee.getText()));
        contenu.setRealisateur(txtRealisateur.getText());
        contenu.setStatut(cbStatut.getValue());
        contenu.setNote(spNote.getValue());
        contenu.setWatchlist(false);

        Genre genre = cbGenre.getValue();
        contenu.setGenreId(genre.getId());

        // 🔥 SAVE CONTENU
        if (contenu.getId() == 0) {
            apiService.addContenu(contenu);
        } else {
            apiService.updateContenu(contenu);
        }

        // 🔥 SAVE PROGRESSION SI SERIE
        if ("SERIE".equals(contenu.getType())) {

            int saisonsTotales = parseSafe(txtSaisonsTotales.getText());
            int saisonsVues = parseSafe(txtSaisonsVues.getText());
            int episodesTotaux = parseSafe(txtEpisodesTotaux.getText());
            int episodesVus = parseSafe(txtEpisodesVus.getText());

            apiService.saveOrUpdateProgression(
                    contenu.getId(),
                    saisonsTotales,
                    saisonsVues,
                    episodesTotaux,
                    episodesVus
            );
        }

        Stage stage = (Stage) txtTitre.getScene().getWindow();
        stage.close();
    }

    // ✅ méthode sécurisée pour éviter NumberFormatException
    private int parseSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}