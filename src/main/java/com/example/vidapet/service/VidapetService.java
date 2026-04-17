package com.example.vidapet.service;

import com.example.vidapet.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import java.time.LocalDate;

@Service
public class VidapetService {

    private final JdbcTemplate jdbcTemplate;

    private final PropietarioDAO propietarioDAO;
    private final MascotaDAO mascotaDAO;
    private final TratamientoDAO tratamientoDAO;
    private final ConsultaDAO consultaDAO;
    private final citaDAO citaDAO;
    private final VeterinarioDAO veterinarioDAO;

    public VidapetService(JdbcTemplate jdbcTemplate,
                          PropietarioDAO propietarioDAO,
                          MascotaDAO mascotaDAO,
                          TratamientoDAO tratamientoDAO,
                          ConsultaDAO consultaDAO,
                          citaDAO citaDAO,
                          VeterinarioDAO veterinarioDAO) {

        this.jdbcTemplate = jdbcTemplate;
        this.propietarioDAO = propietarioDAO;
        this.mascotaDAO = mascotaDAO;
        this.tratamientoDAO = tratamientoDAO;
        this.consultaDAO = consultaDAO;
        this.citaDAO = citaDAO;
        this.veterinarioDAO = veterinarioDAO;
    }

    /*================= CITAS =================*/
    // CITAS
    public Map<String, Object> obtenerVeterinarioPorCita(int citaId) {
        String sql = """
        SELECT v.nombre, v.apellido
        FROM cita c
        JOIN veterinario v ON c.veterinario_id = v.id
        WHERE c.id = ?
    """;

        return jdbcTemplate.queryForMap(sql, citaId);
    }
    public List<Map<String, Object>> listarCitas(
            String mascota,
            String propietario,
            LocalDate fecha,
            String orden
    ) {
        return citaDAO.findFiltered(mascota, propietario, fecha, orden);
    }

    public void guardarCita(int mascotaId,
                            int propietarioId,
                            Integer veterinarioId,
                            LocalDateTime fecha,
                            String nota) {

        citaDAO.save(mascotaId, propietarioId, veterinarioId, fecha, nota);
    }

    public Map<String, Object> obtenerCita(int id) {
        return citaDAO.findById(id);
    }

    public void eliminarCita(int id) {
        citaDAO.delete(id);
    }

    public void cambiarEstadoCita(int id, String estado) {
        citaDAO.updateEstado(id, estado);
    }
    public int guardarConsultaDesdeCita(int citaId, int mascotaId, String diagnostico) {

        int consultaId = consultaDAO.saveConCita(citaId, mascotaId, diagnostico);

        jdbcTemplate.update(
                "UPDATE cita SET consulta_id = ?, estado = 'ATENDIDO' WHERE id = ?",
                consultaId, citaId
        );

        return consultaId;
    }
    public Map<String, Object> obtenerConsultaCompleta(int id) {
        return consultaDAO.findConsultaFull(id);
    }
    public void actualizarCita(int id, int mascotaId, int propietarioId, LocalDateTime fecha, String nota) {
        citaDAO.update(id, mascotaId, propietarioId, fecha, nota);
    }
    public void marcarCitaConConsulta(int citaId, int consultaId) {
        jdbcTemplate.update(
                "UPDATE cita SET consulta_id = ?, estado = 'ATENDIDO' WHERE id = ?",
                consultaId, citaId
        );
    }
    public Map<String, Object> obtenerPropietarioPorMascota(Long id) {
        return mascotaDAO.findPropietarioByMascota(id);
    }
    /*---------------------------- PROPIETARIOS ----------------------------*/

    public List<Map<String,Object>> listarPropietarios(String search) {
        if (search == null || search.isBlank()) {
            return propietarioDAO.findAll();
        }
        return propietarioDAO.search(search);
    }

    public void guardarPropietario(String nombre, String apellido, String telefono, String email) {
        propietarioDAO.save(nombre, apellido, telefono, email);
    }

    public void actualizarPropietario(Long id, String nombre, String apellido, String telefono, String email) {
        propietarioDAO.update(id, nombre, apellido, telefono, email);
    }

