package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class citaDAO {

    private final JdbcTemplate jdbcTemplate;

    public citaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();

        map.put("id", rs.getInt("id"));
        map.put("mascota_id", rs.getInt("mascota_id"));
        map.put("propietario_id", rs.getInt("propietario_id"));
        map.put("fecha", rs.getObject("fecha", LocalDateTime.class));
        map.put("nota", rs.getString("nota"));

        map.put("estado", rs.getString("estado"));
        map.put("consulta_id", rs.getObject("consulta_id")); // ⭐ NEW

        map.put("mascota_nombre", rs.getString("mascota_nombre"));
        map.put("propietario_nombre", rs.getString("propietario_nombre"));
        map.put("propietario_apellido", rs.getString("propietario_apellido"));

        return map;
    };

    public List<Map<String, Object>> findFiltered(
            String mascota,
            String propietario,
            LocalDate fecha,
            String orden
    ) {

        String sql = """
        SELECT c.id,
               c.mascota_id,
               c.propietario_id,
               c.fecha,
               c.nota,
               c.estado,
               co.id AS consulta_id,
               m.nombre AS mascota_nombre,
               p.nombre AS propietario_nombre,
               p.apellido AS propietario_apellido
        FROM cita c
        LEFT JOIN consulta co ON co.cita_id = c.id
        JOIN mascota m ON c.mascota_id = m.id
        JOIN propietario p ON c.propietario_id = p.id
        WHERE 1=1
        """;

        List<Object> params = new ArrayList<>();

        if (mascota != null && !mascota.isEmpty()) {
            sql += " AND LOWER(m.nombre) LIKE ?";
            params.add("%" + mascota.toLowerCase() + "%");
        }

        if (propietario != null && !propietario.isEmpty()) {
            sql += " AND LOWER(p.nombre) LIKE ?";
            params.add("%" + propietario.toLowerCase() + "%");
        }

        if (fecha != null) {
            sql += " AND DATE(c.fecha) = ?";
            params.add(fecha);
        }

        sql += " ORDER BY c.fecha " + ("desc".equalsIgnoreCase(orden) ? "DESC" : "ASC");

        return jdbcTemplate.query(sql, rowMapper, params.toArray());
    }

    public void save(int mascotaId,
                     int propietarioId,
                     LocalDateTime fecha,
                     String nota) {

        jdbcTemplate.update("""
            INSERT INTO cita (mascota_id, propietario_id, fecha, nota, estado)
            VALUES (?, ?, ?, ?, ?)
        """, mascotaId, propietarioId, fecha, nota, "ESPERANDO");
    }

    public Map<String, Object> findById(int id) {
        return jdbcTemplate.queryForMap("SELECT * FROM cita WHERE id=?", id);
    }

    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM cita WHERE id=?", id);
    }

    public void updateEstado(int id, String estado) {
        jdbcTemplate.update("UPDATE cita SET estado=? WHERE id=?", estado, id);
    }
    public void update(int id, int mascotaId, int propietarioId, LocalDateTime fecha, String nota) {
        jdbcTemplate.update("""
        UPDATE cita
        SET mascota_id=?, propietario_id=?, fecha=?, nota=?
        WHERE id=?
    """, mascotaId, propietarioId, fecha, nota, id);
    }
}