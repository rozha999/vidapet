package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class TratamientoDAO {

    private final JdbcTemplate jdbcTemplate;

    public TratamientoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getInt("id"),
            "consulta_id", rs.getInt("consulta_id"),
            "tratamiento", rs.getString("tratamiento"),
            "fecha_inicio", rs.getObject("fecha_inicio", LocalDate.class),
            "fecha_fin", rs.getObject("fecha_fin", LocalDate.class),
            "observaciones", rs.getString("observaciones")
    );

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM tratamiento";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Map<String, Object> findById(int id) {
        String sql = "SELECT * FROM tratamiento WHERE id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }

    public List<Map<String, Object>> findByConsultaId(int consultaId) {
        String sql = "SELECT * FROM tratamiento WHERE consulta_id=?";
        return jdbcTemplate.query(sql, new Object[]{consultaId}, rowMapper);
    }

    public void save(int consultaId, String tratamiento, LocalDate fechaInicio, LocalDate fechaFin, String observaciones) {
        String sql = "INSERT INTO tratamiento(consulta_id, tratamiento, fecha_inicio, fecha_fin, observaciones) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, consultaId, tratamiento, fechaInicio, fechaFin, observaciones);
    }

    public void update(int id, int consultaId, String tratamiento, LocalDate fechaInicio, LocalDate fechaFin, String observaciones) {
        String sql = "UPDATE tratamiento SET consulta_id=?, tratamiento=?, fecha_inicio=?, fecha_fin=?, observaciones=? WHERE id=?";
        jdbcTemplate.update(sql, consultaId, tratamiento, fechaInicio, fechaFin, observaciones, id);
    }

    public void delete(int id) {
        String sql = "DELETE FROM tratamiento WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}