    public void eliminarPropietario(Long id) {
        propietarioDAO.delete(id);
    }

    public Map<String, Object> obtenerPropietarioPorId(Long id) {
        return propietarioDAO.findById(id);
    }

    /*---------------------------- MASCOTAS ----------------------------*/

    public List<Map<String, Object>> listarMascotas() {
        return mascotaDAO.findAll();
    }

    public void guardarMascota(String nombre, String especie, String raza,
                               LocalDate fechaNacimiento, Long propietarioId, String foto) {
        mascotaDAO.save(nombre, especie, raza, fechaNacimiento, propietarioId, foto);
    }

    public void actualizarMascota(Long id, String nombre, String especie, String raza,
                                  LocalDate fechaNacimiento, Long propietarioId, String foto) {
        mascotaDAO.update(id, nombre, especie, raza, fechaNacimiento, propietarioId, foto);
    }
    public Map<String, Object> obtenerMascotaPorId(Long id) {
        return mascotaDAO.findById(id);
    }


    public void eliminarMascota(Long id) {
        mascotaDAO.delete(id);
    }


    /*==================== CONSULTAS ====================*/

    public List<Map<String, Object>> listarConsultas() {
        return consultaDAO.findAll();
    }

    public Map<String, Object> obtenerConsultaPorId(int id) {
        return consultaDAO.findConsultaWithTratamientos(id);
    }

    public void actualizarConsulta(int id, int mascotaId, String diagnostico) {
        consultaDAO.update(id, mascotaId, diagnostico);
    }

    public void eliminarConsulta(int id) {
        consultaDAO.delete(id);
    }

    public List<Map<String, Object>> listarConsultasConTratamientos() {
        return consultaDAO.findAllWithTratamientos();
    }

    /*==================== TRATAMIENTOS ====================*/

    public List<Map<String, Object>> listarTratamientos() {
        return tratamientoDAO.findAll();
    }

    public void guardarTratamiento(int consultaId, String tratamiento, LocalDate fechaInicio,
                                   LocalDate fechaFin, String observaciones) {
        tratamientoDAO.save(consultaId, tratamiento, fechaInicio, fechaFin, observaciones);
    }

    public Map<String, Object> obtenerTratamientoPorId(int id) {
        return tratamientoDAO.findById(id);
    }

    public void actualizarTratamiento(int id, int consultaId, String tratamiento, LocalDate fechaInicio,
                                      LocalDate fechaFin, String observaciones) {
        tratamientoDAO.update(id, consultaId, tratamiento, fechaInicio, fechaFin, observaciones);
    }

    public void eliminarTratamiento(int id) {
        tratamientoDAO.delete(id);
    }

    public List<Map<String, Object>> buscarConsultas(String nombreMascota) {
        return consultaDAO.searchByMascota(nombreMascota);
    }

    /* ================= VETERINARIOS ================= */

        // 📌 لیست + جستجو
        public List<Map<String, Object>> listarVeterinarios(String search) {
            if (search != null && !search.trim().isEmpty()) {
                return veterinarioDAO.search(search);
            }
            return veterinarioDAO.findAll();
        }

        // 📌 گرفتن یک دامپزشک با id
        public Map<String, Object> obtenerVeterinarioPorId(Long id) {
            return veterinarioDAO.findById(id);
        }

        // 📌 ذخیره
        public void guardarVeterinario(String nombre,
                                       String apellido,
                                       String telefono,
                                       String email,
                                       String especialidad,
                                       String codigo_colegiado) {

            veterinarioDAO.save(
                    nombre,
                    apellido,
                    telefono,
                    email,
                    especialidad,
                    codigo_colegiado
            );
        }

        // 📌 آپدیت
        public void actualizarVeterinario(Long id,
                                          String nombre,
                                          String apellido,
                                          String telefono,
                                          String email,
                                          String especialidad,
                                          String codigo_colegiado) {

            veterinarioDAO.update(
                    id,
                    nombre,
                    apellido,
                    telefono,
                    email,
                    especialidad,
                    codigo_colegiado
            );
        }

        // 📌 حذف
        public void eliminarVeterinario(Long id) {
            veterinarioDAO.delete(id);
        }
    }




