package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Component
public class ConsultaDAO {

    private final JdbcTemplate jdbcTemplate;

    public ConsultaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ===== RowMapper para mapear resultados de la tabla 'consulta' =====
    private RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "fecha", rs.getObject("fecha", LocalDateTime.class), // fecha como LocalDateTime
            "motivo", rs.getString("motivo"),
            "diagnostico", rs.getString("diagnostico"),
            "mascota_id", rs.getLong("mascota_id")
    );

    /*---------------------------- MÉTODOS CRUD ----------------------------*/

    // ===== Listar todas las consultas =====
    public List<Map<String,Object>> findAll() {
        return jdbcTemplate.query("SELECT * FROM consulta", rowMapper);
    }

    // ===== Obtener una consulta por ID =====
    public Map<String,Object> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM consulta WHERE id=?",
                new Object[]{id},
                rowMapper
        );
    }

    // ===== Guardar una nueva consulta =====
    public void save(LocalDateTime fecha, String motivo, String diagnostico, Long mascotaId) {
        jdbcTemplate.update(
                "INSERT INTO consulta(fecha, motivo, diagnostico, mascota_id) VALUES (?, ?, ?, ?)",
                fecha, motivo, diagnostico, mascotaId
        );
    }

    // ===== Actualizar una consulta existente =====
    public void update(Long id, LocalDateTime fecha, String motivo, String diagnostico, Long mascotaId) {
        jdbcTemplate.update(
                "UPDATE consulta SET fecha=?, motivo=?, diagnostico=?, mascota_id=? WHERE id=?",
                fecha, motivo, diagnostico, mascotaId, id
        );
    }

    // ===== Eliminar una consulta por ID =====
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM consulta WHERE id=?", id);
    }
}