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
import java.time.LocalDate;

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

    @GetMapping("/tratamientos")
    public String listarTratamientos(Model model) {
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());
        return "tratamientos";
    }

    @GetMapping("/tratamientos/nuevo")
    public String nuevoTratamiento(Model model) {
        Map<String, Object> tratamiento = new HashMap<>();
        tratamiento.put("id", "");
        tratamiento.put("consulta_id", "");
        tratamiento.put("tratamiento", "");
        tratamiento.put("fecha_inicio", "");
        tratamiento.put("fecha_fin", "");
        tratamiento.put("observaciones", "");

        model.addAttribute("tratamiento", tratamiento);
        model.addAttribute("consultas", vidapetService.listarConsultas());
        return "tratamiento_form";
    }

    @PostMapping("/tratamientos/guardar")
    public String guardarTratamiento(@RequestParam int consulta_id,
                                     @RequestParam String tratamiento,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_inicio,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_fin,
                                     @RequestParam(required = false) String observaciones) {
        vidapetService.guardarTratamiento(consulta_id, tratamiento, fecha_inicio, fecha_fin, observaciones);
        return "redirect:/tratamientos";
    }

    @GetMapping("/tratamientos/editar/{id}")
    public String editarTratamiento(@PathVariable int id, Model model) {
        Map<String, Object> tratamiento = vidapetService.obtenerTratamientoPorId(id);
        model.addAttribute("tratamiento", tratamiento);
        model.addAttribute("consultas", vidapetService.listarConsultas());
        return "tratamiento_form";
    }

    @PostMapping("/tratamientos/actualizar")
    public String actualizarTratamiento(@RequestParam int id,
                                        @RequestParam int consulta_id,
                                        @RequestParam String tratamiento,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_inicio,
                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_fin,
                                        @RequestParam(required = false) String observaciones) {
        vidapetService.actualizarTratamiento(id, consulta_id, tratamiento, fecha_inicio, fecha_fin, observaciones);
        return "redirect:/tratamientos";
    }

    @GetMapping("/tratamientos/eliminar/{id}")
    public String eliminarTratamiento(@PathVariable int id) {
        vidapetService.eliminarTratamiento(id);
        return "redirect:/tratamientos";
    }

    /*==================== CONSULTAS ====================*/

    @GetMapping("/consulta")
    public String listarConsultas(Model model) {
        List<Map<String, Object>> consultas = vidapetService.listarConsultasConTratamientos();
        model.addAttribute("consultas", consultas);
        return "consultas";
    }

    @GetMapping("/consulta/nueva")
    public String nuevaConsulta(Model model) {
        Map<String, Object> consulta = new HashMap<>();
        consulta.put("id", null);
        consulta.put("mascota_id", null);
        consulta.put("diagnostico", "");

        model.addAttribute("consulta", consulta);
        model.addAttribute("tratamientos", List.of()); // لیست خالی برای فرم جدید
        model.addAttribute("mascotas", vidapetService.listarMascotas()); // اگر متد listarMascotas داری
        return "consulta_form";
    }

    @PostMapping("/consulta/guardar")
    public String guardarConsulta(@RequestParam int mascota_id,
                                  @RequestParam String diagnostico,
                                  @RequestParam(required = false) List<String> tratamientos,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fecha_inicio,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fecha_fin,
                                  @RequestParam(required = false) List<String> observaciones) {

        vidapetService.guardarConsulta(mascota_id, diagnostico);

        // گرفتن آخرین id ذخیره شده (فرض کنید JdbcTemplate auto-increment درست کار می‌کند)
        Map<String, Object> ultimaConsulta = vidapetService.listarConsultas().get(vidapetService.listarConsultas().size() - 1);
        int consultaId = (Integer) ultimaConsulta.get("id");

        if (tratamientos != null) {
            for (int i = 0; i < tratamientos.size(); i++) {
                vidapetService.guardarTratamiento(
                        consultaId,
                        tratamientos.get(i),
                        fecha_inicio != null ? fecha_inicio.get(i) : null,
                        fecha_fin != null ? fecha_fin.get(i) : null,
                        observaciones != null ? observaciones.get(i) : null
                );
            }
        }

        return "redirect:/consulta";
    }

    @GetMapping("/consulta/editar/{id}")
    public String editarConsulta(@PathVariable int id, Model model) {
        Map<String, Object> consulta = vidapetService.obtenerConsultaPorId(id);
        model.addAttribute("consulta", consulta);

        List<Map<String, Object>> tratamientos = (List<Map<String, Object>>) consulta.get("tratamientos");
        model.addAttribute("tratamientos", tratamientos);

        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "consulta_form";
    }

    @PostMapping("/consulta/actualizar")
    public String actualizarConsulta(@RequestParam int id,
                                     @RequestParam int mascota_id,
                                     @RequestParam String diagnostico,
                                     @RequestParam(required = false) List<Integer> tratamientoId,
                                     @RequestParam(required = false) List<String> tratamientos,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fecha_inicio,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) List<LocalDate> fecha_fin,
                                     @RequestParam(required = false) List<String> observaciones) {

        vidapetService.actualizarConsulta(id, mascota_id, diagnostico);

        if (tratamientos != null) {
            for (int i = 0; i < tratamientos.size(); i++) {
                if (tratamientoId != null && i < tratamientoId.size() && tratamientoId.get(i) != null) {
                    vidapetService.actualizarTratamiento(
                            tratamientoId.get(i),
                            id,
                            tratamientos.get(i),
                            fecha_inicio != null ? fecha_inicio.get(i) : null,
                            fecha_fin != null ? fecha_fin.get(i) : null,
                            observaciones != null ? observaciones.get(i) : null
                    );
                } else {
                    vidapetService.guardarTratamiento(
                            id,
                            tratamientos.get(i),
                            fecha_inicio != null ? fecha_inicio.get(i) : null,
                            fecha_fin != null ? fecha_fin.get(i) : null,
                            observaciones != null ? observaciones.get(i) : null
                    );
                }
            }
        }

        return "redirect:/consulta";
    }

    @GetMapping("/consulta/eliminar/{id}")
    public String eliminarConsulta(@PathVariable int id) {
        vidapetService.eliminarConsulta(id);
        return "redirect:/consulta";
    }

    /*-------------------- MASTER-DETAIL DASHBOARD --------------------*/

    @GetMapping("/consultas/dashboard")
    public String dashboardConsultas(Model model) {
        model.addAttribute("consultas", vidapetService.listarConsultasConTratamientos());
        return "consultas";
    }

    @PostMapping("/consultas/{id}/tratamiento")
    public String agregarTratamientoDashboard(@PathVariable int id,
                                              @RequestParam String tratamiento,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_inicio,
                                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_fin,
                                              @RequestParam(required = false) String observaciones) {
        vidapetService.guardarTratamiento(id, tratamiento, fecha_inicio, fecha_fin, observaciones);
        return "redirect:/consultas";
    }

    /*================= CITAS =================*/


}
