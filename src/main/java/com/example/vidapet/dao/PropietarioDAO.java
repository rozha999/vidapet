package com.example.vidapet.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PropietarioDAO {

    private final JdbcTemplate jdbcTemplate;

    public PropietarioDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== RowMapper para mapear resultados de la tabla 'propietario' =====
    private RowMapper<Map<String, Object>> rowMapper = (rs, rowNum) -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", rs.getLong("id"));
        map.put("nombre", rs.getString("nombre"));
        map.put("apellido", rs.getString("apellido"));
        map.put("telefono", rs.getString("telefono")); // puede ser null
        map.put("email", rs.getString("email"));       // puede ser null
        return map;
    };

    /*---------------------------- MÉTODOS CRUD ----------------------------*/

    // ===== Listar todos los propietarios =====
    public List<Map<String, Object>> findAll() {
        return jdbcTemplate.query("SELECT * FROM propietario", rowMapper);
    }

    // ===== Obtener un propietario por ID =====
    public Map<String, Object> findById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM propietario WHERE id=?",
                new Object[]{id},
                rowMapper
        );
    }

    // ===== Guardar un nuevo propietario =====
    public void save(String nombre, String apellido, String telefono, String email) {
        jdbcTemplate.update(
                "INSERT INTO propietario(nombre, apellido, telefono, email) VALUES (?, ?, ?, ?)",
                nombre, apellido, telefono, email
        );
    }

    // ===== Actualizar un propietario existente =====
    public void update(Long id, String nombre, String apellido, String telefono, String email) {
        jdbcTemplate.update(
                "UPDATE propietario SET nombre=?, apellido=?, telefono=?, email=? WHERE id=?",
                nombre, apellido, telefono, email, id
        );
    }

    // ===== Eliminar un propietario por ID =====
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM propietario WHERE id=?", id);
    }

    public List<Map<String,Object>> search(String search) {

        String sql = """
        SELECT * FROM propietario
        WHERE LOWER(nombre) LIKE LOWER(?)
           OR LOWER(apellido) LIKE LOWER(?)
    """;

        String keyword = "%" + search + "%";

        return jdbcTemplate.query(sql, rowMapper, keyword, keyword);
    }
}