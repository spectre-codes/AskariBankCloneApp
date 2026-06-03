package com.askaribank;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

final class IconFactory {

    private static final Color DEFAULT_BLUE = Color.rgb(23, 117, 209);
    private static final Set<String> MISSING_ICON_WARNINGS = new HashSet<>();

    private IconFactory() {
    }

    static Node createServiceIcon(String iconKey, double size) {
        return createServiceIcon(iconKey, size, DEFAULT_BLUE);
    }

    static Node createServiceIcon(String iconKey, double size, Color strokeColor) {
        ImageView pngIcon = loadPngIcon(toPngFileName(iconKey), size);
        if (pngIcon != null) {
            return pngIcon;
        }

        return createFallbackLineIcon(toIconKey(iconKey), size, strokeColor);
    }

    static ImageView loadPngIcon(String fileName, double size) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        String pngFileName = toPngFileName(fileName);
        try (InputStream iconStream = IconFactory.class.getResourceAsStream("/icons/" + pngFileName)) {
            if (iconStream == null) {
                warnMissingIcon(pngFileName);
                return null;
            }

            ImageView imageView = new ImageView(new Image(iconStream));
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            return imageView;
        } catch (Exception exception) {
            warnMissingIcon(pngFileName);
            return null;
        }
    }

    static Node createFallbackLineIcon(String iconKey, double size) {
        return createFallbackLineIcon(iconKey, size, DEFAULT_BLUE);
    }

    static Node createFallbackLineIcon(String iconKey, double size, Color strokeColor) {
        String key = toIconKey(iconKey);
        Pane icon = createIconPane(size);

        switch (key) {
            case "send_money" -> drawSendMoney(icon, size, strokeColor);
            case "pay_bill" -> drawReceipt(icon, size, strokeColor);
            case "mtag" -> drawMtag(icon, size, strokeColor);
            case "card_discounts" -> drawCardDiscount(icon, size, strokeColor);
            case "bill_split" -> drawBillSplit(icon, size, strokeColor);
            case "buy_tickets" -> drawTicket(icon, size, strokeColor);
            case "foundation_securities" -> drawFoundation(icon, size, strokeColor);
            case "guest_room" -> drawGuestRoom(icon, size, strokeColor);
            case "beneficiary" -> drawBeneficiary(icon, size, strokeColor);
            case "scan_to_pay" -> drawScanToPay(icon, size, strokeColor);
            case "mobile_packages" -> drawMobilePackage(icon, size, strokeColor);
            case "education" -> drawEducation(icon, size, strokeColor);
            case "donations" -> drawDonations(icon, size, strokeColor);
            case "manage_limit" -> drawManageLimit(icon, size, strokeColor);
            case "cards_management" -> drawCardsManagement(icon, size, strokeColor);
            case "raast" -> drawRaast(icon, size, strokeColor);
            case "cardless_cash" -> drawCardlessCash(icon, size, strokeColor);
            case "ask_advance" -> drawMoneyNote(icon, size, strokeColor);
            case "transaction_history" -> drawTransactionHistory(icon, size, strokeColor);
            case "mobile_topup" -> drawMobileTopUp(icon, size, strokeColor);
            case "fpin" -> drawFPin(icon, size, strokeColor);
            case "manage_devices" -> drawManageDevices(icon, size, strokeColor);
            case "change_password" -> drawLock(icon, size, strokeColor);
            case "certificates" -> drawCertificate(icon, size, strokeColor);
            case "email_alerts", "message_box", "subscription" -> drawEnvelope(icon, size, strokeColor);
            case "cheques" -> drawCheque(icon, size, strokeColor);
            case "instant_payment" -> drawInstantPayment(icon, size, strokeColor);
            case "spending_insight" -> drawSpendingInsight(icon, size, strokeColor);
            case "askari_edge" -> drawAskariEdge(icon, size, strokeColor);
            case "atm_locator" -> drawAtmLocator(icon, size, strokeColor);
            case "home_askari" -> drawHomeAskari(icon, size, strokeColor);
            case "transaction_alerts" -> drawBell(icon, size, strokeColor);
            case "profile" -> drawProfile(icon, size, strokeColor);
            default -> drawGenericService(icon, size, strokeColor);
        }

        return icon;
    }

    private static void drawSendMoney(Pane icon, double size, Color color) {
        add(icon, circle(size, 18, 18, 13, color));
        add(icon, line(size, 12, 22, 24, 10, color));
        add(icon, line(size, 24, 10, 23, 17, color));
        add(icon, line(size, 24, 10, 17, 11, color));
    }

    private static void drawReceipt(Pane icon, double size, Color color) {
        add(icon, rect(size, 9, 5, 18, 26, 2, color));
        add(icon, line(size, 13, 12, 23, 12, color));
        add(icon, line(size, 13, 18, 22, 18, color));
        add(icon, line(size, 13, 24, 20, 24, color));
    }

    private static void drawMtag(Pane icon, double size, Color color) {
        add(icon, rect(size, 6, 9, 24, 18, 4, color));
        add(icon, line(size, 10, 14, 26, 14, color));
        add(icon, text(size, "ONE", color, 9, 7, 12, 22, 12));
    }

    private static void drawCardDiscount(Pane icon, double size, Color color) {
        add(icon, rect(size, 5, 9, 26, 18, 3, color));
        add(icon, line(size, 5, 15, 31, 15, color));
        add(icon, circle(size, 13, 21, 2, color));
        add(icon, circle(size, 23, 21, 2, color));
        add(icon, line(size, 14, 25, 24, 17, color));
    }

    private static void drawBillSplit(Pane icon, double size, Color color) {
        add(icon, rect(size, 13, 8, 10, 18, 2, color));
        add(icon, line(size, 18, 26, 10, 31, color));
        add(icon, line(size, 10, 31, 12, 26, color));
        add(icon, line(size, 10, 31, 15, 31, color));
        add(icon, line(size, 18, 26, 26, 31, color));
        add(icon, line(size, 26, 31, 24, 26, color));
        add(icon, line(size, 26, 31, 21, 31, color));
    }

    private static void drawTicket(Pane icon, double size, Color color) {
        add(icon, rect(size, 5, 11, 26, 15, 3, color));
        add(icon, line(size, 14, 11, 14, 26, color));
        add(icon, circle(size, 9, 18.5, 1.5, color));
        add(icon, circle(size, 26, 18.5, 1.5, color));
    }

    private static void drawFoundation(Pane icon, double size, Color color) {
        add(icon, polygon(size, color, 6, 14, 18, 7, 30, 14));
        add(icon, rect(size, 8, 14, 20, 16, 1, color));
        add(icon, line(size, 13, 18, 13, 28, color));
        add(icon, line(size, 18, 18, 18, 28, color));
        add(icon, line(size, 23, 18, 23, 28, color));
        add(icon, line(size, 10, 30, 26, 30, color));
    }

    private static void drawGuestRoom(Pane icon, double size, Color color) {
        add(icon, polygon(size, color, 7, 16, 18, 8, 29, 16));
        add(icon, rect(size, 10, 16, 16, 13, 1, color));
        add(icon, rect(size, 13, 22, 10, 7, 1, color));
        add(icon, line(size, 13, 22, 23, 22, color));
    }

    private static void drawBeneficiary(Pane icon, double size, Color color) {
        add(icon, circle(size, 13, 12, 4, color));
        add(icon, arc(size, 13, 24, 8, 7, 25, 130, color));
        add(icon, circle(size, 24, 14, 3.5, color));
        add(icon, arc(size, 24, 25, 7, 6, 30, 120, color));
        add(icon, polygon(size, color, 19, 20, 24, 18, 29, 20, 28, 27, 24, 30, 20, 27));
    }

    private static void drawScanToPay(Pane icon, double size, Color color) {
        add(icon, line(size, 7, 12, 7, 7, color));
        add(icon, line(size, 7, 7, 12, 7, color));
        add(icon, line(size, 24, 7, 29, 7, color));
        add(icon, line(size, 29, 7, 29, 12, color));
        add(icon, line(size, 7, 24, 7, 29, color));
        add(icon, line(size, 7, 29, 12, 29, color));
        add(icon, line(size, 24, 29, 29, 29, color));
        add(icon, line(size, 29, 29, 29, 24, color));
        add(icon, rect(size, 13, 13, 4, 4, 1, color));
        add(icon, rect(size, 20, 13, 4, 4, 1, color));
        add(icon, rect(size, 13, 20, 4, 4, 1, color));
        add(icon, line(size, 20, 22, 25, 22, color));
    }

    private static void drawMobilePackage(Pane icon, double size, Color color) {
        add(icon, rect(size, 11, 5, 14, 26, 3, color));
        add(icon, line(size, 15, 27, 21, 27, color));
        add(icon, polygon(size, color, 26, 8, 28, 12, 32, 12, 29, 15, 30, 19, 26, 16, 22, 19, 23, 15, 20, 12, 24, 12));
    }

    private static void drawEducation(Pane icon, double size, Color color) {
        add(icon, line(size, 18, 11, 18, 29, color));
        add(icon, polygon(size, color, 7, 9, 18, 13, 18, 29, 7, 25));
        add(icon, polygon(size, color, 29, 9, 18, 13, 18, 29, 29, 25));
        add(icon, line(size, 10, 15, 15, 17, color));
        add(icon, line(size, 21, 17, 26, 15, color));
    }

    private static void drawDonations(Pane icon, double size, Color color) {
        add(icon, line(size, 7, 25, 15, 25, color));
        add(icon, line(size, 15, 25, 21, 21, color));
        add(icon, line(size, 15, 21, 21, 21, color));
        add(icon, line(size, 21, 21, 28, 17, color));
        add(icon, circle(size, 15, 12, 3, color));
        add(icon, circle(size, 21, 12, 3, color));
        add(icon, polygon(size, color, 12, 13, 24, 13, 18, 21));
    }

    private static void drawManageLimit(Pane icon, double size, Color color) {
        add(icon, line(size, 8, 11, 28, 11, color));
        add(icon, circle(size, 14, 11, 3, color));
        add(icon, line(size, 8, 18, 28, 18, color));
        add(icon, circle(size, 22, 18, 3, color));
        add(icon, line(size, 8, 25, 28, 25, color));
        add(icon, circle(size, 17, 25, 3, color));
    }

    private static void drawCardsManagement(Pane icon, double size, Color color) {
        add(icon, rect(size, 6, 12, 22, 14, 3, color));
        add(icon, rect(size, 10, 8, 20, 13, 3, color));
        add(icon, circle(size, 25, 25, 4, color));
        add(icon, line(size, 25, 18, 25, 21, color));
        add(icon, line(size, 25, 29, 25, 32, color));
        add(icon, line(size, 18, 25, 21, 25, color));
        add(icon, line(size, 29, 25, 32, 25, color));
    }

    private static void drawRaast(Pane icon, double size, Color color) {
        add(icon, arc(size, 18, 21, 11, 8, 0, 180, color));
        add(icon, line(size, 7, 21, 29, 21, color));
        add(icon, line(size, 10, 26, 26, 26, color));
        add(icon, text(size, "Raast", color, 7, 4, 10, 28, 8));
    }

    private static void drawCardlessCash(Pane icon, double size, Color color) {
        add(icon, rect(size, 7, 6, 22, 24, 3, color));
        add(icon, rect(size, 11, 10, 14, 6, 1, color));
        add(icon, rect(size, 12, 20, 12, 5, 1, color));
        add(icon, line(size, 15, 22.5, 21, 22.5, color));
    }

    private static void drawMoneyNote(Pane icon, double size, Color color) {
        add(icon, rect(size, 6, 10, 24, 16, 2, color));
        add(icon, circle(size, 18, 18, 4, color));
        add(icon, line(size, 10, 15, 12, 15, color));
        add(icon, line(size, 24, 21, 26, 21, color));
    }

    private static void drawTransactionHistory(Pane icon, double size, Color color) {
        add(icon, rect(size, 8, 6, 18, 24, 2, color));
        add(icon, line(size, 12, 13, 22, 13, color));
        add(icon, line(size, 12, 18, 20, 18, color));
        add(icon, circle(size, 25, 25, 6, color));
        add(icon, line(size, 25, 22, 25, 25, color));
        add(icon, line(size, 25, 25, 28, 27, color));
    }

    private static void drawMobileTopUp(Pane icon, double size, Color color) {
        add(icon, rect(size, 11, 5, 14, 26, 3, color));
        add(icon, line(size, 18, 24, 18, 12, color));
        add(icon, line(size, 18, 12, 14, 16, color));
        add(icon, line(size, 18, 12, 22, 16, color));
    }

    private static void drawFPin(Pane icon, double size, Color color) {
        add(icon, rect(size, 10, 5, 16, 26, 3, color));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                add(icon, filledCircle(size, 14 + column * 4, 15 + row * 4, 0.8, color));
            }
        }
    }

    private static void drawManageDevices(Pane icon, double size, Color color) {
        add(icon, rect(size, 9, 5, 13, 25, 3, color));
        add(icon, rect(size, 19, 13, 10, 14, 2, color));
        add(icon, circle(size, 27, 10, 3, color));
        add(icon, line(size, 27, 5, 27, 7, color));
        add(icon, line(size, 27, 13, 27, 15, color));
    }

    private static void drawLock(Pane icon, double size, Color color) {
        add(icon, arc(size, 18, 16, 8, 8, 0, 180, color));
        add(icon, rect(size, 9, 16, 18, 13, 3, color));
        add(icon, line(size, 18, 21, 18, 25, color));
    }

    private static void drawCertificate(Pane icon, double size, Color color) {
        add(icon, rect(size, 9, 5, 18, 25, 2, color));
        add(icon, line(size, 13, 12, 23, 12, color));
        add(icon, line(size, 13, 17, 21, 17, color));
        add(icon, polygon(size, color, 18, 22, 22, 20, 26, 22, 24, 30, 22, 27, 20, 30));
    }

    private static void drawEnvelope(Pane icon, double size, Color color) {
        add(icon, rect(size, 6, 11, 24, 16, 2, color));
        add(icon, line(size, 7, 12, 18, 21, color));
        add(icon, line(size, 29, 12, 18, 21, color));
        add(icon, line(size, 7, 27, 15, 19, color));
        add(icon, line(size, 29, 27, 21, 19, color));
    }

    private static void drawCheque(Pane icon, double size, Color color) {
        add(icon, rect(size, 5, 10, 26, 16, 2, color));
        add(icon, line(size, 9, 15, 17, 15, color));
        add(icon, line(size, 9, 20, 22, 20, color));
        add(icon, line(size, 23, 23, 29, 18, color));
    }

    private static void drawInstantPayment(Pane icon, double size, Color color) {
        add(icon, line(size, 8, 13, 27, 13, color));
        add(icon, line(size, 27, 13, 22, 8, color));
        add(icon, line(size, 27, 13, 22, 18, color));
        add(icon, line(size, 28, 23, 9, 23, color));
        add(icon, line(size, 9, 23, 14, 18, color));
        add(icon, line(size, 9, 23, 14, 28, color));
    }

    private static void drawSpendingInsight(Pane icon, double size, Color color) {
        add(icon, line(size, 8, 28, 29, 28, color));
        add(icon, line(size, 8, 28, 8, 8, color));
        add(icon, rect(size, 12, 20, 3, 8, 1, color));
        add(icon, rect(size, 18, 15, 3, 13, 1, color));
        add(icon, rect(size, 24, 10, 3, 18, 1, color));
    }

    private static void drawAskariEdge(Pane icon, double size, Color color) {
        add(icon, polygon(size, color, 18, 5, 31, 18, 18, 31, 5, 18));
        add(icon, line(size, 18, 10, 25, 18, color));
        add(icon, line(size, 25, 18, 18, 26, color));
        add(icon, line(size, 18, 10, 11, 18, color));
    }

    private static void drawAtmLocator(Pane icon, double size, Color color) {
        add(icon, circle(size, 18, 14, 7, color));
        add(icon, polygon(size, color, 12, 18, 18, 31, 24, 18));
        add(icon, circle(size, 18, 14, 2.5, color));
    }

    private static void drawHomeAskari(Pane icon, double size, Color color) {
        add(icon, polygon(size, color, 18, 5, 31, 18, 18, 31, 5, 18));
        add(icon, polygon(size, color, 12, 19, 18, 13, 24, 19, 24, 26, 12, 26));
    }

    private static void drawBell(Pane icon, double size, Color color) {
        add(icon, arc(size, 18, 18, 9, 10, 0, 180, color));
        add(icon, line(size, 9, 18, 9, 25, color));
        add(icon, line(size, 27, 18, 27, 25, color));
        add(icon, line(size, 8, 25, 28, 25, color));
        add(icon, circle(size, 18, 29, 2, color));
        add(icon, line(size, 29, 9, 29, 16, color));
        add(icon, filledCircle(size, 29, 21, 1.2, color));
    }

    private static void drawProfile(Pane icon, double size, Color color) {
        add(icon, circle(size, 18, 12, 6, color));
        add(icon, arc(size, 18, 28, 12, 10, 25, 130, color));
    }

    private static void drawGenericService(Pane icon, double size, Color color) {
        add(icon, circle(size, 18, 18, 12, color));
        add(icon, line(size, 12, 18, 24, 18, color));
        add(icon, line(size, 18, 12, 18, 24, color));
    }

    private static Pane createIconPane(double size) {
        Pane pane = new Pane();
        pane.setMinSize(size, size);
        pane.setPrefSize(size, size);
        pane.setMaxSize(size, size);
        return pane;
    }

    private static String toPngFileName(String iconKey) {
        String cleaned = iconKey == null ? "" : iconKey.trim();
        if (cleaned.endsWith(".png")) {
            return cleaned;
        }
        return cleaned + ".png";
    }

    private static String toIconKey(String iconKey) {
        String cleaned = iconKey == null ? "" : iconKey.trim().toLowerCase();
        if (cleaned.endsWith(".png")) {
            return cleaned.substring(0, cleaned.length() - 4);
        }
        return cleaned;
    }

    private static void warnMissingIcon(String fileName) {
        if (MISSING_ICON_WARNINGS.add(fileName)) {
            System.out.println("Warning: missing icon resource /icons/" + fileName + ". Using JavaFX line icon fallback.");
        }
    }

    private static void add(Pane pane, Node node) {
        pane.getChildren().add(node);
    }

    private static double p(double size, double value) {
        return size * value / 36.0;
    }

    private static double stroke(double size) {
        return Math.max(1.5, size / 16.0);
    }

    private static Line line(double size, double startX, double startY, double endX, double endY, Color color) {
        Line line = new Line(p(size, startX), p(size, startY), p(size, endX), p(size, endY));
        line.setStroke(color);
        line.setStrokeWidth(stroke(size));
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        return line;
    }

    private static Rectangle rect(double size, double x, double y, double width, double height, double arc, Color color) {
        Rectangle rectangle = new Rectangle(p(size, x), p(size, y), p(size, width), p(size, height));
        rectangle.setArcWidth(p(size, arc));
        rectangle.setArcHeight(p(size, arc));
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(color);
        rectangle.setStrokeWidth(stroke(size));
        return rectangle;
    }

    private static Circle circle(double size, double centerX, double centerY, double radius, Color color) {
        Circle circle = new Circle(p(size, centerX), p(size, centerY), p(size, radius));
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(color);
        circle.setStrokeWidth(stroke(size));
        return circle;
    }

    private static Circle filledCircle(double size, double centerX, double centerY, double radius, Color color) {
        Circle circle = new Circle(p(size, centerX), p(size, centerY), p(size, radius));
        circle.setFill(color);
        circle.setStroke(Color.TRANSPARENT);
        return circle;
    }

    private static Arc arc(double size, double centerX, double centerY, double radiusX, double radiusY,
                           double startAngle, double length, Color color) {
        Arc arc = new Arc(p(size, centerX), p(size, centerY), p(size, radiusX), p(size, radiusY), startAngle, length);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(color);
        arc.setStrokeWidth(stroke(size));
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        arc.setType(ArcType.OPEN);
        return arc;
    }

    private static Polygon polygon(double size, Color color, double... points) {
        Polygon polygon = new Polygon();
        for (double point : points) {
            polygon.getPoints().add(p(size, point));
        }
        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(color);
        polygon.setStrokeWidth(stroke(size));
        polygon.setStrokeLineJoin(StrokeLineJoin.ROUND);
        return polygon;
    }

    private static Label text(double size, String value, Color color, double fontSize,
                              double x, double y, double width, double height) {
        Label label = new Label(value);
        label.setFont(Font.font("System", FontWeight.BOLD, p(size, fontSize)));
        label.setTextFill(color);
        label.setAlignment(Pos.CENTER);
        label.setMinSize(p(size, width), p(size, height));
        label.setPrefSize(p(size, width), p(size, height));
        label.setLayoutX(p(size, x));
        label.setLayoutY(p(size, y));
        return label;
    }
}
