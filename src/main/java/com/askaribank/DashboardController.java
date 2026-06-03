package com.askaribank;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    private Label bankNameLabel;
    private ImageView sidebarLogoImageView;
    private ImageView headerLogoImageView;
    private Label welcomeLabel;
    private Label totalCustomersLabel;
    private Label totalBankBalanceLabel;
    private Label totalTransactionsLabel;
    private Label approvedLoansLabel;
    private StackPane contentArea;
    private VBox dashboardHome;

    public Parent createView() {
        BorderPane root = new BorderPane();
        root.setLeft(createSidebar());

        BorderPane mainArea = new BorderPane();
        mainArea.setTop(createHeader());
        contentArea = new StackPane();
        dashboardHome = createDashboardHome();
        mainArea.setCenter(contentArea);
        root.setCenter(mainArea);

        initialize();
        return root;
    }

    private VBox createSidebar() {
        VBox sidebar = UiFactory.sidebar(230);

        sidebarLogoImageView = UiFactory.logo(170, 58);
        bankNameLabel = UiFactory.headerTitle(BankDataStore.bankName);
        Label portalLabel = new Label("Staff Portal");

        Button dashboardButton = UiFactory.fullWidthButton("Dashboard");
        dashboardButton.setOnAction(event -> showDashboard());
        Button customersButton = UiFactory.fullWidthButton("Manage Customers");
        customersButton.setOnAction(event -> showAccountView());
        Button applicationsButton = UiFactory.fullWidthButton("Account Applications");
        applicationsButton.setOnAction(event -> showAccountApplicationsView());
        Button transactionsButton = UiFactory.fullWidthButton("Transactions");
        transactionsButton.setOnAction(event -> showTransactionsView());
        Button cardsButton = UiFactory.fullWidthButton("Cards");
        cardsButton.setOnAction(event -> showStaffCardsView());
        Button chequeRequestsButton = UiFactory.fullWidthButton("Cheque Requests");
        chequeRequestsButton.setOnAction(event -> showChequeRequestsView());
        Button loansButton = UiFactory.fullWidthButton("Loan Applications");
        loansButton.setOnAction(event -> showLoanApplicationView());
        Button emiButton = UiFactory.fullWidthButton("EMI Calculator");
        emiButton.setOnAction(event -> showEmiCalculatorView());
        Button statementsButton = UiFactory.fullWidthButton("Statements");
        statementsButton.setOnAction(event -> showStatementView());
        Button reportsButton = UiFactory.fullWidthButton("Reports");
        reportsButton.setOnAction(event -> showReportsView());
        Button auditButton = UiFactory.fullWidthButton("Audit Log");
        auditButton.setOnAction(event -> showAuditLogView());
        Button logoutButton = UiFactory.fullWidthButton("Logout");
        logoutButton.setOnAction(this::handleLogout);

        sidebar.getChildren().addAll(
                sidebarLogoImageView,
                bankNameLabel,
                portalLabel,
                UiFactory.separator(),
                dashboardButton,
                customersButton,
                applicationsButton,
                transactionsButton,
                cardsButton,
                chequeRequestsButton,
                loansButton,
                emiButton,
                statementsButton,
                reportsButton,
                auditButton,
                UiFactory.verticalSpacer(),
                logoutButton
        );
        return sidebar;
    }

    private HBox createHeader() {
        HBox header = UiFactory.hbox(14, Pos.CENTER_LEFT);
        header.setPadding(new Insets(12, 18, 12, 18));
        headerLogoImageView = UiFactory.logo(150, 42);

        VBox titleBox = new VBox(2);
        titleBox.getChildren().addAll(
                UiFactory.headerTitle("Askari Bank Desktop Banking System"),
                new Label("Banking Operations Dashboard")
        );

        header.getChildren().addAll(headerLogoImageView, titleBox);
        return header;
    }

    private VBox createDashboardHome() {
        VBox home = new VBox(18);
        home.setPadding(new Insets(24));

        welcomeLabel = new Label("Welcome to Askari Bank Staff Portal");
        VBox titleBox = new VBox(6);
        titleBox.getChildren().addAll(
                UiFactory.largeTitle("Banking Operations Dashboard"),
                welcomeLabel
        );

        totalCustomersLabel = UiFactory.value("0", 24);
        totalBankBalanceLabel = UiFactory.value("PKR 0", 20);
        totalTransactionsLabel = UiFactory.value("0", 24);
        approvedLoansLabel = UiFactory.value("0", 24);

        TilePane cards = new TilePane();
        cards.setHgap(14);
        cards.setVgap(14);
        cards.setPrefColumns(4);
        cards.setPrefTileWidth(190);
        cards.setPrefTileHeight(110);
        cards.getChildren().addAll(
                summaryCard("Total Customers", totalCustomersLabel),
                summaryCard("Total Bank Balance", totalBankBalanceLabel),
                summaryCard("Total Transactions", totalTransactionsLabel),
                summaryCard("Approved Loans", approvedLoansLabel)
        );

        home.getChildren().addAll(
                titleBox,
                cards,
                UiFactory.separator(),
                new Label("Use the left menu for customer management, transactions, cards, loans, statements, reports, and audit log.")
        );
        return home;
    }

    private GridPane summaryCard(String title, Label valueLabel) {
        GridPane card = UiFactory.grid(10, 8);
        card.setPadding(new Insets(12));
        card.add(new Label(title), 0, 0);
        card.add(valueLabel, 0, 1);
        return card;
    }

    private void initialize() {
        bankNameLabel.setText(BankDataStore.bankName);
        welcomeLabel.setText("Welcome to " + BankDataStore.bankName + " Staff Portal");
        showDashboard();
    }

    private void showDashboard() {
        refreshDashboardCards();
        contentArea.getChildren().setAll(dashboardHome);
    }

    private void showAccountView() {
        loadContent(new AccountController().createView());
    }

    private void showAccountApplicationsView() {
        loadContent(new AccountApplicationsController().createView());
    }

    private void showLoanApplicationView() {
        loadContent(new LoanApplicationController().createView());
    }

    private void showEmiCalculatorView() {
        loadContent(new EmiCalculatorController().createView());
    }

    private void showTransactionsView() {
        loadContent(new TransactionsController().createView());
    }

    private void showStaffCardsView() {
        loadContent(new StaffCardController().createView());
    }

    private void showStatementView() {
        loadContent(new StatementController().createView());
    }

    private void showChequeRequestsView() {
        loadContent(new StaffChequeRequestsController().createView());
    }

    private void showReportsView() {
        loadContent(new StaffReportsController().createView());
    }

    private void showAuditLogView() {
        loadContent(new AuditLogController().createView());
    }

    private void handleLogout(ActionEvent event) {
        LoginSession.clear();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        BankScreens.replaceScene(stage, BankScreens.login());
    }

    private void loadContent(Parent view) {
        contentArea.getChildren().setAll(view);
    }

    private void refreshDashboardCards() {
        totalCustomersLabel.setText(String.valueOf(BankDataStore.allCustomers.size()));
        totalBankBalanceLabel.setText(String.format("PKR %,.0f", BankDataStore.getTotalBankBalance()));
        totalTransactionsLabel.setText(String.valueOf(BankDataStore.getTotalTransactions()));
        approvedLoansLabel.setText(String.valueOf(BankDataStore.getApprovedLoanCount()));
    }

    private void showLoadError(String message) {
        Label errorLabel = new Label(message);
        contentArea.getChildren().setAll(errorLabel);
    }
}
