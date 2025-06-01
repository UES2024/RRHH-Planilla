package com.gestionplanillas.application.views.viewcontrolempleados;


import com.gestionplanillas.application.data.Empleado;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("View Control Empleados")
@Route("view-control-empleados")
@Menu(order = 1, icon = LineAwesomeIconUrl.ADDRESS_BOOK_SOLID)
@PermitAll
@Uses(Icon.class)
public class ViewControlEmpleadosView extends Composite<VerticalLayout> {

    public ViewControlEmpleadosView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        TextField textField = new TextField();
        TextField textField2 = new TextField();
        DatePicker datePicker = new DatePicker();
        ComboBox comboBox = new ComboBox();
        ComboBox comboBox2 = new ComboBox();
        Button buttonSecondary = new Button();
        Button buttonSecondary2 = new Button();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        Grid stripedGrid = new Grid(Empleado.class);
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        Button buttonPrimary = new Button();
        Button buttonPrimary2 = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutRow2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        layoutRow2.setAlignItems(Alignment.START);
        layoutRow2.setJustifyContentMode(JustifyContentMode.CENTER);
        textField.setLabel("Filtrar por Nombre");
        textField.setWidth("200px");
        textField2.setLabel("Filtrar por Apellido");
        textField2.setWidth("200px");
        datePicker.setLabel("Filtrar por Fecha de Ingreso");
        datePicker.setWidth("200px");
        comboBox.setLabel("Filtrar por Departamento");
        comboBox.setWidth("200px");
        setComboBoxSampleData(comboBox);
        comboBox2.setLabel("Filtrar por Puesto");
        comboBox2.setWidth("200px");
        setComboBoxSampleData(comboBox2);
        buttonSecondary.setText("Aplicar Filtros");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.CENTER, buttonSecondary);
        buttonSecondary.setWidth("min-content");
        buttonSecondary2.setText("Limpiar Filtros");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.CENTER, buttonSecondary2);
        buttonSecondary2.setWidth("min-content");
        layoutRow3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.getStyle().set("flex-grow", "1");
        stripedGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        stripedGrid.setWidth("100%");
        stripedGrid.getStyle().set("flex-grow", "0");
        setGridSampleData(stripedGrid);
        layoutRow4.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow4);
        layoutRow4.addClassName(Gap.MEDIUM);
        layoutRow4.setWidth("100%");
        layoutRow4.getStyle().set("flex-grow", "1");
        layoutRow4.setAlignItems(Alignment.START);
        layoutRow4.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonPrimary.setText("Agragar Nuevo Empleado");
        layoutRow4.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary);
        buttonPrimary.setWidth("262px");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("Editar Información de Empleado");
        layoutRow4.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary2);
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(layoutRow);
        layoutRow.add(layoutRow2);
        layoutRow2.add(textField);
        layoutRow2.add(textField2);
        layoutRow2.add(datePicker);
        layoutRow2.add(comboBox);
        layoutRow2.add(comboBox2);
        layoutRow2.add(buttonSecondary);
        layoutRow2.add(buttonSecondary2);
        getContent().add(layoutRow3);
        layoutRow3.add(stripedGrid);
        getContent().add(layoutRow4);
        layoutRow4.add(buttonPrimary);
        layoutRow4.add(buttonPrimary2);
    }

    record SampleItem(String value, String label, Boolean disabled) {
    }

    private void setComboBoxSampleData(ComboBox comboBox) {
        List<SampleItem> sampleItems = new ArrayList<>();
        sampleItems.add(new SampleItem("first", "First", null));
        sampleItems.add(new SampleItem("second", "Second", null));
        sampleItems.add(new SampleItem("third", "Third", Boolean.TRUE));
        sampleItems.add(new SampleItem("fourth", "Fourth", null));
        comboBox.setItems(sampleItems);
        comboBox.setItemLabelGenerator(item -> ((SampleItem) item).label());
    }

    private void setGridSampleData(Grid grid) {
        //grid.setItems(query -> samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
    }
}
