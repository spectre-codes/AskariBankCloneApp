package com.askaribank;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.util.Duration;

public class CustomerDashboardController {

    private static final Color PAGE_BACKGROUND = Color.rgb(244, 247, 251);
    private static final Color CARD_BLUE = Color.rgb(20, 91, 178);
    private static final Color CARD_DARK_BLUE = Color.rgb(11, 58, 135);
    private static final Color ACCENT_BLUE = Color.rgb(23, 117, 209);
    private static final Color TEAL = Color.rgb(18, 155, 164);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color SOFT_BORDER = Color.rgb(219, 226, 235);
    private static final Color TEXT_DARK = Color.rgb(35, 45, 61);
    private static final Color TEXT_MUTED = Color.rgb(95, 106, 124);
    private static final Color BADGE_RED = Color.rgb(215, 42, 48);
    private static final CornerRadii CARD_RADIUS = new CornerRadii(20);
    private static final CornerRadii SOFT_RADIUS = new CornerRadii(14);
    private static final DateTimeFormatter LOGIN_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

    private final LocalDateTime loginTime = LocalDateTime.now();

    private boolean balanceVisible = false;
    private boolean sideDrawerOpen = false;
    private int morePageIndex = 0;
    private double moreGridDragStartX;

    private StackPane rootStack;
    private StackPane contentArea;
    private VBox dashboardHome;
    private HBox frequentlyUsedRow;
    private GridPane moreServiceGrid;
    private HBox morePageDots;
    private Region drawerOverlay;
    private VBox sideDrawer;
    private PauseTransition sessionTimeout;
    private Label accountTypeLabel;
    private Label accountNumberLabel;
    private Label balanceLabel;
    private Label customerNameLabel;
    private Label restrictionWarningLabel;
    private Label loginTimeLabel;
    private Label messageBadgeLabel;
    private TextField serviceSearchField;
    private Button balanceToggleButton;
    private List<ServiceItem> frequentlyUsedItems;
    private List<List<ServiceItem>> servicePages;
    private List<ServiceItem> allServiceItems;

    public Parent createView() {
        buildServiceCatalog();

        rootStack = new StackPane();

        BorderPane appLayout = new BorderPane();
        appLayout.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        contentArea = new StackPane();
        dashboardHome = createDashboardHome();
        appLayout.setCenter(contentArea);
        appLayout.setBottom(createBottomNavigationBar());
        rootStack.getChildren().addAll(appLayout, createDrawerLayer());

        initialize();
        return rootStack;
    }

    private VBox createDashboardHome() {
        VBox home = new VBox(13);
        home.setPadding(new Insets(16, 18, 20, 18));
        home.setMaxWidth(560);

        restrictionWarningLabel = UiFactory.messageLabel();
        restrictionWarningLabel.setTextFill(Color.rgb(160, 48, 38));

        loginTimeLabel = new Label("Login at " + loginTime.format(LOGIN_TIME_FORMATTER));
        loginTimeLabel.setFont(Font.font(11));
        loginTimeLabel.setTextFill(TEXT_MUTED);

        home.getChildren().addAll(
                createTopHeader(),
                createAccountCard(),
                loginTimeLabel,
                restrictionWarningLabel,
                createSearchBar(),
                createFrequentlyUsedSection(),
                createMoreWithAskariSection(),
                createPromotionsSection()
        );
        return home;
    }

    private HBox createTopHeader() {
        HBox header = UiFactory.hbox(10, Pos.CENTER);
        header.setPadding(new Insets(2, 0, 4, 0));

        Button menuButton = createHeaderIconButton("☰");
        menuButton.setOnAction(event -> toggleSideDrawer());

        Button logoButton = createLogoHeaderButton();
        logoButton.setOnAction(event -> showDashboardHome());
        HBox.setHgrow(logoButton, Priority.ALWAYS);

        Button powerButton = createHeaderIconButton("⏻");
        powerButton.setOnAction(event -> confirmLogout());

        header.getChildren().addAll(menuButton, logoButton, powerButton);
        return header;
    }

