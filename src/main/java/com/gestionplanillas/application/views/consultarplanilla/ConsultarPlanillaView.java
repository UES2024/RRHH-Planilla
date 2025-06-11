package com.gestionplanillas.application.views.consultarplanilla;

import com.gestionplanillas.application.repository.EmpleadoRepository;
import com.gestionplanillas.application.data.Empleado;
import com.vaadin.flow.data.provider.ListDataProvider;

import com.vaadin.flow.component.orderedlayout.FlexComponent;
import java.time.LocalDate;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.ArrayList;

@PermitAll
@PageTitle("Consulta de Planilla")
@Route("consulta-planilla")
@Uses(Icon.class)
public class ConsultarPlanillaView extends Composite<VerticalLayout> {

    public ConsultarPlanillaView(EmpleadoRepository empleadoRepository) {
        ComboBox<Empleado> comboBoxEmpleados = new ComboBox<>("Seleccionar Empleado");
        List<Empleado> empleados = empleadoRepository.findAll();
        // 🔍 Aquí imprimimos en consola los empleados que se cargaron
        empleados.forEach(emp ->
                System.out.println("Empleado cargado: " + emp.getNombres() + " " + emp.getApellidos())
        );

        ListDataProvider<Empleado> dataProvider = new ListDataProvider<>(empleados);

        // Layouts
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidthFull();

        //DataPicker
        ComboBox<String> comboBoxMes = new ComboBox<>("Mes");
        comboBoxMes.setItems("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre");
        comboBoxMes.setValue(obtenerMesActual()); // Establecer por defecto

        ComboBox<Integer> comboBoxAnio = new ComboBox<>("Año");
        comboBoxAnio.setItems(generarAnios());
        comboBoxAnio.setValue(LocalDate.now().getYear()); // Año actual


        // ComboBox: Quincena
        ComboBox<String> comboBoxQuincena = new ComboBox<>("Seleccione Quincena");
        comboBoxQuincena.setItems("Primera Quincena", "Segunda Quincena");
        comboBoxQuincena.setWidth("250px");

        //Combo Empleados
        comboBoxEmpleados.setItems(dataProvider);
        comboBoxEmpleados.setItemLabelGenerator(e -> e.getNombres() + " " + e.getApellidos());
        comboBoxEmpleados.setPlaceholder("Buscar empleado...");

        // Botón Aceptar
        Button buttonAceptar = new Button("Aceptar");
        buttonAceptar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Botón Imprimir
        Button buttonImprimir = new Button("Imprimir Planilla");

        layoutRow.add(comboBoxQuincena, comboBoxMes, comboBoxAnio, comboBoxEmpleados, buttonAceptar, buttonImprimir);
        layoutRow.setAlignItems(FlexComponent.Alignment.END);


        // Grid de resultados
        Grid<ReportePlanillaItem> grid = new Grid<>(ReportePlanillaItem.class, false);
        grid.addColumn(ReportePlanillaItem::getEmpleado).setHeader("Empleado");
        grid.addColumn(ReportePlanillaItem::getCargo).setHeader("Cargo");
        grid.addColumn(ReportePlanillaItem::getSalarioMensual).setHeader("Salario Mensual");
        grid.addColumn(ReportePlanillaItem::getVacaciones).setHeader("Vacaciones");
        grid.addColumn(ReportePlanillaItem::getHorasExtrasDiurnas).setHeader("Horas Extras Diurnas");
        grid.addColumn(ReportePlanillaItem::getHorasExtrasNocturnas).setHeader("Horas Extras Nocturnas");
        grid.addColumn(ReportePlanillaItem::getMontoHoraDiurna).setHeader("Monto por Hora Diurna");
        grid.addColumn(ReportePlanillaItem::getMontoHoraNocturna).setHeader("Monto Hora Nocturna");
        grid.addColumn(ReportePlanillaItem::getAguinaldo).setHeader("Aguinaldo");
        grid.addColumn(ReportePlanillaItem::getSalarioGravado).setHeader("Salario Gravado");
        grid.addColumn(ReportePlanillaItem::getIsssPatronal).setHeader("ISSS Patronal");
        grid.addColumn(ReportePlanillaItem::getIsssEmpleado).setHeader("ISSS Empleado");
        grid.addColumn(ReportePlanillaItem::getAfpPatronal).setHeader("AFP Patronal");
        grid.addColumn(ReportePlanillaItem::getAfpEmpleado).setHeader("AFP Empleado");
        grid.addColumn(ReportePlanillaItem::getSalarioDevengado).setHeader("Salario Devengado");

        grid.setWidthFull();

        // Layout principal
        getContent().add(layoutRow, grid);
    }

