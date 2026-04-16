package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
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

    /* ---------------- ROW MAPPER ---------------- */

    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getInt("id"));
        map.put("mascota_id", rs.getInt("mascota_id"));
        map.put("diagnostico", rs.getString("diagnostico"));
        map.put("mascota_nombre", rs.getString("mascota_nombre"));
        return map;
    };

    /* ---------------- FIND ALL ---------------- */

    public List<Map<String, Object>> findAll() {

        String sql = """
            SELECT c.id, c.mascota_id, c.diagnostico, m.nombre AS mascota_nombre
            FROM consulta c
            JOIN mascota m ON c.mascota_id = m.id
        """;

        return jdbcTemplate.query(sql, rowMapper);
    }

    /* ---------------- FIND BY ID ---------------- */

    public Map<String, Object> findById(int id) {

        String sql = """
        SELECT c.id, c.mascota_id, c.diagnostico, m.nombre AS mascota_nombre
        FROM consulta c
        JOIN mascota m ON c.mascota_id = m.id
        WHERE c.id = ?
    """;

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
        } catch (Exception e) {
            return null;
        }
    }

    /* ---------------- FIND BY MASCOTA ID ---------------- */

    public List<Map<String, Object>> findByMascotaId(int mascotaId) {

        String sql = """
            SELECT c.id, c.mascota_id, c.diagnostico, m.nombre AS mascota_nombre
            FROM consulta c
            JOIN mascota m ON c.mascota_id = m.id
            WHERE c.mascota_id = ?
        """;

        return jdbcTemplate.query(sql, new Object[]{mascotaId}, rowMapper);
    }

    /* ---------------- SAVE ---------------- */

    public int save(int mascotaId, String diagnostico) {

        String sql = "INSERT INTO consulta(mascota_id, diagnostico) VALUES (?, ?)";

        jdbcTemplate.update(sql, mascotaId, diagnostico);

        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
    }

    /* ---------------- UPDATE ---------------- */

    public void update(int id, int mascotaId, String diagnostico) {

        String sql = "UPDATE consulta SET mascota_id=?, diagnostico=? WHERE id=?";

        jdbcTemplate.update(sql, mascotaId, diagnostico, id);
    }

    /* ---------------- DELETE ---------------- */

    public void delete(int id) {

        jdbcTemplate.update("DELETE FROM consulta WHERE id=?", id);
    }

    /* ---------------- SEARCH BY MASCOTA NAME ---------------- */

    public List<Map<String, Object>> searchByMascota(String nombre) {

        String sql = """
            SELECT c.id, c.mascota_id, c.diagnostico, m.nombre AS mascota_nombre
            FROM consulta c
            JOIN mascota m ON c.mascota_id = m.id
            WHERE m.nombre LIKE ?
        """;

        List<Map<String, Object>> consultas = jdbcTemplate.query(
                sql,
                new Object[]{"%" + nombre + "%"},
                rowMapper
        );


        return consultas;
    }

    /* ---------------- FIND ALL WITH TRATAMIENTOS ---------------- */

    public List<Map<String, Object>> findAllWithTratamientos() {

        List<Map<String, Object>> consultas = findAll();

        attachTratamientos(consultas);

        return consultas;
    }

    /* ---------------- FIND BY ID WITH TRATAMIENTOS ---------------- */

    public Map<String, Object> findConsultaWithTratamientos(int id) {

        Map<String, Object> consulta = new HashMap<>(findById(id));

        String sql = "SELECT * FROM tratamiento WHERE consulta_id=?";

        List<Map<String, Object>> tratamientos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> t = new HashMap<>();
            t.put("id", rs.getInt("id"));
            t.put("consulta_id", rs.getInt("consulta_id"));
            t.put("tratamiento", rs.getString("tratamiento"));
            t.put("fecha_inicio", rs.getObject("fecha_inicio", LocalDate.class));
            t.put("fecha_fin", rs.getObject("fecha_fin", LocalDate.class));
            t.put("observaciones", rs.getString("observaciones"));
            return t;
        }, id);

        consulta.put("tratamientos", tratamientos);

        return consulta;
    }

    /* ---------------- SAVE WITH CITA ---------------- */

    public int saveConCita(int citaId, int mascotaId, String diagnostico) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO consulta (cita_id, mascota_id, diagnostico) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, citaId);
            ps.setInt(2, mascotaId);
            ps.setString(3, diagnostico);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    /* ---------------- FULL DETAIL ---------------- */

    public Map<String, Object> findConsultaFull(int id) {

        String sql = """
            SELECT 
                c.id,
                c.diagnostico,

                m.nombre AS mascota_nombre,
                m.especie,
                m.raza,
                m.fecha_nacimiento,
                m.foto,

                p.nombre AS propietario_nombre,
                p.apellido AS propietario_apellido,
                p.telefono AS propietario_telefono,
                p.email AS propietario_email

            FROM consulta c
            JOIN mascota m ON c.mascota_id = m.id
            JOIN propietario p ON m.propietario_id = p.id
            WHERE c.id = ?
        """;

        Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);

        List<Map<String, Object>> tratamientos = jdbcTemplate.query(
                "SELECT * FROM tratamiento WHERE consulta_id=?",
                (rs, rowNum) -> {
                    Map<String, Object> t = new HashMap<>();
                    t.put("tratamiento", rs.getString("tratamiento"));
                    t.put("fecha_inicio", rs.getObject("fecha_inicio"));
                    t.put("fecha_fin", rs.getObject("fecha_fin"));
                    t.put("observaciones", rs.getString("observaciones"));
                    return t;
                },
                id
        );

        data.put("tratamientos", tratamientos);

        return data;
    }

    /* ---------------- HELPER (IMPORTANT) ---------------- */

    private void attachTratamientos(List<Map<String, Object>> consultas) {

        for (Map<String, Object> c : consultas) {

            int consultaId = (Integer) c.get("id");

            String sql = "SELECT * FROM tratamiento WHERE consulta_id=?";

            List<Map<String, Object>> tratamientos = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> t = new HashMap<>();
                t.put("id", rs.getInt("id"));
                t.put("tratamiento", rs.getString("tratamiento"));
                return t;
            }, consultaId);

            c.put("tratamientos", tratamientos);
        }
    }
}