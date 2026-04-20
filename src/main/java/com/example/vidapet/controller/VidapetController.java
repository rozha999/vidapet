package com.example.vidapet.controller;

import com.example.vidapet.dao.ConsultaDAO;
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
        mascota.put("especie_id", "");
        mascota.put("raza", "");
        mascota.put("fecha_nacimiento", "");
        mascota.put("propietario_id", "");
        mascota.put("foto", "");

        model.addAttribute("mascota", mascota);
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        model.addAttribute("especies", vidapetService.listarEspecies(null)); // لیست گونه‌ها اضافه شد

        return "mascota_form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(
            @RequestParam String nombre,
            @RequestParam Long especie_id, // هماهنگ با رابطه جدید
            @RequestParam String raza,
            @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
            @RequestParam(required = false) Long propietario_id, // برای جلوگیری از ارور 400
            @RequestParam(required = false) MultipartFile foto,
            RedirectAttributes ra
    ) throws Exception {

        // مدیریت خطای صاحب
        if (propietario_id == null) {
            ra.addFlashAttribute("error", "Debe seleccionar un propietario válido de la lista.");
            return "redirect:/mascotas/nuevo";
        }

        String filePath = "";
        if (foto != null && !foto.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Files.write(uploadDir.resolve(fileName), foto.getBytes());
            filePath = "uploads/" + fileName;
        }

        LocalDate fechaNacimiento = LocalDate.parse(fechaNacimientoStr);

        vidapetService.guardarMascota(
                nombre,
                especie_id, // ارسال ID گونه
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
        model.addAttribute("especies", vidapetService.listarEspecies(null)); // لیست گونه‌ها اضافه شد

        return "mascota_form";
    }

    @PostMapping("/mascotas/actualizar")
    public String actualizarMascota(
            @RequestParam Long id,
            @RequestParam String nombre,
            @RequestParam Long especie_id, // هماهنگ با رابطه جدید
            @RequestParam String raza,
            @RequestParam("fecha_nacimiento") String fechaNacimientoStr,
            @RequestParam(required = false) Long propietario_id,
            @RequestParam(required = false) MultipartFile foto,
            RedirectAttributes ra
    ) throws Exception {

        if (propietario_id == null) {
            ra.addFlashAttribute("error", "Debe seleccionar un propietario válido.");
            return "redirect:/mascotas/editar/" + id;
        }

        Map<String, Object> mascotaExistente = vidapetService.obtenerMascotaPorId(id);
        String filePath = (mascotaExistente != null && mascotaExistente.get("foto") != null)
                ? mascotaExistente.get("foto").toString() : "";

        if (foto != null && !foto.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Files.write(uploadDir.resolve(fileName), foto.getBytes());
            filePath = "uploads/" + fileName;
        }

        vidapetService.actualizarMascota(
                id,
                nombre,
                especie_id,
                raza,
                LocalDate.parse(fechaNacimientoStr),
                propietario_id,
                filePath
        );

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

    @GetMapping("/consultas")
    public String listarConsultas(@RequestParam(required = false) String search, Model model) {
        List<Map<String, Object>> consultas;

        if (search != null && !search.isBlank()) {
            // جستجو بر اساس نام حیوان (این متد در DAO شما هست)
            consultas = vidapetService.buscarConsultas(search);
        } else {
            // نمایش همه معاینات
            consultas = vidapetService.listarConsultas();
        }

        model.addAttribute("consultas", consultas);
        model.addAttribute("search", search); // برای اینکه کلمه جستجو شده در باکس باقی بماند
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

    @GetMapping("/consultas/editar/{id}")
    public String editarConsulta(@PathVariable int id, Model model) {

        Map<String, Object> consulta = vidapetService.obtenerConsultaPorId(id);

        model.addAttribute("consulta", consulta);

        model.addAttribute("tratamientos",
                consulta.getOrDefault("tratamientos", new ArrayList<>()));

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


    @GetMapping("/consultas/eliminar/{id}")
    public String eliminarConsulta(@PathVariable int id) {
        vidapetService.eliminarConsulta(id);
        return "redirect:/consultas";
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
    @GetMapping("/consultas/{id}")
    public String verDetalleConsulta(@PathVariable int id, Model model) {
        // گرفتن دیتای کامل
        Map<String, Object> data = vidapetService.obtenerConsultaDetalleCompleto(id);

        // فرستادن به صفحه HTML
        model.addAttribute("data", data);

        return "consulta_detalle"; // نام فایل HTML که قبلاً ساختیم
    }
    @GetMapping("/api/mascotas/{id}/propietario")
    @ResponseBody // مهم: برای اینکه خروجی فقط داده باشد نه صفحه وب
    public Map<String, Object> getPropietarioByMascota(@PathVariable Long id) {
        return vidapetService.obtenerPropietarioPorMascota(id);
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
        model.addAttribute("mascotas", vidapetService.listarMascotas()); // لیست حیوانات
        model.addAttribute("veterinarios", vidapetService.listarVeterinarios(null));
        return "cita_form";
    }

    @PostMapping("/citas/guardar")
    public String guardarCita(
            @RequestParam int mascota_id,
            @RequestParam int propietario_id,
            @RequestParam(required = false) Integer veterinario_id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha,
            @RequestParam String nota
    ) {

        vidapetService.guardarCita(
                mascota_id,
                propietario_id,
                veterinario_id,
                fecha,
                nota
        );

        return "redirect:/citas";
    }

    @GetMapping("/citas/editar/{id}")
    public String editarCita(@PathVariable int id, Model model) {

        model.addAttribute("cita", vidapetService.obtenerCita(id));
        model.addAttribute("mascotas", vidapetService.listarMascotas());
        model.addAttribute("propietarios", vidapetService.listarPropietarios(null));
        model.addAttribute("veterinarios", vidapetService.listarVeterinarios(null));

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
        Integer mascotaId = (Integer) cita.get("mascota_id");

        // 🔥 نکته مهم: از متدی استفاده کن که اطلاعات کامل پت (شامل گونه و صاحب) را برمی‌گرداند
        // اگر متد findConsultaFull را داری که قبلاً نوشتیم، اینجا از آن استفاده کن
        // یا مطمئن شو که این متد اطلاعات کامل را می‌آورد:
        Map<String, Object> data = vidapetService.obtenerDetalleParaNuevaConsulta(mascotaId, citaId);

        Map<String, Object> consulta = new HashMap<>();
        consulta.put("cita_id", citaId);
        consulta.put("mascota_id", mascotaId);
        consulta.put("diagnostico", "");

        model.addAttribute("consulta", consulta);
        model.addAttribute("data", data); // این دیتا باید شامل 'especie' و 'propietario_email' باشد
        model.addAttribute("tratamientos", new ArrayList<>());

        return "consulta_form";
    }
    @GetMapping("/consultas/buscar")
    public String buscarConsultas(@RequestParam(name = "nombre", required = false) String nombre, Model model) {

        // استفاده از متد "Seguro" که در سرویس ساختید
        List<Map<String, Object>> resultados = vidapetService.buscarConsultasSeguro(nombre);

        model.addAttribute("consultas", resultados);
        model.addAttribute("nombre", nombre);

        return "consultas";
    }
    @PostMapping("/consultas/guardar-desde-cita")
    public String guardarConsultaDesdeCita(
            @RequestParam int cita_id,
            @RequestParam int mascota_id,
            @RequestParam String diagnostico,
            @RequestParam(required = false) List<String> tratamientos,
            @RequestParam(required = false) List<LocalDate> fecha_inicio,
            @RequestParam(required = false) List<LocalDate> fecha_fin,
            @RequestParam(required = false) List<String> observaciones
    ) {
        int consultaId = vidapetService.guardarConsultaDesdeCita(cita_id, mascota_id, diagnostico);

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

        // تغییر این خط:
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


    // ================= VETERINARIOS (FIXED) =================
    @GetMapping("/veterinarios")
    public String listarVeterinarios(@RequestParam(required = false) String search,
                                     Model model) {

        model.addAttribute("veterinarios",
                vidapetService.listarVeterinarios(search));

        return "veterinarios";
    }
    @GetMapping("/veterinarios/nuevo")
    public String mostrarFormularioNuevoVeterinario(Model model) {
        Map<String, Object> veterinario = new HashMap<>();
        veterinario.put("id", "");
        veterinario.put("nombre", "");
        veterinario.put("apellido", "");
        veterinario.put("telefono", "");
        veterinario.put("email", "");
        veterinario.put("especialidad", "");
        veterinario.put("codigo_colegiado", "");

        model.addAttribute("veterinario", veterinario);
        return "veterinario_form";
    }
    @PostMapping("/veterinarios/guardar")
    public String guardarVeterinario(@RequestParam String nombre,
                                     @RequestParam String apellido,
                                     @RequestParam String telefono,
                                     @RequestParam String email,
                                     @RequestParam String especialidad,
                                     @RequestParam String codigo_colegiado) {

        vidapetService.guardarVeterinario(
                nombre, apellido, telefono, email, especialidad, codigo_colegiado
        );

        return "redirect:/veterinarios";
    }
    @GetMapping("/veterinarios/editar/{id}")
    public String mostrarFormularioEditarVeterinario(@PathVariable Long id, Model model) {
        Map<String, Object> veterinario =
                vidapetService.obtenerVeterinarioPorId(id);

        model.addAttribute("veterinario", veterinario);
        return "veterinario_form";
    }
    @PostMapping("/veterinarios/actualizar")
    public String actualizarVeterinario(@RequestParam Long id,
                                        @RequestParam String nombre,
                                        @RequestParam String apellido,
                                        @RequestParam String telefono,
                                        @RequestParam String email,
                                        @RequestParam String especialidad,
                                        @RequestParam String codigo_colegiado) {

        vidapetService.actualizarVeterinario(
                id, nombre, apellido, telefono, email, especialidad, codigo_colegiado
        );

        return "redirect:/veterinarios";
    }
    @GetMapping("/veterinarios/eliminar/{id}")
    public String eliminarVeterinario(@PathVariable Long id) {
        vidapetService.eliminarVeterinario(id);
        return "redirect:/veterinarios";
    }


    /*---------------------------- LISTAR ----------------------------*/

    @GetMapping("/especies")
    public String listarEspecies(@RequestParam(required = false) String search,
                                 Model model) {

        model.addAttribute("especies",
                vidapetService.listarEspecies(search));

        model.addAttribute("search", search);

        return "especies";
    }

    /*---------------------------- NUEVO ----------------------------*/

    @GetMapping("/especies/nuevo")
    public String mostrarFormularioNuevoEspecie(Model model) {
        Map<String, Object> especie = new HashMap<>();
        especie.put("id", "");       // این خیلی مهم است که خالی باشد نه نال
        especie.put("nombre", "");
        especie.put("foto", "");

        // دقت کن اسم ویژگی "especie" باشد
        model.addAttribute("especie", especie);

        return "especie_form";
    }
    /*---------------------------- GUARDAR ----------------------------*/

    @PostMapping("/especies/guardar")
    public String guardarEspecie(@RequestParam String nombre,
                                 @RequestParam("archivoFoto") MultipartFile archivoFoto,
                                 RedirectAttributes redirectAttributes) throws Exception {

        if (nombre.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio");
            return "redirect:/especies/nuevo";
        }

        String pathFinal = "";

        // منطق ذخیره فایل روی هارد
        if (archivoFoto != null && !archivoFoto.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + archivoFoto.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");

            if (!Files.exists(rutaUploads)) {
                Files.createDirectories(rutaUploads);
            }

            Path rutaCompleta = rutaUploads.resolve(nombreArchivo);
            Files.write(rutaCompleta, archivoFoto.getBytes());

            // این همان آدرسی است که در دیتابیس ذخیره می‌شود
            pathFinal = "uploads/" + nombreArchivo;
        }

        vidapetService.guardarEspecie(nombre, pathFinal);
        redirectAttributes.addFlashAttribute("success", "Especie creada correctamente");

        return "redirect:/especies";
    }
    /*---------------------------- EDITAR ----------------------------*/

    @GetMapping("/especies/editar/{id}")
    public String mostrarFormularioEditarEspecie(@PathVariable Long id, Model model) {
        Map<String, Object> especie = vidapetService.obtenerEspeciePorId(id);

        if (especie == null) {
            return "redirect:/especies"; // اگر پیدا نشد برگرد به لیست
        }

        model.addAttribute("especie", especie);
        return "especie_form";
    }

    /*---------------------------- ACTUALIZAR ----------------------------*/

    @PostMapping("/especies/actualizar")
    public String actualizarEspecie(@RequestParam Long id,
                                    @RequestParam String nombre,
                                    @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto, // تغییر به MultipartFile
                                    RedirectAttributes redirectAttributes) throws Exception {

        Map<String, Object> especieExistente = vidapetService.obtenerEspeciePorId(id);
        String pathFinal = (especieExistente.get("foto") != null) ? especieExistente.get("foto").toString() : "";

        if (archivoFoto != null && !archivoFoto.isEmpty()) {
            String nombreArchivo = System.currentTimeMillis() + "_" + archivoFoto.getOriginalFilename();
            Path rutaUploads = Paths.get("uploads");
            if (!Files.exists(rutaUploads)) Files.createDirectories(rutaUploads);
            Files.write(rutaUploads.resolve(nombreArchivo), archivoFoto.getBytes());
            pathFinal = "uploads/" + nombreArchivo;
        }

        vidapetService.actualizarEspecie(id, nombre, pathFinal);
        redirectAttributes.addFlashAttribute("success", "Especie actualizada");
        return "redirect:/especies";
    }

    /*---------------------------- ELIMINAR ----------------------------*/

    @GetMapping("/especies/eliminar/{id}")
    public String eliminarEspecie(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {

        vidapetService.eliminarEspecie(id);

        redirectAttributes.addFlashAttribute("success", "Especie eliminada");

        return "redirect:/especies";
    }
}

