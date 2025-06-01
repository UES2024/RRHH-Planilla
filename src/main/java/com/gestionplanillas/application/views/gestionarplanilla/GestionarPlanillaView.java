package com.gestionplanillas.application.views.gestionarplanilla;


import com.gestionplanillas.application.data.Empleado;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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


@PermitAll
@PageTitle("Gestionar Planilla")
@Route("view-gestionar-planilla")
@Menu(order = 3, icon = LineAwesomeIconUrl.MONEY_BILL_WAVE_SOLID)
@Uses(Icon.class)
public class GestionarPlanillaView extends Composite<VerticalLayout> {

    public GestionarPlanillaView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        ComboBox comboBox = new ComboBox();
        ComboBox comboBox2 = new ComboBox();
        DatePicker datePicker = new DatePicker();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        Button buttonPrimary = new Button();
        HorizontalLayout layoutRow3 = new HorizontalLayout();
        H5 h5 = new H5();
        HorizontalLayout layoutRow4 = new HorizontalLayout();
        Button buttonSecondary = new Button();
        Button buttonSecondary2 = new Button();
        HorizontalLayout layoutRow5 = new HorizontalLayout();
        Grid basicGrid = new Grid(Empleado.class);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutRow.setAlignItems(Alignment.START);
        layoutRow.setJustifyContentMode(JustifyContentMode.CENTER);
        comboBox.setLabel("Código del Empleado");
        comboBox.setWidth("min-content");
        setComboBoxSampleData(comboBox);
        comboBox2.setLabel("Tipo de Jornada");
        comboBox2.setWidth("min-content");
        setComboBoxSampleData(comboBox2);
        datePicker.setLabel("Fecha");
        datePicker.setWidth("min-content");
        layoutRow2.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        layoutRow2.setAlignItems(Alignment.START);
        layoutRow2.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonPrimary.setText("Calcular Planilla");
        layoutRow2.setAlignSelf(FlexComponent.Alignment.CENTER, buttonPrimary);
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layoutRow3.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow3);
        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.setHeight("min-content");
        layoutRow3.setAlignItems(Alignment.START);
        layoutRow3.setJustifyContentMode(JustifyContentMode.CENTER);
        h5.setText("Planilla generada con Éxito");
        h5.setWidth("max-content");
        layoutRow4.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow4);
        layoutRow4.addClassName(Gap.MEDIUM);
        layoutRow4.setWidth("100%");
        layoutRow4.getStyle().set("flex-grow", "1");
        layoutRow4.setAlignItems(Alignment.START);
        layoutRow4.setJustifyContentMode(JustifyContentMode.END);
        buttonSecondary.setText("Exportar a Excel");
        buttonSecondary.setWidth("145px");
        buttonSecondary2.setText("Imprimir");
        buttonSecondary2.setWidth("145px");
        layoutRow5.setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow5);
        layoutRow5.addClassName(Gap.MEDIUM);
        layoutRow5.setWidth("100%");
        layoutRow5.getStyle().set("flex-grow", "1");
        basicGrid.setWidth("100%");
        basicGrid.getStyle().set("flex-grow", "0");
        //setGridSampleData(basicGrid);
        getContent().add(layoutRow);
        layoutRow.add(comboBox);
        layoutRow.add(comboBox2);
        layoutRow.add(datePicker);
        getContent().add(layoutRow2);
        layoutRow2.add(buttonPrimary);
        getContent().add(layoutRow3);
        layoutRow3.add(h5);
        getContent().add(layoutRow4);
        layoutRow4.add(buttonSecondary);
        layoutRow4.add(buttonSecondary2);
        getContent().add(layoutRow5);
        layoutRow5.add(basicGrid);
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

   /*  private void setGridSampleData(Grid grid) {
        grid.setItems(query -> samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
    }

    @Autowired()
    private SamplePersonService samplePersonService;*/
}
