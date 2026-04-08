package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsultaDAO {

    private final JdbcTemplate jdbcTemplate;

    public ConsultaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getInt("id"));
        map.put("mascota_id", rs.getInt("mascota_id"));
        map.put("diagnostico", rs.getString("diagnostico"));
        return map;
    };

    /*---------------------------- CRUD ----------------------------*/

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM consulta";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Map<String, Object> findById(int id) {
        String sql = "SELECT * FROM consulta WHERE id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }

    public List<Map<String, Object>> findByMascotaId(int mascotaId) {
        String sql = "SELECT * FROM consulta WHERE mascota_id=?";
        return jdbcTemplate.query(sql, new Object[]{mascotaId}, rowMapper);
    }

    public void save(int mascotaId, String diagnostico) {
        String sql = "INSERT INTO consulta(mascota_id, diagnostico) VALUES (?, ?)";
        jdbcTemplate.update(sql, mascotaId, diagnostico);
    }

    public void update(int id, int mascotaId, String diagnostico) {
        String sql = "UPDATE consulta SET mascota_id=?, diagnostico=? WHERE id=?";
        jdbcTemplate.update(sql, mascotaId, diagnostico, id);
    }

    public void delete(int id) {
        String sql = "DELETE FROM consulta WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

    /*---------------------------- CONSULTA CON TRATAMIENTOS ----------------------------*/

    // یک Consulta به همراه تمام Tratamientos مرتبط
    public Map<String, Object> findConsultaWithTratamientos(int id) {
        Map<String, Object> consulta = new HashMap<>(findById(id)); // ← استفاده از HashMap قابل تغییر

        String sqlTratamientos = "SELECT * FROM tratamiento WHERE consulta_id=?";
        List<Map<String, Object>> tratamientos = jdbcTemplate.query(sqlTratamientos, (rs, rowNum) -> {
            Map<String, Object> t = new HashMap<>();
            t.put("id", rs.getInt("id"));
            t.put("consulta_id", rs.getInt("consulta_id"));
            t.put("tratamiento", rs.getString("tratamiento"));
            t.put("fecha_inicio", rs.getObject("fecha_inicio", LocalDate.class));
            t.put("fecha_fin", rs.getObject("fecha_fin", LocalDate.class));
            t.put("observaciones", rs.getString("observaciones"));
            return t;
        }, id);

        consulta.put("tratamientos", tratamientos); // ← اضافه کردن List به Map
        return consulta;
    }

    // همه Consultas همراه با Tratamientos
    public List<Map<String, Object>> findAllWithTratamientos() {
        List<Map<String, Object>> consultas = findAll(); // همه Consultas
        for (Map<String, Object> c : consultas) {
            int consultaId = (Integer) c.get("id");

            String sqlTratamientos = "SELECT * FROM tratamiento WHERE consulta_id=?";
            List<Map<String, Object>> tratamientos = jdbcTemplate.query(sqlTratamientos, (rs, rowNum) -> {
                Map<String, Object> t = new HashMap<>();
                t.put("id", rs.getInt("id"));
                t.put("consulta_id", rs.getInt("consulta_id"));
                t.put("tratamiento", rs.getString("tratamiento"));
                t.put("fecha_inicio", rs.getObject("fecha_inicio", LocalDate.class));
                t.put("fecha_fin", rs.getObject("fecha_fin", LocalDate.class));
                t.put("observaciones", rs.getString("observaciones"));
                return t;
            }, consultaId);

            // حتماً Map قابل تغییر است
            Map<String, Object> consultaMutable = new HashMap<>(c);
            consultaMutable.put("tratamientos", tratamientos);
            c.clear();
            c.putAll(consultaMutable); // ← جایگزینی Map اصلی با نسخه قابل تغییر
        }
        return consultas;
    }
}