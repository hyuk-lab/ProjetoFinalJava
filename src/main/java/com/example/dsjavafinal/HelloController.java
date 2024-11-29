package com.example.dsjavafinal;

import com.example.dsjavafinal.Model.Reserva;
import com.example.dsjavafinal.Model.Dao.ReservaDao;
import com.example.dsjavafinal.Model.Database.Database;
import com.example.dsjavafinal.Model.Database.DatabaseFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;

public class HelloController {
    private final Database database = DatabaseFactory.getDatabase("mysql");
    private final Connection connection = database.conectar();
    private final ReservaDao reservaDao = new ReservaDao();

    @FXML
    private TextField txtNumeroSala;
    @FXML
    private TextField txtCurso;
    @FXML
    private TextField txtDisciplina;
    @FXML
    private TextField txtProfessor;
    @FXML
    private TextField txtData;
    @FXML
    private TextField txtHrEntrada;
    @FXML
    private TextField txtHrSaida;
    @FXML
    private ToggleGroup grpTurno;
    @FXML
    private RadioButton rbManha;
    @FXML
    private RadioButton rbTarde;
    @FXML
    private RadioButton rbNoite;
    @FXML
    private CheckBox chkInformatica;
    @FXML
    private TextField txtCodigo;
    @FXML
    private Button btnCadastrar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnDeletar;

    private Reserva reserva;
    private ObservableList<Reserva> listReservas;

    @FXML
    private TableView<Reserva> tbvReservas;
    @FXML
    private TableColumn<Reserva, Integer> tbcId;
    @FXML
    private TableColumn<Reserva, String> tbcNumeroSala;
    @FXML
    private TableColumn<Reserva, String> tbcCurso;
    @FXML
    private TableColumn<Reserva, String> tbcDisciplina;
    @FXML
    private TableColumn<Reserva, String> tbcProfessor;
    @FXML
    private TableColumn<Reserva, String> tbcData;
    @FXML
    private TableColumn<Reserva, String> tbcHrEntrada;
    @FXML
    private TableColumn<Reserva, String> tbcHrSaida;
    @FXML
    private TableColumn<Reserva, String> tbcTurno;
    @FXML
    private TableColumn<Reserva, Boolean> tbcInformatica;

    @FXML
    public void initialize() {
        listReservas = FXCollections.observableArrayList();
        tbvReservas.setItems(listReservas);

        tbcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tbcNumeroSala.setCellValueFactory(new PropertyValueFactory<>("numeroSala"));
        tbcCurso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        tbcDisciplina.setCellValueFactory(new PropertyValueFactory<>("disciplina"));
        tbcProfessor.setCellValueFactory(new PropertyValueFactory<>("professor"));
        tbcData.setCellValueFactory(new PropertyValueFactory<>("data"));
        tbcHrEntrada.setCellValueFactory(new PropertyValueFactory<>("hrEntrada"));
        tbcHrSaida.setCellValueFactory(new PropertyValueFactory<>("hrSaida"));
        tbcTurno.setCellValueFactory(new PropertyValueFactory<>("turno"));
        tbcInformatica.setCellValueFactory(new PropertyValueFactory<>("informatica"));

        carregarDados();
    }

