package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TratamientoDAO {

    private final JdbcTemplate jdbcTemplate;

    public TratamientoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===--== RowMapper para mapear resultados de la tabla 'tratamiento' =====
    private RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "tipo", rs.getString("tipo"),
            "duracion", rs.getString("duracion"),  // duración como String
            "consulta_id", rs.getLong("consulta_id")
    );

    /*---------------------------- MÉTODOS CRUD ----------------------------*/

    // ===== Listar todos los tratamientos =====
    public List<Map<String,Object>> findAll() {
        return jdbcTemplate.query("SELECT * FROM tratamiento", rowMapper);
    }

    // ===== Obtener un tratamiento por ID =====
    public Map<String,Object> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM tratamiento WHERE id=?",
                new Object[]{id},
                rowMapper
        );
    }

    // ===== Guardar un nuevo tratamiento =====
    public void save(String tipo, String duracion, Long consultaId) {
        jdbcTemplate.update(
                "INSERT INTO tratamiento(tipo, duracion, consulta_id) VALUES (?, ?, ?)",
                tipo, duracion, consultaId
        );
    }

    // ===== Actualizar un tratamiento existente =====
    public void update(Long id, String tipo, String duracion, Long consultaId) {
        jdbcTemplate.update(
                "UPDATE tratamiento SET tipo=?, duracion=?, consulta_id=? WHERE id=?",
                tipo, duracion, consultaId, id
        );
    }

    // ===== Eliminar un tratamiento por ID =====
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM tratamiento WHERE id=?", id);
    }
}