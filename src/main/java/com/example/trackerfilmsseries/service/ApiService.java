package com.example.trackerfilmsseries.service;

import com.example.trackerfilmsseries.model.Contenu;
import com.example.trackerfilmsseries.model.Genre;
import com.example.trackerfilmsseries.model.ProgressionSerie;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApiService {

    // =====================================================
    // 🔎 READ ALL CONTENUS (avec JOIN + progression)
    // =====================================================
    public List<Contenu> getAllContenus() {

        List<Contenu> liste = new ArrayList<>();

        String sql = """
                SELECT c.*, g.nom AS genre_nom
                FROM contenus c
                LEFT JOIN genres g ON c.genre_id = g.id
                ORDER BY c.id DESC
                """;

        try (Connection conn = ApiClient.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {

                Contenu contenu = new Contenu();

                contenu.setId(rs.getInt("id"));
                contenu.setTitre(rs.getString("titre"));
                contenu.setType(rs.getString("type"));
                contenu.setAnneeSortie(rs.getInt("annee_sortie"));
                contenu.setRealisateur(rs.getString("realisateur"));
                contenu.setSynopsis(rs.getString("synopsis"));
                contenu.setStatut(rs.getString("statut"));
                contenu.setNote(rs.getInt("note"));
                contenu.setWatchlist(rs.getBoolean("watchlist"));
                contenu.setGenreId(rs.getInt("genre_id"));
                contenu.setGenreNom(rs.getString("genre_nom"));

                Timestamp ts = rs.getTimestamp("date_ajout");
                if (ts != null) {
                    contenu.setDateAjout(ts.toLocalDateTime());
                }

                // 🔥 charger progression si série
                if ("SERIE".equals(contenu.getType())) {
                    ProgressionSerie p = getProgressionByContenu(contenu.getId());
                    if (p != null) {
                        contenu.setProgression(p.getProgression());
                    }
                }

                liste.add(contenu);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================================
    // ➕ CREATE
    // =====================================================
    public void addContenu(Contenu contenu) {

        String sql = """
                INSERT INTO contenus
                (titre, type, annee_sortie, realisateur, synopsis,
                 genre_id, statut, note, watchlist)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contenu.getTitre());
            pstmt.setString(2, contenu.getType());
            pstmt.setInt(3, contenu.getAnneeSortie());
            pstmt.setString(4, contenu.getRealisateur());
            pstmt.setString(5, contenu.getSynopsis());
            pstmt.setInt(6, contenu.getGenreId());
            pstmt.setString(7, contenu.getStatut());
            pstmt.setInt(8, contenu.getNote());
            pstmt.setBoolean(9, contenu.isWatchlist());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // ✏ UPDATE
    // =====================================================
    public void updateContenu(Contenu contenu) {

        String sql = """
                UPDATE contenus
                SET titre = ?, type = ?, annee_sortie = ?, realisateur = ?,
                    synopsis = ?, genre_id = ?, statut = ?, note = ?, watchlist = ?
                WHERE id = ?
                """;

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contenu.getTitre());
            pstmt.setString(2, contenu.getType());
            pstmt.setInt(3, contenu.getAnneeSortie());
            pstmt.setString(4, contenu.getRealisateur());
            pstmt.setString(5, contenu.getSynopsis());
            pstmt.setInt(6, contenu.getGenreId());
            pstmt.setString(7, contenu.getStatut());
            pstmt.setInt(8, contenu.getNote());
            pstmt.setBoolean(9, contenu.isWatchlist());
            pstmt.setInt(10, contenu.getId());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // ❌ DELETE
    // =====================================================
    public void deleteContenu(int id) {

        String sql = "DELETE FROM contenus WHERE id = ?";

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    // 📚 GET ALL GENRES
    // =====================================================
    public List<Genre> getAllGenres() {

        List<Genre> liste = new ArrayList<>();
        String sql = "SELECT * FROM genres ORDER BY nom";

        try (Connection conn = ApiClient.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Genre genre = new Genre(
                        rs.getInt("id"),
                        rs.getString("nom")
                );
                liste.add(genre);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================================
    // 📺 GET PROGRESSION
    // =====================================================
    public ProgressionSerie getProgressionByContenu(int contenuId) {

        String sql = "SELECT * FROM progression_series WHERE contenu_id = ?";
        ProgressionSerie p = null;

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contenuId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p = new ProgressionSerie();
                p.setContenuId(contenuId);
                p.setSaisonsTotales(rs.getInt("saisons_totales"));
                p.setSaisonsVues(rs.getInt("saisons_vues"));
                p.setEpisodesTotaux(rs.getInt("episodes_totaux"));
                p.setEpisodesVus(rs.getInt("episodes_vus"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return p;
    }

    // =====================================================
    // 🔎 FILTRER
    // =====================================================
    public List<Contenu> filtrer(String genre, String statut, String motCle) {

        List<Contenu> liste = new ArrayList<>();

        String sql = """
                SELECT c.*, g.nom AS genre_nom
                FROM contenus c
                LEFT JOIN genres g ON c.genre_id = g.id
                WHERE (? IS NULL OR g.nom = ?)
                AND (? IS NULL OR c.statut = ?)
                AND (? IS NULL OR LOWER(c.titre) LIKE LOWER(?))
                """;

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, genre);
            ps.setString(2, genre);
            ps.setString(3, statut);
            ps.setString(4, statut);

            if (motCle == null || motCle.isEmpty()) {
                ps.setString(5, null);
                ps.setString(6, null);
            } else {
                ps.setString(5, motCle);
                ps.setString(6, "%" + motCle + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Contenu contenu = new Contenu();
                contenu.setId(rs.getInt("id"));
                contenu.setTitre(rs.getString("titre"));
                contenu.setType(rs.getString("type"));
                contenu.setAnneeSortie(rs.getInt("annee_sortie"));
                contenu.setRealisateur(rs.getString("realisateur"));
                contenu.setStatut(rs.getString("statut"));
                contenu.setNote(rs.getInt("note"));
                contenu.setGenreNom(rs.getString("genre_nom"));

                liste.add(contenu);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }
    // =========================
// UPDATE PROGRESSION
// =========================
    public void saveOrUpdateProgression(int contenuId,
                                        int saisonsTotales,
                                        int saisonsVues,
                                        int episodesTotaux,
                                        int episodesVus) {

        String checkSql = "SELECT id FROM progression_series WHERE contenu_id = ?";
        String insertSql = """
        INSERT INTO progression_series
        (contenu_id, saisons_totales, saisons_vues, episodes_totaux, episodes_vus)
        VALUES (?, ?, ?, ?, ?)
    """;

        String updateSql = """
        UPDATE progression_series
        SET saisons_totales = ?, saisons_vues = ?,
            episodes_totaux = ?, episodes_vus = ?
        WHERE contenu_id = ?
    """;

        try (Connection conn = ApiClient.getConnection()) {

            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setInt(1, contenuId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement(updateSql);
                update.setInt(1, saisonsTotales);
                update.setInt(2, saisonsVues);
                update.setInt(3, episodesTotaux);
                update.setInt(4, episodesVus);
                update.setInt(5, contenuId);
                update.executeUpdate();
            } else {
                PreparedStatement insert = conn.prepareStatement(insertSql);
                insert.setInt(1, contenuId);
                insert.setInt(2, saisonsTotales);
                insert.setInt(3, saisonsVues);
                insert.setInt(4, episodesTotaux);
                insert.setInt(5, episodesVus);
                insert.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // =====================================================
// 🔎 RECHERCHE AUTOMATIQUE PAR TITRE
// =====================================================
    public List<Contenu> rechercherParTitre(String motCle) {

        List<Contenu> liste = new ArrayList<>();

        String sql = """
        SELECT c.*, g.nom AS genre_nom
        FROM contenus c
        LEFT JOIN genres g ON c.genre_id = g.id
        WHERE LOWER(c.titre) LIKE ?
        ORDER BY c.id DESC
    """;

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (motCle == null || motCle.isEmpty()) {
                ps.setString(1, "%");
            } else {
                ps.setString(1, "%" + motCle.toLowerCase() + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Contenu contenu = new Contenu();
                contenu.setId(rs.getInt("id"));
                contenu.setTitre(rs.getString("titre"));
                contenu.setType(rs.getString("type"));
                contenu.setAnneeSortie(rs.getInt("annee_sortie"));
                contenu.setRealisateur(rs.getString("realisateur"));
                contenu.setStatut(rs.getString("statut"));
                contenu.setNote(rs.getInt("note"));
                contenu.setGenreNom(rs.getString("genre_nom"));

                Timestamp ts = rs.getTimestamp("date_ajout");
                if (ts != null) {
                    contenu.setDateAjout(ts.toLocalDateTime());
                }

                // 🔥 Charger progression si série
                if ("SERIE".equals(contenu.getType())) {
                    ProgressionSerie p = getProgressionByContenu(contenu.getId());
                    if (p != null) {
                        contenu.setProgression(p.getProgression());
                    }
                }

                liste.add(contenu);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }

    // =====================================================
// 🔎 FILTRE GLOBAL (Genre + Statut + Recherche)
// =====================================================
    public List<Contenu> filtrerGlobal(String genre, String statut, String motCle) {

        List<Contenu> liste = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT c.*, g.nom AS genre_nom
        FROM contenus c
        LEFT JOIN genres g ON c.genre_id = g.id
        WHERE 1=1
    """);

        if (genre != null && !genre.equals("Tous")) {
            sql.append(" AND g.nom = ?");
        }

        if (statut != null && !statut.equals("Tous")) {
            sql.append(" AND c.statut = ?");
        }

        if (motCle != null && !motCle.trim().isEmpty()) {
            sql.append(" AND LOWER(c.titre) LIKE ?");
        }

        sql.append(" ORDER BY c.id DESC");

        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (genre != null && !genre.equals("Tous")) {
                ps.setString(index++, genre);
            }

            if (statut != null && !statut.equals("Tous")) {
                ps.setString(index++, statut);
            }

            if (motCle != null && !motCle.trim().isEmpty()) {
                ps.setString(index++, "%" + motCle.toLowerCase().trim() + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Contenu contenu = new Contenu();
                contenu.setId(rs.getInt("id"));
                contenu.setTitre(rs.getString("titre"));
                contenu.setType(rs.getString("type"));
                contenu.setAnneeSortie(rs.getInt("annee_sortie"));
                contenu.setRealisateur(rs.getString("realisateur"));
                contenu.setStatut(rs.getString("statut"));
                contenu.setNote(rs.getInt("note"));
                contenu.setGenreNom(rs.getString("genre_nom"));

                liste.add(contenu);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return liste;
    }
    // ================= DASHBOARD STATS =================
    public int getTotalContenus() {
        String sql = "SELECT COUNT(*) FROM contenus";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalByStatut(String statut) {
        String sql = "SELECT COUNT(*) FROM contenus WHERE statut = ?";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, statut);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getMoyenneNote() {
        String sql = "SELECT AVG(note) FROM contenus";
        try (Connection conn = ApiClient.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}