    @FXML
    protected void onClickCadastrar() {
        if (txtNumeroSala.getText().trim().isEmpty()) {
            mostrarAlerta("Erro no Cadastro", "Número da Sala Inválido", "Por favor, insira o número da sala.");
            txtNumeroSala.requestFocus();
            return;
        }
        if (txtCurso.getText().trim().isEmpty()) {
            mostrarAlerta("Erro no Cadastro", "Curso Inválido", "Por favor, insira o nome do curso.");
            txtCurso.requestFocus();
            return;
        }
        if (txtDisciplina.getText().trim().isEmpty()) {
            mostrarAlerta("Erro no Cadastro", "Disciplina Inválida", "Por favor, insira o nome da disciplina.");
            txtDisciplina.requestFocus();
            return;
        }
        if (txtProfessor.getText().trim().isEmpty()) {
            mostrarAlerta("Erro no Cadastro", "Professor Inválido", "Por favor, insira o nome do professor.");
            txtProfessor.requestFocus();
            return;
        }
        if (txtData.getText().trim().isEmpty() || !txtData.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
            mostrarAlerta("Erro no Cadastro", "Data Inválida", "Por favor, insira uma data válida no formato YYYY-MM-DD.");
            txtData.requestFocus();
            return;
        }

        reserva = new Reserva(
                txtNumeroSala.getText(),
                txtCurso.getText(),
                txtDisciplina.getText(),
                txtProfessor.getText(),
                txtData.getText(),
                txtHrEntrada.getText(),
                txtHrSaida.getText(),
                chkInformatica.isSelected(),
                ((RadioButton) grpTurno.getSelectedToggle()).getText()
        );

        reservaDao.setConnection(connection);
        if (!reservaDao.inserir(reserva)) {
            mostrarAlerta("Sucesso", "Cadastro Realizado", "Reserva cadastrada com sucesso!");
            limparCampos();
            carregarDados();
        } else {
            mostrarAlerta("Erro no Cadastro", "Falha ao Salvar", "Ocorreu um problema ao tentar salvar a reserva.");
        }
    }

    @FXML
    protected void onClickBuscar() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta("Erro na Busca", "Código Inválido", "Por favor, insira o código da reserva.");
            txtCodigo.requestFocus();
            return;
        }

        reservaDao.setConnection(connection);
        reserva = reservaDao.getReservaById(Integer.parseInt(txtCodigo.getText()));

        if (reserva != null) {
            preencherCampos(reserva);
        } else {
            mostrarAlerta("Erro na Busca", "Reserva Não Encontrada", "Nenhuma reserva foi encontrada com o código fornecido.");
        }
    }

    @FXML
    protected void onClickDeletar() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarAlerta("Erro na Exclusão", "Código Inválido", "Por favor, insira o código da reserva.");
            txtCodigo.requestFocus();
            return;
        }

        reservaDao.setConnection(connection);
        if (reservaDao.delete(Integer.parseInt(txtCodigo.getText()))) {
            mostrarAlerta("Sucesso", "Exclusão Realizada", "Reserva excluída com sucesso!");
            limparCampos();
            carregarDados();
        } else {
            mostrarAlerta("Erro na Exclusão", "Falha ao Excluir", "Ocorreu um problema ao tentar excluir a reserva.");
        }
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }

    private void preencherCampos(Reserva reserva) {
        txtNumeroSala.setText(reserva.getNumeroSala());
        txtCurso.setText(reserva.getCurso());
        txtDisciplina.setText(reserva.getDisciplina());
        txtProfessor.setText(reserva.getProfessor());
        txtData.setText(reserva.getData());
        txtHrEntrada.setText(reserva.getHrEntrada());
        txtHrSaida.setText(reserva.getHrSaida());
        chkInformatica.setSelected(reserva.getInformatica());

        switch (reserva.getTurno().toLowerCase()) {
            case "manhã":
                rbManha.setSelected(true);
                break;
            case "tarde":
                rbTarde.setSelected(true);
                break;
            case "noite":
                rbNoite.setSelected(true);
                break;
        }
    }

    private void limparCampos() {
        txtNumeroSala.clear();
        txtCurso.clear();
        txtDisciplina.clear();
        txtProfessor.clear();
        txtData.clear();
        txtHrEntrada.clear();
        txtHrSaida.clear();
        chkInformatica.setSelected(false);
        grpTurno.selectToggle(null);
        txtCodigo.clear();
    }

    private void carregarDados() {
        reservaDao.setConnection(connection);
        listReservas.setAll(reservaDao.getReservas());
    }
}
