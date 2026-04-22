package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EspecieDAO {

    private final JdbcTemplate jdbcTemplate;

    public EspecieDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ================= ROW MAPPER =================
    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getLong("id"));
        map.put("nombre", rs.getString("nombre"));
        map.put("foto", rs.getString("foto"));
        return map;
    };

    // ================= FIND ALL =================
    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.query(
                "SELECT id, nombre, foto FROM especie ORDER BY id DESC",
                rowMapper
        );
    }

    // ================= FIND BY ID =================
    public Map<String, Object> findById(Long id) {
        try {
            return jdbcTemplate.queryForMap(
                    "SELECT id, nombre, foto FROM especie WHERE id=?",
                    id
            );
        } catch (Exception e) {
            return null;
        }
    }

    // ================= SAVE =================
    public void save(String nombre, String foto) {
        jdbcTemplate.update(
                "INSERT INTO especie (nombre, foto) VALUES (?, ?)",
                nombre,
                foto
        );
    }

    // ================= UPDATE =================
    public void update(Long id, String nombre, String foto) {
        jdbcTemplate.update(
                "UPDATE especie SET nombre=?, foto=? WHERE id=?",
                nombre,
                foto,
                id
        );
    }

    // ================= DELETE =================
    public void delete(Long id) {
        jdbcTemplate.update(
                "DELETE FROM especie WHERE id=?",
                id
        );
    }

    // ================= SEARCH =================
    public List<Map<String, Object>> search(String s) {

        String sql = """
            SELECT id, nombre, foto
            FROM especie
            WHERE LOWER(nombre) LIKE LOWER(?)
            ORDER BY id DESC
        """;

        return jdbcTemplate.query(sql, rowMapper, "%" + s.trim() + "%");
    }
}