    private Button createLogoHeaderButton() {
        HBox logoBox = UiFactory.hbox(8, Pos.CENTER);
        ImageView logoImageView = UiFactory.logo(94, 30);
        Label logoText = new Label("askaribank");
        logoText.setFont(Font.font("System", FontWeight.BOLD, 18));
        logoText.setTextFill(CARD_DARK_BLUE);
        logoBox.getChildren().addAll(logoImageView, logoText);

        Button button = new Button();
        button.setGraphic(logoBox);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setMinHeight(42);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPadding(new Insets(6, 10, 6, 10));
        button.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, SOFT_RADIUS, Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                SOFT_RADIUS, new BorderWidths(1))));
        return button;
    }

    private Button createHeaderIconButton(String icon) {
        Button button = new Button(icon);
        button.setFont(Font.font("System", FontWeight.BOLD, 20));
        button.setTextFill(CARD_DARK_BLUE);
        button.setMinSize(42, 42);
        button.setPrefSize(42, 42);
        button.setPadding(new Insets(4));
        button.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, SOFT_RADIUS, Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                SOFT_RADIUS, new BorderWidths(1))));
        return button;
    }

    private VBox createAccountCard() {
        VBox accountCard = new VBox(12);
        accountCard.setPadding(new Insets(18, 20, 18, 20));
        accountCard.setBackground(new Background(new BackgroundFill(CARD_BLUE, CARD_RADIUS, Insets.EMPTY)));
        accountCard.setBorder(new Border(new BorderStroke(CARD_DARK_BLUE, BorderStrokeStyle.SOLID,
                CARD_RADIUS, new BorderWidths(1))));

        HBox topRow = UiFactory.hbox(10, Pos.CENTER_LEFT);
        accountTypeLabel = createCardCaption("-");
        HBox.setHgrow(accountTypeLabel, Priority.ALWAYS);
        Label bankLabel = createCardCaption("Askari Digital");
        topRow.getChildren().addAll(accountTypeLabel, bankLabel);

        accountNumberLabel = createCardMainText("-");

        Label balanceCaptionLabel = createCardCaption("Available Balance");
        balanceLabel = new Label("PKR XXXXXXX");
        balanceLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        balanceLabel.setTextFill(Color.WHITE);

        balanceToggleButton = createCardActionButton("Show");
        balanceToggleButton.setOnAction(event -> toggleBalance());

        HBox balanceRow = UiFactory.hbox(10, Pos.CENTER_LEFT);
        HBox.setHgrow(balanceLabel, Priority.ALWAYS);
        balanceLabel.setMaxWidth(Double.MAX_VALUE);
        balanceRow.getChildren().addAll(balanceLabel, balanceToggleButton);

        customerNameLabel = createCardCaption("Customer");

        HBox cardActions = UiFactory.hbox(8, Pos.CENTER_LEFT);
        Button miniStatementButton = createCardActionButton("Mini Statement");
        miniStatementButton.setOnAction(event -> showMiniStatementView());
        Button shareButton = createCardActionButton("Share");
        shareButton.setOnAction(event -> handleShareAccountSummary());
        Button requestToPayButton = createCardActionButton("Request to Pay");
        requestToPayButton.setOnAction(event -> showComingSoon("Request to Pay",
                "Request to Pay is prepared as a safe simulation for this desktop version."));
        cardActions.getChildren().addAll(miniStatementButton, shareButton, requestToPayButton);

        accountCard.getChildren().addAll(
                topRow,
                accountNumberLabel,
                balanceCaptionLabel,
                balanceRow,
                customerNameLabel,
                cardActions
        );
        return accountCard;
    }

    private TextField createSearchBar() {
        TextField searchField = UiFactory.textField("Search services");
        serviceSearchField = searchField;
        searchField.setMinHeight(42);
        searchField.setPadding(new Insets(8, 14, 8, 14));
        searchField.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND,
                new CornerRadii(18), Insets.EMPTY)));
        searchField.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                new CornerRadii(18), new BorderWidths(1))));
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterDashboardServices(newValue));
        searchField.setOnAction(event -> openSearchResultsDialog(searchField.getText()));
        return searchField;
    }

    private VBox createFrequentlyUsedSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(2, 0, 0, 0));
        section.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, CARD_RADIUS, Insets.EMPTY)));
        section.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                CARD_RADIUS, new BorderWidths(1))));
        section.setPadding(new Insets(12, 12, 12, 12));

        HBox titleRow = createSectionTitleRow("Frequently Used");
        Button editButton = createInlineLinkButton("Edit ✎");
        editButton.setOnAction(event -> AlertHelper.showInfo("Frequently Used",
                "Frequently Used editing will be available soon."));
        titleRow.getChildren().add(editButton);

        frequentlyUsedRow = UiFactory.hbox(4, Pos.CENTER);
        rebuildFrequentlyUsedItems(frequentlyUsedItems);

        section.getChildren().addAll(titleRow, frequentlyUsedRow);
        return section;
    }

    private VBox createMoreWithAskariSection() {
        VBox section = new VBox(8);
        section.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, CARD_RADIUS, Insets.EMPTY)));
        section.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                CARD_RADIUS, new BorderWidths(1))));
        section.setPadding(new Insets(12, 12, 12, 12));

        HBox titleRow = createSectionTitleRow("More with Askari");
        Button viewAllButton = createInlineLinkButton("View All »");
        viewAllButton.setOnAction(event -> openAllServicesDialog());
        titleRow.getChildren().add(viewAllButton);

        moreServiceGrid = UiFactory.grid(8, 8);
        moreServiceGrid.setAlignment(Pos.CENTER);
        moreServiceGrid.setOnMousePressed(event -> moreGridDragStartX = event.getSceneX());
        moreServiceGrid.setOnMouseReleased(event -> handleMoreGridSwipe(event.getSceneX()));

        morePageDots = UiFactory.hbox(6, Pos.CENTER);
        renderMoreWithAskariPage(0);

        section.getChildren().addAll(titleRow, moreServiceGrid, morePageDots);
        return section;
    }

    private VBox createPromotionsSection() {
        VBox section = new VBox(8);
        HBox titleRow = createSectionTitleRow("Discounts & Promotions");

        HBox banner = UiFactory.hbox(14, Pos.CENTER_LEFT);
        banner.setPadding(new Insets(15, 16, 15, 16));
        banner.setMinHeight(112);
        banner.setBackground(new Background(new BackgroundFill(TEAL, CARD_RADIUS, Insets.EMPTY)));
        banner.setBorder(new Border(new BorderStroke(Color.rgb(7, 126, 136), BorderStrokeStyle.SOLID,
                CARD_RADIUS, new BorderWidths(1))));

        VBox textBox = new VBox(5);
        Label titleLabel = new Label("Tap, Book & Go.");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.WHITE);
        Label subtitleLabel = new Label("All in your Askari Mobile App");
        subtitleLabel.setFont(Font.font(13));
        subtitleLabel.setTextFill(Color.rgb(226, 250, 252));
        Label chipsLabel = new Label("Travel • Entertainment • Hotel Stays");
        chipsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        chipsLabel.setTextFill(Color.WHITE);
        textBox.getChildren().addAll(titleLabel, subtitleLabel, chipsLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button offersButton = createWhitePillButton("View Offers");
        offersButton.setOnAction(event -> showDiscountsInfo());
        banner.getChildren().addAll(textBox, spacer, offersButton);

        section.getChildren().addAll(titleRow, banner);
        return section;
    }

    private HBox createSectionTitleRow(String title) {
        HBox row = UiFactory.hbox(10, Pos.CENTER_LEFT);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 17));
        titleLabel.setTextFill(TEXT_DARK);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        row.getChildren().add(titleLabel);
        return row;
    }

    private Button createInlineLinkButton(String text) {
        Button button = UiFactory.button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setTextFill(ACCENT_BLUE);
        button.setPadding(new Insets(3, 6, 3, 6));
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);
        return button;
    }

    private Button createWhitePillButton(String text) {
        Button button = UiFactory.button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 12));
        button.setTextFill(TEAL);
        button.setPadding(new Insets(8, 12, 8, 12));
        button.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(18), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(Color.rgb(224, 249, 250), BorderStrokeStyle.SOLID,
                new CornerRadii(18), new BorderWidths(1))));
        return button;
    }

    private Parent createBottomNavigationBar() {
        StackPane bottomWrapper = new StackPane();
        bottomWrapper.setPadding(new Insets(0, 16, 12, 16));
        bottomWrapper.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox bottomNavigationBar = UiFactory.hbox(4, Pos.CENTER);
        bottomNavigationBar.setMaxWidth(580);
        bottomNavigationBar.setMinHeight(72);
        bottomNavigationBar.setPadding(new Insets(8, 10, 8, 10));
        bottomNavigationBar.setBackground(new Background(new BackgroundFill(CARD_BLUE,
                new CornerRadii(24), Insets.EMPTY)));
        bottomNavigationBar.setBorder(new Border(new BorderStroke(CARD_DARK_BLUE, BorderStrokeStyle.SOLID,
                new CornerRadii(24), new BorderWidths(1))));

        bottomNavigationBar.getChildren().addAll(
                createBottomNavItem("atm_locator.png", "ATM Locator", this::openAtmLocator, false, false),
                createBottomNavItem("message_box.png", "Message Box", this::openMessageBox, true, false),
                createBottomNavItem("home_askari.png", "Askari", this::showDashboardHome, false, true),
                createBottomNavItem("transaction_alerts.png", "Transaction Alerts", this::openTransactionAlerts, false, false),
                createBottomNavItem("profile.png", "Profile", this::openPersonalSettings, false, false)
        );

        bottomWrapper.getChildren().add(bottomNavigationBar);
        updateBottomNavBadges();
        return bottomWrapper;
    }

    private Button createBottomNavItem(String iconFileName, String title, Runnable action, boolean showBadge, boolean centerButton) {
        Node iconNode = centerButton
                ? createCenterDiamondIcon(iconFileName, title)
                : createBottomNavIcon(iconFileName, title, showBadge);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, centerButton ? 10 : 9));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(centerButton ? 72 : 78);

        VBox buttonContent = new VBox(centerButton ? 2 : 3, iconNode, titleLabel);
        buttonContent.setAlignment(Pos.CENTER);

        Button navButton = new Button();
        navButton.setGraphic(buttonContent);
        navButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        navButton.setPadding(new Insets(4));
        navButton.setMinSize(centerButton ? 88 : 86, centerButton ? 74 : 58);
        navButton.setPrefSize(centerButton ? 88 : 86, centerButton ? 74 : 58);
        navButton.setBackground(Background.EMPTY);
        navButton.setBorder(Border.EMPTY);
        navButton.setOnAction(event -> action.run());
        if (centerButton) {
            navButton.setTranslateY(-12);
        }
        return navButton;
    }

    private StackPane createBottomNavIcon(String iconFileName, String title, boolean showBadge) {
        Node iconNode = IconFactory.createServiceIcon(iconFileName, 26, Color.WHITE);

        StackPane iconStack = new StackPane(iconNode);
        iconStack.setMinSize(34, 24);
        iconStack.setPrefSize(34, 24);
        if (showBadge) {
            messageBadgeLabel = createBadgeLabel();
            StackPane.setAlignment(messageBadgeLabel, Pos.TOP_RIGHT);
            iconStack.getChildren().add(messageBadgeLabel);
        }
        return iconStack;
    }

    private Label createBadgeLabel() {
        Label badge = new Label();
        badge.setFont(Font.font("System", FontWeight.BOLD, 9));
        badge.setTextFill(Color.WHITE);
        badge.setAlignment(Pos.CENTER);
        badge.setMinSize(17, 17);
        badge.setPrefSize(17, 17);
        badge.setBackground(new Background(new BackgroundFill(BADGE_RED, new CornerRadii(9), Insets.EMPTY)));
        badge.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID,
                new CornerRadii(9), new BorderWidths(1))));
        return badge;
    }

    private StackPane createCenterDiamondIcon(String iconFileName, String title) {
        Region diamondShape = new Region();
        diamondShape.setMinSize(34, 34);
        diamondShape.setPrefSize(34, 34);
        diamondShape.setMaxSize(34, 34);
        diamondShape.setRotate(45);
        diamondShape.setBackground(new Background(new BackgroundFill(Color.WHITE,
                new CornerRadii(6), Insets.EMPTY)));
        diamondShape.setBorder(new Border(new BorderStroke(Color.rgb(210, 232, 255), BorderStrokeStyle.SOLID,
                new CornerRadii(6), new BorderWidths(1))));

        Node iconNode = IconFactory.createServiceIcon(iconFileName, 24, CARD_DARK_BLUE);

        StackPane diamondIcon = new StackPane(diamondShape, iconNode);
        diamondIcon.setMinSize(44, 40);
        diamondIcon.setPrefSize(44, 40);
        return diamondIcon;
    }

    private void buildServiceCatalog() {
        frequentlyUsedItems = List.of(
                new ServiceItem("Send Money", "send_money.png", this::showTransferView),
                new ServiceItem("Pay Bill", "pay_bill.png", this::showBillPaymentsView),
                new ServiceItem("M-Tag", "mtag.png", this::showMTagPaymentView),
                new ServiceItem("Card Discounts", "card_discounts.png", this::showDiscountsInfo)
        );

        servicePages = List.of(
                List.of(
                        new ServiceItem("Bill-Split", "bill_split.png", () -> showComingSoon("Bill-Split", "Split a bill with saved beneficiaries and friends.")),
                        new ServiceItem("Buy Tickets", "buy_tickets.png", this::showTicketPurchaseView),
                        new ServiceItem("Foundation Securities", "foundation_securities.png", () -> showInfoScreen("Foundation Securities",
                                "Investment account services are available as an information screen in this desktop demo.")),
                        new ServiceItem("AFP Guest Room Booking", "guest_room.png", () -> showInfoScreen("AFP Guest Room Booking",
                                "Guest room booking is simulated for the Askari services catalog.")),
                        new ServiceItem("Beneficiary Management", "beneficiary.png", this::showBeneficiaryView),
                        new ServiceItem("Scan To Pay", "scan_to_pay.png", () -> showComingSoon("Scan To Pay", "QR and merchant scan payments will be available soon.")),
                        new ServiceItem("M-Tag", "mtag.png", this::showMTagPaymentView),
                        new ServiceItem("Mobile Packages", "mobile_packages.png", () -> showInfoScreen("Mobile Packages",
                                "Browse simulated network bundles and use Mobile Top-up for live demo payments.")),
                        new ServiceItem("Education", "education.png", () -> showInfoScreen("Education", "Education fee payment is prepared as a safe service simulation.")),
                        new ServiceItem("Donations", "donations.png", this::showDonationsView),
                        new ServiceItem("Pay Bill", "pay_bill.png", this::showBillPaymentsView),
                        new ServiceItem("Send Money", "send_money.png", this::showTransferView)
                ),
                List.of(
                        new ServiceItem("Manage Limit", "manage_limit.png", this::showLimitManagementView),
                        new ServiceItem("Cards Management", "cards_management.png", this::showCardsView),
                        new ServiceItem("Raast ID Management", "raast.png", this::showRaastIdView),
                        new ServiceItem("Cardless Cash", "cardless_cash.png", this::showCardlessCashView),
                        new ServiceItem("Ask Advance", "ask_advance.png", this::showLoansView),
                        new ServiceItem("Transaction History", "transaction_history.png", this::showMiniStatementView),
                        new ServiceItem("Mobile Top-up", "mobile_topup.png", this::showMobileTopUpView),
                        new ServiceItem("F-PIN", "fpin.png", this::showSecuritySettingsView),
                        new ServiceItem("Manage Devices", "manage_devices.png", this::showManageDevicesInfo),
                        new ServiceItem("Change Password", "change_password.png", this::showSecuritySettingsView),
                        new ServiceItem("Certificates", "certificates.png", () -> showInfoScreen("Certificates",
                                "Certificate requests are shown as a safe information service in this build.")),
                        new ServiceItem("Email Alerts", "email_alerts.png", this::showEmailAlertsInfo)
                ),
                List.of(
                        new ServiceItem("Subscription", "subscription.png", this::showSubscriptionInfo),
                        new ServiceItem("Cheques", "cheques.png", this::showChequeBookRequestView),
                        new ServiceItem("Instant Payment", "instant_payment.png", this::showTransferView),
                        new ServiceItem("Spending Insight", "spending_insight.png", () -> showInfoScreen("Spending Insight",
                                "A simple spending report will be available soon. Use Transaction History for current records.")),
                        new ServiceItem("Card Discounts", "card_discounts.png", this::showDiscountsInfo),
                        new ServiceItem("Askari Edge", "askari_edge.png", () -> showInfoScreen("Askari Edge",
                                "Askari Edge benefits and offers are represented as a safe information screen."))
                )
        );

        allServiceItems = new ArrayList<>();
        for (List<ServiceItem> page : servicePages) {
            allServiceItems.addAll(page);
        }
    }

    private void filterDashboardServices(String queryText) {
        String query = queryText == null ? "" : queryText.trim().toLowerCase();
        if (query.isEmpty()) {
            rebuildFrequentlyUsedItems(frequentlyUsedItems);
            renderMoreWithAskariPage(morePageIndex);
            return;
        }

        List<ServiceItem> frequentMatches = frequentlyUsedItems.stream()
                .filter(item -> item.matches(query))
                .toList();
        rebuildFrequentlyUsedItems(frequentMatches);

        List<ServiceItem> allMatches = allServiceItems.stream()
                .filter(item -> item.matches(query))
                .toList();
        renderServiceGrid(allMatches);
        morePageDots.getChildren().clear();
        if (allMatches.isEmpty()) {
            Label emptyLabel = new Label("No matching services found.");
            emptyLabel.setTextFill(TEXT_MUTED);
            moreServiceGrid.add(emptyLabel, 0, 0);
        }
    }

    private void rebuildFrequentlyUsedItems(List<ServiceItem> items) {
        frequentlyUsedRow.getChildren().clear();
        if (items.isEmpty()) {
            Label emptyLabel = new Label("No matching frequent services.");
            emptyLabel.setTextFill(TEXT_MUTED);
            frequentlyUsedRow.getChildren().add(emptyLabel);
            return;
        }

        for (ServiceItem item : items) {
            Button itemButton = createIconServiceItem(item.iconFileName(), item.title(), item.action());
            HBox.setHgrow(itemButton, Priority.ALWAYS);
            itemButton.setMaxWidth(Double.MAX_VALUE);
            frequentlyUsedRow.getChildren().add(itemButton);
        }
    }

    private void renderMoreWithAskariPage(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= servicePages.size()) {
            return;
        }

        morePageIndex = pageIndex;
        renderServiceGrid(servicePages.get(pageIndex));
        updatePageDots();
    }

    private void renderServiceGrid(List<ServiceItem> items) {
        moreServiceGrid.getChildren().clear();
        int columns = serviceColumnCount();
        for (int index = 0; index < items.size(); index++) {
            ServiceItem item = items.get(index);
            moreServiceGrid.add(createIconServiceItem(item.iconFileName(), item.title(), item.action()),
                    index % columns, index / columns);
        }
    }

    private int serviceColumnCount() {
        double width = dashboardHome == null ? 0 : dashboardHome.getWidth();
        return width > 0 && width < 500 ? 3 : 4;
    }

    private void updatePageDots() {
        morePageDots.getChildren().clear();
        for (int index = 0; index < servicePages.size(); index++) {
            int page = index;
            Button dotButton = new Button("●");
            dotButton.setFont(Font.font(12));
            dotButton.setTextFill(index == morePageIndex ? ACCENT_BLUE : Color.rgb(180, 188, 198));
            dotButton.setPadding(new Insets(0, 3, 0, 3));
            dotButton.setBackground(Background.EMPTY);
            dotButton.setBorder(Border.EMPTY);
            dotButton.setOnAction(event -> renderMoreWithAskariPage(page));
            morePageDots.getChildren().add(dotButton);
        }
    }

    private Button createIconServiceItem(String iconFileName, String title, Runnable action) {
        Node iconNode = IconFactory.createServiceIcon(iconFileName, 34, ACCENT_BLUE);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        titleLabel.setTextFill(TEXT_DARK);
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(112);

        VBox tileContent = new VBox(6, iconNode, titleLabel);
        tileContent.setAlignment(Pos.CENTER);

        Button serviceItem = new Button();
        serviceItem.setGraphic(tileContent);
        serviceItem.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        serviceItem.setMinSize(114, 76);
        serviceItem.setPrefSize(114, 76);
        serviceItem.setPadding(new Insets(4));
        serviceItem.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        serviceItem.setBorder(Border.EMPTY);
        serviceItem.setOnMouseEntered(event -> serviceItem.setBackground(new Background(new BackgroundFill(
                Color.rgb(238, 247, 255), new CornerRadii(10), Insets.EMPTY))));
        serviceItem.setOnMouseExited(event -> serviceItem.setBackground(new Background(new BackgroundFill(
                CARD_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY))));
        serviceItem.setOnAction(event -> action.run());
        return serviceItem;
    }

    private void handleMoreGridSwipe(double releaseX) {
        double delta = releaseX - moreGridDragStartX;
        if (Math.abs(delta) < 70) {
            return;
        }

        if (delta < 0 && morePageIndex < servicePages.size() - 1) {
            renderMoreWithAskariPage(morePageIndex + 1);
        } else if (delta > 0 && morePageIndex > 0) {
            renderMoreWithAskariPage(morePageIndex - 1);
        }
    }

    private Label createCardCaption(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        label.setTextFill(Color.rgb(218, 234, 255));
        return label;
    }

    private Label createCardMainText(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 17));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private Button createCardActionButton(String text) {
        Button button = UiFactory.button(text);
        button.setFont(Font.font("System", FontWeight.BOLD, 11));
        button.setTextFill(Color.WHITE);
        button.setPadding(new Insets(7, 10, 7, 10));
        button.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.18),
                new CornerRadii(14), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(Color.rgb(255, 255, 255, 0.32), BorderStrokeStyle.SOLID,
                new CornerRadii(14), new BorderWidths(1))));
        return button;
    }

    private StackPane createDrawerLayer() {
        StackPane drawerLayer = new StackPane();
        drawerLayer.setMouseTransparent(false);

        drawerOverlay = new Region();
        drawerOverlay.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.35),
                CornerRadii.EMPTY, Insets.EMPTY)));
        drawerOverlay.setVisible(false);
        drawerOverlay.setOnMouseClicked(event -> closeSideDrawer());

        sideDrawer = createSideDrawer();
        sideDrawer.setTranslateX(-312);
        StackPane.setAlignment(sideDrawer, Pos.CENTER_LEFT);

        drawerLayer.getChildren().addAll(drawerOverlay, sideDrawer);
        drawerLayer.setPickOnBounds(false);
        drawerOverlay.prefWidthProperty().bind(drawerLayer.widthProperty());
        drawerOverlay.prefHeightProperty().bind(drawerLayer.heightProperty());
        return drawerLayer;
    }

    private VBox createSideDrawer() {
        Customer customer = LoginSession.getLoggedInCustomer();
        VBox drawer = new VBox(8);
        drawer.setPrefWidth(312);
        drawer.setMaxWidth(312);
        drawer.setPadding(new Insets(22, 16, 22, 16));
        drawer.setBackground(new Background(new BackgroundFill(Color.WHITE,
                new CornerRadii(0, 18, 18, 0, false), Insets.EMPTY)));
        drawer.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                new CornerRadii(0, 18, 18, 0, false), new BorderWidths(0, 1, 0, 0))));

        Label nameLabel = UiFactory.headerTitle(customer == null ? "Customer" : customer.getCustomerFullName());
        nameLabel.setTextFill(TEXT_DARK);
        Label accountLabel = new Label(customer == null ? "-" : maskAccountNumber(customer.getAccountNumber()));
        accountLabel.setTextFill(TEXT_MUTED);
        Label statusLabel = new Label(customer == null ? "" : customer.getAccountType().toUpperCase() + " • " + customer.getAccountStatus());
        statusLabel.setTextFill(ACCENT_BLUE);
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 11));

        drawer.getChildren().addAll(
                nameLabel,
                accountLabel,
                statusLabel,
                UiFactory.separator(),
                createDrawerItem("◉", "Account Information", this::showAccountDetailsView),
                createDrawerItem("▤", "Personal Settings", this::openPersonalSettings),
                createDrawerItem("⌘", "Security Settings", this::showSecuritySettingsView),
                createDrawerItem("◌", "Beneficiary Management", this::showBeneficiaryView),
                createDrawerItem("✉", "Messages", this::openMessageBox),
                createDrawerItem("!", "Transaction Alerts", this::openTransactionAlerts),
                createDrawerItem("?", "Help / Need Assistance", this::showSupportView),
                createDrawerItem("i", "About Askari Digital", this::showAboutAskari),
                UiFactory.verticalSpacer(),
                createDrawerItem("⏻", "Logout", this::confirmLogout)
        );
        return drawer;
    }

    private Button createDrawerItem(String icon, String title, Runnable action) {
        HBox content = UiFactory.hbox(10, Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        iconLabel.setMinWidth(24);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        iconLabel.setTextFill(ACCENT_BLUE);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        titleLabel.setTextFill(TEXT_DARK);
        content.getChildren().addAll(iconLabel, titleLabel);

        Button button = UiFactory.fullWidthButton("");
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setMinHeight(38);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(5, 6, 5, 6));
        button.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));
        button.setBorder(Border.EMPTY);
        button.setOnAction(event -> {
            closeSideDrawer();
            action.run();
        });
        return button;
    }

    private void toggleSideDrawer() {
        if (sideDrawerOpen) {
            closeSideDrawer();
        } else {
            openSideDrawer();
        }
    }

    private void openSideDrawer() {
        sideDrawerOpen = true;
        drawerOverlay.setVisible(true);
        drawerOverlay.setMouseTransparent(false);
        TranslateTransition transition = new TranslateTransition(Duration.millis(220), sideDrawer);
        transition.setToX(0);
        transition.play();
    }

    private void closeSideDrawer() {
        sideDrawerOpen = false;
        TranslateTransition transition = new TranslateTransition(Duration.millis(220), sideDrawer);
        transition.setToX(-312);
        transition.setOnFinished(event -> {
            drawerOverlay.setVisible(false);
            drawerOverlay.setMouseTransparent(true);
        });
        transition.play();
    }

    private void initialize() {
        showDashboardHome();
        rootStack.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null && sessionTimeout == null) {
                sessionTimeout = SessionManager.startSessionTimeout(newScene, this::logoutFromSessionTimeout);
            }
        });
    }

    private void showDashboardHome() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            showSimpleMessage("No customer is logged in.");
            return;
        }

        refreshDashboard(customer);
        if (serviceSearchField != null && !serviceSearchField.getText().isBlank()) {
            serviceSearchField.clear();
        }
        StackPane phoneWrapper = new StackPane(dashboardHome);
        phoneWrapper.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        phoneWrapper.setPadding(new Insets(0, 0, 6, 0));
        ScrollPane scrollPane = UiFactory.scroll(phoneWrapper);
        scrollPane.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane.setBorder(Border.EMPTY);
        contentArea.getChildren().setAll(scrollPane);
        updateBottomNavBadges();
    }

    private void refreshDashboard(Customer customer) {
        balanceVisible = !customer.isHideBalanceByDefault();
        accountTypeLabel.setText(customer.getAccountType().toUpperCase());
        accountNumberLabel.setText(displayAccountNumber(customer.getAccountNumber()));
        customerNameLabel.setText(customer.getCustomerFullName());
        restrictionWarningLabel.setText(customer.isFrozen() || customer.isLocked()
                ? "Your account is temporarily restricted. Please contact branch staff."
                : "");
        updateBalanceLabel(customer);
    }

    private void toggleBalance() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            return;
        }

        balanceVisible = !balanceVisible;
        updateBalanceLabel(customer);
    }

    private void updateBalanceLabel(Customer customer) {
        if (balanceVisible) {
            balanceLabel.setText(String.format("PKR %,.2f", customer.getAccountBalance()));
            balanceToggleButton.setText("Hide");
        } else {
            balanceLabel.setText("PKR XXXXXXX");
            balanceToggleButton.setText("Show");
        }
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return "XXXXXX";
        }

        String digitsOnly = accountNumber.replaceAll("\\D", "");
        if (digitsOnly.length() >= 8) {
            return digitsOnly.substring(0, 4) + "XXXXXX" + digitsOnly.substring(digitsOnly.length() - 4);
        }

        String visibleAccountNumber = digitsOnly.isBlank() ? accountNumber : digitsOnly;
        int visibleStart = Math.max(0, visibleAccountNumber.length() - 4);
        return "XXXXXX" + visibleAccountNumber.substring(visibleStart);
    }

    private String displayAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            return "-";
        }

        return accountNumber.trim();
    }

    private void showTransferView() {
        loadContent(new CustomerTransferController().createView());
    }

    private void showRaastIdView() {
        loadContent(new RaastIdManagementController().createView());
    }

    private void showAccountDetailsView() {
        loadContent(CustomerAccountDetailsView.create());
    }

    private void showBillPaymentsView() {
        loadContent(new BillPaymentController().createView());
    }

    private void showMTagPaymentView() {
        loadContent(new MTagPaymentController().createView());
    }

    private void showCardlessCashView() {
        loadContent(new CardlessCashController().createView());
    }

    private void showCardsView() {
        loadContent(new CustomerCardController().createView());
    }

    private void showTicketPurchaseView() {
        loadContent(new TicketPurchaseController().createView());
    }

    private void showDonationsView() {
        loadContent(new DonationController().createView());
    }

    private void showLimitManagementView() {
        loadContent(new LimitManagementController().createView());
    }

    private void showMiniStatementView() {
        loadContent(new CustomerMiniStatementController().createView());
    }

    private void showMobileTopUpView() {
        loadContent(new MobileTopUpController().createView());
    }

    private void showBeneficiaryView() {
        loadContent(new BeneficiaryController().createView());
    }

    private void showChequeBookRequestView() {
        loadContent(new ChequeBookRequestController().createView());
    }

    private void openTransactionAlerts() {
        loadContent(new NotificationsController().createView());
    }

    private void openMessageBox() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer != null) {
            NotificationService.markAllRead(customer.getAccountNumber());
        }
        loadContent(new MessagesController().createView());
        updateBottomNavBadges();
    }

    private void showSettingsView() {
        loadContent(new CustomerSettingsController().createView());
    }

    private void showProfileDetailsView() {
        loadContent(new CustomerProfileController().createView());
    }

    private void showLoansView() {
        loadContent(new CustomerLoansController().createView());
    }

    private void showSecuritySettingsView() {
        loadContent(new SecuritySettingsController(this::showDashboardHome).createView());
    }

    private void showSupportView() {
        loadContent(new SupportTicketController().createView());
    }

    private void openPersonalSettings() {
        loadContent(createPersonalSettingsView());
    }

    private Parent createPersonalSettingsView() {
        VBox page = new VBox(0);
        page.setPadding(new Insets(20));
        page.setMaxWidth(620);
        page.setBackground(new Background(new BackgroundFill(CARD_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Personal Settings");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
        titleLabel.setTextFill(TEXT_DARK);
        titleLabel.setPadding(new Insets(0, 0, 12, 0));

        VBox rows = new VBox(0);
        rows.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                new CornerRadii(12), new BorderWidths(1))));
        rows.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(12), Insets.EMPTY)));
        rows.getChildren().addAll(
                createPersonalSettingsRow("◉", "Account Information", this::showAccountDetailsView),
                createPersonalSettingsRow("≡", "Manage Limit", this::showLimitManagementView),
                createPersonalSettingsRow("⌘", "Change Password", this::showSecuritySettingsView),
                createPersonalSettingsRow("▧", "Manage Login Setting", this::showSecuritySettingsView),
                createPersonalSettingsRow("◌", "Beneficiary Management", this::showBeneficiaryView),
                createPersonalSettingsRow("@", "Manage Email Alert", this::showEmailAlertsInfo),
                createPersonalSettingsRow("✎", "Update Personal Information", this::showProfileDetailsView),
                createPersonalSettingsRow("⎇", "Subscription", this::showSubscriptionInfo),
                createPersonalSettingsRow("▧", "Manage Devices", this::showManageDevicesInfo),
                createPersonalSettingsRow("●", "F-PIN Generate / Change", this::showSettingsView),
                createPersonalSettingsRow("✓", "Dormant Account Activation", () -> showInfoScreen("Dormant Account Activation",
                        "Your account is active. Dormant activation is available here as an information screen."))
        );

        Button doneButton = createFilledActionButton("Done");
        doneButton.setOnAction(event -> showDashboardHome());
        VBox.setMargin(doneButton, new Insets(16, 0, 0, 0));

        page.getChildren().addAll(titleLabel, rows, doneButton);
        StackPane wrapper = new StackPane(page);
        wrapper.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        ScrollPane scrollPane = UiFactory.scroll(wrapper);
        scrollPane.setBackground(new Background(new BackgroundFill(PAGE_BACKGROUND, CornerRadii.EMPTY, Insets.EMPTY)));
        scrollPane.setBorder(Border.EMPTY);
        return scrollPane;
    }

    private Button createPersonalSettingsRow(String icon, String title, Runnable action) {
        HBox content = UiFactory.hbox(12, Pos.CENTER_LEFT);
        content.setPadding(new Insets(12, 12, 12, 12));

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        iconLabel.setTextFill(ACCENT_BLUE);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setMinWidth(26);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        titleLabel.setTextFill(TEXT_DARK);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Label chevronLabel = new Label(">");
        chevronLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        chevronLabel.setTextFill(TEXT_MUTED);

        content.getChildren().addAll(iconLabel, titleLabel, chevronLabel);

        Button rowButton = new Button();
        rowButton.setGraphic(content);
        rowButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        rowButton.setMaxWidth(Double.MAX_VALUE);
        rowButton.setMinHeight(48);
        rowButton.setPadding(Insets.EMPTY);
        rowButton.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        rowButton.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        rowButton.setOnAction(event -> action.run());
        return rowButton;
    }

    private Button createFilledActionButton(String text) {
        Button button = UiFactory.fullWidthButton(text);
        button.setMinHeight(44);
        button.setFont(Font.font("System", FontWeight.BOLD, 14));
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(ACCENT_BLUE, new CornerRadii(18), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(CARD_DARK_BLUE, BorderStrokeStyle.SOLID,
                new CornerRadii(18), new BorderWidths(1))));
        return button;
    }

    private void openAtmLocator() {
        VBox page = infoPage("ATM Locator",
                "Static branch and ATM list for the desktop demo.",
                List.of(
                        "Karachi: I.I. Chundrigar Road ATM - 24/7",
                        "Lahore: Gulberg Main Boulevard ATM - 24/7",
                        "Islamabad: Blue Area Branch ATM - 24/7",
                        "Rawalpindi: Saddar Branch ATM - 09:00 AM to 05:00 PM",
                        "Peshawar: University Road ATM - 24/7"
                ));
        loadContent(UiFactory.scroll(page));
    }

    private void showEmailAlertsInfo() {
        showInfoScreen("Manage Email Alert",
                "Email alerts are simulated. Transaction receipts and account notices can be sent to your registered email in a future version.");
    }

    private void showSubscriptionInfo() {
        showInfoScreen("Subscription",
                "Subscription settings are simulated for SMS alerts, e-statements, and campaign messages.");
    }

    private void showManageDevicesInfo() {
        showInfoScreen("Manage Devices",
                "Registered device management is simulated. Current desktop device: Trusted Demo Device.");
    }

    private void showDiscountsInfo() {
        VBox page = infoPage("Card Discounts",
                "Static offers prepared for the Askari mobile-style catalog.",
                List.of(
                        "Travel: Save on selected hotel bookings.",
                        "Entertainment: Discounted ticket purchases through Askari services.",
                        "Dining: Weekend card offers at selected merchants.",
                        "Shopping: Seasonal card promotions and Askari Edge benefits."
                ));
        loadContent(UiFactory.scroll(page));
    }

    private void showAboutAskari() {
        showInfoScreen("About Askari Digital",
                "Askari Bank Desktop Banking System - JavaFX semester project with file-based data storage and simulated mobile banking services.");
    }

    private void showInfoScreen(String title, String detail) {
        loadContent(UiFactory.scroll(infoPage(title, detail, List.of())));
    }

    private VBox infoPage(String title, String detail, List<String> rows) {
        VBox page = UiFactory.page();
        page.setMaxWidth(700);

        HBox header = UiFactory.hbox(12, Pos.CENTER_LEFT);
        Button backButton = UiFactory.button("Back");
        backButton.setOnAction(event -> showDashboardHome());
        Label titleLabel = UiFactory.title(title);
        header.getChildren().addAll(backButton, titleLabel);

        Label detailLabel = new Label(detail);
        detailLabel.setWrapText(true);
        detailLabel.setTextFill(TEXT_MUTED);

        VBox listBox = new VBox(8);
        for (String row : rows) {
            Label rowLabel = new Label(row);
            rowLabel.setWrapText(true);
            rowLabel.setPadding(new Insets(10, 12, 10, 12));
            rowLabel.setTextFill(TEXT_DARK);
            rowLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
            rowLabel.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                    new CornerRadii(10), new BorderWidths(1))));
            listBox.getChildren().add(rowLabel);
        }

        page.getChildren().addAll(header, detailLabel, listBox);
        return page;
    }

    private void showComingSoon(String title, String message) {
        AlertHelper.showInfo(title, message);
    }

    private void openAllServicesDialog() {
        showServiceDialog("All Services", allServiceItems);
    }

    private void openSearchResultsDialog(String searchText) {
        String query = searchText == null ? "" : searchText.trim().toLowerCase();
        if (query.isEmpty()) {
            AlertHelper.showInfo("Search", "Type a service name, then press Enter.");
            return;
        }

        List<ServiceItem> matches = allServiceItems.stream()
                .filter(item -> item.matches(query))
                .toList();
        if (matches.isEmpty()) {
            AlertHelper.showInfo("Search", "No matching service was found.");
            return;
        }

        showServiceDialog("Search Results", matches);
    }

    private void showServiceDialog(String title, List<ServiceItem> services) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox listBox = new VBox(6);
        listBox.setPadding(new Insets(10));
        for (ServiceItem item : services) {
            Button row = createDialogServiceRow(item);
            listBox.getChildren().add(row);
        }

        ScrollPane scrollPane = UiFactory.scroll(listBox);
        scrollPane.setPrefSize(420, 420);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.showAndWait();
    }

    private Button createDialogServiceRow(ServiceItem item) {
        HBox content = UiFactory.hbox(10, Pos.CENTER_LEFT);
        Node iconNode = IconFactory.createServiceIcon(item.iconFileName(), 24, ACCENT_BLUE);
        Label titleLabel = new Label(item.title());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
        titleLabel.setTextFill(TEXT_DARK);
        content.getChildren().addAll(iconNode, titleLabel);

        Button button = UiFactory.fullWidthButton("");
        button.setGraphic(content);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(9, 10, 9, 10));
        button.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(9), Insets.EMPTY)));
        button.setBorder(new Border(new BorderStroke(SOFT_BORDER, BorderStrokeStyle.SOLID,
                new CornerRadii(9), new BorderWidths(1))));
        button.setOnAction(event -> {
            Node source = (Node) event.getSource();
            Stage dialogStage = (Stage) source.getScene().getWindow();
            dialogStage.close();
            item.action().run();
        });
        return button;
    }

    private void updateBottomNavBadges() {
        if (messageBadgeLabel == null) {
            return;
        }

        Customer customer = LoginSession.getLoggedInCustomer();
        long unreadCount = customer == null ? 0 : NotificationService.getUnreadCount(customer.getAccountNumber());
        if (unreadCount > 0) {
            messageBadgeLabel.setText(unreadCount > 9 ? "9+" : Long.toString(unreadCount));
            messageBadgeLabel.setVisible(true);
            messageBadgeLabel.setManaged(true);
        } else {
            messageBadgeLabel.setText("");
            messageBadgeLabel.setVisible(false);
            messageBadgeLabel.setManaged(false);
        }
    }

    private void handleShareAccountSummary() {
        Customer customer = LoginSession.getLoggedInCustomer();
        if (customer == null) {
            AlertHelper.showError("Share Account", "No customer is logged in.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Account Summary");
        fileChooser.setInitialFileName("account_summary_" + customer.getAccountNumber() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(contentArea.getScene().getWindow());
        if (file == null) {
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(BankDataStore.appName + System.lineSeparator());
            writer.write("Account Summary" + System.lineSeparator());
            writer.write("Date: " + LocalDate.now() + System.lineSeparator());
            writer.write("Customer: " + customer.getCustomerFullName() + System.lineSeparator());
            writer.write("Account Type: " + customer.getAccountType() + System.lineSeparator());
            writer.write("Masked Account: " + maskAccountNumber(customer.getAccountNumber()) + System.lineSeparator());
            writer.write("Balance: " + (balanceVisible
                    ? String.format("PKR %,.2f", customer.getAccountBalance())
                    : "PKR XXXXXXX") + System.lineSeparator());
            AlertHelper.showInfo("Share Account", "Account summary exported successfully.");
        } catch (IOException exception) {
            AlertHelper.showError("Share Account", "Account summary could not be exported.");
        }
    }

    private void confirmLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Logout from Askari Digital?");
        alert.setContentText("Your current customer session will be closed.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            logoutFromSessionTimeout();
        }
    }

    private void logoutFromSessionTimeout() {
        if (sessionTimeout != null) {
            sessionTimeout.stop();
        }
        LoginSession.clear();
        Stage stage = (Stage) rootStack.getScene().getWindow();
        BankScreens.replaceScene(stage, BankScreens.login());
    }

    private void loadContent(Parent view) {
        contentArea.getChildren().setAll(view);
        updateBottomNavBadges();
    }

    private void showSimpleMessage(String message) {
        Label label = new Label(message);
        label.setFont(Font.font("System", FontWeight.BOLD, 15));
        label.setTextFill(TEXT_DARK);
        contentArea.getChildren().setAll(label);
    }

    private record ServiceItem(String title, String iconFileName, Runnable action) {

        private boolean matches(String query) {
            String titleText = title.toLowerCase();
            return titleText.contains(query)
                    || query.contains(titleText)
                    || matchesKeyword(query, titleText);
        }

        private boolean matchesKeyword(String query, String titleText) {
            return titleText.contains("send") && (query.contains("transfer") || query.contains("instant"))
                    || titleText.contains("bill") && query.contains("pay")
                    || titleText.contains("card") && query.contains("card")
                    || titleText.contains("m-tag") && (query.contains("mtag") || query.contains("m-tag"))
                    || titleText.contains("top-up") && (query.contains("topup") || query.contains("mobile"))
                    || titleText.contains("advance") && query.contains("loan")
                    || titleText.contains("history") && (query.contains("statement") || query.contains("transaction"))
                    || titleText.contains("beneficiary") && query.contains("payee")
                    || titleText.contains("raast") && query.contains("id")
                    || titleText.contains("change password") && query.contains("security");
        }
    }
}