    // Clase interna para representar los datos del grid
    public static class ReportePlanillaItem {
        private String empleado;
        private String cargo;
        private double salarioMensual;
        private double vacaciones;
        private double horasExtrasDiurnas;
        private double horasExtrasNocturnas;
        private double montoHoraDiurna;
        private double montoHoraNocturna;
        private double aguinaldo;
        private double salarioGravado;
        private double isssPatronal;
        private double isssEmpleado;
        private double afpPatronal;
        private double afpEmpleado;
        private double salarioDevengado;

        // Getters
        public String getEmpleado() { return empleado; }
        public String getCargo() { return cargo; }
        public double getSalarioMensual() { return salarioMensual; }
        public double getVacaciones() { return vacaciones; }
        public double getHorasExtrasDiurnas() { return horasExtrasDiurnas; }
        public double getHorasExtrasNocturnas() { return horasExtrasNocturnas; }
        public double getMontoHoraDiurna() { return montoHoraDiurna; }
        public double getMontoHoraNocturna() { return montoHoraNocturna; }
        public double getAguinaldo() { return aguinaldo; }
        public double getSalarioGravado() { return salarioGravado; }
        public double getIsssPatronal() { return isssPatronal; }
        public double getIsssEmpleado() { return isssEmpleado; }
        public double getAfpPatronal() { return afpPatronal; }
        public double getAfpEmpleado() { return afpEmpleado; }
        public double getSalarioDevengado() { return salarioDevengado; }

        // Constructor vacío
        public ReportePlanillaItem() {}

        // Opcional: constructor con parámetros
        public ReportePlanillaItem(String empleado, String cargo, double salarioMensual, double vacaciones,
                                   double horasExtrasDiurnas, double horasExtrasNocturnas,
                                   double montoHoraDiurna, double montoHoraNocturna, double aguinaldo,
                                   double salarioGravado, double isssPatronal, double isssEmpleado,
                                   double afpPatronal, double afpEmpleado, double salarioDevengado) {
            this.empleado = empleado;
            this.cargo = cargo;
            this.salarioMensual = salarioMensual;
            this.vacaciones = vacaciones;
            this.horasExtrasDiurnas = horasExtrasDiurnas;
            this.horasExtrasNocturnas = horasExtrasNocturnas;
            this.montoHoraDiurna = montoHoraDiurna;
            this.montoHoraNocturna = montoHoraNocturna;
            this.aguinaldo = aguinaldo;
            this.salarioGravado = salarioGravado;
            this.isssPatronal = isssPatronal;
            this.isssEmpleado = isssEmpleado;
            this.afpPatronal = afpPatronal;
            this.afpEmpleado = afpEmpleado;
            this.salarioDevengado = salarioDevengado;
        }
    }

    private String obtenerMesActual() {
        int mes = LocalDate.now().getMonthValue();
        return switch (mes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "";
        };
    }

    private List<Integer> generarAnios() {
        int anioActual = LocalDate.now().getYear();
        List<Integer> anios = new ArrayList<>();
        for (int i = anioActual - 5; i <= anioActual; i++) {
            anios.add(i);
        }
        return anios;
    }

}

