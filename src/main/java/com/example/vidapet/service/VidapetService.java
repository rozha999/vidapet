package com.example.vidapet.service;

import com.example.vidapet.dao.*;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class VidapetService {

    private final PropietarioDAO propietarioDAO;
    private final MascotaDAO mascotaDAO;
    private final TratamientoDAO tratamientoDAO;
    private final HistorialMedicoDAO historialMedicoDAO;
    private final citaDAO citaDAO;

    // Constructor con todos los DAO
    public VidapetService(PropietarioDAO propietarioDAO,
                          MascotaDAO mascotaDAO,
                          TratamientoDAO tratamientoDAO,
                          HistorialMedicoDAO historialMedicoDAO,
                          citaDAO citaDAO) {  // اضافه شد
        this.propietarioDAO = propietarioDAO;
        this.mascotaDAO = mascotaDAO;
        this.tratamientoDAO = tratamientoDAO;
        this.historialMedicoDAO = historialMedicoDAO;
        this.citaDAO = citaDAO;  // اضافه شد
    }

    /*================= CITAS =================*/
    /*================= CITAS =================*/
    public List<Map<String,Object>> listarCitas() {
        return citaDAO.findAll();
    }

    public Map<String,Object> obtenerCitaPorId(Long id) {
        return citaDAO.findById(id);
    }

    public void guardarCita(Long mascotaId, String fechaStr, String horaStr, String notas) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalTime hora = LocalTime.parse(horaStr);
        citaDAO.save(mascotaId, fecha, hora, notas);
    }

    public void actualizarCita(Long id, Long mascotaId, String fechaStr, String horaStr, String notas) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalTime hora = LocalTime.parse(horaStr);
        citaDAO.update(id, mascotaId, fecha, hora, notas);
    }

    public void eliminarCita(Long id) {
        citaDAO.delete(id);
    }

    /*================= FULLCALENDAR =================*/
    public List<Map<String,Object>> listarCitasParaCalendar() {
        List<Map<String,Object>> citas = citaDAO.findAll();
        // برای هر نوبت، می‌توانیم یک عنوان مناسب بسازیم
        for (Map<String,Object> c : citas) {
            String titulo = c.get("mascota_nombre") + " (" + c.get("mascota_propietario") + ")";
            c.put("title", titulo);
            c.put("start", c.get("fecha") + "T" + c.get("hora"));
        }
        return citas;
    }

    /*================= HORAS LIBRES =================*/
    public List<String> obtenerHorasLibres(String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        List<String> horasOcupadas = citaDAO.findHorasByFecha(fecha);
        // فرض کنیم ساعات کاری کلینیک 09:00 تا 17:00 هر نیم ساعت یک نوبت
        List<String> todasHoras = List.of(
                "09:00","09:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30",
                "15:00","15:30","16:00","16:30"
        );
        // حذف ساعت‌های پر شده
        return todasHoras.stream()
                .filter(h -> !horasOcupadas.contains(h))
                .toList();
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

    // Listar todos los tratamientos
    public List<Map<String, Object>> listarTratamientos() {
        return tratamientoDAO.findAll();
    }

    // Guardar un nuevo tratamiento
    public void guardarTratamiento(String nombre) {
        tratamientoDAO.save(nombre);
    }

    // Obtener un tratamiento por ID
    public Map<String, Object> obtenerTratamientoPorId(Long id) {
        return tratamientoDAO.findById(id);
    }

    // Actualizar un tratamiento existente
    public void actualizarTratamiento(Long id, String nombre) {
        tratamientoDAO.update(id, nombre);
    }

    // Eliminar un tratamiento por ID
    public void eliminarTratamiento(Long id) {
        tratamientoDAO.delete(id);
    }



    /*---------------------------- HISTORIAL MÉDICO ----------------------------*/

    // Listar todos los historiales
    public List<Map<String, Object>> listarHistoriales() {
        return historialMedicoDAO.findAll();
    }

    // Listar historial por mascota
    public List<Map<String, Object>> listarHistorialPorMascota(int mascotaId) {
        return historialMedicoDAO.findByMascotaId(mascotaId);
    }

    // Obtener historial por ID
    public Map<String, Object> obtenerHistorialPorId(int id) {
        return historialMedicoDAO.findById(id);
    }

    // Guardar nuevo historial
    public void guardarHistorial(int mascotaId, String fecha, String problema, Integer tratamientoId, String nombreTratamiento, String notas) {
        historialMedicoDAO.save(mascotaId, fecha, problema, tratamientoId, nombreTratamiento, notas);
    }

    // Actualizar historial existente
    public void actualizarHistorial(int id, int mascotaId, String fecha, String problema, Integer tratamientoId, String nombreTratamiento, String notas) {
        historialMedicoDAO.update(id, mascotaId, fecha, problema, tratamientoId, nombreTratamiento, notas);
    }

    // Eliminar historial
    public void eliminarHistorial(int id) {
        historialMedicoDAO.delete(id);
    }
}