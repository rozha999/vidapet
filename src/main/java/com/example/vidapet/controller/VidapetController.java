package com.example.vidapet.controller;

import com.example.vidapet.service.VidapetService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalTime;
import java.time.YearMonth;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class VidapetController {

    private final VidapetService vidapetService;

    public VidapetController(VidapetService vidapetService) {
        this.vidapetService = vidapetService;
    }

    // ===== Página principal =====
    @GetMapping
    public String index() {
        return "index"; // index.html
    }

    /*---------------------------- PROPIETARIOS ----------------------------*/

    @GetMapping("/propietarios")
    public String listarPropietarios(Model model) {
        model.addAttribute("propietarios", vidapetService.listarPropietarios());
        return "propietarios";
    }

    @GetMapping("/propietarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        Map<String, Object> propietario = new HashMap<>();
        propietario.put("id", "");
        propietario.put("nombre", "");
        propietario.put("apellido", "");
        propietario.put("telefono", "");
        propietario.put("email", "");
        model.addAttribute("propietario", propietario);
        return "propietario_form";
    }

    @PostMapping("/propietarios/guardar")
    public String guardarPropietario(@RequestParam String nombre,
                                     @RequestParam String apellido,
                                     @RequestParam String telefono,
                                     @RequestParam String email) {
        vidapetService.guardarPropietario(nombre, apellido, telefono, email);
        return "redirect:/propietarios";
    }

    @GetMapping("/propietarios/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Map<String, Object> propietario = vidapetService.obtenerPropietarioPorId(id);
        model.addAttribute("propietario", propietario);
        return "propietario_form";
    }

    @PostMapping("/propietarios/actualizar")
    public String actualizarPropietario(@RequestParam Long id,
                                        @RequestParam String nombre,
                                        @RequestParam String apellido,
                                        @RequestParam String telefono,
                                        @RequestParam String email) {
        vidapetService.actualizarPropietario(id, nombre, apellido, telefono, email);
        return "redirect:/propietarios";
    }

    @GetMapping("/propietarios/eliminar/{id}")
    public String eliminarPropietario(@PathVariable Long id) {
        vidapetService.eliminarPropietario(id);
        return "redirect:/propietarios";
    }

    /*------------------------------- MASCOTAS ----------------------------------*/

    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "mascotas";
    }

    @GetMapping("/mascotas/nuevo")
    public String nuevaMascota(Model model) {
        Map<String, Object> mascota = new HashMap<>();
        mascota.put("id", "");
        mascota.put("nombre", "");
        mascota.put("especie", "");
        mascota.put("raza", "");
        mascota.put("fecha_nacimiento", ""); // ← اصلاح شد
        mascota.put("propietario_id", "");

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios());
        return "mascota_form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@RequestParam String nombre,
                                 @RequestParam String especie,
                                 @RequestParam String raza,
                                 @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
                                 @RequestParam Long propietario_id) {

        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
        vidapetService.guardarMascota(nombre, especie, raza, fechaNacimiento, propietario_id);
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        Map<String, Object> mascota = vidapetService.obtenerMascotaPorId(id);
        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios());
        return "mascota_form";
    }

    @PostMapping("/mascotas/actualizar")
    public String actualizarMascota(@RequestParam Long id,
                                    @RequestParam String nombre,
                                    @RequestParam String especie,
                                    @RequestParam String raza,
                                    @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
                                    @RequestParam Long propietario_id) {

        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);
        vidapetService.actualizarMascota(id, nombre, especie, raza, fechaNacimiento, propietario_id);
        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id) {
        vidapetService.eliminarMascota(id);
        return "redirect:/mascotas";
    }

    /*---------------------------- TRATAMIENTOS ----------------------------*/

    // Listar todos los tratamientos
    @GetMapping("/tratamientos")
    public String listarTratamientos(Model model) {
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());
        return "tratamientos";
    }

    // Formulario para crear un nuevo tratamiento
    @GetMapping("/tratamientos/nuevo")
    public String nuevoTratamiento(Model model) {
        Map<String, Object> tratamiento = new HashMap<>();
        tratamiento.put("id", "");
        tratamiento.put("nombre", "");
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento_form";
    }

    // Guardar un nuevo tratamiento
    @PostMapping("/tratamientos/guardar")
    public String guardarTratamiento(@RequestParam String nombre) {
        vidapetService.guardarTratamiento(nombre);
        return "redirect:/tratamientos";
    }

    // Formulario para editar tratamiento existente
    @GetMapping("/tratamientos/editar/{id}")
    public String editarTratamiento(@PathVariable Long id, Model model) {
        Map<String, Object> tratamiento = vidapetService.obtenerTratamientoPorId(id);
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento_form";
    }

    // Actualizar tratamiento
    @PostMapping("/tratamientos/actualizar")
    public String actualizarTratamiento(@RequestParam Long id,
                                        @RequestParam String nombre) {
        vidapetService.actualizarTratamiento(id, nombre);
        return "redirect:/tratamientos";
    }

    // Eliminar tratamiento
    @GetMapping("/tratamientos/eliminar/{id}")
    public String eliminarTratamiento(@PathVariable Long id) {
        vidapetService.eliminarTratamiento(id);
        return "redirect:/tratamientos";
    }

    /*---------------------------- HISTORIAL MÉDICO ----------------------------*/

    // Listar todos los historiales
    @GetMapping("/historial")
    public String listarHistoriales(Model model) {
        model.addAttribute("historiales", vidapetService.listarHistoriales());
        return "historiales";
    }

    // Formulario para crear nuevo historial
    @GetMapping("/historial/nuevo")
    public String nuevoHistorial(Model model) {
        Map<String, Object> historial = new HashMap<>();
        historial.put("id", "");
        historial.put("mascota_id", "");
        historial.put("fecha", "");
        historial.put("problema", "");
        historial.put("tratamiento_id", "");
        historial.put("nombre_tratamiento", "");
        historial.put("notas", "");

        model.addAttribute("historial", historial);
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());
        return "historial_form";
    }

    // Guardar historial
    @PostMapping("/historial/guardar")
    public String guardarHistorial(@RequestParam int mascota_id,
                                   @RequestParam String fecha,
                                   @RequestParam String problema,
                                   @RequestParam(required = false) Integer tratamiento_id,
                                   @RequestParam String nombre_tratamiento,
                                   @RequestParam String notas) {

        vidapetService.guardarHistorial(mascota_id, fecha, problema, tratamiento_id, nombre_tratamiento, notas);
        return "redirect:/historial";
    }

    // Formulario para editar historial
    @GetMapping("/historial/editar/{id}")
    public String editarHistorial(@PathVariable int id, Model model) {
        Map<String, Object> historial = vidapetService.obtenerHistorialPorId(id);
        model.addAttribute("historial", historial);
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());
        return "historial_form";
    }

    // Actualizar historial
    @PostMapping("/historial/actualizar")
    public String actualizarHistorial(@RequestParam int id,
                                      @RequestParam int mascota_id,
                                      @RequestParam String fecha,
                                      @RequestParam String problema,
                                      @RequestParam(required = false) Integer tratamiento_id,
                                      @RequestParam String nombre_tratamiento,
                                      @RequestParam String notas) {

        vidapetService.actualizarHistorial(id, mascota_id, fecha, problema, tratamiento_id, nombre_tratamiento, notas);
        return "redirect:/historial";
    }

    // Eliminar historial
    @GetMapping("/historial/eliminar/{id}")
    public String eliminarHistorial(@PathVariable int id) {
        vidapetService.eliminarHistorial(id);
        return "redirect:/historial";
    }

    /*================= CITAS =================*/
    /*================= CITAS =================*/

    @GetMapping("/citas")
    public String listarCitas(Model model) {
        model.addAttribute("citas", vidapetService.listarCitas());
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "citas"; // نمایش لیست نوبت‌ها
    }

    @GetMapping("/citas/nuevo")
    public String nuevoCita(Model model) {
        Map<String,Object> cita = new HashMap<>();
        cita.put("id", "");
        cita.put("mascota_id", "");
        cita.put("fecha", "");
        cita.put("hora", "");
        cita.put("notas", "");

        model.addAttribute("cita", cita);
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "cita_form"; // فرم رزرو نوبت
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(@RequestParam Long mascota_id,
                              @RequestParam String fecha,
                              @RequestParam String hora,
                              @RequestParam String notas) {
        vidapetService.guardarCita(mascota_id, fecha, hora, notas);
        return "redirect:/citas";
    }

    @GetMapping("/citas/editar/{id}")
    public String editarCita(@PathVariable Long id, Model model) {
        Map<String,Object> cita = vidapetService.obtenerCitaPorId(id);
        model.addAttribute("cita", cita);
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "cita_form";
    }

    @PostMapping("/citas/actualizar")
    public String actualizarCita(@RequestParam Long id,
                                 @RequestParam Long mascota_id,
                                 @RequestParam String fecha,
                                 @RequestParam String hora,
                                 @RequestParam String notas) {
        vidapetService.actualizarCita(id, mascota_id, fecha, hora, notas);
        return "redirect:/citas";
    }

    @GetMapping("/citas/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id) {
        vidapetService.eliminarCita(id);
        return "redirect:/citas";
    }

    /*================= CALENDARIO =================*/
    @GetMapping("/citas/calendar")
    public String verCalendario(Model model) {
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "citas_calendar";
    }

    /*================= DATOS JSON PARA FULLCALENDAR =================*/
    @GetMapping("/citas/calendar-data")
    @ResponseBody
    public List<Map<String,Object>> getCitasJson() {
        return vidapetService.listarCitasParaCalendar();
    }

    /*================= RESERVAR CITA AJAX =================*/
    @PostMapping("/citas/reserve")
    @ResponseBody
    public String reservarCita(@RequestParam Long mascota_id,
                               @RequestParam String fecha,
                               @RequestParam String hora,
                               @RequestParam(required = false) String notas) {
        vidapetService.guardarCita(mascota_id, fecha, hora, notas);
        return "OK";
    }

    /*================= OBTENER HORAS LIBRES =================*/
    @GetMapping("/citas/horas-libres")
    @ResponseBody
    public List<String> horasLibres(@RequestParam String fecha) {
        return vidapetService.obtenerHorasLibres(fecha);
    }

    /*================= ELIMINAR CITA AJAX =================*/
    @PostMapping("/citas/delete")
    @ResponseBody
    public String eliminarCitaAjax(@RequestParam Long citaId) {
        vidapetService.eliminarCita(citaId);
        return "OK";
    }
}
