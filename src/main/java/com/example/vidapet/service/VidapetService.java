package com.example.vidapet.service;

import com.example.vidapet.dao.PropietarioDAO;
import com.example.vidapet.dao.MascotaDAO;
import com.example.vidapet.dao.TratamientoDAO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class VidapetService {

    private final PropietarioDAO propietarioDAO;
    private final MascotaDAO mascotaDAO;
    private final TratamientoDAO tratamientoDAO;

    public VidapetService(PropietarioDAO propietarioDAO,
                          MascotaDAO mascotaDAO,
                          TratamientoDAO tratamientoDAO) {

        this.propietarioDAO = propietarioDAO;
        this.mascotaDAO = mascotaDAO;
        this.tratamientoDAO = tratamientoDAO;
    }

    /*---------------------------- PROPIETARIOS ----------------------------*/

    // ===== Listar todos los propietarios =====
    public List<Map<String,Object>> listarPropietarios() {
        return propietarioDAO.findAll();
    }

    // ===== Guardar nuevo propietario =====
    public void guardarPropietario(String nombre, String apellido, String telefono, String email) {
        propietarioDAO.save(nombre, apellido, telefono, email);
    }

    // ===== Actualizar propietario existente =====
    public void actualizarPropietario(Long id, String nombre, String apellido, String telefono, String email) {
        propietarioDAO.update(id, nombre, apellido, telefono, email);
    }

    // ===== Eliminar propietario =====
    public void eliminarPropietario(Long id) {
        propietarioDAO.delete(id);
    }

    // ===== Obtener propietario por ID =====
    public Map<String,Object> obtenerPropietarioPorId(Long id) {
        return propietarioDAO.findById(id);
    }

    /*---------------------------- MASCOTAS ----------------------------*/

    // ===== Listar todas las mascotas =====
    public List<Map<String,Object>> listarMascotas() {
        return mascotaDAO.findAll();
    }

    // ===== Guardar nueva mascota =====
    public void guardarMascota(String nombre, String especie, String raza, int edad, Long propietarioId) {
        mascotaDAO.save(nombre, especie, raza, edad, propietarioId);
    }

    // ===== Obtener mascota por ID =====
    public Map<String,Object> obtenerMascotaPorId(Long id) {
        return mascotaDAO.findById(id);
    }

    // ===== Actualizar mascota existente =====
    public void actualizarMascota(Long id, String nombre, String especie, String raza, int edad, Long propietarioId) {
        mascotaDAO.update(id, nombre, especie, raza, edad, propietarioId);
    }

    // ===== Eliminar mascota =====
    public void eliminarMascota(Long id) {
        mascotaDAO.delete(id);
    }

    /*---------------------------- TRATAMIENTOS ----------------------------*/

    // ===== Listar todos los tratamientos =====
    public List<Map<String,Object>> listarTratamientos() {
        return tratamientoDAO.findAll();
    }

    // ===== Guardar nuevo tratamiento =====
    public void guardarTratamiento(String tipo, String duracion, Long consultaId) {
        tratamientoDAO.save(tipo, duracion, consultaId);
    }

    // ===== Obtener tratamiento por ID =====
    public Map<String,Object> obtenerTratamientoPorId(Long id) {
        return tratamientoDAO.findById(id);
    }

    // ===== Actualizar tratamiento existente =====
    public void actualizarTratamiento(Long id, String tipo, String duracion, Long consultaId) {
        tratamientoDAO.update(id, tipo, duracion, consultaId);
    }

    // ===== Eliminar tratamiento =====
    public void eliminarTratamiento(Long id) {
        tratamientoDAO.delete(id);
    }
}