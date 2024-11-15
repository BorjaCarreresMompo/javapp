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
    private static final String FILE_PATH = "productos.csv"; // Ruta del archivo
    private ArrayList<String> cobrosList = new ArrayList<>(); // Lista para registros de cobros

    public CannaGrowApp() {
        setTitle("CannaGrow - Gestión de Productos");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel principal con fondo temático
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(34, 139, 34)); // Verde oscuro
        add(mainPanel);

        // Encabezado
        JLabel headerLabel = new JLabel("Gestión de Productos CannaGrow", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 250, 240)); // Blanco crema
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Modelo de la tabla
        tableModel = new DefaultTableModel(new String[]{"Nombre", "Tipo", "Stock", "Precio"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de entrada de datos
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBackground(new Color(50, 205, 50)); // Verde claro
        
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

        // Botones para agregar, eliminar y registrar cobros
        JButton addButton = new JButton("Agregar Producto");
        addButton.setBackground(new Color(60, 179, 113));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
            }
        });
        

        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.setBackground(new Color(255, 69, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });
        
        JButton cobrarButton = new JButton("Registrar Cobro");
        cobrarButton.setBackground(new Color(100, 149, 237));
        cobrarButton.setForeground(Color.WHITE);
        cobrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarCobro();
            }
        });

        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.add(cobrarButton);

        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        // Dentro del constructor de la clase CannaGrowApp, añade un botón para editar:
        JButton editButton = new JButton("Editar Producto");
        editButton.setBackground(new Color(255, 165, 0)); // Naranja
        editButton.setForeground(Color.WHITE);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarProducto();
            }
        });
        JButton viewHistoryButton = new JButton("Ver Historial de Ventas");
        viewHistoryButton.setBackground(new Color(30, 144, 255)); // Azul
        viewHistoryButton.setForeground(Color.WHITE);
        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verHistorialVentas();
            }
        });
        
        inputPanel.add(viewHistoryButton); // Añadir el botón al panel de entrada
        




