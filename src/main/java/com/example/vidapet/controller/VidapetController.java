package com.example.vidapet.controller;

import com.example.vidapet.service.VidapetService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public String listarPropietarios(@RequestParam(required = false) String search,
                                     Model model) {

        model.addAttribute("propietarios",
                vidapetService.listarPropietarios(search));

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
        mascota.put("fecha_nacimiento", "");
        mascota.put("propietario_id", "");
        mascota.put("foto", "");

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));

        return "mascota_form";
    }

    /*====================== GUARDAR ======================*/
    @PostMapping("/mascotas/guardar")
    public String guardarMascota(
            @RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam String raza,
            @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
            @RequestParam Long propietario_id,
            @RequestParam(required = false) MultipartFile foto
    ) throws Exception {

        String filePath = "";

        if (foto != null && !foto.isEmpty()) {

            String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();

            Path uploadDir = Paths.get("uploads");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePathDisk = uploadDir.resolve(fileName);
            Files.write(filePathDisk, foto.getBytes());

            filePath = "uploads/" + fileName;
        }

        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

        vidapetService.guardarMascota(
                nombre,
                especie,
                raza,
                fechaNacimiento,
                propietario_id,
                filePath
        );

        return "redirect:/mascotas";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {

        Map<String, Object> mascota = vidapetService.obtenerMascotaPorId(id);

        if (mascota == null) {
            return "redirect:/mascotas";
        }

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));

        return "mascota_form";
    }

    /*====================== ACTUALIZAR ======================*/
    @PostMapping("/mascotas/actualizar")
    public String actualizarMascota(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam String especie,
            @RequestParam String raza,
            @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
            @RequestParam Long propietario_id,
            @RequestParam(required = false) MultipartFile foto
    ) throws Exception {

        Map<String, Object> mascotaExistente =
                vidapetService.obtenerMascotaPorId(id);

        if (mascotaExistente == null) {
            return "redirect:/mascotas";
        }

        String filePath = "";

        Object fotoDB = mascotaExistente.get("foto");
        if (fotoDB != null) {
            filePath = fotoDB.toString();
        }

        if (foto != null && !foto.isEmpty()) {

            String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();

            Path uploadDir = Paths.get("uploads");

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePathDisk = uploadDir.resolve(fileName);
            Files.write(filePathDisk, foto.getBytes());

            filePath = "uploads/" + fileName;
        }

        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

        vidapetService.actualizarMascota(
                id,
                nombre,
                especie,
                raza,
                fechaNacimiento,
                propietario_id,
                filePath
        );

        return "redirect:/mascotas";
    }

    /*====================== DELETE ======================*/
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

    @GetMapping("/consultas")
    public String listarConsultas(Model model) {
        model.addAttribute("consultas",
                vidapetService.listarConsultasConTratamientos());
        return "consultas";
    }

    @GetMapping("/consultas/nueva")
    public String nuevaConsulta(Model model) {

        Map<String, Object> consulta = new HashMap<>();
        consulta.put("id", null);
        consulta.put("mascota_id", null);
        consulta.put("cita_id", null);
        consulta.put("diagnostico", "");

        model.addAttribute("consulta", consulta);
        model.addAttribute("tratamientos", List.of());
        model.addAttribute("mascotas", vidapetService.listarMascotas());

        return "consulta_form";
    }

    @GetMapping("/consulta/editar/{id}")
    public String editarConsulta(@PathVariable int id, Model model) {

        Map<String, Object> consulta = vidapetService.obtenerConsultaPorId(id);

        model.addAttribute("consulta", consulta);

        model.addAttribute("tratamientos",
                consulta.get("tratamientos") != null
                        ? consulta.get("tratamientos")
                        : List.of());

        return "consulta_form";
    }

    @PostMapping("/consultas/actualizar")
    public String actualizarConsulta(
            @RequestParam int id,
            @RequestParam int mascota_id,
            @RequestParam int cita_id,
            @RequestParam String diagnostico,
            @RequestParam(required = false) List<Integer> tratamientoId,
            @RequestParam(required = false) List<String> tratamientos,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            List<LocalDate> fecha_inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            List<LocalDate> fecha_fin,
            @RequestParam(required = false) List<String> observaciones
    ) {

        // update consulta
        vidapetService.actualizarConsulta(id, mascota_id, diagnostico);

        // update/insert tratamientos
        if (tratamientos != null) {
            for (int i = 0; i < tratamientos.size(); i++) {

                Integer tid = (tratamientoId != null && i < tratamientoId.size())
                        ? tratamientoId.get(i)
                        : null;

                if (tid != null) {

                    vidapetService.actualizarTratamiento(
                            tid,
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

        return "redirect:/citas";
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
    @GetMapping("/citas")
    public String getCitas(
            @RequestParam(required = false) String mascota,
            @RequestParam(required = false) String propietario,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false, defaultValue = "asc") String orden,
            Model model
    ) {

        model.addAttribute("citas",
                vidapetService.listarCitas(mascota, propietario, fecha, orden));

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietario", propietario);
        model.addAttribute("fecha", fecha);
        model.addAttribute("orden", orden);

        return "citas";
    }
    @GetMapping("/citas/nueva")
    public String nuevaCita(Model model) {
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        return "cita_form";
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(
            @RequestParam int mascota_id,
            @RequestParam int propietario_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam String nota
    ) {
        vidapetService.guardarCita(mascota_id, propietario_id, fecha, nota);
        return "redirect:/citas";
    }

    @GetMapping("/citas/editar/{id}")
    public String editarCita(@PathVariable int id, Model model) {
        model.addAttribute("cita", vidapetService.obtenerCita(id));
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        return "cita_form";
    }

    @GetMapping("/citas/eliminar/{id}")
    public String eliminarCita(@PathVariable int id) {
        vidapetService.eliminarCita(id);
        return "redirect:/citas";
    }
    @GetMapping("/citas/estado/{id}/{estado}")
    public String cambiarEstado(@PathVariable int id,
                                @PathVariable String estado) {

        vidapetService.cambiarEstadoCita(id, estado);
        return "redirect:/citas";
    }

    @GetMapping("/consultas/nueva-desde-cita/{citaId}")
    public String nuevaDesdeCita(@PathVariable int citaId, Model model) {

        Map<String, Object> cita = vidapetService.obtenerCita(citaId);

        Map<String, Object> mascota = vidapetService.obtenerMascotaPorId(
                Long.valueOf(cita.get("mascota_id").toString())
        );

        Map<String, Object> propietario = vidapetService.obtenerPropietarioPorId(
                Long.valueOf(cita.get("propietario_id").toString())
        );

        Map<String, Object> data = new HashMap<>();

        // 🐶 mascota
        data.put("mascota_nombre", mascota.get("nombre"));
        data.put("especie", mascota.get("especie"));
        data.put("raza", mascota.get("raza"));
        data.put("fecha_nacimiento", mascota.get("fecha_nacimiento"));
        data.put("foto", mascota.get("foto"));

        // 👤 propietario
        data.put("propietario_nombre", propietario.get("nombre"));
        data.put("propietario_apellido", propietario.get("apellido"));
        data.put("propietario_telefono", propietario.get("telefono"));
        data.put("propietario_email", propietario.get("email"));

        // cita
        data.put("cita_id", citaId);

        model.addAttribute("data", data);
        model.addAttribute("tratamientos", List.of());

        return "consulta_form";
    }
    @GetMapping("/consultas/{id}")
    public String verConsulta(@PathVariable int id, Model model) {

        model.addAttribute("data",
                vidapetService.obtenerConsultaCompleta(id));

        return "consulta_detalle";
    }
    @PostMapping("/consultas/guardar-desde-cita")
    public String guardarConsultaDesdeCita(
            @RequestParam int cita_id,
            @RequestParam int mascota_id,
            @RequestParam String diagnostico
    ) {

        vidapetService.guardarConsultaDesdeCita(
                cita_id,
                mascota_id,
                diagnostico
        );

        return "redirect:/citas";
    }
    @PostMapping("/citas/actualizar")
    public String actualizarCita(
            @RequestParam int id,
            @RequestParam int mascota_id,
            @RequestParam int propietario_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam String nota
    ) {
        vidapetService.actualizarCita(id, mascota_id, propietario_id, fecha, nota);
        return "redirect:/citas";
    }
}
