package com.example.trackerfilmsseries.controller;

import com.example.trackerfilmsseries.model.Contenu;
import com.example.trackerfilmsseries.model.Genre;
import com.example.trackerfilmsseries.model.ProgressionSerie;
import com.example.trackerfilmsseries.service.ApiService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.time.LocalDateTime;

public class MainController {

    @FXML private TableView<Contenu> tableContenus;

    @FXML private TableColumn<Contenu, String> colTitre;
    @FXML private TableColumn<Contenu, String> colType;
    @FXML private TableColumn<Contenu, Integer> colAnnee;
    @FXML private TableColumn<Contenu, String> colStatut;
    @FXML private TableColumn<Contenu, Integer> colNote;
    @FXML private TableColumn<Contenu, String> colGenre;
    @FXML private TableColumn<Contenu, String> colRealisateur;
    @FXML private TableColumn<Contenu, Boolean> colWatchlist;
    @FXML private TableColumn<Contenu, LocalDateTime> colDateAjout;
    @FXML private TableColumn<Contenu, Double> colProgression;

    @FXML private HBox watchlistContainer;

    @FXML private ComboBox<String> cbFiltreGenre;
    @FXML private ComboBox<String> cbFiltreStatut;
    @FXML private TextField txtRecherche;

    @FXML private Label lblTotal;
    @FXML private Label lblVus;
    @FXML private Label lblEnCours;
    @FXML private Label lblMoyenne;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {

        configTable();
        chargerFiltres();
        chargerContenus();

        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> appliquerFiltre());
        cbFiltreGenre.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltre());
        cbFiltreStatut.valueProperty().addListener((obs, oldVal, newVal) -> appliquerFiltre());
    }

    private void configTable() {

        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneeSortie"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genreNom"));
        colRealisateur.setCellValueFactory(new PropertyValueFactory<>("realisateur"));
        colWatchlist.setCellValueFactory(new PropertyValueFactory<>("watchlist"));
        colDateAjout.setCellValueFactory(new PropertyValueFactory<>("dateAjout"));
        colProgression.setCellValueFactory(new PropertyValueFactory<>("progression"));

        colProgression.setCellFactory(tc -> new TableCell<>() {
            private final ProgressBar bar = new ProgressBar();

            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setGraphic(null);
                } else {
                    bar.setProgress(value);
                    bar.setPrefWidth(140);
                    setGraphic(bar);
                }
            }
        });
    }

    private void chargerContenus() {

        ObservableList<Contenu> liste =
                FXCollections.observableArrayList(apiService.getAllContenus());

        tableContenus.setItems(liste);
        mettreAJourDashboard(liste);
        chargerWatchlist(liste);
    }

    private void appliquerFiltre() {

        String genre = cbFiltreGenre.getValue();
        String statut = cbFiltreStatut.getValue();
        String motCle = txtRecherche.getText();

        ObservableList<Contenu> liste =
                FXCollections.observableArrayList(
                        apiService.filtrerGlobal(genre, statut, motCle)
                );

        tableContenus.setItems(liste);
        mettreAJourDashboard(liste);
        chargerWatchlist(liste);
    }

    private void mettreAJourDashboard(ObservableList<Contenu> liste) {

        int total = liste.size();
        int vus = 0;
        int enCours = 0;
        double sommeNotes = 0;

        for (Contenu c : liste) {

            if ("VU".equalsIgnoreCase(c.getStatut())) vus++;
            if ("EN_COURS".equalsIgnoreCase(c.getStatut())) enCours++;

            sommeNotes += c.getNote();
        }

        double moyenne = total > 0 ? sommeNotes / total : 0;

        lblTotal.setText(String.valueOf(total));
        lblVus.setText(String.valueOf(vus));
        lblEnCours.setText(String.valueOf(enCours));
        lblMoyenne.setText(String.format("%.1f", moyenne));
    }

    private void chargerWatchlist(ObservableList<Contenu> liste) {

        watchlistContainer.getChildren().clear();

        for (Contenu c : liste) {
            if (c.isWatchlist()) {

                VBox card = new VBox();
                card.getStyleClass().add("watchlist-card");
                card.setSpacing(8);
                card.setMinWidth(220);
                card.setPrefWidth(220);
                card.setAlignment(Pos.CENTER_LEFT);

                Label titre = new Label(c.getTitre());
                Label type = new Label(c.getType());

                card.getChildren().addAll(titre, type);
                watchlistContainer.getChildren().add(card);
            }
        }
    }

    private void chargerFiltres() {

        cbFiltreGenre.getItems().clear();
        cbFiltreGenre.getItems().add("Tous");

        for (Genre g : apiService.getAllGenres()) {
            cbFiltreGenre.getItems().add(g.getNom());
        }

        cbFiltreGenre.setValue("Tous");

        cbFiltreStatut.getItems().clear();
        cbFiltreStatut.getItems().addAll("Tous", "A_VOIR", "EN_COURS", "VU");
        cbFiltreStatut.setValue("Tous");
    }

    public void handleAjouter() {
        ouvrirFormulaire(null);
    }

    public void handleModifier() {

        Contenu selection = tableContenus.getSelectionModel().getSelectedItem();

        if (selection == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un élément").show();
            return;
        }

        ouvrirFormulaire(selection);
    }

    public void handleSupprimer() {

        Contenu selection = tableContenus.getSelectionModel().getSelectedItem();

        if (selection == null) {
            new Alert(Alert.AlertType.WARNING, "Sélectionnez un élément").show();
            return;
        }

        apiService.deleteContenu(selection.getId());
        chargerContenus();
    }

    public void handleRefresh() {
        cbFiltreGenre.setValue("Tous");
        cbFiltreStatut.setValue("Tous");
        txtRecherche.clear();
        chargerContenus();
    }

    private void ouvrirFormulaire(Contenu contenu) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/trackerfilmsseries/view/contenu-form.fxml")
            );

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            if (contenu != null) {
                ContenuFormController controller = loader.getController();
                controller.setContenu(contenu);
                stage.setTitle("Modifier Contenu");
            } else {
                stage.setTitle("Ajouter Contenu");
            }

            stage.showAndWait();
            chargerContenus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}