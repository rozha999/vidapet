package com.example.vidapet.service;

import com.example.vidapet.dao.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import java.time.LocalDate;

@Service
public class VidapetService {

    private final PropietarioDAO propietarioDAO;
    private final MascotaDAO mascotaDAO;
    private final TratamientoDAO tratamientoDAO;
    private final ConsultaDAO consultaDAO;
    private final citaDAO citaDAO;

    // Constructor con todos los DAO
    public VidapetService(PropietarioDAO propietarioDAO,
                          MascotaDAO mascotaDAO,
                          TratamientoDAO tratamientoDAO,
                          ConsultaDAO consultaDAO,
                          citaDAO citaDAO) {  // اضافه شد
        this.propietarioDAO = propietarioDAO;
        this.mascotaDAO = mascotaDAO;
        this.tratamientoDAO = tratamientoDAO;
        this.consultaDAO = consultaDAO;
        this.citaDAO = citaDAO;  // اضافه شد
    }

    /*================= CITAS =================*/
    // CITAS

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
                            LocalDateTime fecha,
                            String nota) {
        citaDAO.save(mascotaId, propietarioId, fecha, nota);
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
        return consultaDAO.saveConCita(citaId, mascotaId, diagnostico);
    }
    public Map<String, Object> obtenerConsultaCompleta(int id) {
        return consultaDAO.findConsultaFull(id);
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

    public int guardarConsulta(int mascotaId, String diagnostico) {
        return consultaDAO.save(mascotaId, diagnostico);
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
}