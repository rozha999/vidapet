package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Component
public class MascotaDAO {

    private final JdbcTemplate jdbcTemplate;

    public MascotaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "nombre", rs.getString("nombre"),
            "especie", rs.getString("especie"),
            "raza", rs.getString("raza"),
            "fecha_nacimiento", rs.getObject("fecha_nacimiento", LocalDate.class),
            "propietario_id", rs.getLong("propietario_id"),
            "foto", rs.getString("foto") // ⭐ جدید
    );

    public List<Map<String,Object>> findAll() {
        String sql =
                "SELECT m.id, m.nombre, m.especie, m.raza, m.fecha_nacimiento, " +
                        "m.propietario_id, m.foto, " +
                        "p.nombre AS propietario_nombre, " +
                        "p.apellido AS propietario_apellido " +
                        "FROM mascota m " +
                        "JOIN propietario p ON m.propietario_id = p.id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();

            map.put("id", rs.getLong("id"));
            map.put("nombre", rs.getString("nombre"));
            map.put("especie", rs.getString("especie"));
            map.put("raza", rs.getString("raza"));
            map.put("fecha_nacimiento", rs.getObject("fecha_nacimiento", LocalDate.class));
            map.put("propietario_id", rs.getLong("propietario_id"));

            map.put("foto", rs.getString("foto"));

            // ⭐ IMPORTANT
            map.put("propietario_nombre", rs.getString("propietario_nombre"));
            map.put("propietario_apellido", rs.getString("propietario_apellido"));

            return map;
        });
    }
    public Map<String,Object> findById(Long id) {
        String sql =
                "SELECT m.id, m.nombre, m.especie, m.raza, m.fecha_nacimiento, " +
                        "m.propietario_id, m.foto, " +
                        "p.nombre AS propietario_nombre, " +
                        "p.apellido AS propietario_apellido " +
                        "FROM mascota m " +
                        "JOIN propietario p ON m.propietario_id = p.id " +
                        "WHERE m.id = ?";

        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public void save(String nombre, String especie, String raza,
                     LocalDate fechaNacimiento, Long propietarioId, String foto) {

        jdbcTemplate.update(
                "INSERT INTO mascota(nombre, especie, raza, fecha_nacimiento, propietario_id, foto) VALUES (?, ?, ?, ?, ?, ?)",
                nombre, especie, raza, fechaNacimiento, propietarioId, foto
        );
    }

    public void update(Long id, String nombre, String especie, String raza,
                       LocalDate fechaNacimiento, Long propietarioId, String foto) {

        jdbcTemplate.update(
                "UPDATE mascota SET nombre=?, especie=?, raza=?, fecha_nacimiento=?, propietario_id=?, foto=? WHERE id=?",
                nombre, especie, raza, fechaNacimiento, propietarioId, foto, id
        );
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM mascota WHERE id=?", id);
    }
    public Map<String, Object> findPropietarioByMascota(Long mascotaId) {

        return jdbcTemplate.queryForMap("""
        SELECT p.id, p.nombre, p.apellido
        FROM mascota m
        JOIN propietario p ON m.propietario_id = p.id
        WHERE m.id = ?
    """, mascotaId);
    }
}