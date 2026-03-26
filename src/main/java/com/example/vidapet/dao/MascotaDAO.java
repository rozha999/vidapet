package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Component
public class MascotaDAO {

    private final JdbcTemplate jdbcTemplate;

    public MascotaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== RowMapper para mapear resultados de la tabla 'mascota' =====
    private final RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "nombre", rs.getString("nombre"),
            "especie", rs.getString("especie"),
            "raza", rs.getString("raza"),
            "fecha_nacimiento", rs.getObject("fecha_nacimiento", LocalDate.class),
            "propietario_id", rs.getLong("propietario_id")
    );

    /*---------------------------- MÉTODOS CRUD ----------------------------*/

    // ===== Listar todas las mascotas con información de su propietario =====
    public List<Map<String,Object>> findAll() {
        String sql = "SELECT m.id, m.nombre, m.especie, m.raza, m.fecha_nacimiento, " +
                "m.propietario_id, p.nombre AS propietario_nombre, p.apellido AS propietario_apellido " +
                "FROM mascota m " +
                "JOIN propietario p ON m.propietario_id = p.id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> Map.of(
                "id", rs.getLong("id"),
                "nombre", rs.getString("nombre"),
                "especie", rs.getString("especie"),
                "raza", rs.getString("raza"),
                "fecha_nacimiento", rs.getObject("fecha_nacimiento", LocalDate.class),
                "propietario_id", rs.getLong("propietario_id"),
                "propietario_nombre", rs.getString("propietario_nombre"),
                "propietario_apellido", rs.getString("propietario_apellido")
        ));
    }

    // ===== Obtener una mascota por ID =====
    public Map<String,Object> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM mascota WHERE id=?",
                new Object[]{id},
                rowMapper
        );
    }

    // ===== Guardar una nueva mascota =====
    public void save(String nombre, String especie, String raza, LocalDate fechaNacimiento, Long propietarioId) {
        jdbcTemplate.update(
                "INSERT INTO mascota(nombre, especie, raza, fecha_nacimiento, propietario_id) VALUES (?, ?, ?, ?, ?)",
                nombre, especie, raza, fechaNacimiento, propietarioId
        );
    }

    // ===== Actualizar una mascota existente =====
    public void update(Long id, String nombre, String especie, String raza, LocalDate fechaNacimiento, Long propietarioId) {
        jdbcTemplate.update(
                "UPDATE mascota SET nombre=?, especie=?, raza=?, fecha_nacimiento=?, propietario_id=? WHERE id=?",
                nombre, especie, raza, fechaNacimiento, propietarioId, id
        );
    }

    // ===== Eliminar una mascota por ID =====
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM mascota WHERE id=?", id);
    }
}