inputPanel.add(editButton); // Añadir el botón al panel de entrada de datos
        // Cargar productos desde el archivo al iniciar la aplicación
        cargarProductos();
    }
    private void editarProducto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para editar.");
            return;
        }
    
        // Obtener valores actuales del producto seleccionado
        String nombreActual = tableModel.getValueAt(selectedRow, 0).toString();
        String tipoActual = tableModel.getValueAt(selectedRow, 1).toString();
        int stockActual = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        double precioActual = Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString());
    
        // Campos para editar
        JTextField nombreField = new JTextField(nombreActual);
        JTextField tipoField = new JTextField(tipoActual);
        JTextField stockField = new JTextField(String.valueOf(stockActual));
        JTextField precioField = new JTextField(String.valueOf(precioActual));
    
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
                String nuevoNombre = nombreField.getText();
                String nuevoTipo = tipoField.getText();
                int nuevoStock = Integer.parseInt(stockField.getText());
                double nuevoPrecio = Double.parseDouble(precioField.getText());
    
                // Actualizar valores en la tabla
                tableModel.setValueAt(nuevoNombre, selectedRow, 0);
                tableModel.setValueAt(nuevoTipo, selectedRow, 1);
                tableModel.setValueAt(nuevoStock, selectedRow, 2);
                tableModel.setValueAt(nuevoPrecio, selectedRow, 3);
    
                guardarProductos(); // Guardar los cambios en el archivo
                JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.");
    
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: Stock y precio deben ser numéricos.");
            }
        }
    }




    private void verHistorialVentas() {
        if (cobrosList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay registros de ventas en el historial.");
            return;
        }
    
        // Crear un panel para mostrar el historial
        JPanel historyPanel = new JPanel(new BorderLayout());
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    
        // Cargar las ventas en el área de texto
        StringBuilder historial = new StringBuilder();
        for (String registro : cobrosList) {
            historial.append(registro).append("\n");
        }
        historyArea.setText(historial.toString());
    
        // Añadir el área de texto al panel con un scroll
        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
    
        // Mostrar el cuadro de diálogo
        JOptionPane.showMessageDialog(this, historyPanel, "Historial de Ventas", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void agregarProducto() {
        String nombre = nameField.getText();
        String tipo = typeField.getText();
        int stock;
        double precio;

        try {
            stock = Integer.parseInt(stockField.getText());
            precio = Double.parseDouble(priceField.getText());

            // Añadir los datos a la tabla
            tableModel.addRow(new Object[]{nombre, tipo, stock, precio});
            limpiarCampos();
            
            // Guardar en archivo
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
            
            // Guardar cambios en archivo
            guardarProductos();

            JOptionPane.showMessageDialog(this, "Producto eliminado.");
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.");
        }
    }

    private void limpiarCampos() {
        nameField.setText("");
        typeField.setText("");
        stockField.setText("");
        priceField.setText("");
    }

    // Cargar productos desde el archivo
    private void cargarProductos() {
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                if (data.length == 4) {
                    String nombre = data[0];
                    String tipo = data[1];
                    int stock = Integer.parseInt(data[2]);
                    double precio = Double.parseDouble(data[3]);
                    tableModel.addRow(new Object[]{nombre, tipo, stock, precio});
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de productos no encontrado. Se creará uno nuevo al guardar.");
        }
    }

    // Guardar productos en el archivo
    private void guardarProductos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String nombre = tableModel.getValueAt(i, 0).toString();
                String tipo = tableModel.getValueAt(i, 1).toString();
                int stock = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                double precio = Double.parseDouble(tableModel.getValueAt(i, 3).toString());
                writer.println(nombre + "," + tipo + "," + stock + "," + precio);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar productos en el archivo.");
        }
    }

    // Método para registrar un cobro
    private void registrarCobro() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto para realizar el cobro.");
            return;
        }

        String nombreProducto = tableModel.getValueAt(selectedRow, 0).toString();
        int stockActual = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        double precioProducto = Double.parseDouble(tableModel.getValueAt(selectedRow, 3).toString());

        JTextField clienteField = new JTextField();
        JTextField cantidadField = new JTextField();
        String[] metodosPago = {"Efectivo", "Tarjeta"};
        JComboBox<String> metodoPagoBox = new JComboBox<>(metodosPago);

        JPanel cobroPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        cobroPanel.add(new JLabel("Nombre del Cliente:"));
        cobroPanel.add(clienteField);
        cobroPanel.add(new JLabel("Cantidad:"));
        cobroPanel.add(cantidadField);
        cobroPanel.add(new JLabel("Método de Pago:"));
        cobroPanel.add(metodoPagoBox);

        int result = JOptionPane.showConfirmDialog(this, cobroPanel, "Registrar Cobro - " + nombreProducto, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String cliente = clienteField.getText();
                int cantidad = Integer.parseInt(cantidadField.getText());
                String metodoPago = metodoPagoBox.getSelectedItem().toString();

                if (cantidad > stockActual) {
                    JOptionPane.showMessageDialog(this, "Error: No hay suficiente stock para la cantidad solicitada.");
                    return;
                }

                double montoTotal = cantidad * precioProducto;
                int nuevoStock = stockActual - cantidad;
                tableModel.setValueAt(nuevoStock, selectedRow, 2); // Actualizar el stock en la tabla
                guardarProductos(); // Guardar cambios en archivo

                String registroCobro = "Cliente: " + cliente + ", Producto: " + nombreProducto + ", Cantidad: " + cantidad + ", Monto: €" + montoTotal + ", Método: " + metodoPago;
                cobrosList.add(registroCobro); // Añadir a la lista de cobros
                JOptionPane.showMessageDialog(this, "Cobro registrado:\n" + registroCobro);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: La cantidad debe ser un número entero.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CannaGrowApp app = new CannaGrowApp();
            app.setVisible(true);
        });
    }
}
