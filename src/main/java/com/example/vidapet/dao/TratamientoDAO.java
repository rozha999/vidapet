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

    // === RowMapper para mapear resultados de la tabla 'tratamiento' ===
    private RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "nombre", rs.getString("nombre")
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
    public void save(String nombre) {
        jdbcTemplate.update(
                "INSERT INTO tratamiento(nombre) VALUES (?)",
                nombre
        );
    }

    // ===== Actualizar un tratamiento existente =====
    public void update(Long id, String nombre) {
        jdbcTemplate.update(
                "UPDATE tratamiento SET nombre=? WHERE id=?",
                nombre, id
        );
    }

    // ===== Eliminar un tratamiento por ID =====
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM tratamiento WHERE id=?", id);
    }
}