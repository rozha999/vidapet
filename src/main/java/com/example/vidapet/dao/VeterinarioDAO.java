package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VeterinarioDAO {

    private final JdbcTemplate jdbcTemplate;

    public VeterinarioDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();

        map.put("id", rs.getLong("id"));
        map.put("nombre", rs.getString("nombre"));
        map.put("apellido", rs.getString("apellido"));
        map.put("telefono", rs.getString("telefono"));
        map.put("email", rs.getString("correo")); // FIX
        map.put("especialidad", rs.getString("especialidad"));
        map.put("codigo_colegiado", rs.getString("codigo_colegiado"));

        return map;
    };

    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.query("SELECT * FROM veterinario", rowMapper);
    }

    public Map<String, Object> findById(Long id) {
        return jdbcTemplate.queryForMap(
                "SELECT * FROM veterinario WHERE id=?",
                id
        );
    }

    public void save(String n, String a, String t,
                     String e, String esp, String cod) {

        jdbcTemplate.update("""
            INSERT INTO veterinario
            (nombre, apellido, telefono, correo, especialidad, codigo_colegiado)
            VALUES (?, ?, ?, ?, ?, ?)
        """, n, a, t, e, esp, cod);
    }

    public void update(Long id, String n, String a, String t,
                       String e, String esp, String cod) {

        jdbcTemplate.update("""
            UPDATE veterinario
            SET nombre=?, apellido=?, telefono=?, correo=?, especialidad=?, codigo_colegiado=?
            WHERE id=?
        """, n, a, t, e, esp, cod, id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM veterinario WHERE id=?", id);
    }

    public List<Map<String, Object>> search(String s) {

        String sql = """
            SELECT * FROM veterinario
            WHERE LOWER(nombre) LIKE LOWER(?)
               OR LOWER(apellido) LIKE LOWER(?)
               OR LOWER(especialidad) LIKE LOWER(?)
        """;

        String k = "%" + s + "%";
        return jdbcTemplate.query(sql, rowMapper, k, k, k);
    }

}