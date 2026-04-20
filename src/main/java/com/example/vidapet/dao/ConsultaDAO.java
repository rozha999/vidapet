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

    /* ---------------- ROW MAPPER (اصلاح شده) ---------------- */
    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getInt("id"));
        map.put("mascota_id", rs.getInt("mascota_id"));
        map.put("diagnostico", rs.getString("diagnostico"));

        // چک کردن وجود ستون تاریخ برای جلوگیری از ارور
        try {
            map.put("fecha", rs.getObject("fecha"));
        } catch (Exception e) {
            map.put("fecha", null);
        }

        String mascotaNombre = rs.getString("mascota_nombre");
        map.put("mascota_nombre", mascotaNombre != null ? mascotaNombre : "Sin mascota");
        return map;
    };

    /* ---------------- FIND ALL (با اضافه کردن تاریخ از جدول Cita) ---------------- */
    public List<Map<String, Object>> findAll() {
        String sql = """
            SELECT 
                c.id,
                c.mascota_id,
                c.diagnostico,
                ci.fecha AS fecha,
                COALESCE(m.nombre, 'Sin mascota') AS mascota_nombre
            FROM consulta c
            LEFT JOIN mascota m ON m.id = c.mascota_id
            LEFT JOIN cita ci ON c.cita_id = ci.id
            ORDER BY ci.fecha DESC
        """;
        return jdbcTemplate.query(sql, rowMapper);
    }

    /* ---------------- FIND BY ID ---------------- */
    public Map<String, Object> findById(int id) {
        String sql = """
            SELECT 
                c.id,
                c.mascota_id,
                c.diagnostico,
                ci.fecha AS fecha,
                COALESCE(m.nombre, 'Sin mascota') AS mascota_nombre
            FROM consulta c
            LEFT JOIN mascota m ON m.id = c.mascota_id
            LEFT JOIN cita ci ON c.cita_id = ci.id
            WHERE c.id = ?
        """;
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (Exception e) {
            return null;
        }
    }

    /* ---------------- SEARCH BY MASCOTA NAME ---------------- */
    public List<Map<String, Object>> searchByMascota(String nombre) {
        String sql = """
        SELECT c.*, m.nombre AS mascota_nombre, ci.fecha
        FROM consulta c
        JOIN mascota m ON c.mascota_id = m.id
        LEFT JOIN cita ci ON c.cita_id = ci.id
        WHERE m.nombre LIKE ?
    """;
        return jdbcTemplate.queryForList(sql, "%" + nombre + "%");
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
        jdbcTemplate.update("DELETE FROM tratamiento WHERE consulta_id=?", id);
        jdbcTemplate.update("DELETE FROM consulta WHERE id=?", id);
    }

    /* ---------------- FIND ALL WITH TRATAMIENTOS ---------------- */
    public List<Map<String, Object>> findAllWithTratamientos() {
        List<Map<String, Object>> consultas = findAll();
        attachTratamientos(consultas);
        return consultas;
    }

    /* ---------------- FIND BY ID WITH TRATAMIENTOS ---------------- */
    public Map<String, Object> findConsultaWithTratamientos(int id) {
        Map<String, Object> consulta = findById(id);
        if (consulta == null) return null;

        String sql = "SELECT * FROM tratamiento WHERE consulta_id=?";
        List<Map<String, Object>> tratamientos = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> t = new HashMap<>();
            t.put("id", rs.getInt("id"));
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
            LEFT JOIN cita ci ON c.cita_id = ci.id 
            LEFT JOIN mascota m ON c.mascota_id = m.id 
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

    /* ---------------- HELPER ---------------- */
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
    public Map<String, Object> findConsultaFull(int id) {
        // استفاده از LEFT JOIN برای جلوگیری از ارور در صورت خالی بودن گونه یا صاحب
        String sql = "SELECT " +
                "  c.id, c.diagnostico, " +
                "  m.nombre AS mascota_nombre, m.raza, m.fecha_nacimiento, m.foto, " +
                "  COALESCE(e.nombre, 'Sin especie') AS especie, " + // اگر گونه نبود بنویسد بدون گونه
                "  p.nombre AS propietario_nombre, p.apellido AS propietario_apellido, " +
                "  p.telefono AS propietario_telefono, p.email AS propietario_email " +
                "FROM consulta c " +
                "LEFT JOIN mascota m ON c.mascota_id = m.id " +
                "LEFT JOIN especie e ON m.especie_id = e.id " + // تغییر به LEFT JOIN
                "LEFT JOIN propietario p ON m.propietario_id = p.id " + // تغییر به LEFT JOIN
                "WHERE c.id = ?";

        try {
            // ۱. اجرای کوئری اصلی
            Map<String, Object> data = jdbcTemplate.queryForMap(sql, id);

            // ۲. گرفتن لیست درمان‌ها
            String sqlTratamientos = "SELECT tratamiento, fecha_inicio, fecha_fin, observaciones " +
                    "FROM tratamiento WHERE consulta_id = ?";
            List<Map<String, Object>> tratamientos = jdbcTemplate.queryForList(sqlTratamientos, id);

            // ۳. اضافه کردن لیست به مپ
            data.put("tratamientos", tratamientos);

            return data;
        } catch (Exception e) {
            // چاپ خطا در کنسول برای دیباگ راحت‌تر
            System.err.println("Error en findConsultaFull ID " + id + ": " + e.getMessage());
            return null;
        }
    }
    public Map<String, Object> obtenerDetalleCompletoParaFormulario(int mascotaId, int citaId) {
        String sql = """
        SELECT 
            m.nombre AS mascota_nombre, m.raza, m.fecha_nacimiento, m.foto,
            e.nombre AS especie, 
            p.nombre AS propietario_nombre, p.apellido AS propietario_apellido, 
            p.telefono AS propietario_telefono, p.email AS propietario_email,
            v.nombre AS nombre, v.apellido AS apellido
        FROM mascota m
        LEFT JOIN especie e ON m.especie_id = e.id
        LEFT JOIN propietario p ON m.propietario_id = p.id
        LEFT JOIN cita ci ON ci.mascota_id = m.id
        LEFT JOIN veterinario v ON ci.veterinario_id = v.id
        WHERE m.id = ? AND ci.id = ?
    """;
        try {
            return jdbcTemplate.queryForMap(sql, mascotaId, citaId);
        } catch (Exception e) {
            return new HashMap<>(); // برگشت مپ خالی برای جلوگیری از ارور 500
        }
    }

}