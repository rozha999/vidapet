package com.example.vidapet.service;

import com.example.vidapet.dao.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VidapetService {

    private final JdbcTemplate jdbcTemplate;
    private final PropietarioDAO propietarioDAO;
    private final MascotaDAO mascotaDAO;
    private final TratamientoDAO tratamientoDAO;
    private final ConsultaDAO consultaDAO;
    private final citaDAO citaDAO;
    private final VeterinarioDAO veterinarioDAO;
    private final EspecieDAO especieDAO;

    public VidapetService(JdbcTemplate jdbcTemplate,
                          PropietarioDAO propietarioDAO,
                          MascotaDAO mascotaDAO,
                          TratamientoDAO tratamientoDAO,
                          ConsultaDAO consultaDAO,
                          citaDAO citaDAO,
                          VeterinarioDAO veterinarioDAO,
                          EspecieDAO especieDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.propietarioDAO = propietarioDAO;
        this.mascotaDAO = mascotaDAO;
        this.tratamientoDAO = tratamientoDAO;
        this.consultaDAO = consultaDAO;
        this.citaDAO = citaDAO;
        this.veterinarioDAO = veterinarioDAO;
        this.especieDAO = especieDAO;
    }

    /*================= CITAS =================*/
    public List<Map<String, Object>> listarCitas(String mascota, String propietario, LocalDate fecha, String orden) {
        return citaDAO.findFiltered(mascota, propietario, fecha, orden);
    }

    public void guardarCita(int mascotaId, int propietarioId, Integer veterinarioId, LocalDateTime fecha, String nota) {
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

    public void actualizarCita(int id, int mascotaId, int propietarioId, LocalDateTime fecha, String nota) {
        citaDAO.update(id, mascotaId, propietarioId, fecha, nota);
    }

    /*==================== CONSULTAS (اصلاح شده) ====================*/

    public int guardarConsultaDesdeCita(int citaId, String diagnostico) {
        // 1. ذخیره در جدول consulta (بدون mascota_id)
        int consultaId = consultaDAO.save(citaId, diagnostico);

        // 2. به‌روزرسانی وضعیت نوبت به 'ATENDIDO' و لینک کردن به معاینه
        jdbcTemplate.update("UPDATE cita SET consulta_id = ?, estado = 'ATENDIDO' WHERE id = ?", consultaId, citaId);

        return consultaId;
    }

    public List<Map<String, Object>> listarConsultas() {
        return consultaDAO.findAll();
    }

    public Map<String, Object> obtenerConsultaPorId(int id) {
        return consultaDAO.findById(id);
    }

    public void actualizarConsulta(int id, String diagnostico) {
        // حذف mascotaId از اینجا چون در دیتابیس دیگر این ستون را نداریم
        consultaDAO.update(id, diagnostico);
    }

    public void eliminarConsulta(int id) {
        consultaDAO.delete(id);
    }

    public List<Map<String, Object>> listarConsultasConTratamientos() {
        return consultaDAO.findAllWithTratamientos();
    }

    public Map<String, Object> obtenerConsultaDetalleCompleto(int id) {
        return consultaDAO.obtenerDetalleCompleto(id);
    }

    public List<Map<String, Object>> buscarConsultasSeguro(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return consultaDAO.findAll();
        }
        return consultaDAO.searchByMascota(nombre);
    }

    public Map<String, Object> obtenerDetalleParaNuevaConsulta(Integer mascotaId, Integer citaId) {
        Map<String, Object> details = new HashMap<>();
        // واکشی اطلاعات حیوان
        details.put("mascota", mascotaDAO.findById(Long.valueOf(mascotaId)));
        // واکشی اطلاعات نوبت
        details.put("cita", citaDAO.findById(citaId));
        return details;
    }

    /*==================== PROPIETARIOS ====================*/
    public List<Map<String, Object>> listarPropietarios(String search) {
        return (search == null || search.isBlank()) ? propietarioDAO.findAll() : propietarioDAO.search(search);
    }

    public void guardarPropietario(String nombre, String apellido, String telefono, String email) {
        propietarioDAO.save(nombre, apellido, telefono, email);
    }

    public Map<String, Object> obtenerPropietarioPorId(Long id) {
        return propietarioDAO.findById(id);
    }

    public void actualizarPropietario(Long id, String nombre, String apellido, String telefono, String email) {
        propietarioDAO.update(id, nombre, apellido, telefono, email);
    }

    public void eliminarPropietario(Long id) {
        propietarioDAO.delete(id);
    }

    /*==================== MASCOTAS ====================*/
    public List<Map<String, Object>> listarMascotas() {
        return mascotaDAO.findAll();
    }

    public void guardarMascota(String nombre, Long especieId, String raza, LocalDate fechaNacimiento, Long propietarioId, String foto) {
        mascotaDAO.save(nombre, especieId, raza, fechaNacimiento, propietarioId, foto);
    }

    public Map<String, Object> obtenerMascotaPorId(Long id) {
        return mascotaDAO.findById(id);
    }

    public void actualizarMascota(Long id, String nombre, Long especieId, String raza, LocalDate fechaNacimiento, Long propietarioId, String foto) {
        mascotaDAO.update(id, nombre, especieId, raza, fechaNacimiento, propietarioId, foto);
    }

    public void eliminarMascota(Long id) {
        mascotaDAO.delete(id);
    }

    public Map<String, Object> obtenerPropietarioPorMascota(Long id) {
        return mascotaDAO.findPropietarioByMascota(id);
    }

    /*==================== TRATAMIENTOS ====================*/
    public void guardarTratamiento(int consultaId, String tratamiento, LocalDate fechaInicio, LocalDate fechaFin, String observaciones) {
        tratamientoDAO.save(consultaId, tratamiento, fechaInicio, fechaFin, observaciones);
    }

    public void eliminarTratamiento(int id) {
        tratamientoDAO.delete(id);
    }

    /*==================== VETERINARIOS ====================*/
    public List<Map<String, Object>> listarVeterinarios(String search) {
        return (search != null && !search.trim().isEmpty()) ? veterinarioDAO.search(search) : veterinarioDAO.findAll();
    }

    public Map<String, Object> obtenerVeterinarioPorId(Long id) {
        return veterinarioDAO.findById(id);
    }

    public void guardarVeterinario(String nombre, String apellido, String telefono, String email, String especialidad, String codigo_colegiado) {
        veterinarioDAO.save(nombre, apellido, telefono, email, especialidad, codigo_colegiado);
    }

    public void actualizarVeterinario(Long id, String nombre, String apellido, String telefono, String email, String especialidad, String codigo_colegiado) {
        veterinarioDAO.update(id, nombre, apellido, telefono, email, especialidad, codigo_colegiado);
    }

    public void eliminarVeterinario(Long id) {
        veterinarioDAO.delete(id);
    }

    /*================= ESPECIES =================*/
    // ================= LISTAR =================
    public List<Map<String, Object>> listarEspecies(String search) {
        if (search != null && !search.trim().isEmpty()) {
            return especieDAO.search(search.trim());
        }
        return especieDAO.findAll();
    }

    // ================= GUARDAR =================
    public void guardarEspecie(String nombre, String foto) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        especieDAO.save(nombre.trim(), foto);
    }

    // ================= BUSCAR POR ID =================
    public Map<String, Object> obtenerPorId(Long id) {
        return especieDAO.findById(id);
    }

    // ================= ACTUALIZAR =================
    public void actualizarEspecie(Long id, String nombre, String foto) {

        if (id == null) {
            throw new IllegalArgumentException("ID obligatorio");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre obligatorio");
        }

        especieDAO.update(id, nombre.trim(), foto);
    }

    // ================= ELIMINAR =================
    public void eliminarEspecie(Long id) {
        especieDAO.delete(id);
    }

}