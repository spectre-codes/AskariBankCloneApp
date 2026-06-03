package com.askaribank;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;

final class UiFactory {

    private static final String LOGO_RESOURCE = "/com/askaribank/askari-bank-logo.png";

    private UiFactory() {
    }

    static VBox page() {
        VBox page = new VBox(16);
        page.setPadding(new Insets(22));
        return page;
    }

    static ScrollPane scroll(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    static VBox sidebar(double width) {
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(width);
        sidebar.setPadding(new Insets(18, 16, 18, 16));
        return sidebar;
    }

    static HBox hbox(double spacing) {
        return new HBox(spacing);
    }

    static HBox hbox(double spacing, Pos alignment) {
        HBox box = new HBox(spacing);
        box.setAlignment(alignment);
        return box;
    }

    static GridPane grid(double hgap, double vgap) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(hgap);
        gridPane.setVgap(vgap);
        return gridPane;
    }

    static Label title(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 22));
        return label;
    }

    static Label largeTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 24));
        return label;
    }

    static Label headerTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 18));
        return label;
    }

    static Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 16));
        return label;
    }

    static Label value(String text, double size) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, size));
        return label;
    }

    static Label messageLabel() {
        Label label = new Label();
        label.setMinHeight(24);
        label.setWrapText(true);
        return label;
    }

    static TextField textField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    static PasswordField passwordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        return passwordField;
    }

    static <T> ComboBox<T> comboBox(String promptText) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setPromptText(promptText);
        comboBox.setMaxWidth(Double.MAX_VALUE);
        return comboBox;
    }

    static Button button(String text) {
        return new Button(text);
    }

    static Button fullWidthButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    static ToggleButton toggleButton(String text, double minWidth) {
        ToggleButton toggleButton = new ToggleButton(text);
        toggleButton.setMinWidth(minWidth);
        return toggleButton;
    }

    static TextArea textArea(double prefHeight) {
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setPrefHeight(prefHeight);
        textArea.setWrapText(false);
        return textArea;
    }

    static <S, T> TableColumn<S, T> column(String text, double width) {
        TableColumn<S, T> column = new TableColumn<>(text);
        column.setPrefWidth(width);
        return column;
    }

    @SafeVarargs
    static <S> TableView<S> table(double prefHeight, TableColumn<S, ?>... columns) {
        TableView<S> tableView = new TableView<>();
        tableView.setPrefHeight(prefHeight);
        tableView.getColumns().addAll(columns);
        return tableView;
    }

    static ImageView logo(double fitWidth, double fitHeight) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);

        URL logoUrl = Main.class.getResource(LOGO_RESOURCE);
        if (logoUrl != null) {
            imageView.setImage(new Image(logoUrl.toExternalForm()));
        }
        return imageView;
    }

    static Separator separator() {
        return new Separator();
    }

    static Region verticalSpacer() {
        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        return region;
    }

    static void growHorizontally(Node node) {
        GridPane.setHgrow(node, Priority.ALWAYS);
        if (node instanceof Control control) {
            control.setMaxWidth(Double.MAX_VALUE);
        }
    }

    static void add(GridPane gridPane, Node node, int column, int row) {
        gridPane.add(node, column, row);
    }

    static void addGrowing(GridPane gridPane, Node node, int column, int row) {
        growHorizontally(node);
        gridPane.add(node, column, row);
    }
}
