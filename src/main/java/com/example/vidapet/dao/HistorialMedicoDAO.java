package com.example.vidapet.dao;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HistorialMedicoDAO {

    private final JdbcTemplate jdbcTemplate;

    public HistorialMedicoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper para mapear resultados de HistorialMedico
    private RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> Map.of(
            "id", rs.getInt("id"),
            "mascota_id", rs.getInt("mascota_id"),
            "fecha", rs.getString("fecha"),
            "problema", rs.getString("problema"),
            "tratamiento_id", rs.getObject("tratamiento_id"),
            "nombre_tratamiento", rs.getString("nombre_tratamiento"),
            "notas", rs.getString("notas")
    );

    /*---------------------------- CRUD ----------------------------*/

    // Listar todos los historiales
    public List<Map<String,Object>> findAll() {
        String sql = "SELECT * FROM HistorialMedico";
        return jdbcTemplate.query(sql, rowMapper);
    }

    // Obtener historial por ID
    public Map<String,Object> findById(int id) {
        String sql = "SELECT * FROM HistorialMedico WHERE id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }

    // Listar historial por mascota_id
    public List<Map<String,Object>> findByMascotaId(int mascotaId) {
        String sql = "SELECT * FROM HistorialMedico WHERE mascota_id=?";
        return jdbcTemplate.query(sql, new Object[]{mascotaId}, rowMapper);
    }

    // Guardar nuevo historial
    public void save(int mascotaId, String fecha, String problema, Integer tratamientoId, String nombreTratamiento, String notas) {
        String sql = "INSERT INTO HistorialMedico(mascota_id, fecha, problema, tratamiento_id, nombre_tratamiento, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, mascotaId, fecha, problema, tratamientoId, nombreTratamiento, notas);
    }

    // Actualizar historial existente
    public void update(int id, int mascotaId, String fecha, String problema, Integer tratamientoId, String nombreTratamiento, String notas) {
        String sql = "UPDATE HistorialMedico SET mascota_id=?, fecha=?, problema=?, tratamiento_id=?, nombre_tratamiento=?, notas=? WHERE id=?";
        jdbcTemplate.update(sql, mascotaId, fecha, problema, tratamientoId, nombreTratamiento, notas, id);
    }

    // Eliminar historial por ID
    public void delete(int id) {
        String sql = "DELETE FROM HistorialMedico WHERE id=?";
        jdbcTemplate.update(sql, id);
    }
}