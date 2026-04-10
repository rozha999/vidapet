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
        map.put("mascota_nombre", rs.getString("mascota_nombre")); // ✅ مهم
        return map;
    };
    /*---------------------------- CRUD ----------------------------*/

    public List<Map<String, Object>> findAll() {
        String sql = "SELECT c.id, c.mascota_id, c.diagnostico, m.nombre AS mascota_nombre " +
                "FROM consulta c " +
                "JOIN mascota m ON c.mascota_id = m.id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Map<String, Object> findById(int id) {
        String sql = "SELECT c.id, c.mascota_id, c.diagnostico, m.nombre AS mascota_nombre " +
                "FROM consulta c " +
                "JOIN mascota m ON c.mascota_id = m.id " +
                "WHERE c.id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
    }
    public List<Map<String, Object>> findByMascotaId(int mascotaId) {
        String sql = "SELECT * FROM consulta WHERE mascota_id=?";
        return jdbcTemplate.query(sql, new Object[]{mascotaId}, rowMapper);
    }

    public int save(int mascotaId, String diagnostico) {
        String sql = "INSERT INTO consulta(mascota_id, diagnostico) VALUES (?, ?)";
        jdbcTemplate.update(sql, mascotaId, diagnostico);

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
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
    public int saveConCita(int citaId, int mascotaId, String diagnostico) {

        jdbcTemplate.update("""
        INSERT INTO consulta (cita_id, mascota_id, diagnostico)
        VALUES (?, ?, ?)
    """, citaId, mascotaId, diagnostico);

        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );
    }
    public Map<String, Object> findConsultaFull(int id) {

        String sql = """
        SELECT 
            co.id AS consulta_id,
            co.diagnostico,

            m.id AS mascota_id,
            m.nombre AS mascota_nombre,
            m.especie,
            m.raza,

            p.id AS propietario_id,
            p.nombre AS propietario_nombre,
            p.apellido AS propietario_apellido,
            p.telefono,
            p.email

        FROM consulta co
        JOIN mascota m ON co.mascota_id = m.id
        JOIN propietario p ON m.propietario_id = p.id
        WHERE co.id = ?
    """;

        Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);

        List<Map<String, Object>> tratamientos = jdbcTemplate.query("""
        SELECT * FROM tratamiento WHERE consulta_id=?
    """, (rs, rowNum) -> {
            Map<String, Object> t = new HashMap<>();
            t.put("tratamiento", rs.getString("tratamiento"));
            t.put("fecha_inicio", rs.getObject("fecha_inicio"));
            t.put("fecha_fin", rs.getObject("fecha_fin"));
            t.put("observaciones", rs.getString("observaciones"));
            return t;
        }, id);

        data.put("tratamientos", tratamientos);

        return data;
    }
}