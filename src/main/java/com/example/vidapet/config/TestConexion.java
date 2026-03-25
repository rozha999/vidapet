package com.example.vidapet.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TestConexion {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void probarConexion() {
        String sql = "SELECT 1";
        Integer resultado = jdbcTemplate.queryForObject(sql, Integer.class);

        System.out.println("Conexión OK: " + resultado);
    }
}