package com.example.vidapet.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DetalleTratamientoDAO {

    private final JdbcTemplate jdbcTemplate;

    public DetalleTratamientoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== RowMapper برای map کردن نتایج جدول =====
    private final RowMapper<Map<String,Object>> rowMapper = (rs, rowNum) -> {
        Map<String,Object> map = new HashMap<>();
        map.put("id", rs.getLong("id"));
        map.put("consulta_id", rs.getLong("consulta_id"));
        map.put("tratamiento_id", rs.getLong("tratamiento_id"));
        return map;
    };
    /*---------------------------- MÉTODOS CRUD ----------------------------*/

    // ===== لیست همه رکوردها =====
    public List<Map<String,Object>> findAll() {
        return jdbcTemplate.query("SELECT * FROM detalle_tratamiento", rowMapper);
    }

    // ===== دریافت یک رکورد بر اساس ID =====
    public Map<String,Object> findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM detalle_tratamiento WHERE id=?",
                    new Object[]{id},
                    rowMapper
            );
        } catch (EmptyResultDataAccessException e) {
            return null; // اگر پیدا نشد null برگردان
        }
    }

    // ===== اضافه کردن رکورد جدید =====
    public void save(Long consultaId, Long tratamientoId) {
        jdbcTemplate.update(
                "INSERT INTO detalle_tratamiento(consulta_id, tratamiento_id) VALUES (?, ?)",
                consultaId, tratamientoId
        );
    }

    // ===== بروزرسانی یک رکورد =====
    public void update(Long id, Long consultaId, Long tratamientoId) {
        jdbcTemplate.update(
                "UPDATE detalle_tratamiento SET consulta_id=?, tratamiento_id=? WHERE id=?",
                consultaId, tratamientoId, id
        );
    }

    // ===== حذف یک رکورد =====
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM detalle_tratamiento WHERE id=?", id);
    }
}