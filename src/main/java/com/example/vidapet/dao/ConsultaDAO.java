package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsultaDAO {

    private final JdbcTemplate jdbcTemplate;

    public ConsultaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ---------------- ROW MAPPER  ---------------- */
    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getInt("id"));
        map.put("diagnostico", rs.getString("diagnostico"));
        map.put("cita_id", rs.getInt("cita_id"));

        try {
            map.put("fecha", rs.getObject("fecha"));
            map.put("mascota_nombre", rs.getString("mascota_nombre"));
        } catch (Exception e) {
            // اگر ستون‌ها در کوئری نبودند، مقدار پیش‌فرض
        }
        return map;
    };

    /* ---------------- FIND ALL  ---------------- */
    public List<Map<String, Object>> findAll() {
        String sql = """
            SELECT 
                c.id, 
                c.diagnostico, 
                c.cita_id,
                ci.fecha AS fecha, 
                m.nombre AS mascota_nombre 
            FROM consulta c
            JOIN cita ci ON c.cita_id = ci.id
            JOIN mascota m ON ci.mascota_id = m.id
        """;
        return jdbcTemplate.queryForList(sql);
    }

    /* ---------------- FIND BY ID (اصلاح شده) ---------------- */
    public Map<String, Object> findById(int id) {
        String sql = """
            SELECT 
                c.id, c.diagnostico, c.cita_id,
                ci.fecha AS fecha,
                m.nombre AS mascota_nombre
            FROM consulta c
            JOIN cita ci ON c.cita_id = ci.id
            JOIN mascota m ON ci.mascota_id = m.id
            WHERE c.id = ?
        """;
        try {
            return jdbcTemplate.queryForMap(sql, id);
        } catch (Exception e) {
            return null;
        }
    }

    /* ---------------- SAVE  ---------------- */
    public int save(int citaId, String diagnostico) {
        String sql = "INSERT INTO consulta (cita_id, diagnostico) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, citaId);
            ps.setString(2, diagnostico);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    /* ---------------- UPDATE ---------------- */
    public void update(int id, String diagnostico) {
        String sql = "UPDATE consulta SET diagnostico=? WHERE id=?";
        jdbcTemplate.update(sql, diagnostico, id);
    }

    /* ---------------- DELETE ---------------- */
    public void delete(int id) {
        jdbcTemplate.update("DELETE FROM tratamiento WHERE consulta_id=?", id);
        jdbcTemplate.update("DELETE FROM consulta WHERE id=?", id);
    }

    /* ---------------- FULL DETAIL ---------------- */
    public Map<String, Object> obtenerDetalleCompleto(int consultaId) {
        String sql = """
            SELECT 
                c.id, c.diagnostico, 
                ci.fecha AS fecha_consulta, 
                m.nombre AS mascota_nombre, m.foto, m.raza, m.fecha_nacimiento, 
                e.nombre AS especie_nombre, 
                p.nombre AS propietario_nombre, p.apellido AS propietario_apellido, 
                p.telefono AS propietario_telefono, p.email AS propietario_email, 
                v.nombre AS veterinario_nombre, v.apellido AS veterinario_apellido 
            FROM consulta c 
            JOIN cita ci ON c.cita_id = ci.id 
            JOIN mascota m ON ci.mascota_id = m.id 
            LEFT JOIN especie e ON m.especie_id = e.id 
            LEFT JOIN propietario p ON m.propietario_id = p.id 
            LEFT JOIN veterinario v ON ci.veterinario_id = v.id 
            WHERE c.id = ?
        """;
        try {
            Map<String, Object> data = jdbcTemplate.queryForMap(sql, consultaId);
            String sqlTratamientos = "SELECT * FROM tratamiento WHERE consulta_id = ?";
            data.put("tratamientos", jdbcTemplate.queryForList(sqlTratamientos, consultaId));
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    /* ---------------- SEARCH BY MASCOTA NAME ---------------- */
    public List<Map<String, Object>> searchByMascota(String nombre) {
        String sql = """
            SELECT c.*, m.nombre AS mascota_nombre, ci.fecha
            FROM consulta c
            JOIN cita ci ON c.cita_id = ci.id
            JOIN mascota m ON ci.mascota_id = m.id
            WHERE m.nombre LIKE ?
        """;
        return jdbcTemplate.queryForList(sql, "%" + nombre + "%");
    }

    /* ---------------- ATTACH TRATAMIENTOS ---------------- */
    public List<Map<String, Object>> findAllWithTratamientos() {
        List<Map<String, Object>> consultas = findAll();
        for (Map<String, Object> c : consultas) {
            int consultaId = (Integer) c.get("id");
            String sql = "SELECT * FROM tratamiento WHERE consulta_id=?";
            c.put("tratamientos", jdbcTemplate.queryForList(sql, consultaId));
        }
        return consultas;
    }
}