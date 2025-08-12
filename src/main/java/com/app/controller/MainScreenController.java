package com.app.controller;

import com.app.config.SystemProperties;
import com.app.model.*;
import com.app.repository.FertilizationHistoryRepository;
import com.app.repository.PlotRepository;
import com.app.service.DispenserService;
import com.app.service.FertilizerService;
import com.app.service.MonitoringService;
import com.app.service.VarietyService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainScreenController {

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private FertilizationHistoryRepository historyRepo;

    @Autowired
    private FertilizerService fertilizerService;

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private VarietyService varietyService;

    @Autowired
    private PlotRepository plotRepo;

    @Autowired
    private DispenserService dispenserService;

    @FXML private Label statusLabel;
    @FXML private VBox dynamicPanel;

    private Plot currentPlot;
    private boolean systemEnabled = false;
    private List<Plot> availablePlots = new ArrayList<>();

    @FXML
    public void initialize() {
        availablePlots = plotRepo.findAll();
        updateStatus();
    }


    private void appendLog(String log) {
        Platform.runLater(() -> {
            Label logLabel = new Label(log);
            logLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
            dynamicPanel.getChildren().add(logLabel);
        });
    }

    private void updateStatus() {
        statusLabel.setText(systemEnabled ? "Włączony" : "Wyłączony");
        statusLabel.setStyle(systemEnabled
                ? "-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px;"
                : "-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 8px;");
    }

    private void clearDynamicPanel() {
        dynamicPanel.getChildren().clear();
    }

    private void refreshLogs() {
        List<String> newLogs = monitoringService.getLastLogsAndClear();
        newLogs.forEach(this::appendLog);
    }

    @FXML
    private void onEnableSystem() {
        clearDynamicPanel();
        ComboBox<Plot> plotCombo = new ComboBox<>();
        plotCombo.getItems().addAll(availablePlots);
        Button startButton = new Button("Start");
        startButton.setDisable(true);
        plotCombo.valueProperty().addListener((obs,o,n) -> startButton.setDisable(n==null));
        startButton.setOnAction(e -> {
            currentPlot = plotCombo.getValue();
            dispenserService.setStatusByPlot(currentPlot.getId(), DispenserStatus.ACTIVE);
            systemEnabled = true;
            updateStatus();
            monitoringService.startContinuousMonitoring(plotCombo.getValue(), this::refreshLogs);
        });
        dynamicPanel.getChildren().addAll(new Label("Wybierz kwaterę:"), plotCombo, startButton);
    }

    @FXML
    private void onDisableSystem() {
        if (currentPlot != null) {
            dispenserService.setStatusByPlot(currentPlot.getId(), DispenserStatus.INACTIVE);
        }
        systemEnabled = false;
        updateStatus();
        monitoringService.stopContinuousMonitoring();
        Label info = new Label("Monitoring został wyłączony.");
        dynamicPanel.getChildren().setAll(info);
    }

    @FXML
    private void onAddFertilizer() {
        clearDynamicPanel();
        TextField nameField = new TextField();
        ComboBox<String> ingredientCombo = new ComboBox<>();
        ingredientCombo.getItems().addAll("N","P","K");
        TextField minField = new TextField();
        Button save = new Button("Zapisz");
        save.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty()
                    || ingredientCombo.getValue() == null
                    || minField.getText().trim().isEmpty()) {
                showError("Proszę wypełnić wszystkie pola.");
                return;
            }
            double minValue;
            try {
                minValue = Double.parseDouble(minField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Nieprawidłowa wartość min. ilości.");
                return;
            }
            Fertilizer f = new Fertilizer();
            f.setName(nameField.getText());
            f.setIngredient(ingredientCombo.getValue());
            f.setMinThreshold(Double.parseDouble(minField.getText()));
            f.setAvailableAmount(1000);
            fertilizerService.saveAndAssignToAllDispensers(f);
            clearDynamicPanel();
            appendLog("Dodano nawóz: "+f.getName());
        });
        dynamicPanel.getChildren().addAll(
                new Label("Nazwa:"), nameField,
                new Label("Składnik:"), ingredientCombo,
                new Label("Min. ilość:"), minField,
                save
        );
    }

    @FXML
    private void onShowHistory() {
        clearDynamicPanel();

        Label title = new Label("Historia nawożenia");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<FertilizationHistory> table = new TableView<>();
        TableColumn<FertilizationHistory,String> dateCol = new TableColumn<>("Data");
        dateCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getApplicationDate().toString())
        );
        TableColumn<FertilizationHistory,String> nameCol = new TableColumn<>("Nawóz");
        nameCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFertilizerName())
        );
        TableColumn<FertilizationHistory,String> amountCol = new TableColumn<>("Ilość (kg)");
        amountCol.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().getAmount()))
        );
        TableColumn<FertilizationHistory,String> plotCol = new TableColumn<>("Kwatera");
        plotCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPlotName())
        );
        table.getColumns().addAll(dateCol, nameCol, amountCol, plotCol);

        List<FertilizationHistory> history = historyRepo.findAll();
        table.getItems().setAll(history);

        dynamicPanel.getChildren().addAll(title, table);
    }

    @FXML
    private void onAddVariety() {
        clearDynamicPanel();
        ComboBox<Plot> plotSelector = new ComboBox<>();
        plotSelector.getItems().setAll(availablePlots);
        Button nextButton = new Button("Dalej");
        nextButton.setDisable(true);
        plotSelector.valueProperty().addListener((obs, old, selected) ->
                nextButton.setDisable(selected == null)
        );
        nextButton.setOnAction(e -> showVarietyForm(plotSelector.getValue()));
        dynamicPanel.getChildren().addAll(
                new Label("Wybierz kwaterę:"), plotSelector, nextButton
        );
    }

    private void showVarietyForm(Plot selectedPlot) {
        clearDynamicPanel();
        TextField nameField = new TextField();
        TextArea descField = new TextArea();
        descField.setPrefRowCount(2);
        ComboBox<String> fruitCombo = new ComboBox<>();
        fruitCombo.getItems().addAll("Malina","Truskawka","Borówka");
        TextField nField = new TextField();
        TextField pField = new TextField();
        TextField kField = new TextField();
        TextField moistureField = new TextField();
        TextField phField = new TextField();
        Button save = new Button("Zapisz");
        save.setOnAction(ev -> {
            if (nameField.getText().trim().isEmpty()
                    || fruitCombo.getValue() == null
                    || nField.getText().trim().isEmpty()
                    || pField.getText().trim().isEmpty()
                    || kField.getText().trim().isEmpty()
                    || moistureField.getText().trim().isEmpty()
                    || phField.getText().trim().isEmpty()) {
                showError("Proszę wypełnić wszystkie pola.");
                return;
            }
            double requiredN, requiredP, requiredK, requiredMoisture, requiredPh;
            try {
                requiredN = Double.parseDouble(nField.getText().trim());
                requiredP = Double.parseDouble(pField.getText().trim());
                requiredK = Double.parseDouble(kField.getText().trim());
                requiredMoisture = Double.parseDouble(moistureField.getText().trim());
                requiredPh = Double.parseDouble(phField.getText().trim());
            } catch (NumberFormatException ex) {
                showError("Proszę podać poprawne wartości liczbowe.");
                return;
            }
            Variety v = new Variety();
            v.setName(nameField.getText());
            v.setDescription(descField.getText());
            v.setFruit(new Fruit(fruitCombo.getValue(), v));
            varietyService.addFullVarietyToPlot(
                    selectedPlot.getId(),
                    v,
                    systemProperties.getSeedlingsCount(),
                    Double.parseDouble(nField.getText()),
                    Double.parseDouble(pField.getText()),
                    Double.parseDouble(kField.getText()),
                    Double.parseDouble(moistureField.getText()),
                    Double.parseDouble(phField.getText()),
                    systemProperties.getInitial().getNutrient().getN(),
                    systemProperties.getInitial().getNutrient().getP(),
                    systemProperties.getInitial().getNutrient().getK(),
                    systemProperties.getInitial().getHumidity(),
                    systemProperties.getInitial().getPh()
            );
            clearDynamicPanel();
            appendLog("Dodano odmianę: " + v.getName());
        });
        dynamicPanel.getChildren().addAll(
                new Label("Nazwa odmiany:"), nameField,
                new Label("Opis:"), descField,
                new Label("Typ owocu:"), fruitCombo,
                new Label("Wymagane N:"), nField,
                new Label("Wymagane P:"), pField,
                new Label("Wymagane K:"), kField,
                new Label("Wymagana wilgotność:"), moistureField,
                new Label("Wymagane pH:"), phField,
                save
        );
    }

    @FXML
    private void onShowDispensersByFertilizer() {
        clearDynamicPanel();
        ComboBox<Fertilizer> fertCombo = new ComboBox<>();
        fertCombo.getItems().setAll(fertilizerService.findAll());
        fertCombo.setPromptText("Wybierz nawóz");

        TableView<Dispenser> table = new TableView<>();
        TableColumn<Dispenser,String> idCol = new TableColumn<>("ID dozownika");
        idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId().toString()));
        TableColumn<Dispenser,String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().name()));
        table.getColumns().addAll(idCol, statusCol);

        fertCombo.valueProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                table.getItems().setAll(dispenserService.findByFertilizer(sel.getId()));
            } else {
                table.getItems().clear();
            }
        });

        dynamicPanel.getChildren().addAll(
                new Label("Pokaż dozowniki dla nawozu:"), fertCombo, table
        );
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}