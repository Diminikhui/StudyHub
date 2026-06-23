package ru.mirigrushek.ui;

import ru.mirigrushek.data.Database;
import ru.mirigrushek.model.OrderSummary;
import ru.mirigrushek.model.UserSession;
import ru.mirigrushek.util.AppColors;
import ru.mirigrushek.util.Ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class OrderPanel extends JPanel {
    private final Database database;
    private final UserSession session;
    private final DefaultTableModel model;
    private final JTable table;
    private List<OrderSummary> orders = new ArrayList<>();

    public OrderPanel(Database database, UserSession session) {
        super(new BorderLayout(10, 10));
        this.database = database;
        this.session = session;
        setBackground(AppColors.PRIMARY_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Список заказов");
        Ui.titleFont(title, 22);
        header.add(title, BorderLayout.WEST);

        if (session.isAdmin()) {
            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            buttons.setOpaque(false);
            JButton add = Ui.button("Добавить заказ");
            JButton edit = Ui.button("Редактировать");
            JButton delete = Ui.button("Удалить");
            JButton refresh = Ui.button("Обновить");
            add.addActionListener(event -> openDialog(null));
            edit.addActionListener(event -> editSelected());
            delete.addActionListener(event -> deleteSelected());
            refresh.addActionListener(event -> reload());
            buttons.add(add);
            buttons.add(edit);
            buttons.add(delete);
            buttons.add(refresh);
            header.add(buttons, BorderLayout.EAST);
        }
        add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{
                        "Номер",
                        "Состав заказа",
                        "Дата заказа",
                        "Дата доставки",
                        "Пункт выдачи",
                        "Клиент",
                        "Код",
                        "Статус"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(4).setPreferredWidth(280);
        table.getColumnModel().getColumn(5).setPreferredWidth(210);
        if (session.isAdmin()) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        editSelected();
                    }
                }
            });
        }
        add(new JScrollPane(table), BorderLayout.CENTER);
        reload();
    }

    private void reload() {
        try {
            orders = database.listOrders();
            model.setRowCount(0);
            for (OrderSummary order : orders) {
                model.addRow(new Object[]{
                        order.orderNumber(),
                        order.items(),
                        order.orderDate(),
                        order.deliveryDate(),
                        order.pickupAddress(),
                        order.clientName(),
                        order.pickupCode(),
                        order.status()
                });
            }
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }

    private OrderSummary selectedOrder() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            Ui.showError(this, "Выберите заказ в таблице.");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        return orders.get(modelRow);
    }

    private void editSelected() {
        OrderSummary order = selectedOrder();
        if (order != null) {
            openDialog(order.id());
        }
    }

    private void openDialog(Long orderId) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        OrderDialog dialog = new OrderDialog(owner, database, orderId);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            reload();
        }
    }

    private void deleteSelected() {
        OrderSummary order = selectedOrder();
        if (order == null) {
            return;
        }
        if (!Ui.confirm(this, "Удалить заказ №" + order.orderNumber() + "?\nОперацию невозможно отменить.")) {
            return;
        }
        try {
            database.deleteOrder(order.id());
            reload();
            Ui.showInfo(this, "Заказ удалён.");
        } catch (Exception error) {
            Ui.showError(this, error);
        }
    }
}
