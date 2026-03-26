package com.example.vidapet.controller;

import com.example.vidapet.service.VidapetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/tratamientos")
    public String listarTratamientos(Model model) {
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());
        return "tratamientos";
    }

    @GetMapping("/tratamientos/nuevo")
    public String nuevoTratamiento(Model model) {
        Map<String, Object> tratamiento = new HashMap<>();
        tratamiento.put("id", "");
        tratamiento.put("tipo", "");
        tratamiento.put("duracion", "");
        tratamiento.put("consulta_id", "");
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento_form";
    }

    @PostMapping("/tratamientos/guardar")
    public String guardarTratamiento(@RequestParam String tipo,
                                     @RequestParam String duracion,
                                     @RequestParam Long consulta_id) {

        vidapetService.guardarTratamiento(tipo, duracion, consulta_id);
        return "redirect:/tratamientos";
    }

    @GetMapping("/tratamientos/editar/{id}")
    public String editarTratamiento(@PathVariable Long id, Model model) {
        Map<String, Object> tratamiento = vidapetService.obtenerTratamientoPorId(id);
        model.addAttribute("tratamiento", tratamiento);
        return "tratamiento_form";
    }

    @PostMapping("/tratamientos/actualizar")
    public String actualizarTratamiento(@RequestParam Long id,
                                        @RequestParam String tipo,
                                        @RequestParam String duracion,
                                        @RequestParam Long consulta_id) {

        vidapetService.actualizarTratamiento(id, tipo, duracion, consulta_id);
        return "redirect:/tratamientos";
    }

    @GetMapping("/tratamientos/eliminar/{id}")
    public String eliminarTratamiento(@PathVariable Long id) {
        vidapetService.eliminarTratamiento(id);
        return "redirect:/tratamientos";
    }

    /*---------------------------- CONSULTAS ----------------------------*/

    @GetMapping("/consultas")
    public String listarConsultas(Model model) {
        model.addAttribute("consultas", vidapetService.listarConsultas());
        return "consultas"; // consultas.html
    }

    @GetMapping("/consultas/form")
    public String nuevoConsulta(Model model) {
        Map<String, Object> consulta = Map.of(
                "id", "",
                "fecha", "",
                "motivo", "",
                "diagnostico", "",
                "mascota_id", ""
        );
        model.addAttribute("consulta", consulta);
        model.addAttribute("mascotas", vidapetService.listarMascotas()); // برای انتخاب Mascota
        return "consulta_form"; // consulta_form.html
    }

    @PostMapping("/consultas/save")
    public String guardarConsulta(@RequestParam("fecha") String fechaStr,
                                  @RequestParam String motivo,
                                  @RequestParam String diagnostico,
                                  @RequestParam Long mascota_id) {
        LocalDateTime fecha = LocalDateTime.parse(fechaStr);
        vidapetService.guardarConsulta(fecha, motivo, diagnostico, mascota_id);
        return "redirect:/consultas";
    }

    @GetMapping("/consultas/form/{id}")
    public String editarConsulta(@PathVariable Long id, Model model) {
        Map<String, Object> consulta = vidapetService.obtenerConsultaPorId(id);
        model.addAttribute("consulta", consulta);
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        return "consulta_form";
    }

    @PostMapping("/consultas/update")
    public String actualizarConsulta(@RequestParam Long id,
                                     @RequestParam("fecha") String fechaStr,
                                     @RequestParam String motivo,
                                     @RequestParam String diagnostico,
                                     @RequestParam Long mascota_id) {
        LocalDateTime fecha = LocalDateTime.parse(fechaStr);
        vidapetService.actualizarConsulta(id, fecha, motivo, diagnostico, mascota_id);
        return "redirect:/consultas";
    }

    @GetMapping("/consultas/delete/{id}")
    public String eliminarConsulta(@PathVariable Long id) {
        vidapetService.eliminarConsulta(id);
        return "redirect:/consultas";
    }



    /*---------------------------- DETALLE_TRATAMIENTO ----------------------------*/

    @GetMapping("/detalles")
    public String listarDetalles(Model model) {
        List<Map<String,Object>> detalles = vidapetService.listarDetalles();
        List<Map<String,Object>> consultas = vidapetService.listarConsultas();
        List<Map<String,Object>> tratamientos = vidapetService.listarTratamientos();

        // لیست جدید با HashMap mutable
        List<Map<String,Object>> detallesList = new ArrayList<>();
        for (Map<String,Object> d : detalles) {
            Map<String,Object> detalle = new HashMap<>(d); // HashMap قابل تغییر

            Long consultaId = (Long) d.get("consulta_id");
            Long tratamientoId = (Long) d.get("tratamiento_id");

            // پیدا کردن motivo از consulta
            String consultaMotivo = consultas.stream()
                    .filter(c -> c.get("id").equals(consultaId))
                    .map(c -> (String) c.get("motivo"))
                    .findFirst()
                    .orElse("N/A");
            detalle.put("consulta_motivo", consultaMotivo);

            // پیدا کردن نوع درمان
            String tratamientoTipo = tratamientos.stream()
                    .filter(t -> t.get("id").equals(tratamientoId))
                    .map(t -> (String) t.get("tipo"))
                    .findFirst()
                    .orElse("N/A");
            detalle.put("tratamiento_tipo", tratamientoTipo);

            detallesList.add(detalle);
        }

        model.addAttribute("detalles", detallesList);
        return "detalle_tratamiento";
    }

    @GetMapping("/detalles/nuevo")
    public String nuevoDetalle(Model model) {
        Map<String, Object> detalle = new HashMap<>();
        detalle.put("id", "");           // خالی چون جدید است
        detalle.put("consulta_id", "");
        detalle.put("tratamiento_id", "");

        model.addAttribute("detalle", detalle);
        model.addAttribute("consultas", vidapetService.listarConsultas());
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());

        return "detalle_form"; // نام فایل HTML فرم
    }

    // ذخیره جزئیات جدید
    @PostMapping("/detalles/guardar")
    public String guardarDetalle(@RequestParam Long consulta_id,
                                 @RequestParam Long tratamiento_id) {
        vidapetService.guardarDetalle(consulta_id, tratamiento_id);
        return "redirect:/detalles";
    }

    @GetMapping("/detalles/editar/{id}")
    public String editarDetalle(@PathVariable Long id, Model model) {
        Map<String, Object> detalle = vidapetService.obtenerDetallePorId(id);

        if (detalle == null) {
            // اگر چیزی پیدا نشد، Map خالی بساز
            detalle = new HashMap<>();
            detalle.put("id", "");
            detalle.put("consulta_id", "");
            detalle.put("tratamiento_id", "");
        }

        model.addAttribute("detalle", detalle);
        model.addAttribute("consultas", vidapetService.listarConsultas());
        model.addAttribute("tratamientos", vidapetService.listarTratamientos());

        return "detalle_form";
    }

    // بروزرسانی جزئیات
    @PostMapping("/detalles/actualizar")
    public String actualizarDetalle(@RequestParam Long id,
                                    @RequestParam Long consulta_id,
                                    @RequestParam Long tratamiento_id) {
        vidapetService.actualizarDetalle(id, consulta_id, tratamiento_id);
        return "redirect:/detalles";
    }

    // حذف جزئیات
    @GetMapping("/detalles/eliminar/{id}")
    public String eliminarDetalle(@PathVariable Long id) {
        vidapetService.eliminarDetalle(id);
        return "redirect:/detalles";
    }
}