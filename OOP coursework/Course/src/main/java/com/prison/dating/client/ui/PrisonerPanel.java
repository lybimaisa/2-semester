package main.java.com.prison.dating.client.ui;

import main.java.com.prison.dating.api.services.PrisonerService;
import main.java.com.prison.dating.api.services.VisitService;
import main.java.com.prison.dating.api.models.Contact;
import main.java.com.prison.dating.client.api.ApiClient;
import main.java.com.prison.dating.server.database.PrisonerDAO;
import main.java.com.prison.dating.server.entities.PrisonerEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class PrisonerPanel extends JFrame {
    private int prisonerId;
    private PrisonerService prisonerService;
    private VisitService visitService;
    private DefaultTableModel contactsTableModel;
    private DefaultTableModel visitsTableModel;
    private JTable contactsTable;
    private JTable visitsTable;


    public PrisonerPanel(int prisonerId) {
        this.prisonerId = prisonerId;

        try {
            // Сначала проверяем соединение с сервером
            if (!ApiClient.isServerAvailable()) {
                JOptionPane.showMessageDialog(null,
                        "Сервер недоступен!\n" +
                                "Проверьте, запущен ли Tomcat на localhost:8080",
                        "Ошибка подключения",
                        JOptionPane.ERROR_MESSAGE);
            }

            prisonerService = new PrisonerService();
            visitService = new VisitService();

            // Инициализируем GUI
            initComponents();

            // Загружаем данные в фоновом режиме
            SwingUtilities.invokeLater(() -> {
                try {
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PrisonerPanel.this,
                            "Ошибка загрузки данных: " + e.getMessage() +
                                    "\nВозможно, сервер недоступен.",
                            "Предупреждение", JOptionPane.WARNING_MESSAGE);
                }
            });

            setTitle("Личный кабинет заключенного #" + prisonerId);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1000, 600);
            setLocationRelativeTo(null);
            setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось создать PrisonerPanel: " + e.getMessage(), e);
        }
    }

    private void initComponents() {

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Мой профиль", createProfilePanel());
        tabbedPane.addTab("Мои контакты", createContactsPanel());
        tabbedPane.addTab("Мои заявки", createVisitsPanel());
        tabbedPane.addTab("Мои свидания", createDatesPanel());
        tabbedPane.addTab("Подать заявку", createRequestPanel());
        add(tabbedPane);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Мои данные"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("ЛИЧНАЯ ИНФОРМАЦИЯ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(titleLabel, gbc);

        gbc.gridy = 1; gbc.gridwidth = 2;
        infoPanel.add(new JSeparator(), gbc);

        gbc.gridwidth = 1; gbc.gridy = 2;

        gbc.gridx = 0;
        infoPanel.add(new JLabel("ID заключённого:"), gbc);
        gbc.gridx = 1;
        JLabel idLabel = new JLabel(String.valueOf(prisonerId));
        idLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        infoPanel.add(idLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1;
        JLabel nameLabel = new JLabel("Загрузка...");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(nameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        infoPanel.add(new JLabel("Личный номер:"), gbc);
        gbc.gridx = 1;
        JLabel numberLabel = new JLabel("Загрузка...");
        numberLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoPanel.add(numberLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        infoPanel.add(new JLabel("Дата рождения:"), gbc);
        gbc.gridx = 1;
        JLabel birthLabel = new JLabel("Загрузка...");
        birthLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoPanel.add(birthLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton refreshButton = new JButton("Обновить данные");
        refreshButton.addActionListener(e -> loadPrisonerInfo(nameLabel, numberLabel, birthLabel));
        infoPanel.add(refreshButton, gbc);

        JPanel limitsPanel = new JPanel(new BorderLayout(10, 10));
        limitsPanel.setBorder(BorderFactory.createTitledBorder("Мои лимиты свиданий"));

        JTextArea limitsArea = new JTextArea();
        limitsArea.setEditable(false);
        limitsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        limitsArea.setBackground(new Color(245, 245, 245));
        limitsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loadLimitsIntoTextArea(limitsArea);

        JScrollPane scrollPane = new JScrollPane(limitsArea);
        scrollPane.setPreferredSize(new Dimension(300, 250));

        JPanel limitsButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshLimitsButton = new JButton("Обновить лимиты");
        refreshLimitsButton.addActionListener(e -> loadLimitsIntoTextArea(limitsArea));
        limitsButtonPanel.add(refreshLimitsButton);

        limitsPanel.add(scrollPane, BorderLayout.CENTER);
        limitsPanel.add(limitsButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(infoPanel);
        contentPanel.add(limitsPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> loadPrisonerInfo(nameLabel, numberLabel, birthLabel));

        return panel;
    }

    private JPanel createDatesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("МОИ СВИДАНИЯ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Контакт", "Дата", "Тип", "Статус", "Примечание"};
        DefaultTableModel datesModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable datesTable = new JTable(datesModel);
        JScrollPane scrollPane = new JScrollPane(datesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton refreshButton = new JButton("🔄 Обновить");

        refreshButton.addActionListener(e -> {
            refreshButton.setEnabled(false);
            refreshButton.setText("Загрузка...");
            loadDatesData(datesModel);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        public void run() {
                            SwingUtilities.invokeLater(() -> {
                                refreshButton.setText("🔄 Обновить");
                                refreshButton.setEnabled(true);
                            });
                        }
                    },
                    2000
            );
        });

        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadDatesData(DefaultTableModel model) {
        model.setRowCount(0);
        model.addRow(new Object[]{"Загрузка...", "Получение данных", "", "", "", ""});

        new Thread(() -> {
            try {
                System.out.println("Запрос свиданий для prisonerId=" + prisonerId);

                // Пробуем API
                String response = ApiClient.getPrisonerVisits(prisonerId);
                System.out.println("Ответ API: " + (response != null ? response.length() : 0) + " символов");

                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0);

                    if (response == null || response.isEmpty()) {
                        model.addRow(new Object[]{
                                "Нет данных",
                                "Сервер не вернул данные",
                                "Проверьте подключение",
                                "", "", ""
                        });
                        return;
                    }

                    // Парсим ответ
                    List<DateItem> dates = parseDatesFromHTML(response);

                    if (dates.isEmpty()) {
                        model.addRow(new Object[]{
                                "Нет свиданий",
                                "У вас нет подтверждённых свиданий",
                                "Подайте заявку и дождитесь одобрения",
                                "", "", ""
                        });
                        return;
                    }

                    // Заполняем таблицу
                    for (DateItem date : dates) {
                        model.addRow(new Object[]{
                                date.id,
                                date.contactName,
                                date.visitDate.toString(),
                                date.visitType,
                                getStatusWithColor(date.status),
                                getDateNote(date)
                        });
                    }

                    System.out.println("Загружено свиданий: " + dates.size());
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0);
                    model.addRow(new Object[]{
                            "Ошибка",
                            "Не удалось загрузить данные",
                            e.getMessage(),
                            "", "", ""
                    });
                });
            }
        }).start();
    }

    // Парсинг ответа API
    private List<DateItem> parseDatesFromHTML(String html) {
        List<DateItem> dates = new ArrayList<>();

        try {
            // Ищем строки таблицы
            String[] rows = html.split("<tr>");

            for (String row : rows) {
                if (row.contains("<td>") && row.contains("</td>")) {
                    try {
                        // Извлекаем ячейки
                        List<String> cells = new ArrayList<>();
                        String temp = row;

                        while (temp.contains("<td>")) {
                            int start = temp.indexOf("<td>") + 4;
                            int end = temp.indexOf("</td>", start);

                            if (end != -1) {
                                String cell = temp.substring(start, end)
                                        .replaceAll("<[^>]+>", "")
                                        .trim();
                                cells.add(cell);
                                temp = temp.substring(end + 5);
                            } else {
                                break;
                            }
                        }
                        if (cells.size() >= 5) {
                            // Парсим ID
                            int id = -1;
                            try {
                                id = Integer.parseInt(cells.get(0).replaceAll("[^0-9]", ""));
                            } catch (NumberFormatException e) {
                                continue; // Пропускаем если не число
                            }

                            // Парсим дату
                            LocalDate visitDate = LocalDate.now();
                            try {
                                java.util.regex.Pattern datePattern =
                                        java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
                                java.util.regex.Matcher dateMatcher = datePattern.matcher(row);

                                if (dateMatcher.find()) {
                                    visitDate = LocalDate.parse(dateMatcher.group());
                                }
                            } catch (Exception e) {
                                // Используем текущую дату
                            }

                            // Определяем тип и статус
                            String contactName = cells.size() > 1 ? cells.get(1) : "Контакт";
                            String type = cells.size() > 3 ? cells.get(3) : "краткосрочное";
                            String status = cells.size() > 4 ? cells.get(4) : "подтверждено";

                            // Нормализуем статус
                            status = normalizeStatus(status);

                            // Создаём объект
                            dates.add(new DateItem(id, -1, contactName, visitDate, type, status));
                        }

                    } catch (Exception e) {
                        // Пропускаем невалидные строки
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка парсинга HTML: " + e.getMessage());
        }

        return dates;
    }


    private String getDateNote(DateItem date) {
        LocalDate today = LocalDate.now();

        if ("состоялось".equals(date.status)) {
            return "✅ Состоялось";
        } else if ("отменено".equals(date.status)) {
            return "❌ Отменено";
        } else if ("подтверждено".equals(date.status)) {
            if (date.visitDate.isBefore(today)) {
                return "⚠️ Просрочено";
            } else if (date.visitDate.isEqual(today)) {
                return "🎉 Сегодня";
            } else if (date.visitDate.isBefore(today.plusDays(3))) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(today, date.visitDate);
                return "⏳ Через " + days + " д.";
            }
            return "📅 Запланировано";
        }
        return "";
    }

    // Внутренний класс для хранения данных о свидании
    private class DateItem {
        int id;
        int contactId;
        String contactName;
        LocalDate visitDate;
        String visitType;
        String status;

        public DateItem(int id, int contactId, String contactName,
                        LocalDate visitDate, String visitType, String status) {
            this.id = id;
            this.contactId = contactId;
            this.contactName = contactName;
            this.visitDate = visitDate;
            this.visitType = visitType;
            this.status = status;
        }
    }

    private void loadPrisonerInfo(JLabel nameLabel, JLabel numberLabel, JLabel birthLabel) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String fullName = "Не загружено";
            private String prisonerNumber = "N/A";
            private String birthDate = "N/A";

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Прямой доступ к DAO (если всё в одном приложении)
                    PrisonerDAO prisonerDAO = new PrisonerDAO();
                    PrisonerEntity prisoner = prisonerDAO.getPrisonerById(prisonerId);

                    if (prisoner != null) {
                        fullName = prisoner.getFullName() != null ? prisoner.getFullName() : "Не указано";
                        prisonerNumber = prisoner.getPrisonerNumber() != null ? prisoner.getPrisonerNumber() : "N/A";
                        birthDate = prisoner.getBirthDate() != null ? prisoner.getBirthDate().toString() : "Не указана";
                    } else {
                        fullName = "Заключённый не найден";
                    }

                } catch (Exception e) {
                    System.err.println("Ошибка DAO: " + e.getMessage());
                    fullName = "Ошибка: " + e.getClass().getSimpleName();
                }
                return null;
            }

            @Override
            protected void done() {
                nameLabel.setText(fullName);
                numberLabel.setText(prisonerNumber);
                birthLabel.setText(birthDate);

                // Обновляем заголовок окна
                if (!fullName.startsWith("Ошибка") && !fullName.equals("Не загружено")) {
                    setTitle("Личный кабинет - " + fullName + " (ID: " + prisonerId + ")");
                }
            }
        };
        worker.execute();
    }

    // Вспомогательный класс для данных заключённого
    class PrisonerData {
        int id = 0;
        String number = "";
        String name = "";
        String birthDate = "";

        @Override
        public String toString() {
            return "PrisonerData{id=" + id + ", name='" + name + "', number='" + number + "'}";
        }
    }

    // Парсинг одного объекта заключённого
    private PrisonerData parsePrisonerObject(String json) {
        PrisonerData prisoner = new PrisonerData();

        try {
            // Убираем фигурные скобки
            String content = json.substring(1, json.length() - 1).trim();
            if (content.isEmpty()) {
                return prisoner;
            }

            // Разделяем поля
            String[] fields = content.split(",");
            for (String field : fields) {
                field = field.trim();
                String[] parts = field.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().replace("\"", "").replace("\'", "");
                    String value = parts[1].trim();

                    // Убираем кавычки если есть
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    } else if (value.startsWith("\'") && value.endsWith("\'")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    System.out.println("Поле: " + key + " = " + value);

                    switch (key) {
                        case "id":
                        case "prisonerId":
                        case "prisoner_id":
                            try {
                                prisoner.id = Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                prisoner.id = 0;
                            }
                            break;
                        case "prisonerNumber":
                        case "number":
                        case "prisoner_number":
                            prisoner.number = value;
                            break;
                        case "fullName":
                        case "name":
                        case "full_name":
                            prisoner.name = value;
                            break;
                        case "birthDate":
                        case "birth_date":
                            prisoner.birthDate = value.equals("null") ? "" : value;
                            break;
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка парсинга объекта: " + e.getMessage());
        }

        return prisoner;
    }

    private void loadLimitsIntoTextArea(JTextArea limitsArea) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String limitsText = "";

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Получаем лимиты через сервис
                    limitsText = prisonerService.getRemainingVisits(prisonerId);

                    // Или через ApiClient если нужно:
                    // limitsText = ApiClient.getVisitLimits(prisonerId);

                    if (limitsText == null || limitsText.isEmpty()) {
                        limitsText = "Лимиты не найдены\nПроверьте соединение с сервером";
                    }

                } catch (Exception e) {
                    limitsText = "Ошибка загрузки лимитов:\n" + e.getMessage();
                    System.err.println("Ошибка загрузки лимитов: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void done() {
                limitsArea.setText(limitsText);
            }
        };
        worker.execute();
    }

    private JPanel createContactsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица контактов
        String[] columns = {"ФИО", "Родство", "Статус"};
        contactsTableModel = new DefaultTableModel(columns, 0);

        JTable contactsTable = new JTable(contactsTableModel);
        JScrollPane scrollPane = new JScrollPane(contactsTable);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Обновить");

        refreshButton.addActionListener(e -> loadContacts());

        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createVisitsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Заголовок
        JLabel titleLabel = new JLabel("МОИ ЗАЯВКИ НА СВИДАНИЯ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Панель с таблицей
        JPanel tablePanel = new JPanel(new BorderLayout());

        // Таблица заявок
        String[] columns = {"ID", "Контакт", "Дата подачи", "Дата свидания", "Тип", "Статус"};
        visitsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Таблица только для чтения
            }
        };

        visitsTable = new JTable(visitsTableModel);

        // Настройка таблицы
        visitsTable.setRowHeight(25);
        visitsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        visitsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Контакт
        visitsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Дата подачи
        visitsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Дата свидания
        visitsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Тип
        visitsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Статус

        JScrollPane scrollPane = new JScrollPane(visitsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Панель статистики
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statsLabel = new JLabel("Статистика: ");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsPanel.add(statsLabel);

        JLabel countLabel = new JLabel("Загрузка...");
        statsPanel.add(countLabel);

        tablePanel.add(statsPanel, BorderLayout.SOUTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        // Панель с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton refreshButton = new JButton("🔄 Обновить");
        JButton detailsButton = new JButton("🔍 Подробнее");

        refreshButton.addActionListener(e -> {
            refreshButton.setText("Обновление...");
            refreshButton.setEnabled(false);

            new Thread(() -> {
                loadVisitRequests();

                // Обновляем статистику
                SwingUtilities.invokeLater(() -> {
                    int rowCount = visitsTableModel.getRowCount();
                    int actualRequests = 0;

                    // Считаем только реальные заявки (не сообщения об ошибках)
                    for (int i = 0; i < rowCount; i++) {
                        Object value = visitsTableModel.getValueAt(i, 0);
                        if (value instanceof Integer ||
                                (value instanceof String && ((String)value).matches("\\d+"))) {
                            actualRequests++;
                        }
                    }

                    countLabel.setText("Всего: " + actualRequests + " заявок");
                    refreshButton.setText("🔄 Обновить");
                    refreshButton.setEnabled(true);
                });
            }).start();
        });

        detailsButton.addActionListener(e -> {
            int selectedRow = visitsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel,
                        "Выберите заявку для просмотра деталей",
                        "Внимание",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object idObj = visitsTableModel.getValueAt(selectedRow, 0);
            Object contactObj = visitsTableModel.getValueAt(selectedRow, 1);
            Object dateObj = visitsTableModel.getValueAt(selectedRow, 3);
            Object typeObj = visitsTableModel.getValueAt(selectedRow, 4);
            Object statusObj = visitsTableModel.getValueAt(selectedRow, 5);

            // Убираем HTML теги из статуса
            String status = statusObj.toString().replaceAll("<[^>]+>", "");

            JOptionPane.showMessageDialog(panel,
                    "Детали заявки:\n\n" +
                            "ID: " + idObj + "\n" +
                            "Контакт: " + contactObj + "\n" +
                            "Дата свидания: " + dateObj + "\n" +
                            "Тип: " + typeObj + "\n" +
                            "Статус: " + status,
                    "Детали заявки",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(detailsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Загружаем данные при создании
        SwingUtilities.invokeLater(() -> {
            refreshButton.doClick(); // Автоматически обновляем при открытии вкладки
        });

        return panel;
    }

    private JPanel createRequestPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Заголовок
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("ПОДАТЬ ЗАЯВКУ НА СВИДАНИЕ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, gbc);

        // Разделитель
        gbc.gridy = 1;
        panel.add(new JSeparator(), gbc);

        // Выбор контакта
        gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Выберите контакт:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        JComboBox<ContactItem> contactCombo = new JComboBox<>();
        contactCombo.setRenderer(new ContactListRenderer());
        panel.add(contactCombo, gbc);

        // Тип свидания
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(new JLabel("Тип свидания:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{
                "краткосрочное",
                "длительное"
        });
        panel.add(typeCombo, gbc);

        // Дата свидания
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Дата свидания:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        JTextField dateField = new JTextField();

        // Устанавливаем дату через 7 дней от сегодня
        LocalDate nextWeek = LocalDate.now().plusDays(7);
        dateField.setText(nextWeek.toString());

        // Добавляем кнопку выбора даты
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.add(dateField, BorderLayout.CENTER);

        JButton datePickerButton = new JButton("📅");
        datePickerButton.addActionListener(e -> showDatePicker(dateField));
        datePanel.add(datePickerButton, BorderLayout.EAST);

        panel.add(datePanel, gbc);

        // Информация о лимитах
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 3;
        JTextArea limitsInfoArea = new JTextArea(3, 40);
        limitsInfoArea.setEditable(false);
        limitsInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        limitsInfoArea.setBackground(new Color(240, 240, 240));
        limitsInfoArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        limitsInfoArea.setText("Загрузка информации о лимитах...");
        panel.add(new JScrollPane(limitsInfoArea), gbc);

        // Кнопки
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        JButton checkButton = new JButton("Проверить возможность");
        panel.add(checkButton, gbc);

        gbc.gridx = 1;
        JButton refreshButton = new JButton("Обновить контакты");
        panel.add(refreshButton, gbc);

        gbc.gridx = 2;
        JButton submitButton = new JButton("Подать заявку");
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(submitButton, gbc);

        // Статусная строка
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.BLUE);
        panel.add(statusLabel, gbc);

        // ============ ОБРАБОТЧИКИ СОБЫТИЙ ============

        // Загрузка контактов при создании
        SwingUtilities.invokeLater(() -> loadContactsIntoCombo(contactCombo, statusLabel));

        // Кнопка обновления контактов
        refreshButton.addActionListener(e -> {
            statusLabel.setText("Загрузка контактов...");
            loadContactsIntoCombo(contactCombo, statusLabel);
        });

        // Кнопка проверки возможности
        checkButton.addActionListener(e -> {
            String visitType = (String) typeCombo.getSelectedItem();
            boolean can = prisonerService.canRequestVisit(prisonerId, visitType);

            if (can) {
                JOptionPane.showMessageDialog(panel,
                        "✓ Вы можете подать заявку на " + visitType + " свидание",
                        "Проверка возможности",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(panel,
                        "✗ Лимит " + visitType + " свиданий исчерпан",
                        "Проверка возможности",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Кнопка подачи заявки
        submitButton.addActionListener(e -> {
            ContactItem selectedContact = (ContactItem) contactCombo.getSelectedItem();
            String visitType = (String) typeCombo.getSelectedItem();
            String dateStr = dateField.getText().trim();

            // Валидация
            if (selectedContact == null || selectedContact.id <= 0) {
                JOptionPane.showMessageDialog(panel,
                        "Выберите контакт из списка",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Введите дату свидания",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                LocalDate visitDate = LocalDate.parse(dateStr);

                // Проверка возможности
                if (!prisonerService.canRequestVisit(prisonerId, visitType)) {
                    JOptionPane.showMessageDialog(panel,
                            "Лимит " + visitType + " свиданий исчерпан",
                            "Невозможно подать заявку",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Подача заявки
                submitVisitRequest(selectedContact.id, visitDate, visitType, statusLabel);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Неверный формат даты. Используйте ГГГГ-ММ-ДД\n" + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Загрузка информации о лимитах
        loadLimitsInfo(limitsInfoArea);

        // Обновление лимитов при смене типа
        typeCombo.addActionListener(e -> loadLimitsInfo(limitsInfoArea));

        return panel;
    }

    private void showDatePicker(JTextField dateField) {
        // Простой диалог выбора даты без внешних библиотек
        JDialog dialog = new JDialog(this, "Выберите дату", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Текущая дата
        LocalDate currentDate = LocalDate.now();
        if (!dateField.getText().isEmpty()) {
            try {
                currentDate = LocalDate.parse(dateField.getText());
            } catch (Exception e) {
                // Оставляем текущую дату
            }
        }

        // Панель с полями для ввода даты
        JPanel datePanel = new JPanel(new FlowLayout());

        JSpinner yearSpinner = new JSpinner(
                new SpinnerNumberModel(currentDate.getYear(), 2023, 2030, 1));
        JSpinner monthSpinner = new JSpinner(
                new SpinnerNumberModel(currentDate.getMonthValue(), 1, 12, 1));
        JSpinner daySpinner = new JSpinner(
                new SpinnerNumberModel(currentDate.getDayOfMonth(), 1, 31, 1));

        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "####"));
        monthSpinner.setEditor(new JSpinner.NumberEditor(monthSpinner, "#"));
        daySpinner.setEditor(new JSpinner.NumberEditor(daySpinner, "#"));

        datePanel.add(new JLabel("Год:"));
        datePanel.add(yearSpinner);
        datePanel.add(new JLabel("Месяц:"));
        datePanel.add(monthSpinner);
        datePanel.add(new JLabel("День:"));
        datePanel.add(daySpinner);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");

        okButton.addActionListener(e -> {
            try {
                int year = (Integer) yearSpinner.getValue();
                int month = (Integer) monthSpinner.getValue();
                int day = (Integer) daySpinner.getValue();

                LocalDate selectedDate = LocalDate.of(year, month, day);
                dateField.setText(selectedDate.toString());
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Неверная дата: " + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        panel.add(new JLabel("Выберите дату свидания:"), BorderLayout.NORTH);
        panel.add(datePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Класс для отображения контактов в ComboBox
    private class ContactItem {
        int id;
        String name;
        String relation;
        boolean approved;

        public ContactItem(int id, String name, String relation, boolean approved) {
            this.id = id;
            this.name = name;
            this.relation = relation;
            this.approved = approved;
        }

        @Override
        public String toString() {
            String status = approved ? "✓" : "⏳";
            return String.format("%s %s (%s)", status, name, relation);
        }
    }

    // Кастомный рендерер для ComboBox
    private class ContactListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ContactItem) {
                ContactItem contact = (ContactItem) value;
                setText(contact.toString());

                // Раскрашиваем по статусу
                if (!contact.approved) {
                    setForeground(Color.GRAY);
                    setToolTipText("Контакт ожидает одобрения администратора");
                } else {
                    setForeground(Color.BLACK);
                    setToolTipText("Контакт одобрен для свиданий");
                }
            }

            return c;
        }
    }

    // Загрузка контактов в ComboBox
    private void loadContactsIntoCombo(JComboBox<ContactItem> combo, JLabel statusLabel) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<ContactItem> contacts = new ArrayList<>();
            private String error = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    System.out.println("=== ЗАГРУЗКА КОНТАКТОВ ДЛЯ ФОРМЫ ===");
                    System.out.println("prisonerId: " + prisonerId);

                    // ВАЖНО: Используем тот же сервис, что и "Мои контакты"!
                    List<Contact> serviceContacts = prisonerService.getPrisonerContacts(prisonerId);

                    System.out.println("Получено контактов из сервиса: " + serviceContacts.size());

                    // Конвертируем List<Contact> в List<ContactItem>
                    for (Contact contact : serviceContacts) {
                        // ДЕБАГ вывод каждого контакта
                        System.out.println("Контакт: ID=" + contact.getContactId() +
                                ", Имя='" + contact.getFullName() + "'" +
                                ", Родство='" + contact.getRelation() + "'" +
                                ", Одобрен=" + contact.isApproved());

                        // Создаем ContactItem с правильными данными
                        ContactItem item = new ContactItem(
                                contact.getContactId(),      // ← используем getContactId()
                                contact.getFullName(),       // ← используем getFullName()
                                contact.getRelation(),       // ← используем getRelation()
                                contact.isApproved()         // ← используем isApproved()
                        );
                        contacts.add(item);
                    }

                    System.out.println("Создано ContactItem: " + contacts.size());

                } catch (Exception e) {
                    error = e.getMessage();
                    System.err.println("Ошибка загрузки контактов: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                combo.removeAllItems();

                if (error != null) {
                    combo.addItem(new ContactItem(-1, "Ошибка загрузки", error, false));
                    statusLabel.setText("Ошибка загрузки контактов");
                    statusLabel.setForeground(Color.RED);
                    return;
                }

                if (contacts.isEmpty()) {
                    combo.addItem(new ContactItem(-1, "Нет доступных контактов",
                            "Добавьте контакты через AdminPanel", false));
                    statusLabel.setText("Добавьте контакты через AdminPanel");
                    statusLabel.setForeground(Color.ORANGE);
                } else {
                    for (ContactItem contact : contacts) {
                        combo.addItem(contact);
                        System.out.println("Добавлен в ComboBox: " + contact.toString());
                    }
                    statusLabel.setText("Загружено контактов: " + contacts.size());
                    statusLabel.setForeground(new Color(0, 100, 0)); // темно-зеленый
                }
            }
        };
        worker.execute();
    }

    // Метод загрузки информации о лимитах (добавьте в класс PrisonerPanel)
    private void loadLimitsInfo(JTextArea limitsInfoArea) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String limitsText = "";

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Получаем лимиты через существующий сервис
                    limitsText = prisonerService.getRemainingVisits(prisonerId);

                    if (limitsText == null || limitsText.isEmpty()) {
                        limitsText = "Лимиты не найдены\nПроверьте соединение с сервером";
                    }

                } catch (Exception e) {
                    limitsText = "Ошибка загрузки лимитов:\n" + e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                limitsInfoArea.setText(limitsText);
            }
        };
        worker.execute();
    }

    private int extractNumberFromJSON(String json, String key) {
        try {
            String pattern = "\"" + key + "\":\\s*(\\d+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);

            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception e) {
            // Игнорируем
        }
        return -1;
    }

    // Метод extractTableCells должен уже быть (из предыдущего кода с заявками)
    private List<String> extractTableCells(String row) {
        List<String> cells = new ArrayList<>();
        try {
            String temp = row;
            while (temp.contains("<td>")) {
                int start = temp.indexOf("<td>") + 4;
                int end = temp.indexOf("</td>", start);

                if (end != -1) {
                    String cell = temp.substring(start, end)
                            .replaceAll("<[^>]+>", "")
                            .trim();
                    cells.add(cell);
                    temp = temp.substring(end + 5);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            // Возвращаем пустой список
        }
        return cells;
    }

    private int extractNumber(String text) {
        try {
            String digits = text.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                return Integer.parseInt(digits);
            }
        } catch (Exception e) {
            // Игнорируем
        }
        return -1;
    }

    // Метод для подачи заявки
    private void submitVisitRequest(int contactId, LocalDate visitDate,
                                    String visitType, JLabel statusLabel) {

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private String result = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Используем ApiClient для создания заявки
                    String response = ApiClient.createVisitRequest(
                            prisonerId, contactId, visitDate.toString(), visitType);

                    System.out.println("Ответ на создание заявки: " + response);

                    if (response.contains("Успех") || response.contains("success") ||
                            response.contains("создан") || response.toLowerCase().contains("created")) {
                        result = "Заявка успешно создана!";
                        return true;
                    } else {
                        result = "Ошибка: " + response;
                        return false;
                    }

                } catch (Exception e) {
                    result = "Ошибка соединения: " + e.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();

                    if (success) {
                        statusLabel.setText(result);
                        statusLabel.setForeground(Color.GREEN);

                        JOptionPane.showMessageDialog(PrisonerPanel.this,
                                "✓ Заявка успешно подана!\n\n" +
                                        "Контакт ID: " + contactId + "\n" +
                                        "Дата: " + visitDate + "\n" +
                                        "Тип: " + visitType,
                                "Успех",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        statusLabel.setText(result);
                        statusLabel.setForeground(Color.RED);

                        JOptionPane.showMessageDialog(PrisonerPanel.this,
                                result,
                                "Ошибка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Ошибка: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                }
            }
        };

        worker.execute();
    }

    private void loadData() {
        loadContacts();
        loadVisitRequests();
    }

    private void loadVisitRequests() {
        visitsTableModel.setRowCount(0);

        // ТОЛЬКО сообщение о загрузке
        visitsTableModel.addRow(new Object[]{
                "Загрузка...", "Идёт получение данных с сервера", "", "", "", ""
        });

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    System.out.println("=== ЗАГРУЗКА РЕАЛЬНЫХ ЗАЯВОК ===");
                    System.out.println("prisonerId: " + prisonerId);

                    // 1. Пробуем API
                    String response = ApiClient.getPrisonerVisitRequests(prisonerId);
                    System.out.println("Ответ от API: " + response.length() + " символов");

                    // 2. Парсим реальные данные
                    List<VisitRequestItem> requests = parseRealRequestsFromHTML(response);

                    // 3. Обновляем GUI в основном потоке
                    SwingUtilities.invokeLater(() -> {
                        visitsTableModel.setRowCount(0);

                        if (requests.isEmpty()) {
                            visitsTableModel.addRow(new Object[]{
                                    "Нет заявок",
                                    "У вас пока нет заявок на свидания",
                                    "Подайте заявку через форму",
                                    "", "", ""
                            });
                            return;
                        }

                        for (VisitRequestItem request : requests) {
                            // ПОЛУЧАЕМ ИМЯ КОНТАКТА вместо ID
                            String contactName = getContactNameById(request.contactId);

                            visitsTableModel.addRow(new Object[]{
                                    request.id,
                                    contactName,  // ← Теперь имя контакта
                                    request.requestDate != null ? request.requestDate.toString() : "",
                                    request.visitDate != null ? request.visitDate.toString() : "",
                                    request.visitType,
                                    getStatusWithColor(request.status)
                            });
                        }

                        System.out.println("Загружено реальных заявок: " + requests.size());
                    });

                } catch (Exception e) {
                    System.err.println("ОШИБКА загрузки заявок: " + e.getMessage());
                    e.printStackTrace();

                    // ВСЕГО ЛИШЬ сообщение об ошибке, НИКАКИХ тестовых данных
                    SwingUtilities.invokeLater(() -> {
                        visitsTableModel.setRowCount(0);
                        visitsTableModel.addRow(new Object[]{
                                "Ошибка загрузки",
                                "Не удалось получить данные с сервера",
                                e.getMessage(),
                                "Проверьте подключение",
                                "", ""
                        });
                    });
                }
                return null;
            }
        };

        worker.execute();
    }

    private String getContactNameById(int contactId) {
        if (contactId <= 0) {
            return "Не указан";
        }
        return String.valueOf(contactId);
    }
    private Map<Integer, String> cachedContacts;

    private List<VisitRequestItem> parseRealRequestsFromHTML(String html) {
        List<VisitRequestItem> requests = new ArrayList<>();

        try {
            System.out.println("Парсинг HTML ответа...");

            // Если ответ пустой или содержит сообщение "нет заявок"
            if (html == null || html.isEmpty() ||
                    html.contains("нет заявок") || html.contains("Нет запросов")) {
                System.out.println("Сервер сообщает: заявок нет");
                return requests; // Пустой список
            }

            // Ищем таблицу с заявками
            if (!html.contains("<table") && !html.contains("<tr>")) {
                System.out.println("Ответ не содержит таблицу");
                return requests;
            }

            // Простой парсинг таблицы
            String[] rows = html.split("<tr>");

            for (String row : rows) {
                // Пропускаем заголовки
                if (row.contains("<th>") || row.contains("ID заявки")) {
                    continue;
                }

                // Ищем строки с данными
                if (row.contains("<td>")) {
                    // Извлекаем ячейки
                    List<String> cells = extractTableCells(row);

                    if (cells.size() >= 6) {
                        try {
                            VisitRequestItem item = new VisitRequestItem();

                            // ID заявки
                            item.id = extractNumber(cells.get(0));

                            // ID контакта (третья ячейка или извлекаем)
                            item.contactId = extractNumber(cells.get(1));

                            // Даты
                            item.requestDate = extractDateFromCells(cells, 2);
                            item.visitDate = extractDateFromCells(cells, 3);

                            // Тип и статус
                            item.visitType = cells.size() > 5 ? normalizeType(cells.get(5)) : "краткосрочное";
                            item.status = cells.size() > 6 ? normalizeStatus(cells.get(6)) : "ожидает";

                            if (item.id > 0) {
                                requests.add(item);
                                System.out.println("Найдена заявка #" + item.id);
                            }

                        } catch (Exception e) {
                            // Пропускаем невалидные строки
                        }
                    }
                }
            }

            System.out.println("Парсинг завершён, найдено: " + requests.size() + " заявок");

        } catch (Exception e) {
            System.err.println("Ошибка парсинга: " + e.getMessage());
        }

        return requests;
    }

    // Извлечение даты из ячеек
    private LocalDate extractDateFromCells(List<String> cells, int index) {
        if (index >= cells.size()) {
            return LocalDate.now();
        }

        try {
            // Ищем дату в формате ГГГГ-ММ-ДД
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
            java.util.regex.Matcher matcher = pattern.matcher(cells.get(index));

            if (matcher.find()) {
                return LocalDate.parse(matcher.group());
            }
        } catch (Exception e) {
            // Игнорируем
        }

        return LocalDate.now();
    }

    // Нормализация типа
    private String normalizeType(String type) {
        if (type == null) return "краткосрочное";

        type = type.toLowerCase();
        if (type.contains("длитель") || type.contains("long")) {
            return "длительное";
        }
        return "краткосрочное";
    }

    // Нормализация статуса
    private String normalizeStatus(String status) {
        if (status == null) return "ожидает";

        status = status.toLowerCase();
        if (status.contains("одобр")) return "одобрена";
        if (status.contains("отклон")) return "отклонена";
        if (status.contains("отмен")) return "отменена";
        if (status.contains("ожида")) return "ожидает";

        return "ожидает";
    }

    private class VisitRequestItem {
        int id;
        int prisonerId;
        int contactId;
        LocalDate requestDate;
        LocalDate visitDate;
        String visitType;
        String status;

        // Явно добавляем конструктор по умолчанию
        public VisitRequestItem() {
            // Может быть пустым
        }

        // Также можно добавить конструктор с параметрами для удобства
        public VisitRequestItem(int id, int prisonerId, int contactId,
                                LocalDate requestDate, LocalDate visitDate,
                                String visitType, String status) {
            this.id = id;
            this.prisonerId = prisonerId;
            this.contactId = contactId;
            this.requestDate = requestDate;
            this.visitDate = visitDate;
            this.visitType = visitType;
            this.status = status;
        }
    }

    // Получение статуса с цветом для таблицы
    private String getStatusWithColor(String status) {
        String color;

        switch (status.toLowerCase()) {
            case "одобрена":
            case "approved":
                color = "green";
                break;
            case "отклонена":
            case "rejected":
                color = "red";
                break;
            case "отменена":
            case "cancelled":
                color = "gray";
                break;
            case "ожидает":
            case "pending":
                color = "orange";
                break;
            default:
                color = "black";
        }

        return "<html><font color='" + color + "'><b>" + status + "</b></font></html>";
    }

    private void loadContacts() {
        // Очищаем таблицу
        contactsTableModel.setRowCount(0);

        // Получаем контакты через сервис
        List<Contact> contacts = prisonerService.getPrisonerContacts(prisonerId);

        // Добавляем контакты в таблицу
        for (Contact contact : contacts) {
            String status = contact.isApproved() ? "Одобрен" : "На рассмотрении";
            contactsTableModel.addRow(new Object[]{
                    contact.getFullName(),
                    contact.getRelation(),
                    status
            });
        }
    }
}
