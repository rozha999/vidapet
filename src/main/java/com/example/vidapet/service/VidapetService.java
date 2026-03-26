package com.example.vidapet.service;

import com.example.vidapet.dao.PropietarioDAO;
import com.example.vidapet.dao.DetalleTratamientoDAO;
import com.example.vidapet.dao.MascotaDAO;
import com.example.vidapet.dao.TratamientoDAO;
import com.example.vidapet.dao.ConsultaDAO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class VidapetService {

    private final PropietarioDAO propietarioDAO;
    private final MascotaDAO mascotaDAO;
    private final TratamientoDAO tratamientoDAO;
    private final ConsultaDAO consultaDAO;
    private final DetalleTratamientoDAO detalleTratamientoDAO;

    public VidapetService(PropietarioDAO propietarioDAO,
                          MascotaDAO mascotaDAO,
                          TratamientoDAO tratamientoDAO,
                          ConsultaDAO consultaDAO,
                          DetalleTratamientoDAO detalleTratamientoDAO) {

        this.propietarioDAO = propietarioDAO;
        this.mascotaDAO = mascotaDAO;
        this.tratamientoDAO = tratamientoDAO;
        this.consultaDAO = consultaDAO;
        this.detalleTratamientoDAO = detalleTratamientoDAO;
    }

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

    /*---------------------------- TRATAMIENTOS ----------------------------*/

    public List<Map<String, Object>> listarTratamientos() {
        return tratamientoDAO.findAll();
    }

    public void guardarTratamiento(String tipo, String duracion, Long consultaId) {
        tratamientoDAO.save(tipo, duracion, consultaId);
    }

    public Map<String, Object> obtenerTratamientoPorId(Long id) {
        return tratamientoDAO.findById(id);
    }

    public void actualizarTratamiento(Long id, String tipo, String duracion, Long consultaId) {
        tratamientoDAO.update(id, tipo, duracion, consultaId);
    }

    public void eliminarTratamiento(Long id) {
        tratamientoDAO.delete(id);
    }

    /*---------------------------- CONSULTAS ----------------------------*/

    public List<Map<String, Object>> listarConsultas() {
        return consultaDAO.findAll();
    }

    public void guardarConsulta(LocalDateTime fecha, String motivo, String diagnostico, Long mascotaId) {
        consultaDAO.save(fecha, motivo, diagnostico, mascotaId);
    }

    public Map<String, Object> obtenerConsultaPorId(Long id) {
        return consultaDAO.findById(id);
    }

    public void actualizarConsulta(Long id, LocalDateTime fecha, String motivo, String diagnostico, Long mascotaId) {
        consultaDAO.update(id, fecha, motivo, diagnostico, mascotaId);
    }

    public void eliminarConsulta(Long id) {
        consultaDAO.delete(id);
    }

    /*---------------------------- DETALLE_TRATAMIENTO ----------------------------*/

    public List<Map<String, Object>> listarDetalles() {
        return detalleTratamientoDAO.findAll();
    }

    public void guardarDetalle(Long consultaId, Long tratamientoId) {
        detalleTratamientoDAO.save(consultaId, tratamientoId);
    }

    public Map<String, Object> obtenerDetallePorId(Long id) {
        return detalleTratamientoDAO.findById(id);
    }

    public void actualizarDetalle(Long id, Long consultaId, Long tratamientoId) {
        detalleTratamientoDAO.update(id, consultaId, tratamientoId);
    }

    public void eliminarDetalle(Long id) {
        detalleTratamientoDAO.delete(id);
    }
}