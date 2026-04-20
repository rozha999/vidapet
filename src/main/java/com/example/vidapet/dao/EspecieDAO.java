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

    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();

        map.put("id", rs.getLong("id"));
        map.put("nombre", rs.getString("nombre"));
        map.put("foto", rs.getString("foto"));

        return map;
    };

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.query("SELECT * FROM especie", rowMapper);
    }

    public Map<String, Object> findById(Long id) {
        return jdbcTemplate.queryForMap(
                "SELECT * FROM especie WHERE id=?",
                id
        );
    }

    public void save(String nombre, String foto) {
        jdbcTemplate.update("""
            INSERT INTO especie (nombre, foto)
            VALUES (?, ?)
        """, nombre, foto);
    }

    public void update(Long id, String nombre, String foto) {
        jdbcTemplate.update("""
            UPDATE especie
            SET nombre=?, foto=?
            WHERE id=?
        """, nombre, foto, id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM especie WHERE id=?", id);
    }

    public List<Map<String, Object>> search(String s) {
        String sql = """
            SELECT * FROM especie
            WHERE LOWER(nombre) LIKE LOWER(?)
        """;

        String k = "%" + s + "%";
        return jdbcTemplate.query(sql, rowMapper, k);
    }
}