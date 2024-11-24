import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class CannaGrowApp extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nameField, typeField, stockField, priceField;
    private static final String PRODUCTOS_FILE_PATH = "productos.csv";
    private static final String VENTAS_FILE_PATH = "historial_ventas.csv";
    private ArrayList<String> cobrosList = new ArrayList<>();

    public CannaGrowApp() {
        setTitle("CannaGrow - Gestión de Productos");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 34));
        add(mainPanel);

        JLabel headerLabel = new JLabel("Gestión de Productos CannaGrow", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 250, 240));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Nombre", "Tipo", "Stock", "Precio"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBackground(new Color(50, 205, 50));

        inputPanel.add(new JLabel("Nombre:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Tipo:"));
        typeField = new JTextField();
        inputPanel.add(typeField);

        inputPanel.add(new JLabel("Stock:"));
        stockField = new JTextField();
        inputPanel.add(stockField);

        inputPanel.add(new JLabel("Precio:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        JButton addButton = new JButton("Agregar Producto");
        addButton.addActionListener(e -> agregarProducto());
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.addActionListener(e -> eliminarProducto());
        inputPanel.add(deleteButton);

        JButton editButton = new JButton("Editar Producto");
        editButton.addActionListener(e -> editarProducto());
        inputPanel.add(editButton);

        JButton cobrarButton = new JButton("Registrar Cobro");
        cobrarButton.addActionListener(e -> registrarCobro());
        inputPanel.add(cobrarButton);

        JButton viewHistoryButton = new JButton("Ver Historial de Ventas");
        viewHistoryButton.addActionListener(e -> verHistorialVentas());
        inputPanel.add(viewHistoryButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        cargarProductos();
        cargarHistorialVentas();
    }

    private void agregarProducto() {
        String nombre = nameField.getText();
        String tipo = typeField.getText();
        try {
            int stock = Integer.parseInt(stockField.getText());
            double precio = Double.parseDouble(priceField.getText());
            tableModel.addRow(new Object[]{nombre, tipo, stock, precio});
            limpiarCampos();
            guardarProductos();
            JOptionPane.showMessageDialog(this, "Producto agregado exitosamente.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Stock y precio deben ser numéricos.");
        }
    }

    private void eliminarProducto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            guardarProductos();
            JOptionPane.showMessageDialog(this, "Producto eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
        }
    }

    private void editarProducto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar.");
            return;
        }

        JTextField nombreField = new JTextField(tableModel.getValueAt(selectedRow, 0).toString());
        JTextField tipoField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField stockField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        JTextField precioField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());

        JPanel editPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        editPanel.add(new JLabel("Nombre:"));
        editPanel.add(nombreField);
        editPanel.add(new JLabel("Tipo:"));
        editPanel.add(tipoField);
        editPanel.add(new JLabel("Stock:"));
        editPanel.add(stockField);
        editPanel.add(new JLabel("Precio:"));
        editPanel.add(precioField);

        int result = JOptionPane.showConfirmDialog(this, editPanel, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                tableModel.setValueAt(nombreField.getText(), selectedRow, 0);
                tableModel.setValueAt(tipoField.getText(), selectedRow, 1);
                tableModel.setValueAt(Integer.parseInt(stockField.getText()), selectedRow, 2);
                tableModel.setValueAt(Double.parseDouble(precioField.getText()), selectedRow, 3);
                guardarProductos();
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Stock y precio deben ser numéricos.");
            }
        }
    }

    private void registrarCobro() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para registrar el cobro.");
            return;
        }

        String nombreProducto = tableModel.getValueAt(selectedRow, 0).toString();
        int stockActual = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        double precioUnitario = Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString());

        JPanel cobroPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField clienteField = new JTextField();
        JTextField cantidadField = new JTextField();
        JTextField metodoPagoField = new JTextField();

        cobroPanel.add(new JLabel("Nombre del cliente:"));
        cobroPanel.add(clienteField);
        cobroPanel.add(new JLabel("Cantidad a cobrar:"));
        cobroPanel.add(cantidadField);
        cobroPanel.add(new JLabel("Método de pago:"));
        cobroPanel.add(metodoPagoField);

        int result = JOptionPane.showConfirmDialog(this, cobroPanel, "Registrar Cobro", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String cliente = clienteField.getText();
                int cantidad = Integer.parseInt(cantidadField.getText());
                String metodoPago = metodoPagoField.getText();

                if (cantidad > stockActual) {
                    JOptionPane.showMessageDialog(this, "No hay suficiente stock disponible.");
                    return;
                }

                double total = cantidad * precioUnitario;
                int nuevoStock = stockActual - cantidad;
                tableModel.setValueAt(nuevoStock, selectedRow, 2);

                String registro = "Cliente: " + cliente + ", Producto: " + nombreProducto + ", Cantidad: " + cantidad
                        + ", Total: $" + total + ", Método de Pago: " + metodoPago;
                cobrosList.add(registro);
                guardarHistorialVentas();
                guardarProductos();
                JOptionPane.showMessageDialog(this, "Cobro registrado:\n" + registro);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser un número válido.");
            }
        }
    }

    private void verHistorialVentas() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        cobrosList.forEach(cobro -> textArea.append(cobro + "\n"));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Historial de Ventas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarCampos() {
        nameField.setText("");
        typeField.setText("");
        stockField.setText("");
        priceField.setText("");
    }

    private void cargarProductos() {
        try (Scanner scanner = new Scanner(new File(PRODUCTOS_FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                tableModel.addRow(new Object[]{data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3])});
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de productos no encontrado.");
        }
    }

    private void guardarProductos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTOS_FILE_PATH))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.println(tableModel.getValueAt(i, 0) + "," + tableModel.getValueAt(i, 1) + ","
                        + tableModel.getValueAt(i, 2) + "," + tableModel.getValueAt(i, 3));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar los productos.");
        }
    }

    private void cargarHistorialVentas() {
        try (Scanner scanner = new Scanner(new File(VENTAS_FILE_PATH))) {
            while (scanner.hasNextLine()) {
                cobrosList.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de historial de ventas no encontrado.");
        }
    }

    private void guardarHistorialVentas() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VENTAS_FILE_PATH))) {
            for (String cobro : cobrosList) {
                writer.println(cobro);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el historial de ventas.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CannaGrowApp().setVisible(true));
    }
}
//pendiente arreglar fallos