package com.askaribank;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginController {

    private static final Color PAGE_BACKGROUND = Color.rgb(248, 251, 255);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color ASKARI_BLUE = Color.rgb(20, 91, 178);
    private static final Color ASKARI_DARK_BLUE = Color.rgb(11, 58, 135);
    private static final Color SOFT_BLUE = Color.rgb(232, 243, 255);
    private static final Color SOFT_BORDER = Color.rgb(207, 218, 232);
    private static final Color TEXT_DARK = Color.rgb(34, 45, 62);
    private static final Color TEXT_MUTED = Color.rgb(98, 109, 126);
    private static final CornerRadii FIELD_RADIUS = new CornerRadii(18);
    private static final CornerRadii BUTTON_RADIUS = new CornerRadii(18);

    private BorderPane root;
    private TextField loginIdField;
    private PasswordField passwordField;
    private TextField visiblePasswordField;
    private Button passwordVisibilityButton;
    private Button loginButton;
    private Label validationLabel;

    private boolean customerMode = true;
    private boolean passwordVisible = false;
    private AppMode appMode = AppMode.CUSTOMER;

    public Parent createView() {
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        root.setOnKeyPressed(event -> {
            if (appMode == AppMode.CUSTOMER && event.isControlDown() && event.isShiftDown()
                    && event.getCode() == KeyCode.S) {
                showStaffLoginScreen();
                event.consume();
            }
        });

        showCustomerLoginScreen();
        return root;
    }

    private void showCustomerLoginScreen() {
        appMode = AppMode.CUSTOMER;
        customerMode = true;
        passwordVisible = false;
        VBox content = new VBox(14);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(20, 36, 24, 36));
        content.setMaxWidth(520);

        content.getChildren().addAll(
                createWelcomeRow(),
                createLogoBlock(),
                createCredentialFields(),
                createPrimaryActions(),
                createTermsText(),
                createQuickLinksSection(),
                createBiometricButton(),
                createStaffAccessFooterLink(),
                createDemoHint()
        );

        setLoginContent(content);
        initializeCustomerLogin();
    }

    private void showStaffLoginScreen() {
        appMode = AppMode.STAFF;
        customerMode = false;
        passwordVisible = false;

        VBox content = new VBox(14);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(42, 48, 36, 48));
        content.setMaxWidth(470);

        ImageView logoImageView = UiFactory.logo(190, 72);

        Label headingLabel = new Label("Staff Access");
        headingLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headingLabel.setTextFill(ASKARI_DARK_BLUE);

        Label subtitleLabel = new Label("Authorized bank staff only");
        subtitleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        subtitleLabel.setTextFill(Color.rgb(150, 55, 45));

        loginIdField = UiFactory.textField("Staff username");
        VBox usernameBox = createTextFieldBox("✉", loginIdField, new Region());

        passwordField = UiFactory.passwordField("Staff password");
        visiblePasswordField = UiFactory.textField("Staff password");
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        passwordVisibilityButton = createLinkButton("Show", this::togglePasswordVisibility);
        StackPane passwordStack = new StackPane(passwordField, visiblePasswordField);
        HBox.setHgrow(passwordStack, Priority.ALWAYS);
        VBox passwordBox = createPasswordFieldBox("⌕", passwordStack, passwordVisibilityButton, new Region());

        validationLabel = UiFactory.messageLabel();
        validationLabel.setMaxWidth(430);
        validationLabel.setTextFill(Color.rgb(176, 50, 42));

        loginButton = createFilledButton("Login");
        loginButton.setOnAction(this::handleLogin);
        loginButton.setMaxWidth(430);

        Button backButton = createOutlineButton("Back to Customer Login");
        backButton.setOnAction(event -> showCustomerLoginScreen());
        backButton.setMaxWidth(430);

        Label warningLabel = new Label("Staff portal is restricted to authorized bank operators.");
        warningLabel.setFont(Font.font(11));
        warningLabel.setTextFill(TEXT_MUTED);
        warningLabel.setWrapText(true);
        warningLabel.setAlignment(Pos.CENTER);

        content.getChildren().addAll(
                logoImageView,
                headingLabel,
                subtitleLabel,
                usernameBox,
                passwordBox,
                validationLabel,
                loginButton,
                backButton,
                warningLabel
        );

        setLoginContent(content);
        initializeStaffLogin();
    }

    private void setLoginContent(VBox content) {
        StackPane centeredContent = new StackPane(content);
        centeredContent.setPadding(new Insets(0, 16, 0, 16));

        ScrollPane scrollPane = new ScrollPane(centeredContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane.setBorder(Border.EMPTY);
        root.setCenter(scrollPane);
    }

    private void initializeCustomerLogin() {
        loginIdField.setPromptText("Username");
        passwordField.setPromptText("Password");
        visiblePasswordField.setPromptText("Password");
        validationLabel.setText("");
        loginIdField.setOnAction(this::handleLogin);
        passwordField.setOnAction(this::handleLogin);
        visiblePasswordField.setOnAction(this::handleLogin);
    }

    private void initializeStaffLogin() {
        validationLabel.setText("");
        loginIdField.setOnAction(this::handleLogin);
        passwordField.setOnAction(this::handleLogin);
        visiblePasswordField.setOnAction(this::handleLogin);
        loginIdField.requestFocus();
    }

    private HBox createWelcomeRow() {
        HBox row = UiFactory.hbox(12, Pos.TOP_CENTER);
        row.setPadding(new Insets(4, 0, 0, 0));

        Label welcomeLabel = new Label("Welcome");
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        welcomeLabel.setTextFill(TEXT_DARK);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox languageBox = new VBox(4);
        languageBox.setAlignment(Pos.TOP_RIGHT);
        HBox languageRow = UiFactory.hbox(6, Pos.CENTER_RIGHT);
        Button englishButton = createTinyPillButton("English", true);
        Button urduButton = createTinyPillButton("اردو", false);
        englishButton.setOnAction(event -> AlertHelper.showInfo("Language", "English is already selected."));
        urduButton.setOnAction(event -> AlertHelper.showInfo("Language", "Urdu language simulation will be available soon."));
        languageRow.getChildren().addAll(englishButton, urduButton);

        Label versionLabel = new Label("Version 2.3.0");
        versionLabel.setFont(Font.font(11));
        versionLabel.setTextFill(TEXT_MUTED);
        languageBox.getChildren().addAll(languageRow, versionLabel);

        row.getChildren().addAll(welcomeLabel, spacer, languageBox);
        return row;
    }

    private VBox createLogoBlock() {
        VBox logoBlock = new VBox(5);
        logoBlock.setAlignment(Pos.CENTER);
        logoBlock.setPadding(new Insets(12, 0, 8, 0));

        ImageView logoImageView = UiFactory.logo(230, 102);
        Label digitalLabel = new Label("Askari Digital");
        digitalLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        digitalLabel.setTextFill(ASKARI_DARK_BLUE);

        Label subLabel = new Label("Secure mobile banking experience");
        subLabel.setFont(Font.font(12));
        subLabel.setTextFill(TEXT_MUTED);

        logoBlock.getChildren().addAll(logoImageView, digitalLabel, subLabel);
        return logoBlock;
    }

    private VBox createCredentialFields() {
        VBox fields = new VBox(9);
        fields.setAlignment(Pos.CENTER);

        loginIdField = UiFactory.textField("Username");
        VBox usernameBox = createTextFieldBox("✉", loginIdField, createLinkButton("Forgot Username?",
                () -> AlertHelper.showInfo("Forgot Username", "Please contact Askari support or visit your branch to recover your username.")));

        passwordField = UiFactory.passwordField("Password");
        visiblePasswordField = UiFactory.textField("Password");
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        passwordVisibilityButton = createLinkButton("Show", this::togglePasswordVisibility);
        StackPane passwordStack = new StackPane(passwordField, visiblePasswordField);
        HBox.setHgrow(passwordStack, Priority.ALWAYS);
        VBox passwordBox = createPasswordFieldBox("⌕", passwordStack, passwordVisibilityButton,
                createLinkButton("Forgot Password?",
                        () -> AlertHelper.showInfo("Forgot Password", "For your safety, password reset is simulated. Please verify your account through support.")));

        validationLabel = UiFactory.messageLabel();
        validationLabel.setMaxWidth(430);
        validationLabel.setTextFill(Color.rgb(176, 50, 42));

        fields.getChildren().addAll(usernameBox, passwordBox, validationLabel);
        return fields;
    }

    private VBox createTextFieldBox(String icon, TextField field, Node linkNode) {
        configurePlainField(field);

        HBox fieldRow = createFieldShell();
        Label iconLabel = createFieldIcon(icon);
        HBox.setHgrow(field, Priority.ALWAYS);
        fieldRow.getChildren().addAll(iconLabel, field);

        HBox linkRow = UiFactory.hbox(0, Pos.CENTER_RIGHT);
        linkRow.getChildren().add(linkNode);

        VBox box = new VBox(2, fieldRow, linkRow);
        box.setMaxWidth(430);
        return box;
    }

    private VBox createPasswordFieldBox(String icon, StackPane fieldStack, Button trailingButton, Node linkNode) {
        configurePlainField(passwordField);
        configurePlainField(visiblePasswordField);

        HBox fieldRow = createFieldShell();
        Label iconLabel = createFieldIcon(icon);
        fieldRow.getChildren().addAll(iconLabel, fieldStack, trailingButton);

        HBox linkRow = UiFactory.hbox(0, Pos.CENTER_RIGHT);
        linkRow.getChildren().add(linkNode);

        VBox box = new VBox(2, fieldRow, linkRow);
        box.setMaxWidth(430);
        return box;
    }

    private HBox createFieldShell() {
        HBox fieldRow = UiFactory.hbox(9, Pos.CENTER_LEFT);
        fieldRow.setPadding(new Insets(7, 12, 7, 12));
        fieldRow.setMinHeight(46);
        fieldRow.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, FIELD_RADIUS, Insets.EMPTY)));
        fieldRow.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                FIELD_RADIUS, new BorderWidths(1))));
        return fieldRow;
    }

    private Label createFieldIcon(String icon) {
        Label iconLabel = new Label(icon);
        iconLabel.setMinWidth(24);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        iconLabel.setTextFill(ASKARI_BLUE);
        return iconLabel;
    }

    private void configurePlainField(TextField field) {
        field.setBorder(Border.EMPTY);
        field.setBackground(Background.EMPTY);
        field.setFont(Font.font(14));
        field.setMinHeight(30);
        field.setMaxWidth(Double.MAX_VALUE);
    }

    private HBox createPrimaryActions() {
        HBox row = UiFactory.hbox(12, Pos.CENTER);
        row.setMaxWidth(430);

        Button registerButton = createOutlineButton("Register");
        registerButton.setOnAction(event -> showAccountApplicationForm());

        loginButton = createFilledButton("Login");
        loginButton.setOnAction(this::handleLogin);

        HBox.setHgrow(registerButton, Priority.ALWAYS);
        HBox.setHgrow(loginButton, Priority.ALWAYS);
        row.getChildren().addAll(registerButton, loginButton);
        return row;
    }

    private Label createTermsText() {
        Label termsLabel = new Label("By signing in you are agreeing to our T&C's");
        termsLabel.setFont(Font.font(12));
        termsLabel.setTextFill(TEXT_MUTED);
        termsLabel.setAlignment(Pos.CENTER);
        return termsLabel;
    }

    private VBox createQuickLinksSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(6, 0, 0, 0));

        HBox quickLinks = UiFactory.hbox(8, Pos.CENTER);
        quickLinks.getChildren().addAll(
                createLoginQuickLink("＋", "Open Click\nAccount", this::showAccountApplicationForm),
                createLoginQuickLink("↗", "Apply\nOnline", this::showAccountApplicationForm),
                createLoginQuickLink("☏", "Whatsapp\nBanking", () -> AlertHelper.showInfo("Whatsapp Banking",
                        "WhatsApp banking simulation: send Hi to Askari support from your registered mobile number.")),
                createLoginQuickLink("％", "Card\nDiscounts", () -> AlertHelper.showInfo("Card Discounts",
                        "Card discount offers are available after customer login in Discounts & Promotions.")),
                createLoginQuickLink("?", "Need\nAssistance?", () -> AlertHelper.showInfo("Need Assistance?",
                        "For this desktop project, support is simulated through the customer support module."))
        );

        section.getChildren().add(quickLinks);
        return section;
    }

    private Button createBiometricButton() {
        Button button = new Button();

        Label iconLabel = new Label("◎");
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        iconLabel.setTextFill(ASKARI_BLUE);

        Label label = new Label("Biometric");
        label.setFont(Font.font("System", FontWeight.BOLD, 11));
        label.setTextFill(TEXT_MUTED);

        VBox content = new VBox(1, iconLabel, label);
        content.setAlignment(Pos.CENTER);
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setPadding(new Insets(8));
        button.setMinSize(96, 78);
        button.setPrefSize(96, 78);
        button.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, new CornerRadii(28), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                new CornerRadii(28), new BorderWidths(1))));
        button.setOnAction(this::handleBiometricLogin);
        return button;
    }

    private Button createStaffAccessFooterLink() {
        Button staffAccessButton = createLinkButton("Staff Access", this::showStaffLoginScreen);
        staffAccessButton.setFont(Font.font("System", FontWeight.BOLD, 10));
        staffAccessButton.setTextFill(TEXT_MUTED);
        return staffAccessButton;
    }

    private Label createDemoHint() {
        Label demoLabel = new Label("Demo customer: ASK-2026-00001 / 1234");
        demoLabel.setFont(Font.font(11));
        demoLabel.setTextFill(TEXT_MUTED);
        demoLabel.setWrapText(true);
        demoLabel.setAlignment(Pos.CENTER);
        return demoLabel;
    }

    private Button createLoginQuickLink(String icon, String title, Runnable action) {
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        iconLabel.setTextFill(Color.WHITE);
        iconLabel.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 9));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(70);

        VBox content = new VBox(3, iconLabel, titleLabel);
        content.setAlignment(Pos.CENTER);

        Button button = new Button();
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setMinSize(76, 68);
        button.setPrefSize(76, 68);
        button.setPadding(new Insets(6));
        button.setBackground(new Background(new BackgroundFill(ASKARI_BLUE, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(ASKARI_DARK_BLUE, BorderStrokeStyle.SOLID,
                new CornerRadii(8), new BorderWidths(1))));
        button.setOnAction(event -> action.run());
        return button;
    }

    private Button createTinyPillButton(String text, boolean selected) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 11));
        button.setPadding(new Insets(3, 9, 3, 9));
        button.setTextFill(selected ? Color.WHITE : ASKARI_BLUE);
        button.setBackground(new Background(new BackgroundFill(selected ? ASKARI_BLUE : CARD_BACKGROUND,
                new CornerRadii(12), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(ASKARI_BLUE, BorderStrokeStyle.SOLID,
                new CornerRadii(12), new BorderWidths(1))));
        return button;
    }

    private Button createLinkButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 11));
        button.setTextFill(ASKARI_BLUE);
        button.setPadding(new Insets(2, 4, 2, 4));
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);
        button.setOnAction(event -> action.run());
        return button;
    }

    private Button createFilledButton(String text) {
        Button button = UiFactory.button(text);
        button.setMinHeight(42);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(ASKARI_BLUE, BUTTON_RADIUS, Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(ASKARI_DARK_BLUE, BorderStrokeStyle.SOLID,
                BUTTON_RADIUS, new BorderWidths(1))));
        return button;
    }

    private Button createOutlineButton(String text) {
        Button button = UiFactory.button(text);
        button.setMinHeight(42);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setTextFill(ASKARI_BLUE);
        button.setBackground(new Background(new BackgroundFill(SOFT_BLUE, BUTTON_RADIUS, Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(ASKARI_BLUE, BorderStrokeStyle.SOLID,
                BUTTON_RADIUS, new BorderWidths(1))));
        return button;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);
        visiblePasswordField.setVisible(passwordVisible);
        visiblePasswordField.setManaged(passwordVisible);
        passwordVisibilityButton.setText(passwordVisible ? "Hide" : "Show");
    }

    private void handleLogin(ActionEvent event) {
        String username = loginIdField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showValidation("Please enter username and password/PIN.");
            AlertHelper.showError("Login Error", "Please enter username and password/PIN.");
            return;
        }

        if (customerMode) {
            handleCustomerLogin(event, username, password);
        } else {
            handleStaffLogin(event, username, password);
        }
    }

    private void handleStaffLogin(ActionEvent event, String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) {
            LoginSession.loginStaff();
            AuditLogger.log("STAFF_LOGIN", "Staff admin logged in");
            AuditLogger.log("LOGIN_SUCCESS", "Staff login successful");
            openView(event, BankScreens.staffDashboard());
        } else {
            AuditLogger.log("LOGIN_FAILED", "Staff login failed for " + username);
            showValidation("Invalid staff username or password.");
            AlertHelper.showError("Login Failed", "Invalid staff username or password.");
        }
    }

    private void handleCustomerLogin(ActionEvent event, String loginId, String pin) {
        Customer customer = BankDataStore.findCustomerByAccountOrCnic(loginId);
        if (customer != null && customer.isLocked()) {
            AuditLogger.log("LOGIN_BLOCKED", "Locked customer login attempted for " + customer.getAccountNumber());
            showValidation("This customer account is locked. Please contact branch staff.");
            AlertHelper.showError("Account Locked", "This customer account is locked. Please contact branch staff.");
            return;
        }

        if (customer != null && pin.equals(customer.getAtmPin())) {
            loginCustomer(event, customer);
        } else {
            if (customer != null) {
                customer.incrementFailedLoginAttempts();
                if (customer.getFailedLoginAttempts() >= 3) {
                    customer.setLocked(true);
                    AuditLogger.log("ACCOUNT_LOCKED", customer.getAccountNumber() + " locked after failed login attempts");
                    NotificationService.addNotification(customer.getAccountNumber(),
                            "Your account was locked after 3 failed login attempts. Please contact branch staff.");
                }
                FileDataStore.saveAll();
            }
            AuditLogger.log("LOGIN_FAILED", "Customer login failed for " + loginId);
            showValidation("Invalid username/account/CNIC or PIN.");
            AlertHelper.showError("Login Failed", "Invalid username/account/CNIC or PIN.");
        }
    }

    private void handleBiometricLogin(ActionEvent event) {
        if (!customerMode) {
            AlertHelper.showInfo("Biometric Login", "Biometric login is available for customer access only.");
            return;
        }

        String loginId = loginIdField == null ? "" : loginIdField.getText().trim();
        Customer customer = BankDataStore.findCustomerByAccountOrCnic(loginId);
        String message = "Biometric login is not enrolled for this customer. Please login with Account/CNIC and PIN first.";

        if (customer == null) {
            showValidation(message);
        } else {
            showValidation(message);
            AuditLogger.log("BIOMETRIC_LOGIN_NOT_ENROLLED", customer.getAccountNumber());
        }

        AlertHelper.showInfo("Biometric Verification", message);
    }

    private void showAccountApplicationForm() {
        appMode = AppMode.CUSTOMER;
        customerMode = true;
        passwordVisible = false;

        VBox content = new VBox(14);
        content.setAlignment(Pos.TOP_CENTER);
        content.setPadding(new Insets(24, 42, 28, 42));
        content.setMaxWidth(560);

        Label titleLabel = new Label("Apply Online");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setTextFill(ASKARI_DARK_BLUE);

        Label detailLabel = new Label("Submit a demo account application for staff verification.");
        detailLabel.setFont(Font.font(13));
        detailLabel.setTextFill(TEXT_MUTED);
        detailLabel.setWrapText(true);

        TextField fullNameField = UiFactory.textField("Full Name");
        TextField cnicField = UiFactory.textField("CNIC - 13 digits");
        TextField mobileField = UiFactory.textField("Mobile Number");
        TextField emailField = UiFactory.textField("Email");
        TextField cityField = UiFactory.textField("City");
        ComboBox<String> accountTypeCombo = UiFactory.comboBox("Account Type");
        accountTypeCombo.getItems().addAll("Savings", "Current");

        GridPane formGrid = UiFactory.grid(12, 10);
        formGrid.setMaxWidth(500);
        formGrid.add(new Label("Full Name"), 0, 0);
        UiFactory.addGrowing(formGrid, fullNameField, 1, 0);
        formGrid.add(new Label("CNIC"), 0, 1);
        UiFactory.addGrowing(formGrid, cnicField, 1, 1);
        formGrid.add(new Label("Mobile Number"), 0, 2);
        UiFactory.addGrowing(formGrid, mobileField, 1, 2);
        formGrid.add(new Label("Email"), 0, 3);
        UiFactory.addGrowing(formGrid, emailField, 1, 3);
        formGrid.add(new Label("City"), 0, 4);
        UiFactory.addGrowing(formGrid, cityField, 1, 4);
        formGrid.add(new Label("Account Type"), 0, 5);
        UiFactory.addGrowing(formGrid, accountTypeCombo, 1, 5);

        Label applicationMessageLabel = UiFactory.messageLabel();
        applicationMessageLabel.setMaxWidth(500);

        Button submitButton = createFilledButton("Submit Application");
        submitButton.setOnAction(event -> submitAccountApplication(fullNameField, cnicField,
                mobileField, emailField, cityField, accountTypeCombo, applicationMessageLabel));

        Button backButton = createOutlineButton("Back to Login");
        backButton.setOnAction(event -> showCustomerLoginScreen());

        HBox actions = UiFactory.hbox(12, Pos.CENTER);
        HBox.setHgrow(backButton, Priority.ALWAYS);
        HBox.setHgrow(submitButton, Priority.ALWAYS);
        actions.setMaxWidth(500);
        actions.getChildren().addAll(backButton, submitButton);

        Label noteLabel = new Label("Submitting this form does not create an active bank account or login PIN.");
        noteLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        noteLabel.setTextFill(TEXT_MUTED);
        noteLabel.setWrapText(true);
        noteLabel.setAlignment(Pos.CENTER);

        content.getChildren().addAll(
                UiFactory.logo(190, 72),
                titleLabel,
                detailLabel,
                formGrid,
                applicationMessageLabel,
                actions,
                noteLabel
        );

        setLoginContent(content);
    }

    private void submitAccountApplication(TextField fullNameField, TextField cnicField,
                                          TextField mobileField, TextField emailField,
                                          TextField cityField, ComboBox<String> accountTypeCombo,
                                          Label applicationMessageLabel) {
        try {
            AccountApplication application = new AccountApplicationService().submitApplication(
                    fullNameField.getText(),
                    cnicField.getText(),
                    mobileField.getText(),
                    emailField.getText(),
                    cityField.getText(),
                    accountTypeCombo.getValue());
            String message = "Your application has been submitted for staff verification. "
                    + "This does not create an active bank account yet.";
            applicationMessageLabel.setText(message + " Reference: " + application.getApplicationId());
            AlertHelper.showInfo("Application Submitted", message);
            fullNameField.clear();
            cnicField.clear();
            mobileField.clear();
            emailField.clear();
            cityField.clear();
            accountTypeCombo.getSelectionModel().clearSelection();
        } catch (IllegalArgumentException exception) {
            applicationMessageLabel.setText(exception.getMessage());
            AlertHelper.showError("Application Error", exception.getMessage());
        }
    }

    private void loginCustomer(ActionEvent event, Customer customer) {
        customer.setFailedLoginAttempts(0);
        LimitService.resetDailyUsageIfNeeded(customer);
        FileDataStore.saveAll();
        LoginSession.loginCustomer(customer);
        AuditLogger.log("CUSTOMER_LOGIN", customer.getAccountNumber() + " logged in");
        AuditLogger.log("LOGIN_SUCCESS", "Customer login successful for " + customer.getAccountNumber());
        openView(event, BankScreens.customerDashboard());
    }

    private void openView(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        BankScreens.replaceScene(stage, root);
    }

    private void showValidation(String message) {
        validationLabel.setText(message);
    }

    private enum AppMode {
        CUSTOMER,
        STAFF
    }
}
