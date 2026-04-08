package com.example.vidapet.service;

import com.example.vidapet.dao.*;
import org.springframework.stereotype.Service;

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




    /*---------------------------- PROPIETARIOS ----------------------------*/

    public List<Map<String, Object>> listarPropietarios() {
        return propietarioDAO.findAll();
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

    // ==== اصلاح شده: از int edad به LocalDate fechaNacimiento تغییر کرد ====
    public void guardarMascota(String nombre, String especie, String raza, LocalDate fechaNacimiento, Long propietarioId) {
        mascotaDAO.save(nombre, especie, raza, fechaNacimiento, propietarioId);
    }

    public Map<String, Object> obtenerMascotaPorId(Long id) {
        return mascotaDAO.findById(id);
    }

    public void actualizarMascota(Long id, String nombre, String especie, String raza, LocalDate fechaNacimiento, Long propietarioId) {
        mascotaDAO.update(id, nombre, especie, raza, fechaNacimiento, propietarioId);
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

    public void guardarConsulta(int mascotaId, String diagnostico) {
        consultaDAO.save(mascotaId, diagnostico);
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