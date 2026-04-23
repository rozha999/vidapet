package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
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


    private final RowMapper<Map<String, Object>> rowMapperFull = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getLong("id"));
        map.put("nombre", rs.getString("nombre"));
        map.put("raza", rs.getString("raza"));
        map.put("fecha_nacimiento", rs.getObject("fecha_nacimiento", LocalDate.class));
        map.put("propietario_id", rs.getLong("propietario_id"));
        map.put("especie_id", rs.getLong("especie_id"));
        map.put("foto", rs.getString("foto"));

        // اطلاعات استخراج شده از JOIN (نام صاحب و نام گونه)
        map.put("propietario_nombre", rs.getString("propietario_nombre"));
        map.put("propietario_apellido", rs.getString("propietario_apellido"));
        map.put("especie_nombre", rs.getString("especie_nombre"));

        return map;
    };


    public List<Map<String, Object>> findAll() {
        String sql = """
                SELECT m.id, m.nombre, m.raza, m.fecha_nacimiento, m.foto, 
                       m.propietario_id, m.especie_id,
                       p.nombre AS propietario_nombre, p.apellido AS propietario_apellido,
                       e.nombre AS especie_nombre
                FROM mascota m
                JOIN propietario p ON m.propietario_id = p.id
                LEFT JOIN especie e ON m.especie_id = e.id
                """;
        return jdbcTemplate.query(sql, rowMapperFull);
    }


    public Map<String, Object> findById(Long id) {
        String sql = """
                SELECT m.id, m.nombre, m.raza, m.fecha_nacimiento, m.foto, 
                       m.propietario_id, m.especie_id,
                       p.nombre AS propietario_nombre, p.apellido AS propietario_apellido,
                       e.nombre AS especie_nombre
                FROM mascota m
                JOIN propietario p ON m.propietario_id = p.id
                LEFT JOIN especie e ON m.especie_id = e.id
                WHERE m.id = ?
                """;
        return jdbcTemplate.queryForObject(sql, rowMapperFull, id);
    }


    public void save(String nombre, Long especieId, String raza,
                     LocalDate fechaNacimiento, Long propietarioId, String foto) {
        String sql = """
                INSERT INTO mascota (nombre, especie_id, raza, fecha_nacimiento, propietario_id, foto)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql, nombre, especieId, raza, fechaNacimiento, propietarioId, foto);
    }

    public void update(Long id, String nombre, Long especieId, String raza,
                       LocalDate fechaNacimiento, Long propietarioId, String foto) {
        String sql = """
                UPDATE mascota 
                SET nombre=?, especie_id=?, raza=?, fecha_nacimiento=?, propietario_id=?, foto=? 
                WHERE id=?
                """;
        jdbcTemplate.update(sql, nombre, especieId, raza, fechaNacimiento, propietarioId, foto, id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM mascota WHERE id=?", id);
    }


    public List<Map<String, Object>> search(String s) {
        String sql = """
                SELECT m.id, m.nombre, m.raza, m.fecha_nacimiento, m.foto, 
                       m.propietario_id, m.especie_id,
                       p.nombre AS propietario_nombre, p.apellido AS propietario_apellido,
                       e.nombre AS especie_nombre
                FROM mascota m
                JOIN propietario p ON m.propietario_id = p.id
                LEFT JOIN especie e ON m.especie_id = e.id
                WHERE LOWER(m.nombre) LIKE LOWER(?)
                """;
        String k = "%" + s + "%";
        return jdbcTemplate.query(sql, rowMapperFull, k);
    }
    public Map<String, Object> findPropietarioByMascota(Long mascotaId) {
        String sql = """
        SELECT p.id, p.nombre, p.apellido
        FROM mascota m
        JOIN propietario p ON m.propietario_id = p.id
        WHERE m.id = ?
    """;
        try {

            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, mascotaId);
            if (result.isEmpty()) {
                return Collections.emptyMap();
            }
            return result.get(0);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
    public List<Map<String, Object>> buscarPorNombre(String nombre) {
        String sql = "SELECT m.*, e.nombre as especie_nombre, p.nombre as propietario_nombre, p.apellido as propietario_apellido " +
                "FROM mascota m " +
                "JOIN especie e ON m.especie_id = e.id " +
                "JOIN propietario p ON m.propietario_id = p.id " +
                "WHERE m.nombre LIKE ? OR p.nombre LIKE ?";

        return jdbcTemplate.queryForList(sql, "%" + nombre + "%", "%" + nombre + "%");
    }
    public List<Map<String, Object>> listarTodas() {
        String sql = "SELECT m.*, e.nombre as especie_nombre, p.nombre as propietario_nombre, p.apellido as propietario_apellido " +
                "FROM mascota m " +
                "LEFT JOIN especie e ON m.especie_id = e.id " +
                "LEFT JOIN propietario p ON m.propietario_id = p.id";

        return jdbcTemplate.queryForList(sql);
    }

}