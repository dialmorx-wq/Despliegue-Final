package com.example.sga_rpf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int COLOR_BACKGROUND = Color.rgb(244, 246, 249);
    private static final int COLOR_DARK = Color.rgb(33, 37, 41);
    private static final int COLOR_PRIMARY = Color.rgb(13, 110, 253);
    private static final int COLOR_SUCCESS = Color.rgb(25, 135, 84);
    private static final int COLOR_WARNING = Color.rgb(181, 129, 5);
    private static final int COLOR_DANGER = Color.rgb(220, 53, 69);
    private static final int COLOR_MUTED = Color.rgb(108, 117, 125);
    private static final int COLOR_BORDER = Color.rgb(225, 229, 234);

    private LocalStore store;
    private ApiClient apiClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private User currentUser;
    private String activeScreen = "dashboard";
    private final Locale colombia = new Locale("es", "CO");
    private List<User> apiUsers;
    private List<Order> apiOrders;
    private List<Provider> apiProviders;
    private CplDashboard cplDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = new LocalStore(this);
        apiClient = new ApiClient("https://sgarpf-production.up.railway.app/api");
        currentUser = store.getLoggedUser();

        if (currentUser == null) {
            showLogin();
        } else {
            showDashboard();
        }
    }

    private void showLogin() {
        activeScreen = "login";

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setGravity(Gravity.CENTER);
        page.setPadding(dp(24), dp(24), dp(24), dp(24));
        page.setBackgroundColor(COLOR_BACKGROUND);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(24), dp(26), dp(24), dp(26));
        panel.setBackground(cardBackground(Color.WHITE, 12, COLOR_BORDER));
        page.addView(panel, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView title = title("SGA_RPF", 32, COLOR_DARK);
        title.setGravity(Gravity.CENTER);
        panel.addView(title);

        TextView subtitle = label("Sistema de Gestion de Usuarios", 15, COLOR_MUTED);
        subtitle.setGravity(Gravity.CENTER);
        panel.addView(subtitle);

        addSpace(panel, 22);

        EditText email = input("Correo");
        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        panel.addView(email);

        EditText password = input("Contrasena");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setImeOptions(EditorInfo.IME_ACTION_DONE);
        panel.addView(password);

        Button login = primaryButton("Ingresar");
        panel.addView(login);

        TextView hint = label("Usuario inicial: admin@sga.com / 1234", 13, COLOR_MUTED);
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(12), 0, 0);
        panel.addView(hint);

        login.setOnClickListener(v -> {
            String cleanEmail = email.getText().toString().trim();
            String cleanPassword = password.getText().toString();

            if (cleanEmail.isEmpty() || cleanPassword.isEmpty()) {
                toast("Ingresa correo y contrasena.");
                return;
            }

            login.setEnabled(false);
            login.setText("Ingresando...");

            executor.execute(() -> {
                try {
                    User user = apiClient.login(cleanEmail, cleanPassword);
                    runOnUiThread(() -> {
                        currentUser = user;
                        store.saveLoggedUser(user);
                        toast("Sesion iniciada con Spring Boot.");
                        showDashboard(true);
                    });
                } catch (Exception apiError) {
                    User localUser = store.login(cleanEmail, cleanPassword);
                    runOnUiThread(() -> {
                        login.setEnabled(true);
                        login.setText("Ingresar");

                        if (localUser == null) {
                            toast("No se pudo iniciar sesion. Verifica Spring Boot o las credenciales.");
                            return;
                        }

                        currentUser = localUser;
                        store.saveLoggedUser(localUser);
                        toast("Sesion local. Spring Boot no respondio.");
                        showDashboard(false);
                    });
                }
            });
        });

        setContentView(page);
    }

    private void showDashboard() {
        showDashboard(true);
    }

    private void showDashboard(boolean syncWithApi) {
        activeScreen = "dashboard";
        setContentView(shell("Gestion de Usuarios", this::buildDashboardContent));
        if (syncWithApi) {
            loadApiUsers();
        }
    }

    private void showOrders() {
        activeScreen = "orders";
        setContentView(shell("Repositorio de Ordenes de Compra", this::buildOrdersContent));
        loadApiOrders();
    }

    private void showProviders() {
        activeScreen = "providers";
        setContentView(shell("Hojas de Vida de Proveedores", this::buildProvidersContent));
        loadApiProviders();
    }

    private void showCpl() {
        activeScreen = "cpl";
        setContentView(shell("Compras Publicas Locales", this::buildCplContent));
        loadCplDashboard();
    }

    private View shell(String screenTitle, ContentBuilder builder) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(COLOR_BACKGROUND);

        root.addView(toolbar(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setPadding(dp(12), dp(10), dp(12), dp(10));
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(Color.WHITE);
        addNavButton(nav, "Usuarios", "dashboard", this::showDashboard);
        addNavButton(nav, "Ordenes", "orders", this::showOrders);
        addNavButton(nav, "Proveedores", "providers", this::showProviders);
        addNavButton(nav, "CPL", "cpl", this::showCpl);
        root.addView(nav);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(18), dp(16), dp(28));
        scrollView.addView(content);

        TextView heading = title(screenTitle, 24, COLOR_DARK);
        heading.setPadding(0, 0, 0, dp(12));
        content.addView(heading);

        builder.build(content);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        return root;
    }

    private View toolbar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(16), dp(14), dp(16), dp(14));
        bar.setBackgroundColor(COLOR_DARK);

        TextView brand = title("SGA_RPF", 20, Color.WHITE);
        bar.addView(brand, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

        TextView user = label(currentUser.name + "  " + currentUser.role, 13, Color.WHITE);
        user.setGravity(Gravity.END);
        bar.addView(user);

        Button logout = outlineButton("Salir", Color.WHITE);
        logout.setPadding(dp(10), 0, dp(10), 0);
        logout.setOnClickListener(v -> {
            store.clearLoggedUser();
            currentUser = null;
            showLogin();
        });
        bar.addView(logout);

        return bar;
    }

    private void buildDashboardContent(LinearLayout content) {
        List<User> users = getUsersForUi();
        LinearLayout kpis = new LinearLayout(this);
        kpis.setOrientation(LinearLayout.VERTICAL);
        content.addView(kpis);
        kpis.addView(metricCard("Total Usuarios", String.valueOf(users.size()), COLOR_PRIMARY));
        kpis.addView(metricCard("Reportes", "35", COLOR_SUCCESS));
        kpis.addView(metricCard("Instituciones", "18", COLOR_DARK));

        if (isAdmin()) {
            addSpace(content, 8);
            content.addView(userForm(null));
        }

        addSpace(content, 12);
        TextView listTitle = title("Lista de Usuarios", 20, COLOR_DARK);
        content.addView(listTitle);

        EditText search = input("Buscar por nombre, correo o rol");
        content.addView(search);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        content.addView(list);

        Runnable render = () -> renderUsers(list, search.getText().toString());
        search.addTextChangedListener(simpleWatcher(render));
        render.run();
    }

    private List<User> getUsersForUi() {
        return apiUsers != null ? apiUsers : store.getUsers();
    }

    private boolean usingApiUsers() {
        return apiUsers != null;
    }

    private void loadApiUsers() {
        executor.execute(() -> {
            try {
                List<User> users = apiClient.getUsers();
                runOnUiThread(() -> {
                    apiUsers = users;
                    if ("dashboard".equals(activeScreen)) {
                        setContentView(shell("Gestion de Usuarios", this::buildDashboardContent));
                    }
                });
            } catch (Exception ignored) {
                runOnUiThread(() -> {
                    if (apiUsers == null && "dashboard".equals(activeScreen)) {
                        toast("Spring Boot no respondio. Mostrando datos locales.");
                    }
                });
            }
        });
    }

    private View userForm(User editing) {
        LinearLayout form = panel();
        TextView header = title(editing == null ? "Registrar Usuario" : "Editar Usuario", 18, COLOR_DARK);
        form.addView(header);

        EditText name = input("Nombre");
        EditText email = input("Correo");
        EditText password = input("Contrasena");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Spinner role = spinner(new String[]{"USUARIO", "ADMIN"});

        if (editing != null) {
            name.setText(editing.name);
            email.setText(editing.email);
            password.setText(editing.password);
            role.setSelection("ADMIN".equals(editing.role) ? 1 : 0);
        }

        form.addView(name);
        form.addView(email);
        form.addView(password);
        form.addView(role);

        Button save = primaryButton(editing == null ? "Guardar" : "Actualizar");
        form.addView(save);

        save.setOnClickListener(v -> {
            String cleanName = name.getText().toString().trim();
            String cleanEmail = email.getText().toString().trim();
            String cleanPassword = password.getText().toString();
            String selectedRole = role.getSelectedItem().toString();

            if (cleanName.length() < 3 || cleanEmail.isEmpty() || cleanPassword.length() < 4) {
                toast("Revisa nombre, correo y contrasena.");
                return;
            }

            if (emailExists(cleanEmail, editing == null ? -1 : editing.id)) {
                toast("Ya existe un usuario registrado con ese correo.");
                return;
            }

            User userToSave = new User(
                    editing == null ? store.nextUserId() : editing.id,
                    cleanName,
                    cleanEmail,
                    cleanPassword,
                    selectedRole
            );

            if (usingApiUsers()) {
                saveUserWithApi(userToSave, editing == null);
                return;
            }

            if (editing == null) {
                store.saveUser(userToSave);
                toast("Usuario registrado correctamente.");
            } else {
                store.saveUser(userToSave);
                toast("Usuario actualizado correctamente.");
            }
            showDashboard(false);
        });

        if (editing != null) {
            Button cancel = outlineButton("Cancelar", COLOR_MUTED);
            cancel.setOnClickListener(v -> showDashboard());
            form.addView(cancel);
        }

        return form;
    }

    private void renderUsers(LinearLayout list, String query) {
        list.removeAllViews();
        String text = query.trim().toLowerCase(colombia);
        int count = 0;

        for (User user : getUsersForUi()) {
            if (!text.isEmpty()
                    && !user.name.toLowerCase(colombia).contains(text)
                    && !user.email.toLowerCase(colombia).contains(text)
                    && !user.role.toLowerCase(colombia).contains(text)) {
                continue;
            }

            count++;
            LinearLayout row = panel();
            row.addView(title(user.name, 17, COLOR_DARK));
            row.addView(label(user.email, 14, COLOR_MUTED));
            row.addView(label("Rol: " + user.role, 14, "ADMIN".equals(user.role) ? COLOR_PRIMARY : COLOR_MUTED));

            if (isAdmin()) {
                LinearLayout actions = new LinearLayout(this);
                actions.setOrientation(LinearLayout.HORIZONTAL);
                Button edit = outlineButton("Editar", COLOR_WARNING);
                Button delete = outlineButton("Eliminar", COLOR_DANGER);
                actions.addView(edit, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                actions.addView(delete, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(actions);

                edit.setOnClickListener(v -> {
                    LinearLayout content = new LinearLayout(this);
                    content.setOrientation(LinearLayout.VERTICAL);
                    content.setPadding(dp(12), dp(8), dp(12), dp(8));
                    content.addView(userForm(user));
                    setContentView(shell("Gestion de Usuarios", parent -> parent.addView(content)));
                });

                delete.setOnClickListener(v -> confirm("Eliminar usuario", "Seguro que deseas eliminar este usuario?", () -> {
                    if (currentUser.id == user.id) {
                        toast("No puedes eliminar la sesion activa.");
                        return;
                    }
                    if (usingApiUsers()) {
                        deleteUserWithApi(user.id);
                    } else {
                        store.deleteUser(user.id);
                        toast("Usuario eliminado correctamente.");
                        showDashboard(false);
                    }
                }));
            }

            list.addView(row);
        }

        if (count == 0) {
            list.addView(empty("No hay usuarios para mostrar."));
        }
    }

    private boolean emailExists(String email, int ignoringId) {
        for (User user : getUsersForUi()) {
            if (user.id != ignoringId && user.email.equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private void saveUserWithApi(User user, boolean creating) {
        executor.execute(() -> {
            try {
                if (creating) {
                    apiClient.createUser(user);
                } else {
                    apiClient.updateUser(user);
                }
                apiUsers = apiClient.getUsers();
                runOnUiThread(() -> {
                    toast(creating ? "Usuario registrado en MySQL." : "Usuario actualizado en MySQL.");
                    showDashboard(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> toast("No se pudo guardar en Spring Boot."));
            }
        });
    }

    private void deleteUserWithApi(int id) {
        executor.execute(() -> {
            try {
                apiClient.deleteUser(id);
                apiUsers = apiClient.getUsers();
                runOnUiThread(() -> {
                    toast("Usuario eliminado en MySQL.");
                    showDashboard(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> toast("No se pudo eliminar en Spring Boot."));
            }
        });
    }

    private void buildOrdersContent(LinearLayout content) {
        TextView intro = label("Gestor documental institucional para las ordenes de compra del Programa de Alimentacion Escolar.", 14, COLOR_MUTED);
        content.addView(intro);
        addSpace(content, 10);

        EditText search = input("Buscar por codigo, tipo, proveedor o estado");
        content.addView(search);

        TextView source = label(apiOrders == null ? "Fuente: datos locales de respaldo" : "Fuente: MySQL via Spring Boot", 13,
                apiOrders == null ? COLOR_WARNING : COLOR_SUCCESS);
        content.addView(source);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        content.addView(list);

        Runnable render = () -> renderOrders(list, search.getText().toString());
        search.addTextChangedListener(simpleWatcher(render));
        render.run();
    }

    private void loadApiOrders() {
        executor.execute(() -> {
            try {
                List<Order> orders = apiClient.getPurchaseOrders();
                runOnUiThread(() -> {
                    apiOrders = orders;
                    if ("orders".equals(activeScreen)) {
                        setContentView(shell("Repositorio de Ordenes de Compra", this::buildOrdersContent));
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if ("orders".equals(activeScreen)) {
                        toast("No se pudieron cargar ordenes desde MySQL. Usando respaldo local.");
                    }
                });
            }
        });
    }

    private void renderOrders(LinearLayout list, String query) {
        list.removeAllViews();
        List<Order> filtered = filterOrders(query);
        list.addView(sectionLabel("Proveedores de Alimentos"));
        int food = 0;
        for (Order order : filtered) {
            if ("Proveedor de Alimentos".equals(order.type)) {
                list.addView(orderCard(order));
                food++;
            }
        }
        if (food == 0) {
            list.addView(empty("No se encontraron ordenes de proveedores de alimentos."));
        }

        list.addView(sectionLabel("Proveedores Logisticos"));
        int logistics = 0;
        for (Order order : filtered) {
            if ("Proveedor Logistico".equals(order.type)) {
                list.addView(orderCard(order));
                logistics++;
            }
        }
        if (logistics == 0) {
            list.addView(empty("No se encontraron ordenes de proveedores logisticos."));
        }
    }

    private List<Order> filterOrders(String query) {
        String text = query.trim().toLowerCase(colombia);
        List<Order> result = new ArrayList<>();
        List<Order> source = apiOrders != null ? apiOrders : store.getOrders();
        for (Order order : source) {
            if (text.isEmpty()
                    || order.code.toLowerCase(colombia).contains(text)
                    || order.type.toLowerCase(colombia).contains(text)
                    || order.status.toLowerCase(colombia).contains(text)
                    || order.provider.toLowerCase(colombia).contains(text)) {
                result.add(order);
            }
        }
        return result;
    }

    private View orderCard(Order order) {
        LinearLayout card = panel();
        card.addView(title(order.code, 16, COLOR_DARK));
        card.addView(label("Tipo: " + order.type, 14, COLOR_MUTED));
        card.addView(label("Estado: " + order.status, 14, statusColor(order.status)));
        card.addView(label("Proveedor: " + order.provider, 14, COLOR_MUTED));
        card.addView(label("Inicio: " + order.startDate + "  Finalizacion: " + order.endDate, 13, COLOR_MUTED));
        Button edit = outlineButton(apiOrders == null ? "Ver / Editar Detalle" : "Ver Detalle", COLOR_PRIMARY);
        edit.setOnClickListener(v -> showOrderEditor(order));
        card.addView(edit);
        return card;
    }

    private void showOrderEditor(Order order) {
        if (apiOrders != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Detalle de Orden")
                    .setMessage("Codigo: " + order.code
                            + "\nTipo: " + order.type
                            + "\nEstado: " + order.status
                            + "\nProveedor: " + order.provider
                            + "\nInicio: " + order.startDate
                            + "\nFinalizacion: " + order.endDate)
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(10), dp(4), dp(10), 0);

        TextView code = title(order.code, 16, COLOR_DARK);
        form.addView(code);

        Spinner type = spinner(new String[]{"Proveedor de Alimentos", "Proveedor Logistico"});
        type.setSelection("Proveedor Logistico".equals(order.type) ? 1 : 0);
        Spinner status = spinner(new String[]{"Activo", "En Revision", "Finalizado", "Suspendido"});
        status.setSelection(statusIndex(order.status));
        EditText provider = input("Proveedor");
        provider.setText(order.provider);
        EditText start = input("Fecha de Inicio yyyy-mm-dd");
        start.setText(order.startDate);
        EditText end = input("Fecha de Finalizacion yyyy-mm-dd");
        end.setText(order.endDate);

        form.addView(label("Tipo de Proveedor", 13, COLOR_MUTED));
        form.addView(type);
        form.addView(label("Estado", 13, COLOR_MUTED));
        form.addView(status);
        form.addView(provider);
        form.addView(start);
        form.addView(end);

        new AlertDialog.Builder(this)
                .setTitle("Detalle de Orden")
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    if (end.getText().toString().compareTo(start.getText().toString()) < 0) {
                        toast("La fecha de finalizacion no puede ser anterior a la fecha de inicio.");
                        return;
                    }
                    store.saveOrder(new Order(
                            order.code,
                            type.getSelectedItem().toString(),
                            status.getSelectedItem().toString(),
                            provider.getText().toString().trim(),
                            start.getText().toString().trim(),
                            end.getText().toString().trim()
                    ));
                    toast("Informacion de la orden actualizada correctamente.");
                    showOrders();
                })
                .show();
    }

    private void buildProvidersContent(LinearLayout content) {
        TextView intro = label("Seguimiento institucional de hojas de vida de proveedores de alimentos y logisticos.", 14, COLOR_MUTED);
        content.addView(intro);
        addSpace(content, 10);

        EditText search = input("Buscar por nombre, NIT o municipio");
        content.addView(search);

        TextView source = label(apiProviders == null ? "Fuente: datos locales de respaldo" : "Fuente: MySQL via Spring Boot", 13,
                apiProviders == null ? COLOR_WARNING : COLOR_SUCCESS);
        content.addView(source);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        content.addView(list);

        Runnable render = () -> renderProviders(list, search.getText().toString());
        search.addTextChangedListener(simpleWatcher(render));
        render.run();
    }

    private void loadApiProviders() {
        executor.execute(() -> {
            try {
                List<Provider> providers = apiClient.getProviderLifeSheets();
                runOnUiThread(() -> {
                    apiProviders = providers;
                    if ("providers".equals(activeScreen)) {
                        setContentView(shell("Hojas de Vida de Proveedores", this::buildProvidersContent));
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if ("providers".equals(activeScreen)) {
                        toast("No se pudieron cargar proveedores desde MySQL. Usando respaldo local.");
                    }
                });
            }
        });
    }

    private void renderProviders(LinearLayout list, String query) {
        list.removeAllViews();
        String text = query.trim().toLowerCase(colombia);
        int count = 0;

        List<Provider> source = apiProviders != null ? apiProviders : store.getProviders();
        for (Provider provider : source) {
            if (!text.isEmpty()
                    && !provider.name.toLowerCase(colombia).contains(text)
                    && !provider.nit.toLowerCase(colombia).contains(text)
                    && !provider.city.toLowerCase(colombia).contains(text)) {
                continue;
            }

            count++;
            LinearLayout card = panel();
            card.addView(title(provider.name, 17, COLOR_DARK));
            card.addView(label("NIT " + provider.nit, 14, COLOR_MUTED));
            card.addView(label(provider.city + ", " + provider.department, 14, COLOR_MUTED));
            if (!provider.certifications.isEmpty()) {
                card.addView(label("Total Certificado: " + money(provider.total()), 14, COLOR_SUCCESS));
                card.addView(label("Mes de Mayor Facturacion: " + provider.bestMonth(), 14, COLOR_DARK));
            }
            card.addView(label("Productos: " + join(provider.products), 13, COLOR_MUTED));
            if (!provider.certifications.isEmpty()) {
                card.addView(new BarChartView(this, provider));
            }

            Button edit = outlineButton(apiProviders == null ? "Ver / Editar Informacion" : "Ver Hoja de Vida", COLOR_PRIMARY);
            edit.setOnClickListener(v -> showProviderEditor(provider));
            card.addView(edit);
            list.addView(card);
        }

        if (count == 0) {
            list.addView(empty("No se encontraron proveedores con ese criterio."));
        }
    }

    private void showProviderEditor(Provider provider) {
        if (apiProviders != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Hoja de Vida")
                    .setMessage("Proveedor: " + provider.name
                            + "\nCodigo/NIT: " + provider.nit
                            + "\nTipo: " + provider.type
                            + "\nMunicipio: " + provider.city
                            + "\nDepartamento: " + provider.department
                            + "\nProductos: " + join(provider.products))
                    .setPositiveButton("Aceptar", null)
                    .show();
            return;
        }

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(10), dp(4), dp(10), 0);

        EditText name = input("Nombre del proveedor");
        name.setText(provider.name);
        EditText nit = input("NIT");
        nit.setText(provider.nit);
        EditText representative = input("Representante Legal");
        representative.setText(provider.representative);
        EditText email = input("Correo electronico");
        email.setText(provider.email);
        EditText phone = input("Telefono");
        phone.setText(provider.phone);
        EditText city = input("Municipio");
        city.setText(provider.city);
        EditText department = input("Departamento");
        department.setText(provider.department);
        EditText type = input("Tipo de proveedor");
        type.setText(provider.type);
        EditText products = input("Productos separados por coma");
        products.setMinLines(2);
        products.setText(join(provider.products));
        EditText certifications = input("Certificaciones mes:valor separadas por coma");
        certifications.setMinLines(2);
        certifications.setText(provider.certificationsText());

        form.addView(name);
        form.addView(nit);
        form.addView(representative);
        form.addView(email);
        form.addView(phone);
        form.addView(city);
        form.addView(department);
        form.addView(type);
        form.addView(products);
        form.addView(certifications);

        new AlertDialog.Builder(this)
                .setTitle("Hoja de Vida")
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    List<String> parsedProducts = split(products.getText().toString());
                    List<Certification> parsedCertifications = parseCertifications(certifications.getText().toString());
                    if (parsedProducts.isEmpty()) {
                        toast("Debes registrar al menos un producto ofertado.");
                        return;
                    }
                    if (parsedCertifications.isEmpty()) {
                        toast("Debes registrar al menos una certificacion.");
                        return;
                    }

                    store.saveProvider(new Provider(
                            provider.id,
                            name.getText().toString().trim(),
                            nit.getText().toString().trim(),
                            representative.getText().toString().trim(),
                            email.getText().toString().trim(),
                            phone.getText().toString().trim(),
                            city.getText().toString().trim(),
                            department.getText().toString().trim(),
                            type.getText().toString().trim(),
                            parsedProducts,
                            parsedCertifications
                    ));
                    toast("Hoja de vida actualizada correctamente.");
                    showProviders();
                })
                .show();
    }

    private void buildCplContent(LinearLayout content) {
        TextView intro = label("Control de Compras Publicas Locales para interventoria PAE Bogota. El porcentaje se calcula solo sobre valor total de alimentos.", 14, COLOR_MUTED);
        content.addView(intro);
        addSpace(content, 10);

        if (cplDashboard == null) {
            content.addView(empty("Cargando dashboard CPL desde Spring Boot..."));
            return;
        }

        int semaforoColor = "VERDE".equals(cplDashboard.semaforo) ? COLOR_SUCCESS : COLOR_DANGER;
        content.addView(metricCard("Porcentaje CPL mensual", cplDashboard.porcentajeCpl + "%", semaforoColor));
        content.addView(metricCard("Valor compras locales validadas", money(cplDashboard.valorComprasLocalesValidadas), COLOR_PRIMARY));
        content.addView(metricCard("Valor total alimentos", money(cplDashboard.valorTotalAlimentos), COLOR_DARK));

        LinearLayout panel = panel();
        panel.addView(title("Indicadores de interventoria", 19, COLOR_DARK));
        panel.addView(label("Semaforo: " + cplDashboard.semaforo, 15, semaforoColor));
        panel.addView(label("Compras aprobadas: " + cplDashboard.comprasAprobadas, 14, COLOR_SUCCESS));
        panel.addView(label("Compras observadas: " + cplDashboard.comprasObservadas, 14, COLOR_WARNING));
        panel.addView(label("Compras rechazadas: " + cplDashboard.comprasRechazadas, 14, COLOR_DANGER));
        panel.addView(label("Compras pendientes: " + cplDashboard.comprasPendientes, 14, COLOR_MUTED));
        panel.addView(label("Trazabilidad incompleta: " + cplDashboard.trazabilidadIncompleta, 14,
                cplDashboard.trazabilidadIncompleta > 0 ? COLOR_WARNING : COLOR_SUCCESS));
        content.addView(panel);

        LinearLayout rules = panel();
        rules.addView(title("Reglas aplicadas", 19, COLOR_DARK));
        rules.addView(label("Formula: compras locales validadas / valor total alimentos x 100", 14, COLOR_MUTED));
        rules.addView(label("No incluye transporte, administracion, personal ni servicios.", 14, COLOR_MUTED));
        rules.addView(label("No cuenta compras futuras ni compras sin soportes y trazabilidad.", 14, COLOR_MUTED));
        rules.addView(label("Industrializados: se valida la materia prima principal.", 14, COLOR_MUTED));
        content.addView(rules);

        Button refresh = primaryButton("Actualizar CPL");
        refresh.setOnClickListener(v -> loadCplDashboard());
        content.addView(refresh);
    }

    private void loadCplDashboard() {
        executor.execute(() -> {
            try {
                CplDashboard dashboard = apiClient.getCplDashboard(1, "2025-04");
                runOnUiThread(() -> {
                    cplDashboard = dashboard;
                    if ("cpl".equals(activeScreen)) {
                        setContentView(shell("Compras Publicas Locales", this::buildCplContent));
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if ("cpl".equals(activeScreen)) {
                        toast("No se pudo cargar CPL desde Spring Boot.");
                    }
                });
            }
        });
    }

    private TextWatcher simpleWatcher(Runnable afterChange) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                afterChange.run();
            }
        };
    }

    private void addNavButton(LinearLayout nav, String text, String screen, Runnable action) {
        Button button = outlineButton(text, activeScreen.equals(screen) ? COLOR_PRIMARY : COLOR_MUTED);
        button.setOnClickListener(v -> action.run());
        nav.addView(button, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    }

    private LinearLayout panel() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(14), dp(16), dp(14));
        layout.setBackground(cardBackground(Color.WHITE, 8, COLOR_BORDER));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(8), 0, dp(8));
        layout.setLayoutParams(params);
        return layout;
    }

    private TextView metricCard(String label, String value, int color) {
        TextView card = title(label + "\n" + value, 18, Color.WHITE);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(cardBackground(color, 8, color));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(5), 0, dp(5));
        card.setLayoutParams(params);
        return card;
    }

    private TextView sectionLabel(String text) {
        TextView view = title(text, 20, COLOR_DARK);
        view.setPadding(0, dp(18), 0, dp(4));
        return view;
    }

    private TextView empty(String message) {
        TextView view = label(message, 14, COLOR_MUTED);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(12), dp(18), dp(12), dp(18));
        view.setBackground(cardBackground(Color.WHITE, 8, Color.rgb(206, 212, 218)));
        return view;
    }

    private EditText input(String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        editText.setTextSize(15);
        editText.setSingleLine(false);
        editText.setMaxLines(3);
        editText.setPadding(dp(12), 0, dp(12), 0);
        editText.setBackground(cardBackground(Color.WHITE, 8, Color.rgb(206, 212, 218)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        params.setMargins(0, dp(7), 0, dp(7));
        editText.setLayoutParams(params);
        return editText;
    }

    private Spinner spinner(String[] values) {
        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, values));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        params.setMargins(0, dp(7), 0, dp(7));
        spinner.setLayoutParams(params);
        return spinner;
    }

    private Button primaryButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackground(cardBackground(COLOR_PRIMARY, 8, COLOR_PRIMARY));
        return withButtonParams(button);
    }

    private Button outlineButton(String text, int color) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(color);
        button.setAllCaps(false);
        button.setBackground(cardBackground(Color.TRANSPARENT, 8, color));
        return withButtonParams(button);
    }

    private Button withButtonParams(Button button) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(44)
        );
        params.setMargins(dp(4), dp(7), dp(4), dp(7));
        button.setLayoutParams(params);
        return button;
    }

    private TextView title(String text, int size, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setLineSpacing(0, 1.05f);
        return view;
    }

    private TextView label(String text, int size, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setLineSpacing(dp(2), 1.0f);
        return view;
    }

    private GradientDrawable cardBackground(int fill, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private void addSpace(LinearLayout layout, int height) {
        View space = new View(this);
        layout.addView(space, new LinearLayout.LayoutParams(1, dp(height)));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.role);
    }

    private void confirm(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Aceptar", (dialog, which) -> onConfirm.run())
                .show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private int statusIndex(String status) {
        if ("En Revision".equals(status)) return 1;
        if ("Finalizado".equals(status)) return 2;
        if ("Suspendido".equals(status)) return 3;
        return 0;
    }

    private int statusColor(String status) {
        if ("En Revision".equals(status)) return COLOR_WARNING;
        if ("Finalizado".equals(status)) return COLOR_PRIMARY;
        if ("Suspendido".equals(status)) return COLOR_DANGER;
        return COLOR_SUCCESS;
    }

    private String money(double value) {
        return NumberFormat.getCurrencyInstance(colombia).format(value);
    }

    private String join(List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) builder.append(", ");
            builder.append(values.get(i));
        }
        return builder.toString();
    }

    private List<String> split(String value) {
        List<String> result = new ArrayList<>();
        for (String item : value.split(",")) {
            String clean = item.trim();
            if (!clean.isEmpty()) {
                result.add(clean);
            }
        }
        return result;
    }

    private List<Certification> parseCertifications(String value) {
        List<Certification> result = new ArrayList<>();
        for (String item : value.split(",")) {
            String[] parts = item.trim().split(":");
            if (parts.length != 2) {
                continue;
            }
            try {
                result.add(new Certification(parts[0].trim(), Double.parseDouble(parts[1].trim())));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private interface ContentBuilder {
        void build(LinearLayout content);
    }

    private static class User {
        final int id;
        final String name;
        final String email;
        final String password;
        final String role;

        User(int id, String name, String email, String password, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }

    private static class Order {
        final String code;
        final String type;
        final String status;
        final String provider;
        final String startDate;
        final String endDate;

        Order(String code, String type, String status, String provider, String startDate, String endDate) {
            this.code = code;
            this.type = type;
            this.status = status;
            this.provider = provider;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private static class Certification {
        final String month;
        final double value;

        Certification(String month, double value) {
            this.month = month;
            this.value = value;
        }
    }

    private static class Provider {
        final int id;
        final String name;
        final String nit;
        final String representative;
        final String email;
        final String phone;
        final String city;
        final String department;
        final String type;
        final List<String> products;
        final List<Certification> certifications;

        Provider(int id, String name, String nit, String representative, String email, String phone,
                 String city, String department, String type, List<String> products,
                 List<Certification> certifications) {
            this.id = id;
            this.name = name;
            this.nit = nit;
            this.representative = representative;
            this.email = email;
            this.phone = phone;
            this.city = city;
            this.department = department;
            this.type = type;
            this.products = products;
            this.certifications = certifications;
        }

        double total() {
            double total = 0;
            for (Certification certification : certifications) {
                total += certification.value;
            }
            return total;
        }

        String bestMonth() {
            if (certifications.isEmpty()) {
                return "-";
            }
            Certification best = certifications.get(0);
            for (Certification certification : certifications) {
                if (certification.value > best.value) {
                    best = certification;
                }
            }
            return best.month;
        }

        String certificationsText() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < certifications.size(); i++) {
                if (i > 0) builder.append(", ");
                Certification c = certifications.get(i);
                builder.append(c.month).append(":").append((long) c.value);
            }
            return builder.toString();
        }
    }

    private static class CplDashboard {
        final double valorComprasLocalesValidadas;
        final double valorTotalAlimentos;
        final double porcentajeCpl;
        final String semaforo;
        final int comprasAprobadas;
        final int comprasObservadas;
        final int comprasRechazadas;
        final int comprasPendientes;
        final int trazabilidadIncompleta;

        CplDashboard(JSONObject object) {
            valorComprasLocalesValidadas = object.optDouble("valorComprasLocalesValidadas");
            valorTotalAlimentos = object.optDouble("valorTotalAlimentos");
            porcentajeCpl = object.optDouble("porcentajeCpl");
            semaforo = object.optString("semaforo", "ROJO");
            comprasAprobadas = object.optInt("comprasAprobadas");
            comprasObservadas = object.optInt("comprasObservadas");
            comprasRechazadas = object.optInt("comprasRechazadas");
            comprasPendientes = object.optInt("comprasPendientes");
            trazabilidadIncompleta = object.optInt("trazabilidadIncompleta");
        }
    }

    private class BarChartView extends View {
        private final Provider provider;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        BarChartView(Context context, Provider provider) {
            super(context);
            this.provider = provider;
            setMinimumHeight(dp(180));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(dp(190), MeasureSpec.EXACTLY));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            int padding = dp(24);
            double max = 1;
            for (Certification certification : provider.certifications) {
                max = Math.max(max, certification.value);
            }

            paint.setColor(Color.rgb(248, 250, 252));
            canvas.drawRoundRect(0, dp(10), width, height - dp(4), dp(8), dp(8), paint);

            int count = Math.max(1, provider.certifications.size());
            float availableWidth = width - padding * 2f;
            float barWidth = availableWidth / count * 0.52f;
            float gap = availableWidth / count;
            float base = height - dp(36);
            float chartHeight = height - dp(70);

            paint.setTextSize(dp(11));
            paint.setTypeface(Typeface.DEFAULT_BOLD);

            for (int i = 0; i < provider.certifications.size(); i++) {
                Certification certification = provider.certifications.get(i);
                float left = padding + i * gap + (gap - barWidth) / 2f;
                float top = base - (float) (certification.value / max * chartHeight);
                paint.setColor(COLOR_PRIMARY);
                canvas.drawRoundRect(left, top, left + barWidth, base, dp(5), dp(5), paint);
                paint.setColor(COLOR_MUTED);
                canvas.drawText(certification.month.substring(0, Math.min(3, certification.month.length())),
                        left - dp(2), height - dp(15), paint);
            }
        }
    }

    private static class ApiClient {
        private final String[] baseUrls;

        ApiClient(String baseUrl) {
            this.baseUrls = new String[]{
                    baseUrl
            };
        }

        User login(String email, String password) throws IOException, JSONException {
            JSONObject body = new JSONObject();
            body.put("correo", email);
            body.put("password", password);
            return userFromJson(request("POST", "/auth/login", body));
        }

        List<User> getUsers() throws IOException, JSONException {
            JSONArray array = requestArray("GET", "/usuarios", null);
            List<User> users = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                users.add(userFromJson(array.getJSONObject(i)));
            }
            return users;
        }

        void createUser(User user) throws IOException, JSONException {
            request("POST", "/usuarios", userToJson(user, false));
        }

        void updateUser(User user) throws IOException, JSONException {
            request("PUT", "/usuarios/" + user.id, userToJson(user, true));
        }

        void deleteUser(int id) throws IOException, JSONException {
            requestText("DELETE", "/usuarios/" + id, null);
        }

        List<Order> getPurchaseOrders() throws IOException, JSONException {
            List<Order> orders = new ArrayList<>();

            JSONArray alimentos = requestArray("GET", "/proveedores-alimentos", null);
            for (int i = 0; i < alimentos.length(); i++) {
                JSONObject item = alimentos.getJSONObject(i);
                String odc = item.optString("odcCce");
                String proveedor = item.optString("nombre");
                orders.add(new Order(
                        "OC_CCE_" + odc,
                        item.optString("tipoProveedor", "Proveedor de Alimentos"),
                        item.optString("estado", "Activo"),
                        proveedor,
                        "2025-01-20",
                        "2025-12-19"
                ));
            }

            JSONArray logisticos = requestArray("GET", "/proveedores-logisticos", null);
            for (int i = 0; i < logisticos.length(); i++) {
                JSONObject item = logisticos.getJSONObject(i);
                String contratoSed = item.optString("contratoSed");
                String contratoCce = item.optString("contratoCce");
                orders.add(new Order(
                        "OC_SED_" + contratoSed + "_CCE_" + contratoCce + "_2025",
                        item.optString("tipoProveedor", "Proveedor Logistico"),
                        item.optString("estado", "Activo"),
                        item.optString("nombre"),
                        "2025-01-20",
                        "2025-12-19"
                ));
            }

            return orders;
        }

        List<Provider> getProviderLifeSheets() throws IOException, JSONException {
            JSONArray array = requestArray("GET", "/hojas-vida-proveedores", null);
            List<Provider> providers = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                List<String> products = new ArrayList<>();
                products.add(item.optString("tipoProveedor", "Proveedor"));

                String codigo = item.optString("codigoProveedor");
                if (item.has("codigoCce")) {
                    codigo = codigo + " / CCE " + item.optString("codigoCce");
                }

                providers.add(new Provider(
                        i + 1,
                        item.optString("nombre"),
                        codigo,
                        "Pendiente ampliar hoja de vida",
                        "",
                        "",
                        item.optString("contratoSed", ""),
                        item.optString("estado", "Activo"),
                        item.optString("tipoProveedor", "Proveedor"),
                        products,
                        new ArrayList<>()
                ));
            }
            return providers;
        }

        CplDashboard getCplDashboard(int contratoId, String periodo) throws IOException, JSONException {
            JSONObject object = request("GET", "/cpl/dashboard?contratoId=" + contratoId + "&periodo=" + periodo, null);
            return new CplDashboard(object);
        }

        private JSONObject request(String method, String path, JSONObject body) throws IOException, JSONException {
            return new JSONObject(requestText(method, path, body));
        }

        private JSONArray requestArray(String method, String path, JSONObject body) throws IOException, JSONException {
            return new JSONArray(requestText(method, path, body));
        }

        private String requestText(String method, String path, JSONObject body) throws IOException {
            IOException lastError = null;
            for (String baseUrl : baseUrls) {
                try {
                    return requestTextFromBase(baseUrl, method, path, body);
                } catch (IOException error) {
                    lastError = error;
                }
            }
            throw lastError == null ? new IOException("No API base URL configured") : lastError;
        }

        private String requestTextFromBase(String baseUrl, String method, String path, JSONObject body) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + path).openConnection();
            connection.setRequestMethod(method);
            connection.setConnectTimeout(2500);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            if (body != null) {
                connection.setDoOutput(true);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body.toString().getBytes("UTF-8"));
                }
            }

            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String response = readStream(stream);
            connection.disconnect();

            if (code < 200 || code >= 300) {
                throw new IOException("HTTP " + code + ": " + response);
            }

            return response;
        }

        private static String readStream(InputStream stream) throws IOException {
            if (stream == null) {
                return "";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }

        private static JSONObject userToJson(User user, boolean includeId) throws JSONException {
            JSONObject object = new JSONObject();
            if (includeId) {
                object.put("id", user.id);
            }
            object.put("nombre", user.name);
            object.put("correo", user.email);
            object.put("password", user.password);
            object.put("rol", user.role);
            return object;
        }

        private static User userFromJson(JSONObject object) {
            return new User(
                    object.optInt("id"),
                    object.optString("nombre"),
                    object.optString("correo"),
                    object.optString("password"),
                    object.optString("rol", "USUARIO")
            );
        }
    }

    private static class LocalStore {
        private static final String PREFS = "sga_rpf_local_store";
        private static final String USERS = "users";
        private static final String LOGGED_USER = "logged_user";
        private static final String ORDERS = "orders";
        private static final String PROVIDERS = "providers";

        private final SharedPreferences prefs;

        LocalStore(Context context) {
            prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
            seedIfNeeded();
        }

        private void seedIfNeeded() {
            if (!prefs.contains(USERS)) {
                List<User> users = new ArrayList<>();
                users.add(new User(1, "Administrador SGA", "admin@sga.com", "1234", "ADMIN"));
                users.add(new User(2, "Usuario Consulta", "usuario@sga.com", "1234", "USUARIO"));
                saveUsers(users);
            }
            if (!prefs.contains(ORDERS)) {
                saveOrders(defaultOrders());
            }
            if (!prefs.contains(PROVIDERS)) {
                saveProviders(defaultProviders());
            }
        }

        User login(String email, String password) {
            for (User user : getUsers()) {
                if (user.email.equalsIgnoreCase(email) && user.password.equals(password)) {
                    return user;
                }
            }
            return null;
        }

        User getLoggedUser() {
            String data = prefs.getString(LOGGED_USER, null);
            if (data == null) return null;
            try {
                return userFromJson(new JSONObject(data));
            } catch (JSONException e) {
                return null;
            }
        }

        void saveLoggedUser(User user) {
            prefs.edit().putString(LOGGED_USER, userToJson(user).toString()).apply();
        }

        void clearLoggedUser() {
            prefs.edit().remove(LOGGED_USER).apply();
        }

        int nextUserId() {
            int next = 1;
            for (User user : getUsers()) {
                next = Math.max(next, user.id + 1);
            }
            return next;
        }

        boolean emailExists(String email, int ignoringId) {
            for (User user : getUsers()) {
                if (user.id != ignoringId && user.email.equalsIgnoreCase(email)) {
                    return true;
                }
            }
            return false;
        }

        List<User> getUsers() {
            List<User> users = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(prefs.getString(USERS, "[]"));
                for (int i = 0; i < array.length(); i++) {
                    users.add(userFromJson(array.getJSONObject(i)));
                }
            } catch (JSONException ignored) {
            }
            return users;
        }

        void saveUser(User updated) {
            List<User> users = getUsers();
            boolean replaced = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).id == updated.id) {
                    users.set(i, updated);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                users.add(updated);
            }
            saveUsers(users);
        }

        void deleteUser(int id) {
            List<User> users = getUsers();
            for (int i = users.size() - 1; i >= 0; i--) {
                if (users.get(i).id == id) {
                    users.remove(i);
                }
            }
            saveUsers(users);
        }

        void saveUsers(List<User> users) {
            JSONArray array = new JSONArray();
            for (User user : users) {
                array.put(userToJson(user));
            }
            prefs.edit().putString(USERS, array.toString()).apply();
        }

        List<Order> getOrders() {
            List<Order> orders = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(prefs.getString(ORDERS, "[]"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    orders.add(new Order(
                            object.getString("code"),
                            object.getString("type"),
                            object.getString("status"),
                            object.getString("provider"),
                            object.getString("startDate"),
                            object.getString("endDate")
                    ));
                }
            } catch (JSONException ignored) {
            }
            return orders;
        }

        void saveOrder(Order updated) {
            List<Order> orders = getOrders();
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).code.equals(updated.code)) {
                    orders.set(i, updated);
                    saveOrders(orders);
                    return;
                }
            }
            orders.add(updated);
            saveOrders(orders);
        }

        void restoreOrders() {
            saveOrders(defaultOrders());
        }

        void saveOrders(List<Order> orders) {
            JSONArray array = new JSONArray();
            for (Order order : orders) {
                JSONObject object = new JSONObject();
                try {
                    object.put("code", order.code);
                    object.put("type", order.type);
                    object.put("status", order.status);
                    object.put("provider", order.provider);
                    object.put("startDate", order.startDate);
                    object.put("endDate", order.endDate);
                    array.put(object);
                } catch (JSONException ignored) {
                }
            }
            prefs.edit().putString(ORDERS, array.toString()).apply();
        }

        List<Provider> getProviders() {
            List<Provider> providers = new ArrayList<>();
            try {
                JSONArray array = new JSONArray(prefs.getString(PROVIDERS, "[]"));
                for (int i = 0; i < array.length(); i++) {
                    providers.add(providerFromJson(array.getJSONObject(i)));
                }
            } catch (JSONException ignored) {
            }
            return providers;
        }

        void saveProvider(Provider updated) {
            List<Provider> providers = getProviders();
            for (int i = 0; i < providers.size(); i++) {
                if (providers.get(i).id == updated.id) {
                    providers.set(i, updated);
                    saveProviders(providers);
                    return;
                }
            }
            providers.add(updated);
            saveProviders(providers);
        }

        void restoreProviders() {
            saveProviders(defaultProviders());
        }

        void saveProviders(List<Provider> providers) {
            JSONArray array = new JSONArray();
            for (Provider provider : providers) {
                array.put(providerToJson(provider));
            }
            prefs.edit().putString(PROVIDERS, array.toString()).apply();
        }

        private static JSONObject userToJson(User user) {
            JSONObject object = new JSONObject();
            try {
                object.put("id", user.id);
                object.put("name", user.name);
                object.put("email", user.email);
                object.put("password", user.password);
                object.put("role", user.role);
            } catch (JSONException ignored) {
            }
            return object;
        }

        private static User userFromJson(JSONObject object) throws JSONException {
            return new User(
                    object.getInt("id"),
                    object.getString("name"),
                    object.getString("email"),
                    object.getString("password"),
                    object.optString("role", "USUARIO")
            );
        }

        private static JSONObject providerToJson(Provider provider) {
            JSONObject object = new JSONObject();
            JSONArray products = new JSONArray();
            JSONArray certifications = new JSONArray();
            try {
                object.put("id", provider.id);
                object.put("name", provider.name);
                object.put("nit", provider.nit);
                object.put("representative", provider.representative);
                object.put("email", provider.email);
                object.put("phone", provider.phone);
                object.put("city", provider.city);
                object.put("department", provider.department);
                object.put("type", provider.type);
                for (String product : provider.products) {
                    products.put(product);
                }
                object.put("products", products);
                for (Certification certification : provider.certifications) {
                    JSONObject item = new JSONObject();
                    item.put("month", certification.month);
                    item.put("value", certification.value);
                    certifications.put(item);
                }
                object.put("certifications", certifications);
            } catch (JSONException ignored) {
            }
            return object;
        }

        private static Provider providerFromJson(JSONObject object) throws JSONException {
            List<String> products = new ArrayList<>();
            JSONArray productsArray = object.getJSONArray("products");
            for (int i = 0; i < productsArray.length(); i++) {
                products.add(productsArray.getString(i));
            }

            List<Certification> certifications = new ArrayList<>();
            JSONArray certificationsArray = object.getJSONArray("certifications");
            for (int i = 0; i < certificationsArray.length(); i++) {
                JSONObject item = certificationsArray.getJSONObject(i);
                certifications.add(new Certification(item.getString("month"), item.getDouble("value")));
            }

            return new Provider(
                    object.getInt("id"),
                    object.getString("name"),
                    object.getString("nit"),
                    object.getString("representative"),
                    object.getString("email"),
                    object.getString("phone"),
                    object.getString("city"),
                    object.getString("department"),
                    object.getString("type"),
                    products,
                    certifications
            );
        }

        private static List<Order> defaultOrders() {
            List<Order> orders = new ArrayList<>();
            String[] foods = {
                    "OC_SED_4207_CCE_153106_2025", "OC_SED_4208_CCE_153111_2025",
                    "OC_SED_4209_CCE_153112_2025", "OC_SED_4210_CCE_153109_2025",
                    "OC_SED_4211_CCE_153113_2025", "OC_SED_4212_CCE_153114_2025",
                    "OC_SED_4213_CCE_153115_2025", "OC_SED_4214_CCE_153116_2025",
                    "OC_SED_4215_CCE_153117_2025", "OC_SED_4216_CCE_153107_2025",
                    "OC_SED_4217_CCE_153118_2025", "OC_SED_4218_CCE_153119_2025",
                    "OC_SED_4219_CCE_153120_2025", "OC_SED_4220_CCE_153121_2025",
                    "OC_SED_4221_CCE_153122_2025", "OC_SED_4222_CCE_153124_2025",
                    "OC_SED_4223_CCE_153101_2025", "OC_SED_4225_CCE_153100_2025",
                    "OC_SED_4226_CCE_153123_2025", "OC_SED_4227_CCE_153102_2025",
                    "OC_SED_4228_CCE_153103_2025", "OC_SED_4229_CCE_153104_2025",
                    "OC_SED_4230_CCE_153099_2025", "OC_SED_4231_CCE_153098_2025",
                    "OC_SED_4238_CCE_153357_2025"
            };
            String[] logistics = {
                    "OC_SED_4312_CCE_153637_2025", "OC_SED_4313_CCE_153638_2025",
                    "OC_SED_4314_CCE_153639_2025", "OC_SED_4315_CCE_153640_2025",
                    "OC_SED_4316_CCE_153641_2025", "OC_SED_4317_CCE_153642_2025",
                    "OC_SED_4318_CCE_153643_2025", "OC_SED_4319_CCE_153644_2025",
                    "OC_SED_4320_CCE_153645_2025", "OC_SED_4321_CCE_153646_2025"
            };
            for (int i = 0; i < foods.length; i++) {
                orders.add(new Order(foods[i], "Proveedor de Alimentos", "Activo",
                        "Proveedor de Alimentos " + (i + 1), "2025-01-20", "2025-12-19"));
            }
            for (int i = 0; i < logistics.length; i++) {
                orders.add(new Order(logistics[i], "Proveedor Logistico", "Activo",
                        "Proveedor Logistico " + (i + 1), "2025-01-20", "2025-12-19"));
            }
            return orders;
        }

        private static List<Provider> defaultProviders() {
            List<Provider> providers = new ArrayList<>();
            providers.add(new Provider(1, "Agroalimentos del Norte S.A.S.", "900123456-1",
                    "Claudia Marcela Rojas", "contacto@agronorte.com", "315 456 7890",
                    "Tunja", "Boyaca", "Proveedor de Alimentos",
                    list("Papa Criolla", "Papa Pastusa", "Zanahoria", "Cebolla", "Arveja"),
                    certs(12500000, 14200000, 11900000, 15100000)));
            providers.add(new Provider(2, "Distribuidora Campo Verde", "901778234-5",
                    "Andres Felipe Molina", "operaciones@campoverde.com", "310 882 4471",
                    "Duitama", "Boyaca", "Proveedor de Alimentos",
                    list("Tomate", "Lechuga", "Frijol", "Cebolla", "Zanahoria"),
                    certs(9800000, 11300000, 12150000, 10800000)));
            providers.add(new Provider(3, "Lacteos y Proteinas Andinas", "830456901-8",
                    "Sandra Milena Torres", "administracion@proteinasandinas.com", "312 904 1188",
                    "Sogamoso", "Boyaca", "Proveedor de Alimentos",
                    list("Huevos", "Lacteos", "Queso", "Yogurt", "Leche"),
                    certs(16200000, 15850000, 17100000, 16500000)));
            providers.add(new Provider(4, "Mercados Escolares Integrales", "901112778-2",
                    "Jorge Enrique Vargas", "info@mercadosintegrales.com", "320 778 9912",
                    "Chiquinquira", "Boyaca", "Proveedor de Alimentos",
                    list("Frijol", "Arveja", "Lenteja", "Arroz", "Aceite"),
                    certs(13500000, 14700000, 13900000, 15500000)));
            providers.add(new Provider(5, "Hortalizas La Provincia", "900884321-6",
                    "Paula Andrea Cardenas", "proveedores@laprovincia.com", "317 334 1290",
                    "Paipa", "Boyaca", "Proveedor de Alimentos",
                    list("Papa Criolla", "Tomate", "Lechuga", "Zanahoria", "Cebolla"),
                    certs(11100000, 12800000, 12400000, 13250000)));
            return providers;
        }

        private static List<String> list(String... values) {
            List<String> result = new ArrayList<>();
            for (String value : values) {
                result.add(value);
            }
            return result;
        }

        private static List<Certification> certs(double january, double february, double march, double april) {
            List<Certification> result = new ArrayList<>();
            result.add(new Certification("Enero", january));
            result.add(new Certification("Febrero", february));
            result.add(new Certification("Marzo", march));
            result.add(new Certification("Abril", april));
            return result;
        }
    }
}
