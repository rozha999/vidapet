package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
public class citaDAO {

    private final JdbcTemplate jdbcTemplate;

    public citaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private final RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getLong("id"),
            "mascota_id", rs.getLong("mascota_id"),
            "fecha", rs.getObject("fecha", LocalDate.class),
            "hora", rs.getObject("hora", LocalTime.class),
            "notas", rs.getString("notas"),
            "mascota_nombre", rs.getString("mascota_nombre"),
            "mascota_propietario", rs.getString("mascota_propietario")
    );

    /*================= CRUD =================*/
    public List<Map<String,Object>> findAll() {
        String sql = "SELECT c.*, m.nombre AS mascota_nombre, p.nombre AS mascota_propietario " +
                "FROM cita c " +
                "JOIN mascota m ON c.mascota_id = m.id " +
                "JOIN propietario p ON m.propietario_id = p.id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Map<String,Object> findById(Long id) {
        String sql = "SELECT c.*, m.nombre AS mascota_nombre, p.nombre AS mascota_propietario " +
                "FROM cita c " +
                "JOIN mascota m ON c.mascota_id = m.id " +
                "JOIN propietario p ON m.propietario_id = p.id " +
                "WHERE c.id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }

    public void save(Long mascotaId, LocalDate fecha, LocalTime hora, String notas) {
        String sql = "INSERT INTO cita (mascota_id, fecha, hora, notas) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, mascotaId, fecha, hora, notas);
    }

    public void update(Long id, Long mascotaId, LocalDate fecha, LocalTime hora, String notas) {
        String sql = "UPDATE cita SET mascota_id=?, fecha=?, hora=?, notas=? WHERE id=?";
        jdbcTemplate.update(sql, mascotaId, fecha, hora, notas, id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM cita WHERE id=?", id);
    }

    /*================= HORAS POR FECHA =================*/
    public List<String> findHorasByFecha(LocalDate fecha) {
        String sql = "SELECT hora FROM cita WHERE fecha=?";
        return jdbcTemplate.queryForList(sql, new Object[]{fecha}, String.class);
    }
}