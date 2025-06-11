package com.gestionplanillas.application.services;
import com.gestionplanillas.application.data.*;
import com.gestionplanillas.application.repository.ContratoEmpleadoRepository;
import com.gestionplanillas.application.repository.JornadaExtraRepository;
import com.gestionplanillas.application.repository.JornadaLaboralRepository;
import com.gestionplanillas.application.repository.PlanillaRepository;
import com.gestionplanillas.application.repository.RegistroPlanillaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PlanillaService {
    private final PlanillaRepository planillaRepository;
    private final RegistroPlanillaRepository registroPlanillaRepository;
    private final ContratoEmpleadoRepository contratoEmpleadoRepository;
    private final JornadaLaboralRepository jornadaLaboralRepository;
    private final JornadaExtraRepository jornadaExtraRepository;

    // Constantes para cálculos (El Salvador)
    private static final int DIAS_MES = 30; // Para cálculos de salario diario o por periodo (común en planillas)
    private static final int HORAS_JORNADA_ORDINARIA = 8; // Horas normales por día
    private static final BigDecimal PORCENTAJE_HORA_EXTRA_DIURNA_ORDINARIA = new BigDecimal("0.50"); // 50% adicional
    private static final BigDecimal PORCENTAJE_HORA_EXTRA_NOCTURNA_ORDINARIA = new BigDecimal("0.75"); // 75% adicional
    private static final BigDecimal PORCENTAJE_HORA_EXTRA_DIURNA_DESCANSO_FERIADO = new BigDecimal("1.00"); // 100% adicional
    private static final BigDecimal PORCENTAJE_HORA_EXTRA_NOCTURNA_DESCANSO_FERIADO = new BigDecimal("1.25"); // 125% adicional

    // Porcentajes de prestaciones de ley (HARDCODEADOS, revisar legislación actual)
    private static final BigDecimal ISSS_EMPLEADO_PORCENTAJE = new BigDecimal("0.03"); // 3%
    private static final BigDecimal ISSS_EMPLEADO_TOPE = new BigDecimal("1000.00"); // Tope $1,000.00
    private static final BigDecimal ISSS_PATRONO_PORCENTAJE = new BigDecimal("0.075"); // 7.5%
    private static final BigDecimal ISSS_PATRONO_TOPE = new BigDecimal("7000.00"); // Tope $7,000.00

    private static final BigDecimal AFP_EMPLEADO_PORCENTAJE = new BigDecimal("0.0725"); // 7.25%
    private static final BigDecimal AFP_EMPLEADO_TOPE = new BigDecimal("7250.00"); // Tope $7,250.00 (revisar si aplica al mes)
    private static final BigDecimal AFP_PATRONO_PORCENTAJE = new BigDecimal("0.0875"); // 8.75%
    private static final BigDecimal AFP_PATRONO_TOPE = new BigDecimal("7250.00"); // Tope $7,250.00

    // Precisión para BigDecimal
    private static final int DECIMAL_SCALE = 2;
    private static final java.math.RoundingMode ROUNDING_MODE = java.math.RoundingMode.HALF_UP;


    public PlanillaService(PlanillaRepository planillaRepository,
                           RegistroPlanillaRepository registroPlanillaRepository,
                           ContratoEmpleadoRepository contratoEmpleadoRepository,
                           JornadaLaboralRepository jornadaLaboralRepository,
                           JornadaExtraRepository jornadaExtraRepository) {
        this.planillaRepository = planillaRepository;
        this.registroPlanillaRepository = registroPlanillaRepository;
        this.contratoEmpleadoRepository = contratoEmpleadoRepository;
        this.jornadaLaboralRepository = jornadaLaboralRepository; // Inyectado
        this.jornadaExtraRepository = jornadaExtraRepository;
    }

    // --- Métodos para Planilla ---
    public List<Planilla> getAllPlanillas() {
        return planillaRepository.findAll();
    }

    public Optional<Planilla> getPlanillaById(Long id) {
        return planillaRepository.findById(id);
    }

    public Planilla savePlanilla(Planilla planilla) {
        return planillaRepository.save(planilla);
    }

    public void deletePlanilla(Long id) {
        planillaRepository.deleteById(id);
    }

    public Optional<Planilla> getPlanillaWithDetails(Long id) {
        return planillaRepository.findByIdWithDetails(id);
    }

    // --- Métodos para RegistroPlanilla ---
    public List<RegistroPlanilla> getAllRegistrosPlanilla() {
        return registroPlanillaRepository.findAll();
    }

    public Optional<RegistroPlanilla> getRegistroPlanillaById(Long id) {
        return registroPlanillaRepository.findById(id);
    }

    // --- Lógica para Generar Planilla ---
    @Transactional
    public Planilla generarPlanilla(LocalDate fechaInicioCorte, LocalDate fechaFinCorte) {
        Planilla nuevaPlanilla = new Planilla();
        nuevaPlanilla.setFecha_inicio_corte(Date.valueOf(fechaInicioCorte));
        nuevaPlanilla.setFecha_fin_corte(Date.valueOf(fechaFinCorte));

        nuevaPlanilla = planillaRepository.save(nuevaPlanilla);

        List<ContratoEmpleado> contratosActivos = contratoEmpleadoRepository.findAll(); // Obtener todos los contratos activos

        for (ContratoEmpleado contrato : contratosActivos) {
            RegistroPlanilla registro = calcularRegistroPlanillaParaEmpleado(contrato, fechaInicioCorte, fechaFinCorte);
            registro.setPlanilla(nuevaPlanilla); // Asigna la planilla al registro
            registroPlanillaRepository.save(registro); // Guarda el registro individualmente
        }
        return planillaRepository.save(nuevaPlanilla); // Guarda la planilla con los registros asociados si el cascade está configurado
    }

    private RegistroPlanilla calcularRegistroPlanillaParaEmpleado(ContratoEmpleado contratoEmpleado, LocalDate fechaInicioCorte, LocalDate fechaFinCorte) {
        RegistroPlanilla registro = new RegistroPlanilla();
        registro.setContratoEmpleado(contratoEmpleado);

        // Inicializar todos los BigDecimal a cero para evitar NullPointerExceptions
        registro.setMontoHoraExtrasDiurnas(BigDecimal.ZERO);
        registro.setMontoHorasExtrasNocturnas(BigDecimal.ZERO);
        registro.setHorasExtrasDiurnas(0.0);
        registro.setHorasExtrasNocturnas(0.0);
        registro.setAguinaldo(BigDecimal.ZERO);
        registro.setVacacion(BigDecimal.ZERO);
        registro.setRenta(BigDecimal.ZERO);
        registro.setIsssEmpleado(BigDecimal.ZERO);
        registro.setIsssPatrono(BigDecimal.ZERO);
        registro.setAfpEmpleado(BigDecimal.ZERO);
        registro.setAfpPatrono(BigDecimal.ZERO);
        registro.setSalarioBasePeriodo(BigDecimal.ZERO);
        registro.setTotalDevengado(BigDecimal.ZERO);
        registro.setTotalDeducciones(BigDecimal.ZERO);
        registro.setSalarioNeto(BigDecimal.ZERO);

        BigDecimal salarioBaseMensual = contratoEmpleado.getSalarioBaseMensual();
        if (salarioBaseMensual == null) {
            salarioBaseMensual = BigDecimal.ZERO;
        }

        long diasPeriodo = ChronoUnit.DAYS.between(fechaInicioCorte, fechaFinCorte) + 1;
        BigDecimal salarioDiario = salarioBaseMensual.divide(new BigDecimal(DIAS_MES), DECIMAL_SCALE, ROUNDING_MODE);
        BigDecimal salarioBasePeriodo = salarioDiario.multiply(new BigDecimal(diasPeriodo)).setScale(DECIMAL_SCALE, ROUNDING_MODE);
        registro.setSalarioBasePeriodo(salarioBasePeriodo);


        // --- CELDAS DE CALCULO DE SALARIO DEVENGADO ---

        // **1. Obtener tiempo trabajado y calcular horas extras (Jornadas Laborales y Extras)**
        List<JornadaExtra> jornadasExtras = jornadaExtraRepository.findByJornadaLaboral_ContratoEmpleado_IdContratoEmpleadoAndFechaBetween(
                contratoEmpleado.getIdContratoEmpleado(), fechaInicioCorte, fechaFinCorte);

        double horasExtrasDiurnas = 0.0;
        double horasExtrasNocturnas = 0.0;
        BigDecimal montoHorasExtrasDiurnas = BigDecimal.ZERO;
        BigDecimal montoHorasExtrasNocturnas = BigDecimal.ZERO;

        for (JornadaExtra extra : jornadasExtras) {
            long duracionMinutos = ChronoUnit.MINUTES.between(extra.getHoraInicio(), extra.getHora_fin());

            if (duracionMinutos < 0) { // Manejar horas extras que cruzan la medianoche
                duracionMinutos = ChronoUnit.MINUTES.between(extra.getHoraInicio(), LocalDateTime.MAX) +
                        ChronoUnit.MINUTES.between(LocalDateTime.MIN, extra.getHora_fin());
            }
            double duracionHoras = duracionMinutos / 60.0;

            boolean esNocturna = isHoraNocturna(extra.getHoraInicio().toLocalTime(), extra.getHora_fin().toLocalTime());
            boolean esDiaDescansoFeriado = isDiaDescansoFeriado(extra.getFecha());

            BigDecimal valorHoraOrdinaria = salarioDiario.divide(new BigDecimal(HORAS_JORNADA_ORDINARIA), DECIMAL_SCALE, ROUNDING_MODE);

            if (esNocturna) {
                horasExtrasNocturnas += duracionHoras;
                if (esDiaDescansoFeriado) {
                    montoHorasExtrasNocturnas = montoHorasExtrasNocturnas.add(
                            valorHoraOrdinaria.multiply(new BigDecimal(duracionHoras))
                                    .multiply(BigDecimal.ONE.add(PORCENTAJE_HORA_EXTRA_NOCTURNA_DESCANSO_FERIADO))
                    ).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                } else {
                    montoHorasExtrasNocturnas = montoHorasExtrasNocturnas.add(
                            valorHoraOrdinaria.multiply(new BigDecimal(duracionHoras))
                                    .multiply(BigDecimal.ONE.add(PORCENTAJE_HORA_EXTRA_NOCTURNA_ORDINARIA))
                    ).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                }
            } else { // Diurna
                horasExtrasDiurnas += duracionHoras;
                if (esDiaDescansoFeriado) {
                    montoHorasExtrasDiurnas = montoHorasExtrasDiurnas.add(
                            valorHoraOrdinaria.multiply(new BigDecimal(duracionHoras))
                                    .multiply(BigDecimal.ONE.add(PORCENTAJE_HORA_EXTRA_DIURNA_DESCANSO_FERIADO))
                    ).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                } else {
                    montoHorasExtrasDiurnas = montoHorasExtrasDiurnas.add(
                            valorHoraOrdinaria.multiply(new BigDecimal(duracionHoras))
                                    .multiply(BigDecimal.ONE.add(PORCENTAJE_HORA_EXTRA_DIURNA_ORDINARIA))
                    ).setScale(DECIMAL_SCALE, ROUNDING_MODE);
                }
            }
        }
        registro.setHorasExtrasDiurnas(horasExtrasDiurnas);
        registro.setMontoHoraExtrasDiurnas(montoHorasExtrasDiurnas);
        registro.setHorasExtrasNocturnas(horasExtrasNocturnas);
        registro.setMontoHorasExtrasNocturnas(montoHorasExtrasNocturnas);

        // **2. Obtener el monto de bonificaciones y viáticos asociados al contrato**
        BigDecimal totalBonificaciones = contratoEmpleado.getBonificaciones().stream()
                .filter(b -> !b.getFecha().isBefore(fechaInicioCorte) && !b.getFecha().isAfter(fechaFinCorte))
                .map(Bonificacion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);

        BigDecimal totalViaticos = contratoEmpleado.getViaticos().stream()
                .filter(v -> !v.getFecha().isBefore(fechaInicioCorte) && !v.getFecha().isAfter(fechaFinCorte))
                .map(Viatico::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);

        // --- CALCULAR AGUINALDO ---
        BigDecimal montoAguinaldo = BigDecimal.ZERO;
        LocalDate fechaContrato = LocalDate.from(contratoEmpleado.getFechaContrato());
        // `aniosDeServicio` se calcula hasta la `fechaFinCorte` de la planilla actual
        long aniosDeServicio = ChronoUnit.YEARS.between(fechaContrato, fechaFinCorte);
        long diasDeServicioTotal = ChronoUnit.DAYS.between(fechaContrato, fechaFinCorte);
// evitar negativos si fechaContrato es posterior a fechaFinCorte
        if (diasDeServicioTotal < 0) {
            diasDeServicioTotal = 0;
            aniosDeServicio = 0; // Si no ha trabajado, no tiene años de servicio
        }
        // Regla: Pagar entre el 12 y 20 de diciembre
        if (fechaFinCorte.getMonthValue() == 12 && fechaFinCorte.getDayOfMonth() >= 12 && fechaFinCorte.getDayOfMonth() <= 20) {
            if (aniosDeServicio >= 10) {
                montoAguinaldo = salarioDiario.multiply(new BigDecimal("21")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
            } else if (aniosDeServicio >= 3) {
                montoAguinaldo = salarioDiario.multiply(new BigDecimal("19")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
            } else if (aniosDeServicio >= 1) {
                montoAguinaldo = salarioDiario.multiply(new BigDecimal("15")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
            } else {
                ////////////////
                // Menos de un año de servicio: proporcional
                // FÓRMULA CORREGIDA PARA AGUINALDO PROPORCIONAL (< 1 AÑO DE SERVICIO)
                // Se calcula la parte proporcional de los 15 días de aguinaldo.
                // (Salario Diario * 15 días de base) * (Días trabajados / 365 días del año)
                montoAguinaldo = salarioDiario
                        .multiply(new BigDecimal("15")) // Los días de base para el aguinaldo de 1 año son 15
                        .multiply(new BigDecimal(diasDeServicioTotal))
                        .divide(new BigDecimal("365"), DECIMAL_SCALE, ROUNDING_MODE);
            /////////////////
            }
        }
        registro.setAguinaldo(montoAguinaldo);

        // --- CALCULAR VACACIONES ---
        BigDecimal montoVacacion = BigDecimal.ZERO;
        // Regla: Después de un año de trabajo continuo
        // Lógica: Se activa en el mes del aniversario del contrato si ya cumplió 1 año o más.
        if (aniosDeServicio >= 1 && fechaFinCorte.getMonthValue() == fechaContrato.getMonthValue()) {
            // Remuneración de 15 días de salario ordinario + 30% de recargo
            BigDecimal quinceDiasSalario = salarioDiario.multiply(new BigDecimal("15"));
            montoVacacion = quinceDiasSalario.multiply(new BigDecimal("0.30")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
        }
        registro.setVacacion(montoVacacion);


        // **3. Calcular Salario Bruto (Total Devengado)
        BigDecimal totalDevengado = salarioBasePeriodo
                .add(montoHorasExtrasDiurnas)
                .add(montoHorasExtrasNocturnas)
                .add(totalBonificaciones)
                .add(totalViaticos)
                .add(registro.getAguinaldo())
                .add(registro.getVacacion())
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        registro.setTotalDevengado(totalDevengado);


        // --- CELDAS DE CALCULO DEL SALARIO A DEDUCIR ---

        // **4. Cálculo de Prestaciones de Ley (ISSS, AFP, Renta) **

        // Base de cálculo para ISSS/AFP del período (quincenal)
        // Aguinaldo y Vacación (la parte del recargo) usualmente no son gravables para ISSS/AFP.
        BigDecimal baseCalculoPrestacionPeriodo = salarioBasePeriodo;

        // ISSS Empleado
        BigDecimal isssEmpleado = baseCalculoPrestacionPeriodo.multiply(ISSS_EMPLEADO_PORCENTAJE)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        // Ajuste de tope proporcional al periodo (ej. si es quincenal, la mitad del tope mensual)
        BigDecimal isssEmpleadoTopePeriodo = ISSS_EMPLEADO_TOPE.multiply(new BigDecimal(diasPeriodo)).divide(new BigDecimal(DIAS_MES), DECIMAL_SCALE, ROUNDING_MODE);
        if (isssEmpleado.compareTo(isssEmpleadoTopePeriodo) > 0) {
            isssEmpleado = isssEmpleadoTopePeriodo;
        }
        registro.setIsssEmpleado(isssEmpleado);

        // AFP Empleado
        BigDecimal afpEmpleado = baseCalculoPrestacionPeriodo.multiply(AFP_EMPLEADO_PORCENTAJE)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        // Ajuste de tope proporcional al periodo
        BigDecimal afpEmpleadoTopePeriodo = AFP_EMPLEADO_TOPE.multiply(new BigDecimal(diasPeriodo)).divide(new BigDecimal(DIAS_MES), DECIMAL_SCALE, ROUNDING_MODE);
        if (afpEmpleado.compareTo(afpEmpleadoTopePeriodo) > 0) {
            afpEmpleado = afpEmpleadoTopePeriodo;
        }
        registro.setAfpEmpleado(afpEmpleado);

        // ISSS Patrono
        BigDecimal isssPatrono = baseCalculoPrestacionPeriodo.multiply(ISSS_PATRONO_PORCENTAJE)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        // Ajuste de tope proporcional al periodo
        BigDecimal isssPatronoTopePeriodo = ISSS_PATRONO_TOPE.multiply(new BigDecimal(diasPeriodo)).divide(new BigDecimal(DIAS_MES), DECIMAL_SCALE, ROUNDING_MODE);
        if (isssPatrono.compareTo(isssPatronoTopePeriodo) > 0) {
            isssPatrono = isssPatronoTopePeriodo;
        }
        registro.setIsssPatrono(isssPatrono);

        // AFP Patrono
        BigDecimal afpPatrono = baseCalculoPrestacionPeriodo.multiply(AFP_PATRONO_PORCENTAJE)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        // Ajuste de tope proporcional al periodo
        BigDecimal afpPatronoTopePeriodo = AFP_PATRONO_TOPE.multiply(new BigDecimal(diasPeriodo)).divide(new BigDecimal(DIAS_MES), DECIMAL_SCALE, ROUNDING_MODE);
        if (afpPatrono.compareTo(afpPatronoTopePeriodo) > 0) {
            afpPatrono = afpPatronoTopePeriodo;
        }
        registro.setAfpPatrono(afpPatrono);

        // Cálculo de Renta (Impuesto sobre la Renta - ISR)

        BigDecimal salarioGravableMensual = totalDevengado.subtract(registro.getIsssEmpleado()).subtract(registro.getAfpEmpleado());
        BigDecimal salarioGravableProyectadoMensual = salarioGravableMensual.multiply(new BigDecimal(DIAS_MES)).divide(new BigDecimal(diasPeriodo), DECIMAL_SCALE, ROUNDING_MODE);

        BigDecimal rentaCalculadaMensual = calcularRenta(salarioGravableProyectadoMensual);
        // La renta que se deduce en el período es la parte proporcional de la renta mensual calculada.
        BigDecimal rentaPeriodo = rentaCalculadaMensual.multiply(new BigDecimal(diasPeriodo)).divide(new BigDecimal(DIAS_MES), DECIMAL_SCALE, ROUNDING_MODE);
        registro.setRenta(rentaPeriodo);


        // **5. Calcular Total Deducciones**
        BigDecimal totalDeducciones = registro.getIsssEmpleado()
                .add(registro.getAfpEmpleado())
                .add(registro.getRenta())
                // .add(otrosDescuentos) // Si hay otros descuentos, súmalos aquí
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        registro.setTotalDeducciones(totalDeducciones);


        // --- CELDA DE CALCULO DEL SALARIO NETO DEVENGADO ---

        // **6. Calcular Salario Neto a Devenegar (Salario Neto)**
        BigDecimal salarioNeto = totalDevengado.subtract(totalDeducciones)
                .setScale(DECIMAL_SCALE, ROUNDING_MODE);
        registro.setSalarioNeto(salarioNeto);

        return registro;
    }

    /**
     * Calcula el Impuesto sobre la Renta (ISR) basado en el salario gravable mensual.
     * Esta es una lógica específica para El Salvador y sus tramos de renta (VERIFICAR AÑO FISCAL ACTUAL).
     * @param salarioGravableMensual Salario después de deducciones de ISSS y AFP (mensual).
     * @return El monto de Renta a deducir (mensual).
     */
    private BigDecimal calcularRenta(BigDecimal salarioGravableMensual) {
        BigDecimal renta = BigDecimal.ZERO;


        // Tramo 1: Hasta $472.00 (Exento)
        // Tramo 2: De $472.01 a $895.23 (10% sobre el excedente de $472.00 + cuota fija de $17.67)
        // Tramo 3: De $895.24 a $2,038.10 (20% sobre el excedente de $895.23 + cuota fija de $60.00)
        // Tramo 4: Más de $2,038.10 (30% sobre el excedente de $2,038.10 + cuota fija de $288.57)

        BigDecimal tramo1Limite = new BigDecimal("472.00");
        BigDecimal tramo2Limite = new BigDecimal("895.23");
        BigDecimal tramo3Limite = new BigDecimal("2038.10");

        if (salarioGravableMensual.compareTo(tramo1Limite) <= 0) {
            renta = BigDecimal.ZERO; // Exento
        } else if (salarioGravableMensual.compareTo(tramo2Limite) <= 0) {
            // Tramo 2
            BigDecimal excedente = salarioGravableMensual.subtract(tramo1Limite);
            renta = excedente.multiply(new BigDecimal("0.10")).add(new BigDecimal("17.67")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
        } else if (salarioGravableMensual.compareTo(tramo3Limite) <= 0) {
            // Tramo 3
            BigDecimal excedente = salarioGravableMensual.subtract(tramo2Limite);
            renta = excedente.multiply(new BigDecimal("0.20")).add(new BigDecimal("60.00")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
        } else {
            // Tramo 4
            BigDecimal excedente = salarioGravableMensual.subtract(tramo3Limite);
            renta = excedente.multiply(new BigDecimal("0.30")).add(new BigDecimal("288.57")).setScale(DECIMAL_SCALE, ROUNDING_MODE);
        }

        return renta;
    }

    /**
     * Determina si el rango de horas es principalmente nocturno.
     * Definición de horas nocturnas: 7 PM (19:00) a 6 AM (6:00 del día siguiente).
     * Calcula los minutos que caen en el rango nocturno y compara con la mitad de la duración total.
     * @param horaInicio Hora de inicio de la jornada extra (LocalTime).
     * @param horaFin Hora de fin de la jornada extra (LocalTime).
     * @return true si la mayor parte de la jornada extra cae en horario nocturno.
     */
    private boolean isHoraNocturna(LocalTime horaInicio, LocalTime horaFin) {
        LocalTime NOCHE_INICIO = LocalTime.of(19, 0); // 7 PM
        LocalTime DIA_FIN = LocalTime.of(6, 0);     // 6 AM

        long totalMinutos;
        if (horaInicio.isBefore(horaFin)) {
            totalMinutos = ChronoUnit.MINUTES.between(horaInicio, horaFin);
        } else {
            // La jornada cruza la medianoche
            totalMinutos = ChronoUnit.MINUTES.between(horaInicio, LocalTime.MAX) +
                    ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, horaFin);
        }

        if (totalMinutos == 0) return false;

        long nocturnoMinutos = 0;

        if (horaInicio.isBefore(horaFin)) {
            // Caso 1: La jornada no cruza la medianoche (ej. 18:00 a 22:00)
            // Intersección con el rango nocturno 1 (19:00 - 23:59)
            LocalTime startOverlap1 = horaInicio.isAfter(NOCHE_INICIO) ? horaInicio : NOCHE_INICIO;
            LocalTime endOverlap1 = horaFin.isBefore(LocalTime.MAX) ? horaFin : LocalTime.MAX;

            if (startOverlap1.isBefore(endOverlap1)) {
                nocturnoMinutos += ChronoUnit.MINUTES.between(startOverlap1, endOverlap1);
            }

            // Intersección con el rango nocturno 2 (00:00 - 06:00)
            LocalTime startOverlap2 = horaInicio.isAfter(LocalTime.MIDNIGHT) ? horaInicio : LocalTime.MIDNIGHT; // Desde 00:00
            LocalTime endOverlap2 = horaFin.isBefore(DIA_FIN) ? horaFin : DIA_FIN;

            if (startOverlap2.isBefore(endOverlap2)) {
                nocturnoMinutos += ChronoUnit.MINUTES.between(startOverlap2, endOverlap2);
            }

        } else {
            // Caso 2: La jornada cruza la medianoche (ej. 22:00 a 03:00)
            // Minutos nocturnos del primer día (desde horaInicio hasta 23:59)
            LocalTime startOverlap1 = horaInicio.isAfter(NOCHE_INICIO) ? horaInicio : NOCHE_INICIO;
            if (startOverlap1.isBefore(LocalTime.MAX)) {
                nocturnoMinutos += ChronoUnit.MINUTES.between(startOverlap1, LocalTime.MAX);
            }

            // Minutos nocturnos del segundo día (desde 00:00 hasta horaFin)
            LocalTime endOverlap2 = horaFin.isBefore(DIA_FIN) ? horaFin : DIA_FIN;
            if (LocalTime.MIDNIGHT.isBefore(endOverlap2)) { // Desde 00:00
                nocturnoMinutos += ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, endOverlap2);
            }
        }
        // Si más de la mitad de la duración cae en horario nocturno, se considera nocturna.
        return nocturnoMinutos * 2 > totalMinutos;
    }


    /**
     * Determina si una fecha dada es un día de descanso (domingo) o un día feriado.
     * Actualmente, solo verifica si es domingo.
     * @param fecha La fecha a verificar.
     * @return true si es un día de descanso o feriado.
     */
    private boolean isDiaDescansoFeriado(LocalDate fecha) {
        // 1. Verificar si es domingo
        if (fecha.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            return true;
        }
        // 2. Verificar si es feriado (NO IMPLEMENTADO, solo devuelve false)

        return false;
    }

    public Page<Planilla> listPlanillas(Pageable pageable, Specification<Planilla> filter) {
        // Este metodo usa el PlanillaRepository para buscar planillas con paginación y filtro.
        return planillaRepository.findAll(filter, pageable);
    }
}
